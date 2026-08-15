// Función para expandir o colapsar dinámicamente los sub-niveles del árbol
function toggleSubTree(element) {
    element.classList.toggle('collapsed');

    // Busca la rama contenedora para ubicar su sub-árbol inmediato
    const treeBranch = element.closest('.tree-branch');
    if (treeBranch) {
        const subTree = treeBranch.querySelector(':scope > .sub-tree');
        if (subTree) {
            subTree.classList.toggle('show');
        }
    }
}