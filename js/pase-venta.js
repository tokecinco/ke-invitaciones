import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const $$ = (n) => "$" + Number(n || 0).toLocaleString("es-AR");

function el(tag, clase, texto) {
  const e = document.createElement(tag);
  if (clase) e.className = clase;
  if (texto) e.textContent = texto;
  return e;
}

async function iniciar() {
  try {
    const [pq, site] = await Promise.all([
      getDoc(doc(db, "settings", "pase")),
      getDoc(doc(db, "settings", "site"))
    ]);
    if (!pq.exists()) return;
    const x = pq.data();
    if (x.visible === false || !(x.price >= 0)) return;
    const ref = document.getElementById("precios");
    if (!ref) return;
    const wa = String((site.exists() ? site.data().whatsapp : "") || "").replace(/\D/g, "");

    const estilo = document.createElement("style");
    estilo.textContent =
      ".pase-venta{max-width:420px;margin:0 auto;background:#1a1a1a;color:#fff;border-radius:18px;padding:26px 22px;text-align:center}" +
      ".pase-venta h3{margin:0;font-weight:normal;color:#c9a24b;font-size:1.3rem}" +
      ".pase-venta .monto{font-size:2.2rem;color:#c9a24b;margin:8px 0 2px}" +
      ".pase-venta .sub{margin:0 0 6px;font-size:.95rem;color:#ddd}" +
      ".pase-venta ul{list-style:none;padding:0;margin:16px 0;text-align:left}" +
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

    const c = el("div", "pase-venta");
    c.appendChild(el("h3", null, "Pase QR"));
    c.appendChild(el("div", "monto", $$(x.price)));
    c.appendChild(el("p", "sub", "por evento" + (x.deposit ? " - seña de " + $$(x.deposit) : "")));
    const ul = el("ul");
    [
      "Pase digital con código QR para cada invitado",
      "Control de ingreso desde el celular",
      "Aviso si un pase ya fue utilizado",
      "Resumen de ingresos en tiempo real"
    ].forEach((t) => ul.appendChild(el("li", null, t)));
    if (x.included > 0) ul.appendChild(el("li", null, "Incluye hasta " + x.included + " pases"));
    if (x.extra > 0) ul.appendChild(el("li", null, $$(x.extra) + " por cada pase adicional"));
    c.appendChild(ul);
    if (x.description) c.appendChild(el("p", "desc", x.description));
    if (wa) {
      const a = el("a", "btn", "Pedir pase QR");
      a.href = "https://wa.me/" + wa + "?text=" +
        encodeURIComponent("Hola! Quiero contratar el pase QR para mi evento.");
      a.target = "_blank";
      a.rel = "noopener";
      c.appendChild(a);
    }
    s.appendChild(c);
    ref.insertAdjacentElement("afterend", s);
  } catch (e) { console.error(e); }
}

iniciar();
