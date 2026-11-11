const CLAVE = "ke-ref";
const VIGENCIA = 30 * 24 * 60 * 60 * 1000;

const param = new URLSearchParams(location.search).get("r");
if (param && /^[a-z0-9]{3,20}$/i.test(param)) {
  try {
    localStorage.setItem(CLAVE, JSON.stringify({ c: param.toLowerCase(), t: Date.now() }));
  } catch (e) {}
}

function codigo() {
  try {
    const v = JSON.parse(localStorage.getItem(CLAVE) || "null");
    if (v && Date.now() - v.t < VIGENCIA) return v.c;
  } catch (e) {}
  return "";
}

document.addEventListener("click", (e) => {
  const a = e.target.closest && e.target.closest("a[href^='https://wa.me/']");
  if (!a) return;
  const c = codigo();
  if (!c) return;
  try {
    const u = new URL(a.href);
    let t = u.searchParams.get("text") || "";
    if (t.indexOf("Código de revendedor") !== -1) return;
    t += (t ? "\n\n" : "") + "Código de revendedor: " + c;
    u.searchParams.set("text", t);
    a.href = u.toString();
  } catch (err) {}
}, true);
