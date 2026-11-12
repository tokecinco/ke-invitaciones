const GRUPOS = [
  ["Evento en vivo", [
    ["Álbum de fotos", "galeria.html"], ["Libro de mensajes", "libro.html"],
    ["Regalos", "regalos.html"], ["Pases QR", "pases.html"]
  ]],
  ["Revendedores", [["Pedidos", "pedidos.html"], ["Revendedores", "revendedores.html"]]]
];

const t = setInterval(() => {
  const m = document.getElementById("menu");
  if (m && m.children.length) { clearInterval(t); agregar(m); }
}, 200);
setTimeout(() => clearInterval(t), 10000);

function agregar(m) {
  GRUPOS.forEach(([titulo, items]) => {
    const h = document.createElement("h3");
    h.textContent = titulo;
    const g = document.createElement("div");
    g.className = "menu";
    items.forEach(([texto, url]) => {
      const a = document.createElement("a");
      a.href = url;
      a.textContent = texto;
      g.appendChild(a);
    });
    m.append(h, g);
  });
}
