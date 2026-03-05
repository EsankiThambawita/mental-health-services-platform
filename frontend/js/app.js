// ============================================
// CONFIGURATION
// ============================================

const BASE_URL = "http://localhost:8082/api/v1/availability";

// ============================================
// INIT AFTER DOM LOAD (IMPORTANT)
// ============================================

document.addEventListener("DOMContentLoaded", () => {
  // DOM ELEMENTS
  const createSlotForm = document.getElementById("createSlotForm");
  const createSubmitBtn = document.getElementById("createSubmitBtn");
  const createMessage = document.getElementById("createMessage");
  const createCounselorId = document.getElementById("createCounselorId");
  const createDate = document.getElementById("createDate");
  const startTime = document.getElementById("startTime");
  const endTime = document.getElementById("endTime");

  const searchForm = document.getElementById("searchForm");
  const searchSubmitBtn = document.getElementById("searchSubmitBtn");
  const searchCounselorId = document.getElementById("searchCounselorId");
  const searchDate = document.getElementById("searchDate");

  const availableByDateForm = document.getElementById("availableByDateForm");
  const availableSubmitBtn = document.getElementById("availableSubmitBtn");
  const availableDate = document.getElementById("availableDate");

  const resultsBody = document.getElementById("resultsBody");
  const tableContainer = document.getElementById("tableContainer");
  const noResults = document.getElementById("noResults");
  const loadingSpinner = document.getElementById("loadingSpinner");

  console.log("✅ app.js loaded");
  console.log("availableByDateForm:", availableByDateForm);

  // ============================================
  // EVENT LISTENERS
  // ============================================

  createSlotForm?.addEventListener("submit", handleCreateSlot);
  searchForm?.addEventListener("submit", handleSearchSlots);
  availableByDateForm?.addEventListener("submit", handleFetchAllAvailable);

  // ============================================
  // CREATE SLOT
  // ============================================

  async function handleCreateSlot(event) {
    event.preventDefault();

    const counselorId = createCounselorId.value.trim();
    const date = createDate.value;
    const start = startTime.value;
    const end = endTime.value;

    if (!counselorId || !date || !start || !end) {
      showMessage(createMessage, "Please fill in all fields", "error");
      return;
    }

    if (start >= end) {
      showMessage(createMessage, "Start time must be before end time", "error");
      return;
    }

    setButtonLoading(createSubmitBtn, true);
    clearMessage(createMessage);

    try {
      const response = await fetch(BASE_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ counselorId, date, startTime: start, endTime: end }),
      });

      if (response.ok) {
        showMessage(createMessage, "✓ Slot created successfully!", "success");
        createSlotForm.reset();

        if (searchCounselorId.value && searchDate.value) {
          await handleSearchSlots(new Event("submit"));
        }
      } else {
        const errorText = await response.text();
        showMessage(createMessage, `Error: ${errorText}`, "error");
      }
    } catch (error) {
      console.error("Create slot error:", error);
      showMessage(createMessage, "Network error. Please try again.", "error");
    } finally {
      setButtonLoading(createSubmitBtn, false);
    }
  }

  // ============================================
  // SEARCH SLOTS (COUNSELOR + DATE)
  // ============================================

  async function handleSearchSlots(event) {
    event.preventDefault();

    const counselorId = searchCounselorId.value.trim();
    const date = searchDate.value;

    if (!counselorId || !date) {
      showMessage(createMessage, "Please enter counselor ID and date", "error");
      return;
    }

    showLoadingState(true);
    clearMessage(createMessage);
    setButtonLoading(searchSubmitBtn, true);

    try {
      const url = `${BASE_URL}?counselorId=${encodeURIComponent(counselorId)}&date=${encodeURIComponent(date)}`;
      const response = await fetch(url);

      if (!response.ok) {
        const t = await response.text();
        throw new Error(`${response.status} ${response.statusText} - ${t}`);
      }

      const data = await response.json();
      renderSlots(data);
    } catch (error) {
      console.error("Search slots error:", error);
      showMessage(createMessage, "Failed to load slots. Please try again.", "error");
      showLoadingState(false);
      showNoResults(true);
    } finally {
      setButtonLoading(searchSubmitBtn, false);
    }
  }

  // ============================================
  // SHOW ALL AVAILABLE BY DATE (THIS IS YOUR BUTTON)
  // ============================================

  async function handleFetchAllAvailable(event) {
    event.preventDefault();
    console.log("✅ Show Available clicked");

    const date = availableDate.value;

    if (!date) {
      showMessage(createMessage, "Please select a date.", "error");
      return;
    }

    showLoadingState(true);
    clearMessage(createMessage);
    setButtonLoading(availableSubmitBtn, true);

    try {
      const url = `${BASE_URL}/available?date=${encodeURIComponent(date)}`;
      console.log("Fetching:", url);

      const response = await fetch(url);

      if (!response.ok) {
        const t = await response.text();
        throw new Error(`${response.status} ${response.statusText} - ${t}`);
      }

      const data = await response.json();
      renderSlots(data);
    } catch (error) {
      console.error("Fetch available slots error:", error);
      showMessage(createMessage, "Failed to load available slots. Check backend endpoint /available.", "error");
      showLoadingState(false);
      showNoResults(true);
    } finally {
      setButtonLoading(availableSubmitBtn, false);
    }
  }

  // ============================================
  // RENDER TABLE
  // ============================================

  function renderSlots(slots) {
    resultsBody.innerHTML = "";

    if (!slots || slots.length === 0) {
      showLoadingState(false);
      showNoResults(true);
      return;
    }

    showLoadingState(false);
    showNoResults(false);
    tableContainer.classList.remove("hidden");

    slots.forEach((slot) => {
      const row = document.createElement("tr");

      const startTimeFormatted = formatTime(slot.startTime);
      const endTimeFormatted = formatTime(slot.endTime);

      const isAvailable = slot.status === "AVAILABLE";
      const statusClass = isAvailable ? "status-available" : "status-booked";
      const statusBadge = `<span class="status-badge ${statusClass}">${slot.status}</span>`;

      const actionContent = isAvailable
        ? `<button class="btn btn-success" data-slot-id="${escapeHtml(slot.availabilityId)}">Book</button>`
        : `<span style="color: var(--neutral-500);">Booked</span>`;

      row.innerHTML = `
        <td>${escapeHtml(slot.availabilityId)}</td>
        <td>${escapeHtml(slot.counselorId)}</td>
        <td>${slot.date}</td>
        <td>${startTimeFormatted}</td>
        <td>${endTimeFormatted}</td>
        <td>${statusBadge}</td>
        <td>${actionContent}</td>
      `;

      const bookBtn = row.querySelector(".btn-success");
      if (bookBtn) bookBtn.addEventListener("click", handleBookSlot);

      resultsBody.appendChild(row);
    });
  }

  // ============================================
  // BOOK SLOT
  // ============================================

  async function handleBookSlot(event) {
    const btn = event.target;
    const slotId = btn.getAttribute("data-slot-id");

    if (!slotId) return;

    setButtonLoading(btn, true);
    clearMessage(createMessage);

    try {
      const response = await fetch(`${BASE_URL}/${encodeURIComponent(slotId)}/book`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
      });

      if (!response.ok) {
        const t = await response.text();
        throw new Error(`${response.status} ${response.statusText} - ${t}`);
      }

      showMessage(createMessage, "✓ Slot booked successfully!", "success");

      // Refresh whichever section makes sense
      if (searchCounselorId.value && searchDate.value) {
        await handleSearchSlots(new Event("submit"));
      } else if (availableDate.value) {
        await handleFetchAllAvailable(new Event("submit"));
      }
    } catch (error) {
      console.error("Book slot error:", error);
      showMessage(createMessage, "Failed to book slot. Please try again.", "error");
    } finally {
      setButtonLoading(btn, false);
    }
  }

  // ============================================
  // UI HELPERS
  // ============================================

  function showLoadingState(isLoading) {
    if (isLoading) {
      loadingSpinner.classList.remove("hidden");
      tableContainer.classList.add("hidden");
      noResults.classList.add("hidden");
    } else {
      loadingSpinner.classList.add("hidden");
    }
  }

  function showNoResults(show) {
    if (show) {
      noResults.classList.remove("hidden");
      tableContainer.classList.add("hidden");
    } else {
      noResults.classList.add("hidden");
    }
  }

  function showMessage(element, text, type) {
    element.textContent = text;
    element.className = `message ${type}`;
    element.classList.remove("hidden");

    if (type === "success") setTimeout(() => clearMessage(element), 5000);
  }

  function clearMessage(element) {
    element.textContent = "";
    element.className = "message hidden";
  }

  function setButtonLoading(btn, isLoading) {
    if (!btn) return;
    btn.disabled = isLoading;
    btn.style.opacity = isLoading ? "0.7" : "1";
  }

  function formatTime(timeString) {
    if (!timeString || timeString.length !== 5) return timeString;

    const [hours, minutes] = timeString.split(":");
    const hour = parseInt(hours, 10);
    const ampm = hour >= 12 ? "PM" : "AM";
    const displayHour = hour % 12 || 12;

    return `${displayHour}:${minutes} ${ampm}`;
  }

  function escapeHtml(text) {
    const str = String(text ?? "");
    const map = { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;" };
    return str.replace(/[&<>"']/g, (m) => map[m]);
  }
});