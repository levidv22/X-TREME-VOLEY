package control.referidos.voley.app.repository;

import control.referidos.voley.infraestructure.entity.AsistenciaUsuarioLibre;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.time.LocalDate;
import java.util.List;

public interface AsistenciaUsuarioLibreRepository {
    List<AsistenciaUsuarioLibre> findByPatrocinadorAndContabilizadoParaPuntoFalse(Usuario patrocinador);
    List<AsistenciaUsuarioLibre> findByUsuarioLibreAndFechaAsistenciaBetween(Usuario usuarioLibre, LocalDate inicio, LocalDate fin);
    long countByPatrocinadorAndUsuarioLibreAndContabilizadoParaPuntoFalse(Usuario patrocinador, Usuario usuarioLibre);
    AsistenciaUsuarioLibre save(AsistenciaUsuarioLibre asistencia);
    void deleteAll(List<AsistenciaUsuarioLibre> asistencias);
}