package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.app.repository.RedAfiliadosRepository;
import control.referidos.voley.infraestructure.entity.RedAfiliados;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class RedAfiliadosRepositoryImpl implements RedAfiliadosRepository {

    private final RedAfiliadosCrudRepository redAfiliadosCrudRepository;

    public RedAfiliadosRepositoryImpl(RedAfiliadosCrudRepository redAfiliadosCrudRepository) {
        this.redAfiliadosCrudRepository = redAfiliadosCrudRepository;
    }

    @Override
    public List<RedAfiliados> findByPatrocinador(Usuario patrocinador) {
        return redAfiliadosCrudRepository.findByPatrocinador(patrocinador);
    }

    @Override
    public List<RedAfiliados> findByPatrocinadorAndNivel(Usuario patrocinador, int nivel) {
        return redAfiliadosCrudRepository.findByPatrocinadorAndNivel(patrocinador, nivel);
    }

    @Override
    public List<RedAfiliados> findByReferido(Usuario referido) {
        return redAfiliadosCrudRepository.findByReferido(referido);
    }

    @Override
    public boolean existsByPatrocinadorAndReferido(Usuario patrocinador, Usuario referido) {
        return redAfiliadosCrudRepository.existsByPatrocinadorAndReferido(patrocinador, referido);
    }

    @Override
    public long countByPatrocinador(Usuario patrocinador) {
        return redAfiliadosCrudRepository.countByPatrocinador(patrocinador);
    }

    @Override
    public RedAfiliados save(RedAfiliados redAfiliados) {
        return redAfiliadosCrudRepository.save(redAfiliados);
    }

    @Override
    @Transactional
    public void delete(RedAfiliados redAfiliados) {
        redAfiliadosCrudRepository.delete(redAfiliados);
    }
}