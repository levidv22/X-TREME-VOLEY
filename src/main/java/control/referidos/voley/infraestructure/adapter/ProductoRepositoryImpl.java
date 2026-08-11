package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.app.repository.ProductoRepository;
import control.referidos.voley.infraestructure.entity.Producto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ProductoRepositoryImpl implements ProductoRepository {

    private final ProductoCrudRepository productoCrudRepository;

    public ProductoRepositoryImpl(ProductoCrudRepository productoCrudRepository) {
        this.productoCrudRepository = productoCrudRepository;
    }

    @Override
    public List<Producto> findAll() {
        return (List<Producto>) productoCrudRepository.findAll();
    }

    @Override
    public Producto findById(Long id) {
        return productoCrudRepository.findById(id).orElse(null);
    }

    @Override
    public List<Producto> findByStockGreaterThan(int stock) {
        return productoCrudRepository.findByStockGreaterThan(stock);
    }

    @Override
    public Producto save(Producto producto) {
        return productoCrudRepository.save(producto);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productoCrudRepository.deleteById(id);
    }
}