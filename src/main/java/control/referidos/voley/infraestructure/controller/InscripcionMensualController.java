package control.referidos.voley.infraestructure.controller;

import control.referidos.voley.app.service.InscripcionMensualService;
import control.referidos.voley.app.service.UsuarioService;
import control.referidos.voley.infraestructure.entity.InscripcionMensual;
import control.referidos.voley.infraestructure.entity.Rol;
import control.referidos.voley.infraestructure.entity.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/inscripciones")
public class InscripcionMensualController {

    private final InscripcionMensualService inscripcionMensualService;
    private final UsuarioService usuarioService;

    public InscripcionMensualController(InscripcionMensualService inscripcionMensualService, UsuarioService usuarioService) {
        this.inscripcionMensualService = inscripcionMensualService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarInscripciones(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        if (usuarioLogueado.getRol() == Rol.ADMIN) {
            model.addAttribute("usuarios", usuarioService.findClientesSinAdmin());
            model.addAttribute("periodoActual", YearMonth.now().toString());
            return "inscripciones/admin-lista";
        } else {
            model.addAttribute("inscripciones", inscripcionMensualService.findByUsuario(usuarioLogueado));
            model.addAttribute("usuario", usuarioLogueado);
            return "inscripciones/lista";
        }
    }

    @GetMapping("/cliente/{id}")
    public String historialCliente(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || usuarioLogueado.getRol() != Rol.ADMIN) {
            return "redirect:/usuarios/login";
        }

        Usuario cliente = usuarioService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        LocalDate hoy = LocalDate.now();
        List<InscripcionMensual> inscripciones = inscripcionMensualService.findByUsuario(cliente);

        for (InscripcionMensual ins : inscripciones) {
            if (ins.isActivo() && ins.getFechaPago() != null) {
                LocalDate fechaVencimiento = ins.getFechaPago().plusMonths(1);
                if (!hoy.isBefore(fechaVencimiento)) {
                    ins.setActivo(false);
                }
            }
        }

        String periodoActual = YearMonth.now().toString();
        boolean tieneAbonoParcial = inscripciones.stream()
                .anyMatch(ins -> ins.getPeriodoMes().equals(periodoActual) && !ins.isActivo() && ins.getMontoPagado() > 0);

        model.addAttribute("cliente", cliente);
        model.addAttribute("inscripciones", inscripciones);
        model.addAttribute("tieneAbonoParcial", tieneAbonoParcial);
        return "inscripciones/historial-cliente";
    }

    @PostMapping("/admin/abonar")
    public String registrarAbono(@RequestParam Long usuarioId,
                                 @RequestParam double monto,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || usuarioLogueado.getRol() != Rol.ADMIN) {
            return "redirect:/usuarios/login";
        }

        try {
            inscripcionMensualService.registrarAbonoParcial(usuarioId, monto);
            redirectAttributes.addFlashAttribute("exito", "Abono de S/. " + monto + " registrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inscripciones/cliente/" + usuarioId;
    }

    @PostMapping("/admin/pagar-completo")
    public String registrarPagoCompleto(@RequestParam Long usuarioId,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || usuarioLogueado.getRol() != Rol.ADMIN) {
            return "redirect:/usuarios/login";
        }

        try {
            inscripcionMensualService.registrarPagoCompleto(usuarioId);
            redirectAttributes.addFlashAttribute("exito", "Inscripción pagada por completo (S/. 40) exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inscripciones/cliente/" + usuarioId;
    }

    @PostMapping("/admin/eliminar")
    public String eliminarInscripcion(@RequestParam Long id,
                                      @RequestParam Long usuarioId,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || usuarioLogueado.getRol() != Rol.ADMIN) {
            return "redirect:/usuarios/login";
        }

        try {
            inscripcionMensualService.eliminarInscripcion(id);
            redirectAttributes.addFlashAttribute("exito", "Registro de pago eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inscripciones/cliente/" + usuarioId;
    }
}