document.addEventListener("DOMContentLoaded", function () {
    const btnPagar = document.getElementById("btnPagarCompleto");
    const formPagar = document.getElementById("formPagarCompleto");

    if (btnPagar && formPagar) {
        btnPagar.addEventListener("click", function (e) {
            e.preventDefault();

            // Bloquear el botón temporalmente y mostrar loader
            btnPagar.disabled = true;
            btnPagar.innerHTML = `<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Procesando...`;

            const formData = new FormData(formPagar);

            fetch(formPagar.action, {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (response.ok) {
                        mostrarToast("Pago registrado con éxito.", true);

                        setTimeout(() => {
                            window.location.reload();
                        }, 1200);
                    } else {
                        mostrarToast("Ocurrió un error al procesar el pago.", false);
                        btnPagar.disabled = false;
                        btnPagar.innerHTML = `<i class="bi bi-check2-all me-1"></i> Registrar Pago Completo`;
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    mostrarToast("Error de conexión con el servidor.", false);
                    btnPagar.disabled = false;
                    btnPagar.innerHTML = `<i class="bi bi-check2-all me-1"></i> Registrar Pago Completo`;
                });
        });
    }

    // Función global para Toasts estilo SaaS
    function mostrarToast(mensaje, esExito = true) {
        const toastEl = document.getElementById('toastNotificacion');
        const toastMensaje = document.getElementById('toastMensaje');
        const icon = toastEl.querySelector('.toast-body .bi');

        if (toastMensaje && icon) {
            toastMensaje.innerText = mensaje;
            if (esExito) {
                icon.className = "bi bi-check-circle-fill text-success fs-5 me-2";
            } else {
                icon.className = "bi bi-exclamation-triangle-fill text-danger fs-5 me-2";
            }

            const toast = new bootstrap.Toast(toastEl, { delay: 2500 });
            toast.show();
        }
    }
});