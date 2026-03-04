// Main app logic for mood tracking & resources

// app state
let currentUserId = '';
let selectedMoodLevel = null;
let selectedMoodCategory = null;
let editingEntryId = null;
let allCategories = [];

const MOOD_CATEGORIES = ['happy', 'sad', 'anxious', 'calm', 'angry', 'excited', 'tired', 'hopeful', 'stressed', 'grateful'];
const MOOD_EMOJIS = { happy: '😊', sad: '😢', anxious: '😰', calm: '😌', angry: '😠', excited: '🤩', tired: '😴', hopeful: '🌟', stressed: '😣', grateful: '🙏' };
const CATEGORY_COLORS = { happy: '#10b981', sad: '#6366f1', anxious: '#f59e0b', calm: '#3b82f6', angry: '#ef4444', excited: '#ec4899', tired: '#8b5cf6', hopeful: '#14b8a6', stressed: '#f97316', grateful: '#06b6d4' };

// kick everything off when the page loads
document.addEventListener('DOMContentLoaded', () => {
  initTabs();
  initMoodForm();
  initResourcesTab();
  initCategoriesTab();
  initAnalyticsTab();
  initAvailabilityTab();
  initUserIdInput();
});

// save/restore user ID from localStorage
function initUserIdInput() {
  const input = document.getElementById('userId');
  const saved = localStorage.getItem('mt_userId') || '';
  if (saved) {
    input.value = saved;
    currentUserId = saved;
    // load data if we already have a user
    setTimeout(() => refreshCurrentTab(), 100);
  }
  input.addEventListener('change', () => {
    currentUserId = input.value.trim();
    localStorage.setItem('mt_userId', currentUserId);
    refreshCurrentTab();
  });
}

function requireUserId() {
  // make sure we have a user ID before doing anything user-specific
  if (!currentUserId) {
    showToast('Please enter your User ID first', 'error');
    document.getElementById('userId').focus();
    return false;
  }
  return true;
}

// tab switching
function initTabs() {
  document.querySelectorAll('.nav-tab').forEach(tab => {
    tab.addEventListener('click', () => {
      const target = tab.dataset.tab;
      document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));
      document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
      tab.classList.add('active');
      document.getElementById(`tab-${target}`).classList.add('active');
      refreshTab(target);
    });
  });
}

function refreshCurrentTab() {
  const activeTab = document.querySelector('.nav-tab.active');
  if (activeTab) refreshTab(activeTab.dataset.tab);
}

function refreshTab(tab) {
  // figure out which tab we're on and load its data
  switch (tab) {
    case 'mood':
      if (currentUserId) loadMoodEntries();
      break;
    case 'analytics':
      if (currentUserId) loadAnalytics();
      break;
    case 'resources':
      loadResources();
      break;
    case 'categories':
      loadCategories();
      break;
  }
}

// little toast popups for feedback
function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  const icons = { success: '✅', error: '❌', info: 'ℹ️' };
  toast.innerHTML = `<span class="toast-icon">${icons[type] || icons.info}</span><span>${message}</span>`;
  container.appendChild(toast);
  setTimeout(() => { toast.style.opacity = '0'; toast.style.transform = 'translateX(20px)'; setTimeout(() => toast.remove(), 300); }, 3500);
}

// mood form setup
function initMoodForm() {
  // mood level buttons (1-10)
  const selector = document.getElementById('moodSelector');
  for (let i = 1; i <= 10; i++) {
    const btn = document.createElement('button');
    btn.type = 'button';
    btn.className = 'mood-level-btn';
    btn.dataset.level = i;
    btn.textContent = i;
    btn.addEventListener('click', () => {
      document.querySelectorAll('.mood-level-btn').forEach(b => b.classList.remove('selected'));
      btn.classList.add('selected');
      selectedMoodLevel = i;
    });
    selector.appendChild(btn);
  }

  // mood category pills
  const pills = document.getElementById('categoryPills');
  MOOD_CATEGORIES.forEach(cat => {
    const pill = document.createElement('button');
    pill.type = 'button';
    pill.className = 'category-pill';
    pill.textContent = `${MOOD_EMOJIS[cat] || ''} ${cat}`;
    pill.addEventListener('click', () => {
      document.querySelectorAll('.category-pill').forEach(p => p.classList.remove('selected'));
      pill.classList.add('selected');
      selectedMoodCategory = cat;
    });
    pills.appendChild(pill);
  });

  // handle form submission
  document.getElementById('moodEntryForm').addEventListener('submit', handleMoodSubmit);
}

async function handleMoodSubmit(e) {
  e.preventDefault();
  if (!requireUserId()) return;
  if (!selectedMoodLevel) { showToast('Please select a mood level', 'error'); return; }
  if (!selectedMoodCategory) { showToast('Please select a mood category', 'error'); return; }

  const notes = document.getElementById('moodNotes').value.trim();
  const entry = { userId: currentUserId, moodLevel: selectedMoodLevel, moodCategory: selectedMoodCategory, notes };

  try {
    if (editingEntryId) {
      await MOOD_API.moodEntries.update(editingEntryId, entry);
      showToast('Mood entry updated!', 'success');
      editingEntryId = null;
      document.getElementById('moodFormTitle').textContent = 'How are you feeling?';
      document.getElementById('moodSubmitBtn').textContent = '💾 Save Mood Entry';
      document.getElementById('cancelEditBtn').classList.add('hidden');
    } else {
      await MOOD_API.moodEntries.create(entry);
      showToast('Mood entry saved!', 'success');
    }
    resetMoodForm();
    loadMoodEntries();
    loadAnalytics(); // keep stats up to date
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function resetMoodForm() {
  selectedMoodLevel = null;
  selectedMoodCategory = null;
  document.querySelectorAll('.mood-level-btn').forEach(b => b.classList.remove('selected'));
  document.querySelectorAll('.category-pill').forEach(p => p.classList.remove('selected'));
  document.getElementById('moodNotes').value = '';
}

async function loadMoodEntries() {
  if (!currentUserId) return;
  const container = document.getElementById('moodEntriesList');
  const loading = document.getElementById('moodEntriesLoading');
  const empty = document.getElementById('moodEntriesEmpty');

  container.innerHTML = '';
  loading.classList.remove('hidden');
  empty.classList.add('hidden');

  try {
    const entries = await MOOD_API.moodEntries.getByUser(currentUserId);
    loading.classList.add('hidden');

    if (!entries || entries.length === 0) {
      empty.classList.remove('hidden');
      return;
    }

    entries.forEach(entry => {
      container.appendChild(createMoodEntryCard(entry));
    });
  } catch (err) {
    loading.classList.add('hidden');
    showToast('Failed to load mood entries: ' + err.message, 'error');
  }
}

function createMoodEntryCard(entry) {
  const card = document.createElement('div');
  card.className = 'mood-entry-card';

  const levelClass = `mood-${entry.moodLevel}`;
  const date = entry.createdAt ? formatDateTime(entry.createdAt) : 'Unknown';
  const emoji = MOOD_EMOJIS[entry.moodCategory] || '🙂';

  card.innerHTML = `
    <div class="mood-entry-level ${levelClass}">${entry.moodLevel}</div>
    <div class="mood-entry-body">
      <div class="mood-entry-header">
        <span class="mood-entry-category">${emoji} ${entry.moodCategory}</span>
        <span class="mood-entry-date">${date}</span>
      </div>
      ${entry.notes ? `<div class="mood-entry-notes">${escapeHtml(entry.notes)}</div>` : ''}
      ${entry.archived ? '<span class="resource-tag" style="margin-top:4px;background:#fee2e2;color:#b91c1c;">Archived</span>' : ''}
    </div>
    <div class="mood-entry-actions">
      <button class="btn-icon" title="Edit" onclick="editMoodEntry('${entry.id}')">✏️</button>
      <button class="btn-icon" title="Archive" onclick="archiveMoodEntry('${entry.id}')">📦</button>
      <button class="btn-icon delete" title="Delete" onclick="deleteMoodEntry('${entry.id}')">🗑️</button>
    </div>`;
  return card;
}

async function editMoodEntry(id) {
  try {
    const entry = await MOOD_API.moodEntries.getById(id);
    if (!entry) return;

    editingEntryId = id;
    document.getElementById('moodFormTitle').textContent = 'Edit Mood Entry';
    document.getElementById('moodSubmitBtn').textContent = '✏️ Update Entry';
    document.getElementById('cancelEditBtn').classList.remove('hidden');

    // Set mood level
    selectedMoodLevel = entry.moodLevel;
    document.querySelectorAll('.mood-level-btn').forEach(b => {
      b.classList.toggle('selected', parseInt(b.dataset.level) === entry.moodLevel);
    });

    // Set category
    selectedMoodCategory = entry.moodCategory;
    document.querySelectorAll('.category-pill').forEach(p => {
      const text = p.textContent.trim().toLowerCase();
      p.classList.toggle('selected', text.includes(entry.moodCategory));
    });

    // Set notes
    document.getElementById('moodNotes').value = entry.notes || '';

    // Scroll to form
    document.getElementById('moodEntryForm').scrollIntoView({ behavior: 'smooth', block: 'center' });
  } catch (err) {
    showToast('Failed to load entry: ' + err.message, 'error');
  }
}

function cancelEdit() {
  editingEntryId = null;
  document.getElementById('moodFormTitle').textContent = 'How are you feeling?';
  document.getElementById('moodSubmitBtn').textContent = '💾 Save Mood Entry';
  document.getElementById('cancelEditBtn').classList.add('hidden');
  resetMoodForm();
}

async function archiveMoodEntry(id) {
  if (!confirm('Archive this mood entry?')) return;
  try {
    await MOOD_API.moodEntries.archive(id);
    showToast('Entry archived', 'success');
    loadMoodEntries();
    loadAnalytics(); // refresh stats after archiving
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function deleteMoodEntry(id) {
  if (!confirm('Delete this mood entry permanently?')) return;
  try {
    await MOOD_API.moodEntries.delete(id);
    showToast('Entry deleted', 'success');
    loadMoodEntries();
    loadAnalytics(); // refresh stats after deleting
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// analytics tab setup
function initAnalyticsTab() {
  document.getElementById('analyticsDateRange').addEventListener('change', loadAnalytics);
  document.getElementById('generateDailyBtn').addEventListener('click', () => generateSummary('daily'));
  document.getElementById('generateWeeklyBtn').addEventListener('click', () => generateSummary('weekly'));
  document.getElementById('generateMonthlyBtn').addEventListener('click', () => generateSummary('monthly'));
}

async function loadAnalytics() {
  if (!currentUserId) return;

  const range = document.getElementById('analyticsDateRange').value;
  const { startDate, endDate } = getDateRange(range);

  loadStatistics(startDate, endDate);
  loadSummaries();
}

async function loadStatistics(startDate, endDate) {
  const container = document.getElementById('statsGrid');
  try {
    const stats = await MOOD_API.moodEntries.getStatistics(currentUserId, startDate, endDate);
    container.innerHTML = `
      <div class="stat-card">
        <div class="stat-value">${stats.totalEntries}</div>
        <div class="stat-label">Total Entries</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">${stats.averageMood ? stats.averageMood.toFixed(1) : '-'}</div>
        <div class="stat-label">Average Mood</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">${stats.highestMood || '-'}</div>
        <div class="stat-label">Highest</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">${stats.lowestMood || '-'}</div>
        <div class="stat-label">Lowest</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">${stats.dominantMood || '-'}</div>
        <div class="stat-label">Dominant Mood</div>
      </div>`;

    // draw the mood distribution bar chart
    renderDistribution(stats.moodDistribution);
  } catch (err) {
    container.innerHTML = '<div class="empty-state"><p>No statistics available for this period.</p></div>';
  }
}

function renderDistribution(distribution) {
  const container = document.getElementById('moodDistribution');
  if (!distribution || Object.keys(distribution).length === 0) {
    container.innerHTML = '<p style="color:var(--mt-text-muted);font-size:0.85rem;">No distribution data yet.</p>';
    return;
  }

  const total = Object.values(distribution).reduce((a, b) => a + b, 0);
  let barHtml = '<div class="distribution-bar">';
  let legendHtml = '<div class="distribution-legend">';

  Object.entries(distribution).forEach(([mood, count]) => {
    const pct = ((count / total) * 100).toFixed(0);
    const color = CATEGORY_COLORS[mood] || '#6366f1';
    barHtml += `<div class="distribution-segment" style="width:${pct}%;background:${color};" title="${mood}: ${count}">${pct}%</div>`;
    legendHtml += `<div class="legend-item"><div class="legend-dot" style="background:${color};"></div>${mood} (${count})</div>`;
  });

  barHtml += '</div>';
  legendHtml += '</div>';
  container.innerHTML = barHtml + legendHtml;
}

async function loadSummaries() {
  const container = document.getElementById('summariesList');
  try {
    const summaries = await MOOD_API.moodAnalytics.getByUser(currentUserId);
    if (!summaries || summaries.length === 0) {
      container.innerHTML = '<div class="empty-state"><p>No summaries generated yet. Use the buttons above to generate one.</p></div>';
      return;
    }

    // newest summaries first
    summaries.sort((a, b) => new Date(b.generatedAt) - new Date(a.generatedAt));

    container.innerHTML = '<div class="analytics-grid">' +
      summaries.slice(0, 6).map(s => `
        <div class="summary-card">
          <div class="summary-card-header">
            <h4>${s.period} Summary</h4>
            <button class="btn-icon delete" title="Delete summary" onclick="deleteSummary('${s.id}')">🗑️</button>
          </div>
          <div class="big-number">${s.averageMood ? s.averageMood.toFixed(1) : '-'}</div>
          <div class="summary-period">${formatDateTime(s.periodStart)} – ${formatDateTime(s.periodEnd)}</div>
          <div style="margin-top:8px;font-size:0.8rem;color:var(--mt-text-secondary);">
            ${s.totalEntries} entries · Dominant: ${s.dominantMood || 'N/A'}
          </div>
        </div>`).join('') +
      '</div>';
  } catch (err) {
    container.innerHTML = '<div class="empty-state"><p>Failed to load summaries.</p></div>';
  }
}

async function deleteSummary(id) {
  if (!confirm('Delete this summary?')) return;
  try {
    await MOOD_API.moodAnalytics.delete(id);
    showToast('Summary deleted', 'success');
    loadSummaries();
  } catch (err) {
    showToast(err.message, 'error');
  }
}


async function generateSummary(type) {
  if (!requireUserId()) return;
  const today = new Date();
  try {
    if (type === 'daily') {
      await MOOD_API.moodAnalytics.generateDaily(currentUserId, formatDate(today));
    } else if (type === 'weekly') {
      const weekStart = new Date(today);
      weekStart.setDate(today.getDate() - today.getDay());
      await MOOD_API.moodAnalytics.generateWeekly(currentUserId, formatDate(weekStart));
    } else {
      const month = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}`;
      await MOOD_API.moodAnalytics.generateMonthly(currentUserId, month);
    }
    showToast(`${type.charAt(0).toUpperCase() + type.slice(1)} summary generated!`, 'success');
    loadSummaries();
  } catch (err) {
    showToast('Failed to generate summary: ' + err.message, 'error');
  }
}

// resources tab setup
function initResourcesTab() {
  document.getElementById('resourceSearchInput').addEventListener('input', debounce(filterResources, 400));
  document.getElementById('resourceCategoryFilter').addEventListener('change', filterResources);
  document.getElementById('resourceTypeFilter').addEventListener('change', filterResources);
  document.getElementById('resourceDifficultyFilter').addEventListener('change', filterResources);
  document.getElementById('addResourceBtn').addEventListener('click', openResourceModal);
  document.getElementById('resourceForm').addEventListener('submit', handleResourceSubmit);
  document.getElementById('closeResourceModal').addEventListener('click', closeResourceModal);
  document.getElementById('cancelResourceBtn').addEventListener('click', closeResourceModal);
  populateCategoryDropdowns();
}

// grab categories and fill the filter + modal dropdowns
function populateCategoryDropdowns() {
  return MOOD_API.categories.getAll()
    .then(cats => {
      allCategories = cats || [];
    })
    .catch(() => {
      allCategories = [];
    })
    .then(() => {
      // filter dropdown on resources tab
      const filterSelect = document.getElementById('resourceCategoryFilter');
      if (filterSelect) {
        const currentFilter = filterSelect.value;
        filterSelect.innerHTML = '<option value="">All Categories</option>';
        allCategories.forEach(cat => {
          const opt = document.createElement('option');
          opt.value = cat.name;
          opt.textContent = cat.name;
          filterSelect.appendChild(opt);
        });
        filterSelect.value = currentFilter;
      }

      // category dropdown in the add/edit modal
      const modalSelect = document.getElementById('resCategory');
      if (modalSelect) {
        const currentModal = modalSelect.value;
        modalSelect.innerHTML = '<option value="">Select category</option>';
        if (allCategories.length === 0) {
          const opt = document.createElement('option');
          opt.value = '';
          opt.textContent = '— No categories yet (create one first) —';
          opt.disabled = true;
          modalSelect.appendChild(opt);
        } else {
          allCategories.forEach(cat => {
            const opt = document.createElement('option');
            opt.value = cat.name;
            opt.textContent = cat.name;
            modalSelect.appendChild(opt);
          });
        }
        modalSelect.value = currentModal;
      }
    });
}

let allResources = [];

async function loadResources() {
  const container = document.getElementById('resourcesList');
  const loading = document.getElementById('resourcesLoading');
  const empty = document.getElementById('resourcesEmpty');

  container.innerHTML = '';
  loading.classList.remove('hidden');
  empty.classList.add('hidden');

  try {
    // fetch resources and categories at the same time
    const [resources] = await Promise.all([
      MOOD_API.resources.getAll(),
      populateCategoryDropdowns()
    ]);
    allResources = resources || [];
    loading.classList.add('hidden');
    renderResources(allResources);
  } catch (err) {
    loading.classList.add('hidden');
    showToast('Failed to load resources: ' + err.message, 'error');
  }
}

function filterResources() {
  const query = document.getElementById('resourceSearchInput').value.toLowerCase();
  const cat = document.getElementById('resourceCategoryFilter').value;
  const type = document.getElementById('resourceTypeFilter').value;
  const diff = document.getElementById('resourceDifficultyFilter').value;

  let filtered = allResources;
  if (query) filtered = filtered.filter(r => r.title.toLowerCase().includes(query) || (r.description && r.description.toLowerCase().includes(query)));
  if (cat) filtered = filtered.filter(r => r.category === cat);
  if (type) filtered = filtered.filter(r => r.resourceType === type);
  if (diff) filtered = filtered.filter(r => r.difficulty === diff);

  renderResources(filtered);
}

function renderResources(resources) {
  const container = document.getElementById('resourcesList');
  const empty = document.getElementById('resourcesEmpty');

  if (!resources || resources.length === 0) {
    container.innerHTML = '';
    // Show a relevant empty message
    const hasFilters = document.getElementById('resourceSearchInput').value ||
                       document.getElementById('resourceCategoryFilter').value ||
                       document.getElementById('resourceTypeFilter').value ||
                       document.getElementById('resourceDifficultyFilter').value;
    empty.innerHTML = hasFilters
      ? '<div class="empty-icon">🔍</div><h3>No matching resources</h3><p>Try adjusting your filters or search query.</p>'
      : '<div class="empty-icon">📚</div><h3>No resources found</h3><p>Add your first resource using the button above.</p>';
    empty.classList.remove('hidden');
    return;
  }

  empty.classList.add('hidden');
  container.innerHTML = '';
  const grid = document.createElement('div');
  grid.className = 'resources-grid';

  resources.forEach(r => {
    const card = document.createElement('div');
    card.className = 'resource-card';
    const typeLower = (r.resourceType || '').toLowerCase();
    card.innerHTML = `
      <div class="resource-card-header">
        <h3>${escapeHtml(r.title)}</h3>
        <span class="resource-type-badge ${typeLower}">${r.resourceType || 'N/A'}</span>
      </div>
      <p>${escapeHtml(r.description || '')}</p>
      <div class="resource-meta">
        ${r.category ? `<span>📂 ${r.category}</span>` : ''}
        ${r.difficulty ? `<span class="difficulty-badge ${r.difficulty.toLowerCase()}">${r.difficulty}</span>` : ''}
        ${r.durationMinutes ? `<span>⏱️ ${r.durationMinutes} min</span>` : ''}
        ${r.author ? `<span>✍️ ${r.author}</span>` : ''}
        <span>👁️ ${r.viewCount || 0} views</span>
      </div>
      ${r.tags && r.tags.length > 0 ? `<div class="resource-tags">${r.tags.map(t => `<span class="resource-tag">${t}</span>`).join('')}</div>` : ''}
      <div class="resource-card-actions">
        <button class="btn btn-sm btn-outline" onclick="viewResource('${r.id}')">View</button>
        <button class="btn btn-sm btn-outline" onclick="editResource('${r.id}')">Edit</button>
        <button class="btn btn-sm btn-danger" onclick="deleteResource('${r.id}')">Delete</button>
      </div>`;
    grid.appendChild(card);
  });

  container.appendChild(grid);
}

// Resource Modal
let editingResourceId = null;

function openResourceModal(editData = null) {
  editingResourceId = editData ? editData.id : null;
  document.getElementById('resourceModalTitle').textContent = editData ? 'Edit Resource' : 'Add New Resource';
  document.getElementById('resourceSubmitBtn').textContent = editData ? 'Update Resource' : 'Create Resource';

  // refresh categories then fill the form
  populateCategoryDropdowns().then(() => {
    if (editData) {
      document.getElementById('resTitle').value = editData.title || '';
      document.getElementById('resDescription').value = editData.description || '';
      document.getElementById('resContent').value = editData.content || '';
      document.getElementById('resCategory').value = editData.category || '';
      document.getElementById('resType').value = editData.resourceType || '';
      document.getElementById('resDifficulty').value = editData.difficulty || '';
      document.getElementById('resDuration').value = editData.durationMinutes || '';
      document.getElementById('resAuthor').value = editData.author || '';
      document.getElementById('resSourceUrl').value = editData.sourceUrl || '';
      document.getElementById('resTags').value = (editData.tags || []).join(', ');
      document.getElementById('resMoodLevels').value = (editData.recommendedMoodLevels || []).join(', ');
    } else {
      document.getElementById('resourceForm').reset();
      // Clear category selection when adding new
      const cat = document.getElementById('resCategory');
      if (cat) cat.value = '';
    }
  });

  document.getElementById('resourceModal').classList.add('open');
}

function closeResourceModal() {
  document.getElementById('resourceModal').classList.remove('open');
  editingResourceId = null;
}

async function handleResourceSubmit(e) {
  e.preventDefault();
  const resource = {
    title: document.getElementById('resTitle').value.trim(),
    description: document.getElementById('resDescription').value.trim(),
    content: document.getElementById('resContent').value.trim(),
    category: document.getElementById('resCategory').value.trim(),
    resourceType: document.getElementById('resType').value,
    difficulty: document.getElementById('resDifficulty').value,
    durationMinutes: parseInt(document.getElementById('resDuration').value) || null,
    author: document.getElementById('resAuthor').value.trim(),
    sourceUrl: document.getElementById('resSourceUrl').value.trim(),
    tags: document.getElementById('resTags').value.split(',').map(s => s.trim()).filter(Boolean),
    recommendedMoodLevels: document.getElementById('resMoodLevels').value.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n))
  };

  try {
    if (editingResourceId) {
      await MOOD_API.resources.update(editingResourceId, resource);
      showToast('Resource updated!', 'success');
    } else {
      await MOOD_API.resources.create(resource);
      showToast('Resource created!', 'success');
    }
    closeResourceModal();
    loadResources();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function viewResource(id) {
  try {
    const r = await MOOD_API.resources.getById(id);
    if (!r) return;
    // Show in a simple modal
    const content = `
      <h3 style="margin-bottom:12px;">${escapeHtml(r.title)}</h3>
      <p style="color:var(--mt-text-secondary);margin-bottom:16px;">${escapeHtml(r.description || '')}</p>
      ${r.content ? `<div style="background:var(--mt-bg-accent);padding:16px;border-radius:var(--mt-radius-md);margin-bottom:16px;font-size:0.9rem;line-height:1.7;">${escapeHtml(r.content)}</div>` : ''}
      <div class="resource-meta">
        ${r.category ? `<span>📂 ${r.category}</span>` : ''}
        ${r.resourceType ? `<span>📄 ${r.resourceType}</span>` : ''}
        ${r.difficulty ? `<span>📊 ${r.difficulty}</span>` : ''}
        ${r.durationMinutes ? `<span>⏱️ ${r.durationMinutes} min</span>` : ''}
        ${r.author ? `<span>✍️ ${r.author}</span>` : ''}
      </div>
      ${r.sourceUrl ? `<p style="margin-top:12px;"><a href="${escapeHtml(r.sourceUrl)}" target="_blank" style="color:var(--mt-accent-primary);">🔗 Source Link</a></p>` : ''}`;

    document.getElementById('resourceViewContent').innerHTML = content;
    document.getElementById('resourceViewModal').classList.add('open');
  } catch (err) {
    showToast('Failed to load resource', 'error');
  }
}

async function editResource(id) {
  try {
    const r = await MOOD_API.resources.getById(id);
    if (r) openResourceModal(r);
  } catch (err) {
    showToast('Failed to load resource', 'error');
  }
}

async function deleteResource(id) {
  if (!confirm('Delete this resource permanently?')) return;
  try {
    await MOOD_API.resources.delete(id);
    showToast('Resource deleted', 'success');
    loadResources();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// categories tab setup
let editingCategoryId = null;

function initCategoriesTab() {
  document.getElementById('addCategoryBtn').addEventListener('click', () => openCategoryModal());
  document.getElementById('categoryForm').addEventListener('submit', handleCategorySubmit);
  document.getElementById('closeCategoryModal').addEventListener('click', closeCategoryModal);
  document.getElementById('cancelCategoryBtn').addEventListener('click', closeCategoryModal);
}

async function loadCategories() {
  const container = document.getElementById('categoriesList');
  const loading = document.getElementById('categoriesLoading');
  const empty = document.getElementById('categoriesEmpty');

  container.innerHTML = '';
  loading.classList.remove('hidden');
  empty.classList.add('hidden');

  try {
    const categories = await MOOD_API.categories.getAll() || [];
    loading.classList.add('hidden');

    if (categories.length === 0) {
      empty.classList.remove('hidden');
      return;
    }

    const grid = document.createElement('div');
    grid.className = 'categories-grid';

    categories.forEach(cat => {
      const card = document.createElement('div');
      card.className = 'category-card';
      const bgColor = cat.color || '#6366f1';
      card.innerHTML = `
        <div class="category-icon" style="background:${bgColor}15;color:${bgColor};">
          ${cat.icon || '📁'}
        </div>
        <div class="category-info">
          <h4>${escapeHtml(cat.name)}</h4>
          ${cat.description ? `<p>${escapeHtml(cat.description)}</p>` : ''}
          ${cat.tags && cat.tags.length > 0 ? `<div class="category-tags">${cat.tags.map(t => `<span class="resource-tag">${escapeHtml(t)}</span>`).join('')}</div>` : ''}
        </div>
        <div class="category-actions">
          <button class="btn btn-sm btn-outline" onclick="editCategory('${cat.id}')">✏️ Edit</button>
          <button class="btn btn-sm btn-danger" onclick="deleteCategory('${cat.id}')">🗑️ Delete</button>
        </div>`;
      grid.appendChild(card);
    });

    container.appendChild(grid);
  } catch (err) {
    loading.classList.add('hidden');
    showToast('Failed to load categories: ' + err.message, 'error');
  }
}

function openCategoryModal(editData = null) {
  editingCategoryId = editData ? editData.id : null;
  document.getElementById('categoryModalTitle').textContent = editData ? 'Edit Category' : 'Add New Category';
  document.getElementById('categorySubmitBtn').textContent = editData ? 'Update Category' : 'Create Category';

  if (editData) {
    document.getElementById('catName').value = editData.name || '';
    document.getElementById('catDescription').value = editData.description || '';
    document.getElementById('catIcon').value = editData.icon || '';
    document.getElementById('catColor').value = editData.color || '#6366f1';
    document.getElementById('catDisplayOrder').value = editData.displayOrder || '';
    document.getElementById('catTags').value = (editData.tags || []).join(', ');
  } else {
    document.getElementById('categoryForm').reset();
    document.getElementById('catColor').value = '#6366f1';
  }

  document.getElementById('categoryModal').classList.add('open');
}

function closeCategoryModal() {
  document.getElementById('categoryModal').classList.remove('open');
  editingCategoryId = null;
}

async function handleCategorySubmit(e) {
  e.preventDefault();
  const category = {
    name: document.getElementById('catName').value.trim(),
    description: document.getElementById('catDescription').value.trim(),
    icon: document.getElementById('catIcon').value.trim(),
    color: document.getElementById('catColor').value,
    displayOrder: parseInt(document.getElementById('catDisplayOrder').value) || 0,
    tags: document.getElementById('catTags').value.split(',').map(s => s.trim()).filter(Boolean)
  };

  try {
    if (editingCategoryId) {
      await MOOD_API.categories.update(editingCategoryId, category);
      showToast('Category updated!', 'success');
    } else {
      await MOOD_API.categories.create(category);
      showToast('Category created!', 'success');
    }
    closeCategoryModal();
    loadCategories();
    populateCategoryDropdowns(); // keep resource dropdowns in sync
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function editCategory(id) {
  try {
    const cat = await MOOD_API.categories.getById(id);
    if (cat) openCategoryModal(cat);
  } catch (err) {
    showToast('Failed to load category', 'error');
  }
}

async function deleteCategory(id) {
  if (!confirm('Delete this category permanently?')) return;
  try {
    await MOOD_API.categories.delete(id);
    showToast('Category deleted', 'success');
    loadCategories();
    populateCategoryDropdowns(); // keep resource dropdowns in sync
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// today's available counselors (calls the availability service)
function initAvailabilityTab() {
  document.getElementById('refreshAvailabilityBtn').addEventListener('click', loadTodayAvailability);
  loadTodayAvailability();
}

async function loadTodayAvailability() {
  const container = document.getElementById('todaySlotsList');
  const loading = document.getElementById('todaySlotsLoading');
  const empty = document.getElementById('todaySlotsEmpty');

  container.innerHTML = '';
  loading.classList.remove('hidden');
  empty.classList.add('hidden');

  const today = new Date().toISOString().split('T')[0];

  try {
    const slots = await MOOD_API.counselorAvailability.getAvailableByDate(today);
    loading.classList.add('hidden');

    if (!slots || slots.length === 0) {
      empty.classList.remove('hidden');
      return;
    }

    // group by counselor so each one gets a card
    const grouped = {};
    slots.forEach(s => {
      if (!grouped[s.counselorId]) grouped[s.counselorId] = [];
      grouped[s.counselorId].push(s);
    });

    let html = '<div class="availability-today-grid">';
    Object.entries(grouped).forEach(([counselorId, counselorSlots]) => {
      html += `<div class="availability-today-card">
        <div class="availability-today-header">
          <span class="availability-today-icon">👤</span>
          <span class="availability-today-name">${escapeHtml(counselorId)}</span>
          <span class="availability-today-count">${counselorSlots.length} slot${counselorSlots.length > 1 ? 's' : ''}</span>
        </div>
        <div class="availability-today-slots">
          ${counselorSlots.map(s => `<span class="availability-today-chip">${s.startTime} – ${s.endTime}</span>`).join('')}
        </div>
      </div>`;
    });
    html += '</div>';

    container.innerHTML = html;
  } catch (err) {
    loading.classList.add('hidden');
    container.innerHTML = `<div class="empty-state"><div class="empty-icon">⚠️</div><h3>Availability service unreachable</h3><p>Make sure the Availability Management Service is running.</p></div>`;
  }
}

// helpers
function formatDateTime(dateStr) {
  if (!dateStr) return '';
  try {
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  } catch { return dateStr; }
}

function formatDate(date) {
  return date.toISOString().split('T')[0];
}

function getDateRange(range) {
  const end = new Date();
  const start = new Date();

  switch (range) {
    case '7d': start.setDate(end.getDate() - 7); break;
    case '30d': start.setDate(end.getDate() - 30); break;
    case '90d': start.setDate(end.getDate() - 90); break;
    case '1y': start.setFullYear(end.getFullYear() - 1); break;
    default: start.setDate(end.getDate() - 30);
  }

  return {
    startDate: toLocalISOString(start),
    endDate: toLocalISOString(end)
  };
}

// local ISO string without the timezone suffix (Spring doesn't like the Z)
function toLocalISOString(date) {
  const pad = n => String(n).padStart(2, '0');
  return date.getFullYear() + '-' + pad(date.getMonth() + 1) + '-' + pad(date.getDate())
    + 'T' + pad(date.getHours()) + ':' + pad(date.getMinutes()) + ':' + pad(date.getSeconds());
}

function escapeHtml(str) {
  if (!str) return '';
  const div = document.createElement('div');
  div.textContent = str;
  return div.innerHTML;
}

function debounce(fn, delay) {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => fn(...args), delay);
  };
}

