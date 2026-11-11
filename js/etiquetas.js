import { db, auth } from "./firebase.js";
import { onAuthStateChanged }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-auth.js";
import { collection, getDocs, query, where }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

let eventosPase = new Set();
const lista = document.getElementById("lista");

function marcar() {
  if (!lista || !eventosPase.size) return;
  lista.querySelectorAll(".tarjeta").forEach((t) => {
    const a = t.querySelector("a[href*='/i/']");
    if (!a) return;
    const id = decodeURIComponent(a.href.split("/i/").pop());
    if (!eventosPase.has(id)) return;
    const estado = t.children[1];
    if (estado && estado.textContent.indexOf(" - Básico - ") !== -1) {
      estado.textContent = estado.textContent.replace(" - Básico - ", " - Pase QR - ");
    }
  });
}

if (lista) new MutationObserver(marcar).observe(lista, { childList: true });

onAuthStateChanged(auth, async (u) => {
  if (!u) return;
  try {
    const s = await getDocs(query(collection(db, "invitations"), where("kind", "==", "pase")));
    eventosPase = new Set(s.docs.map((d) => d.id));
    marcar();
  } catch (e) { console.error(e); }
});
