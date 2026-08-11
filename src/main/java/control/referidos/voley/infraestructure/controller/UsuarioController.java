package control.referidos.voley.infraestructure.controller;

import control.referidos.voley.app.repository.NotificacionRangoRepository;
import control.referidos.voley.app.repository.RedAfiliadosRepository;
import control.referidos.voley.app.service.*;
import control.referidos.voley.infraestructure.entity.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RedAfiliadosRepository redAfiliadosRepository;
    private final RedAfiliadosService redAfiliadosService;
    private final InscripcionMensualService inscripcionMensualService;
    private final PuntosService puntosService;
    private final PagoBonoService pagoBonoService;
    private final NotificacionRangoRepository notificacionRangoRepository;

    public UsuarioController(UsuarioService usuarioService,
                             RedAfiliadosRepository redAfiliadosRepository,
                             RedAfiliadosService redAfiliadosService,
                             InscripcionMensualService inscripcionMensualService,
                             PuntosService puntosService,
                             PagoBonoService pagoBonoService,
                             NotificacionRangoRepository notificacionRangoRepository) {
        this.usuarioService = usuarioService;
        this.redAfiliadosRepository = redAfiliadosRepository;
        this.redAfiliadosService = redAfiliadosService;
        this.inscripcionMensualService = inscripcionMensualService;
        this.puntosService = puntosService;
        this.pagoBonoService = pagoBonoService;
        this.notificacionRangoRepository = notificacionRangoRepository;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuarios/lista";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(@RequestParam(value = "ref", required = false) String ref, Model model) {
        Usuario usuario = new Usuario();
        if (ref != null && !ref.trim().isEmpty()) {
            model.addAttribute("codigoPrecargado", ref.trim());
        }
        model.addAttribute("esPrimerUsuario", usuarioService.isTableEmpty());
        model.addAttribute("usuario", usuario);
        return "usuarios/registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario,
                                   @RequestParam(value = "codigoReferidoIngresado", required = false) String codigoReferidoIngresado,
                                   @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto,
                                   RedirectAttributes redirectAttributes) {

        boolean esPrimerUsuario = usuarioService.isTableEmpty();

        if (usuarioService.existsByEmail(usuario.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está registrado.");
            return "redirect:/usuarios/registro";
        }
        if (usuario.getDni() != null && usuarioService.existsByDni(usuario.getDni())) {
            redirectAttributes.addFlashAttribute("error", "El DNI ya está registrado.");
            return "redirect:/usuarios/registro";
        }

        Usuario patrocinador = null;

        if (esPrimerUsuario) {
            usuario.setRol(Rol.ADMIN);
        } else {
            usuario.setRol(Rol.USUARIO_LIBRE);

            // A partir del segundo usuario, el código de referido es obligatorio
            if (codigoReferidoIngresado == null || codigoReferidoIngresado.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El código de referido es obligatorio para registrarse.");
                return "redirect:/usuarios/registro";
            }

            Optional<Usuario> patrocinadorOpt = usuarioService.findByCodigoReferido(codigoReferidoIngresado.trim());
            if (patrocinadorOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El código de referido ingresado no existe.");
                return "redirect:/usuarios/registro";
            }
            patrocinador = patrocinadorOpt.get();
            if (patrocinador.getRol() == Rol.USUARIO_LIBRE) {
                long referidosActuales = redAfiliadosService.contarReferidosDirectos(patrocinador);
                if (referidosActuales >= 3) {
                    redirectAttributes.addFlashAttribute("error", "El patrocinador ya alcanzó el límite máximo de 3 referidos permitidos.");
                    return "redirect:/usuarios/registro";
                }
            }
        }

        usuario.setTipoRed(TipoUsuarioRed.LIBRE);
        usuario.setCodigoReferido(usuarioService.generarCodigoUnico(usuario.getNombre()));

        if (fileFoto != null && !fileFoto.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + fileFoto.getOriginalFilename();
                Path uploadPath = Paths.get("uploads/");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Files.copy(fileFoto.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                usuario.setFotoUrl("/uploads/" + fileName);
            } catch (IOException e) {
                usuario.setFotoUrl("/img/default.jpg");
            }
        } else {
            usuario.setFotoUrl("/img/default.jpg");
        }

        Usuario usuarioGuardado = usuarioService.save(usuario);

        // Si tiene patrocinador, registrar la relación directamente en nivel 1
        if (patrocinador != null) {
            RedAfiliados relacion = new RedAfiliados();
            relacion.setPatrocinador(patrocinador);
            relacion.setReferido(usuarioGuardado);
            relacion.setNivel(1);
            relacion.setEsDerrame(false);
            redAfiliadosRepository.save(relacion);
        }

        redirectAttributes.addFlashAttribute("exito", "Usuario registrado exitosamente.");
        return "redirect:/usuarios/login";
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "usuarios/login";
    }

    @PostMapping("/login")
    public String iniciarSesion(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.findByEmailOrDni(username, username);
        if (usuarioOpt.isPresent() && usuarioOpt.get().getPassword().equals(password)) {
            session.setAttribute("usuarioLogueado", usuarioOpt.get());
            return "redirect:/usuarios/perfil";
        }
        redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas.");
        return "redirect:/usuarios/login";
    }

    @GetMapping("/perfil")
    public String verPerfil(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Usuario usuarioActualizado = usuarioService.findById(usuarioLogueado.getId()).orElse(usuarioLogueado);

        // Si es usuario normal y subió de rango
        if (usuarioLogueado.getRangoActual() != null && usuarioActualizado.getRangoActual() != null) {
            if (!usuarioLogueado.getRangoActual().equals(usuarioActualizado.getRangoActual())) {
                model.addAttribute("nuevoRangoAlcanzado", usuarioActualizado.getRangoActual().name());
                model.addAttribute("premioRangoAlcanzado", obtenerPremioPorRango(usuarioActualizado.getRangoActual()));

                // Guardamos automáticamente la alerta pendiente para el admin
                NotificacionRango noti = new NotificacionRango();
                noti.setUsuario(usuarioActualizado);
                noti.setRangoAlcanzado(usuarioActualizado.getRangoActual().name().replace("_", " "));
                noti.setPremio(obtenerPremioPorRango(usuarioActualizado.getRangoActual()));
                noti.setFechaNotificacion(LocalDateTime.now());
                noti.setLeido(false);
                notificacionRangoRepository.save(noti);

                session.setAttribute("usuarioLogueado", usuarioActualizado);
            }
        }

        // Si el usuario logueado es ADMIN, enviamos las notificaciones pendientes a la vista
        if (usuarioActualizado.getRol().name().equals("ADMIN")) {
            List<NotificacionRango> notificacionesPendientes = notificacionRangoRepository.findByLeidoFalse();
            model.addAttribute("notificacionesAdmin", notificacionesPendientes);
        }

        model.addAttribute("usuario", usuarioActualizado);

        List<Puntos> listaPuntos = puntosService.findByUsuario(usuarioActualizado);
        int totalPuntos = listaPuntos.stream().mapToInt(Puntos::getPuntosTotalesAcumulados).sum();
        model.addAttribute("totalPuntos", totalPuntos);

        List<PagoBono> listaBonos = pagoBonoService.findByUsuario(usuarioActualizado);
        double totalBonos = listaBonos.stream().mapToDouble(PagoBono::getMonto).sum();
        model.addAttribute("totalBonos", totalBonos);

        model.addAttribute("inscripciones", inscripcionMensualService.findByUsuario(usuarioActualizado));

        return "usuarios/perfil";
    }

    @PostMapping("/notificar-rango")
    @ResponseBody
    public ResponseEntity<?> notificarRangoAdmin(HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }
        // Aquí puedes realizar cualquier validación adicional si deseas marcar que ya dio clic
        return ResponseEntity.ok().body("Notificación enviada con éxito");
    }

    @PostMapping("/marcar-leido/{id}")
    public String marcarNotificacionLeida(@PathVariable Long id) {
        Optional<NotificacionRango> notiOpt = notificacionRangoRepository.findById(id);
        if (notiOpt.isPresent()) {
            NotificacionRango noti = notiOpt.get();
            noti.setLeido(true);
            notificacionRangoRepository.save(noti);
        }
        return "redirect:/usuarios/perfil";
    }

    private String obtenerPremioPorRango(RangoCarrera rango) {
        switch (rango) {
            case LIDER: return "Reconocimiento y Agasajo";
            case MAESTRO: return "Paseo Provincial (2 personas)";
            case BRONCE: return "Regalo Sorpresa";
            case PLATA: return "Paseo Interprovincial (2 personas)";
            case ORO: return "Celular Redmi (Última Generación)";
            case ZAFIRO: return "Bono S/2,000";
            case DIAMANTE: return "Premio (Laptop)";
            case DIAMANTE_AZUL: return "Paseo Nacional (2 personas)";
            case DIAMANTE_NEGRO: return "Premio Moto Deportiva";
            case DIAMANTE_IMPERIAL: return "Resort Internacional (2 personas)";
            case STAR: return "Bono S/50,000";
            case GOLD_STAR: return "Zafari 5 días (2 personas)";
            case GOLDEN_STAR: return "Premio (Camioneta Ford)";
            case DIAMOND_STAR: return "Bono S/500,000";
            case XTREME_STAR: return "Bono S/1,000,000";
            default: return "¡Nuevos beneficios desbloqueados!";
        }
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/usuarios/login";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Optional<Usuario> usuarioOpt = usuarioService.findById(id);
        if (usuarioOpt.isPresent()) {
            model.addAttribute("usuario", usuarioOpt.get());
            return "usuarios/editar";
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/actualizar")
    public String actualizarUsuario(@ModelAttribute Usuario usuario,
                                    @RequestParam(value = "fileFoto", required = false) MultipartFile fileFoto,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioExistenteOpt = usuarioService.findById(usuario.getId());
        if (usuarioExistenteOpt.isPresent()) {
            Usuario usuarioDb = usuarioExistenteOpt.get();

            usuarioDb.setNombre(usuario.getNombre());
            usuarioDb.setApellido(usuario.getApellido());
            usuarioDb.setEmail(usuario.getEmail());
            usuarioDb.setDni(usuario.getDni());
            usuarioDb.setTelefono(usuario.getTelefono());
            usuarioDb.setDepartamento(usuario.getDepartamento());
            usuarioDb.setProvincia(usuario.getProvincia());
            usuarioDb.setDistrito(usuario.getDistrito());
            usuarioDb.setDireccion(usuario.getDireccion());
            usuarioDb.setFechaNacimiento(usuario.getFechaNacimiento());

            if (fileFoto != null && !fileFoto.isEmpty()) {
                try {
                    String fileName = System.currentTimeMillis() + "_" + fileFoto.getOriginalFilename();
                    Path uploadPath = Paths.get("uploads/");
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    Files.copy(fileFoto.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                    usuarioDb.setFotoUrl("/uploads/" + fileName);
                } catch (IOException e) {
                    // Mantener la foto anterior en caso de error
                }
            }

            usuarioService.save(usuarioDb);

            Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
            if (usuarioLogueado != null && usuarioLogueado.getId().equals(usuarioDb.getId())) {
                session.setAttribute("usuarioLogueado", usuarioDb);
            }
        }

        redirectAttributes.addFlashAttribute("exito", "Perfil actualizado exitosamente.");
        return "redirect:/usuarios/perfil";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return "redirect:/usuarios";
    }
}