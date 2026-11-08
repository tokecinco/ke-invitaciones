import { db } from "./firebase.js";
import {
  doc, getDoc, collection, addDoc, serverTimestamp
} from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const $ = (id) => document.getElementById(id);
const idInvitacion = new URLSearchParams(location.search).get("i");

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

  mostrar(d);
}

function mostrar(d) {
  const plantillas = ["cumple", "quince", "boda"];
  if (plantillas.includes(d.template)) document.body.classList.add("tpl-" + d.template);

  const nombre = d.honoree || "";
  document.title = nombre ? nombre + " - Invitación" : "Invitación";
  $("sobre-nombre").textContent = nombre;
  $("nombre").textContent = nombre;
  $("tipo").textContent = d.eventType || "";
  $("bienvenida").textContent = d.welcome || "";
  $("mensaje").textContent = d.message || "";

  const fotos = Array.isArray(d.photos) ? d.photos.filter(imagenSegura) : [];
  if (fotos.length) {
    $("portada").style.backgroundImage = 'url("' + fotos[0] + '")';
    $("portada").classList.add("con-foto");
  }
  if (fotos.length > 1) {
    fotos.slice(1).forEach((src) => {
      const img = document.createElement("img");
      img.src = src;
      img.alt = "Foto";
      img.loading = "lazy";
      $("galeria").appendChild(img);
    });
    $("sec-galeria").hidden = false;
  }

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

  const lugar = [d.venue, d.address].filter(Boolean).join(" - ");
  $("lugar").textContent = lugar;
  if (urlSegura(d.mapsUrl)) {
    $("btn-mapa").href = d.mapsUrl;
    $("btn-mapa").hidden = false;
  }
  if (d.info) {
    $("info").textContent = d.info;
    $("sec-info").hidden = false;
  }

  configurarMusica(d.music);
  configurarRsvp(d);
  configurarCompartir(nombre);

  $("cargando").hidden = true;
  $("sobre").hidden = false;
  $("btn-abrir").addEventListener("click", () => {
    $("sobre").hidden = true;
    $("invitacion").hidden = false;
    window.scrollTo(0, 0);
    if (urlSegura(d.music)) reproducir();
  });
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
  if (!urlSegura(url)) return;
  const audio = $("audio");
  audio.src = url;
  $("btn-musica").hidden = false;
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
