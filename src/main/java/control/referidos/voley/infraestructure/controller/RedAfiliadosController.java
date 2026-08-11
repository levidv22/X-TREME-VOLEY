package control.referidos.voley.infraestructure.controller;

import control.referidos.voley.app.service.PuntosService;
import control.referidos.voley.app.service.RedAfiliadosService;
import control.referidos.voley.app.service.UsuarioService;
import control.referidos.voley.dto.NodoRedDto;
import control.referidos.voley.infraestructure.entity.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/red-afiliados")
public class RedAfiliadosController {

    private final RedAfiliadosService redAfiliadosService;
    private final UsuarioService usuarioService;
    private final PuntosService puntosService;

    public RedAfiliadosController(RedAfiliadosService redAfiliadosService, UsuarioService usuarioService, PuntosService puntosService) {
        this.redAfiliadosService = redAfiliadosService;
        this.usuarioService = usuarioService;
        this.puntosService = puntosService;
    }

    @GetMapping
    public String verRedAfiliados(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.findById(usuarioLogueado.getId());
        if (usuarioOpt.isPresent()) {
            usuarioLogueado = usuarioOpt.get();
            session.setAttribute("usuarioLogueado", usuarioLogueado);
        }

        List<NodoRedDto> referidosNivel1 = redAfiliadosService.obtenerReferidosNivelUno(usuarioLogueado, puntosService);

        model.addAttribute("referidosNivel1", referidosNivel1);
        model.addAttribute("arbolRed", redAfiliadosService.construirArbolRed(usuarioLogueado)); // Ahora ya calcula los puntos internamente
        model.addAttribute("usuario", usuarioLogueado);
        return "red/afiliados";
    }

    @GetMapping("/nivel-1")
    public String verReferidosNivelUno(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        List<NodoRedDto> referidosNivel1 = redAfiliadosService.obtenerReferidosNivelUno(usuarioLogueado, puntosService);

        model.addAttribute("referidosNivel1", referidosNivel1);
        model.addAttribute("usuario", usuarioLogueado);
        return "red/nivel-uno"; // Nombre de la nueva vista HTML
    }
}