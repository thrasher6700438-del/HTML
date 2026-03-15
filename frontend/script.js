// Data State
let keys = JSON.parse(localStorage.getItem('strength_keys')) || [];

// Cursor Tracking
document.addEventListener('mousemove', (e) => {
    const dot = document.getElementById('cursor-dot');
    const outline = document.getElementById('cursor-outline');

    dot.style.transform = `translate(${e.clientX}px, ${e.clientY}px)`;

    // Smooth trail
    requestAnimationFrame(() => {
        outline.style.transform = `translate(${e.clientX}px, ${e.clientY}px)`;
    });
});

// LOGIN LOGIC - THE FIX
function login() {
    const user = document.getElementById('login-username').value;
    const pass = document.getElementById('login-password').value;

    // Use admin / admin123
    if (user === "admin" && pass === "admin123") {
        const loginScreen = document.getElementById('login-screen');
        const dashboard = document.getElementById('dashboard');

        // Start Transition
        loginScreen.style.opacity = '0';
        loginScreen.style.transform = 'scale(1.1)';

        setTimeout(() => {
            loginScreen.classList.add('hidden');

            // Show Dashboard with Animation
            dashboard.classList.remove('pointer-events-none');
            dashboard.style.opacity = '1';
            dashboard.style.transform = 'translateY(0)';

            initDashboard();
        }, 600);
    } else {
        const card = document.querySelector('.login-card');
        card.classList.add('shake-anim');
        setTimeout(() => card.classList.remove('shake-anim'), 500);
    }
}

function initDashboard() {
    renderKeys();
    load50Settings();
}

function load50Settings() {
    const grid = document.getElementById('page-settings');
    grid.innerHTML = '';
    for (let i = 1; i <= 50; i++) {
        grid.innerHTML += `
            <div class="card p-5 flex items-center justify-between hover:bg-white/[0.03]">
                <div>
                    <p class="font-bold text-sm tracking-tight">ENCRYPTION_PARAMETER_0${i}</p>
                    <p class="text-[9px] text-blue-500 font-bold uppercase tracking-widest">Core Security Config</p>
                </div>
                <div class="relative inline-block w-10 h-6">
                    <input type="checkbox" checked class="peer appearance-none w-10 h-6 bg-white/10 rounded-full checked:bg-blue-600 transition-all cursor-pointer">
                    <span class="absolute left-1 top-1 w-4 h-4 bg-white rounded-full transition-all peer-checked:left-5 pointer-events-none"></span>
                </div>
            </div>
        `;
    }
}

function showPage(pageId) {
    document.querySelectorAll('.page-content').forEach(p => p.classList.add('hidden'));
    document.querySelectorAll('.nav-link').forEach(n => n.classList.remove('active'));

    document.getElementById('page-' + pageId).classList.remove('hidden');
    document.getElementById('nav-' + pageId).classList.add('active');
    document.getElementById('page-title').innerText = pageId.toUpperCase();
}

function generateKey() {
    const newKey = 'STRENGTH-' + Math.random().toString(36).substr(2, 10).toUpperCase();
    keys.push({ id: newKey, user: 'ROOT_ADMIN' });
    localStorage.setItem('strength_keys', JSON.stringify(keys));
    renderKeys();
}

function renderKeys() {
    const tbody = document.getElementById('key-table-body');
    const total = document.getElementById('stat-total');
    if (!tbody) return;

    total.innerText = keys.length;
    tbody.innerHTML = keys.map((k, i) => `
        <tr class="border-t border-white/5 hover:bg-white/[0.02] transition-all">
            <td class="p-6 font-bold text-white/50">${k.user}_${i + 100}</td>
            <td class="p-6"><span class="font-mono text-blue-400 bg-blue-400/5 px-3 py-1 rounded-lg border border-blue-400/20">${k.id}</span></td>
            <td class="p-6"><span class="flex items-center gap-2 text-emerald-400 font-black text-[10px] tracking-widest uppercase"><span class="w-1.5 h-1.5 rounded-full bg-emerald-500 shadow-[0_0_10px_#10b981] animate-pulse"></span> Authorized</span></td>
            <td class="p-6 text-right">
                <button onclick="deleteKey(${i})" class="w-10 h-10 rounded-xl bg-red-500/10 text-red-500 hover:bg-red-500 hover:text-white transition-all"><i class="fas fa-trash-alt"></i></button>
            </td>
        </tr>
    `).join('');
}

function deleteKey(i) {
    keys.splice(i, 1);
    localStorage.setItem('strength_keys', JSON.stringify(keys));
    renderKeys();
}

function logout() { location.reload(); }