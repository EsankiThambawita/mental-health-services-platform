function $(id) { return document.getElementById(id); }

function setStatus(el, type, text) {
    el.classList.remove("ok", "warn", "bad");
    if (type) el.classList.add(type);
    el.textContent = text;
}

function pretty(obj) {
    try { return JSON.stringify(obj, null, 2); } catch { return String(obj); }
}

async function request(method, url, body) {
    const init = {
        method,
        headers: { "Content-Type": "application/json" }
    };
    if (body !== undefined && body !== null) init.body = JSON.stringify(body);

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

function apiBase() {
    return ($("apiBase").value || "http://localhost:8083").replace(/\/$/, "");
}

function copyText(text) {
    navigator.clipboard.writeText(text).catch(() => {});
}

function renderTable(items) {
    const tbody = $("appointmentsTable").querySelector("tbody");
    tbody.innerHTML = "";
    if (!Array.isArray(items)) return;

    for (const a of items) {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${a.id ?? ""}</td>
      <td>${a.availabilityId ?? ""}</td>
      <td>${a.userId ?? ""}</td>
      <td>${a.counselorId ?? ""}</td>
      <td>${a.date ?? ""}</td>
      <td>${a.startTime ?? ""}</td>
      <td>${a.endTime ?? ""}</td>
      <td>${a.status ?? ""}</td>
    `;
        tbody.appendChild(tr);
    }
}

/* BOOK */
$("bookForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const statusEl = $("bookStatus");
    setStatus(statusEl, "warn", "Booking...");

    const body = {
        availabilityId: $("availabilityId").value.trim(),
        userId: $("userId").value.trim()
    };

    try {
        const data = await request("POST", `${apiBase()}/api/v1/appointments`, body);
        $("bookOutput").textContent = pretty(data);
        setStatus(statusEl, "ok", "201 Created");

        // convenience: auto-fill cancel/reschedule with returned appointment id
        if (data && data.id) {
            $("cancelAppointmentId").value = data.id;
            $("rescheduleAppointmentId").value = data.id;
        }
    } catch (err) {
        $("bookOutput").textContent = pretty(err.data || { error: err.message, status: err.status });
        setStatus(statusEl, "bad", `Error ${err.status || ""}`.trim());
    }
});

$("clearBook").addEventListener("click", () => {
    $("availabilityId").value = "";
    $("userId").value = "user-1";
    $("bookOutput").textContent = "{}";
    setStatus($("bookStatus"), "", "Idle");
});

$("copyBookJson").addEventListener("click", () => copyText($("bookOutput").textContent));

/* CANCEL */
$("cancelForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const statusEl = $("cancelStatus");
    setStatus(statusEl, "warn", "Cancelling...");

    const id = $("cancelAppointmentId").value.trim();
    const cancelReason = $("cancelReason").value.trim();
    const body = cancelReason ? { cancelReason } : {};

    try {
        const data = await request("PATCH", `${apiBase()}/api/v1/appointments/${encodeURIComponent(id)}/cancel`, body);
        $("cancelOutput").textContent = pretty(data);
        setStatus(statusEl, "ok", "200 OK");
    } catch (err) {
        $("cancelOutput").textContent = pretty(err.data || { error: err.message, status: err.status });
        setStatus(statusEl, "bad", `Error ${err.status || ""}`.trim());
    }
});

$("clearCancel").addEventListener("click", () => {
    $("cancelAppointmentId").value = "";
    $("cancelReason").value = "";
    $("cancelOutput").textContent = "{}";
    setStatus($("cancelStatus"), "", "Idle");
});

$("copyCancelJson").addEventListener("click", () => copyText($("cancelOutput").textContent));

/* RESCHEDULE */
$("rescheduleForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const statusEl = $("rescheduleStatus");
    setStatus(statusEl, "warn", "Rescheduling...");

    const id = $("rescheduleAppointmentId").value.trim();
    const body = { newAvailabilityId: $("newAvailabilityId").value.trim() };

    try {
        const data = await request("PATCH", `${apiBase()}/api/v1/appointments/${encodeURIComponent(id)}/reschedule`, body);
        $("rescheduleOutput").textContent = pretty(data);
        setStatus(statusEl, "ok", "200 OK");
    } catch (err) {
        $("rescheduleOutput").textContent = pretty(err.data || { error: err.message, status: err.status });
        setStatus(statusEl, "bad", `Error ${err.status || ""}`.trim());
    }
});

$("clearReschedule").addEventListener("click", () => {
    $("rescheduleAppointmentId").value = "";
    $("newAvailabilityId").value = "";
    $("rescheduleOutput").textContent = "{}";
    setStatus($("rescheduleStatus"), "", "Idle");
});

$("copyRescheduleJson").addEventListener("click", () => copyText($("rescheduleOutput").textContent));

/* GET */
$("getForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const statusEl = $("getStatus");
    setStatus(statusEl, "warn", "Fetching...");

    const type = $("queryType").value;
    const value = $("queryValue").value.trim();
    const url = `${apiBase()}/api/v1/appointments?${encodeURIComponent(type)}=${encodeURIComponent(value)}`;

    try {
        const data = await request("GET", url);
        $("getOutput").textContent = pretty(data);
        setStatus(statusEl, "ok", "200 OK");
        renderTable(data);
    } catch (err) {
        $("getOutput").textContent = pretty(err.data || { error: err.message, status: err.status });
        setStatus(statusEl, "bad", `Error ${err.status || ""}`.trim());
        renderTable([]);
    }
});

$("clearGet").addEventListener("click", () => {
    $("queryType").value = "userId";
    $("queryValue").value = "user-1";
    $("getOutput").textContent = "[]";
    setStatus($("getStatus"), "", "Idle");
    renderTable([]);
});

$("copyGetJson").addEventListener("click", () => copyText($("getOutput").textContent));