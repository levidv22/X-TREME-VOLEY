package control.referidos.voley.app.repository;

import control.referidos.voley.infraestructure.entity.InscripcionMensual;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface InscripcionMensualRepository {
    Optional<InscripcionMensual> findByUsuarioAndPeriodoMes(Usuario usuario, String periodoMes);
    List<InscripcionMensual> findByUsuario(Usuario usuario);
    List<InscripcionMensual> findAll();
    boolean existsByUsuarioAndPeriodoMesAndActivoTrue(Usuario usuario, String periodoMes);
    InscripcionMensual save(InscripcionMensual inscripcion);
    Optional<InscripcionMensual> findById(Long id);
    void deleteById(Long id);
}