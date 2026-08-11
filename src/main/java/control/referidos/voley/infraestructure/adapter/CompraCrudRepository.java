package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.infraestructure.entity.Compra;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDate;
import java.util.List;

public interface CompraCrudRepository extends CrudRepository<Compra, Long> {
    List<Compra> findByUsuario(Usuario usuario);
    List<Compra> findByFechaCompraBetween(LocalDate inicio, LocalDate fin);
}