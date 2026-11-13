import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

async function iniciar() {
  const ref = document.getElementById("precios");
  if (!ref) return;
  const viejo = document.getElementById("pase-qr");
  if (viejo) viejo.remove();

  let wa = "";
  try {
    const s = await getDoc(doc(db, "settings", "site"));
    if (s.exists()) wa = String(s.data().whatsapp || "").replace(/\D/g, "");
  } catch (e) { console.error(e); }

  const estilo = document.createElement("style");
  estilo.textContent =
    ".banner-qr{max-width:720px;margin:0 auto;background:linear-gradient(135deg,#1a1a1a,#2b221b);" +
    "border:1px solid #c9a24b;border-radius:18px;padding:30px 22px;text-align:center;color:#fff}" +
    ".banner-qr svg{width:52px;height:52px;fill:none;stroke:#c9a24b;stroke-width:1.6;" +
    "stroke-linecap:round;stroke-linejoin:round}" +
    ".banner-qr h2{color:#fff;font-size:1.6rem;line-height:1.25;margin:12px 0 18px}";
  document.head.appendChild(estilo);

  const s = document.createElement("section");
  s.id = "pase-qr";
  const b = document.createElement("div");
  b.className = "banner-qr";
  b.innerHTML =
    '<svg viewBox="0 0 24 24" aria-hidden="true">' +
    '<rect width="5" height="5" x="3" y="3" rx="1"/><rect width="5" height="5" x="16" y="3" rx="1"/>' +
    '<rect width="5" height="5" x="3" y="16" rx="1"/><path d="M21 16h-3a2 2 0 0 0-2 2v3"/>' +
    '<path d="M21 21v.01"/><path d="M12 7v3a2 2 0 0 1-2 2H7"/><path d="M3 12h.01"/><path d="M12 3h.01"/>' +
    '<path d="M12 16v.01"/><path d="M16 12h1"/><path d="M21 12v.01"/><path d="M12 21v-1"/></svg>';
  const h = document.createElement("h2");
  h.textContent = "Entradas y control QR para eventos";
  b.appendChild(h);

  if (wa) {
    const a = document.createElement("a");
    a.className = "btn";
    a.textContent = "Consultar";
    a.href = "https://wa.me/" + wa + "?text=" +
      encodeURIComponent("Hola! Quiero consultar por entradas y control QR para eventos.");
    a.target = "_blank";
    a.rel = "noopener";
    b.appendChild(a);
  }
  s.appendChild(b);
  ref.insertAdjacentElement("afterend", s);
}

iniciar();
