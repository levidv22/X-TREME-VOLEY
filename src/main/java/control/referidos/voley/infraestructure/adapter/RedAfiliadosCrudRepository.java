package control.referidos.voley.infraestructure.adapter;

import control.referidos.voley.infraestructure.entity.RedAfiliados;
import control.referidos.voley.infraestructure.entity.Usuario;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface RedAfiliadosCrudRepository extends CrudRepository<RedAfiliados, Long> {
    List<RedAfiliados> findByPatrocinador(Usuario patrocinador);
    List<RedAfiliados> findByPatrocinadorAndNivel(Usuario patrocinador, int nivel);
    List<RedAfiliados> findByReferido(Usuario referido);
    boolean existsByPatrocinadorAndReferido(Usuario patrocinador, Usuario referido);
    long countByPatrocinador(Usuario patrocinador);
}