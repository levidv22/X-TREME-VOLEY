package control.referidos.voley.app.repository;

import control.referidos.voley.infraestructure.entity.HorarioReservaVoley;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.time.LocalDateTime;
import java.util.List;

public interface HorarioReservaVoleyRepository {
    List<HorarioReservaVoley> findByDisponibleTrue();
    List<HorarioReservaVoley> findByUsuario(Usuario usuario);
    List<HorarioReservaVoley> findByEquipoCampoAndFechaHoraBetween(String equipoCampo, LocalDateTime inicio, LocalDateTime fin);
    HorarioReservaVoley save(HorarioReservaVoley horario);
    HorarioReservaVoley findById(Long id);
}