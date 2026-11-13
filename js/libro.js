import { db } from "./firebase.js";
import { doc, getDoc, getDocs, addDoc, collection, query, where, serverTimestamp }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const idInv = new URLSearchParams(location.search).get("i");
const $ = (id) => document.getElementById(id);
const VISIBLES = 6;
const MAX_DISPOSITIVO = 5;

const css = document.createElement("link");
css.rel = "stylesheet";
css.href = "css/libro.css";
document.head.appendChild(css);

function el(tag, clase, texto) {
  const e = document.createElement(tag);
  if (clase) e.className = clase;
  if (texto) e.textContent = texto;
  return e;
}

function enviados() {
  try { return parseInt(localStorage.getItem("ke-libro-" + idInv), 10) || 0; } catch (e) { return 0; }
}
function sumar() {
  try { localStorage.setItem("ke-libro-" + idInv, String(enviados() + 1)); } catch (e) {}
}

async function iniciar() {
  if (!idInv) return;
  try {
    const inv = await getDoc(doc(db, "invitations", idInv));
    if (!inv.exists() || inv.data().active !== true) return;
    const abierto = inv.data().guestbookOpen === true;

    let msgs = [];
    try {
      const s = await getDocs(query(collection(db, "wishes"),
        where("invitationId", "==", idInv), where("approved", "==", true)));
      msgs = s.docs.map((d) => d.data())
        .sort((a, b) => (b.createdAt?.seconds || 0) - (a.createdAt?.seconds || 0)).slice(0, 60);
    } catch (e) { console.error(e); }

    if (!abierto && !msgs.length) return;
    cuandoListo(() => construir(abierto, msgs));
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

function tarjeta(m) {
  const c = el("div", "libro-card");
  c.appendChild(el("p", "libro-texto", m.message));
  c.appendChild(el("p", "libro-nombre", "- " + m.name));
  return c;
}

function construir(abierto, msgs) {
  const s = el("section");
  s.id = "sec-libro";
  const ic = el("div", "icono");
  ic.innerHTML = '<svg viewBox="0 0 24 24" aria-hidden="true">' +
    '<path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>';
  s.append(ic, el("h2", null, "Libro de mensajes"));

  if (abierto) {
    s.appendChild(el("p", null, "Dejá unas palabras para los anfitriones."));
    const f = el("form", "libro-form");
    f.noValidate = true;
    const nombre = document.createElement("input");
    nombre.type = "text"; nombre.placeholder = "Tu nombre"; nombre.maxLength = 60;
    const rsvp = $("r-nombre");
    if (rsvp && rsvp.value) nombre.value = rsvp.value;
    const texto = document.createElement("textarea");
    texto.placeholder = "Tu mensaje o dedicatoria"; texto.maxLength = 400;
    const b = el("button", "boton", "Enviar dedicatoria");
    b.type = "submit";
    const estado = el("p", "libro-estado");
    f.append(nombre, texto, b, estado);
    f.addEventListener("submit", async (e) => {
      e.preventDefault();
      const n = nombre.value.trim(), m = texto.value.trim();
      if (!n) { estado.textContent = "Escribí tu nombre."; return; }
      if (!m) { estado.textContent = "Escribí tu mensaje."; return; }
      if (enviados() >= MAX_DISPOSITIVO) { estado.textContent = "Ya enviaste varios mensajes desde este celular. ¡Gracias!"; return; }
      b.disabled = true;
      estado.textContent = "Enviando...";
      try {
        await addDoc(collection(db, "wishes"), {
          invitationId: idInv, name: n, message: m, approved: false, createdAt: serverTimestamp()
        });
        sumar();
        texto.value = "";
        estado.textContent = "¡Gracias! Tu mensaje se publicará cuando los anfitriones lo aprueben.";
      } catch (err) {
        console.error(err);
        estado.textContent = "No se pudo enviar. Intentá de nuevo.";
      }
      b.disabled = false;
    });
    s.appendChild(f);
  }

  if (msgs.length) {
    const lista = el("div", "libro-lista");
    msgs.slice(0, VISIBLES).forEach((m) => lista.appendChild(tarjeta(m)));
    s.appendChild(lista);
    if (msgs.length > VISIBLES) {
      const mas = el("button", "boton libro-mas", "Ver todos los mensajes (" + msgs.length + ")");
      mas.type = "button";
      mas.addEventListener("click", () => {
        msgs.slice(VISIBLES).forEach((m) => lista.appendChild(tarjeta(m)));
        mas.remove();
      });
      s.appendChild(mas);
    }
  }

  const cierre = $("btn-compartir");
  const ref = cierre ? cierre.closest("section") : null;
  if (ref) {
    ref.insertAdjacentElement("beforebegin", s);
  } else {
    const r = $("form-rsvp");
    const rs = r ? r.closest("section") : null;
    if (rs) rs.insertAdjacentElement("afterend", s);
  }
}

iniciar();
