const AUTH_URL = "http://localhost:8081";
let token = null;
let currentUser = {};

const hazardFields = {
  flood: [["peakWaterLevelMetres","Peak Water Level (m)","number"],["riverBasin","River Basin","text"],["householdsDisplaced","Households Displaced","number"],["areaFloodedHectares","Area Flooded (ha)","number"],["durationDays","Duration (days)","number"]],
  drought: [["rainfallDeficitMm","Rainfall Deficit (mm)","number"],["consecutiveDryDays","Consecutive Dry Days","number"],["cropFailurePercentage","Crop Failure %","number"],["peopleFacingWaterShortages","People Facing Water Shortages","number"],["livestockMortalityCount","Livestock Mortality Count","number"]],
  fire: [["areaBurnedHectares","Area Burned (ha)","number"],["suspectedCause","Suspected Cause (natural/accidental/deliberate)","text"],["injuriesOrFatalities","Injuries/Fatalities","number"],["structuresDestroyed","Structures Destroyed","number"],["stillActive","Still Active (true/false)","text"]],
  zoonotic: [["pathogenName","Pathogen Name","text"],["animalSpeciesAffected","Animal Species Affected","text"],["confirmedHumanCases","Confirmed Human Cases","number"],["confirmedAnimalCases","Confirmed Animal Cases","number"],["classification","Classification (CLUSTER/OUTBREAK)","text"]],
  mining: [["mineName","Mine Name","text"],["mineType","Mine Type (formal/artisanal)","text"],["accidentType","Accident Type","text"],["trappedOrInjuredMiners","Trapped/Injured Miners","number"],["fatalities","Fatalities","number"]]
};

const hazardPorts = { flood: 8082, drought: 8083, fire: 8084, zoonotic: 8085, mining: 8086 };

function renderForm() {
  const hazard = document.getElementById("hazardSelect").value;
  const fields = hazardFields[hazard];
  let html = `
    <input id="f_severity" placeholder="Severity (LOW/MEDIUM/HIGH)">
    <input id="f_lat" type="number" step="any" placeholder="Latitude">
    <input id="f_lng" type="number" step="any" placeholder="Longitude">
    <input id="f_occurredAt" type="datetime-local">
    <input id="f_province" placeholder="Province">
  `;
  fields.forEach(([key, label, type]) => {
    html += `<input id="f_${key}" type="${type}" placeholder="${label}">`;
  });
  document.getElementById("dynamicForm").innerHTML = html;
}

function showTab(tab) {
  document.getElementById("tab-report").classList.add("hidden");
  document.getElementById("tab-incidents").classList.add("hidden");
  document.getElementById("tab-dashboard").classList.add("hidden");
  document.getElementById("tab-" + tab).classList.remove("hidden");
  if (tab === "incidents") loadIncidents();
  if (tab === "dashboard") loadDashboard();
}

async function login() {
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;
  try {
    const res = await fetch(`${AUTH_URL}/auth/login`, {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify({username, password})
    });
    if (!res.ok) throw new Error("Login failed");
    const data = await res.json();
    token = data.token;
    const payload = JSON.parse(atob(token.split(".")[1]));
    currentUser = payload;
    document.getElementById("whoami").innerText = payload.sub;
    document.getElementById("whorole").innerText = payload.role;
    document.getElementById("whohazard").innerText = payload.hazard || "ALL";
    document.getElementById("loginSection").classList.add("hidden");
    document.getElementById("appSection").classList.remove("hidden");
    renderForm();
    showTab('report');
  } catch (e) {
    document.getElementById("loginMsg").innerText = "Login failed. Check credentials.";
  }
}

function logout() {
  token = null;
  currentUser = {};
  document.getElementById("appSection").classList.add("hidden");
  document.getElementById("loginSection").classList.remove("hidden");
}

async function submitIncident() {
  const hazard = document.getElementById("hazardSelect").value;
  const port = hazardPorts[hazard];
  const body = {
    severity: document.getElementById("f_severity").value,
    latitude: parseFloat(document.getElementById("f_lat").value),
    longitude: parseFloat(document.getElementById("f_lng").value),
    occurredAt: document.getElementById("f_occurredAt").value,
    province: document.getElementById("f_province").value
  };
  hazardFields[hazard].forEach(([key, label, type]) => {
    const val = document.getElementById(`f_${key}`).value;
    body[key] = type === "number" ? parseFloat(val) : val;
  });

  try {
    const res = await fetch(`http://localhost:${port}/${hazard}-incidents`, {
      method: "POST",
      headers: {"Content-Type": "application/json", "Authorization": "Bearer " + token},
      body: JSON.stringify(body)
    });
    if (res.ok) {
      document.getElementById("submitMsg").innerText = "Incident submitted!";
    } else {
      document.getElementById("submitMsg").innerText = "Error: " + await res.text();
    }
  } catch (e) {
    document.getElementById("submitMsg").innerText = "Failed to submit.";
  }
}

async function loadIncidents() {
  const hazard = currentUser.hazard ? currentUser.hazard.toLowerCase() : document.getElementById("hazardSelect").value;
  const port = hazardPorts[hazard];
  const listDiv = document.getElementById("incidentsList");
  listDiv.innerHTML = "Loading...";

  try {
    const res = await fetch(`http://localhost:${port}/${hazard}-incidents`, {
      headers: {"Authorization": "Bearer " + token}
    });
    const incidents = await res.json();
    if (!Array.isArray(incidents) || incidents.length === 0) {
      listDiv.innerHTML = "<p>No incidents found.</p>";
      return;
    }
    listDiv.innerHTML = incidents.map(inc => `
      <div class="card">
        <p><b>ID ${inc.id}</b> | Ward: ${inc.ward} | District: ${inc.district} | Severity: ${inc.severity}</p>
        <p>Status: <span class="status-${inc.status}">${inc.status}</span></p>
        <div class="btn-row">
          ${currentUser.role === "PROVINCIAL_SUPERVISOR" && inc.status === "PENDING" ? `
            <button onclick="actOnIncident('${hazard}', ${inc.id}, 'approve')">Approve</button>
            <button onclick="actOnIncident('${hazard}', ${inc.id}, 'reject')">Reject</button>
            <button onclick="actOnIncident('${hazard}', ${inc.id}, 'request-correction')">Request Correction</button>
          ` : ""}
        </div>
      </div>
    `).join("");
  } catch (e) {
    listDiv.innerHTML = "<p>Failed to load incidents.</p>";
  }
}

async function actOnIncident(hazard, id, action) {
  const port = hazardPorts[hazard];
  await fetch(`http://localhost:${port}/${hazard}-incidents/${id}/${action}`, {
    method: "PUT",
    headers: {"Authorization": "Bearer " + token}
  });
  loadIncidents();
}

async function loadDashboard() {
  const resultDiv = document.getElementById("dashboardResult");
  resultDiv.innerHTML = "Loading...";
  try {
    const res = await fetch("http://localhost:8087/dashboard/summary", {
      headers: {"Authorization": "Bearer " + token}
    });
    const data = await res.json();
    let html = `<p><b>Total Incidents:</b> ${data.totalIncidents}</p>`;
    for (const [hazard, info] of Object.entries(data.hazards)) {
      html += `<div class="card"><b>${hazard.toUpperCase()}</b>: ${info.count ?? 0} incidents</div>`;
    }
    resultDiv.innerHTML = html;
  } catch (e) {
    resultDiv.innerHTML = "<p>Failed to load dashboard.</p>";
  }
}