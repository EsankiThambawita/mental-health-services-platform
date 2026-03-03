function $(id) { return document.getElementById(id); }

function apiBase() {
    return ($("apiBase").value || "http://localhost:8083").replace(/\/$/, "");
}

function pretty(obj) {
    try { return JSON.stringify(obj, null, 2); } catch { return String(obj); }
}

function setBanner(type, text) {
    const b = $("banner");
    if (!text) { b.hidden = true; b.textContent = ""; b.className = "notice"; return; }
    b.hidden = false;
    b.textContent = text;
    b.className = "notice";
    if (type === "ok") b.style.borderLeftColor = "#16a34a";
    else if (type === "bad") b.style.borderLeftColor = "#dc2626";
    else if (type === "warn") b.style.borderLeftColor = "#f59e0b";
    else b.style.borderLeftColor = "#2563eb";
}

function setStatus(statusEl, type, text) {
    statusEl.classList.remove("ok", "warn", "bad");
    if (type) statusEl.classList.add(type);
    statusEl.textContent = text;
}

function setSummary(summaryEl, text) {
    summaryEl.textContent = text;
}

function statusBadge(status) {
    const s = String(status || "").toUpperCase();
    const cls =
        s === "CONFIRMED" ? "ok" :
            s === "CANCELLED" ? "bad" :
                s === "RESCHEDULED" ? "warn" : "";
    return `<span class="badge ${cls}">${s || "-"}</span>`;
}

async function request(method, url, body) {
    const init = {
        method,
        headers: { "Content-Type": "application/json" }
    };
    if (body !== undefined && body !== null && method !== "GET") {
        init.body = JSON.stringify(body);
    }

    const res = await fetch(url, init);
    const isJson = (res.headers.get("content-type") || "").includes("application/json");
    const data = isJson ? await res.json().catch(() => null) : await res.text().catch(() => null);

    if (!res.ok) {
        const msg = (data && data.message) ? data.message : `${res.status} ${res.statusText}`;
        const err = new Error(msg);
        err.status = res.status;
        err.data = data;
        throw err;
    }
    return data;
}

function renderTable(items) {
    const tbody = $("appointmentsTable").querySelector("tbody");
    const empty = $("emptyState");
    tbody.innerHTML = "";

    const list = Array.isArray(items) ? items : [];
    empty.hidden = list.length !== 0;

    for (const a of list) {
        const tr = document.createElement("tr");
        const time = `${a.startTime ?? ""} - ${a.endTime ?? ""}`.trim();
        tr.innerHTML = `
      <td>${a.date ?? "-"}</td>
      <td>${time || "-"}</td>
      <td>${a.counselorId ?? "-"}</td>
      <td>${statusBadge(a.status)}</td>
      <td>${a.id ?? "-"}</td>
    `;
        tbody.appendChild(tr);
    }
}

function humanError(err) {
    // Map your service messages -> user-friendly text
    const msg = (err && err.message) ? String(err.message) : "Something went wrong.";

    if (err?.status === 404) return "We couldn’t find that slot / appointment. Check the ID and try again.";
    if (err?.status === 409) {
        if (msg.toLowerCase().includes("already booked") || msg.toLowerCase().includes("taken"))
            return "That slot is already booked. Please choose another slot.";
        return "There’s a conflict (maybe already booked). Try a different slot.";
    }
    if (err?.status === 503) return "Booking service is temporarily unavailable. Please try again in a moment.";
    return msg;
}

function saveDebugJson(obj) {
    $("debugJson").textContent = pretty(obj ?? {});
}

/* BOOK */
$("bookForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    setBanner("", "");
    setStatus($("bookStatus"), "warn", "Working...");
    setSummary($("bookSummary"), "Booking your appointment...");

    const body = {
        availabilityId: $("availabilityId").value.trim(),
        userId: $("userId").value.trim()
    };

    try {
        const data = await request("POST", `${apiBase()}/api/v1/appointments`, body);
        saveDebugJson(data);

        setStatus($("bookStatus"), "ok", "Booked");
        setSummary(
            $("bookSummary"),
            `Appointment booked for ${data.date} from ${data.startTime} to ${data.endTime}. Your appointment ID is ${data.id}.`
        );
        setBanner("ok", "Appointment booked successfully.");

        if (data?.id) {
            $("cancelAppointmentId").value = data.id;
            $("rescheduleAppointmentId").value = data.id;
        }
    } catch (err) {
        saveDebugJson(err.data || { error: err.message, status: err.status });
        setStatus($("bookStatus"), "bad", "Couldn’t book");
        setSummary($("bookSummary"), humanError(err));
        setBanner("bad", humanError(err));
    }
});

$("clearBook").addEventListener("click", () => {
    $("availabilityId").value = "";
    $("userId").value = "user-1";
    setStatus($("bookStatus"), "", "Ready");
    setSummary($("bookSummary"), "Fill the form and click Book.");
    setBanner("", "");
});

/* CANCEL */
$("cancelForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    setBanner("", "");
    setStatus($("cancelStatus"), "warn", "Working...");
    setSummary($("cancelSummary"), "Cancelling your appointment...");

    const id = $("cancelAppointmentId").value.trim();
    const reason = $("cancelReason").value.trim();
    const body = reason ? { cancelReason: reason } : {};

    try {
        const data = await request("PATCH", `${apiBase()}/api/v1/appointments/${encodeURIComponent(id)}/cancel`, body);
        saveDebugJson(data);

        setStatus($("cancelStatus"), "ok", "Cancelled");
        setSummary($("cancelSummary"), `Appointment ${data.id} was cancelled.`);
        setBanner("ok", "Appointment cancelled.");
    } catch (err) {
        saveDebugJson(err.data || { error: err.message, status: err.status });
        setStatus($("cancelStatus"), "bad", "Couldn’t cancel");
        setSummary($("cancelSummary"), humanError(err));
        setBanner("bad", humanError(err));
    }
});

$("clearCancel").addEventListener("click", () => {
    $("cancelAppointmentId").value = "";
    $("cancelReason").value = "";
    setStatus($("cancelStatus"), "", "Ready");
    setSummary($("cancelSummary"), "Enter an appointment id and click Cancel.");
    setBanner("", "");
});

/* RESCHEDULE */
$("rescheduleForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    setBanner("", "");
    setStatus($("rescheduleStatus"), "warn", "Working...");
    setSummary($("rescheduleSummary"), "Rescheduling your appointment...");

    const id = $("rescheduleAppointmentId").value.trim();
    const body = { newAvailabilityId: $("newAvailabilityId").value.trim() };

    try {
        const data = await request("PATCH", `${apiBase()}/api/v1/appointments/${encodeURIComponent(id)}/reschedule`, body);
        saveDebugJson(data);

        setStatus($("rescheduleStatus"), "ok", "Rescheduled");
        setSummary(
            $("rescheduleSummary"),
            `Rescheduled to ${data.date} from ${data.startTime} to ${data.endTime}.`
        );
        setBanner("ok", "Appointment rescheduled.");
    } catch (err) {
        saveDebugJson(err.data || { error: err.message, status: err.status });
        setStatus($("rescheduleStatus"), "bad", "Couldn’t reschedule");
        setSummary($("rescheduleSummary"), humanError(err));
        setBanner("bad", humanError(err));
    }
});

$("clearReschedule").addEventListener("click", () => {
    $("rescheduleAppointmentId").value = "";
    $("newAvailabilityId").value = "";
    setStatus($("rescheduleStatus"), "", "Ready");
    setSummary($("rescheduleSummary"), "Enter the appointment id and a new slot id.");
    setBanner("", "");
});

/* GET */
$("getForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    setBanner("", "");
    setStatus($("getStatus"), "warn", "Working...");
    setSummary($("getSummary"), "Loading appointments...");

    const type = $("queryType").value;
    const value = $("queryValue").value.trim();
    const url = `${apiBase()}/api/v1/appointments?${encodeURIComponent(type)}=${encodeURIComponent(value)}`;

    try {
        const data = await request("GET", url);
        saveDebugJson(data);

        renderTable(data);
        setStatus($("getStatus"), "ok", "Loaded");
        setSummary($("getSummary"), Array.isArray(data) && data.length
            ? `Found ${data.length} appointment(s).`
            : "No appointments found."
        );
    } catch (err) {
        saveDebugJson(err.data || { error: err.message, status: err.status });
        renderTable([]);
        setStatus($("getStatus"), "bad", "Couldn’t load");
        setSummary($("getSummary"), humanError(err));
        setBanner("bad", humanError(err));
    }
});

$("clearGet").addEventListener("click", () => {
    $("queryType").value = "userId";
    $("queryValue").value = "user-1";
    renderTable([]);
    saveDebugJson([]);
    setStatus($("getStatus"), "", "Ready");
    setSummary($("getSummary"), "Search to see your appointments below.");
    setBanner("", "");
});