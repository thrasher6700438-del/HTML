
const ADMIN_KEY = "drxcy123";


let players = [];


function addPlayer(playerData, key) {
    if (key !== ADMIN_KEY) {
        alert("Invalid key! Access denied.");
        return;
    }


    players.push(playerData);


    renderPlayer(playerData, players.length);
}

function renderPlayer(player, rank) {
    const container = document.querySelector(".container");
    const card = document.createElement("div");
    card.className = "card";

    card.innerHTML = `
        <div class="rank">${rank}</div>
        <div class="player">
            <img src="${player.avatar}" />
            <div class="player-name">${player.name}</div>
        </div>
        <div class="region">${player.region}</div>
        <div class="tiers">${player.tiers.map(t => `
            <div class="tier">
                <div class="icon ${t.icon}">${t.iconEmoji || ""}</div>
                <div class="badge">${t.level}</div>
            </div>`).join('')}
        </div>
    `;

    container.appendChild(card);
}