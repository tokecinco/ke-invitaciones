import { db } from "./firebase.js";
import {
  doc, getDoc, collection, addDoc, serverTimestamp
} from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const $ = (id) => document.getElementById(id);
const params = new URLSearchParams(location.search);
const idInvitacion = params.get("i");
const idInvitado = params.get("g");
const REDUCIR = window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches;
const LIMITES = {
  basico: { maxPhotos: 7, music: true, personalized: false },
  premium: { maxPhotos: 9, music: true, personalized: true },
  pase: { maxPhotos: 1, music: false, personalized: true }
};

function mostrarError(texto) {
  $("cargando").hidden = true;
  $("error").textContent = texto;
  $("error").hidden = false;
}
function urlSegura(url) {
  return typeof url === "string" && /^https:\/\//i.test(url);
}
/* Lugares de "Cuándo y dónde": todos con el mismo formato
   (estrellita, título, horario dorado, dirección y botón de mapa) */
function armarLugares(d) {
  const dos = !!(d.venue2 || d.address2);
  const conLugar = !!(d.venue || d.address) || dos;
  const nuevo = (tag, clase, texto) => {
    const e = document.createElement(tag);
    if (clase) e.className = clase;
    if (texto) e.textContent = texto;
    return e;
  };

  const previos = [];
  if (conLugar) previos.push(nuevo("span", "lugar-sep", "✦"));
  if (d.venueLabel) previos.push(nuevo("p", "lugar-etq", d.venueLabel));
  if (conLugar && d.time) previos.push(nuevo("p", "lugar-hora", d.time + " hs"));
  previos.forEach((e) => $("lugar").insertAdjacentElement("beforebegin", e));
  if (!dos) return;

  const caja = nuevo("div", "lugar2");
  caja.appendChild(nuevo("span", "lugar-sep", "✦"));
  if (d.venue2Label) caja.appendChild(nuevo("p", "lugar-etq", d.venue2Label));
  if (d.venue2Time) caja.appendChild(nuevo("p", "lugar-hora", d.venue2Time + " hs"));
  caja.appendChild(nuevo("p", "lugar-texto", [d.venue2, d.address2].filter(Boolean).join(" - ")));
  if (urlSegura(d.mapsUrl2)) {
    const b = nuevo("a", "boton", "Ver en Google Maps");
    b.href = d.mapsUrl2;
    b.target = "_blank";
    b.rel = "noopener";
    caja.appendChild(b);
  }
  $("btn-mapa").insertAdjacentElement("afterend", caja);
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

  const clave = d.package === "premium" ? "premium" : (d.package === "pase" ? "pase" : "basico");
  let lim = { ...LIMITES[clave] };
  let tpl = null;
  try {
    const [p, t] = await Promise.all([
      getDoc(doc(db, "settings", "packages")).catch(() => null),
      d.template ? getDoc(doc(db, "templates", d.template)).catch(() => null) : null
    ]);
    if (p && p.exists() && p.data()[clave]) {
      const g = p.data()[clave];
      lim = {
        maxPhotos: g.maxPhotos || lim.maxPhotos,
        music: p.data().v3 === true ? !!g.music : (clave === "pase" ? false : true),
        personalized: !!g.personalized
      };
    }
    if (t && t.exists()) tpl = t.data();
  } catch (e) { console.error(e); }
  if (clave === "pase") lim = { ...LIMITES.pase };

  let invitado = null;
  if (idInvitado && lim.personalized) {
    try {
      const gs = await getDoc(doc(db, "guests", idInvitado));
      if (gs.exists() && gs.data().invitationId === idInvitacion) {
        invitado = { id: idInvitado, ...gs.data() };
      }
    } catch (e) { console.error(e); }
  }

  mostrar(d, lim, invitado, tpl);
}

function mostrar(d, lim, invitado, tpl) {
  const plantillas = ["cumple", "quince", "boda"];
  if (plantillas.includes(d.template)) document.body.classList.add("tpl-" + d.template);

  const nombre = d.honoree || "";
  document.title = nombre ? nombre + " - Invitación" : "Invitación";
  $("sobre-nombre").textContent = nombre;
  $("nombre").textContent = nombre;
  $("tipo").textContent = d.eventType || "";
  $("bienvenida").textContent = d.welcome || "";
  $("mensaje").textContent = d.message || "";

  if (invitado) personalizar(invitado);

  const usarMuestra = tpl && imagenSegura(tpl.sampleImage) &&
    (d.useTemplateCover === true || (d.useTemplateCover !== false && tpl.sampleAsCover === true));
  const fotos = Array.isArray(d.photos) ? d.photos.filter(imagenSegura) : [];
  const portada = usarMuestra ? tpl.sampleImage : (fotos[0] || "");
  const galeria = usarMuestra
    ? fotos.slice(0, Math.max(lim.maxPhotos - 1, 0))
    : fotos.slice(1, lim.maxPhotos);

  if (portada) {
    $("portada").style.setProperty("--foto", 'url("' + portada + '")');
    $("portada").classList.add("con-foto");
  }
  decorarPortada();
  armarGaleria(galeria, d.galleryType);

  const fecha = d.date ? new Date(d.date + "T" + (d.time || "00:00") + ":00") : null;
  if (fecha && !isNaN(fecha)) {
    const f = fecha.toLocaleDateString("es-AR", {
      weekday: "long", day: "numeric", month: "long", year: "numeric"
    });
    const conLugares = !!(d.venue || d.address || d.venue2 || d.address2);
    $("fecha-hora").textContent = f + (d.time && !conLugares ? " - " + d.time + " hs" : "");
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
  armarLugares(d);
  if (d.info) {
    $("info").textContent = d.info;
    $("sec-info").hidden = false;
  }

  const antesDelFormulario = $("form-rsvp").closest("section");
  if (d.dressCode) agregarSeccion("sec-dress", "Dress code", d.dressCode, antesDelFormulario, false);
  if (d.gift && d.package !== "pase") agregarSeccion("sec-regalo", "Regalo", d.gift, antesDelFormulario, true);

  const hayMusica = lim.music && urlSegura(d.music);
  if (hayMusica) configurarMusica(d.music);
  configurarRsvp(d, invitado);
  configurarCompartir(nombre);
  agregarFirma(d);

  let irFotocabina = location.hash === "#sec-fotocabina";
  let yaAbierta = irFotocabina;
  try {
    if (sessionStorage.getItem("ke-ir-fc-" + idInvitacion) === "1") irFotocabina = yaAbierta = true;
    if (sessionStorage.getItem("ke-abierta-" + idInvitacion) === "1") yaAbierta = true;
  } catch (e) {}

  $("cargando").hidden = true;
  if (yaAbierta) {
    $("sobre").hidden = true;
    $("invitacion").hidden = false;
    if (!irFotocabina) window.scrollTo(0, 0);
    activarRevelado();
    if (hayMusica) reproducir();
  } else {
    $("sobre").hidden = false;
  }

  $("btn-abrir").addEventListener("click", () => {
    try { sessionStorage.setItem("ke-abierta-" + idInvitacion, "1"); } catch (e) {}
    const caja = document.querySelector(".sobre-caja");
    const sobreEl = $("sobre");
    const invEl = $("invitacion");
    if (REDUCIR) {
      sobreEl.hidden = true;
      invEl.hidden = false;
      window.scrollTo(0, 0);
      activarRevelado();
      if (hayMusica) reproducir();
      return;
    }
    caja.classList.add("saliendo");
    sobreEl.classList.add("saliendo");
    invEl.style.opacity = "0";
    invEl.hidden = false;
    window.scrollTo(0, 0);
    activarRevelado();
    if (hayMusica) reproducir();
    setTimeout(() => {
      sobreEl.hidden = true;
      invEl.classList.add("entrando");
      invEl.style.opacity = "";
      setTimeout(() => invEl.classList.remove("entrando"), 1400);
    }, 480);
  });
}

function agregarFirma(d) {
  if (d.showBrand === false || $("firma-ke")) return;
  const base = location.origin + location.pathname.replace(/[^/]*$/, "");
  const f = document.createElement("footer");
  f.id = "firma-ke";
  f.className = "firma-ke";
  const a = document.createElement("a");
  a.href = base;
  a.target = "_blank";
  a.rel = "noopener";
  const mono = document.createElement("span");
  mono.className = "firma-mono";
  mono.setAttribute("aria-hidden", "true");
  mono.textContent = "KE";
  const txt = document.createElement("span");
  txt.append("Invitación creada con ");
  const b = document.createElement("b");
  b.textContent = "KE Invitaciones";
  txt.appendChild(b);
  a.append(mono, txt);
  f.appendChild(a);
  $("invitacion").appendChild(f);
}

function personalizar(invitado) {
  const estilo = document.createElement("style");
  estilo.textContent =
    ".para{margin:14px 0 0;font-size:1.05rem;line-height:1.4}" +
    ".para b{color:var(--acento);font-size:1.25rem}" +
    ".portada .para{opacity:0;animation:subir 1s ease 2s forwards}" +
    ".portada.con-foto .para b{color:#fff}";
  document.head.appendChild(estilo);

  const enSobre = document.createElement("p");
  enSobre.className = "para";
  enSobre.append("Para ");
  const b1 = document.createElement("b");
  b1.textContent = invitado.name;
  enSobre.appendChild(b1);
  $("sobre-nombre").insertAdjacentElement("afterend", enSobre);

  const enPortada = document.createElement("p");
  enPortada.className = "para";
  enPortada.append("Invitación para ");
  const b2 = document.createElement("b");
  b2.textContent = invitado.name;
  enPortada.appendChild(b2);
  enPortada.appendChild(document.createElement("br"));
  enPortada.append("Lugares reservados: " + invitado.seats);
  $("bienvenida").insertAdjacentElement("afterend", enPortada);
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

const TIPOS_GALERIA = ["polaroid", "masonry", "revista", "pelicula", "carrusel"];

function armarGaleria(lista, tipo) {
  if (!lista.length) return;
  const g = $("galeria");
  const conTipo = TIPOS_GALERIA.includes(tipo);
  if (conTipo) {
    g.classList.add("gal-" + tipo);
    const css = document.createElement("link");
    css.rel = "stylesheet";
    css.href = "css/galerias.css?v=1";
    document.head.appendChild(css);
  }
  lista.forEach((src, i) => {
    const img = document.createElement("img");
    img.src = src;
    img.alt = "Foto " + (i + 1);
    img.loading = "lazy";
    img.addEventListener("click", () => abrirVisor(lista, i));
    g.appendChild(img);
  });
  $("sec-galeria").hidden = false;
  if (tipo === "carrusel" && lista.length > 1) activarCarrusel(g, lista.length);
}

/* Carrusel: puntitos, avance automático suave y pausa cuando la persona lo toca */
function activarCarrusel(g, n) {
  const puntos = document.createElement("div");
  puntos.className = "gal-puntos";
  const marcas = [];
  for (let i = 0; i < n; i++) {
    const p = document.createElement("span");
    p.addEventListener("click", () => { ultimoToque = Date.now(); ir(i); });
    puntos.appendChild(p);
    marcas.push(p);
  }
  g.insertAdjacentElement("afterend", puntos);

  let actual = 0;
  let ultimoToque = 0;
  let visible = true;
  const paso = () => {
    const im = g.querySelector("img");
    return im ? im.offsetWidth + 12 : g.clientWidth;
  };
  const marcar = () => marcas.forEach((p, i) => p.classList.toggle("activo", i === actual));
  function ir(i) {
    actual = i;
    marcar();
    g.scrollTo({ left: i * paso(), behavior: "smooth" });
  }
  marcar();

  g.addEventListener("scroll", () => {
    const i = Math.max(0, Math.min(n - 1, Math.round(g.scrollLeft / paso())));
    if (i !== actual) { actual = i; marcar(); }
  }, { passive: true });
  ["touchstart", "pointerdown", "wheel"].forEach((ev) =>
    g.addEventListener(ev, () => { ultimoToque = Date.now(); }, { passive: true }));

  if ("IntersectionObserver" in window) {
    new IntersectionObserver((es) => { visible = es[0].isIntersecting; }).observe(g);
  }
  const quieto = window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches;
  if (!quieto) {
    setInterval(() => {
      if (document.hidden || !visible || Date.now() - ultimoToque < 6000) return;
      ir((actual + 1) % n);
    }, 4000);
  }
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

const dos = (n) => String(n).padStart(2, "0");

function iniciarCuenta(fecha) {
  const actualizar = () => {
    let ms = fecha - new Date();
    if (ms < 0) ms = 0;
    const s = Math.floor(ms / 1000);
    $("c-dias").textContent = Math.floor(s / 86400);
    $("c-horas").textContent = dos(Math.floor((s % 86400) / 3600));
    $("c-min").textContent = dos(Math.floor((s % 3600) / 60));
    $("c-seg").textContent = dos(s % 60);
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

function configurarRsvp(d, invitado) {
  const maximo = invitado ? invitado.seats : 20;

  const completar = () => {
    if (!invitado) return;
    $("r-nombre").value = invitado.name;
    $("r-cantidad").max = invitado.seats;
    $("r-cantidad").value = invitado.seats;
  };
  completar();

  $("form-rsvp").addEventListener("submit", async (e) => {
    e.preventDefault();
    const estado = $("r-estado");
    const nombre = $("r-nombre").value.trim();
    const asiste = $("r-asiste").value === "si";
    let cantidad = parseInt($("r-cantidad").value, 10);
    const mensaje = $("r-mensaje").value.trim();

    if (!nombre) { estado.textContent = "Escribí tu nombre."; return; }
    if (asiste && (!Number.isInteger(cantidad) || cantidad < 1 || cantidad > maximo)) {
      estado.textContent = invitado
        ? "Tenés " + invitado.seats + " lugar(es) reservado(s). Elegí entre 1 y " + invitado.seats + "."
        : "La cantidad debe ser entre 1 y 20.";
      return;
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
    if (invitado) datos.guestId = invitado.id;

    const boton = e.target.querySelector("button");
    boton.disabled = true;
    estado.textContent = "Enviando...";
    try {
      await addDoc(collection(db, "rsvps"), datos);
      estado.textContent = "¡Gracias! Tu respuesta fue enviada.";
      e.target.reset();
      completar();
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
