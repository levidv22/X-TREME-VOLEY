package control.referidos.voley.app.service;

import control.referidos.voley.app.repository.RedAfiliadosRepository;
import control.referidos.voley.dto.NodoRedDto;
import control.referidos.voley.infraestructure.entity.Puntos;
import control.referidos.voley.infraestructure.entity.RedAfiliados;
import control.referidos.voley.infraestructure.entity.Rol;
import control.referidos.voley.infraestructure.entity.Usuario;

import java.util.ArrayList;
import java.util.List;

public class RedAfiliadosService {

    private final RedAfiliadosRepository redAfiliadosRepository;
    private final PuntosService puntosService; // Inyectamos PuntosService

    public RedAfiliadosService(RedAfiliadosRepository redAfiliadosRepository, PuntosService puntosService) {
        this.redAfiliadosRepository = redAfiliadosRepository;
        this.puntosService = puntosService;
    }

    public NodoRedDto construirArbolRed(Usuario usuarioLogueado) {
        int nivelMaximo = (usuarioLogueado.getRol() == Rol.ADMIN) ? -1 : 6;

        NodoRedDto raiz = convertirAUsuarioDto(usuarioLogueado, 0, false);
        construirHijosRecursivo(raiz, 1, nivelMaximo);
        return raiz;
    }

    private void construirHijosRecursivo(NodoRedDto padreDto, int nivelActual, int nivelMaximo) {
        if (nivelMaximo != -1 && nivelActual > nivelMaximo) {
            return;
        }

        Usuario usuarioPatrocinador = new Usuario();
        usuarioPatrocinador.setId(padreDto.getId());

        List<RedAfiliados> relaciones = redAfiliadosRepository.findByPatrocinador(usuarioPatrocinador);
        for (RedAfiliados rel : relaciones) {
            Usuario referido = rel.getReferido();
            if (referido != null) {
                NodoRedDto hijoDto = convertirAUsuarioDto(referido, nivelActual, rel.isEsDerrame());

                // CALCULAR PUNTOS Y REFERIDOS PARA CADA NODO DEL ÁRBOL
                long totalRef = contarTodosLosReferidosRecursivo(referido);
                hijoDto.setTotalReferidos((int) totalRef);

                List<Puntos> historialPuntos = puntosService.findByUsuario(referido);
                int sumaPuntosTotales = historialPuntos.stream()
                        .mapToInt(Puntos::getPuntosTotalesAcumulados)
                        .sum();
                hijoDto.setPuntosTotales(sumaPuntosTotales);

                padreDto.getHijos().add(hijoDto);
                construirHijosRecursivo(hijoDto, nivelActual + 1, nivelMaximo);
            }
        }
    }

    private NodoRedDto convertirAUsuarioDto(Usuario u, int nivel, boolean esDerrame) {
        NodoRedDto dto = new NodoRedDto();
        dto.setId(u.getId());
        dto.setNombreCompleto(u.getNombre() + " " + u.getApellido());
        dto.setEmail(u.getEmail());
        dto.setDni(u.getDni());
        dto.setTelefono(u.getTelefono());
        dto.setFotoUrl(u.getFotoUrl() != null ? u.getFotoUrl() : "/img/default.jpg");
        dto.setTipoRed(u.getTipoRed());
        dto.setRangoActual(u.getRangoActual());
        dto.setNivel(nivel);
        dto.setEsDerrame(esDerrame);
        return dto;
    }

    public List<NodoRedDto> obtenerReferidosNivelUno(Usuario usuarioLogueado, PuntosService puntosService) {
        List<RedAfiliados> relacionesNivel1 = redAfiliadosRepository.findByPatrocinador(usuarioLogueado);
        List<NodoRedDto> listaDtos = new ArrayList<>();

        for (RedAfiliados rel : relacionesNivel1) {
            Usuario referido = rel.getReferido();
            if (referido != null) {
                NodoRedDto dto = convertirAUsuarioDto(referido, 1, rel.isEsDerrame());

                long totalRef = contarTodosLosReferidosRecursivo(referido);
                dto.setTotalReferidos((int) totalRef);

                List<Puntos> historialPuntos = puntosService.findByUsuario(referido);
                int sumaPuntosTotales = historialPuntos.stream()
                        .mapToInt(Puntos::getPuntosTotalesAcumulados)
                        .sum();
                dto.setPuntosTotales(sumaPuntosTotales);

                listaDtos.add(dto);
            }
        }
        return listaDtos;
    }

    private long contarTodosLosReferidosRecursivo(Usuario usuarioPatrocinador) {
        List<RedAfiliados> hijosDirectos = redAfiliadosRepository.findByPatrocinador(usuarioPatrocinador);
        long cuenta = hijosDirectos.size();
        for (RedAfiliados h : hijosDirectos) {
            if (h.getReferido() != null) {
                cuenta += contarTodosLosReferidosRecursivo(h.getReferido());
            }
        }
        return cuenta;
    }

    public long contarReferidosDirectos(Usuario patrocinador) {
        return redAfiliadosRepository.countByPatrocinador(patrocinador);
    }

    public List<RedAfiliados> findByPatrocinador(Usuario patrocinador) {
        return redAfiliadosRepository.findByPatrocinador(patrocinador);
    }

    public List<RedAfiliados> findByPatrocinadorAndNivel(Usuario patrocinador, int nivel) {
        return redAfiliadosRepository.findByPatrocinadorAndNivel(patrocinador, nivel);
    }

    public List<RedAfiliados> findByReferido(Usuario referido) {
        return redAfiliadosRepository.findByReferido(referido);
    }

    public boolean existsByPatrocinadorAndReferido(Usuario patrocinador, Usuario referido) {
        return redAfiliadosRepository.existsByPatrocinadorAndReferido(patrocinador, referido);
    }

    public RedAfiliados save(RedAfiliados redAfiliados) {
        return redAfiliadosRepository.save(redAfiliados);
    }

    public void delete(RedAfiliados redAfiliados) {
        redAfiliadosRepository.delete(redAfiliados);
    }
}