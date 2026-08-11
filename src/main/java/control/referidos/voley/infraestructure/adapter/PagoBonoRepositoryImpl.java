package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.app.repository.PagoBonoRepository;
import control.referidos.voley.infraestructure.entity.PagoBono;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class PagoBonoRepositoryImpl implements PagoBonoRepository {

    private final PagoBonoCrudRepository pagoBonoCrudRepository;

    public PagoBonoRepositoryImpl(PagoBonoCrudRepository pagoBonoCrudRepository) {
        this.pagoBonoCrudRepository = pagoBonoCrudRepository;
    }

    @Override
    public List<PagoBono> findByUsuario(Usuario usuario) {
        return pagoBonoCrudRepository.findByUsuario(usuario);
    }

    @Override
    public List<PagoBono> findByFechaPagoBetween(LocalDate inicio, LocalDate fin) {
        return pagoBonoCrudRepository.findByFechaPagoBetween(inicio, fin);
    }

    @Override
    public List<PagoBono> findByInscripcionMensualId(Long inscripcionId) {
        return pagoBonoCrudRepository.findByInscripcionMensualId(inscripcionId);
    }

    @Override
    public void deleteByInscripcionMensualId(Long inscripcionId) {
        pagoBonoCrudRepository.deleteByInscripcionMensualId(inscripcionId);
    }

    @Override
    public PagoBono save(PagoBono pagoBono) {
        return pagoBonoCrudRepository.save(pagoBono);
    }
}