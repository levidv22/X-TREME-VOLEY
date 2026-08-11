package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.infraestructure.entity.Membresia;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface MembresiaCrudRepository extends CrudRepository<Membresia, Long> {
    Optional<Membresia> findByUsuario(Usuario usuario);
    boolean existsByUsuarioAndActivaTrue(Usuario usuario);
}