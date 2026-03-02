// ============================================
// CONFIGURATION
// ============================================

const BASE_URL = "http://localhost:8082/api/v1/availability";

// ============================================
// DOM ELEMENTS
// ============================================

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

const resultsBody = document.getElementById("resultsBody");
const tableContainer = document.getElementById("tableContainer");
const noResults = document.getElementById("noResults");
const loadingSpinner = document.getElementById("loadingSpinner");

// ============================================
// EVENT LISTENERS
// ============================================

// Create slot form submission
createSlotForm.addEventListener("submit", handleCreateSlot);

// Search form submission
searchForm.addEventListener("submit", handleSearchSlots);

// ============================================
// FUNCTIONS - CREATE SLOT
// ============================================

/**
 * Handle form submission for creating a new availability slot
 */
async function handleCreateSlot(event) {
    event.preventDefault();

    // Get form values
    const counselorId = createCounselorId.value.trim();
    const date = createDate.value;
    const start = startTime.value;
    const end = endTime.value;

    // Validate form
    if (!counselorId || !date || !start || !end) {
        showMessage(createMessage, "Please fill in all fields", "error");
        return;
    }

    // Validate time logic
    if (start >= end) {
        showMessage(createMessage, "Start time must be before end time", "error");
        return;
    }

    // Show loading state
    setButtonLoading(createSubmitBtn, true);
    clearMessage(createMessage);

    try {
        const response = await fetch(BASE_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                counselorId,
                date,
                startTime: start,
                endTime: end
            })
        });

        if (response.ok) {
            // Success
            showMessage(createMessage, "✓ Slot created successfully!", "success");
            createSlotForm.reset();

            // Fetch updated slots if search criteria is filled
            if (searchCounselorId.value && searchDate.value) {
                await handleSearchSlots(new Event("submit"));
            }
        } else {
            // Error response
            const errorText = await response.text();
            showMessage(createMessage, `Error: ${errorText}`, "error");
        }
    } catch (error) {
        // Network error
        console.error("Create slot error:", error);
        showMessage(createMessage, "Network error. Please try again.", "error");
    } finally {
        setButtonLoading(createSubmitBtn, false);
    }
}

// ============================================
// FUNCTIONS - SEARCH/FETCH SLOTS
// ============================================

/**
 * Handle form submission for searching availability slots
 */
async function handleSearchSlots(event) {
    event.preventDefault();

    const counselorId = searchCounselorId.value.trim();
    const date = searchDate.value;

    // Validate
    if (!counselorId || !date) {
        showMessage(createMessage, "Please enter counselor ID and date", "error");
        return;
    }

    // Show loading state
    showLoadingState(true);
    clearMessage(createMessage);

    try {
        const response = await fetch(
            `${BASE_URL}?counselorId=${encodeURIComponent(counselorId)}&date=${encodeURIComponent(date)}`
        );

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        // Render results
        renderSlots(data);
    } catch (error) {
        console.error("Search slots error:", error);
        showMessage(createMessage, "Failed to load slots. Please try again.", "error");
        showLoadingState(false);
        showNoResults(true);
    }
}

/**
 * Render slots table with data
 */
function renderSlots(slots) {
    // Clear previous data
    resultsBody.innerHTML = "";

    if (!slots || slots.length === 0) {
        showLoadingState(false);
        showNoResults(true);
        return;
    }

    // Show table, hide others
    showLoadingState(false);
    showNoResults(false);
    tableContainer.classList.remove("hidden");

    // Create table rows
    slots.forEach((slot) => {
        const row = document.createElement("tr");

        // Format time for display
        const startTimeFormatted = formatTime(slot.startTime);
        const endTimeFormatted = formatTime(slot.endTime);

        // Determine status badge class
        const isAvailable = slot.status === "AVAILABLE";
        const statusClass = isAvailable ? "status-available" : "status-booked";
        const statusBadge = `<span class="status-badge ${statusClass}">${slot.status}</span>`;

        // Create action button or text
        const actionContent = isAvailable
            ? `<button class="btn btn-success" data-slot-id="${slot.availabilityId}">Book</button>`
            : `<span style="color: var(--neutral-500);">Booked</span>`;

        // Build row HTML
        row.innerHTML = `
            <td>${escapeHtml(slot.availabilityId)}</td>
            <td>${escapeHtml(slot.counselorId)}</td>
            <td>${slot.date}</td>
            <td>${startTimeFormatted}</td>
            <td>${endTimeFormatted}</td>
            <td>${statusBadge}</td>
            <td>${actionContent}</td>
        `;

        // Add event listener for book button
        const bookBtn = row.querySelector(".btn-success");
        if (bookBtn) {
            bookBtn.addEventListener("click", handleBookSlot);
        }

        resultsBody.appendChild(row);
    });
}

// ============================================
// FUNCTIONS - BOOK SLOT
// ============================================

/**
 * Handle booking a slot
 */
async function handleBookSlot(event) {
    const btn = event.target;
    const slotId = btn.getAttribute("data-slot-id");

    if (!slotId) {
        console.error("Slot ID not found");
        return;
    }

    // Show loading state
    const originalText = btn.textContent;
    setButtonLoading(btn, true);

    try {
        const response = await fetch(`${BASE_URL}/${encodeURIComponent(slotId)}/book`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (response.ok) {
            // Success
            showMessage(createMessage, "✓ Slot booked successfully!", "success");

            // Refresh search results
            if (searchCounselorId.value && searchDate.value) {
                await handleSearchSlots(new Event("submit"));
            }
        } else {
            throw new Error("Booking failed");
        }
    } catch (error) {
        console.error("Book slot error:", error);
        showMessage(createMessage, "Failed to book slot. Please try again.", "error");
        setButtonLoading(btn, false);
    }
}

// ============================================
// UTILITY FUNCTIONS
// ============================================

/**
 * Show/hide loading spinner
 */
function showLoadingState(isLoading) {
    if (isLoading) {
        loadingSpinner.classList.remove("hidden");
        tableContainer.classList.add("hidden");
        noResults.classList.add("hidden");
    } else {
        loadingSpinner.classList.add("hidden");
    }
}

/**
 * Show/hide no results message
 */
function showNoResults(show) {
    if (show) {
        noResults.classList.remove("hidden");
        tableContainer.classList.add("hidden");
    } else {
        noResults.classList.add("hidden");
    }
}

/**
 * Display a message (success or error)
 */
function showMessage(element, text, type) {
    element.textContent = text;
    element.className = `message ${type}`;
    element.classList.remove("hidden");

    // Auto-hide success messages after 5 seconds
    if (type === "success") {
        setTimeout(() => {
            clearMessage(element);
        }, 5000);
    }
}

/**
 * Clear/hide message
 */
function clearMessage(element) {
    element.textContent = "";
    element.className = "message hidden";
}

/**
 * Set button loading state
 */
function setButtonLoading(btn, isLoading) {
    if (isLoading) {
        btn.disabled = true;
        btn.style.opacity = "0.7";
    } else {
        btn.disabled = false;
        btn.style.opacity = "1";
    }
}

/**
 * Format time from 24-hour format to readable format
 */
function formatTime(timeString) {
    if (!timeString || timeString.length !== 5) return timeString;

    const [hours, minutes] = timeString.split(":");
    const hour = parseInt(hours, 10);
    const ampm = hour >= 12 ? "PM" : "AM";
    const displayHour = hour % 12 || 12;

    return `${displayHour}:${minutes} ${ampm}`;
}
async function fetchAllAvailable() {
    const date = document.getElementById("availableDate").value;

    if (!date) {
        alert("Please select a date.");
        return;
    }

    const response = await fetch(`${BASE_URL}/available?date=${date}`);
    const data = await response.json();

    const tbody = document.getElementById("resultsBody");
    tbody.innerHTML = "";

    data.forEach(slot => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${slot.availabilityId}</td>
            <td>${slot.counselorId}</td>
            <td>${slot.date}</td>
            <td>${slot.startTime}</td>
            <td>${slot.endTime}</td>
            <td>${slot.status}</td>
            <td>
                <button onclick="bookSlot('${slot.availabilityId}')">Book</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}
/**
 * Escape HTML to prevent XSS
 */
function escapeHtml(text) {
    const map = {
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    };
    return text.replace(/[&<>"']/g, (m) => map[m]);
}
