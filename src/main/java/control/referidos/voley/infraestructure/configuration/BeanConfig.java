package control.referidos.voley.infraestructure.configuration;

import control.referidos.voley.app.repository.*;
import control.referidos.voley.app.service.*;
import control.referidos.voley.infraestructure.entity.AsistenciaUsuarioLibre;
import control.referidos.voley.infraestructure.entity.InscripcionMensual;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public UsuarioService usuarioService(UsuarioRepository usuarioRepository) {
        return new UsuarioService(usuarioRepository);
    }

    @Bean
    public MembresiaService membresiaService(MembresiaRepository membresiaRepository) {
        return new MembresiaService(membresiaRepository);
    }

    @Bean
    public PagoBonoService pagoBonoService(PagoBonoRepository pagoBonoRepository, RedAfiliadosRepository redAfiliadosRepository, InscripcionMensualRepository inscripcionMensualRepository) {
        return new PagoBonoService(pagoBonoRepository, redAfiliadosRepository, inscripcionMensualRepository);
    }

    @Bean
    public PuntosService puntosService(PuntosRepository puntosRepository, UsuarioRepository usuarioRepository) {
        return new PuntosService(puntosRepository, usuarioRepository);
    }

    @Bean
    public HorarioReservaVoleyService horarioReservaVoleyService(HorarioReservaVoleyRepository horarioReservaVoleyRepository) {
        return new HorarioReservaVoleyService(horarioReservaVoleyRepository);
    }

    @Bean
    public ProductoService productoService(ProductoRepository productoRepository) {
        return new ProductoService(productoRepository);
    }

    @Bean
    public CompraService compraService(CompraRepository compraRepository) {
        return new CompraService(compraRepository);
    }

    @Bean
    public RedAfiliadosService redAfiliadosService(RedAfiliadosRepository redAfiliadosRepository, PuntosService puntosService) {
        return new RedAfiliadosService(redAfiliadosRepository, puntosService);
    }

    @Bean
    public AsistenciaUsuarioLibreService asistenciaUsuarioLibreService(AsistenciaUsuarioLibreRepository asistenciaUsuarioLibreRepository) {
        return new AsistenciaUsuarioLibreService(asistenciaUsuarioLibreRepository);
    }

    @Bean
    public InscripcionMensualService inscripcionMensualService(InscripcionMensualRepository inscripcionMensualRepository, UsuarioRepository usuarioRepository, RedAfiliadosRepository redAfiliadosRepository, PuntosService puntosService, PagoBonoService pagoBonoService, NotifiService notifiService) {
        return new InscripcionMensualService(inscripcionMensualRepository, usuarioRepository, redAfiliadosRepository, puntosService, pagoBonoService, notifiService);
    }

    @Bean
    public NotifiService notifiService(NotifiRepository notificacionRepository) {
        return new NotifiService(notificacionRepository);
    }
}
