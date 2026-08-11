package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.infraestructure.entity.Producto;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ProductoCrudRepository extends CrudRepository<Producto, Long> {
    List<Producto> findByStockGreaterThan(int stock);
}