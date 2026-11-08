import { db, auth } from "./firebase.js";
import { onAuthStateChanged }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-auth.js";
import { collection, getDocs }
  from "https://www.gstatic.com/firebasejs/10.14.1/firebase-firestore.js";

function rellenar(select, items, primera) {
  if (!select) return;
  const actual = select.value;
  select.innerHTML = "";
  if (primera) {
    const o = document.createElement("option");
    o.value = ""; o.textContent = primera;
    select.appendChild(o);
  }
  items.forEach((it) => {
    const o = document.createElement("option");
    o.value = it.id; o.textContent = it.name;
    select.appendChild(o);
  });
  if (items.some((it) => it.id === actual) || (primera && actual === "")) select.value = actual;
}

onAuthStateChanged(auth, async (u) => {
  if (!u) return;
  try {
    const snap = await getDocs(collection(db, "templates"));
    if (snap.empty) return;
    const items = snap.docs
      .filter((d) => d.data().active !== false)
      .map((d) => ({ id: d.id, name: d.data().name || d.id }))
      .sort((a, b) => a.name.localeCompare(b.name, "es"));
    if (!items.length) return;
    rellenar(document.getElementById("f-template"), items, null);
    rellenar(document.getElementById("filtro"), items, "Todas las plantillas");
  } catch (e) {
    console.error(e);
  }
});
