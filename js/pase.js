import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const params = new URLSearchParams(location.search);
const idInv = params.get("i");
const idInvitado = params.get("g");
const $ = (id) => document.getElementById(id);

function el(tag, clase, texto) {
  const e = document.createElement(tag);
  if (clase) e.className = clase;
  if (texto) e.textContent = texto;
  return e;
}

if (idInv && idInvitado) iniciar();

async function iniciar() {
  try {
    const [inv, gs, pq] = await Promise.all([
      getDoc(doc(db, "invitations", idInv)),
      getDoc(doc(db, "guests", idInvitado)),
      getDoc(doc(db, "settings", "packages"))
    ]);
    if (!inv.exists() || !gs.exists()) return;
    const d = inv.data();
    const g = gs.data();
    if (d.active !== true || g.invitationId !== idInv || d.pass === false) return;

    // El pase está incluido en el Premium. En el Básico, solo si el paquete lo permite.
    const clave = d.package === "premium" ? "premium" : "basico";
    let permitido = clave === "premium";
    if (!permitido && pq.exists() && pq.data()[clave]) permitido = !!pq.data()[clave].personalized;
    if (!permitido) return;

    cuandoListo(() => construir(d, g));
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

function cargarQR() {
  return new Promise((ok, mal) => {
    if (window.qrcode) return ok();
    const s = document.createElement("script");
    s.src = "https://cdnjs.cloudflare.com/ajax/libs/qrcode-generator/1.4.4/qrcode.min.js";
    s.onload = ok;
    s.onerror = () => mal(new Error("No se pudo cargar el generador de QR"));
    document.head.appendChild(s);
  });
}

function dibujarQR(texto) {
  const qr = window.qrcode(0, "M");
  qr.addData(texto);
  qr.make();
  const n = qr.getModuleCount();
  const celda = 10, margen = 4;
  const lado = (n + margen * 2) * celda;
  const c = document.createElement("canvas");
  c.width = lado; c.height = lado;
  const g = c.getContext("2d");
  g.fillStyle = "#ffffff";
  g.fillRect(0, 0, lado, lado);
  g.fillStyle = "#000000";
  for (let f = 0; f < n; f++) {
    for (let k = 0; k < n; k++) {
      if (qr.isDark(f, k)) g.fillRect((k + margen) * celda, (f + margen) * celda, celda, celda);
    }
  }
  return c.toDataURL("image/png");
}

function ampliar(src, nombre) {
  const f = el("div", "pase-full");
  const img = document.createElement("img");
  img.src = src;
  img.alt = "Código QR de acceso";
  f.append(img, el("p", null, nombre), el("small", null, "Tocá para cerrar"));
  f.addEventListener("click", () => f.remove());
  document.body.appendChild(f);
}

async function construir(d, g) {
  const base = location.origin + location.pathname.replace(/[^/]*$/, "");
  const url = base + "acceso.html?i=" + encodeURIComponent(idInv) + "&g=" + encodeURIComponent(idInvitado);

  let src;
  try {
    await cargarQR();
    src = dibujarQR(url);
  } catch (e) { console.error(e); return; }

  const css = document.createElement("link");
  css.rel = "stylesheet";
  css.href = "css/pase.css";
  document.head.appendChild(css);

  const s = el("section");
  s.id = "sec-pase";

  const ic = el("div", "icono");
  ic.innerHTML = '<svg viewBox="0 0 24 24" aria-hidden="true">' +
    '<path d="M2 9a3 3 0 0 1 0 6v2a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2a3 3 0 0 1 0-6V7a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2Z"/>' +
    '<path d="M13 5v2"/><path d="M13 17v2"/><path d="M13 11v2"/></svg>';
  s.append(ic, el("h2", null, "Tu pase digital"));

  const pase = el("div", "pase");
  const titulo = [d.honoree, d.eventType].filter(Boolean).join(" - ");
  pase.appendChild(el("p", "pase-evento", titulo || "Pase de acceso"));
  pase.appendChild(el("p", "pase-nombre", g.name));
  pase.appendChild(el("p", "pase-lugares", g.seats + (g.seats === 1 ? " lugar reservado" : " lugares reservados")));

  const fecha = d.date ? new Date(d.date + "T" + (d.time || "00:00") + ":00") : null;
  if (fecha && !isNaN(fecha)) {
    pase.appendChild(el("p", "pase-fecha",
      fecha.toLocaleDateString("es-AR", { day: "numeric", month: "long", year: "numeric" }) +
      (d.time ? " - " + d.time + " hs" : "")));
  }
  pase.appendChild(el("div", "pase-corte"));
  const img = document.createElement("img");
  img.className = "pase-qr";
  img.src = src;
  img.alt = "Código QR de acceso";
  pase.appendChild(img);
  pase.appendChild(el("p", "pase-ayuda", "Mostrá este código en la entrada"));
  s.appendChild(pase);

  const botones = el("div", "pase-botones");
  const bAmpliar = el("button", "boton", "Ampliar QR");
  bAmpliar.type = "button";
  bAmpliar.addEventListener("click", () => ampliar(src, g.name));
  const bGuardar = el("a", "boton", "Guardar QR");
  bGuardar.href = src;
  bGuardar.download = "pase-" + idInvitado + ".png";
  botones.append(bAmpliar, bGuardar);
  s.appendChild(botones);

  const rsvp = $("form-rsvp");
  const ref = rsvp ? rsvp.closest("section") : null;
  if (ref) ref.insertAdjacentElement("afterend", s);
}
