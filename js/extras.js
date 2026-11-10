import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const idInvitacion = new URLSearchParams(location.search).get("i");

const css = document.createElement("link");
css.rel = "stylesheet";
css.href = "css/extras.css";
document.head.appendChild(css);

const ICONOS = {
  reloj: '<circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/>',
  pin: '<path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/>',
  info: '<circle cx="12" cy="12" r="10"/><path d="M12 16v-4"/><path d="M12 8h.01"/>',
  imagen: '<rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="9" cy="9" r="2"/><path d="m21 15-3.09-3.09a2 2 0 0 0-2.82 0L6 21"/>',
  camisa: '<path d="M20.38 3.46 16 2a4 4 0 0 1-8 0L3.62 3.46a2 2 0 0 0-1.34 2.23l.58 3.47a1 1 0 0 0 .99.84H6v10c0 1.1.9 2 2 2h8a2 2 0 0 0 2-2V10h2.15a1 1 0 0 0 .99-.84l.58-3.47a2 2 0 0 0-1.34-2.23z"/>',
  regalo: '<rect x="3" y="8" width="18" height="4" rx="1"/><path d="M12 8v13"/><path d="M19 12v7a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2v-7"/><path d="M7.5 8a2.5 2.5 0 0 1 0-5A4.8 8 0 0 1 12 8a4.8 8 0 0 1 4.5-5 2.5 2.5 0 0 1 0 5"/>',
  musica: '<path d="M9 18V5l12-2v13"/><circle cx="6" cy="18" r="3"/><circle cx="18" cy="16" r="3"/>',
  hash: '<line x1="4" x2="20" y1="9" y2="9"/><line x1="4" x2="20" y1="15" y2="15"/><line x1="10" x2="8" y1="3" y2="21"/><line x1="16" x2="14" y1="3" y2="21"/>',
  corazon: '<path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"/>'
};

function iconoDe(sec) {
  if (sec.id === "sec-info") return "info";
  if (sec.id === "sec-galeria") return "imagen";
  if (sec.id === "sec-dress") return "camisa";
  if (sec.id === "sec-regalo") return "regalo";
  if (sec.id === "sec-playlist") return "musica";
  if (sec.id === "sec-hashtag") return "hash";
  if (sec.querySelector("#cuenta")) return "reloj";
  if (sec.querySelector("#fecha-hora")) return "pin";
  if (sec.querySelector("#form-rsvp")) return "corazon";
  return null;
}

function decorar(sec) {
  if (!sec || sec.tagName !== "SECTION" || sec.classList.contains("portada")) return;
  if (sec.querySelector(":scope > .icono")) return;
  const h2 = sec.querySelector("h2");
  const clave = iconoDe(sec);
  if (!h2 || !clave) return;
  const d = document.createElement("div");
  d.className = "icono";
  d.innerHTML = '<svg viewBox="0 0 24 24" aria-hidden="true">' + ICONOS[clave] + "</svg>";
  h2.insertAdjacentElement("beforebegin", d);
}

const main = document.getElementById("invitacion");
if (main) {
  main.querySelectorAll("section").forEach(decorar);
  new MutationObserver((cambios) => {
    cambios.forEach((c) => c.addedNodes.forEach((n) => {
      if (n.nodeType === 1 && n.tagName === "SECTION") decorar(n);
    }));
  }).observe(main, { childList: true });
}

/* Visor de fotos: reinicia el efecto al cambiar de foto */
new MutationObserver((cambios, obs) => {
  const lb = document.getElementById("lightbox");
  if (!lb) return;
  const img = lb.querySelector("img");
  img.addEventListener("load", () => {
    img.classList.remove("cambia");
    void img.offsetWidth;
    img.classList.add("cambia");
  });
  obs.disconnect();
}).observe(document.body, { childList: true });

/* Decoración flotante según la plantilla (corazones, estrellas, nubes, burbujas) */
async function decoracion() {
  if (!idInvitacion) return;
  try {
    const inv = await getDoc(doc(db, "invitations", idInvitacion));
    if (!inv.exists() || !inv.data().template) return;
    const t = await getDoc(doc(db, "templates", inv.data().template));
    if (!t.exists()) return;
    const deco = t.data().deco;
    if (["corazones", "estrellas", "nubes", "burbujas"].includes(deco)) {
      document.body.classList.add("deco-" + deco);
    }
  } catch (e) { console.error(e); }
}
decoracion();
