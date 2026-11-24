import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";
import { buscarFuente, cargarFuente } from "./fuentes.js";
import { PATRONES, MARCOS, DECOS } from "./opciones-plantillas.js?v=2";

["css/efectos.css", "css/efectos-2.css", "css/efectos-3.css", "css/tamanos.css"].forEach((href) => {
  const l = document.createElement("link");
  l.rel = "stylesheet";
  l.href = href;
  document.head.appendChild(l);
});

const SECCIONES = {
  portada: "portada",
  mensaje: "mensaje",
  cuenta: "cuenta",
  lugar: "fecha-hora",
  info: "sec-info",
  galeria: "sec-galeria",
  rsvp: "form-rsvp",
  compartir: "btn-compartir"
};

// Identificadores válidos (sin la opción vacía "Ninguno")
const VALIDOS = (lista) => lista.map((o) => o[0]).filter(Boolean);
const PATRONES_OK = VALIDOS(PATRONES);
const MARCOS_OK = VALIDOS(MARCOS);
const DECOS_OK = VALIDOS(DECOS);
const EFECTOS = { titulos: "ef-titulos", botones: "ef-botones", cuenta: "ef-cuenta" };
const TAMANOS = ["titulo", "subtitulo", "info", "boton"];

const esColor = (c) => typeof c === "string" && /^#[0-9a-f]{6}$/i.test(c);
const idInvitacion = new URLSearchParams(location.search).get("i");

function esOscuro(hex) {
  if (!esColor(hex)) return false;
  const n = parseInt(hex.slice(1), 16);
  const r = (n >> 16) & 255, g = (n >> 8) & 255, b = n & 255;
  return (0.299 * r + 0.587 * g + 0.114 * b) < 140;
}

// Aplica una tipografía. Si la fuente tiene "peso" (por ejemplo negrita), también lo aplica.
function fuente(estilo, variable, valor, rol) {
  const f = buscarFuente(valor);
  if (!f) return;
  estilo.setProperty(variable, f.css);
  cargarFuente(f);
  if (rol && f.peso) {
    estilo.setProperty("--p-" + rol, String(f.peso));
    document.body.classList.add("peso-" + rol);
  }
}

async function aplicar() {
  if (!idInvitacion) return;
  try {
    const inv = await getDoc(doc(db, "invitations", idInvitacion));
    if (!inv.exists()) return;
    const plantillaId = inv.data().template;
    if (!plantillaId) return;

    const t = await getDoc(doc(db, "templates", plantillaId));
    if (!t.exists()) return;
    const x = t.data();

    const c = x.colors || {};
    const estilo = document.body.style;
    if (esColor(c.fondo)) {
      estilo.setProperty("--fondo", c.fondo);
      if (esOscuro(c.fondo)) document.body.classList.add("tema-oscuro");
    }
    if (esColor(c.texto)) estilo.setProperty("--texto", c.texto);
    if (esColor(c.acento)) estilo.setProperty("--acento", c.acento);
    if (esColor(c.suave)) estilo.setProperty("--suave", c.suave);

    // Tipografías: general, y una para títulos, textos y botones (si la plantilla no las define, usa la general)
    const f = x.fonts || {};
    fuente(estilo, "--fuente", x.font, null);
    fuente(estilo, "--f-titulo", f.titulo || x.font, "titulo");
    fuente(estilo, "--f-texto", f.texto || x.font, "texto");
    fuente(estilo, "--f-boton", f.boton || x.font, "boton");

    // Tamaños de letra (en porcentaje; 100 es el tamaño normal)
    const tam = x.tamanos || {};
    TAMANOS.forEach((k) => {
      const n = Number(tam[k]);
      if (Number.isFinite(n) && n >= 50 && n <= 200 && n !== 100) {
        estilo.setProperty("--z-" + k, String(n / 100));
        document.body.classList.add("z-" + k);
      }
    });

    // Detalle de fondo, marco, decoración y efectos
    if (PATRONES_OK.includes(x.patron)) document.body.classList.add("patron-" + x.patron);
    if (MARCOS_OK.includes(x.marco)) document.body.classList.add("marco-" + x.marco);
    if (DECOS_OK.includes(x.deco)) document.body.classList.add("deco-" + x.deco);
    if (Array.isArray(x.efectos)) {
      x.efectos.forEach((e) => {
        if (EFECTOS[e]) document.body.classList.add(EFECTOS[e]);
      });
    }

    if (Array.isArray(x.sections)) {
      Object.keys(SECCIONES).forEach((clave) => {
        if (clave === "portada") return;
        const el = document.getElementById(SECCIONES[clave]);
        const seccion = el ? el.closest("section") : null;
        if (seccion) seccion.style.display = x.sections.includes(clave) ? "" : "none";
      });
    }
  } catch (e) {
    console.error(e);
  }
}

aplicar();
