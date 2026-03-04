const API_BASE = "http://localhost:8080/api/counselors";

async function loadCounselors() {
    const response = await fetch(API_BASE);
    const counselors = await response.json();

    const container = document.getElementById("counselorList");
    container.innerHTML = "";

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

async function createCounselor() {
    const name = document.getElementById("name").value;
    const languages = document.getElementById("languages").value.split(",");
    const specializations = document.getElementById("specializations").value.split(",");
    const experience = parseInt(document.getElementById("experience").value);

    await fetch(API_BASE, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            name: name,
            languages: languages,
            specializations: specializations,
            experienceYears: experience
        })
    });

    loadCounselors();
}

window.onload = loadCounselors;