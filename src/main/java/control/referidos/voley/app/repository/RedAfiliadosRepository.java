package control.referidos.voley.app.repository;

import control.referidos.voley.infraestructure.entity.RedAfiliados;
import control.referidos.voley.infraestructure.entity.Usuario;
import java.util.List;

public interface RedAfiliadosRepository {
    List<RedAfiliados> findByPatrocinador(Usuario patrocinador);
    List<RedAfiliados> findByPatrocinadorAndNivel(Usuario patrocinador, int nivel);
    List<RedAfiliados> findByReferido(Usuario referido);
    boolean existsByPatrocinadorAndReferido(Usuario patrocinador, Usuario referido);
    long countByPatrocinador(Usuario patrocinador);
    RedAfiliados save(RedAfiliados redAfiliados);
    void delete(RedAfiliados redAfiliados);
}