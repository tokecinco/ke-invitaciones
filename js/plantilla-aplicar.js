import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const FUENTES = [
  "Georgia, serif",
  "'Palatino Linotype', Palatino, serif",
  "'Times New Roman', serif",
  "'Trebuchet MS', sans-serif",
  "Verdana, sans-serif"
];

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

const esColor = (c) => typeof c === "string" && /^#[0-9a-f]{6}$/i.test(c);
const idInvitacion = new URLSearchParams(location.search).get("i");

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
    if (FUENTES.includes(x.font)) estilo.setProperty("--fuente", x.font);

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
