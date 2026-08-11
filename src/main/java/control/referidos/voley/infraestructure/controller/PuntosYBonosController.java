package control.referidos.voley.infraestructure.controller;

import control.referidos.voley.app.service.PagoBonoService;
import control.referidos.voley.app.service.PuntosService;
import control.referidos.voley.app.service.UsuarioService;
import control.referidos.voley.infraestructure.entity.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/puntos-bonos")
public class PuntosYBonosController {

    private final PuntosService puntosService;
    private final PagoBonoService pagoBonoService;
    private final UsuarioService usuarioService;

    public PuntosYBonosController(PuntosService puntosService, PagoBonoService pagoBonoService, UsuarioService usuarioService) {
        this.puntosService = puntosService;
        this.pagoBonoService = pagoBonoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String verPuntosYBonos(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.findById(usuarioLogueado.getId());
        if (usuarioOpt.isPresent()) {
            usuarioLogueado = usuarioOpt.get();
            session.setAttribute("usuarioLogueado", usuarioLogueado);
        }

        model.addAttribute("puntos", puntosService.findByUsuario(usuarioLogueado));
        model.addAttribute("bonos", pagoBonoService.findByUsuario(usuarioLogueado));
        model.addAttribute("usuario", usuarioLogueado);

        return "puntos-bonos/resumen";
    }
}