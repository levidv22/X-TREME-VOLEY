// static/js/login.js
document.addEventListener('DOMContentLoaded', function () {

    // 1. Mostrar/Ocultar Contraseña
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    const toggleIcon = document.getElementById('toggleIcon');

    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', function () {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);

            if (type === 'text') {
                toggleIcon.classList.remove('bi-eye');
                toggleIcon.classList.add('bi-eye-slash');
            } else {
                toggleIcon.classList.remove('bi-eye-slash');
                toggleIcon.classList.add('bi-eye');
            }
        });
    }

    // 2. Medidor en tiempo real de Fortaleza de Contraseña
    const passwordMeter = document.getElementById('passwordMeter');
    if (passwordInput && passwordMeter) {
        passwordInput.addEventListener('input', function () {
            const val = passwordInput.value;
            let score = 0;

            if (val.length >= 6) score += 33;
            if (val.length >= 8 && /[A-Z]/.test(val)) score += 33;
            if (/[0-9]/.test(val) && /[^A-Za-z0-9]/.test(val)) score += 34;

            passwordMeter.style.width = score + '%';

            if (score <= 33) {
                passwordMeter.className = 'progress-bar bg-danger';
            } else if (score <= 66) {
                passwordMeter.className = 'progress-bar bg-warning';
            } else {
                passwordMeter.className = 'progress-bar bg-success';
            }
        });
    }

    // 3. Previsualizador de Foto de Perfil en Tiempo Real
    const fileFotoInput = document.getElementById('fileFoto');
    const avatarPreview = document.getElementById('avatarPreview');

    if (fileFotoInput && avatarPreview) {
        fileFotoInput.addEventListener('change', function (e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function (event) {
                    avatarPreview.src = event.target.result;
                };
                reader.readAsDataURL(file);
            }
        });
    }

    // 4. Validación nativa de Bootstrap
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