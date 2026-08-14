package control.referidos.voley.app.repository;

import control.referidos.voley.infraestructure.entity.Notificacion;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotifiRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByLeidoFalseOrderByFechaCreacionDesc();
    List<Notificacion> findAllByOrderByFechaCreacionDesc();
    long countByLeidoFalse();
    @Modifying
    @Transactional
    @Query("UPDATE Notificacion n SET n.leido = true WHERE n.leido = false")
    void marcarTodasComoLeidas();
}
