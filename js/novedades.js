import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const idInvitacion = new URLSearchParams(location.search).get("i");
const $ = (id) => document.getElementById(id);
const REDUCIR = window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches;

const css = document.createElement("link");
css.rel = "stylesheet";
css.href = "css/novedades.css";
document.head.appendChild(css);

function el(tag, clase, texto) {
  const e = document.createElement(tag);
  if (clase) e.className = clase;
  if (texto) e.textContent = texto;
  return e;
}

function icono(trazos) {
  const d = el("div", "icono");
  d.innerHTML = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">' + trazos + "</svg>";
  return d;
}

function antesDelFormulario(sec) {
  const r = $("form-rsvp");
  const s = r ? r.closest("section") : null;
  if (s) s.insertAdjacentElement("beforebegin", sec);
}

async function iniciar() {
  if (!idInvitacion) return;
  let d;
  try {
    const snap = await getDoc(doc(db, "invitations", idInvitacion));
    if (!snap.exists()) return;
    d = snap.data();
  } catch (e) { return; }
  if (d.active !== true) return;
  cuandoListo(() => construir(d));
}

// Espera a que la invitación termine de armarse.
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

function construir(d) {
  const fecha = d.date ? new Date(d.date + "T" + (d.time || "00:00") + ":00") : null;
  itinerario(d);
  playlist(d);
  hashtag(d);
  cierre(d, fecha);
}

/* Elige un ícono lineal acorde al título de la actividad */
function iconoActividad(titulo) {
  const t = String(titulo || "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
  if (/ceremonia religiosa|religios|bautismo|bautizo|comunion|confirmacion|misa|iglesia|parroquia|catedral/.test(t)) {
    return '<path d="M12 2v4"/><path d="M10 4h4"/><path d="M7 10l5-4 5 4"/><path d="M5 21V11h14v10"/><path d="M3 21h18"/><path d="M10 21v-5a2 2 0 0 1 4 0v5"/>';
  }
  if (/ceremonia|civil|altar|voto|anillo|bendicion/.test(t)) {
    return '<circle cx="9" cy="14" r="5"/><circle cx="15" cy="14" r="5"/><path d="M12 4l1.4 2.1L12 8.2l-1.4-2.1Z"/>';
  }
  if (/recepcion|bienvenida|brindis|coctel|trago|barra|aperitivo/.test(t)) {
    return '<path d="M8 22h8"/><path d="M12 12v10"/><path d="M7 3h10l-1 5a4 4 0 0 1-8 0Z"/><path d="M5 5l1.5.5"/><path d="M19 5l-1.5.5"/>';
  }
  if (/vals/.test(t)) {
    return '<circle cx="7.4" cy="5.8" r="1.55"/><path d="M6.2 8.2h2.8l.4 5.6H6Z"/><path d="M6.8 13.8 5.2 21"/><path d="M8.6 13.8 10.2 21"/><path d="M9.2 9.8 14.2 11.2"/><path d="M8.8 8.4 11.7 3"/><circle cx="16" cy="6.2" r="1.5"/><path d="M14.2 8.6h2.6l-.4 3.2 4.3 7.8c-2.6 1.5-6.2 1.7-8.7.6l2.5-8.4Z"/><path d="M15.3 12 16.7 20"/><path d="M14.3 8.8 11.7 3"/>';
  }
  if (/foto|sesion|camara|cabina/.test(t)) {
    return '<rect x="4" y="2.5" width="16" height="19" rx="1.5"/><rect x="6.5" y="5" width="11" height="10.5" rx=".5"/><circle cx="10" cy="8.5" r="1.2"/><path d="M6.5 15.5l3.8-3.8 2.5 2.5 1.7-1.7 3 3"/><path d="M8.5 18.8h5"/>';
  }
  if (/cena|almuerzo|comida|banquete|catering|asado|pizza|lunch|plato/.test(t)) {
    return '<path d="M3 17h18"/><path d="M5 17a7 7 0 0 1 14 0"/><circle cx="12" cy="8.5" r="1.5"/><path d="M4 19.5h16"/>';
  }
  if (/torta|pastel|dulce|postre|candy|vela/.test(t)) {
    return '<path d="M20 21v-8a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v8"/><path d="M4 16s.5-1 2-1 2.5 2 4 2 2.5-2 4-2 2.5 2 4 2 2-1 2-1"/><path d="M2 21h20"/><path d="M12 7v4"/><path d="M12 4.5c.8.8.8 1.8 0 2.5-.8-.7-.8-1.7 0-2.5Z"/>';
  }
  if (/karaoke|show|grupo musical|animador/.test(t)) {
    return '<rect x="9" y="2" width="6" height="11" rx="3"/><path d="M5 11a7 7 0 0 0 14 0"/><path d="M12 18v4"/><path d="M8 22h8"/>';
  }
  if (/vino|fernet|chupe|escabio|tomar/.test(t)) {
    return '<path d="M10 2h4"/><path d="M10.5 2v5.5C8.5 9 7.5 10 7.5 12v8.5a1.5 1.5 0 0 0 1.5 1.5h6a1.5 1.5 0 0 0 1.5-1.5V12c0-2-1-3-3-4.5V2"/><path d="M7.5 14.5h9"/><path d="M7.5 19h9"/>';
  }
  if (/merienda|\bte\b|cafe/.test(t)) {
    return '<path d="M4 10h13v4a5 5 0 0 1-5 5H9a5 5 0 0 1-5-5Z"/><path d="M17 11h1.5a2.5 2.5 0 0 1 0 5H17"/><path d="M3 21.5h15"/><path d="M8 3.5c-1 1-1 2 0 3"/><path d="M12 3.5c-1 1-1 2 0 3"/>';
  }
  if (/mate/.test(t)) {
    return '<ellipse cx="11" cy="9" rx="5.5" ry="1.8"/><path d="M5.5 9c0 6 1 11 5.5 11s5.5-5 5.5-11"/><path d="M12.5 9 19 2.5"/><path d="M17.8 2l2.2 2.2"/>';
  }
  if (/joda|baile|disco/.test(t)) {
    return '<path d="M12 1.5V5"/><circle cx="12" cy="13" r="8"/><ellipse cx="12" cy="13" rx="3.5" ry="8"/><path d="M4 13h16"/><path d="M5.4 8.5h13.2"/><path d="M5.4 17.5h13.2"/><path d="M12 5v16"/><path d="M21.5 3.5v2.5"/><path d="M20.25 4.75h2.5"/>';
  }
  if (/baile|fiesta|dj|banda|show|musica|carnaval|pista|loca/.test(t)) {
    return '<path d="M9 18V6l10-2v11"/><circle cx="6.5" cy="18" r="2.5"/><circle cx="16.5" cy="15" r="2.5"/><path d="M4 6l.8 1.5L6.3 8.3l-1.5.8L4 10.6l-.8-1.5L1.7 8.3l1.5-.8Z"/><path d="M20.5 18l.6 1.1 1.1.6-1.1.6-.6 1.1-.6-1.1-1.1-.6 1.1-.6Z"/>';
  }
  if (/cierre|fin\b|despedida|adios/.test(t)) {
    return '<path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/><path d="M19 4v3"/><path d="M17.5 5.5h3"/>';
  }
  return '<circle cx="12" cy="12" r="8.5"/><path d="M12 7.5V12l3 2"/>';
}

/* Aplica el mismo efecto reveal al deslizar, esperando a que #invitacion sea visible */
function revelarItinerario(sec, items) {
  if (REDUCIR || !("IntersectionObserver" in window)) return;
  const inv = $("invitacion");
  if (!inv) return;

  const iniciarObs = () => {
    const obs = new IntersectionObserver((entradas) => {
      entradas.forEach((en) => {
        if (en.isIntersecting) {
          en.target.classList.add("visible");
          obs.unobserve(en.target);
        }
      });
    }, { threshold: 0.05 });

    Array.from(sec.children).forEach((ch, i) => {
      if (!ch.classList.contains("timeline") && !ch.classList.contains("reveal")) {
        ch.classList.add("reveal");
        ch.style.transitionDelay = Math.min(i, 4) * 0.12 + "s";
        obs.observe(ch);
      }
    });

    items.forEach((it) => {
      it.classList.add("reveal");
      obs.observe(it);
    });
  };

  if (!inv.hidden) {
    iniciarObs();
  } else {
    items.forEach((it) => it.classList.add("reveal"));
    const mo = new MutationObserver(() => {
      if (!inv.hidden) {
        mo.disconnect();
        requestAnimationFrame(iniciarObs);
      }
    });
    mo.observe(inv, { attributes: true, attributeFilter: ["hidden"] });
  }
}

/* Itinerario: línea central con texto e íconos alternados */
function itinerario(d) {
  const lista = (Array.isArray(d.itinerary) ? d.itinerary : [])
    .filter((x) => x && typeof x.title === "string" && x.title.trim())
    .slice(0, 12);
  if (!lista.length) return;

  const s = el("section");
  s.id = "sec-itinerario";
  s.appendChild(icono(
    '<rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/>' +
    '<line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>' +
    '<path d="M8 14h.01"/><path d="M12 14h.01"/><path d="M16 14h.01"/>' +
    '<path d="M8 18h.01"/><path d="M12 18h.01"/>'));
  s.appendChild(el("h2", null, "Itinerario"));

  const cont = el("div", "timeline");
  const items = [];
  lista.forEach((x) => {
    const f = el("div", "it-item");

    const info = el("div", "it-info");
    let hora = String(x.time || "").trim();
    if (/^\d{1,2}:\d{2}$/.test(hora)) hora += " hs";
    if (hora) info.appendChild(el("span", "it-hora", hora));
    info.appendChild(el("p", "it-titulo", String(x.title).trim()));

    const punto = el("span", "it-punto");
    const ic = el("div", "it-icono");
    ic.innerHTML = '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">' + iconoActividad(x.title) + "</svg>";

    f.append(info, punto, ic);
    cont.appendChild(f);
    items.push(f);
  });
  s.appendChild(cont);

  const ref = $("fecha-hora");
  const sec = ref ? ref.closest("section") : null;
  if (sec) sec.insertAdjacentElement("afterend", s);

  revelarItinerario(s, items);
}

/* Playlist de Spotify */
function datosSpotify(u) {
  const m = /open\.spotify\.com\/(?:intl-[a-z]{2}\/)?(playlist|album|track|episode|show)\/([A-Za-z0-9]{10,30})/
    .exec(String(u || ""));
  return m ? { tipo: m[1], id: m[2] } : null;
}

function playlist(d) {
  const sp = datosSpotify(d.spotify);
  if (!sp) return;
  const s = el("section");
  s.id = "sec-playlist";
  s.appendChild(el("h2", null, "Playlist"));
  s.appendChild(el("p", null, "Escuchá la música del evento"));
  const f = document.createElement("iframe");
  f.className = "spotify";
  f.src = "https://open.spotify.com/embed/" + sp.tipo + "/" + sp.id;
  f.width = "100%";
  f.height = sp.tipo === "track" ? "152" : "352";
  f.setAttribute("frameborder", "0");
  f.setAttribute("allow", "autoplay; clipboard-write; encrypted-media; fullscreen; picture-in-picture");
  f.loading = "lazy";
  f.title = "Playlist de Spotify";
  s.appendChild(f);
  const a = el("a", "boton", "Abrir en Spotify");
  a.href = "https://open.spotify.com/" + sp.tipo + "/" + sp.id;
  a.target = "_blank";
  a.rel = "noopener";
  s.appendChild(a);
  antesDelFormulario(s);
}

/* Hashtag de Instagram: "Un recuerdo con vos" */
function hashtag(d) {
  const tag = String(d.hashtag || "").replace(/^#+/, "")
    .replace(/[^\p{L}\p{N}_]/gu, "").slice(0, 40);
  if (!tag) return;
  const s = el("section");
  s.id = "sec-hashtag";
  s.appendChild(el("h2", null, "Un recuerdo con vos"));
  s.appendChild(el("p", null, "Compartí tus fotos y videos del evento con:"));
  s.appendChild(el("p", "hashtag", "#" + tag));
  const caja = el("div", "botones");
  const copiar = el("button", "boton", "Copiar hashtag");
  copiar.type = "button";
  copiar.addEventListener("click", () => {
    if (navigator.clipboard) {
      navigator.clipboard.writeText("#" + tag).then(() => { copiar.textContent = "Copiado ✓"; });
    }
  });
  const ig = el("a", "boton", "Ver en Instagram");
  ig.href = "https://www.instagram.com/explore/tags/" + encodeURIComponent(tag) + "/";
  ig.target = "_blank";
  ig.rel = "noopener";
  caja.append(copiar, ig);
  s.appendChild(caja);
  antesDelFormulario(s);
}

/* Cierre: reemplaza el botón de compartir */
function cierre(d, fecha) {
  const btn = $("btn-compartir");
  const sec = btn ? btn.closest("section") : null;
  if (!sec) return;
  btn.hidden = true;
  btn.style.display = "none";
  sec.id = "sec-cierre";
  sec.classList.add("cierre");

  const lugar = String(d.venue || d.address || "").trim();
  sec.appendChild(el("p", "nos-vemos", lugar ? "Nos vemos en" : "Nos vemos pronto"));
  if (lugar) sec.appendChild(el("p", "nos-lugar", lugar));

  if (fecha && !isNaN(fecha)) {
    let f = fecha.toLocaleDateString("es-AR", {
      weekday: "long", day: "numeric", month: "long", year: "numeric"
    });
    f = f.charAt(0).toUpperCase() + f.slice(1);
    sec.appendChild(el("p", "nos-fecha", f + (d.time ? " - " + d.time + " hs" : "")));

    const caja = el("div", "cuenta");
    const num = {};
    [["dias", "días"], ["horas", "horas"], ["min", "min"], ["seg", "seg"]].forEach(([k, t]) => {
      const c = el("div");
      const b = el("b", null, "0");
      num[k] = b;
      c.append(b, el("span", null, t));
      caja.appendChild(c);
    });
    sec.appendChild(caja);

    const dos = (n) => String(n).padStart(2, "0");
    let timer = null;
    const actualizar = () => {
      const ms = fecha - new Date();
      if (ms <= 0) {
        clearInterval(timer);
        caja.replaceWith(el("p", "gracias", "¡Gracias por acompañarnos!"));
        return;
      }
      const s = Math.floor(ms / 1000);
      num.dias.textContent = Math.floor(s / 86400);
      num.horas.textContent = dos(Math.floor((s % 86400) / 3600));
      num.min.textContent = dos(Math.floor((s % 3600) / 60));
      num.seg.textContent = dos(s % 60);
    };
    actualizar();
    timer = setInterval(actualizar, 1000);
  }
  sec.appendChild(el("div", "corazon-final", "♥"));
}

iniciar();
