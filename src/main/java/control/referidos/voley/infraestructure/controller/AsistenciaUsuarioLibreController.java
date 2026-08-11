package control.referidos.voley.infraestructure.controller;

import control.referidos.voley.app.service.AsistenciaUsuarioLibreService;
import control.referidos.voley.app.service.UsuarioService;
import control.referidos.voley.infraestructure.entity.AsistenciaUsuarioLibre;
import control.referidos.voley.infraestructure.entity.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/asistencias")
public class AsistenciaUsuarioLibreController {

    private final AsistenciaUsuarioLibreService asistenciaService;
    private final UsuarioService usuarioService;

    public AsistenciaUsuarioLibreController(AsistenciaUsuarioLibreService asistenciaService, UsuarioService usuarioService) {
        this.asistenciaService = asistenciaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarAsistenciasPendientes(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.findById(usuarioLogueado.getId());
        if (usuarioOpt.isPresent()) {
            usuarioLogueado = usuarioOpt.get();
            session.setAttribute("usuarioLogueado", usuarioLogueado);
        }

        List<AsistenciaUsuarioLibre> pendientes = asistenciaService.findByPatrocinadorAndContabilizadoParaPuntoFalse(usuarioLogueado);
        model.addAttribute("asistenciasPendientes", pendientes);
        model.addAttribute("usuario", usuarioLogueado);
        return "asistencias/lista";
    }

    @PostMapping("/registrar")
    public String registrarAsistencia(@RequestParam Long usuarioLibreId,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Optional<Usuario> usuarioLibreOpt = usuarioService.findById(usuarioLibreId);
        if (usuarioLibreOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El usuario libre especificado no existe.");
            return "redirect:/asistencias";
        }

        AsistenciaUsuarioLibre asistencia = new AsistenciaUsuarioLibre();
        asistencia.setUsuarioLibre(usuarioLibreOpt.get());
        asistencia.setPatrocinador(usuarioLogueado);
        asistencia.setFechaAsistencia(LocalDate.now());
        asistencia.setContabilizadoParaPunto(false);

        asistenciaService.save(asistencia);

        redirectAttributes.addFlashAttribute("exito", "Asistencia registrada exitosamente.");
        return "redirect:/asistencias";
    }
}