package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.infraestructure.entity.AsistenciaUsuarioLibre;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDate;
import java.util.List;

public interface AsistenciaUsuarioLibreCrudRepository extends CrudRepository<AsistenciaUsuarioLibre, Long> {
    List<AsistenciaUsuarioLibre> findByPatrocinadorAndContabilizadoParaPuntoFalse(Usuario patrocinador);
    List<AsistenciaUsuarioLibre> findByUsuarioLibreAndFechaAsistenciaBetween(Usuario usuarioLibre, LocalDate inicio, LocalDate fin);
    long countByPatrocinadorAndUsuarioLibreAndContabilizadoParaPuntoFalse(Usuario patrocinador, Usuario usuarioLibre);
}