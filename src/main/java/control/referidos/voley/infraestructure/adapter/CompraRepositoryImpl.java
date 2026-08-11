package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.app.repository.CompraRepository;
import control.referidos.voley.infraestructure.entity.Compra;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public class CompraRepositoryImpl implements CompraRepository {

    private final CompraCrudRepository compraCrudRepository;

    public CompraRepositoryImpl(CompraCrudRepository compraCrudRepository) {
        this.compraCrudRepository = compraCrudRepository;
    }

    @Override
    public List<Compra> findByUsuario(Usuario usuario) {
        return compraCrudRepository.findByUsuario(usuario);
    }

    @Override
    public List<Compra> findByFechaCompraBetween(LocalDate inicio, LocalDate fin) {
        return compraCrudRepository.findByFechaCompraBetween(inicio, fin);
    }

    @Override
    public Compra save(Compra compra) {
        return compraCrudRepository.save(compra);
    }

    @Override
    public Compra findById(Long id) {
        return compraCrudRepository.findById(id).orElse(null);
    }
}