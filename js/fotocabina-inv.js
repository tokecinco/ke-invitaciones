import { db } from "./firebase.js";
import { doc, getDoc, getDocs, collection, query, where }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const idInv = new URLSearchParams(location.search).get("i");
const $ = (id) => document.getElementById(id);
const imgOk = (s) => typeof s === "string" && (/^data:image\//i.test(s) || /^https:\/\//i.test(s));

function el(tag, clase, texto) {
  const e = document.createElement(tag);
  if (clase) e.className = clase;
  if (texto) e.textContent = texto;
  return e;
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

async function iniciar() {
  if (!idInv) return;
  try {
    const inv = await getDoc(doc(db, "invitations", idInv));
    if (!inv.exists()) return;
    const d = inv.data();
    if (d.active !== true || d.package !== "premium" || d.photobooth === false) return;

    let fotos = [];
    try {
      const s = await getDocs(query(collection(db, "photobooth"), where("invitationId", "==", idInv)));
      fotos = s.docs.map((x) => x.data())
        .filter((x) => x && imgOk(x.photo))
        .sort((a, b) => (b.createdAt?.seconds || 0) - (a.createdAt?.seconds || 0));
    } catch (e) { console.error(e); }

    if (d.photoboothOpen === false && !fotos.length) return;
    cuandoListo(() => construir(d, fotos));
  } catch (e) { console.error(e); }
}

function construir(d, fotos) {
  const r = $("form-rsvp");
  const ref = r ? r.closest("section") : null;
  if (!ref || $("sec-fotocabina")) return;

  const st = document.createElement("style");
  st.textContent =
    ".fc-inv-tira{display:flex;justify-content:center;gap:10px;flex-wrap:wrap;margin:14px 0 8px}" +
    ".fc-inv-mini{width:68px;background:#fff;padding:4px 4px 12px;border-radius:3px;box-shadow:0 5px 14px rgba(0,0,0,.16)}" +
    ".fc-inv-mini:nth-child(odd){transform:rotate(-3deg)}.fc-inv-mini:nth-child(even){transform:rotate(3deg)}" +
    ".fc-inv-mini img{width:100%;aspect-ratio:1/1;object-fit:cover;display:block}";
  document.head.appendChild(st);

  const s = el("section");
  s.id = "sec-fotocabina";

  const ic = el("div", "icono");
  ic.innerHTML = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">' +
    '<path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"/><circle cx="12" cy="13" r="3"/></svg>';
  s.append(ic, el("h2", null, "Fotocabina Virtual"));
  s.appendChild(el("p", null, d.photoboothOpen === false
    ? "Mirá las fotos Polaroid y recuerdos que dejaron los invitados."
    : "Sacate una foto estilo Polaroid y dejá un mensaje en el muro del evento."));

  if (fotos.length) {
    const tira = el("div", "fc-inv-tira");
    fotos.slice(0, 4).forEach((f) => {
      const m = el("div", "fc-inv-mini");
      const im = document.createElement("img");
      im.src = f.photo; im.alt = f.name; im.loading = "lazy";
      m.appendChild(im);
      tira.appendChild(m);
    });
    s.appendChild(tira);
  }

  const base = location.origin + location.pathname.replace(/[^/]*$/, "");
  const a = el("a", "boton", fotos.length ? "Ver muro / Subir foto (" + fotos.length + ")" : "Abrir Fotocabina");
  a.href = base + "fotocabina.html?i=" + encodeURIComponent(idInv);
  s.appendChild(a);

  ref.insertAdjacentElement("beforebegin", s);
}

iniciar();
