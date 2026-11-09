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
    setTimeout(() => { btn.dataset.listo = "1"; btn.click(); }, 1500);
  }

  // Intercepta el toque del sello para mostrar la animación antes de abrir la invitación.
  sobre.addEventListener("click", (e) => {
    if (e.target !== btn || btn.dataset.listo === "1" || REDUCIR) return;
    e.stopImmediatePropagation();
    e.preventDefault();
    abrir();
  }, true);

  // También se abre tocando el sobre.
  sobre3d.addEventListener("click", (e) => {
    if (e.target !== btn && !REDUCIR) abrir();
  });
    }
