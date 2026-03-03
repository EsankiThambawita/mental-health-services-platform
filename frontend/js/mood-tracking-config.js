// ============================================================
// API Configuration for Resources & Mood Tracking Service
// ============================================================

const MOOD_API = {
  BASE_URL: 'http://localhost:8081/api',

  // ---------- URL Builders ----------
  url: (path) => `${MOOD_API.BASE_URL}${path}`,

  // ---------- Response Handler ----------
  handleResponse: async (response) => {
    if (response.status === 204) return null;
    if (!response.ok) {
      let msg = 'An error occurred';
      try {
        const err = await response.json();
        msg = err.message || err.error || msg;
      } catch (_) {
        msg = await response.text() || msg;
      }
      throw new Error(msg);
    }
    const text = await response.text();
    return text ? JSON.parse(text) : null;
  },

  // ---------- HTTP Methods ----------
  get: async (path) => {
    const res = await fetch(MOOD_API.url(path));
    return MOOD_API.handleResponse(res);
  },

  post: async (path, data) => {
    const res = await fetch(MOOD_API.url(path), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return MOOD_API.handleResponse(res);
  },

  put: async (path, data) => {
    const res = await fetch(MOOD_API.url(path), {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return MOOD_API.handleResponse(res);
  },

  delete: async (path) => {
    const res = await fetch(MOOD_API.url(path), { method: 'DELETE' });
    return MOOD_API.handleResponse(res);
  },

  // ===================================================
  // MOOD ENTRIES API
  // ===================================================
  moodEntries: {
    create: (entry) => MOOD_API.post('/v1/mood-entries', entry),
    getById: (id) => MOOD_API.get(`/v1/mood-entries/${id}`),
    getByUser: (userId) => MOOD_API.get(`/v1/mood-entries/user/${userId}`),
    update: (id, entry) => MOOD_API.put(`/v1/mood-entries/${id}`, entry),
    archive: (id) => MOOD_API.put(`/v1/mood-entries/${id}/archive`),
    delete: (id) => MOOD_API.delete(`/v1/mood-entries/${id}`),
    getStatistics: (userId, startDate, endDate) =>
      MOOD_API.get(`/v1/mood-entries/user/${userId}/statistics?startDate=${startDate}&endDate=${endDate}`),
  },

  // ===================================================
  // MOOD ANALYTICS API
  // ===================================================
  moodAnalytics: {
    generateDaily: (userId, date) =>
      MOOD_API.post(`/v1/mood-analytics/daily/${userId}?date=${date}`, {}),
    generateWeekly: (userId, weekStart) =>
      MOOD_API.post(`/v1/mood-analytics/weekly/${userId}?weekStart=${weekStart}`, {}),
    generateMonthly: (userId, month) =>
      MOOD_API.post(`/v1/mood-analytics/monthly/${userId}?month=${month}`, {}),
    getByUser: (userId) => MOOD_API.get(`/v1/mood-analytics/user/${userId}`),
    delete: (id) => MOOD_API.delete(`/v1/mood-analytics/${id}`),
  },

  // ===================================================
  // RESOURCES API
  // ===================================================
  resources: {
    create: (resource) => MOOD_API.post('/v1/resources', resource),
    getById: (id) => MOOD_API.get(`/v1/resources/${id}`),
    getAll: () => MOOD_API.get('/v1/resources'),
    update: (id, resource) => MOOD_API.put(`/v1/resources/${id}`, resource),
    delete: (id) => MOOD_API.delete(`/v1/resources/${id}`),
  },

  // ===================================================
  // RESOURCE CATEGORIES API
  // ===================================================
  categories: {
    create: (category) => MOOD_API.post('/v1/resource-categories', category),
    getById: (id) => MOOD_API.get(`/v1/resource-categories/${id}`),
    getAll: () => MOOD_API.get('/v1/resource-categories'),
    update: (id, category) => MOOD_API.put(`/v1/resource-categories/${id}`, category),
    delete: (id) => MOOD_API.delete(`/v1/resource-categories/${id}`),
  }
};

