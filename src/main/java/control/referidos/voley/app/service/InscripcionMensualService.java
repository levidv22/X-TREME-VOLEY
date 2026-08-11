package control.referidos.voley.app.service;

import control.referidos.voley.app.repository.InscripcionMensualRepository;
import control.referidos.voley.app.repository.RedAfiliadosRepository;
import control.referidos.voley.app.repository.UsuarioRepository;
import control.referidos.voley.infraestructure.entity.InscripcionMensual;
import control.referidos.voley.infraestructure.entity.RedAfiliados;
import control.referidos.voley.infraestructure.entity.Rol;
import control.referidos.voley.infraestructure.entity.Usuario;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public class InscripcionMensualService {

    private final InscripcionMensualRepository inscripcionMensualRepository;
    private final UsuarioRepository usuarioRepository;
    private final RedAfiliadosRepository redAfiliadosRepository;
    private final PuntosService puntosService;
    private final PagoBonoService pagoBonoService;

    public InscripcionMensualService(InscripcionMensualRepository inscripcionMensualRepository,
                                     UsuarioRepository usuarioRepository,
                                     RedAfiliadosRepository redAfiliadosRepository,
                                     PuntosService puntosService,
                                     PagoBonoService pagoBonoService) {
        this.inscripcionMensualRepository = inscripcionMensualRepository;
        this.usuarioRepository = usuarioRepository;
        this.redAfiliadosRepository = redAfiliadosRepository;
        this.puntosService = puntosService;
        this.pagoBonoService = pagoBonoService;
    }

    public List<InscripcionMensual> listarTodas() {
        return inscripcionMensualRepository.findAll();
    }

    public List<InscripcionMensual> findByUsuario(Usuario usuario) {
        List<InscripcionMensual> lista = inscripcionMensualRepository.findByUsuario(usuario);
        // Ordena para que el ID más alto (el más reciente) aparezca primero
        lista.sort((i1, i2) -> i2.getId().compareTo(i1.getId()));
        return lista;
    }

    public Optional<InscripcionMensual> findById(Long id) {
        return inscripcionMensualRepository.findById(id);
    }

    public void eliminarInscripcion(Long id) {
        InscripcionMensual inscripcion = inscripcionMensualRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        Usuario usuario = inscripcion.getUsuario();

        // 1. Reversa la propagación de puntos si estaba activo
        if (inscripcion.isActivo()) {
            inscripcion.setActivo(false);
            usuario.setActivoMes(false);
            usuarioRepository.save(usuario);

            LocalDate periodoFecha = inscripcion.getFechaPago() != null
                    ? inscripcion.getFechaPago().withDayOfMonth(1)
                    : LocalDate.now().withDayOfMonth(1);
            revertirPuntosAscendentes(usuario, periodoFecha);
        }

        // 2. ELIMINAR ABSOLUTAMENTE TODAS LAS BONIFICACIONES otorgadas a los patrocinadores por esta inscripción
        pagoBonoService.eliminarBonosPorInscripcion(id);

        // 3. Finalmente eliminamos la inscripción
        inscripcionMensualRepository.deleteById(id);
    }

    public void registrarAbonoParcial(Long usuarioId, double montoAbonado) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        double montoRestante = montoAbonado;
        YearMonth mesTarget = YearMonth.now();

        while (montoRestante > 0) {
            String periodoStr = mesTarget.toString();

            InscripcionMensual inscripcion = inscripcionMensualRepository.findByUsuarioAndPeriodoMes(usuario, periodoStr)
                    .orElseGet(() -> {
                        InscripcionMensual nueva = new InscripcionMensual();
                        nueva.setUsuario(usuario);
                        nueva.setPeriodoMes(periodoStr);
                        nueva.setMontoTotal(40.0);
                        nueva.setMontoPagado(0.0);
                        nueva.setActivo(false);
                        return inscripcionMensualRepository.save(nueva); // Guardamos previamente para tener un ID válido
                    });

            // Si no tiene ID (por si acaso entra por orElseGet sin guardar), nos aseguramos de guardarlo
            if (inscripcion.getId() == null) {
                inscripcion = inscripcionMensualRepository.save(inscripcion);
            }

            if (inscripcion.isActivo()) {
                mesTarget = mesTarget.plusMonths(1);
                continue;
            }

            double espacioDisponible = inscripcion.getMontoTotal() - inscripcion.getMontoPagado();
            double montoAplicadoEnEsteCiclo;

            if (montoRestante >= espacioDisponible) {
                montoAplicadoEnEsteCiclo = espacioDisponible;
                montoRestante -= espacioDisponible;
                inscripcion.setMontoPagado(inscripcion.getMontoTotal());
                inscripcion.setActivo(true);
                inscripcion.setFechaPago(LocalDate.now());
                inscripcionMensualRepository.save(inscripcion);

                if (periodoStr.equals(YearMonth.now().toString())) {
                    usuario.setActivoMes(true);
                    usuarioRepository.save(usuario);
                    propagarPuntosAscendentes(usuario);
                }

                mesTarget = mesTarget.plusMonths(1);
            } else {
                montoAplicadoEnEsteCiclo = montoRestante;
                inscripcion.setMontoPagado(inscripcion.getMontoPagado() + montoRestante);
                inscripcionMensualRepository.save(inscripcion);
                montoRestante = 0;
            }

            // GENERAR LOS BONOS ASCENDENTES Y PASARLE LA INSCRIPCIÓN ACTUAL
            if (montoAplicadoEnEsteCiclo > 0) {
                pagoBonoService.procesarBonosPatrocinio(usuario, montoAplicadoEnEsteCiclo, inscripcion);
            }
        }
    }

    public void registrarPagoCompleto(Long usuarioId) {
        registrarAbonoParcial(usuarioId, 40.0);
    }

    private void propagarPuntosAscendentes(Usuario clienteInscrito) {
        Usuario actual = clienteInscrito;
        String mesActual = YearMonth.now().toString();

        while (true) {
            List<RedAfiliados> relaciones = redAfiliadosRepository.findByReferido(actual);
            if (relaciones.isEmpty()) break;

            RedAfiliados rel = relaciones.get(0);
            Usuario patrocinador = rel.getPatrocinador();
            if (patrocinador == null) break;

            // NUEVA LÓGICA: Verificar si el patrocinador es apto para recibir puntos
            if (esAptoParaRecibirPuntos(patrocinador, mesActual)) {
                puntosService.procesarPuntosYAsignarRango(patrocinador, 1);
            }

            actual = patrocinador;
        }
    }

    private boolean esAptoParaRecibirPuntos(Usuario patrocinador, String mesActual) {
        // Si es ADMIN, siempre recibe puntos
        if (patrocinador.getRol() == Rol.ADMIN) {
            return true;
        }

        // Buscamos su inscripción del mes
        Optional<InscripcionMensual> ins = inscripcionMensualRepository.findByUsuarioAndPeriodoMes(patrocinador, mesActual);

        // Solo recibe puntos si tiene inscripción, está activa (pagó los 50)
        // y la fecha de pago está vigente (según tu lógica de 1 mes de duración)
        if (ins.isPresent()) {
            InscripcionMensual inscripcion = ins.get();
            if (inscripcion.isActivo() && inscripcion.getFechaPago() != null) {
                // Verifica que hoy sea antes de la fecha de vencimiento (fechaPago + 1 mes)
                LocalDate fechaVencimiento = inscripcion.getFechaPago().plusMonths(1);
                return LocalDate.now().isBefore(fechaVencimiento) || LocalDate.now().isEqual(fechaVencimiento);
            }
        }
        return false;
    }

    private void revertirPuntosAscendentes(Usuario clienteInscrito, LocalDate periodoMes) {
        Usuario actual = clienteInscrito;
        while (true) {
            List<RedAfiliados> relaciones = redAfiliadosRepository.findByReferido(actual);
            if (relaciones.isEmpty()) {
                break;
            }
            RedAfiliados rel = relaciones.get(0);
            Usuario patrocinador = rel.getPatrocinador();
            if (patrocinador == null) {
                break;
            }
            puntosService.decrementarPuntosYReevaluarRango(patrocinador, 1, periodoMes);
            actual = patrocinador;
        }
    }
}