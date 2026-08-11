package control.referidos.voley.app.repository;

import control.referidos.voley.infraestructure.entity.Membresia;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.util.Optional;

public interface MembresiaRepository {
    Optional<Membresia> findByUsuario(Usuario usuario);
    boolean existsByUsuarioAndActivaTrue(Usuario usuario);
    Membresia save(Membresia membresia);
}