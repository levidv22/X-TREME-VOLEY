package control.referidos.voley.app.service;

import control.referidos.voley.app.repository.AsistenciaUsuarioLibreRepository;
import control.referidos.voley.infraestructure.entity.AsistenciaUsuarioLibre;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.time.LocalDate;
import java.util.List;

public class AsistenciaUsuarioLibreService {

    private final AsistenciaUsuarioLibreRepository asistenciaUsuarioLibreRepository;

    public AsistenciaUsuarioLibreService(AsistenciaUsuarioLibreRepository asistenciaUsuarioLibreRepository) {
        this.asistenciaUsuarioLibreRepository = asistenciaUsuarioLibreRepository;
    }

    public List<AsistenciaUsuarioLibre> findByPatrocinadorAndContabilizadoParaPuntoFalse(Usuario patrocinador) {
        return asistenciaUsuarioLibreRepository.findByPatrocinadorAndContabilizadoParaPuntoFalse(patrocinador);
    }

    public List<AsistenciaUsuarioLibre> findByUsuarioLibreAndFechaAsistenciaBetween(Usuario usuarioLibre, LocalDate inicio, LocalDate fin) {
        return asistenciaUsuarioLibreRepository.findByUsuarioLibreAndFechaAsistenciaBetween(usuarioLibre, inicio, fin);
    }

    public long countByPatrocinadorAndUsuarioLibreAndContabilizadoParaPuntoFalse(Usuario patrocinador, Usuario usuarioLibre) {
        return asistenciaUsuarioLibreRepository.countByPatrocinadorAndUsuarioLibreAndContabilizadoParaPuntoFalse(patrocinador, usuarioLibre);
    }

    public AsistenciaUsuarioLibre save(AsistenciaUsuarioLibre asistencia) {
        return asistenciaUsuarioLibreRepository.save(asistencia);
    }

    public void deleteAll(List<AsistenciaUsuarioLibre> asistencias) {
        asistenciaUsuarioLibreRepository.deleteAll(asistencias);
    }
}