document.addEventListener("DOMContentLoaded", function () {
    const filasPorPagina = 5;
    const tabla = document.getElementById("tablaBonos");
    if (!tabla) return;

    const tbody = document.getElementById("tbodyBonos");
    const filas = Array.from(tbody.querySelectorAll("tr"));
    const totalFilas = filas.length;
    const totalPaginas = Math.ceil(totalFilas / filasPorPagina);

    let paginaActual = 1;

    const navPaginacion = document.getElementById("paginacionBonos");
    const infoPaginacion = document.getElementById("infoPaginacionBonos");

    function mostrarPagina(pagina) {
        paginaActual = pagina;
        const inicio = (pagina - 1) * filasPorPagina;
        const fin = inicio + filasPorPagina;

        // Ocultar todas las filas y mostrar solo las de la página actual
        filas.forEach((fila, index) => {
            if (index >= inicio && index < fin) {
                fila.style.display = "";
            } else {
                fila.style.display = "none";
            }
        });

        // Actualizar texto informativo
        const registrosInicio = totalFilas === 0 ? 0 : inicio + 1;
        const registrosFin = Math.min(fin, totalFilas);
        infoPaginacion.textContent = `Mostrando ${registrosInicio} a ${registrosFin} de ${totalFilas} registros`;

        renderizarBotones();
    }

    function renderizarBotones() {
        navPaginacion.innerHTML = "";

        if (totalPaginas <= 1) return;

        // Botón Anterior (<)
        const liAnterior = document.createElement("li");
        liAnterior.className = `page-item ${paginaActual === 1 ? 'disabled' : ''}`;
        liAnterior.innerHTML = `<a class="page-link border-0 text-secondary px-3" href="#" aria-label="Anterior"><i class="bi bi-chevron-left"></i></a>`;
        liAnterior.addEventListener("click", (e) => {
            e.preventDefault();
            if (paginaActual > 1) mostrarPagina(paginaActual - 1);
        });
        navPaginacion.appendChild(liAnterior);

        // Números de página inteligentes estilo < 1 2 3 ... 12 >
        const maxPaginasVisibles = 5;
        let startPage = Math.max(1, paginaActual - Math.floor(maxPaginasVisibles / 2));
        let endPage = Math.min(totalPaginas, startPage + maxPaginasVisibles - 1);

        if (endPage - startPage + 1 < maxPaginasVisibles) {
            startPage = Math.max(1, endPage - maxPaginasVisibles + 1);
        }

        if (startPage > 1) {
            agregarBotonPagina(1);
            if (startPage > 2) agregarPuntosSuspensivos();
        }

        for (let i = startPage; i <= endPage; i++) {
            agregarBotonPagina(i);
        }

        if (endPage < totalPaginas) {
            if (endPage < totalPaginas - 1) agregarPuntosSuspensivos();
            agregarBotonPagina(totalPaginas);
        }

        // Botón Siguiente (>)
        const liSiguiente = document.createElement("li");
        liSiguiente.className = `page-item ${paginaActual === totalPaginas ? 'disabled' : ''}`;
        liSiguiente.innerHTML = `<a class="page-link border-0 text-secondary px-3" href="#" aria-label="Siguiente"><i class="bi bi-chevron-right"></i></a>`;
        liSiguiente.addEventListener("click", (e) => {
            e.preventDefault();
            if (paginaActual < totalPaginas) mostrarPagina(paginaActual + 1);
        });
        navPaginacion.appendChild(liSiguiente);
    }

    function agregarBotonPagina(num) {
        const li = document.createElement("li");
        li.className = `page-item ${num === paginaActual ? 'active' : ''}`;

        const a = document.createElement("a");
        a.className = `page-link border-0 fw-semibold px-3 ${num === paginaActual ? 'bg-navy text-white shadow-sm' : 'text-dark'}`;
        a.href = "#";
        a.textContent = num;

        a.addEventListener("click", (e) => {
            e.preventDefault();
            mostrarPagina(num);
        });

        li.appendChild(a);
        navPaginacion.appendChild(li);
    }

    function agregarPuntosSuspensivos() {
        const li = document.createElement("li");
        li.className = "page-item disabled";
        li.innerHTML = `<span class="page-link border-0 text-muted px-2">...</span>`;
        navPaginacion.appendChild(li);
    }

    // Inicializar la tabla en la página 1
    mostrarPagina(1);
});