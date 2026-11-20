import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";
import { buscarFuente, cargarFuente } from "./fuentes.js";

const css = document.createElement("link");
css.rel = "stylesheet";
css.href = "css/efectos.css";
document.head.appendChild(css);

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

const PATRONES = ["puntos", "rayas", "cuadricula", "degrade"];
const MARCOS = ["fino", "doble", "esquinas"];
// Las decoraciones corazones, estrellas, nubes y burbujas las agrega extras.js
const DECOS_NUEVAS = ["flores", "nieve", "destellos", "notas"];
const EFECTOS = { titulos: "ef-titulos", botones: "ef-botones", cuenta: "ef-cuenta" };

const esColor = (c) => typeof c === "string" && /^#[0-9a-f]{6}$/i.test(c);
const idInvitacion = new URLSearchParams(location.search).get("i");

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
    if (esColor(c.fondo)) estilo.setProperty("--fondo", c.fondo);
    if (esColor(c.texto)) estilo.setProperty("--texto", c.texto);
    if (esColor(c.acento)) estilo.setProperty("--acento", c.acento);
    if (esColor(c.suave)) estilo.setProperty("--suave", c.suave);

    // Tipografías: general, y una para títulos, textos y botones (si la plantilla no las define, usa la general)
    const f = x.fonts || {};
    fuente(estilo, "--fuente", x.font, null);
    fuente(estilo, "--f-titulo", f.titulo || x.font, "titulo");
    fuente(estilo, "--f-texto", f.texto || x.font, "texto");
    fuente(estilo, "--f-boton", f.boton || x.font, "boton");

    // Detalle de fondo, marco, decoración y efectos
    if (PATRONES.includes(x.patron)) document.body.classList.add("patron-" + x.patron);
    if (MARCOS.includes(x.marco)) document.body.classList.add("marco-" + x.marco);
    if (DECOS_NUEVAS.includes(x.deco)) document.body.classList.add("deco-" + x.deco);
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
