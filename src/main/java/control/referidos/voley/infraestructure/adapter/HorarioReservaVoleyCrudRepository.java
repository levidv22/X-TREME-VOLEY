package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.infraestructure.entity.HorarioReservaVoley;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface HorarioReservaVoleyCrudRepository extends CrudRepository<HorarioReservaVoley, Long> {
    List<HorarioReservaVoley> findByDisponibleTrue();
    List<HorarioReservaVoley> findByUsuario(Usuario usuario);
    List<HorarioReservaVoley> findByEquipoCampoAndFechaHoraBetween(String equipoCampo, LocalDateTime inicio, LocalDateTime fin);
}