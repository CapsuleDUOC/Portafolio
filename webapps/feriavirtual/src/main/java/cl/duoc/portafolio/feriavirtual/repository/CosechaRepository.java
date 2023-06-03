package cl.duoc.portafolio.feriavirtual.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cl.duoc.portafolio.feriavirtual.domain.Cosecha;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface CosechaRepository extends CrudRepository<Cosecha, Long>{

	Optional<Cosecha> findByAgricultorAndId(final Usuario agricultor, final Long id);

}
