import { db } from "./firebase.js";
import { doc, getDoc, getDocs, collection, query, where, writeBatch, serverTimestamp }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const idInv = new URLSearchParams(location.search).get("i");
const $ = (id) => document.getElementById(id);
const https = (u) => typeof u === "string" && /^https:\/\//i.test(u);

const css = document.createElement("link");
css.rel = "stylesheet";
css.href = "css/regalos.css";
document.head.appendChild(css);

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
    if (!s.exists() || s.data().active !== true) return;
    const d = s.data();
    const alias = String(d.giftAlias || "").trim();
    const pay = https(d.giftPayUrl) ? d.giftPayUrl : "";
    const lista = (Array.isArray(d.giftList) ? d.giftList : [])
      .filter((g) => g && typeof g.name === "string" && g.name.trim()).slice(0, 20);
    if (!alias && !pay && !lista.length) return;
    cuandoListo(() => construir(d, alias, pay, lista));
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

function construir(d, alias, pay, lista) {
  let s = $("sec-regalo");
  if (!s) {
    s = el("section");
    s.id = "sec-regalo";
    s.appendChild(el("h2", null, "Regalo"));
    const r = $("form-rsvp");
    const ref = r ? r.closest("section") : null;
    if (!ref) return;
    ref.insertAdjacentElement("beforebegin", s);
  }

  if (alias) {
    const b = el("div", "regalo-bloque regalo-alias");
    b.appendChild(el("p", "regalo-etiqueta", "Alias"));
    b.appendChild(el("p", "regalo-valor", alias));
    if (d.giftHolder) b.appendChild(el("p", "regalo-titular", "Titular: " + d.giftHolder));
    if (navigator.clipboard) {
      const c = el("button", "boton", "Copiar alias");
      c.type = "button";
      c.addEventListener("click", () => {
        navigator.clipboard.writeText(alias).then(() => { c.textContent = "Alias copiado ✓"; });
      });
      b.appendChild(c);
    }
    s.appendChild(b);
  }

  if (pay) {
    const b = el("div", "regalo-bloque");
    const a = el("a", "boton", d.giftPayLabel || "Enviar regalo");
    a.href = pay;
    a.target = "_blank";
    a.rel = "noopener noreferrer";
    b.appendChild(a);
    s.appendChild(b);
  }

  if (lista.length) {
    const b = el("div", "regalo-bloque");
    const boton = el("button", "boton", "Ver lista de regalos");
    boton.type = "button";
    boton.addEventListener("click", () => abrirLista(lista));
    b.appendChild(boton);
    s.appendChild(b);
  }
}

/* Reservas hechas desde este celular */
function misReservas() {
  try { return JSON.parse(localStorage.getItem("ke-reservas-" + idInv) || "[]"); } catch (e) { return []; }
}
function guardarMia(id) {
  try {
    const m = misReservas();
    m.push(id);
    localStorage.setItem("ke-reservas-" + idInv, JSON.stringify(m));
  } catch (e) {}
}

async function abrirLista(lista) {
  let reservados = new Set();

  const fondo = el("div", "modal-regalos");
  const hoja = el("div", "hoja");
  const cab = el("div", "hoja-cab");
  const x = el("button", "cerrar", "×");
  x.type = "button";
  x.setAttribute("aria-label", "Cerrar");
  cab.append(el("h3", null, "Lista de regalos"), x);
  const ul = el("ul", "regalo-lista");
  const aviso = el("p", "hoja-aviso");
  hoja.append(cab, el("p", "hoja-ayuda", "Reservá un regalo para que no se repita."), ul, aviso);
  fondo.appendChild(hoja);
  document.body.appendChild(fondo);
  document.body.style.overflow = "hidden";

  const cerrar = () => { fondo.remove(); document.body.style.overflow = ""; };
  x.addEventListener("click", cerrar);
  fondo.addEventListener("click", (e) => { if (e.target === fondo) cerrar(); });

  async function cargarReservas() {
    try {
      const s = await getDocs(query(collection(db, "giftClaims"), where("invitationId", "==", idInv)));
      reservados = new Set(s.docs.map((d) => d.data().itemId));
    } catch (e) { console.error(e); }
  }

  function formulario(li, g) {
    if (li.querySelector(".reserva-form")) return;
    const f = el("div", "reserva-form");
    const input = document.createElement("input");
    input.type = "text";
    input.placeholder = "Tu nombre";
    input.maxLength = 60;
    const rsvp = $("r-nombre");
    if (rsvp && rsvp.value) input.value = rsvp.value;
    const ok = el("button", null, "Confirmar");
    ok.type = "button";
    const no = el("button", "sec", "Cancelar");
    no.type = "button";
    no.addEventListener("click", () => f.remove());
    ok.addEventListener("click", async () => {
      const nombre = input.value.trim();
      if (!nombre) { aviso.textContent = "Escribí tu nombre para reservar."; return; }
      ok.disabled = true;
      try {
        const cid = idInv + "_" + g.id;
        const batch = writeBatch(db);
        batch.set(doc(db, "giftClaims", cid), {
          invitationId: idInv, itemId: g.id, createdAt: serverTimestamp()
        });
        batch.set(doc(db, "giftClaimNames", cid), {
          invitationId: idInv, itemId: g.id, name: nombre, createdAt: serverTimestamp()
        });
        await batch.commit();
        reservados.add(g.id);
        guardarMia(g.id);
        aviso.textContent = "¡Gracias! Reservaste: " + g.name;
        pintar();
      } catch (e) {
        console.error(e);
        await cargarReservas();
        aviso.textContent = reservados.has(g.id)
          ? "Ese regalo ya lo reservó otra persona."
          : "No se pudo reservar. Intentá de nuevo.";
        pintar();
      }
    });
    f.append(input, ok, no);
    li.appendChild(f);
    input.focus();
  }

  function pintar() {
    const mias = misReservas();
    ul.textContent = "";
    lista.forEach((g) => {
      const li = el("li");
      li.appendChild(el("span", "nombre", g.name));
      const acc = el("div", "acciones");
      if (https(g.url)) {
        const a = el("a", null, "Ver");
        a.href = g.url;
        a.target = "_blank";
        a.rel = "noopener noreferrer";
        acc.appendChild(a);
      }
      if (g.id) {
        if (mias.includes(g.id)) {
          acc.appendChild(el("span", "reservado", "Reservado por vos ✓"));
        } else if (reservados.has(g.id)) {
          li.classList.add("ocupado");
          acc.appendChild(el("span", "reservado", "Reservado"));
        } else {
          const r = el("button", "reservar", "Reservar");
          r.type = "button";
          r.addEventListener("click", () => formulario(li, g));
          acc.appendChild(r);
        }
      }
      li.appendChild(acc);
      ul.appendChild(li);
    });
  }

  ul.textContent = "Cargando...";
  await cargarReservas();
  pintar();
}

iniciar();
