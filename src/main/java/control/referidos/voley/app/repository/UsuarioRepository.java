package control.referidos.voley.app.repository;

import control.referidos.voley.infraestructure.entity.Rol;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    List<Usuario> findAll();
    List<Usuario> findByRolNot(Rol rol);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByDni(String dni);
    Optional<Usuario> findByEmailOrDni(String email, String dni);
    Optional<Usuario> findByCodigoReferido(String codigoReferido);
    Usuario save(Usuario usuario);
    void deleteById(Long id);
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);
    boolean existsByCodigoReferido(String codigoReferido);
    long count();
}