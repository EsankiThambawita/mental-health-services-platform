const API_BASE = "http://localhost:8081/api/counselors";

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
                <p><strong>Languages:</strong> ${c.languages.join(", ")}</p>
                <p><strong>Specializations:</strong> ${c.specializations.join(", ")}</p>
                <p><strong>Experience:</strong> ${c.experienceYears} years</p>
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
            alert("Error adding counselor. Check console.");
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

// Load all counselors on page load
window.onload = loadCounselors;