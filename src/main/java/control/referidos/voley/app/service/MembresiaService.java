package control.referidos.voley.app.service;

import control.referidos.voley.app.repository.MembresiaRepository;
import control.referidos.voley.infraestructure.entity.Membresia;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.util.Optional;

public class MembresiaService {

    private final MembresiaRepository membresiaRepository;

    public MembresiaService(MembresiaRepository membresiaRepository) {
        this.membresiaRepository = membresiaRepository;
    }

    public Optional<Membresia> findByUsuario(Usuario usuario) {
        return membresiaRepository.findByUsuario(usuario);
    }

    public boolean existsByUsuarioAndActivaTrue(Usuario usuario) {
        return membresiaRepository.existsByUsuarioAndActivaTrue(usuario);
    }

    public Membresia save(Membresia membresia) {
        return membresiaRepository.save(membresia);
    }
}