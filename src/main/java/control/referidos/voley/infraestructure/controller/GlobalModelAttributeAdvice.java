package control.referidos.voley.infraestructure.controller;

import control.referidos.voley.app.service.NotifiService;
import control.referidos.voley.infraestructure.entity.Rol;
import control.referidos.voley.infraestructure.entity.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributeAdvice {

    private final NotifiService notifiService;

    public GlobalModelAttributeAdvice(NotifiService notifiService) {
        this.notifiService = notifiService;
    }

    @ModelAttribute("conteoNotificacionesNoLeidas")
    public long obtenerConteoNotificaciones(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario != null && usuario.getRol() == Rol.ADMIN) {
            return notifiService.contarNoLeidas();
        }
        return 0;
    }
}