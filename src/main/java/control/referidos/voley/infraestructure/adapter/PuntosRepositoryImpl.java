package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.app.repository.PuntosRepository;
import control.referidos.voley.infraestructure.entity.Puntos;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class PuntosRepositoryImpl implements PuntosRepository {

    private final PuntosCrudRepository puntosCrudRepository;

    public PuntosRepositoryImpl(PuntosCrudRepository puntosCrudRepository) {
        this.puntosCrudRepository = puntosCrudRepository;
    }

    @Override
    public Optional<Puntos> findByUsuarioAndPeriodoMes(Usuario usuario, LocalDate periodoMes) {
        return puntosCrudRepository.findByUsuarioAndPeriodoMes(usuario, periodoMes);
    }

    @Override
    public List<Puntos> findByUsuario(Usuario usuario) {
        return puntosCrudRepository.findByUsuario(usuario);
    }

    @Override
    public Puntos save(Puntos puntos) {
        return puntosCrudRepository.save(puntos);
    }
}