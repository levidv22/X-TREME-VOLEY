// static/js/editar-perfil.js
document.addEventListener('DOMContentLoaded', function () {
    const fileInput = document.getElementById('fileFoto');
    const previewImg = document.getElementById('previewImg');
    const uploadBadge = document.querySelector('.upload-badge-btn');
    const avatarWrapper = document.querySelector('.avatar-edit-wrapper');

    // Permitir clic tanto en el botón de cámara como en el área del avatar
    if (uploadBadge) {
        uploadBadge.addEventListener('click', function (e) {
            e.preventDefault();
            fileInput.click();
        });
    }

    if (avatarWrapper) {
        avatarWrapper.addEventListener('click', function () {
            fileInput.click();
        });
    }

    // Previsualización inmediata al seleccionar una imagen
    fileInput.addEventListener('change', function (event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                previewImg.src = e.target.result;
                previewImg.classList.add('animate-pulse');
                setTimeout(() => {
                    previewImg.classList.remove('animate-pulse');
                }, 600);
            };
            reader.readAsDataURL(file);
        }
    });

    // Validación Bootstrap
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
});