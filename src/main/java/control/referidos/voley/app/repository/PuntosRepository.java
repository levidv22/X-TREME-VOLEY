package control.referidos.voley.app.repository;

import control.referidos.voley.infraestructure.entity.Puntos;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PuntosRepository {
    Optional<Puntos> findByUsuarioAndPeriodoMes(Usuario usuario, LocalDate periodoMes);
    List<Puntos> findByUsuario(Usuario usuario);
    Puntos save(Puntos puntos);
}