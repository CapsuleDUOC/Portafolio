package cl.duoc.portafolio.feriavirtual.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface DireccionRepository extends CrudRepository<Direccion, Long>{

	Optional<Direccion> findByUsuarioAndId(final Usuario usuario, final Long id);

}
