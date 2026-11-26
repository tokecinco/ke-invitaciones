import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const $$ = (n) => "$" + Number(n || 0).toLocaleString("es-AR");
const TITULOS = { basico: "Esencial", premium: "Premium" };

function h(tag, texto, clase) {
  const e = document.createElement(tag);
  if (texto) e.textContent = texto;
  if (clase) e.className = clase;
  return e;
}

async function iniciar() {
  try {
    const [pq, site] = await Promise.all([
      getDoc(doc(db, "settings", "packages")),
      getDoc(doc(db, "settings", "site"))
    ]);
    if (!pq.exists()) return;
    const g = pq.data();
    const s = site.exists() ? site.data() : {};
    const wa = String(s.whatsapp || "").replace(/\D/g, "");

    const viejo = document.getElementById("precio");
    if (!viejo) return;

    const estilo = document.createElement("style");
    estilo.textContent =
      ".paquetes{display:grid;grid-template-columns:repeat(auto-fit,minmax(250px,1fr));gap:16px;max-width:900px;margin:0 auto}" +
      ".paq{background:#fff;border:1px solid #eadfc8;border-radius:16px;padding:22px;text-align:center}" +
      ".paq.premium{border:2px solid #c9a24b}" +
      ".paq .monto{font-size:2rem;color:#c9a24b;margin:6px 0}" +
      ".paq ul{list-style:none;padding:0;margin:14px 0;text-align:left}" +
      ".paq li{padding:4px 0 4px 22px;position:relative}" +
      ".paq li:before{content:'\\2713';position:absolute;left:0;color:#c9a24b}" +
      "#precios .paq li.destacado{font-weight:bold;font-size:1.01rem;color:#1a1a1a;padding-top:6px;padding-bottom:6px}" +
      ".paq .desc{font-size:.9rem;color:#666}";
    document.head.appendChild(estilo);

    const cont = h("div", "", "paquetes");
    ["basico", "premium"].forEach((k) => {
      const p = g[k];
      if (!p) return;
      const c = h("div", "", "paq" + (k === "premium" ? " premium" : ""));
      c.appendChild(h("h3", "Paquete " + TITULOS[k]));
      c.appendChild(h("div", $$(p.price), "monto"));
      c.appendChild(h("p", "Seña de " + $$(p.deposit) + " para reservar"));

      const galeria = Math.max((p.maxPhotos || 1) - 1, 0);
      const fotos = galeria > 0
        ? "Foto de portada + " + galeria + " foto" + (galeria === 1 ? "" : "s") + " en la galería"
        : "Foto de portada";

      const items = [
        { t: "Mensaje personalizado" },
        { t: "Cuenta regresiva, fecha y mapa del lugar" },
        { t: "Itinerario" },
        { t: "Confirmación de asistencia online" },
        { t: "Regalo" },
        { t: fotos }
      ];
      if (p.music || g.v3 !== true) items.push({ t: "Música de fondo" });
      items.push({ t: "Playlist de Spotify" });
      items.push({ t: "Hashtag de Instagram + tarjeta QR para la mesa" });
      items.push({ t: "Libro de mensajes" });
      if (k === "premium") {
        items.push({ t: "Fotocabina Virtual (Polaroid con mensajes + tarjeta QR para la mesa)", dest: true });
      }
      if (p.personalized) {
        items.push({ t: "Invitación personalizada por invitado", dest: true });
        items.push({ t: "Pase digital QR, control y organizador de invitados y mesas", dest: true });
      }

      const ul = h("ul");
      items.forEach((it) => ul.appendChild(h("li", it.t, it.dest ? "destacado" : "")));
      c.appendChild(ul);

      if (p.description) c.appendChild(h("p", p.description, "desc"));

      if (wa) {
        const a = h("a", "Elegir este paquete", "btn" + (k === "premium" ? "" : " neg"));
        a.href = "https://wa.me/" + wa + "?text=" +
          encodeURIComponent("Hola! Me interesa el paquete " + TITULOS[k] + " de invitaciones digitales.");
        a.target = "_blank"; a.rel = "noopener";
        c.appendChild(a);
      }
      cont.appendChild(c);
    });

    viejo.style.display = "none";
    viejo.insertAdjacentElement("afterend", cont);

    if (s.promo) {
      const promo = h("div", s.promo, "promo");
      promo.style.cssText = "max-width:900px;margin:14px auto 0;text-align:center";
      cont.insertAdjacentElement("afterend", promo);
    }
  } catch (e) {
    console.error(e);
  }
}

iniciar();
