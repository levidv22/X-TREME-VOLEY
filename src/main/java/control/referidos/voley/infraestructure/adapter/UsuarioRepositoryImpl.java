package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.app.repository.UsuarioRepository;
import control.referidos.voley.infraestructure.entity.Rol;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioCrudRepository usuarioCrudRepository;

    public UsuarioRepositoryImpl(UsuarioCrudRepository usuarioCrudRepository) {
        this.usuarioCrudRepository = usuarioCrudRepository;
    }

    @Override
    public List<Usuario> findAll() {
        return (List<Usuario>) usuarioCrudRepository.findAll();
    }

    @Override
    public List<Usuario> findByRolNot(Rol rol) {
        return usuarioCrudRepository.findByRolNot(rol);
    }

    @Override
    public Optional<Usuario> findByEmailOrDni(String email, String dni) {
        return usuarioCrudRepository.findByEmailOrDni(email, dni);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioCrudRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioCrudRepository.findByEmail(email);
    }

    @Override
    public Optional<Usuario> findByDni(String dni) {
        return usuarioCrudRepository.findByDni(dni);
    }

    @Override
    public Optional<Usuario> findByCodigoReferido(String codigoReferido) {
        return usuarioCrudRepository.findByCodigoReferido(codigoReferido);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioCrudRepository.save(usuario);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        usuarioCrudRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioCrudRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByDni(String dni) {
        return usuarioCrudRepository.existsByDni(dni);
    }

    @Override
    public boolean existsByCodigoReferido(String codigoReferido) {
        return usuarioCrudRepository.existsByCodigoReferido(codigoReferido);
    }

    @Override
    public long count() {
        return usuarioCrudRepository.count();
    }
}