package control.referidos.voley.app.service;

import control.referidos.voley.app.repository.UsuarioRepository;
import control.referidos.voley.infraestructure.entity.Rol;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.util.List;
import java.util.Optional;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> findAll() { return usuarioRepository.findAll(); }
    public List<Usuario> findClientesSinAdmin() { return usuarioRepository.findByRolNot(Rol.ADMIN); }
    public Optional<Usuario> findById(Long id) { return usuarioRepository.findById(id); }
    public Optional<Usuario> findByEmail(String email) { return usuarioRepository.findByEmail(email); }
    public Optional<Usuario> findByDni(String dni) { return usuarioRepository.findByDni(dni); }
    public Optional<Usuario> findByCodigoReferido(String codigo) { return usuarioRepository.findByCodigoReferido(codigo); }
    public Usuario save(Usuario usuario) { return usuarioRepository.save(usuario); }
    public Optional<Usuario> findByEmailOrDni(String email, String dni) { return usuarioRepository.findByEmailOrDni(email, dni); }
    public void deleteById(Long id) { usuarioRepository.deleteById(id); }
    public boolean existsByEmail(String email) { return usuarioRepository.existsByEmail(email); }
    public boolean existsByDni(String dni) { return usuarioRepository.existsByDni(dni); }
    public boolean existsByCodigoReferido(String codigo) { return usuarioRepository.existsByCodigoReferido(codigo); }
    public boolean isTableEmpty() { return usuarioRepository.count() == 0; }

    public String generarCodigoUnico(String nombre) {
        String codigo;
        do {
            String randomNum = String.valueOf((int) (Math.random() * 90000 + 10000));
            String prefix = (nombre != null && nombre.length() >= 3) ? nombre.substring(0, 3).toUpperCase() : "USR";
            codigo = prefix + randomNum;
        } while (usuarioRepository.existsByCodigoReferido(codigo));
        return codigo;
    }
}