const REDUCIR = window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches;
const sobre = document.getElementById("sobre");
const caja = sobre ? sobre.querySelector(".sobre-caja") : null;
const btn = document.getElementById("btn-abrir");
const nombre = document.getElementById("sobre-nombre");

function el(tag, clase, texto) {
  const e = document.createElement(tag);
  if (clase) e.className = clase;
  if (texto) e.textContent = texto;
  return e;
}

/* Sonido generado por el navegador: crac del sello, roce de papel y campanillas */
function sonido() {
  try {
    const AC = window.AudioContext || window.webkitAudioContext;
    if (!AC) return;
    const ctx = new AC();
    const t0 = ctx.currentTime;
    const master = ctx.createGain();
    master.gain.value = 0.5;
    master.connect(ctx.destination);

    const ruido = (dur) => {
      const buf = ctx.createBuffer(1, Math.floor(ctx.sampleRate * dur), ctx.sampleRate);
      const d = buf.getChannelData(0);
      for (let i = 0; i < d.length; i++) d[i] = Math.random() * 2 - 1;
      const s = ctx.createBufferSource();
      s.buffer = buf;
      return s;
    };

    const n1 = ruido(0.08);
    const f1 = ctx.createBiquadFilter();
    f1.type = "highpass"; f1.frequency.value = 1500;
    const g1 = ctx.createGain();
    g1.gain.setValueAtTime(0.35, t0);
    g1.gain.exponentialRampToValueAtTime(0.001, t0 + 0.08);
    n1.connect(f1); f1.connect(g1); g1.connect(master);
    n1.start(t0);

    const n2 = ruido(0.7);
    const f2 = ctx.createBiquadFilter();
    f2.type = "bandpass"; f2.Q.value = 0.8;
    f2.frequency.setValueAtTime(600, t0 + 0.1);
    f2.frequency.exponentialRampToValueAtTime(3500, t0 + 0.7);
    const g2 = ctx.createGain();
    g2.gain.setValueAtTime(0.0001, t0 + 0.1);
    g2.gain.linearRampToValueAtTime(0.18, t0 + 0.3);
    g2.gain.exponentialRampToValueAtTime(0.001, t0 + 0.8);
    n2.connect(f2); f2.connect(g2); g2.connect(master);
    n2.start(t0 + 0.1);

    [1046.5, 1318.5, 1568, 2093].forEach((fr, i) => {
      const o = ctx.createOscillator();
      o.type = "sine"; o.frequency.value = fr;
      const g = ctx.createGain();
      const t = t0 + 0.45 + i * 0.1;
      g.gain.setValueAtTime(0.0001, t);
      g.gain.exponentialRampToValueAtTime(0.14, t + 0.02);
      g.gain.exponentialRampToValueAtTime(0.0001, t + 0.9);
      o.connect(g); g.connect(master);
      o.start(t); o.stop(t + 1);
    });

    setTimeout(() => { ctx.close().catch(() => {}); }, 2500);
  } catch (e) { /* si el navegador no lo permite, la invitación sigue igual */ }
}

if (sobre && caja && btn && nombre) montar();

function montar() {
  const link = document.createElement("link");
  link.rel = "stylesheet";
  link.href = "css/sobre.css";
  document.head.appendChild(link);

  const pre = caja.querySelector("p");
  const sobre3d = el("div", "sobre3d");

  const carta = el("div", "carta");
  const tipo = el("p", "carta-tipo");
  carta.append(el("div", "carta-adorno", "✦"), tipo, el("p", "carta-esp", "Te esperamos"));

  const texto = el("div", "sobre-texto");
  if (pre) { pre.className = "sobre-pre"; texto.appendChild(pre); }
  texto.appendChild(nombre);
  caja.querySelectorAll(".para").forEach((p) => texto.appendChild(p));

  btn.className = "sello";
  btn.textContent = "KE";
  btn.setAttribute("aria-label", "Abrir invitación");

  sobre3d.append(carta, el("div", "sobre-frente"), texto, el("div", "solapa"), btn);
  caja.append(sobre3d, el("p", "toca", "Tocá el sello para abrir"));

  if (!REDUCIR) {
    for (let i = 0; i < 12; i++) {
      const b = el("span", "brillo");
      const t = 4 + Math.random() * 7;
      b.style.width = b.style.height = t + "px";
      b.style.left = Math.random() * 100 + "%";
      b.style.animationDuration = 7 + Math.random() * 8 + "s";
      b.style.animationDelay = Math.random() * 6 + "s";
      sobre.appendChild(b);
    }
  }

  let abierto = false;

  function chispas() {
    for (let i = 0; i < 16; i++) {
      const c = el("span", "chispa");
      const ang = (i / 16) * Math.PI * 2 + Math.random() * 0.4;
      const dist = 70 + Math.random() * 90;
      c.style.setProperty("--dx", Math.cos(ang) * dist + "px");
      c.style.setProperty("--dy", Math.sin(ang) * dist + "px");
      sobre3d.appendChild(c);
      setTimeout(() => c.remove(), 1000);
    }
  }

  function abrir() {
    if (abierto) return;
    abierto = true;
    const t = document.getElementById("tipo");
    tipo.textContent = (t && t.textContent) || "Estás invitado";
    sobre3d.classList.add("abierto");
    chispas();
    sonido();
    setTimeout(() => { btn.dataset.listo = "1"; btn.click(); }, 1500);
  }

  sobre.addEventListener("click", (e) => {
    if (e.target !== btn || btn.dataset.listo === "1" || REDUCIR) return;
    e.stopImmediatePropagation();
    e.preventDefault();
    abrir();
  }, true);

  sobre3d.addEventListener("click", (e) => {
    if (e.target !== btn && !REDUCIR) abrir();
  });
      }
