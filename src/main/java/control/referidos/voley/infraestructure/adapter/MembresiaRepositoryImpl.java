package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.app.repository.MembresiaRepository;
import control.referidos.voley.infraestructure.entity.Membresia;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MembresiaRepositoryImpl implements MembresiaRepository {

    private final MembresiaCrudRepository membresiaCrudRepository;

    public MembresiaRepositoryImpl(MembresiaCrudRepository membresiaCrudRepository) {
        this.membresiaCrudRepository = membresiaCrudRepository;
    }

    @Override
    public Optional<Membresia> findByUsuario(Usuario usuario) {
        return membresiaCrudRepository.findByUsuario(usuario);
    }

    @Override
    public boolean existsByUsuarioAndActivaTrue(Usuario usuario) {
        return membresiaCrudRepository.existsByUsuarioAndActivaTrue(usuario);
    }

    @Override
    public Membresia save(Membresia membresia) {
        return membresiaCrudRepository.save(membresia);
    }
}