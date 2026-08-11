// Función para expandir o colapsar dinámicamente los sub-niveles de la red de un usuario en específico
function toggleSubTree(element) {
    element.classList.toggle('collapsed');
    let nodeCard = element.closest('.tree-branch');
    let subTree = nodeCard.querySelector('.sub-tree');
    if (subTree) {
        subTree.classList.toggle('show');
    }
}