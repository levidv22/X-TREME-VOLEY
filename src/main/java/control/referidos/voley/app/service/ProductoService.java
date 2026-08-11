package control.referidos.voley.app.service;

import control.referidos.voley.app.repository.ProductoRepository;
import control.referidos.voley.infraestructure.entity.Producto;
import java.util.List;

public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Producto findById(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> findByStockGreaterThan(int stock) {
        return productoRepository.findByStockGreaterThan(stock);
    }

    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }
}