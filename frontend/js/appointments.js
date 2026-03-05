/* =========================================================
   appointments.js
   - Uses userName only (no userId)
   - Books / cancels / reschedules appointments
   - Loads available slots by date
   - Renders appointments table
   ========================================================= */

function $(id) {
    return document.getElementById(id);
}

/* -----------------------------
   API base URL helper
------------------------------ */
function apiBase() {
    const v = $("apiBase")?.value?.trim();
    return v || ENV.APPOINTMENT_BASE;
}

/* -----------------------------
   Top banner helpers
------------------------------ */
function setBanner(text, type = "info") {
    const banner = $("banner");
    if (!banner) return;

    banner.hidden = !text;
    banner.textContent = text;

    banner.style.borderLeftColor =
        type === "success" ? "#16A34A" :
            type === "error"   ? "#DC2626" :
                type === "warn"    ? "#F59E0B" :
                    "#2563EB";
}

/* -----------------------------
   Status chip helper
------------------------------ */
function setStatus(id, state, text) {
    const el = $(id);
    if (!el) return;

    el.className =
        "status " +
        (state === "ok" ? "ok" : state === "bad" ? "bad" : state === "warn" ? "warn" : "");
    el.textContent = text;
}

/* -----------------------------
   HTTP helper
   - Throws error on non-2xx
   - Returns JSON (our APIs return JSON)
------------------------------ */
async function request(method, url, body) {
    const options = { method, headers: { "Content-Type": "application/json" } };
    if (body) options.body = JSON.stringify(body);

    const res = await fetch(url, options);
    if (!res.ok) {
        const errText = await res.text();
        throw new Error(errText || "Request failed");
    }

    return res.json();
}

/* =========================================================
   LOAD AVAILABLE SLOTS WHEN DATE CHANGES
   - Calls: GET /api/v1/appointments/available-slots?date=YYYY-MM-DD
   - Populates slotSelect with availabilityId options
========================================================= */
$("dateSelect").addEventListener("change", async () => {
    const date = $("dateSelect").value;
    const slotSelect = $("slotSelect");

    slotSelect.innerHTML = "";
    slotSelect.disabled = true;

    if (!date) return;

    setBanner("Loading available slots...");
    setStatus("bookStatus", "", "Loading");
    $("bookSummary").textContent = "Fetching slots from availability service...";

    try {
        const slots = await request(
            "GET",
            `${apiBase()}/api/v1/appointments/available-slots?date=${date}`
        );

        if (!Array.isArray(slots) || slots.length === 0) {
            slotSelect.innerHTML = `<option value="">No slots available</option>`;
            setBanner("No available slots for this date.", "warn");
            setStatus("bookStatus", "warn", "No slots");
            $("bookSummary").textContent = "Try another date.";
            return;
        }

        slotSelect.innerHTML = `<option value="">Select a time</option>`;
        slots.forEach((slot) => {
            const option = document.createElement("option");
            option.value = slot.availabilityId;
            option.textContent = `${slot.startTime} - ${slot.endTime} (Counselor: ${slot.counselorId})`;
            slotSelect.appendChild(option);
        });

        slotSelect.disabled = false;
        setBanner("Select a time slot.", "info");
        setStatus("bookStatus", "", "Ready");
        $("bookSummary").textContent = `Loaded ${slots.length} slot(s).`;
    } catch (err) {
        setBanner("Could not load slots: " + err.message, "error");
        setStatus("bookStatus", "bad", "Error");
        $("bookSummary").textContent = "Availability service down or API wrong.";
    }
});

/* =========================================================
   BOOK APPOINTMENT
   - Calls: POST /api/v1/appointments
   - Body: { availabilityId, userName }
   - userName only (no userId anywhere)
========================================================= */
$("bookForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const availabilityId = $("slotSelect").value;
    const userName = $("userName").value.trim();

    if (!availabilityId) {
        setBanner("Please select a time slot.", "error");
        setStatus("bookStatus", "bad", "Missing");
        return;
    }

    if (!userName) {
        setBanner("Please enter your username.", "error");
        setStatus("bookStatus", "bad", "Missing");
        return;
    }

    setBanner("Booking appointment...");
    setStatus("bookStatus", "", "Booking");
    $("bookSummary").textContent = "Locking slot + saving appointment...";

    try {
        const data = await request("POST", `${apiBase()}/api/v1/appointments`, {
            availabilityId,
            userName
        });

        setBanner(`Appointment booked on ${data.date} from ${data.startTime} to ${data.endTime}.`, "success");
        setStatus("bookStatus", "ok", "Booked");
        $("bookSummary").textContent = `Appointment ID: ${data.id}`;

        // Reset booking UI
        $("dateSelect").value = "";
        $("slotSelect").innerHTML = `<option value="">-- Select a date first --</option>`;
        $("slotSelect").disabled = true;
    } catch (err) {
        setBanner("Booking failed: " + err.message, "error");
        setStatus("bookStatus", "bad", "Failed");
        $("bookSummary").textContent = "Slot may already be taken.";
    }
});

/* =========================================================
   CANCEL APPOINTMENT
   - Calls: PATCH /api/v1/appointments/{id}/cancel
   - Body: { cancelReason? }
   - Backend releases the slot in Availability service
========================================================= */
$("cancelForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const appointmentId = $("cancelAppointmentId").value.trim();
    const cancelReason = $("cancelReason").value.trim();

    if (!appointmentId) return;

    setBanner("Cancelling appointment...");
    setStatus("cancelStatus", "", "Cancelling");
    $("cancelSummary").textContent = "Releasing slot + marking appointment cancelled...";

    try {
        const body = cancelReason ? { cancelReason } : {};
        const data = await request(
            "PATCH",
            `${apiBase()}/api/v1/appointments/${appointmentId}/cancel`,
            body
        );

        setBanner(`Cancelled appointment ${data.id}. Slot released.`, "success");
        setStatus("cancelStatus", "ok", "Cancelled");
        $("cancelSummary").textContent = `Status: ${data.status}`;
    } catch (err) {
        setBanner("Cancel failed: " + err.message, "error");
        setStatus("cancelStatus", "bad", "Failed");
        $("cancelSummary").textContent = "Check appointment id.";
    }
});

/* -----------------------------
   Cancel form clear button
------------------------------ */
$("clearCancel").addEventListener("click", () => {
    $("cancelAppointmentId").value = "";
    $("cancelReason").value = "";
    setStatus("cancelStatus", "", "Ready");
    $("cancelSummary").textContent = "Enter an appointment id and click Cancel.";
});

/* =========================================================
   RESCHEDULE APPOINTMENT
   - Calls: PATCH /api/v1/appointments/{id}/reschedule
   - Body: { newAvailabilityId }
   - Backend releases old slot then books new slot
========================================================= */
$("rescheduleForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const appointmentId = $("rescheduleAppointmentId").value.trim();
    const newAvailabilityId = $("newAvailabilityId").value.trim();

    if (!appointmentId || !newAvailabilityId) return;

    setBanner("Rescheduling appointment...");
    setStatus("rescheduleStatus", "", "Rescheduling");
    $("rescheduleSummary").textContent = "Releasing old slot + booking new slot...";

    try {
        const data = await request(
            "PATCH",
            `${apiBase()}/api/v1/appointments/${appointmentId}/reschedule`,
            { newAvailabilityId }
        );

        setBanner(`Rescheduled appointment ${data.id} to ${data.date} ${data.startTime}-${data.endTime}.`, "success");
        setStatus("rescheduleStatus", "ok", "Rescheduled");
        $("rescheduleSummary").textContent = `New slot: ${data.availabilityId}`;
    } catch (err) {
        setBanner("Reschedule failed: " + err.message, "error");
        setStatus("rescheduleStatus", "bad", "Failed");
        $("rescheduleSummary").textContent = "Check ids and slot availability.";
    }
});

/* -----------------------------
   Reschedule form clear button
------------------------------ */
$("clearReschedule").addEventListener("click", () => {
    $("rescheduleAppointmentId").value = "";
    $("newAvailabilityId").value = "";
    setStatus("rescheduleStatus", "", "Ready");
    $("rescheduleSummary").textContent = "Enter the appointment id and a new slot id.";
});

/* =========================================================
   GET APPOINTMENTS
   - Calls: GET /api/v1/appointments?userName=... OR ?counselorId=...
   - Renders table
========================================================= */
$("getForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const userName = $("queryValue").value.trim();
    if (!userName) return;

    const tbody = $("appointmentsTable").querySelector("tbody");
    tbody.innerHTML = "";
    $("emptyState").hidden = true;

    setBanner("Loading appointments...");
    setStatus("getStatus", "", "Loading");
    $("getSummary").textContent = "Querying appointment DB...";

    const qs = new URLSearchParams({ userName });

    try {
        const appointments = await request(
            "GET",
            `${apiBase()}/api/v1/appointments?${qs.toString()}`
        );

        $("debugJson").textContent = JSON.stringify(appointments, null, 2);

        if (!Array.isArray(appointments) || appointments.length === 0) {
            setBanner("No appointments found.", "warn");
            setStatus("getStatus", "warn", "Empty");
            $("getSummary").textContent = "Nothing matched.";
            $("emptyState").hidden = false;
            return;
        }

        appointments.forEach((a) => {
            const row = document.createElement("tr");
            row.innerHTML = `
        <td>${a.date ?? ""}</td>
        <td>${a.startTime ?? ""} - ${a.endTime ?? ""}</td>
        <td>${a.counselorId ?? ""}</td>
        <td>${a.status ?? ""}</td>
        <td>${a.id ?? ""}</td>
      `;
            tbody.appendChild(row);
        });

        setBanner("Appointments loaded.", "success");
        setStatus("getStatus", "ok", "Loaded");
        $("getSummary").textContent = `Found ${appointments.length} appointment(s).`;
    } catch (err) {
        setBanner("Could not load appointments: " + err.message, "error");
        setStatus("getStatus", "bad", "Failed");
        $("getSummary").textContent = "API error.";
    }
});

$("clearGet").addEventListener("click", () => {
    $("queryValue").value = "";
    $("debugJson").textContent = "[]";
    $("appointmentsTable").querySelector("tbody").innerHTML = "";
    $("emptyState").hidden = true;

    setStatus("getStatus", "", "Ready");
    $("getSummary").textContent = "Search to see your appointments below.";
});