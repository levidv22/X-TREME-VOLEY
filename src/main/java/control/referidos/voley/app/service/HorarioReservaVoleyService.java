package control.referidos.voley.app.service;

import control.referidos.voley.app.repository.HorarioReservaVoleyRepository;
import control.referidos.voley.infraestructure.entity.HorarioReservaVoley;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.time.LocalDateTime;
import java.util.List;

public class HorarioReservaVoleyService {

    private final HorarioReservaVoleyRepository horarioReservaVoleyRepository;

    public HorarioReservaVoleyService(HorarioReservaVoleyRepository horarioReservaVoleyRepository) {
        this.horarioReservaVoleyRepository = horarioReservaVoleyRepository;
    }

    public List<HorarioReservaVoley> findByDisponibleTrue() {
        return horarioReservaVoleyRepository.findByDisponibleTrue();
    }

    public List<HorarioReservaVoley> findByUsuario(Usuario usuario) {
        return horarioReservaVoleyRepository.findByUsuario(usuario);
    }

    public List<HorarioReservaVoley> findByEquipoCampoAndFechaHoraBetween(String equipoCampo, LocalDateTime inicio, LocalDateTime fin) {
        return horarioReservaVoleyRepository.findByEquipoCampoAndFechaHoraBetween(equipoCampo, inicio, fin);
    }

    public HorarioReservaVoley save(HorarioReservaVoley horario) {
        return horarioReservaVoleyRepository.save(horario);
    }

    public HorarioReservaVoley findById(Long id) {
        return horarioReservaVoleyRepository.findById(id);
    }
}