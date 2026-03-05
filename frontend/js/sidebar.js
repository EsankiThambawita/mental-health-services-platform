// sidebar that shows on every page
(function () {
  const pages = [
    { name: 'Mood & Resources', icon: '🧠', path: '/pages/mood-tracking/index.html' },
    { name: 'Counselors', icon: '👨‍⚕️', path: '/pages/counselors/index.html' },
    { name: 'Availability', icon: '📅', path: '/pages/availability/index.html' },
    { name: 'Appointments', icon: '📋', path: '/pages/appointments/index.html' },
    { name: 'Recovery Plan', icon: '📝', path: '' },
  ];

  // Get role from localStorage
  const role = localStorage.getItem('role');
  if (role === 'COUNSELOR') {
    pages[4].path = '/pages/recovery-plans/counselor.html';
  } else if (role === 'PATIENT') {
    pages[4].path = '/pages/recovery-plans/patient.html';
  } else {
    pages[4].path = '/pages/authentication/signin.html'; // fallback if not logged in
  }

  // Figure out which page we're on
  const currentPath = window.location.pathname;
  function isActive(pagePath) {
    return currentPath.endsWith(pagePath) || currentPath.endsWith(pagePath.replace('/index.html', '/'));
  }

  // build the nav links
  let linksHtml = '';
  pages.forEach(p => {
    const active = isActive(p.path) ? ' active' : '';
    const href = '../../' + p.path.replace(/^\//, '');
    linksHtml += `<a class="sidebar-link${active}" href="${href}">
      <span class="sidebar-link-icon">${p.icon}</span>
      <span class="sidebar-link-text">${p.name}</span>
    </a>`;
  });

  const sidebar = document.createElement('div');
  sidebar.className = 'sidebar';
  sidebar.innerHTML = `
    <div class="sidebar-logo">
      <span class="sidebar-logo-icon">🏥</span>
      <span class="sidebar-logo-text">Mental Health Platform</span>
    </div>
    <nav class="sidebar-nav">
      ${linksHtml}
    </nav>`;

  // stick it at the top of the page
  document.body.prepend(sidebar);
  document.body.classList.add('has-sidebar');
})();
