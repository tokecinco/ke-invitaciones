import { db } from "./firebase.js";
import { doc, getDoc, getDocs, collection }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const $ = (id) => document.getElementById(id);
const $$ = (n) => "$" + Number(n || 0).toLocaleString("es-AR");
const CATEGORIAS = [
  ["cumpleanos", "Cumpleaños infantiles"], ["quince", "15 años"], ["boda", "Casamientos"],
  ["bautismo", "Bautismos"], ["babyshower", "Baby shower"], ["aniversario", "Aniversarios"],
  ["especial", "Eventos especiales"]
];
const base = location.origin + location.pathname.replace(/[^/]*$/, "");

const css = document.createElement("link");
css.rel = "stylesheet";
css.href = "css/disenos.css";
document.head.appendChild(css);

function el(tag, clase, texto) {
  const e = document.createElement(tag);
  if (clase) e.className = clase;
  if (texto) e.textContent = texto;
  return e;
}

function bordes(s) {
  const izq = s.scrollLeft > 4;
  const der = s.scrollLeft + s.clientWidth < s.scrollWidth - 4;
  s.classList.toggle("mas-izq", izq);
  s.classList.toggle("mas-der", der);
  return { izq, der };
}

async function iniciar() {
  const sec = $("modelos");
  if (!sec) return;
  const titulo = sec.querySelector("h2");
  if (titulo) titulo.textContent = "Nuestros Diseños";
  ["chips", "grid", "nota"].forEach((id) => { const e = $(id); if (e) e.style.display = "none"; });

  const cont = el("div", "dis");
  cont.appendChild(el("p", "dis-carga", "Cargando diseños..."));
  sec.appendChild(cont);

  let tpl = [], demos = {}, wa = "";
  const precios = {};
  try {
    const [tp, site, pq] = await Promise.all([
      getDocs(collection(db, "templates")),
      getDoc(doc(db, "settings", "site")),
      getDoc(doc(db, "settings", "packages"))
    ]);
    tpl = tp.docs.map((d) => ({ id: d.id, ...d.data() })).filter((m) => m.active !== false);
    if (site.exists()) {
      demos = site.data().demos || {};
      wa = String(site.data().whatsapp || "").replace(/\D/g, "");
    }
    if (pq.exists()) {
      ["basico", "premium"].forEach((k) => {
        const p = pq.data()[k];
        if (p && p.price >= 0) precios[k] = p.price;
      });
    }
  } catch (e) {
    console.error(e);
    cont.textContent = "No se pudieron cargar los diseños. Intentá de nuevo más tarde.";
    return;
  }

  cont.textContent = "";
  if (!tpl.length) { cont.textContent = "Pronto vamos a sumar diseños."; return; }
  tpl.sort((a, b) => String(a.name || "").localeCompare(String(b.name || ""), "es"));
  const cats = CATEGORIAS.filter(([k]) => tpl.some((m) => m.category === k));

  let filtro = "";
  let desplazado = false;

  const chips = el("div", "dis-chips");
  const wrap = el("div", "dis-wrap");
  const fila = el("div", "dis-fila");
  const izq = el("button", "dis-flecha dis-izq", "‹");
  izq.type = "button"; izq.hidden = true; izq.setAttribute("aria-label", "Ver anteriores");
  const der = el("button", "dis-flecha dis-der", "›");
  der.type = "button"; der.hidden = true; der.setAttribute("aria-label", "Ver más diseños");
  const pista = el("p", "dis-pista");
  pista.innerHTML = 'Deslizá para ver más <span>&#8594;</span>';
  pista.hidden = true;
  wrap.append(fila, izq, der);
  cont.append(chips, wrap, pista);

  function actualizar() {
    const b = bordes(fila);
    izq.hidden = !b.izq;
    der.hidden = !b.der;
    pista.hidden = desplazado || (!b.izq && !b.der);
    bordes(chips);
  }

  fila.addEventListener("scroll", () => {
    if (fila.scrollLeft > 20) desplazado = true;
    actualizar();
  }, { passive: true });
  chips.addEventListener("scroll", () => bordes(chips), { passive: true });
  window.addEventListener("resize", actualizar);
  izq.addEventListener("click", () => fila.scrollBy({ left: -fila.clientWidth * 0.8, behavior: "smooth" }));
  der.addEventListener("click", () => fila.scrollBy({ left: fila.clientWidth * 0.8, behavior: "smooth" }));

  function pintarChips() {
    chips.textContent = "";
    [["", "Todos"]].concat(cats).forEach(([k, t]) => {
      const b = el("button", k === filtro ? "on" : "", t);
      b.type = "button";
      b.addEventListener("click", () => {
        filtro = k;
        pintarChips();
        pintar();
        const on = chips.querySelector(".on");
        if (on) on.scrollIntoView({ inline: "center", block: "nearest", behavior: "smooth" });
      });
      chips.appendChild(b);
    });
    requestAnimationFrame(() => bordes(chips));
  }

  function tarjeta(m) {
    const c = el("div", "dis-card");
    const col = m.colors || {};
    const p = el("div", "dis-portada");
    if (m.sampleImage && /^data:image\//.test(m.sampleImage)) {
      p.style.backgroundImage = 'url("' + m.sampleImage + '")';
    } else {
      p.style.background = "linear-gradient(160deg, " + (col.suave || "#f6dde2") + ", " + (col.fondo || "#fffaf3") + ")";
      p.style.color = col.acento || "#c9a24b";
      if (typeof m.font === "string") p.style.fontFamily = m.font;
      p.textContent = m.name || "";
    }
    const cu = el("div", "dis-cuerpo");
    cu.appendChild(el("h3", "dis-nombre", m.name || ""));
    const cat = (CATEGORIAS.find((x) => x[0] === m.category) || [0, ""])[1];
    if (cat) cu.appendChild(el("span", "dis-cat", cat));

    const pr = el("div", "dis-precios");
    [["basico", "Básico"], ["premium", "Premium"]].forEach(([k, t]) => {
      if (k in precios) {
        const f = el("div");
        f.append(el("span", null, t), el("b", null, $$(precios[k])));
        pr.appendChild(f);
      }
    });
    if (pr.children.length) cu.appendChild(pr);

    let ver = null;
    if (demos[m.id]) {
      ver = el("a", "dis-ver", "Ver modelo");
      ver.href = base + "i/" + demos[m.id];
    } else if (wa) {
      ver = el("a", "dis-ver", "Ver modelo");
      ver.href = "https://wa.me/" + wa + "?text=" +
        encodeURIComponent('Hola! Quiero ver el modelo "' + (m.name || "") + '".');
    }
    if (ver) { ver.target = "_blank"; ver.rel = "noopener"; cu.appendChild(ver); }

    if (wa) {
      const q = el("a", "dis-quiero", "Elegir este diseño");
      q.href = "https://wa.me/" + wa + "?text=" +
        encodeURIComponent('Hola! Me interesa la invitación digital modelo "' + (m.name || "") + '".');
      q.target = "_blank"; q.rel = "noopener";
      cu.appendChild(q);
    }
    c.append(p, cu);
    return c;
  }

  function pintar() {
    fila.textContent = "";
    tpl.filter((m) => !filtro || m.category === filtro).forEach((m) => fila.appendChild(tarjeta(m)));
    fila.scrollLeft = 0;
    requestAnimationFrame(actualizar);
  }

  pintarChips();
  pintar();
  setTimeout(actualizar, 400);
}

iniciar();
