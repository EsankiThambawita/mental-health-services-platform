// Patient Recovery Plans JavaScript

checkAuth(); // Ensure user is logged in

const RECOVERY_API = 'http://localhost:8083/api/recovery-plans';
const token = getAuthHeader();

// Display user info
document.getElementById('userName').textContent = localStorage.getItem('name');
document.getElementById('userEmail').textContent = localStorage.getItem('email');

const plansContainer = document.getElementById('plansContainer');
const loadingPlans = document.getElementById('loadingPlans');
const emptyPlans = document.getElementById('emptyPlans');

// Load patient's recovery plans
async function loadPlans() {
  loadingPlans.classList.remove('hidden');
  emptyPlans.classList.add('hidden');
  plansContainer.innerHTML = '';
  
  try {
    const response = await fetch(`${RECOVERY_API}/patient/plans`, {
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
          Counselor ID: ${plan.counselorId} • 
          Progress: ${completedTasks}/${taskCount} tasks completed •
          Created: ${formatDate(plan.createdAt)}
        </div>
      </div>
      <span class="badge ${statusBadge}">${plan.status}</span>
    </div>
    
    <p style="margin-bottom: 16px;">${plan.description || 'No description'}</p>
    
    <div>
      <h3 style="margin-bottom: 12px;">My Tasks</h3>
      <div id="tasks${plan.id}"></div>
    </div>
  `;
  
  plansContainer.appendChild(card);
  
  // Render tasks
  const tasksDiv = document.getElementById(`tasks${plan.id}`);
  
  if (plan.tasks && plan.tasks.length > 0) {
    plan.tasks.forEach(task => {
      const taskEl = document.createElement('div');
      taskEl.className = `task ${task.completed ? 'completed' : ''}`;
      
      const isOverdue = !task.completed && new Date(task.dueDate) < new Date();
      
      taskEl.innerHTML = `
        ${!task.completed ? `
          <input type="checkbox" 
                 class="task-checkbox" 
                 onchange="completeTask('${plan.id}', '${task.taskId}')"
                 ${plan.status === 'CANCELLED' ? 'disabled' : ''}>
        ` : ''}
        <div class="task-content">
          <div class="task-description">${task.description}</div>
          <div class="task-meta" style="color: ${isOverdue ? 'var(--danger)' : 'inherit'}">
            ${task.completed ? 
              `✓ Completed: ${formatDate(task.completedAt)}` : 
              `Due: ${formatDate(task.dueDate)}${isOverdue ? ' (Overdue)' : ''}`}
            ${task.counselorNotes ? `<br><em>Notes: ${task.counselorNotes}</em>` : ''}
          </div>
        </div>
      `;
      tasksDiv.appendChild(taskEl);
    });
  } else {
    tasksDiv.innerHTML = '<p class="empty-state">No tasks assigned yet.</p>';
  }
}

// Complete a task
async function completeTask(planId, taskId) {
  try {
    const response = await fetch(`${RECOVERY_API}/${planId}/patient/tasks/${taskId}/complete`, {
      method: 'PATCH',
      headers: { 'Authorization': token }
    });

    if (!response.ok) {
      throw new Error('Failed to complete task');
    }

    showAlert('Task marked as complete! Great job! 🎉', 'success');
    loadPlans(); // Reload to show updated status
    
  } catch (err) {
    showAlert(err.message, 'error');
  }
}

// Format date helper
function formatDate(dateStr) {
  if (!dateStr) return 'N/A';
  return new Date(dateStr).toLocaleDateString();
}

// Initial load
loadPlans();