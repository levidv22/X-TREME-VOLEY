package control.referidos.voley.app.service;

import control.referidos.voley.app.repository.InscripcionMensualRepository;
import control.referidos.voley.app.repository.RedAfiliadosRepository;
import control.referidos.voley.app.repository.UsuarioRepository;
import control.referidos.voley.infraestructure.entity.*;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public class InscripcionMensualService {

    private final InscripcionMensualRepository inscripcionMensualRepository;
    private final UsuarioRepository usuarioRepository;
    private final RedAfiliadosRepository redAfiliadosRepository;
    private final PuntosService puntosService;
    private final PagoBonoService pagoBonoService;
    private final NotifiService notifiService;

    public InscripcionMensualService(InscripcionMensualRepository inscripcionMensualRepository,
                                     UsuarioRepository usuarioRepository,
                                     RedAfiliadosRepository redAfiliadosRepository,
                                     PuntosService puntosService,
                                     PagoBonoService pagoBonoService,
                                     NotifiService notifiService) {
        this.inscripcionMensualRepository = inscripcionMensualRepository;
        this.usuarioRepository = usuarioRepository;
        this.redAfiliadosRepository = redAfiliadosRepository;
        this.puntosService = puntosService;
        this.pagoBonoService = pagoBonoService;
        this.notifiService = notifiService;
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
                        return inscripcionMensualRepository.save(nueva);
                    });

            if (inscripcion.getId() == null) {
                inscripcion = inscripcionMensualRepository.save(inscripcion);
            }

            // 1. Si la inscripción ya está activa o no le queda espacio, avanzamos al siguiente mes
            double espacioDisponible = inscripcion.getMontoTotal() - inscripcion.getMontoPagado();
            if (inscripcion.isActivo() || espacioDisponible <= 0) {
                mesTarget = mesTarget.plusMonths(1);
                continue;
            }

            double montoAplicadoEnEsteCiclo;

            // ASIGNAMOS SIEMPRE EL ESTADO PAGO_ADMIN CUANDO EL ADMIN REGISTRA O ABONA
            inscripcion.setEstadoPago(EstadoPago.PAGO_ADMIN);

            // 2. Si el abono cubre o supera lo que falta para completar los S/. 40 de este mes
            if (montoRestante >= espacioDisponible) {
                montoAplicadoEnEsteCiclo = espacioDisponible;
                montoRestante -= espacioDisponible;

                inscripcion.setMontoPagado(inscripcion.getMontoTotal());
                inscripcion.setMontoReportado(inscripcion.getMontoTotal());
                inscripcion.setActivo(true);
                inscripcion.setFechaPago(LocalDate.now());
                inscripcionMensualRepository.save(inscripcion);

                // Activar al usuario si el pago completado corresponde al mes en curso
                if (periodoStr.equals(YearMonth.now().toString())) {
                    usuario.setActivoMes(true);
                    usuarioRepository.save(usuario);
                    propagarPuntosAscendentes(usuario);
                }

                mesTarget = mesTarget.plusMonths(1); // Pasamos al siguiente mes si aún queda saldo sobrante
            } else {
                // 3. Si el abono es un pago parcial que no llega a cubrir el total del mes
                montoAplicadoEnEsteCiclo = montoRestante;
                inscripcion.setMontoPagado(inscripcion.getMontoPagado() + montoRestante);
                inscripcion.setMontoReportado(inscripcion.getMontoPagado());
                inscripcion.setFechaPago(LocalDate.now());
                inscripcionMensualRepository.save(inscripcion);
                montoRestante = 0; // Se consumió todo el dinero abonado
            }

            // 4. Procesar bonificaciones proporcionales al monto efectivamente aplicado
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

    public void reportarPagoComprobante(Usuario usuario, double monto, String comprobanteUrl) {
        String periodoStr = YearMonth.now().toString();

        InscripcionMensual inscripcion = inscripcionMensualRepository.findByUsuarioAndPeriodoMes(usuario, periodoStr)
                .orElseGet(() -> {
                    InscripcionMensual nueva = new InscripcionMensual();
                    nueva.setUsuario(usuario);
                    nueva.setPeriodoMes(periodoStr);
                    nueva.setMontoTotal(40.0);
                    nueva.setMontoPagado(0.0);
                    nueva.setActivo(false);
                    return inscripcionMensualRepository.save(nueva);
                });

        inscripcion.setComprobanteUrl(comprobanteUrl);
        inscripcion.setMontoReportado(monto);
        inscripcion.setEstadoPago(EstadoPago.EN_REVISION);
        inscripcion.setFechaSubidaComprobante(LocalDateTime.now());
        inscripcionMensualRepository.save(inscripcion);

        // Enviar notificación automática al ADMIN
        notifiService.crearNotificacionAdmin(
                "Nuevo Pago Registrado",
                usuario.getNombre() + " " + usuario.getApellido() + " ha subido un comprobante por S/. " + monto,
                "/inscripciones/cliente/" + usuario.getId()
        );
    }

    // NUEVO: Aprobar Pago desde la vista del ADMIN
    public void aprobarPagoComprobante(Long inscripcionId) {
        InscripcionMensual inscripcion = inscripcionMensualRepository.findById(inscripcionId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        if (inscripcion.getMontoReportado() != null && inscripcion.getMontoReportado() > 0) {
            double montoAprobar = inscripcion.getMontoReportado();
            inscripcion.setEstadoPago(EstadoPago.APROBADO);
            inscripcionMensualRepository.save(inscripcion); // 1. Guardado manual

            // 2. registrarAbonoParcial buscará nuevamente o creará la inscripción por periodo
            registrarAbonoParcial(inscripcion.getUsuario().getId(), montoAprobar);
        }
    }

    // NUEVO: Rechazar Pago
    public void rechazarPagoComprobante(Long inscripcionId) {
        InscripcionMensual inscripcion = inscripcionMensualRepository.findById(inscripcionId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));

        inscripcion.setEstadoPago(EstadoPago.RECHAZADO);
        inscripcionMensualRepository.save(inscripcion);
    }

    @Transactional
    public void pagarConBono(Long usuarioId, double montoAPagar) {
        if (montoAPagar <= 0) {
            throw new RuntimeException("El monto a pagar debe ser mayor a 0");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Validar que el usuario tenga saldo suficiente en sus bonos acumulados
        double saldoDisponible = pagoBonoService.obtenerSaldoDisponible(usuario);
        if (montoAPagar > saldoDisponible) {
            throw new RuntimeException(String.format("Saldo insuficiente. Tu saldo actual en bonos es de S/. %.2f", saldoDisponible));
        }

        double montoRestante = montoAPagar;
        YearMonth mesTarget = YearMonth.now();

        while (montoRestante > 0) {
            String periodoStr = mesTarget.toString();

            // Buscar o crear la inscripción del periodo objetivo
            InscripcionMensual inscripcion = inscripcionMensualRepository.findByUsuarioAndPeriodoMes(usuario, periodoStr)
                    .orElseGet(() -> {
                        InscripcionMensual nueva = new InscripcionMensual();
                        nueva.setUsuario(usuario);
                        nueva.setPeriodoMes(periodoStr);
                        nueva.setMontoTotal(40.0);
                        nueva.setMontoPagado(0.0);
                        nueva.setActivo(false);
                        return inscripcionMensualRepository.save(nueva);
                    });

            if (inscripcion.getId() == null) {
                inscripcion = inscripcionMensualRepository.save(inscripcion);
            }

            // Si la inscripción de este periodo ya está pagada por completo (S/. 40), pasamos al siguiente mes
            double espacioDisponible = inscripcion.getMontoTotal() - inscripcion.getMontoPagado();
            if (inscripcion.isActivo() || espacioDisponible <= 0) {
                mesTarget = mesTarget.plusMonths(1);
                continue;
            }

            double montoAplicadoEnEsteCiclo;

            // Establecemos el estado como PAGO_BONO
            inscripcion.setEstadoPago(EstadoPago.PAGO_BONO);

            // 3. Evaluar si el abono completa o excede el monto del mes actual
            if (montoRestante >= espacioDisponible) {
                montoAplicadoEnEsteCiclo = espacioDisponible;
                montoRestante -= espacioDisponible;

                inscripcion.setMontoPagado(inscripcion.getMontoTotal());
                inscripcion.setMontoReportado(inscripcion.getMontoTotal());
                inscripcion.setActivo(true);
                inscripcion.setFechaPago(LocalDate.now());

                // Activar al usuario en el sistema si se completa la inscripción del mes en curso
                if (periodoStr.equals(YearMonth.now().toString())) {
                    usuario.setActivoMes(true);
                    usuarioRepository.save(usuario);
                    propagarPuntosAscendentes(usuario);
                }

                InscripcionMensual inscripcionGuardada = inscripcionMensualRepository.save(inscripcion);

                // Restar saldo de bonos y procesar la red de patrocinio
                pagoBonoService.registrarUsoDeBono(usuario, montoAplicadoEnEsteCiclo, inscripcionGuardada);
                pagoBonoService.procesarBonosPatrocinio(usuario, montoAplicadoEnEsteCiclo, inscripcionGuardada);

                // Avanzar al siguiente mes para los saldos restantes
                mesTarget = mesTarget.plusMonths(1);
            } else {
                // 4. Pago parcial dentro del mes objetivo
                montoAplicadoEnEsteCiclo = montoRestante;
                inscripcion.setMontoPagado(inscripcion.getMontoPagado() + montoRestante);
                inscripcion.setMontoReportado(inscripcion.getMontoPagado());
                inscripcion.setFechaPago(LocalDate.now());

                InscripcionMensual inscripcionGuardada = inscripcionMensualRepository.save(inscripcion);

                // Restar saldo de bonos y procesar la red de patrocinio
                pagoBonoService.registrarUsoDeBono(usuario, montoAplicadoEnEsteCiclo, inscripcionGuardada);
                pagoBonoService.procesarBonosPatrocinio(usuario, montoAplicadoEnEsteCiclo, inscripcionGuardada);

                montoRestante = 0;
            }
        }
    }
}