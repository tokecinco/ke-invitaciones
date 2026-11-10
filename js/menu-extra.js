const t = setInterval(() => {
  const m = document.getElementById("menu");
  if (m && m.children.length) { clearInterval(t); agregar(m); }
}, 200);
setTimeout(() => clearInterval(t), 10000);

function agregar(m) {
  const h = document.createElement("h3");
  h.textContent = "Evento en vivo";
  const g = document.createElement("div");
  g.className = "menu";
  [["Álbum de fotos", "galeria.html"], ["Regalos", "regalos.html"]].forEach(([texto, url]) => {
    const a = document.createElement("a");
    a.href = url;
    a.textContent = texto;
    g.appendChild(a);
  });
  m.append(h, g);
}
