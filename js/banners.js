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

function montarCarrusel({ ancla, idSec, aria, etiqueta, items, eagerPrimera }) {
  if (!ancla || !items.length) return;

  const sec = el("section", "banners-sec");
  sec.id = idSec;
  sec.setAttribute("aria-label", aria);

  if (etiqueta) sec.appendChild(el("p", "ban-etiqueta", etiqueta));

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
    img.alt = b.alt || (aria + " " + (i + 1));
    img.decoding = "async";
    img.loading = (eagerPrimera && i === 0) ? "eager" : "lazy";
    slide.appendChild(img);
    pista.appendChild(slide);
  });

  vista.appendChild(pista);
  wrap.appendChild(vista);
  sec.appendChild(wrap);
  ancla.insertAdjacentElement("afterend", sec);

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

async function iniciar() {
  const hero = document.querySelector(".hero");
  const precios = document.getElementById("precios");

  const [b1, b2] = await Promise.all([
    getDoc(doc(db, "settings", "banners")).catch(() => null),
    getDoc(doc(db, "settings", "bannersPubli")).catch(() => null)
  ]);

  if (b1 && b1.exists()) {
    const items1 = (Array.isArray(b1.data().items) ? b1.data().items : []).filter((b) => b && imgValida(b.image));
    montarCarrusel({
      ancla: hero,
      idSec: "sec-banners",
      aria: "Novedades y promociones",
      etiqueta: "",
      items: items1,
      eagerPrimera: true
    });
  }

  if (b2 && b2.exists()) {
    const d2 = b2.data();
    const items2 = (Array.isArray(d2.items) ? d2.items : []).filter((b) => b && imgValida(b.image));
    montarCarrusel({
      ancla: precios,
      idSec: "sec-banners-publi",
      aria: "Publicidad",
      etiqueta: String(d2.label || "").trim(),
      items: items2,
      eagerPrimera: false
    });
  }
}

iniciar();
