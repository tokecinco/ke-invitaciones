import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const idInv = new URLSearchParams(location.search).get("i");
const $ = (id) => document.getElementById(id);

function el(tag, clase, texto) {
  const e = document.createElement(tag);
  if (clase) e.className = clase;
  if (texto) e.textContent = texto;
  return e;
}

async function iniciar() {
  if (!idInv) return;
  try {
    const s = await getDoc(doc(db, "invitations", idInv));
    if (!s.exists() || s.data().active !== true || s.data().albumOpen !== true) return;
    cuandoListo(construir);
  } catch (e) { console.error(e); }
}

function cuandoListo(fn) {
  let n = 0;
  const t = setInterval(() => {
    n++;
    const c = $("cargando");
    const listo = c && c.hidden;
    if (listo || n > 80) {
      clearInterval(t);
      if (listo && $("error").hidden) fn();
    }
  }, 150);
}

function construir() {
  const btn = $("btn-compartir");
  const ref = btn ? btn.closest("section") : null;
  if (!ref) return;

  const s = el("section");
  s.id = "sec-album";
  const ic = el("div", "icono");
  ic.innerHTML = '<svg viewBox="0 0 24 24" aria-hidden="true">' +
    '<path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"/>' +
    '<circle cx="12" cy="13" r="3"/></svg>';
  s.append(ic, el("h2", null, "Álbum colaborativo"),
    el("p", null, "Subí tus fotos del evento al álbum privado de los anfitriones."));
  const a = el("a", "boton", "Subir mis fotos");
  a.href = "album.html?i=" + encodeURIComponent(idInv);
  s.appendChild(a);
  ref.insertAdjacentElement("beforebegin", s);
}

iniciar();
