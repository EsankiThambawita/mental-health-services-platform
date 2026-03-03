// Counselor Recovery Plans - JavaScript (Rewritten for Authenticated Backend)

// ========== STATE ==========
let currentPlanId = '';
let currentTaskId = '';
let plans = [];

// ========== INITIALIZATION ==========
document.addEventListener('DOMContentLoaded', () => {
  const user = JSON.parse(localStorage.getItem('user'));
  if (!user || user.role !== 'Counselor') {
    alert('You must be logged in as a counselor.');
    window.location.href = '/pages/authentication/signin.html';
    return;
  }

  // Initialize default dates
  const now = new Date();
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
  document.getElementById('startDate').value = now.toISOString().slice(0, 16);

  const threeMonthsLater = new Date();
  threeMonthsLater.setMonth(threeMonthsLater.getMonth() + 3);
  threeMonthsLater.setMinutes(threeMonthsLater.getMinutes() - threeMonthsLater.getTimezoneOffset());
  document.getElementById('endDate').value = threeMonthsLater.toISOString().slice(0, 16);

  setupEventListeners();
  loadPlans();
});

// ========== EVENT LISTENERS ==========
function setupEventListeners() {
  document.getElementById('toggleFormBtn').addEventListener('click', toggleCreateForm);
  document.getElementById('createPlanForm').addEventListener('submit', handleCreatePlan);
  document.getElementById('refreshBtn').addEventListener('click', loadPlans);
  document.getElementById('closeModal').addEventListener('click', closeTaskModal);
  document.getElementById('taskModal').addEventListener('click', e => {
    if (e.target.id === 'taskModal') closeTaskModal();
  });
  document.getElementById('addTaskForm').addEventListener('submit', handleAddTask);
  document.getElementById('updateStatusBtn').addEventListener('click', handleUpdateStatus);
  document.getElementById('deletePlanBtn').addEventListener('click', handleDeletePlan);
  document.getElementById('closeEditModal').addEventListener('click', closeEditTaskModal);
  document.getElementById('editTaskModal').addEventListener('click', e => {
    if (e.target.id === 'editTaskModal') closeEditTaskModal();
  });
  document.getElementById('editTaskForm').addEventListener('submit', handleEditTask);
}

// ========== TOGGLE CREATE FORM ==========
function toggleCreateForm() {
  const form = document.getElementById('createPlanForm');
  const btn = document.getElementById('toggleFormBtn');
  form.classList.toggle('hidden');
  btn.textContent = form.classList.contains('hidden') ? 'Show Form' : 'Hide Form';
}

// ========== LOAD PLANS ==========
async function loadPlans() {
  const loadingEl = document.getElementById('loadingPlans');
  const emptyStateEl = document.getElementById('emptyState');
  const plansListEl = document.getElementById('plansList');

  loadingEl.classList.remove('hidden');
  emptyStateEl.classList.add('hidden');
  plansListEl.innerHTML = '';

  const user = JSON.parse(localStorage.getItem('user'));

  try {
    plans = await API_CONFIG.get('/counselor', {
      headers: { 'user-id': user.id }
    });

    loadingEl.classList.add('hidden');

    if (!plans || plans.length === 0) emptyStateEl.classList.remove('hidden');
    else renderPlans(plans);
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
                        plan.status === 'COMPLETED' ? 'badge-completed' :
                        'badge-cancelled';
    
    const taskCount = plan.tasks?.length || 0;
    const completedTasks = plan.tasks?.filter(t => t.completed).length || 0;

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
        <button class="btn btn-primary btn-small" onclick="openTaskModal('${plan.id}')">Manage Tasks</button>
      </div>
    `;
    container.appendChild(card);
  });
}

// ========== CREATE PLAN ==========
async function handleCreatePlan(e) {
  e.preventDefault();
  const user = JSON.parse(localStorage.getItem('user'));

  const data = {
    patientId: document.getElementById('patientId').value,
    title: document.getElementById('title').value,
    description: document.getElementById('description').value,
    startDate: document.getElementById('startDate').value,
    endDate: document.getElementById('endDate').value || null
  };

  try {
    await API_CONFIG.post('', data, {
      headers: { 'user-id': user.id }
    });
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

  document.getElementById('modalPlanTitle').textContent = plan.title;
  document.getElementById('planStatusSelect').value = plan.status;

  const oneWeekLater = new Date();
  oneWeekLater.setDate(oneWeekLater.getDate() + 7);
  oneWeekLater.setMinutes(oneWeekLater.getMinutes() - oneWeekLater.getTimezoneOffset());
  document.getElementById('taskDueDate').value = oneWeekLater.toISOString().slice(0, 16);

  renderTasks(plan.tasks || []);
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

  if (!tasks || tasks.length === 0) {
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
    const statusText = task.completed ? `Completed: ${formatDateTime(task.completedAt)}` : `Due: ${formatDate(task.dueDate)}`;

    li.innerHTML = `
      <div class="task-content" style="flex:1;">
        <div class="task-description">${task.description}</div>
        <div class="task-meta">
          <span class="task-due ${dueClass}">${statusText}</span>
          ${task.counselorNotes ? `<br><em>Notes: ${task.counselorNotes}</em>` : ''}
        </div>
      </div>
      <div style="display:flex; gap:8px;">
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
  const user = JSON.parse(localStorage.getItem('user'));

  const data = {
    description: document.getElementById('taskDescription').value,
    dueDate: document.getElementById('taskDueDate').value,
    counselorNotes: document.getElementById('taskNotes').value || null
  };

  try {
    const updatedPlan = await API_CONFIG.post(`/${currentPlanId}/tasks`, data, {
      headers: { 'user-id': user.id }
    });
    showAlert('Task added successfully!', 'success');
    document.getElementById('addTaskForm').reset();
    updateLocalPlan(updatedPlan);
  } catch (error) {
    showError(error);
  }
}

// ========== EDIT TASK ==========
function openEditTaskModal(taskId) {
  currentTaskId = taskId;
  const plan = plans.find(p => p.id === currentPlanId);
  const task = plan.tasks.find(t => t.taskId === taskId);
  if (!task) return;

  document.getElementById('editTaskDescription').value = task.description;
  const dueDate = new Date(task.dueDate);
  dueDate.setMinutes(dueDate.getMinutes() - dueDate.getTimezoneOffset());
  document.getElementById('editTaskDueDate').value = dueDate.toISOString().slice(0, 16);
  document.getElementById('editTaskNotes').value = task.counselorNotes || '';

  document.getElementById('editTaskModal').classList.add('active');
}

function closeEditTaskModal() {
  document.getElementById('editTaskModal').classList.remove('active');
  document.getElementById('editTaskForm').reset();
  currentTaskId = '';
}

async function handleEditTask(e) {
  e.preventDefault();
  const user = JSON.parse(localStorage.getItem('user'));

  const data = {
    description: document.getElementById('editTaskDescription').value,
    dueDate: document.getElementById('editTaskDueDate').value,
    counselorNotes: document.getElementById('editTaskNotes').value || null
  };

  try {
    const updatedPlan = await API_CONFIG.put(`/${currentPlanId}/tasks/${currentTaskId}`, data, {
      headers: { 'user-id': user.id }
    });
    showAlert('Task updated successfully!', 'success');
    closeEditTaskModal();
    updateLocalPlan(updatedPlan);
  } catch (error) {
    showError(error);
  }
}

// ========== DELETE TASK ==========
async function deleteTask(taskId) {
  if (!confirm('Are you sure you want to delete this task?')) return;
  const user = JSON.parse(localStorage.getItem('user'));

  try {
    const updatedPlan = await API_CONFIG.delete(`/${currentPlanId}/tasks/${taskId}`, {
      headers: { 'user-id': user.id }
    });
    showAlert('Task deleted successfully!', 'success');
    updateLocalPlan(updatedPlan);
  } catch (error) {
    showError(error);
  }
}

// ========== PLAN STATUS ==========
async function handleUpdateStatus() {
  const newStatus = document.getElementById('planStatusSelect').value;
  if (!confirm(`Change plan status to ${newStatus}?`)) return;
  const user = JSON.parse(localStorage.getItem('user'));

  try {
    const updatedPlan = await API_CONFIG.patch(`/${currentPlanId}/status`, { status: newStatus }, {
      headers: { 'user-id': user.id }
    });
    showAlert('Plan status updated successfully!', 'success');
    closeTaskModal();
    updateLocalPlan(updatedPlan);
  } catch (error) {
    showError(error);
  }
}

// ========== DELETE PLAN ==========
async function handleDeletePlan() {
  if (!confirm('Are you sure you want to DELETE this entire recovery plan?')) return;
  const user = JSON.parse(localStorage.getItem('user'));

  try {
    await API_CONFIG.delete(`/${currentPlanId}`, { headers: { 'user-id': user.id } });
    showAlert('Recovery plan deleted successfully!', 'success');
    closeTaskModal();
    loadPlans();
  } catch (error) {
    showError(error);
  }
}

// ========== HELPERS ==========
function updateLocalPlan(updatedPlan) {
  const index = plans.findIndex(p => p.id === updatedPlan.id);
  if (index !== -1) plans[index] = updatedPlan;
  renderTasks(updatedPlan.tasks);
  loadPlans();
}

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleDateString();
}

function formatDateTime(dateStr) {
  return new Date(dateStr).toLocaleString();
}

function isOverdue(dateStr) {
  return new Date(dateStr) < new Date();
}