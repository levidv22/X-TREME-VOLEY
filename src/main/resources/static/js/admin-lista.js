const inputBuscador = document.getElementById('inputBuscador');
const btnClear = document.getElementById('btnClear');
const filas = document.querySelectorAll('.fila-cliente');
const noResultados = document.getElementById('noResultados');
const contadorResultados = document.getElementById('contadorResultados');

// Búsqueda interactiva en tiempo real
inputBuscador.addEventListener('input', function() {
    const filtro = this.value.toLowerCase().trim();
    let encontrados = 0;

    // Mostrar / ocultar botón X para limpiar
    btnClear.style.display = filtro.length > 0 ? 'block' : 'none';

    filas.forEach(fila => {
        const nombre = fila.querySelector('.nombre-cliente').textContent.toLowerCase();
        const dni = fila.querySelector('.dni-cliente').textContent.toLowerCase();
        const telefono = fila.querySelector('.telefono-cliente').textContent.toLowerCase();

        if (nombre.includes(filtro) || dni.includes(filtro) || telefono.includes(filtro)) {
            fila.classList.remove('d-none');
            encontrados++;
        } else {
            fila.classList.add('d-none');
        }
    });

    // Actualizar contador y mostrar Empty State si es necesario
    contadorResultados.textContent = encontrados;
    if (encontrados === 0) {
        noResultados.classList.remove('d-none');
    } else {
        noResultados.classList.add('d-none');
    }
});

// Limpiar buscador al pulsar X
btnClear.addEventListener('click', function() {
    inputBuscador.value = '';
    inputBuscador.dispatchEvent(new Event('input'));
    inputBuscador.focus();
});

// Función para copiar DNI al portapapeles
function copiarTexto(element) {
    const dniText = element.previousElementSibling.textContent;
    navigator.clipboard.writeText(dniText).then(() => {
        element.classList.replace('bi-copy', 'bi-check-lg');
        element.classList.add('text-success');
        setTimeout(() => {
            element.classList.replace('bi-check-lg', 'bi-copy');
            element.classList.remove('text-success');
        }, 1500);
    });
}