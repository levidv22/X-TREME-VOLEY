package control.referidos.voley.app.repository;

import control.referidos.voley.infraestructure.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotifiRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByLeidoFalseOrderByFechaCreacionDesc();
    List<Notificacion> findAllByOrderByFechaCreacionDesc();
    long countByLeidoFalse();
}
