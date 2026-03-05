const API_BASE = `${ENV.COUNSELOR_DIRECTORY_BASE}/api/counselors`;

/**
 * Display counselors in the container
 * @param {Array} counselors - list of counselors from backend
 */
function displayCounselors(counselors) {
    const container = document.getElementById("counselorList");
    container.innerHTML = "";

    if (!counselors.length) {
        container.innerHTML = "<p>No counselors found.</p>";
        return;
    }

    counselors.forEach(c => {
        container.innerHTML += `
            <div class="card">
                <h3>${c.name}</h3>
                <p><strong>ID:</strong> <code style="background:#f0f0f0;padding:2px 6px;border-radius:4px;user-select:all">${c.id}</code></p>
                <p><strong>Languages:</strong> ${c.languages.join(", ")}</p>
                <p><strong>Specializations:</strong> ${c.specializations.join(", ")}</p>
                <p><strong>Experience:</strong> ${c.experienceYears} years</p>
                <button onclick="deleteCounselor('${c.id}', '${c.name.replace(/'/g, "\\'")}')" style="margin-top:10px;padding:6px 14px;background:#ef4444;color:#fff;border:none;border-radius:6px;cursor:pointer;font-size:0.85rem;">🗑 Delete</button>
            </div>
        `;
    });
}

/**
 * Load all counselors from backend
 */
async function loadCounselors() {
    try {
        const res = await fetch(API_BASE);
        const data = await res.json();
        displayCounselors(data);
    } catch (err) {
        console.error("Error loading counselors:", err);
        alert("Failed to load counselors. Check console.");
    }
}

const VALID_SPECIALIZATIONS = ["DEPRESSION", "ANXIETY", "CAREER", "RELATIONSHIP", "STRESS"];

/**
 * Add a new counselor
 */
async function createCounselor() {
    const name = document.getElementById("name").value.trim();
    const languages = document.getElementById("languages").value
        .split(",")
        .map(s => s.trim())
        .filter(s => s);

    // Convert input to enum-safe strings
    const specializations = document.getElementById("specializations").value
        .split(",")
        .map(s => s.trim().toUpperCase())
        .filter(s => s);

    const experience = parseInt(document.getElementById("experience").value);

    if (!name || !languages.length || !specializations.length || isNaN(experience)) {
        alert("Please fill all fields correctly");
        return;
    }

    // Validate specializations against allowed values
    const invalid = specializations.filter(s => !VALID_SPECIALIZATIONS.includes(s));
    if (invalid.length) {
        alert(`Invalid specialization(s): ${invalid.join(", ")}\n\nAllowed values: ${VALID_SPECIALIZATIONS.join(", ")}`);
        return;
    }

    try {
        const res = await fetch(API_BASE, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                name,
                languages,
                specializations,
                experienceYears: experience
            })
        });

        if (!res.ok) {
            const text = await res.text();
            console.error("POST failed:", text);
            alert("Error adding counselor: " + (text || "Unknown error. Check console."));
            return;
        }

        // Clear form
        document.getElementById("name").value = "";
        document.getElementById("languages").value = "";
        document.getElementById("specializations").value = "";
        document.getElementById("experience").value = "";

        // Reload list
        loadCounselors();
    } catch (err) {
        console.error("Error adding counselor:", err);
        alert("Error adding counselor. Check console.");
    }
}

/**
 * Search counselors by name or specialization
 */
async function searchCounselors() {
    const query = document.getElementById("search").value.trim();
    if (!query) {
        loadCounselors();
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/search?query=${encodeURIComponent(query)}`);
        const data = await res.json();
        displayCounselors(data);
    } catch (err) {
        console.error("Error searching counselors:", err);
        alert("Error searching counselors. Check console.");
    }
}

/**
 * Delete a counselor by ID
 */
async function deleteCounselor(id, name) {
    if (!confirm(`Delete counselor "${name}"? This cannot be undone.`)) return;

    try {
        const res = await fetch(`${API_BASE}/${id}`, { method: "DELETE" });

        if (!res.ok) {
            const text = await res.text();
            console.error("DELETE failed:", text);
            alert("Error deleting counselor: " + (text || "Unknown error."));
            return;
        }

        loadCounselors();
    } catch (err) {
        console.error("Error deleting counselor:", err);
        alert("Error deleting counselor. Check console.");
    }
}

// Load all counselors on page load
window.onload = loadCounselors;