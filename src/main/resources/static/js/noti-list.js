document.addEventListener("DOMContentLoaded", function () {

    // Listener delegado o individual
    document.querySelectorAll(".btn-eliminar-notif").forEach(btn => {
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            const form = this.closest("form");
            const notifItem = this.closest(".item-notificacion");

            const formData = new FormData(form);

            // Enviar petición POST AJAX (fetch)
            fetch(form.action, {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (response.ok) {
                        // Animación suave de deslizar y desaparecer
                        notifItem.style.transform = "translateX(50px)";
                        notifItem.style.opacity = "0";

                        setTimeout(() => {
                            notifItem.remove();
                            actualizarContadorYEstado();
                            mostrarToast("Notificación eliminada correctamente", true);
                        }, 250);
                    } else {
                        mostrarToast("Error al procesar la solicitud", false);
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    mostrarToast("Error de conexión con el servidor", false);
                });
        });
    });

    // Mostrar Toast
    function mostrarToast(mensaje, esExito = true) {
        const toastEl = document.getElementById('toastEliminado');
        const toastMensaje = document.getElementById('toastMensaje');
        const icon = toastEl.querySelector('.toast-body i');

        toastMensaje.innerText = mensaje;
        if (esExito) {
            icon.className = "bi bi-check-circle-fill text-success fs-5";
        } else {
            icon.className = "bi bi-exclamation-triangle-fill text-danger fs-5";
        }

        const toast = new bootstrap.Toast(toastEl, { delay: 3000 });
        toast.show();
    }

    // Actualiza interfaz si queda vacía
    function actualizarContadorYEstado() {
        const items = document.querySelectorAll(".item-notificacion");
        const contadorEl = document.getElementById("contadorNotificaciones");
        const contenedor = document.getElementById("contenedorNotificaciones");
        const btnMarcarTodas = document.getElementById("btnMarcarTodas");

        contadorEl.innerText = items.length + " Notificaciones";

        if (items.length === 0) {
            if (btnMarcarTodas) btnMarcarTodas.disabled = true;

            contenedor.innerHTML = `
                    <div id="estadoVacio" class="text-center py-5 my-3">
                        <div class="bg-light rounded-circle d-inline-flex p-4 mb-3">
                            <i class="bi bi-bell-slash text-secondary opacity-50 display-5"></i>
                        </div>
                        <h6 class="fw-bold text-navy mb-1">¡Todo al día! No tienes notificaciones.</h6>
                        <p class="text-muted small mb-0">Te avisaremos cuando se generen nuevos eventos o alertas en tu cuenta.</p>
                    </div>
                `;
        }
    }
});