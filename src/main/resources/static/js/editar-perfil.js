// static/js/editar-perfil.js
document.addEventListener('DOMContentLoaded', function () {
    const fileInput = document.getElementById('fileFoto');
    const previewImg = document.getElementById('previewImg');
    const avatarContainer = document.querySelector('.avatar-container');

    avatarContainer.addEventListener('click', function () {
        fileInput.click();
    });

    fileInput.addEventListener('change', function (event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                previewImg.src = e.target.result;
                previewImg.classList.add('animate-pulse');
                setTimeout(() => {
                    previewImg.classList.remove('animate-pulse');
                }, 500);
            };
            reader.readAsDataURL(file);
        }
    });

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