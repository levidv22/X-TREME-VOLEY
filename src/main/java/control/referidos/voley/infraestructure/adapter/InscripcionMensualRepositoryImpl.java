package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.app.repository.InscripcionMensualRepository;
import control.referidos.voley.infraestructure.entity.InscripcionMensual;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class InscripcionMensualRepositoryImpl implements InscripcionMensualRepository {

    private final InscripcionMensualCrudRepository inscripcionMensualCrudRepository;

    public InscripcionMensualRepositoryImpl(InscripcionMensualCrudRepository inscripcionMensualCrudRepository) {
        this.inscripcionMensualCrudRepository = inscripcionMensualCrudRepository;
    }

    @Override
    public List<InscripcionMensual> findAll() {
        return (List<InscripcionMensual>) inscripcionMensualCrudRepository.findAll();
    }

    @Override
    public Optional<InscripcionMensual> findById(Long id) {
        return inscripcionMensualCrudRepository.findById(id);
    }

    @Override
    public Optional<InscripcionMensual> findByUsuarioAndPeriodoMes(Usuario usuario, String periodoMes) {
        return inscripcionMensualCrudRepository.findByUsuarioAndPeriodoMes(usuario, periodoMes);
    }

    @Override
    public List<InscripcionMensual> findByUsuario(Usuario usuario) {
        return inscripcionMensualCrudRepository.findByUsuario(usuario);
    }

    @Override
    public boolean existsByUsuarioAndPeriodoMesAndActivoTrue(Usuario usuario, String periodoMes) {
        return inscripcionMensualCrudRepository.existsByUsuarioAndPeriodoMesAndActivoTrue(usuario, periodoMes);
    }

    @Override
    public InscripcionMensual save(InscripcionMensual inscripcion) {
        return inscripcionMensualCrudRepository.save(inscripcion);
    }
    @Override
    @Transactional
    public void deleteById(Long id) {
        inscripcionMensualCrudRepository.deleteById(id);
    }
}