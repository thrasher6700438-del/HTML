// Load players from localStorage
const players = JSON.parse(localStorage.getItem("players")) || [];

// Auto-generate head from name if avatar empty
players.forEach(p => {
    if (!p.avatar) p.avatar = `https://mc-heads.net/head/${encodeURIComponent(p.name)}/100`;
});

function renderCards() {
    const container = document.querySelector(".container");
    container.querySelectorAll(".card").forEach(c => c.remove());
    players.forEach((player, index) => {
        const card = document.createElement("div");
        card.className = "card";
        card.onclick = () => openModal(player);
        card.innerHTML = `
            <div class="rank">${index + 1}.</div>
            <div class="player">
                <img src="${player.avatar}">
                <div>
                    <div class="player-name">${player.name}</div>
                    <div class="title">${player.title} <span class="points">(${player.points})</span></div>
                </div>
            </div>
            <div class="region">${player.region || "NA"}</div>
            <div class="tiers">
                ${player.tiers.map(t => `
                    <div class="tier">
                        <div class="icon">${t.icon}</div>
                        <div class="badge">${t.label}</div>
                    </div>`).join('')}
            </div>`;
        container.appendChild(card);
    });
}

function openModal(data) {
    document.getElementById("modalName").innerText = data.name;
    document.getElementById("modalTitle").innerText = data.title;
    document.getElementById("modalRank").innerText = data.rank || "#N/A Overall";
    document.getElementById("modalPoints").innerText = data.points;
    document.getElementById("modalAvatar").src = data.avatar;
    const tiers = document.getElementById("modalTiers");
    tiers.innerHTML = "";
    data.tiers.forEach(t => {
        tiers.innerHTML += `
        <div class="tier">
            <div class="icon">${t.icon}</div>
            <div class="badge">${t.label}</div>
        </div>`;
    });
    document.getElementById("modal").classList.add("active");
}

function closeModal() { document.getElementById("modal").classList.remove("active"); }
document.getElementById("modal").addEventListener("click", e => { if (e.target.id === "modal") closeModal(); });

// Initial render
renderCards();