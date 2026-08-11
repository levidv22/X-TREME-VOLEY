package control.referidos.voley.app.repository;

import control.referidos.voley.infraestructure.entity.NotificacionRango;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRangoRepository extends JpaRepository<NotificacionRango, Long> {
    List<NotificacionRango> findByLeidoFalse();
}
