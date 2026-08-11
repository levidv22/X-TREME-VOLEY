package control.referidos.voley.app.service;

import control.referidos.voley.app.repository.InscripcionMensualRepository;
import control.referidos.voley.app.repository.PagoBonoRepository;
import control.referidos.voley.app.repository.RedAfiliadosRepository;
import control.referidos.voley.infraestructure.entity.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public class PagoBonoService {

    private final PagoBonoRepository pagoBonoRepository;
    private final RedAfiliadosRepository redAfiliadosRepository;
    private final InscripcionMensualRepository inscripcionMensualRepository;

    public PagoBonoService(PagoBonoRepository pagoBonoRepository, RedAfiliadosRepository redAfiliadosRepository, InscripcionMensualRepository inscripcionMensualRepository) {
        this.pagoBonoRepository = pagoBonoRepository;
        this.redAfiliadosRepository = redAfiliadosRepository;
        this.inscripcionMensualRepository = inscripcionMensualRepository;
    }
    public List<PagoBono> findByUsuario(Usuario usuario) {
        List<PagoBono> bonos = pagoBonoRepository.findByUsuario(usuario);
        bonos.sort((b1, b2) -> b2.getId().compareTo(b1.getId()));
        return bonos;
    }

    public List<PagoBono> findByFechaPagoBetween(LocalDate inicio, LocalDate fin) {
        return pagoBonoRepository.findByFechaPagoBetween(inicio, fin);
    }

    public PagoBono save(PagoBono pagoBono) {
        return pagoBonoRepository.save(pagoBono);
    }

    public void procesarBonosPatrocinio(Usuario clienteInscrito, double montoAbonado, InscripcionMensual inscripcionMensual) {
        // Definición de porcentajes y su requisito de monto mínimo acumulado
        // Nivel 1 (10%): Requiere >= 20 soles
        // Niveles 2-6 (5, 2, 1, 1, 1%): Requieren >= 50 soles
        double[] porcentajes = {0.10, 0.05, 0.02, 0.01, 0.01, 0.01};

        Usuario actual = clienteInscrito;
        int nivel = 0;

        while (nivel < porcentajes.length) {
            List<RedAfiliados> relaciones = redAfiliadosRepository.findByReferido(actual);
            if (relaciones.isEmpty()) break;

            Usuario patrocinador = relaciones.get(0).getPatrocinador();
            if (patrocinador == null) break;

            // VERIFICACIÓN DE VIGENCIA DEL PATROCINADOR
            boolean puedeCobrar = verificarElegibilidadPatrocinador(patrocinador, nivel);

            if (puedeCobrar) {
                double montoBono = montoAbonado * porcentajes[nivel];
                PagoBono bono = new PagoBono();
                bono.setMonto(montoBono);
                bono.setFechaPago(LocalDate.now());
                bono.setTipoBono("Patrocinio (Nivel " + (nivel + 1) + ")");
                bono.setUsuario(patrocinador);
                bono.setInscripcionMensual(inscripcionMensual);
                pagoBonoRepository.save(bono);
            }

            actual = patrocinador;
            nivel++;
        }
    }

    private boolean verificarElegibilidadPatrocinador(Usuario p, int nivel) {

        if (p.getRol() == Rol.ADMIN) {
            return true;
        }

        // Buscamos la inscripción del mes actual de este patrocinador
        String mesActual = YearMonth.now().toString();
        Optional<InscripcionMensual> ins = inscripcionMensualRepository.findByUsuarioAndPeriodoMes(p, mesActual);

        if (ins.isEmpty()) return false;

        double pagado = ins.get().getMontoPagado();

        // Nivel 0 (es el 10% del Nivel 1) -> Requiere mínimo 20
        if (nivel == 0) {
            return pagado >= 20.0;
        }
        // Niveles 1 al 5 (5%, 2%, 1%, 1%, 1%) -> Requieren mínimo 50
        else {
            return pagado >= 50.0;
        }
    }

    @Transactional
    public void eliminarBonosPorInscripcion(Long inscripcionId) {
        pagoBonoRepository.deleteByInscripcionMensualId(inscripcionId);
    }
}