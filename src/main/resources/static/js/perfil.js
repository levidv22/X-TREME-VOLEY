// Función global para copiar texto y activar el Toast flotante
function copiarTexto(inputId, tipoEtiqueta) {
    const input = document.getElementById(inputId);
    if (!input) return;

    input.select();
    input.setSelectionRange(0, 99999); // Para compatibilidad en móviles

    navigator.clipboard.writeText(input.value).then(() => {
        mostrarToastCopiado(tipoEtiqueta + " copiado al portapapeles con éxito.");
    }).catch(err => {
        console.error("Error al copiar: ", err);
        mostrarToastCopiado("No se pudo copiar el texto.", false);
    });
}

// Muestra la alerta tipo Toast
function mostrarToastCopiado(mensaje, esExito = true) {
    const toastEl = document.getElementById('toastCopiado');
    const toastMensaje = document.getElementById('toastMensajeCopiado');
    const icon = toastEl.querySelector('.bi');

    toastMensaje.innerText = mensaje;
    if (esExito) {
        icon.className = "bi bi-clipboard-check-fill text-success me-2";
    } else {
        icon.className = "bi bi-exclamation-triangle-fill text-danger me-2";
    }

    const toast = new bootstrap.Toast(toastEl, { delay: 2500 });
    toast.show();
}