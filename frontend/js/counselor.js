// Counselor Recovery Plans - JavaScript

// ========== STATE ==========
let counselorId = '';
let currentPlanId = '';
let currentTaskId = '';
let plans = [];

// ========== INITIALIZATION ==========
document.addEventListener('DOMContentLoaded', () => {
  // Get counselor ID from URL parameter or localStorage
  const urlParams = new URLSearchParams(window.location.search);
  counselorId = urlParams.get('counselorId') || localStorage.getItem('counselorId') || 'counselor456';
  
  // Store for future use
  localStorage.setItem('counselorId', counselorId);
  document.getElementById('counselorId').value = counselorId;
  
  // Set default start date to now
  const now = new Date();
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
  document.getElementById('startDate').value = now.toISOString().slice(0, 16);
  
  // Set default end date to 3 months from now
  const threeMonthsLater = new Date();
  threeMonthsLater.setMonth(threeMonthsLater.getMonth() + 3);
  threeMonthsLater.setMinutes(threeMonthsLater.getMinutes() - threeMonthsLater.getTimezoneOffset());
  document.getElementById('endDate').value = threeMonthsLater.toISOString().slice(0, 16);
  
  // Initialize
  setupEventListeners();
  loadPlans();
});

// ========== EVENT LISTENERS ==========
function setupEventListeners() {
  // Toggle create form
  document.getElementById('toggleFormBtn').addEventListener('click', toggleCreateForm);
  
  // Create plan form
  document.getElementById('createPlanForm').addEventListener('submit', handleCreatePlan);
  
  // Refresh button
  document.getElementById('refreshBtn').addEventListener('click', loadPlans);
  
  // Task modal
  document.getElementById('closeModal').addEventListener('click', closeTaskModal);
  document.getElementById('taskModal').addEventListener('click', (e) => {
    if (e.target.id === 'taskModal') closeTaskModal();
  });
  
  // Add task form
  document.getElementById('addTaskForm').addEventListener('submit', handleAddTask);
  
  // Update status button
  document.getElementById('updateStatusBtn').addEventListener('click', handleUpdateStatus);
  
  // Delete plan button
  document.getElementById('deletePlanBtn').addEventListener('click', handleDeletePlan);
  
  // Edit task modal
  document.getElementById('closeEditModal').addEventListener('click', closeEditTaskModal);
  document.getElementById('editTaskModal').addEventListener('click', (e) => {
    if (e.target.id === 'editTaskModal') closeEditTaskModal();
  });
  
  // Edit task form
  document.getElementById('editTaskForm').addEventListener('submit', handleEditTask);
}

// ========== TOGGLE CREATE FORM ==========
function toggleCreateForm() {
  const form = document.getElementById('createPlanForm');
  const btn = document.getElementById('toggleFormBtn');
  
  if (form.classList.contains('hidden')) {
    form.classList.remove('hidden');
    btn.textContent = 'Hide Form';
  } else {
    form.classList.add('hidden');
    btn.textContent = 'Show Form';
  }
}

// ========== LOAD PLANS ==========
async function loadPlans() {
  const loadingEl = document.getElementById('loadingPlans');
  const emptyStateEl = document.getElementById('emptyState');
  const plansListEl = document.getElementById('plansList');
  
  loadingEl.classList.remove('hidden');
  emptyStateEl.classList.add('hidden');
  plansListEl.innerHTML = '';
  
  try {
    plans = await API_CONFIG.get(`/counselor/${counselorId}`);
    
    loadingEl.classList.add('hidden');
    
    if (plans.length === 0) {
      emptyStateEl.classList.remove('hidden');
    } else {
      renderPlans(plans);
    }
  } catch (error) {
    loadingEl.classList.add('hidden');
    showError(error);
  }
}

// ========== RENDER PLANS ==========
function renderPlans(plansList) {
  const container = document.getElementById('plansList');
  container.innerHTML = '';
  
  plansList.forEach(plan => {
    const card = document.createElement('div');
    card.className = 'card';
    
    const statusClass = plan.status === 'ACTIVE' ? 'badge-active' : 
                       plan.status === 'COMPLETED' ? 'badge-completed' : 'badge-cancelled';
    
    const taskCount = plan.tasks ? plan.tasks.length : 0;
    const completedTasks = plan.tasks ? plan.tasks.filter(t => t.completed).length : 0;
    
    card.innerHTML = `
      <div class="card-header">
        <div>
          <div class="card-title">${plan.title}</div>
          <div class="card-meta">
            Patient: ${plan.patientId} • Created: ${formatDate(plan.createdAt)}
          </div>
        </div>
        <span class="badge ${statusClass}">${plan.status}</span>
      </div>
      
      <p>${plan.description || 'No description provided.'}</p>
      
      <div style="display: flex; gap: 8px; margin-bottom: 8px; font-size: 12px;">
        <span><strong>Start:</strong> ${formatDate(plan.startDate)}</span>
        <span><strong>End:</strong> ${formatDate(plan.endDate)}</span>
      </div>
      
      <div style="margin-bottom: 16px; font-size: 14px;">
        <strong>Tasks:</strong> ${completedTasks}/${taskCount} completed
      </div>
      
      <div class="card-actions">
        <button class="btn btn-primary btn-small" onclick="openTaskModal('${plan.id}')">
          Manage Tasks
        </button>
      </div>
    `;
    
    container.appendChild(card);
  });
}

// ========== CREATE PLAN ==========
async function handleCreatePlan(e) {
  e.preventDefault();
  
  const data = {
    patientId: document.getElementById('patientId').value,
    counselorId: counselorId,
    title: document.getElementById('title').value,
    description: document.getElementById('description').value,
    startDate: document.getElementById('startDate').value,
    endDate: document.getElementById('endDate').value || null,
    appointmentId: document.getElementById('appointmentId').value || null
  };
  
  try {
    await API_CONFIG.post('', data);
    showAlert('Recovery plan created successfully!', 'success');
    document.getElementById('createPlanForm').reset();
    toggleCreateForm();
    loadPlans();
  } catch (error) {
    showError(error);
  }
}

// ========== TASK MODAL ==========
async function openTaskModal(planId) {
  currentPlanId = planId;
  const plan = plans.find(p => p.id === planId);
  
  if (!plan) return;
  
  // Set modal title
  document.getElementById('modalPlanTitle').textContent = plan.title;
  
  // Set status select
  document.getElementById('planStatusSelect').value = plan.status;
  
  // Set default task due date to 1 week from now
  const oneWeekLater = new Date();
  oneWeekLater.setDate(oneWeekLater.getDate() + 7);
  oneWeekLater.setMinutes(oneWeekLater.getMinutes() - oneWeekLater.getTimezoneOffset());
  document.getElementById('taskDueDate').value = oneWeekLater.toISOString().slice(0, 16);
  
  // Render tasks
  renderTasks(plan.tasks || []);
  
  // Show modal
  document.getElementById('taskModal').classList.add('active');
}

function closeTaskModal() {
  document.getElementById('taskModal').classList.remove('active');
  document.getElementById('addTaskForm').reset();
  currentPlanId = '';
}

// ========== RENDER TASKS ==========
function renderTasks(tasks) {
  const container = document.getElementById('tasksList');
  const emptyState = document.getElementById('emptyTasks');
  
  if (tasks.length === 0) {
    emptyState.classList.remove('hidden');
    container.innerHTML = '';
    return;
  }
  
  emptyState.classList.add('hidden');
  container.innerHTML = '';
  
  tasks.forEach(task => {
    const li = document.createElement('li');
    li.className = `task-item ${task.completed ? 'completed' : ''}`;
    
    const dueClass = !task.completed && isOverdue(task.dueDate) ? 'task-overdue' : '';
    const statusText = task.completed 
      ? `Completed: ${formatDateTime(task.completedAt)}`
      : `Due: ${formatDate(task.dueDate)}`;
    
    li.innerHTML = `
      <div class="task-content" style="flex: 1;">
        <div class="task-description">${task.description}</div>
        <div class="task-meta">
          <span class="task-due ${dueClass}">${statusText}</span>
          ${task.counselorNotes ? `<br><em>Notes: ${task.counselorNotes}</em>` : ''}
        </div>
      </div>
      <div style="display: flex; gap: 8px;">
        <button class="btn btn-small" onclick="openEditTaskModal('${task.taskId}')">Edit</button>
        <button class="btn btn-danger btn-small" onclick="deleteTask('${task.taskId}')">Delete</button>
      </div>
    `;
    
    container.appendChild(li);
  });
}

// ========== ADD TASK ==========
async function handleAddTask(e) {
  e.preventDefault();
  
  const data = {
    description: document.getElementById('taskDescription').value,
    dueDate: document.getElementById('taskDueDate').value,
    counselorNotes: document.getElementById('taskNotes').value || null
  };
  
  try {
    const updatedPlan = await API_CONFIG.post(
      `/${currentPlanId}/counselor/${counselorId}/tasks`,
      data
    );
    
    showAlert('Task added successfully!', 'success');
    document.getElementById('addTaskForm').reset();
    
    // Update local plan data
    const planIndex = plans.findIndex(p => p.id === currentPlanId);
    if (planIndex !== -1) {
      plans[planIndex] = updatedPlan;
    }
    
    renderTasks(updatedPlan.tasks);
    loadPlans(); // Refresh main list
  } catch (error) {
    showError(error);
  }
}

// ========== EDIT TASK MODAL ==========
function openEditTaskModal(taskId) {
  currentTaskId = taskId;
  const plan = plans.find(p => p.id === currentPlanId);
  const task = plan.tasks.find(t => t.taskId === taskId);
  
  if (!task) return;
  
  // Populate form
  document.getElementById('editTaskDescription').value = task.description;
  
  // Convert ISO date to datetime-local format
  const dueDate = new Date(task.dueDate);
  dueDate.setMinutes(dueDate.getMinutes() - dueDate.getTimezoneOffset());
  document.getElementById('editTaskDueDate').value = dueDate.toISOString().slice(0, 16);
  
  document.getElementById('editTaskNotes').value = task.counselorNotes || '';
  
  // Show modal
  document.getElementById('editTaskModal').classList.add('active');
}

function closeEditTaskModal() {
  document.getElementById('editTaskModal').classList.remove('active');
  document.getElementById('editTaskForm').reset();
  currentTaskId = '';
}

// ========== UPDATE TASK ==========
async function handleEditTask(e) {
  e.preventDefault();
  
  const data = {
    description: document.getElementById('editTaskDescription').value,
    dueDate: document.getElementById('editTaskDueDate').value,
    counselorNotes: document.getElementById('editTaskNotes').value || null
  };
  
  try {
    const updatedPlan = await API_CONFIG.put(
      `/${currentPlanId}/counselor/${counselorId}/tasks/${currentTaskId}`,
      data
    );
    
    showAlert('Task updated successfully!', 'success');
    closeEditTaskModal();
    
    // Update local plan data
    const planIndex = plans.findIndex(p => p.id === currentPlanId);
    if (planIndex !== -1) {
      plans[planIndex] = updatedPlan;
    }
    
    renderTasks(updatedPlan.tasks);
    loadPlans();
  } catch (error) {
    showError(error);
  }
}

// ========== DELETE TASK ==========
async function deleteTask(taskId) {
  if (!confirm('Are you sure you want to delete this task?')) return;
  
  try {
    const updatedPlan = await API_CONFIG.delete(
      `/${currentPlanId}/counselor/${counselorId}/tasks/${taskId}`
    );
    
    showAlert('Task deleted successfully!', 'success');
    
    // Update local plan data
    const planIndex = plans.findIndex(p => p.id === currentPlanId);
    if (planIndex !== -1) {
      plans[planIndex] = updatedPlan;
    }
    
    renderTasks(updatedPlan.tasks);
    loadPlans();
  } catch (error) {
    showError(error);
  }
}

// ========== UPDATE PLAN STATUS ==========
async function handleUpdateStatus() {
  const newStatus = document.getElementById('planStatusSelect').value;
  
  if (!confirm(`Change plan status to ${newStatus}?`)) return;
  
  try {
    await API_CONFIG.patch(
      `/${currentPlanId}/counselor/${counselorId}/status`,
      { status: newStatus }
    );
    
    showAlert('Plan status updated successfully!', 'success');
    closeTaskModal();
    loadPlans();
  } catch (error) {
    showError(error);
  }
}

// ========== DELETE PLAN ==========
async function handleDeletePlan() {
  if (!confirm('Are you sure you want to DELETE this entire recovery plan? This cannot be undone!')) return;
  
  try {
    await API_CONFIG.delete(`/${currentPlanId}/counselor/${counselorId}`);
    showAlert('Recovery plan deleted successfully!', 'success');
    closeTaskModal();
    loadPlans();
  } catch (error) {
    showError(error);
  }
}