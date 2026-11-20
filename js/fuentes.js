// Lista única de tipografías para las plantillas (la usan el editor y la invitación).
// Las que tienen "google" se descargan de Google Fonts solo cuando una plantilla las usa.
export const FUENTES = [
  { css: "Georgia, serif", nombre: "Georgia (clásica)" },
  { css: "'Palatino Linotype', Palatino, serif", nombre: "Palatino (elegante)" },
  { css: "'Times New Roman', serif", nombre: "Times (tradicional)" },
  { css: "'Trebuchet MS', sans-serif", nombre: "Trebuchet (alegre)" },
  { css: "Verdana, sans-serif", nombre: "Verdana (simple)" },
  { css: "'Playfair Display', serif", nombre: "Playfair Display (elegante y moderna)", google: "Playfair+Display:wght@400;700" },
  { css: "'Cormorant Garamond', serif", nombre: "Cormorant Garamond (fina y delicada)", google: "Cormorant+Garamond:wght@400;600" },
  { css: "'Cinzel', serif", nombre: "Cinzel (clásica en mayúsculas)", google: "Cinzel:wght@400;700" },
  { css: "'Great Vibes', cursive", nombre: "Great Vibes (cursiva de boda)", google: "Great+Vibes" },
  { css: "'Dancing Script', cursive", nombre: "Dancing Script (cursiva casual)", google: "Dancing+Script:wght@400;700" },
  { css: "'Pacifico', cursive", nombre: "Pacifico (divertida)", google: "Pacifico" },
  { css: "'Fredoka', sans-serif", nombre: "Fredoka (infantil)", google: "Fredoka:wght@400;600" },
  { css: "'Montserrat', sans-serif", nombre: "Montserrat (moderna)", google: "Montserrat:wght@400;600" },
  { css: "'Poppins', sans-serif", nombre: "Poppins (redondeada)", google: "Poppins:wght@400;600" },
  { css: "'Lora', serif", nombre: "Lora (legible)", google: "Lora:wght@400;700" }
];

export function buscarFuente(css) {
  return FUENTES.find((f) => f.css === css) || null;
}

export function cargarFuente(f) {
  if (!f || !f.google) return;
  const id = "gf-" + f.google.split(":")[0];
  if (document.getElementById(id)) return;
  const l = document.createElement("link");
  l.id = id;
  l.rel = "stylesheet";
  l.href = "https://fonts.googleapis.com/css2?family=" + f.google + "&display=swap";
  document.head.appendChild(l);
    }
