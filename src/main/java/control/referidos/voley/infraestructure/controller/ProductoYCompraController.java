package control.referidos.voley.infraestructure.controller;

import control.referidos.voley.app.service.CompraService;
import control.referidos.voley.app.service.ProductoService;
import control.referidos.voley.app.service.UsuarioService;
import control.referidos.voley.infraestructure.entity.Compra;
import control.referidos.voley.infraestructure.entity.Producto;
import control.referidos.voley.infraestructure.entity.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tienda")
public class ProductoYCompraController {

    private final ProductoService productoService;
    private final CompraService compraService;
    private final UsuarioService usuarioService;

    public ProductoYCompraController(ProductoService productoService, CompraService compraService, UsuarioService usuarioService) {
        this.productoService = productoService;
        this.compraService = compraService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/productos")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.findByStockGreaterThan(0));
        return "tienda/productos";
    }

    @GetMapping("/carrito")
    public String verCarrito(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<Producto> carrito = (List<Producto>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        double total = carrito.stream().mapToDouble(Producto::getPrecio).sum();
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
        return "tienda/carrito";
    }

    @PostMapping("/carrito/agregar/{id}")
    public String agregarAlCarrito(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Producto producto = productoService.findById(id);
        if (producto == null || producto.getStock() <= 0) {
            redirectAttributes.addFlashAttribute("error", "El producto no está disponible o no tiene stock.");
            return "redirect:/tienda/productos";
        }

        @SuppressWarnings("unchecked")
        List<Producto> carrito = (List<Producto>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        carrito.add(producto);
        session.setAttribute("carrito", carrito);

        redirectAttributes.addFlashAttribute("exito", "Producto agregado al carrito.");
        return "redirect:/tienda/productos";
    }

    @PostMapping("/carrito/remover/{index}")
    public String removerDelCarrito(@PathVariable int index, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Producto> carrito = (List<Producto>) session.getAttribute("carrito");
        if (carrito != null && index >= 0 && index < carrito.size()) {
            carrito.remove(index);
            session.setAttribute("carrito", carrito);
        }
        return "redirect:/tienda/carrito";
    }

    @PostMapping("/comprar")
    public String procesarCompra(@RequestParam String metodoPago, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        @SuppressWarnings("unchecked")
        List<Producto> carrito = (List<Producto>) session.getAttribute("carrito");
        if (carrito == null || carrito.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El carrito está vacío.");
            return "redirect:/tienda/carrito";
        }

        // Verificar stock de todos los productos en el carrito
        for (Producto p : carrito) {
            Producto productoActual = productoService.findById(p.getId());
            if (productoActual == null || productoActual.getStock() <= 0) {
                redirectAttributes.addFlashAttribute("error", "Uno de los productos ya no cuenta con stock disponible.");
                return "redirect:/tienda/carrito";
            }
        }

        double total = carrito.stream().mapToDouble(Producto::getPrecio).sum();

        Compra compra = new Compra();
        compra.setFechaCompra(LocalDate.now());
        compra.setTotal(total);
        compra.setMetodoPago(metodoPago);
        compra.setUsuario(usuarioLogueado);
        compra.setProductos(new ArrayList<>(carrito));

        compraService.save(compra);

        // Descontar stock de los productos vendidos
        for (Producto p : carrito) {
            p.setStock(p.getStock() - 1);
            productoService.save(p);
        }

        // Limpiar carrito
        session.removeAttribute("carrito");

        redirectAttributes.addFlashAttribute("exito", "Compra realizada exitosamente.");
        return "redirect:/tienda/mis-compras";
    }

    @GetMapping("/mis-compras")
    public String listarMisCompras(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        Optional<Usuario> usuarioOpt = usuarioService.findById(usuarioLogueado.getId());
        if (usuarioOpt.isPresent()) {
            usuarioLogueado = usuarioOpt.get();
            session.setAttribute("usuarioLogueado", usuarioLogueado);
        }

        model.addAttribute("compras", compraService.findByUsuario(usuarioLogueado));
        model.addAttribute("usuario", usuarioLogueado);
        return "tienda/mis-compras";
    }
}