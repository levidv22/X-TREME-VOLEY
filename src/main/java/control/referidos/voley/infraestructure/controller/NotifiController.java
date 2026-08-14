package control.referidos.voley.infraestructure.controller;

import control.referidos.voley.app.service.NotifiService;
import control.referidos.voley.infraestructure.entity.Notificacion;
import control.referidos.voley.infraestructure.entity.Rol;
import control.referidos.voley.infraestructure.entity.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notificaciones")
public class NotifiController {

    private final NotifiService notifiService;

    public NotifiController(NotifiService notifiService) {
        this.notifiService = notifiService;
    }

    @GetMapping
    public String verNotificaciones(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || usuarioLogueado.getRol() != Rol.ADMIN) {
            return "redirect:/usuarios/login";
        }

        model.addAttribute("notificaciones", notifiService.listarTodas());
        return "notificaciones/lista"; // Asegúrate de ajustar la ruta si tu plantilla tiene otro nombre
    }

    @GetMapping("/ir/{id}")
    public String irANotificacion(@PathVariable Long id, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || usuarioLogueado.getRol() != Rol.ADMIN) {
            return "redirect:/usuarios/login";
        }

        notifiService.marcarComoLeida(id);
        Notificacion notificacion = notifiService.listarTodas().stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (notificacion != null && notificacion.getUrlDestino() != null) {
            return "redirect:" + notificacion.getUrlDestino();
        }
        return "redirect:/notificaciones";
    }

    // NUEVO: Endpoint para marcar todas como leídas
    @PostMapping("/marcar-todas-leidas")
    public String marcarTodasComoLeidas(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || usuarioLogueado.getRol() != Rol.ADMIN) {
            return "redirect:/usuarios/login";
        }

        try {
            notifiService.marcarTodasComoLeidas();
            redirectAttributes.addFlashAttribute("exito", "Todas las notificaciones se marcaron como leídas.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la solicitud.");
        }

        return "redirect:/notificaciones";
    }

    // NUEVO: Endpoint para eliminar una notificación individual
    @PostMapping("/eliminar")
    public String eliminarNotificacion(@RequestParam("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || usuarioLogueado.getRol() != Rol.ADMIN) {
            return "redirect:/usuarios/login";
        }

        try {
            notifiService.eliminarNotificacion(id);
            redirectAttributes.addFlashAttribute("exito", "Notificación eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la notificación.");
        }

        return "redirect:/notificaciones";
    }
}
