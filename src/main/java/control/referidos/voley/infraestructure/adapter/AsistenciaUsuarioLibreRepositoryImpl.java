package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.app.repository.AsistenciaUsuarioLibreRepository;
import control.referidos.voley.infraestructure.entity.AsistenciaUsuarioLibre;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public class AsistenciaUsuarioLibreRepositoryImpl implements AsistenciaUsuarioLibreRepository {

    private final AsistenciaUsuarioLibreCrudRepository asistenciaUsuarioLibreCrudRepository;

    public AsistenciaUsuarioLibreRepositoryImpl(AsistenciaUsuarioLibreCrudRepository asistenciaUsuarioLibreCrudRepository) {
        this.asistenciaUsuarioLibreCrudRepository = asistenciaUsuarioLibreCrudRepository;
    }

    @Override
    public List<AsistenciaUsuarioLibre> findByPatrocinadorAndContabilizadoParaPuntoFalse(Usuario patrocinador) {
        return asistenciaUsuarioLibreCrudRepository.findByPatrocinadorAndContabilizadoParaPuntoFalse(patrocinador);
    }

    @Override
    public List<AsistenciaUsuarioLibre> findByUsuarioLibreAndFechaAsistenciaBetween(Usuario usuarioLibre, LocalDate inicio, LocalDate fin) {
        return asistenciaUsuarioLibreCrudRepository.findByUsuarioLibreAndFechaAsistenciaBetween(usuarioLibre, inicio, fin);
    }

    @Override
    public long countByPatrocinadorAndUsuarioLibreAndContabilizadoParaPuntoFalse(Usuario patrocinador, Usuario usuarioLibre) {
        return asistenciaUsuarioLibreCrudRepository.countByPatrocinadorAndUsuarioLibreAndContabilizadoParaPuntoFalse(patrocinador, usuarioLibre);
    }

    @Override
    public AsistenciaUsuarioLibre save(AsistenciaUsuarioLibre asistencia) {
        return asistenciaUsuarioLibreCrudRepository.save(asistencia);
    }

    @Override
    @Transactional
    public void deleteAll(List<AsistenciaUsuarioLibre> asistencias) {
        asistenciaUsuarioLibreCrudRepository.deleteAll(asistencias);
    }
}