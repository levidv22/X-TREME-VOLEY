package control.referidos.voley.infraestructure.controller;

import control.referidos.voley.app.service.HorarioReservaVoleyService;
import control.referidos.voley.app.service.UsuarioService;
import control.referidos.voley.infraestructure.entity.HorarioReservaVoley;
import control.referidos.voley.infraestructure.entity.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/reservas")
public class HorarioReservaVoleyController {

    private final HorarioReservaVoleyService horarioReservaVoleyService;
    private final UsuarioService usuarioService;

    public HorarioReservaVoleyController(HorarioReservaVoleyService horarioReservaVoleyService, UsuarioService usuarioService) {
        this.horarioReservaVoleyService = horarioReservaVoleyService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/disponibles")
    public String listarDisponibles(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        model.addAttribute("reservasDisponibles", horarioReservaVoleyService.findByDisponibleTrue());
        model.addAttribute("usuario", usuarioLogueado);
        return "reservas/disponibles";
    }

    @GetMapping("/mis-reservas")
    public String listarReservasUsuario(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.findById(usuarioLogueado.getId());
        if (usuarioOpt.isPresent()) {
            usuarioLogueado = usuarioOpt.get();
            session.setAttribute("usuarioLogueado", usuarioLogueado);
        }

        model.addAttribute("misReservas", horarioReservaVoleyService.findByUsuario(usuarioLogueado));
        model.addAttribute("usuario", usuarioLogueado);
        return "reservas/mis-reservas";
    }

    @PostMapping("/reservar/{id}")
    public String reservarHorario(@PathVariable Long id,
                                  @RequestParam String contactoReserva,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        HorarioReservaVoley horario = horarioReservaVoleyService.findById(id);
        if (horario == null || !horario.isDisponible()) {
            redirectAttributes.addFlashAttribute("error", "El horario seleccionado ya no se encuentra disponible.");
            return "redirect:/reservas/disponibles";
        }

        horario.setDisponible(false);
        horario.setContactoReserva(contactoReserva);
        horario.setUsuario(usuarioLogueado);

        horarioReservaVoleyService.save(horario);

        redirectAttributes.addFlashAttribute("exito", "Reserva realizada exitosamente.");
        return "redirect:/reservas/mis-reservas";
    }
}