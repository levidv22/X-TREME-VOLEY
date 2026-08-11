package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.app.repository.HorarioReservaVoleyRepository;
import control.referidos.voley.infraestructure.entity.HorarioReservaVoley;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class HorarioReservaVoleyRepositoryImpl implements HorarioReservaVoleyRepository {

    private final HorarioReservaVoleyCrudRepository horarioReservaVoleyCrudRepository;

    public HorarioReservaVoleyRepositoryImpl(HorarioReservaVoleyCrudRepository horarioReservaVoleyCrudRepository) {
        this.horarioReservaVoleyCrudRepository = horarioReservaVoleyCrudRepository;
    }

    @Override
    public List<HorarioReservaVoley> findByDisponibleTrue() {
        return horarioReservaVoleyCrudRepository.findByDisponibleTrue();
    }

    @Override
    public List<HorarioReservaVoley> findByUsuario(Usuario usuario) {
        return horarioReservaVoleyCrudRepository.findByUsuario(usuario);
    }

    @Override
    public List<HorarioReservaVoley> findByEquipoCampoAndFechaHoraBetween(String equipoCampo, LocalDateTime inicio, LocalDateTime fin) {
        return horarioReservaVoleyCrudRepository.findByEquipoCampoAndFechaHoraBetween(equipoCampo, inicio, fin);
    }

    @Override
    public HorarioReservaVoley save(HorarioReservaVoley horario) {
        return horarioReservaVoleyCrudRepository.save(horario);
    }

    @Override
    public HorarioReservaVoley findById(Long id) {
        return horarioReservaVoleyCrudRepository.findById(id).orElse(null);
    }
}