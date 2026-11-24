import { db } from "./firebase.js";
import { doc, getDoc }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

try { localStorage.removeItem("ke-ref"); } catch (e) {}

const param = new URLSearchParams(location.search).get("r");
const codigo = (param && /^[a-z0-9]{3,20}$/i.test(param)) ? param.toLowerCase() : "";

let waRev = "";

function aplicarEn(a) {
  if (!waRev || !a || a.tagName !== "A") return;
  const href = a.getAttribute("href") || "";
  if (!href.startsWith("https://wa.me/")) return;
  try {
    const u = new URL(href);
    if (u.hostname !== "wa.me" || u.pathname === "/" + waRev) return;
    u.pathname = "/" + waRev;
    a.href = u.toString();
  } catch (err) {}
}

function aplicarEnTodo() {
  if (!waRev) return;
  document.querySelectorAll("a[href^='https://wa.me/']").forEach(aplicarEn);
}

async function iniciar() {
  if (!codigo) return;
  try {
    const snap = await getDoc(doc(db, "settings", "resellers"));
    if (!snap.exists()) return;
    const links = snap.data().links || {};
    const num = String(links[codigo] || "").replace(/\D/g, "");
    if (num.length < 8 || num.length > 15) return;
    waRev = num;
    aplicarEnTodo();

    new MutationObserver((cambios) => {
      cambios.forEach((m) => {
        if (m.type === "attributes") {
          aplicarEn(m.target);
        } else {
          m.addedNodes.forEach((n) => {
            if (n.nodeType !== 1) return;
            if (n.tagName === "A") aplicarEn(n);
            if (n.querySelectorAll) n.querySelectorAll("a[href^='https://wa.me/']").forEach(aplicarEn);
          });
        }
      });
    }).observe(document.body, {
      childList: true,
      subtree: true,
      attributes: true,
      attributeFilter: ["href"]
    });
  } catch (e) {
    console.error(e);
  }
}

document.addEventListener("click", (e) => {
  const a = e.target.closest && e.target.closest("a[href^='https://wa.me/']");
  if (a) aplicarEn(a);
}, true);

iniciar();
