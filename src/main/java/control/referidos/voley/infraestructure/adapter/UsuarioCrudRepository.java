package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.infraestructure.entity.Rol;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioCrudRepository extends CrudRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByDni(String dni);
    Optional<Usuario> findByEmailOrDni(String email, String dni);
    Optional<Usuario> findByCodigoReferido(String codigoReferido);
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);
    List<Usuario> findByRolNot(Rol rol);
    boolean existsByCodigoReferido(String codigoReferido);
}