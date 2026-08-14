document.addEventListener("DOMContentLoaded", function () {
    const btnPagar = document.getElementById("btnPagarCompleto");
    const formPagar = document.getElementById("formPagarCompleto");

    if (btnPagar && formPagar) {
        btnPagar.addEventListener("click", function (e) {
            e.preventDefault();

            // Bloquear el botón temporalmente
            btnPagar.disabled = true;
            btnPagar.innerHTML = `<span class="spinner-border spinner-border-sm me-1" role="status"></span> Procesando...`;

            const formData = new FormData(formPagar);

            fetch(formPagar.action, {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (response.ok) {
                        mostrarToast("Pago completo de 40 soles registrado con éxito.", true);

                        // Recargar suavemente la página después de mostrar la notificación
                        setTimeout(() => {
                            window.location.reload();
                        }, 1200);
                    } else {
                        mostrarToast("Ocurrió un error al procesar el pago.", false);
                        btnPagar.disabled = false;
                        btnPagar.innerHTML = `<i class="bi bi-check-all me-1"></i> Pagar Completo`;
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    mostrarToast("Error en el servidor o problema de red.", false);
                    btnPagar.disabled = false;
                    btnPagar.innerHTML = `<i class="bi bi-check-all me-1"></i> Pagar Completo`;
                });
        });
    }

    // Función para activar Toast flotante
    function mostrarToast(mensaje, esExito = true) {
        const toastEl = document.getElementById('toastNotificacion');
        const toastMensaje = document.getElementById('toastMensaje');
        const icon = toastEl.querySelector('.bi');

        toastMensaje.innerText = mensaje;
        if (esExito) {
            icon.className = "bi bi-check-circle-fill text-success me-2";
        } else {
            icon.className = "bi bi-exclamation-triangle-fill text-danger me-2";
        }

        const toast = new bootstrap.Toast(toastEl, { delay: 2500 });
        toast.show();
    }
});