package control.referidos.voley.app.repository;

import control.referidos.voley.infraestructure.entity.Compra;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.time.LocalDate;
import java.util.List;

public interface CompraRepository {
    List<Compra> findByUsuario(Usuario usuario);
    List<Compra> findByFechaCompraBetween(LocalDate inicio, LocalDate fin);
    Compra save(Compra compra);
    Compra findById(Long id);
}