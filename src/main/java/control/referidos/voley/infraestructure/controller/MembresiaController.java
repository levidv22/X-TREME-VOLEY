package control.referidos.voley.infraestructure.controller;

import control.referidos.voley.app.service.MembresiaService;
import control.referidos.voley.app.service.UsuarioService;
import control.referidos.voley.infraestructure.entity.Membresia;
import control.referidos.voley.infraestructure.entity.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/membresias")
public class MembresiaController {

    private final MembresiaService membresiaService;
    private final UsuarioService usuarioService;

    public MembresiaController(MembresiaService membresiaService, UsuarioService usuarioService) {
        this.membresiaService = membresiaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String verMembresia(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.findById(usuarioLogueado.getId());
        if (usuarioOpt.isPresent()) {
            usuarioLogueado = usuarioOpt.get();
            session.setAttribute("usuarioLogueado", usuarioLogueado);
        }

        Optional<Membresia> membresiaOpt = membresiaService.findByUsuario(usuarioLogueado);
        model.addAttribute("membresia", membresiaOpt.orElse(null));
        model.addAttribute("usuario", usuarioLogueado);

        return "membresias/detalle";
    }

    @GetMapping("/comprar")
    public String mostrarFormularioCompra(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        if (membresiaService.existsByUsuarioAndActivaTrue(usuarioLogueado)) {
            redirectAttributes.addFlashAttribute("error", "Ya cuentas con una membresía activa.");
            return "redirect:/membresias";
        }

        model.addAttribute("costoAnual", 60.0);
        return "membresias/comprar";
    }

    @PostMapping("/comprar")
    public String procesarCompra(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        if (membresiaService.existsByUsuarioAndActivaTrue(usuarioLogueado)) {
            redirectAttributes.addFlashAttribute("error", "Ya cuentas con una membresía activa.");
            return "redirect:/membresias";
        }

        LocalDate hoy = LocalDate.now();
        int anioActual = hoy.getYear();
        // Vence en periodo de los últimos 5 días del año (ej. 27 de diciembre)
        LocalDate fechaFin = LocalDate.of(anioActual, 12, 27);

        Membresia membresia = new Membresia();
        membresia.setFechaInicio(hoy);
        membresia.setFechaFin(fechaFin);
        membresia.setCostoAnual(60.0);
        membresia.setActiva(true);
        membresia.setUsuario(usuarioLogueado);

        membresiaService.save(membresia);

        redirectAttributes.addFlashAttribute("exito", "Membresía adquirida exitosamente.");
        return "redirect:/membresias";
    }
}