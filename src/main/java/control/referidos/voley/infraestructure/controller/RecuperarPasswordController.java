package control.referidos.voley.infraestructure.controller;

import control.referidos.voley.app.service.NotificacionService;
import control.referidos.voley.app.service.UsuarioService;
import control.referidos.voley.infraestructure.entity.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Controller
@RequestMapping("/recuperar")
public class RecuperarPasswordController {

    private final UsuarioService usuarioService;
    private final NotificacionService notificacionService;

    public RecuperarPasswordController(UsuarioService usuarioService, NotificacionService notificacionService) {
        this.usuarioService = usuarioService;
        this.notificacionService = notificacionService;
    }

    // Paso 1: Ingreso de DNI
    @GetMapping
    public String paso1Dni() {
        return "recuperar/paso1-dni";
    }

    @PostMapping("/validar-dni")
    public String validarDni(@RequestParam String dni, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.findByDni(dni.trim());
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El DNI ingresado no está registrado.");
            return "redirect:/recuperar";
        }

        Usuario usuario = usuarioOpt.get();
        session.setAttribute("recuperar_usuario_id", usuario.getId());
        return "redirect:/recuperar/opciones";
    }

    // Paso 2: Mostrar Correo Enmascarado y Enviar Código
    @GetMapping("/opciones")
    public String paso2Opciones(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("recuperar_usuario_id");
        if (usuarioId == null) return "redirect:/recuperar";

        Usuario usuario = usuarioService.findById(usuarioId).orElseThrow();
        model.addAttribute("emailOculto", enmascararEmail(usuario.getEmail()));
        return "recuperar/paso2-opciones";
    }

    @PostMapping("/enviar-codigo")
    public String enviarCodigo(HttpSession session, RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("recuperar_usuario_id");
        if (usuarioId == null) return "redirect:/recuperar";

        Usuario usuario = usuarioService.findById(usuarioId).orElseThrow();

        // Generar código aleatorio de 4 dígitos (p.ej: 4829)
        String codigo = String.format("%04d", new Random().nextInt(10000));

        session.setAttribute("recuperar_codigo", codigo);
        session.setAttribute("recuperar_expiracion", LocalDateTime.now().plusMinutes(5));

        try {
            notificacionService.enviarEmailRecuperacion(usuario.getEmail(), usuario.getNombre(), codigo);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error al enviar el correo electrónico. Intente de nuevo.");
            return "redirect:/recuperar/opciones";
        }

        return "redirect:/recuperar/validar-codigo";
    }

    // Paso 3: Validar Código de 4 dígitos
    @GetMapping("/validar-codigo")
    public String paso3ValidarCodigo(HttpSession session) {
        if (session.getAttribute("recuperar_codigo") == null) return "redirect:/recuperar";
        return "recuperar/paso3-codigo";
    }

    @PostMapping("/validar-codigo")
    public String verificarCodigo(@RequestParam String codigo, HttpSession session, RedirectAttributes redirectAttributes) {
        String codigoValido = (String) session.getAttribute("recuperar_codigo");
        LocalDateTime expiracion = (LocalDateTime) session.getAttribute("recuperar_expiracion");

        if (codigoValido == null || expiracion == null || LocalDateTime.now().isAfter(expiracion)) {
            redirectAttributes.addFlashAttribute("error", "El código ha expirado o es inválido. Solicita uno nuevo.");
            return "redirect:/recuperar/opciones";
        }

        if (!codigoValido.equals(codigo.trim())) {
            redirectAttributes.addFlashAttribute("error", "El código ingresado es incorrecto.");
            return "redirect:/recuperar/validar-codigo";
        }

        session.setAttribute("recuperar_autorizado", true);
        return "redirect:/recuperar/cambiar-password";
    }

    // Paso 4: Cambiar Contraseña
    @GetMapping("/cambiar-password")
    public String paso4CambiarPassword(HttpSession session) {
        Boolean autorizado = (Boolean) session.getAttribute("recuperar_autorizado");
        if (autorizado == null || !autorizado) return "redirect:/recuperar";
        return "recuperar/paso4-password";
    }

    @PostMapping("/cambiar-password")
    public String guardarPassword(@RequestParam String nuevaPassword, HttpSession session, RedirectAttributes redirectAttributes) {
        Long usuarioId = (Long) session.getAttribute("recuperar_usuario_id");
        Boolean autorizado = (Boolean) session.getAttribute("recuperar_autorizado");

        if (usuarioId == null || autorizado == null || !autorizado) return "redirect:/recuperar";

        Usuario usuario = usuarioService.findById(usuarioId).orElseThrow();
        usuario.setPassword(nuevaPassword);
        usuarioService.save(usuario);

        // Limpieza de sesión
        session.removeAttribute("recuperar_usuario_id");
        session.removeAttribute("recuperar_codigo");
        session.removeAttribute("recuperar_expiracion");
        session.removeAttribute("recuperar_autorizado");

        redirectAttributes.addFlashAttribute("exito", "Contraseña actualizada exitosamente. Inicia sesión.");
        return "redirect:/usuarios/login";
    }

    private String enmascararEmail(String email) {
        if (email == null || !email.contains("@")) return "*****";
        String[] partes = email.split("@");
        String usuario = partes[0];
        if (usuario.length() <= 2) return "**@" + partes[1];
        return usuario.substring(0, 2) + "****@" + partes[1];
    }
}