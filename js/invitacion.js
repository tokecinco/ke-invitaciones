import { db } from "./firebase.js";
import {
  doc, getDoc, collection, addDoc, serverTimestamp
} from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const $ = (id) => document.getElementById(id);
const idInvitacion = new URLSearchParams(location.search).get("i");
const REDUCIR = window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches;
const LIMITES = { basico: { maxPhotos: 2, music: false }, premium: { maxPhotos: 6, music: true } };

function mostrarError(texto) {
  $("cargando").hidden = true;
  $("error").textContent = texto;
  $("error").hidden = false;
}
function urlSegura(url) {
  return typeof url === "string" && /^https:\/\//i.test(url);
}
function imagenSegura(url) {
  return typeof url === "string" && (/^https:\/\//i.test(url) || /^data:image\//i.test(url));
}

async function cargar() {
  if (!idInvitacion) return mostrarError("Falta el identificador de la invitación.");

  let d;
  try {
    const snap = await getDoc(doc(db, "invitations", idInvitacion));
    if (!snap.exists()) return mostrarError("Invitación no encontrada.");
    d = snap.data();
  } catch (e) {
    console.error(e);
    return mostrarError("Esta invitación no existe o no está disponible.");
  }
  if (d.active !== true) return mostrarError("Esta invitación no está disponible.");

  const clave = d.package === "premium" ? "premium" : "basico";
  let lim = { ...LIMITES[clave] };
  try {
    const p = await getDoc(doc(db, "settings", "packages"));
    if (p.exists() && p.data()[clave]) {
      const g = p.data()[clave];
      lim = { maxPhotos: g.maxPhotos || lim.maxPhotos, music: !!g.music };
    }
  } catch (e) { console.error(e); }

  mostrar(d, lim);
}

function mostrar(d, lim) {
  const plantillas = ["cumple", "quince", "boda"];
  if (plantillas.includes(d.template)) document.body.classList.add("tpl-" + d.template);

  const nombre = d.honoree || "";
  document.title = nombre ? nombre + " - Invitación" : "Invitación";
  $("sobre-nombre").textContent = nombre;
  $("nombre").textContent = nombre;
  $("tipo").textContent = d.eventType || "";
  $("bienvenida").textContent = d.welcome || "";
  $("mensaje").textContent = d.message || "";

  const fotos = (Array.isArray(d.photos) ? d.photos.filter(imagenSegura) : []).slice(0, lim.maxPhotos);
  if (fotos.length) {
    $("portada").style.setProperty("--foto", 'url("' + fotos[0] + '")');
    $("portada").classList.add("con-foto");
  }
  decorarPortada();
  armarGaleria(fotos.slice(1));

  const fecha = d.date ? new Date(d.date + "T" + (d.time || "00:00") + ":00") : null;
  if (fecha && !isNaN(fecha)) {
    const f = fecha.toLocaleDateString("es-AR", {
      weekday: "long", day: "numeric", month: "long", year: "numeric"
    });
    $("fecha-hora").textContent = f + (d.time ? " - " + d.time + " hs" : "");
    iniciarCuenta(fecha);
  } else {
    $("fecha-hora").textContent = "";
    $("cuenta").parentElement.hidden = true;
  }

  $("lugar").textContent = [d.venue, d.address].filter(Boolean).join(" - ");
  if (urlSegura(d.mapsUrl)) {
    $("btn-mapa").href = d.mapsUrl;
    $("btn-mapa").hidden = false;
  }
  if (d.info) {
    $("info").textContent = d.info;
    $("sec-info").hidden = false;
  }

  const antesDelFormulario = $("form-rsvp").closest("section");
  if (d.dressCode) agregarSeccion("sec-dress", "Dress code", d.dressCode, antesDelFormulario, false);
  if (d.gift) agregarSeccion("sec-regalo", "Regalo", d.gift, antesDelFormulario, true);

  const hayMusica = lim.music && urlSegura(d.music);
  if (hayMusica) configurarMusica(d.music);
  configurarRsvp(d);
  configurarCompartir(nombre);

  $("cargando").hidden = true;
  $("sobre").hidden = false;
  $("btn-abrir").addEventListener("click", () => {
    const caja = document.querySelector(".sobre-caja");
    caja.classList.add("saliendo");
    setTimeout(() => {
      $("sobre").hidden = true;
      $("invitacion").hidden = false;
      window.scrollTo(0, 0);
      activarRevelado();
      if (hayMusica) reproducir();
    }, REDUCIR ? 0 : 600);
  });
}

function decorarPortada() {
  const p = $("portada");
  const linea = document.createElement("div");
  linea.className = "linea";
  $("nombre").insertAdjacentElement("afterend", linea);

  const flecha = document.createElement("div");
  flecha.className = "deslizar";
  flecha.innerHTML = "Deslizá<span>&#8595;</span>";
  p.appendChild(flecha);

  if (REDUCIR) return;
  for (let i = 0; i < 10; i++) {
    const b = document.createElement("span");
    b.className = "brillo";
    const t = 4 + Math.random() * 7;
    b.style.width = b.style.height = t + "px";
    b.style.left = Math.random() * 100 + "%";
    b.style.animationDuration = 7 + Math.random() * 8 + "s";
    b.style.animationDelay = Math.random() * 6 + "s";
    p.appendChild(b);
  }
}

function agregarSeccion(id, titulo, texto, antesDe, conCopiar) {
  const s = document.createElement("section");
  s.id = id;
  const h = document.createElement("h2");
  h.textContent = titulo;
  const p = document.createElement("p");
  p.className = "tarjeta-esp";
  p.textContent = texto;
  s.append(h, p);
  if (conCopiar && navigator.clipboard) {
    const b = document.createElement("button");
    b.type = "button";
    b.className = "boton";
    b.textContent = "Copiar";
    b.addEventListener("click", () => {
      navigator.clipboard.writeText(texto).then(() => { b.textContent = "Copiado ✓"; });
    });
    s.appendChild(b);
  }
  antesDe.insertAdjacentElement("beforebegin", s);
}

function activarRevelado() {
  if (REDUCIR || !("IntersectionObserver" in window)) return;
  const obs = new IntersectionObserver((entradas) => {
    entradas.forEach((en) => {
      if (en.isIntersecting) {
        en.target.classList.add("visible");
        obs.unobserve(en.target);
      }
    });
  }, { threshold: 0.12 });
  document.querySelectorAll("#invitacion section:not(.portada)").forEach((sec) => {
    Array.from(sec.children).forEach((el, i) => {
      el.classList.add("reveal");
      el.style.transitionDelay = Math.min(i, 4) * 0.12 + "s";
      obs.observe(el);
    });
  });
}

/* Galería con visor a pantalla completa */
let lbLista = [], lbIdx = 0, lbEl = null;

function armarGaleria(lista) {
  if (!lista.length) return;
  lista.forEach((src, i) => {
    const img = document.createElement("img");
    img.src = src;
    img.alt = "Foto " + (i + 1);
    img.loading = "lazy";
    img.addEventListener("click", () => abrirVisor(lista, i));
    $("galeria").appendChild(img);
  });
  $("sec-galeria").hidden = false;
}

function crearVisor() {
  if (lbEl) return;
  lbEl = document.createElement("div");
  lbEl.id = "lightbox";
  lbEl.innerHTML =
    '<button type="button" class="lb-btn lb-cerrar" aria-label="Cerrar">&times;</button>' +
    '<button type="button" class="lb-btn lb-ant" aria-label="Anterior">&#8249;</button>' +
    '<img alt="">' +
    '<button type="button" class="lb-btn lb-sig" aria-label="Siguiente">&#8250;</button>' +
    '<div class="lb-cont"></div>';
  document.body.appendChild(lbEl);

  lbEl.querySelector(".lb-cerrar").addEventListener("click", cerrarVisor);
  lbEl.querySelector(".lb-ant").addEventListener("click", () => moverVisor(-1));
  lbEl.querySelector(".lb-sig").addEventListener("click", () => moverVisor(1));
  lbEl.addEventListener("click", (e) => { if (e.target === lbEl) cerrarVisor(); });

  let x0 = null;
  lbEl.addEventListener("touchstart", (e) => { x0 = e.touches[0].clientX; }, { passive: true });
  lbEl.addEventListener("touchend", (e) => {
    if (x0 === null) return;
    const dx = e.changedTouches[0].clientX - x0;
    if (Math.abs(dx) > 50) moverVisor(dx < 0 ? 1 : -1);
    x0 = null;
  });
  document.addEventListener("keydown", (e) => {
    if (!lbEl.classList.contains("abierto")) return;
    if (e.key === "Escape") cerrarVisor();
    if (e.key === "ArrowLeft") moverVisor(-1);
    if (e.key === "ArrowRight") moverVisor(1);
  });
}

function pintarVisor() {
  lbEl.querySelector("img").src = lbLista[lbIdx];
  lbEl.querySelector(".lb-cont").textContent = (lbIdx + 1) + " / " + lbLista.length;
  const solo = lbLista.length < 2;
  lbEl.querySelector(".lb-ant").hidden = solo;
  lbEl.querySelector(".lb-sig").hidden = solo;
}
function abrirVisor(lista, i) {
  crearVisor();
  lbLista = lista; lbIdx = i;
  pintarVisor();
  lbEl.classList.add("abierto");
  document.body.style.overflow = "hidden";
}
function moverVisor(paso) {
  lbIdx = (lbIdx + paso + lbLista.length) % lbLista.length;
  pintarVisor();
}
function cerrarVisor() {
  lbEl.classList.remove("abierto");
  document.body.style.overflow = "";
}

function iniciarCuenta(fecha) {
  const actualizar = () => {
    let ms = fecha - new Date();
    if (ms < 0) ms = 0;
    const s = Math.floor(ms / 1000);
    $("c-dias").textContent = Math.floor(s / 86400);
    $("c-horas").textContent = Math.floor((s % 86400) / 3600);
    $("c-min").textContent = Math.floor((s % 3600) / 60);
    $("c-seg").textContent = s % 60;
  };
  actualizar();
  setInterval(actualizar, 1000);
}

function configurarMusica(url) {
  const audio = $("audio");
  audio.src = url;
  $("btn-musica").hidden = false;
  audio.addEventListener("play", () => $("btn-musica").classList.add("sonando"));
  audio.addEventListener("pause", () => $("btn-musica").classList.remove("sonando"));
  $("btn-musica").addEventListener("click", () => {
    if (audio.paused) reproducir(); else audio.pause();
  });
}
function reproducir() {
  $("audio").play().catch(() => {});
}

function mostrarBotonWhatsapp(d, nombre, asiste, cantidad) {
  const numero = String(d.whatsapp || "").replace(/\D/g, "");
  if (numero.length < 8 || numero.length > 15) return;
  const texto = asiste
    ? "Hola! Soy " + nombre + ". Confirmo mi asistencia a " + (d.honoree || "el evento") +
      " (" + cantidad + " persona/s)."
    : "Hola! Soy " + nombre + ". Lamentablemente no voy a poder asistir a " +
      (d.honoree || "el evento") + ".";
  let a = $("btn-wa");
  if (!a) {
    a = document.createElement("a");
    a.id = "btn-wa";
    a.className = "boton";
    a.target = "_blank";
    a.rel = "noopener";
    $("r-estado").insertAdjacentElement("afterend", a);
  }
  a.textContent = "Avisar también por WhatsApp";
  a.href = "https://wa.me/" + numero + "?text=" + encodeURIComponent(texto);
}

function configurarRsvp(d) {
  $("form-rsvp").addEventListener("submit", async (e) => {
    e.preventDefault();
    const estado = $("r-estado");
    const nombre = $("r-nombre").value.trim();
    const asiste = $("r-asiste").value === "si";
    let cantidad = parseInt($("r-cantidad").value, 10);
    const mensaje = $("r-mensaje").value.trim();

    if (!nombre) { estado.textContent = "Escribí tu nombre."; return; }
    if (asiste && (!Number.isInteger(cantidad) || cantidad < 1 || cantidad > 20)) {
      estado.textContent = "La cantidad debe ser entre 1 y 20."; return;
    }
    if (!asiste) cantidad = 0;

    const datos = {
      invitationId: idInvitacion,
      name: nombre,
      attending: asiste,
      guests: cantidad,
      createdAt: serverTimestamp()
    };
    if (mensaje) datos.message = mensaje;

    const boton = e.target.querySelector("button");
    boton.disabled = true;
    estado.textContent = "Enviando...";
    try {
      await addDoc(collection(db, "rsvps"), datos);
      estado.textContent = "¡Gracias! Tu respuesta fue enviada.";
      e.target.reset();
      mostrarBotonWhatsapp(d, nombre, asiste, cantidad);
    } catch (err) {
      console.error(err);
      estado.textContent = "No se pudo enviar. Intentá de nuevo.";
    }
    boton.disabled = false;
  });
}

function configurarCompartir(nombre) {
  $("btn-compartir").addEventListener("click", () => {
    const texto = "Te invito a " + (nombre || "mi evento") + ": " + location.href;
    window.open("https://wa.me/?text=" + encodeURIComponent(texto), "_blank");
  });
}

cargar();
