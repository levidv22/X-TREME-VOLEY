package control.referidos.voley.app.repository;

import control.referidos.voley.infraestructure.entity.PagoBono;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.time.LocalDate;
import java.util.List;

public interface PagoBonoRepository {
    List<PagoBono> findByUsuario(Usuario usuario);
    List<PagoBono> findByFechaPagoBetween(LocalDate inicio, LocalDate fin);
    PagoBono save(PagoBono pagoBono);
    List<PagoBono> findByInscripcionMensualId(Long inscripcionId);
    void deleteByInscripcionMensualId(Long inscripcionId);
}