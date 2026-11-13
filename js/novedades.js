import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const idInvitacion = new URLSearchParams(location.search).get("i");
const $ = (id) => document.getElementById(id);

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
  d.innerHTML = '<svg viewBox="0 0 24 24" aria-hidden="true">' + trazos + "</svg>";
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

/* Itinerario: se muestra en el mismo orden en que se cargó */
function itinerario(d) {
  const lista = (Array.isArray(d.itinerary) ? d.itinerary : [])
    .filter((x) => x && typeof x.title === "string" && x.title.trim())
    .slice(0, 12);
  if (!lista.length) return;

  const s = el("section");
  s.id = "sec-itinerario";
  s.appendChild(icono(
    '<line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/>' +
    '<line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/>' +
    '<line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/>'));
  s.appendChild(el("h2", null, "Itinerario"));
  const cont = el("div", "timeline");
  lista.forEach((x) => {
    const f = el("div", "it-item");
    f.append(el("span", "it-hora", String(x.time || "")), el("span", "it-punto"),
      el("span", "it-titulo", String(x.title)));
    cont.appendChild(f);
  });
  s.appendChild(cont);

  const ref = $("fecha-hora");
  const sec = ref ? ref.closest("section") : null;
  if (sec) sec.insertAdjacentElement("afterend", s);
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

/* Hashtag de Instagram */
function hashtag(d) {
  const tag = String(d.hashtag || "").replace(/^#+/, "")
    .replace(/[^\p{L}\p{N}_]/gu, "").slice(0, 40);
  if (!tag) return;
  const s = el("section");
  s.id = "sec-hashtag";
  s.appendChild(el("h2", null, "Nuestro hashtag"));
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
      num.horas.textContent = Math.floor((s % 86400) / 3600);
      num.min.textContent = Math.floor((s % 3600) / 60);
      num.seg.textContent = s % 60;
    };
    actualizar();
    timer = setInterval(actualizar, 1000);
  }
  sec.appendChild(el("div", "corazon-final", "♥"));
}

iniciar();
