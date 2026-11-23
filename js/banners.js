import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const REDUCIR = window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches;
const INTERVALO = 5000;

const css = document.createElement("link");
css.rel = "stylesheet";
css.href = "css/banners.css";
document.head.appendChild(css);

function el(tag, clase, texto) {
  const e = document.createElement(tag);
  if (clase) e.className = clase;
  if (texto) e.textContent = texto;
  return e;
}

function imgValida(src) {
  return typeof src === "string" && (/^data:image\//i.test(src) || /^https:\/\//i.test(src));
}

function linkValido(url) {
  return typeof url === "string" && (/^https:\/\//i.test(url) || /^#[a-z0-9-]+$/i.test(url));
}

async function iniciar() {
  const hero = document.querySelector(".hero");
  if (!hero) return;

  let items = [];
  try {
    const snap = await getDoc(doc(db, "settings", "banners"));
    if (!snap.exists()) return;
    const lista = Array.isArray(snap.data().items) ? snap.data().items : [];
    items = lista.filter((b) => b && imgValida(b.image));
  } catch (e) {
    console.error(e);
    return;
  }
  if (!items.length) return;

  const sec = document.createElement("section");
  sec.id = "sec-banners";
  sec.setAttribute("aria-label", "Novedades y promociones");

  const wrap = el("div", "ban-wrap");
  const vista = el("div", "ban-vista");
  const pista = el("div", "ban-pista");

  items.forEach((b, i) => {
    const slide = linkValido(b.link) ? el("a", "ban-slide") : el("div", "ban-slide");
    if (slide.tagName === "A") {
      slide.href = b.link;
      if (/^https:\/\//i.test(b.link)) { slide.target = "_blank"; slide.rel = "noopener"; }
    }
    const img = document.createElement("img");
    img.src = b.image;
    img.alt = b.alt || ("Banner " + (i + 1));
    img.decoding = "async";
    img.loading = i === 0 ? "eager" : "lazy";
    slide.appendChild(img);
    pista.appendChild(slide);
  });

  vista.appendChild(pista);
  wrap.appendChild(vista);
  sec.appendChild(wrap);
  hero.insertAdjacentElement("afterend", sec);

  if (items.length < 2) return;

  let idx = 0;
  let timer = null;

  const izq = el("button", "ban-flecha ban-izq", "‹");
  izq.type = "button"; izq.setAttribute("aria-label", "Banner anterior");
  const der = el("button", "ban-flecha ban-der", "›");
  der.type = "button"; der.setAttribute("aria-label", "Siguiente banner");
  wrap.append(izq, der);

  const puntos = el("div", "ban-puntos");
  const botones = items.map((_, i) => {
    const bt = document.createElement("button");
    bt.type = "button";
    bt.setAttribute("aria-label", "Ver banner " + (i + 1));
    bt.addEventListener("click", () => { ir(i); reiniciar(); });
    puntos.appendChild(bt);
    return bt;
  });
  sec.appendChild(puntos);

  function pintar() {
    pista.style.transform = "translate3d(" + (-idx * 100) + "%, 0, 0)";
    botones.forEach((bt, i) => bt.classList.toggle("on", i === idx));
  }

  function ir(n) {
    idx = (n + items.length) % items.length;
    pintar();
  }

  function detener() {
    if (timer) { clearInterval(timer); timer = null; }
  }

  function reiniciar() {
    detener();
    if (!REDUCIR) timer = setInterval(() => ir(idx + 1), INTERVALO);
  }

  izq.addEventListener("click", () => { ir(idx - 1); reiniciar(); });
  der.addEventListener("click", () => { ir(idx + 1); reiniciar(); });

  wrap.addEventListener("mouseenter", detener);
  wrap.addEventListener("mouseleave", reiniciar);

  let x0 = 0, y0 = 0, dx = 0, modo = "";
  vista.addEventListener("touchstart", (e) => {
    if (e.touches.length !== 1) return;
    x0 = e.touches[0].clientX;
    y0 = e.touches[0].clientY;
    dx = 0;
    modo = "";
    detener();
  }, { passive: true });

  vista.addEventListener("touchmove", (e) => {
    if (e.touches.length !== 1) return;
    const cx = e.touches[0].clientX - x0;
    const cy = e.touches[0].clientY - y0;
    if (!modo && (Math.abs(cx) > 8 || Math.abs(cy) > 8)) {
      modo = Math.abs(cx) > Math.abs(cy) ? "h" : "v";
    }
    if (modo !== "h") return;
    e.preventDefault();
    dx = cx;
    pista.style.transition = "none";
    const pct = (-idx * 100) + (dx / vista.clientWidth) * 100;
    pista.style.transform = "translate3d(" + pct + "%, 0, 0)";
  }, { passive: false });

  function soltar() {
    if (modo === "h") {
      pista.style.transition = "";
      if (Math.abs(dx) > 40) ir(idx + (dx < 0 ? 1 : -1));
      else pintar();
    }
    modo = "";
    reiniciar();
  }
  vista.addEventListener("touchend", soltar);
  vista.addEventListener("touchcancel", soltar);

  document.addEventListener("visibilitychange", () => {
    if (document.hidden) detener(); else reiniciar();
  });

  pintar();
  reiniciar();
}

iniciar();
