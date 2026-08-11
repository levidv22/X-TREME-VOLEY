package control.referidos.voley.app.repository;

import control.referidos.voley.infraestructure.entity.Producto;
import java.util.List;

public interface ProductoRepository {
    List<Producto> findAll();
    Producto findById(Long id);
    List<Producto> findByStockGreaterThan(int stock);
    Producto save(Producto producto);
    void deleteById(Long id);
}