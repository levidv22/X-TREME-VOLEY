package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.infraestructure.entity.PagoBono;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDate;
import java.util.List;

public interface PagoBonoCrudRepository extends CrudRepository<PagoBono, Long> {
    List<PagoBono> findByUsuario(Usuario usuario);
    List<PagoBono> findByFechaPagoBetween(LocalDate inicio, LocalDate fin);
    List<PagoBono> findByInscripcionMensualId(Long inscripcionId);
    List<PagoBono> deleteByInscripcionMensualId(Long inscripcionId);
}