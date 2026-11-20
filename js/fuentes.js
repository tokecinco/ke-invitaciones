// Lista única de tipografías para las plantillas (la usan el editor y la invitación).
// Las que tienen "google" se descargan de Google Fonts solo cuando una plantilla las usa.
// "peso" (opcional) fuerza el grosor de la letra, por ejemplo 700 para negrita.
export const FUENTES = [
  /* Del sistema */
  { css: "Georgia, serif", nombre: "Georgia (clásica)" },
  { css: "'Palatino Linotype', Palatino, serif", nombre: "Palatino (elegante)" },
  { css: "'Times New Roman', serif", nombre: "Times (tradicional)" },
  { css: "'Trebuchet MS', sans-serif", nombre: "Trebuchet (alegre)" },
  { css: "Verdana, sans-serif", nombre: "Verdana (simple)" },

  /* Serif elegantes */
  { css: "'Playfair Display', serif", nombre: "Playfair Display (elegante y moderna)", google: "Playfair+Display:wght@400;700" },
  { css: "'Cormorant Garamond', serif", nombre: "Cormorant Garamond (fina y delicada)", google: "Cormorant+Garamond:wght@400;600" },
  { css: "'Cinzel', serif", nombre: "Cinzel (clásica en mayúsculas)", google: "Cinzel:wght@400;700" },
  { css: "'Bodoni Moda', serif", nombre: "Bodoni Moda (alta costura)", google: "Bodoni+Moda:wght@400;700" },
  { css: "'Gilda Display', serif", nombre: "Gilda Display (refinada)", google: "Gilda+Display" },
  { css: "'Italiana', serif", nombre: "Italiana (fina y estilizada)", google: "Italiana" },
  { css: "'DM Serif Display', serif", nombre: "DM Serif Display (marcada y elegante)", google: "DM+Serif+Display" },
  { css: "'Prata', serif", nombre: "Prata (editorial)", google: "Prata" },
  { css: "'Lora', serif", nombre: "Lora (legible)", google: "Lora:wght@400;700" },
  { css: "'Radley', serif", nombre: "Radley (clásica con carácter)", google: "Radley" },
  { css: "'Antic Slab', serif", nombre: "Antic Slab (con remates rectos)", google: "Antic+Slab" },

  /* Cursivas */
  { css: "'Great Vibes', cursive", nombre: "Great Vibes (cursiva de boda)", google: "Great+Vibes" },
  { css: "'Parisienne', cursive", nombre: "Parisienne (cursiva romántica)", google: "Parisienne" },
  { css: "'Allura', cursive", nombre: "Allura (caligrafía fina)", google: "Allura" },
  { css: "'Alex Brush', cursive", nombre: "Alex Brush (pincel elegante)", google: "Alex+Brush" },
  { css: "'Pinyon Script', cursive", nombre: "Pinyon Script (caligrafía clásica)", google: "Pinyon+Script" },
  { css: "'Sacramento', cursive", nombre: "Sacramento (trazo liviano)", google: "Sacramento" },
  { css: "'Ballet', cursive", nombre: "Ballet (cursiva con vuelo)", google: "Ballet" },
  { css: "'Tangerine', cursive", nombre: "Tangerine (caligrafía delicada)", google: "Tangerine:wght@400;700" },
  { css: "'Italianno', cursive", nombre: "Italianno (cursiva italiana)", google: "Italianno" },
  { css: "'Rouge Script', cursive", nombre: "Rouge Script (cursiva antigua)", google: "Rouge+Script" },
  { css: "'Bad Script', cursive", nombre: "Bad Script (escritura a mano)", google: "Bad+Script" },
  { css: "'Rock Salt', cursive", nombre: "Rock Salt (marcador informal)", google: "Rock+Salt" },
  { css: "'Dancing Script', cursive", nombre: "Dancing Script (cursiva casual)", google: "Dancing+Script:wght@400;700" },
  { css: "'Pacifico', cursive", nombre: "Pacifico (divertida)", google: "Pacifico" },

  /* Modernas y de titulares */
  { css: "'Montserrat', sans-serif", nombre: "Montserrat (moderna)", google: "Montserrat:wght@400;600" },
  { css: "'Poppins', sans-serif", nombre: "Poppins (redondeada)", google: "Poppins:wght@400;600" },
  { css: "'Fredoka', sans-serif", nombre: "Fredoka (infantil)", google: "Fredoka:wght@400;600" },
  { css: "'Manrope', sans-serif", nombre: "Manrope (limpia y actual)", google: "Manrope:wght@400;700" },
  { css: "'Outfit', sans-serif", nombre: "Outfit (geométrica)", google: "Outfit:wght@400;600" },
  { css: "'Plus Jakarta Sans', sans-serif", nombre: "Plus Jakarta Sans (moderna y suave)", google: "Plus+Jakarta+Sans:wght@400;700" },
  { css: "'Raleway', sans-serif", nombre: "Raleway (elegante y fina)", google: "Raleway:wght@400;600" },
  { css: "'Lato', sans-serif", nombre: "Lato (clara y amable)", google: "Lato:wght@400;700" },
  { css: "'Inter', sans-serif", nombre: "Inter (neutra y legible)", google: "Inter:wght@400;600" },
  { css: "'Nunito Sans', sans-serif", nombre: "Nunito Sans (suave)", google: "Nunito+Sans:wght@400;700" },
  { css: "'Spinnaker', sans-serif", nombre: "Spinnaker (redondeada y amable)", google: "Spinnaker" },
  { css: "'Josefin Sans', sans-serif", nombre: "Josefin Sans (geométrica y elegante)", google: "Josefin+Sans:wght@400;600" },
  { css: "'Julius Sans One', sans-serif", nombre: "Julius Sans One (mayúsculas finas)", google: "Julius+Sans+One" },
  { css: "'Oswald', sans-serif", nombre: "Oswald Bold (condensada, en negrita)", google: "Oswald:wght@400;700", peso: 700 },
  { css: "'Anton', sans-serif", nombre: "Anton (titulares gruesos)", google: "Anton" },
  { css: "'Unbounded', sans-serif", nombre: "Unbounded (ancha y moderna)", google: "Unbounded:wght@400;600" }
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
