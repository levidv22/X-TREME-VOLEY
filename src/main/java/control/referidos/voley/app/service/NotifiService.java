package control.referidos.voley.app.service;

import control.referidos.voley.app.repository.NotifiRepository;
import control.referidos.voley.infraestructure.entity.Notificacion;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotifiService {
    private final NotifiRepository notificacionRepository;

    public NotifiService(NotifiRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public void crearNotificacionAdmin(String titulo, String mensaje, String urlDestino) {
        Notificacion notificacion = new Notificacion();
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setUrlDestino(urlDestino);
        notificacionRepository.save(notificacion);
    }

    public List<Notificacion> listarNoLeidas() {
        return notificacionRepository.findByLeidoFalseOrderByFechaCreacionDesc();
    }

    public List<Notificacion> listarTodas() {
        return notificacionRepository.findAllByOrderByFechaCreacionDesc();
    }

    public long contarNoLeidas() {
        return notificacionRepository.countByLeidoFalse();
    }

    public void marcarComoLeida(Long id) {
        notificacionRepository.findById(id).ifPresent(n -> {
            n.setLeido(true);
            notificacionRepository.save(n);
        });
    }
}
