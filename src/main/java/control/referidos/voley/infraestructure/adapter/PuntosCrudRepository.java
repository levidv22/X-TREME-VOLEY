package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.infraestructure.entity.Puntos;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PuntosCrudRepository extends CrudRepository<Puntos, Long> {
    Optional<Puntos> findByUsuarioAndPeriodoMes(Usuario usuario, LocalDate periodoMes);
    List<Puntos> findByUsuario(Usuario usuario);
}