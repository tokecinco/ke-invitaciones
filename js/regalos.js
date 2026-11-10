import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

const idInv = new URLSearchParams(location.search).get("i");
const $ = (id) => document.getElementById(id);
const https = (u) => typeof u === "string" && /^https:\/\//i.test(u);

const css = document.createElement("link");
css.rel = "stylesheet";
css.href = "css/regalos.css";
document.head.appendChild(css);

function el(tag, clase, texto) {
  const e = document.createElement(tag);
  if (clase) e.className = clase;
  if (texto) e.textContent = texto;
  return e;
}

async function iniciar() {
  if (!idInv) return;
  try {
    const s = await getDoc(doc(db, "invitations", idInv));
    if (!s.exists() || s.data().active !== true) return;
    const d = s.data();
    const alias = String(d.giftAlias || "").trim();
    const pay = https(d.giftPayUrl) ? d.giftPayUrl : "";
    const lista = (Array.isArray(d.giftList) ? d.giftList : [])
      .filter((g) => g && typeof g.name === "string" && g.name.trim()).slice(0, 20);
    if (!alias && !pay && !lista.length) return;
    cuandoListo(() => construir(d, alias, pay, lista));
  } catch (e) { console.error(e); }
}

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

function construir(d, alias, pay, lista) {
  let s = $("sec-regalo");
  if (!s) {
    s = el("section");
    s.id = "sec-regalo";
    s.appendChild(el("h2", null, "Regalo"));
    const r = $("form-rsvp");
    const ref = r ? r.closest("section") : null;
    if (!ref) return;
    ref.insertAdjacentElement("beforebegin", s);
  }

  if (alias) {
    const b = el("div", "regalo-bloque regalo-alias");
    b.appendChild(el("p", "regalo-etiqueta", "Alias"));
    b.appendChild(el("p", "regalo-valor", alias));
    if (d.giftHolder) b.appendChild(el("p", "regalo-titular", "Titular: " + d.giftHolder));
    if (navigator.clipboard) {
      const c = el("button", "boton", "Copiar alias");
      c.type = "button";
      c.addEventListener("click", () => {
        navigator.clipboard.writeText(alias).then(() => { c.textContent = "Alias copiado ✓"; });
      });
      b.appendChild(c);
    }
    s.appendChild(b);
  }

  if (pay) {
    const b = el("div", "regalo-bloque");
    const a = el("a", "boton", d.giftPayLabel || "Enviar regalo");
    a.href = pay;
    a.target = "_blank";
    a.rel = "noopener noreferrer";
    b.appendChild(a);
    s.appendChild(b);
  }

  if (lista.length) {
    const b = el("div", "regalo-bloque");
    b.appendChild(el("p", "regalo-subtitulo", "Lista de regalos"));
    const ul = el("ul", "regalo-lista");
    lista.forEach((g) => {
      const li = el("li");
      li.appendChild(el("span", "nombre", g.name));
      if (https(g.url)) {
        const a = el("a", null, "Ver");
        a.href = g.url;
        a.target = "_blank";
        a.rel = "noopener noreferrer";
        li.appendChild(a);
      }
      ul.appendChild(li);
    });
    b.appendChild(ul);
    s.appendChild(b);
  }
}

iniciar();
