function $(id) {
    return document.getElementById(id);
}

function apiBase() {
    return "http://localhost:8083"; // Appointment service port
}

function showMessage(text, type = "info") {
    const msg = $("message");
    msg.textContent = text;
    msg.className = "message " + type;
}

async function request(method, url, body) {

    const options = {
        method,
        headers: { "Content-Type": "application/json" }
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    const res = await fetch(url, options);

    if (!res.ok) {
        const errText = await res.text();
        throw new Error(errText || "Something went wrong");
    }

    return res.json();
}

/* LOAD SLOTS WHEN DATE CHANGES */

$("dateSelect").addEventListener("change", async () => {

    const date = $("dateSelect").value;
    const slotSelect = $("slotSelect");

    slotSelect.innerHTML = "";
    slotSelect.disabled = true;

    if (!date) return;

    showMessage("Loading available slots...");

    try {

        const slots = await request(
            "GET",
            `${apiBase()}/api/v1/appointments/available-slots?date=${date}`
        );

        if (!slots.length) {
            slotSelect.innerHTML =
                `<option>No slots available</option>`;
            showMessage("No available slots for this date.", "error");
            return;
        }

        slotSelect.innerHTML =
            `<option value="">Select a time</option>`;

        slots.forEach(slot => {
            const option = document.createElement("option");
            option.value = slot.availabilityId;
            option.textContent =
                `${slot.startTime} - ${slot.endTime} (Counselor: ${slot.counselorId})`;
            slotSelect.appendChild(option);
        });

        slotSelect.disabled = false;
        showMessage("Select a time slot.");

    } catch (err) {
        showMessage("Could not load slots.", "error");
    }

});


/* BOOK APPOINTMENT */

$("bookForm").addEventListener("submit", async (e) => {

    e.preventDefault();

    const availabilityId = $("slotSelect").value;
    const userId = $("userId").value.trim();

    if (!availabilityId) {
        showMessage("Please select a time slot.", "error");
        return;
    }

    showMessage("Booking appointment...");

    try {

        const data = await request(
            "POST",
            `${apiBase()}/api/v1/appointments`,
            { availabilityId, userId }
        );

        showMessage(
            `Appointment booked on ${data.date} from ${data.startTime} to ${data.endTime}.`,
            "success"
        );

        $("dateSelect").value = "";
        $("slotSelect").innerHTML =
            `<option>Select a date first</option>`;
        $("slotSelect").disabled = true;

    } catch (err) {
        showMessage("Booking failed. Slot may already be taken.", "error");
    }

});


/* LOAD MY APPOINTMENTS */

$("loadAppointments").addEventListener("click", async () => {

    const userId = $("viewUserId").value.trim();
    const tbody = $("appointmentsTable").querySelector("tbody");
    tbody.innerHTML = "";

    showMessage("Loading appointments...");

    try {

        const appointments = await request(
            "GET",
            `${apiBase()}/api/v1/appointments?userId=${userId}`
        );

        if (!appointments.length) {
            showMessage("No appointments found.");
            return;
        }

        appointments.forEach(a => {

            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${a.date}</td>
                <td>${a.startTime} - ${a.endTime}</td>
                <td>${a.counselorId}</td>
                <td>${a.status}</td>
            `;

            tbody.appendChild(row);
        });

        showMessage("Appointments loaded.", "success");

    } catch (err) {
        showMessage("Could not load appointments.", "error");
    }

});