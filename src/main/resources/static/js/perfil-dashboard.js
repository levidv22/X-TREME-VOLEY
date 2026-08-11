/* static/js/perfil-dashboard.js */
document.addEventListener('DOMContentLoaded', function () {
    console.log("Voley Control Dashboard Loaded Successfully.");

    const codigo = document.getElementById('inputCodigo')?.value || '';
    const link = document.getElementById('inputLink')?.value || '';

    const btnWpCodigo = document.getElementById('btnWpCodigo');
    if (btnWpCodigo) {
        const msgCodigo = `¡Únete a Voley Control! Mi código de referido es: ${codigo}`;
        btnWpCodigo.href = `https://api.whatsapp.com/send?text=${encodeURIComponent(msgCodigo)}`;
    }

    const btnWpLink = document.getElementById('btnWpLink');
    if (btnWpLink) {
        const msgLink = `¡Únete a Voley Control usando mi enlace de referido! Regístrate aquí: ${link}`;
        btnWpLink.href = `https://api.whatsapp.com/send?text=${encodeURIComponent(msgLink)}`;
    }
});

function copiarTexto(idElemento) {
    const input = document.getElementById(idElemento);
    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(input.value).then(() => {
            alert("¡Copiado al portapapeles!");
        }).catch(err => {
            fallbackCopiar(input);
        });
    } else {
        fallbackCopiar(input);
    }
}

function fallbackCopiar(input) {
    input.focus();
    input.select();
    input.setSelectionRange(0, 99999);
    try {
        document.execCommand('copy');
        alert("¡Copiado al portapapeles!");
    } catch (err) {
        alert("Error al intentar copiar.");
    }
}