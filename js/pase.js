document.addEventListener("DOMContentLoaded", () => {
    // 1. Capturamos el ID que viene en la URL (ej: pase.html?id=12345)
    const urlParams = new URLSearchParams(window.location.search);
    const paseId = urlParams.get('id');

    if (paseId) {
        cargarDatosPase(paseId);
    } else {
        // Vista de respaldo por si entran sin ID de prueba
        document.getElementById('nombre-invitado').innerText = "Invitado General";
        document.getElementById('cantidad-pases').innerText = "1";
        generarCodigoQR("EJEMPLO-QR-12345");
    }
});

function cargarDatosPase(id) {
    // Asegúrate de que 'firebase' esté inicializado en tu proyecto principal
    firebase.database().ref('invitados/' + id).once('value')
        .then((snapshot) => {
            if (snapshot.exists()) {
                const datos = snapshot.val();
                
                // Rellenamos los textos con la info de la base de datos
                document.getElementById('nombre-invitado').innerText = datos.nombre || "Sin nombre";
                document.getElementById('cantidad-pases').innerText = datos.cantidadPases || "1";
                
                // Texto que contendrá el QR (puede ser el ID o un código único de validación)
                const textoQr = datos.codigoUnico || id;
                generarCodigoQR(textoQr);
            } else {
                document.getElementById('nombre-invitado').innerText = "Pase no encontrado";
                document.getElementById('texto-cargando').innerText = "Código inválido";
            }
        })
        .catch((error) => {
            console.error("Error al leer el pase:", error);
            document.getElementById('nombre-invitado').innerText = "Error de conexión";
        });
}

function generarCodigoQR(texto) {
    const contenedorQr = document.getElementById('codigo-qr-container');
    contenedorQr.innerHTML = ""; // Limpiamos el texto de "Cargando..."
    
    // Generamos el código QR visualmente con la librería qrcode.js
    new QRCode(contenedorQr, {
        text: texto,
        width: 160,
        height: 160,
        colorDark: "#000000",
        colorLight: "#ffffff",
        correctLevel: QRCode.CorrectLevel.H
    });
}
