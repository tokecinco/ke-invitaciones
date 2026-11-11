import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const $$ = (n) => "$" + Number(n || 0).toLocaleString("es-AR");

const BASE = [
  "Pase digital con código QR para cada invitado",
  "Control de ingreso desde el celular",
  "Aviso si un pase ya fue utilizado",
  "Resumen de ingresos en tiempo real"
];
const EXTRA_PREMIUM = [
  "Mesa asignada a cada invitado",
  "Tipo de entrada (General, VIP y más)",
  "Imagen y fotos del evento",
  "Plano del salón",
  "Itinerario e indicaciones del anfitrión",
  "Agregar al calendario"
];

function el(tag, clase, texto) {
  const e = document.createElement(tag);
  if (clase) e.className = clase;
  if (texto) e.textContent = texto;
  return e;
}

async function iniciar() {
  try {
    const [e, p, site] = await Promise.all([
      getDoc(doc(db, "settings", "pase")),
      getDoc(doc(db, "settings", "pasePremium")),
      getDoc(doc(db, "settings", "site"))
    ]);
    if (!e.exists()) return;
    const esencial = e.data();
    if (esencial.visible === false || !(esencial.price >= 0)) return;
    const premium = p.exists() && p.data().price >= 0 ? p.data() : null;
    const ref = document.getElementById("precios");
    if (!ref) return;
    const wa = String((site.exists() ? site.data().whatsapp : "") || "").replace(/\D/g, "");

    const estilo = document.createElement("style");
    estilo.textContent =
      ".pase-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(260px,1fr));gap:16px;max-width:720px;margin:0 auto}" +
      ".pase-venta{background:#1a1a1a;color:#fff;border-radius:18px;padding:26px 22px;text-align:center;border:2px solid transparent}" +
      ".pase-venta.prem{border-color:#c9a24b}" +
      ".pase-venta h3{margin:0;font-weight:normal;color:#c9a24b;font-size:1.3rem}" +
      ".pase-venta .monto{font-size:2.2rem;color:#c9a24b;margin:8px 0 2px}" +
      ".pase-venta .sub{margin:0 0 6px;font-size:.95rem;color:#ddd}" +
      ".pase-venta .mas{margin:14px 0 0;font-size:.85rem;color:#c9a24b;text-align:left}" +
      ".pase-venta ul{list-style:none;padding:0;margin:10px 0 16px;text-align:left}" +
      ".pase-venta li{padding:5px 0 5px 24px;position:relative}" +
      ".pase-venta li:before{content:'\\2713';position:absolute;left:0;color:#c9a24b}" +
      ".pase-venta .desc{font-size:.9rem;color:#bbb;margin:0 0 14px}" +
      ".pase-intro{text-align:center;max-width:560px;margin:0 auto 18px}";
    document.head.appendChild(estilo);

    const s = el("section");
    s.id = "pase-qr";
    s.appendChild(el("h2", null, "Pase QR para eventos"));
    s.appendChild(el("p", "pase-intro",
      "¿Solo necesitás controlar el ingreso a tu evento? Cada invitado recibe su pase digital con código QR y vos registrás los ingresos con la cámara del celular."));

    const grid = el("div", "pase-grid");

    function tarjeta(nombre, d, extra) {
      const c = el("div", "pase-venta" + (extra ? " prem" : ""));
      c.appendChild(el("h3", null, "Pase QR " + nombre));
      c.appendChild(el("div", "monto", $$(d.price)));
      c.appendChild(el("p", "sub", "por evento" + (d.deposit ? " - seña de " + $$(d.deposit) : "")));
      if (extra) c.appendChild(el("p", "mas", "Todo lo del Esencial, más:"));
      const ul = el("ul");
      (extra ? EXTRA_PREMIUM : BASE).forEach((t) => ul.appendChild(el("li", null, t)));
      if (d.included > 0) ul.appendChild(el("li", null, "Incluye hasta " + d.included + " pases"));
      if (d.extra > 0) ul.appendChild(el("li", null, $$(d.extra) + " por cada pase adicional"));
      c.appendChild(ul);
      if (d.description) c.appendChild(el("p", "desc", d.description));
      if (wa) {
        const a = el("a", "btn", "Pedir pase " + nombre);
        a.href = "https://wa.me/" + wa + "?text=" +
          encodeURIComponent("Hola! Quiero contratar el Pase QR " + nombre + " para mi evento.");
        a.target = "_blank";
        a.rel = "noopener";
        c.appendChild(a);
      }
      return c;
    }

    grid.appendChild(tarjeta("Esencial", esencial, false));
    if (premium) grid.appendChild(tarjeta("Premium", premium, true));
    s.appendChild(grid);

    const anterior = document.getElementById("pase-qr");
    if (anterior) anterior.remove();
    ref.insertAdjacentElement("afterend", s);
  } catch (err) { console.error(err); }
}

iniciar();
