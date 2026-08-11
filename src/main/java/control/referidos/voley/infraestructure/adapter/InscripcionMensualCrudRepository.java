package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.infraestructure.entity.InscripcionMensual;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface InscripcionMensualCrudRepository extends CrudRepository<InscripcionMensual, Long> {
    Optional<InscripcionMensual> findByUsuarioAndPeriodoMes(Usuario usuario, String periodoMes);
    List<InscripcionMensual> findByUsuario(Usuario usuario);
    List<InscripcionMensual> findAll();
    boolean existsByUsuarioAndPeriodoMesAndActivoTrue(Usuario usuario, String periodoMes);
}