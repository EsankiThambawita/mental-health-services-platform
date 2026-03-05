// Counselor Recovery Plans JavaScript

checkAuth(); 

const RECOVERY_API = 'http://localhost:8083/api/recovery-plans';
const token = getAuthHeader();

// Show user info
document.getElementById('userName').textContent = localStorage.getItem('name');
document.getElementById('userEmail').textContent = localStorage.getItem('email');

const plansContainer = document.getElementById('plansContainer');
const loadingPlans = document.getElementById('loadingPlans');
const emptyPlans = document.getElementById('emptyPlans');
const createPlanForm = document.getElementById('createPlanForm');

// Create Recovery Plan
createPlanForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  
  const email = document.getElementById('patientEmail').value;
  const title = document.getElementById('planTitle').value;
  const desc = document.getElementById('planDesc').value;

  console.log('Creating plan for:', email); 

  try {
    // Step 1: Get patientId from email
    console.log('Fetching patient ID...');
    const userRes = await fetch(`http://localhost:8084/api/auth/userIdByEmail?email=${encodeURIComponent(email)}`, {
      headers: { 'Authorization': token }
    });

    console.log('User lookup response status:', userRes.status);

    if (!userRes.ok) {
      const errorText = await userRes.text();
      console.error('User lookup failed:', errorText);
      throw new Error('Patient not found with that email');
    }

    const userData = await userRes.json();
    console.log('User data:', userData);
    
    // Verify it's a patient
    if (userData.role !== 'PATIENT') {
      throw new Error('The email provided is not a patient account');
    }

    const patientId = userData.userId;
    console.log('Patient ID:', patientId);

    // Step 2: Create the recovery plan
    const now = new Date().toISOString();
    const threeMonths = new Date();
    threeMonths.setMonth(threeMonths.getMonth() + 3);

    const planData = {
      patientId,
      counselorId: localStorage.getItem('userId'), // Will be overridden by backend from token
      title,
      description: desc,
      startDate: now,
      endDate: threeMonths.toISOString()
    };

    console.log('Creating plan with data:', planData);

    const response = await fetch(RECOVERY_API, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'Authorization': token
      },
      body: JSON.stringify(planData)
    });

    console.log('Create plan response status:', response.status);

    if (!response.ok) {
      const errText = await response.text();
      console.error('Create plan failed:', errText);
      let errData;
      try {
        errData = JSON.parse(errText);
      } catch {
        errData = { message: errText };
      }
      throw new Error(errData.message || 'Failed to create plan');
    }

    const result = await response.json();
    console.log('Plan created successfully:', result);

    showAlert('Recovery plan created successfully!', 'success');
    createPlanForm.reset();
    loadPlans();
    
  } catch (err) {
    console.error('Error creating plan:', err);
    showAlert(err.message, 'error');
  }
});

// Load counselor's recovery plans
async function loadPlans() {
  loadingPlans.classList.remove('hidden');
  emptyPlans.classList.add('hidden');
  plansContainer.innerHTML = '';
  
  try {
    const response = await fetch(`${RECOVERY_API}/counselor/plans`, {
      headers: { 'Authorization': token }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch plans');
    }

    const plans = await response.json();
    loadingPlans.classList.add('hidden');
    
    if (plans.length === 0) {
      emptyPlans.classList.remove('hidden');
      return;
    }

    plans.forEach(plan => renderPlan(plan));
    
  } catch (err) {
    loadingPlans.classList.add('hidden');
    showAlert(err.message, 'error');
  }
}

// Render a single plan
function renderPlan(plan) {
  const card = document.createElement('div');
  card.className = 'plan-card';
  
  const taskCount = plan.tasks ? plan.tasks.length : 0;
  const completedTasks = plan.tasks ? plan.tasks.filter(t => t.completed).length : 0;
  
  const statusBadge = plan.status === 'ACTIVE' ? 'badge-active' : 
                      plan.status === 'COMPLETED' ? 'badge-completed' : 'badge-cancelled';
  
  card.innerHTML = `
    <div class="plan-header">
      <div>
        <div class="plan-title">${plan.title}</div>
        <div class="plan-meta">
          Patient: ${plan.patientEmail || plan.patientId} • 
          Tasks: ${completedTasks}/${taskCount} completed •
          Created: ${formatDate(plan.createdAt)}
        </div>
      </div>
      <span class="badge ${statusBadge}">${plan.status}</span>
    </div>
    
    <p style="margin-bottom: 16px;">${plan.description || 'No description'}</p>
    
    <div style="display: flex; gap: 8px; flex-wrap: wrap;">
      <button onclick="viewPlanDetails('${plan.id}')" class="btn btn-primary btn-small">View Details</button>
      <button onclick="addTask('${plan.id}')" class="btn btn-success btn-small">Add Task</button>
      <button onclick="updateStatus('${plan.id}', '${plan.status}')" class="btn btn-secondary btn-small">Update Status</button>
      <button onclick="deletePlan('${plan.id}')" class="btn btn-danger btn-small">Delete</button>
    </div>
    
    <div id="planDetails${plan.id}" class="hidden" style="margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--neutral-200);">
      <h3>Tasks</h3>
      <div id="tasks${plan.id}"></div>
    </div>
  `;
  
  plansContainer.appendChild(card);
}

// View plan details
async function viewPlanDetails(planId) {
  const detailsDiv = document.getElementById(`planDetails${planId}`);
  
  if (!detailsDiv.classList.contains('hidden')) {
    detailsDiv.classList.add('hidden');
    return;
  }
  
  try {
    const response = await fetch(`${RECOVERY_API}/${planId}/counselor`, {
      headers: { 'Authorization': token }
    });

    if (!response.ok) throw new Error('Failed to fetch plan details');

    const plan = await response.json();
    const tasksDiv = document.getElementById(`tasks${planId}`);
    
    if (plan.tasks && plan.tasks.length > 0) {
      tasksDiv.innerHTML = '';
      plan.tasks.forEach(task => {
        const taskEl = document.createElement('div');
        taskEl.className = `task ${task.completed ? 'completed' : ''}`;
        taskEl.innerHTML = `
          <div class="task-content">
            <div class="task-description">${task.description}</div>
            <div class="task-meta">
              ${task.completed ? 
                `Completed: ${formatDate(task.completedAt)}` : 
                `Due: ${formatDate(task.dueDate)}`}
              ${task.counselorNotes ? `<br>Notes: ${task.counselorNotes}` : ''}
            </div>
          </div>
          <div style="display: flex; gap: 4px;">
            <button onclick="editTask('${planId}', '${task.taskId}')" class="btn btn-secondary btn-small">Edit</button>
            <button onclick="deleteTask('${planId}', '${task.taskId}')" class="btn btn-danger btn-small">Delete</button>
          </div>
        `;
        tasksDiv.appendChild(taskEl);
      });
    } else {
      tasksDiv.innerHTML = '<p class="empty-state">No tasks yet. Add tasks to this plan.</p>';
    }
    
    detailsDiv.classList.remove('hidden');
    
  } catch (err) {
    showAlert(err.message, 'error');
  }
}

// Add task - Used prompt dialog boxes for simplicity
function addTask(planId) {
  const description = prompt('Task description:');
  if (!description) return;
  
  const dueDate = prompt('Due date (YYYY-MM-DD):');
  if (!dueDate) return;
  
  const notes = prompt('Counselor notes (optional):');
  
  performAddTask(planId, description, dueDate, notes);
}

async function performAddTask(planId, description, dueDate, notes) {
  try {
    const response = await fetch(`${RECOVERY_API}/${planId}/counselor/tasks`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token
      },
      body: JSON.stringify({
        description,
        dueDate: dueDate + 'T00:00:00',
        counselorNotes: notes || null
      })
    });

    if (!response.ok) throw new Error('Failed to add task');

    showAlert('Task added successfully!', 'success');
    loadPlans();
    
  } catch (err) {
    showAlert(err.message, 'error');
  }
}

// Edit task
function editTask(planId, taskId) {
  const description = prompt('New task description:');
  if (!description) return;
  
  const dueDate = prompt('New due date (YYYY-MM-DD):');
  if (!dueDate) return;
  
  const notes = prompt('Counselor notes (optional):');
  
  performEditTask(planId, taskId, description, dueDate, notes);
}

async function performEditTask(planId, taskId, description, dueDate, notes) {
  try {
    const response = await fetch(`${RECOVERY_API}/${planId}/counselor/tasks/${taskId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token
      },
      body: JSON.stringify({
        description,
        dueDate: dueDate + 'T00:00:00',
        counselorNotes: notes || null
      })
    });

    if (!response.ok) throw new Error('Failed to update task');

    showAlert('Task updated successfully!', 'success');
    loadPlans();
    
  } catch (err) {
    showAlert(err.message, 'error');
  }
}

// Delete task
async function deleteTask(planId, taskId) {
  if (!confirm('Delete this task?')) return;
  
  try {
    const response = await fetch(`${RECOVERY_API}/${planId}/counselor/tasks/${taskId}`, {
      method: 'DELETE',
      headers: { 'Authorization': token }
    });

    if (!response.ok) throw new Error('Failed to delete task');

    showAlert('Task deleted!', 'success');
    loadPlans();
    
  } catch (err) {
    showAlert(err.message, 'error');
  }
}

// Update plan status
function updateStatus(planId, currentStatus) {
  const newStatus = prompt(`Current status: ${currentStatus}. Enter new status (ACTIVE, COMPLETED, CANCELLED):`);
  if (!newStatus || !['ACTIVE', 'COMPLETED', 'CANCELLED'].includes(newStatus.toUpperCase())) {
    showAlert('Invalid status', 'error');
    return;
  }
  
  performUpdateStatus(planId, newStatus.toUpperCase());
}

async function performUpdateStatus(planId, status) {
  try {
    const response = await fetch(`${RECOVERY_API}/${planId}/counselor/status`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token
      },
      body: JSON.stringify({ status })
    });

    if (!response.ok) throw new Error('Failed to update status');

    showAlert('Status updated!', 'success');
    loadPlans();
    
  } catch (err) {
    showAlert(err.message, 'error');
  }
}

// Delete plan
async function deletePlan(planId) {
  if (!confirm('Delete this entire recovery plan? This cannot be undone!')) return;
  
  try {
    const response = await fetch(`${RECOVERY_API}/${planId}/counselor`, {
      method: 'DELETE',
      headers: { 'Authorization': token }
    });

    if (!response.ok) throw new Error('Failed to delete plan');

    showAlert('Plan deleted!', 'success');
    loadPlans();
    
  } catch (err) {
    showAlert(err.message, 'error');
  }
}

function formatDate(dateStr) {
  if (!dateStr) return 'N/A';
  return new Date(dateStr).toLocaleDateString();
}

loadPlans();