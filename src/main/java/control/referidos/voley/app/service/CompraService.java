package control.referidos.voley.app.service;

import control.referidos.voley.app.repository.CompraRepository;
import control.referidos.voley.infraestructure.entity.Compra;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.time.LocalDate;
import java.util.List;

public class CompraService {

    private final CompraRepository compraRepository;

    public CompraService(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    public List<Compra> findByUsuario(Usuario usuario) {
        return compraRepository.findByUsuario(usuario);
    }

    public List<Compra> findByFechaCompraBetween(LocalDate inicio, LocalDate fin) {
        return compraRepository.findByFechaCompraBetween(inicio, fin);
    }

    public Compra save(Compra compra) {
        return compraRepository.save(compra);
    }

    public Compra findById(Long id) {
        return compraRepository.findById(id);
    }
}