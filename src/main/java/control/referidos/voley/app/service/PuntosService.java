package control.referidos.voley.app.service;

import control.referidos.voley.app.repository.PuntosRepository;
import control.referidos.voley.app.repository.UsuarioRepository;
import control.referidos.voley.infraestructure.entity.Puntos;
import control.referidos.voley.infraestructure.entity.RangoCarrera;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PuntosService {

    private final PuntosRepository puntosRepository;
    private final UsuarioRepository usuarioRepository;

    public PuntosService(PuntosRepository puntosRepository, UsuarioRepository usuarioRepository) {
        this.puntosRepository = puntosRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Puntos> findByUsuarioAndPeriodoMes(Usuario usuario, LocalDate periodoMes) {
        return puntosRepository.findByUsuarioAndPeriodoMes(usuario, periodoMes);
    }

    public List<Puntos> findByUsuario(Usuario usuario) {
        List<Puntos> puntos = puntosRepository.findByUsuario(usuario);
        puntos.sort((p1, p2) -> p2.getId().compareTo(p1.getId()));
        return puntos;
    }

    public Puntos save(Puntos puntos) {
        return puntosRepository.save(puntos);
    }

    public void procesarPuntosYAsignarRango(Usuario patrocinador, int puntosNuevos) {
        LocalDate periodoActual = LocalDate.now().withDayOfMonth(1);

        Puntos puntosRegistro = puntosRepository.findByUsuarioAndPeriodoMes(patrocinador, periodoActual)
                .orElseGet(() -> {
                    Puntos p = new Puntos();
                    p.setUsuario(patrocinador);
                    p.setPeriodoMes(periodoActual);
                    p.setPuntosMensuales(0);
                    p.setPuntosTotalesAcumulados(0);
                    return p;
                });

        puntosRegistro.setPuntosMensuales(puntosRegistro.getPuntosMensuales() + puntosNuevos);
        puntosRegistro.setPuntosTotalesAcumulados(puntosRegistro.getPuntosTotalesAcumulados() + puntosNuevos);
        puntosRepository.save(puntosRegistro);

        actualizarRangoCarrera(patrocinador, puntosRegistro.getPuntosTotalesAcumulados());
    }

    public void decrementarPuntosYReevaluarRango(Usuario patrocinador, int puntosQuitados, LocalDate periodoMes) {
        LocalDate periodoPuntos = periodoMes != null ? periodoMes.withDayOfMonth(1) : LocalDate.now().withDayOfMonth(1);

        Puntos puntosRegistro = puntosRepository.findByUsuarioAndPeriodoMes(patrocinador, periodoPuntos).orElse(null);
        if (puntosRegistro != null) {
            int nuevosMensuales = Math.max(0, puntosRegistro.getPuntosMensuales() - puntosQuitados);
            int nuevosTotales = Math.max(0, puntosRegistro.getPuntosTotalesAcumulados() - puntosQuitados);

            puntosRegistro.setPuntosMensuales(nuevosMensuales);
            puntosRegistro.setPuntosTotalesAcumulados(nuevosTotales);
            puntosRepository.save(puntosRegistro);
        }

        // Recalcular los puntos totales acumulados reales sumando todos los registros del usuario
        List<Puntos> todosLosPuntos = puntosRepository.findByUsuario(patrocinador);
        int sumaTotalAcumulada = todosLosPuntos.stream().mapToInt(Puntos::getPuntosTotalesAcumulados).sum();

        actualizarRangoCarrera(patrocinador, sumaTotalAcumulada);
    }

    private void actualizarRangoCarrera(Usuario usuario, int puntosTotales) {
        RangoCarrera nuevoRango = RangoCarrera.NINGUNO;

        if (puntosTotales >= 5000000) {
            nuevoRango = RangoCarrera.XTREME_STAR;
        } else if (puntosTotales >= 2000000) {
            nuevoRango = RangoCarrera.DIAMOND_STAR;
        } else if (puntosTotales >= 1000000) {
            nuevoRango = RangoCarrera.GOLDEN_STAR;
        } else if (puntosTotales >= 500000) {
            nuevoRango = RangoCarrera.GOLD_STAR;
        } else if (puntosTotales >= 200000) {
            nuevoRango = RangoCarrera.STAR;
        } else if (puntosTotales >= 100000) {
            nuevoRango = RangoCarrera.DIAMANTE_IMPERIAL;
        } else if (puntosTotales >= 50000) {
            nuevoRango = RangoCarrera.DIAMANTE_NEGRO;
        } else if (puntosTotales >= 20000) {
            nuevoRango = RangoCarrera.DIAMANTE_AZUL;
        } else if (puntosTotales >= 10000) {
            nuevoRango = RangoCarrera.DIAMANTE;
        } else if (puntosTotales >= 5000) {
            nuevoRango = RangoCarrera.ZAFIRO;
        } else if (puntosTotales >= 2000) {
            nuevoRango = RangoCarrera.ORO;
        } else if (puntosTotales >= 1000) {
            nuevoRango = RangoCarrera.PLATA;
        } else if (puntosTotales >= 500) {
            nuevoRango = RangoCarrera.BRONCE;
        } else if (puntosTotales >= 200) {
            nuevoRango = RangoCarrera.MAESTRO;
        } else if (puntosTotales >= 100) {
            nuevoRango = RangoCarrera.LIDER;
        }

        usuario.setRangoActual(nuevoRango);
        usuarioRepository.save(usuario);
    }
}