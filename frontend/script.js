let keys = JSON.parse(localStorage.getItem('str_keys')) || [];

// --- Cursor Logic ---
const dot = document.getElementById('cursor-dot');
const outline = document.getElementById('cursor-outline');
let mX = 0, mY = 0, oX = 0, oY = 0;

window.addEventListener('mousemove', e => {
    mX = e.clientX;
    mY = e.clientY;
    dot.style.transform = `translate3d(${mX}px, ${mY}px, 0)`;
});

function animateCursor() {
    oX += (mX - oX) * 0.15;
    oY += (mY - oY) * 0.15;
    outline.style.transform = `translate3d(${oX}px, ${oY}px, 0)`;
    requestAnimationFrame(animateCursor);
}
animateCursor();

// --- Profile Logic ---
function updateName(val) {
    localStorage.setItem('op_name', val);
    document.getElementById('side-pfp-init').innerText = val.charAt(0).toUpperCase();
}

function updatePFP(input) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = e => {
            const url = e.target.result;
            document.getElementById('main-pfp').innerHTML = `<img src="${url}" class="w-full h-full object-cover">`;
            document.getElementById('side-pfp').innerHTML = `<img src="${url}" class="w-full h-full object-cover">`;
            localStorage.setItem('pfp_data', url);
        };
        reader.readAsDataURL(input.files[0]);
    }
}

// --- Dashboard Init (200 Settings) ---
function init() {
    const container = document.getElementById('page-settings');
    const params = ["Kernel Protection", "HWID Spoofer", "Anti-Debug", "RSA Handshake", "Packet Filter"];
    let html = '';
    for (let i = 1; i <= 200; i++) {
        html += `<div class="card p-5 flex justify-between items-center">
            <div><p class="text-sm font-bold">${params[i % 5]} 0x${i}</p><p class="text-[9px] text-blue-500 font-black">ENCRYPTED</p></div>
            <label class="switch"><input type="checkbox" checked><span class="slider"></span></label>
        </div>`;
    }
    container.innerHTML = html;
    render();
}

// --- Core App Functions ---
function handleLogin() {
    const u = document.getElementById('user').value;
    const p = document.getElementById('pass').value;
    if (u === "admin" && p === "admin123") {
        document.getElementById('login-box').classList.add('hidden');
        document.getElementById('intro-box').classList.remove('hidden');
        setTimeout(() => {
            document.getElementById('auth-layer').classList.add('hidden');
            document.getElementById('dashboard').classList.remove('hidden');
            document.getElementById('dashboard').style.opacity = '1';
            init();
        }, 1500);
    }
}

function showPage(p) {
    document.querySelectorAll('.page-content').forEach(el => el.classList.add('hidden'));
    document.querySelectorAll('.nav-link').forEach(el => el.classList.remove('active'));
    document.getElementById('page-' + p).classList.remove('hidden');
    document.getElementById('btn-' + p).classList.add('active');
    document.getElementById('title').innerText = p.toUpperCase();
}

function addKey() {
    keys.push('KEY-' + Math.random().toString(36).substr(2, 9).toUpperCase());
    localStorage.setItem('str_keys', JSON.stringify(keys));
    render();
}

function render() {
    document.getElementById('key-count').innerText = keys.length;
    document.getElementById('key-list').innerHTML = keys.map((k, i) => `
        <tr class="border-b border-white/5">
            <td class="p-6 text-blue-400 font-mono">${k}</td>
            <td class="p-6 text-right"><button onclick="keys.splice(${i},1);render()" class="text-red-500">DEL</button></td>
        </tr>`).join('');
}

// --- Chatbot ---
function toggleChat() { document.getElementById('chat-window').classList.toggle('hidden'); }
function sendChat() {
    const input = document.getElementById('chat-in');
    const box = document.getElementById('chat-msgs');
    if (!input.value) return;
    box.innerHTML += `<div class="text-white/40 italic">User: ${input.value}</div>`;
    let res = "Kernel Command Processed.";
    if (input.value.includes("hi")) res = "System online, Operator.";
    setTimeout(() => {
        box.innerHTML += `<div class="bg-blue-600/20 p-2 rounded-lg">AI: ${res}</div>`;
        box.scrollTop = box.scrollHeight;
    }, 400);
    input.value = '';
}