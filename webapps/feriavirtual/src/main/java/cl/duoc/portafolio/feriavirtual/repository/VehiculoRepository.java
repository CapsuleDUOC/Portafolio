package cl.duoc.portafolio.feriavirtual.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.Vehiculo;

public interface VehiculoRepository extends CrudRepository<Vehiculo, Long>{

	Optional<Vehiculo> findByUsuarioAndPatente(final Usuario usuario, final String patente);

	Optional<Vehiculo> findByUsuarioAndId(final Usuario usuario, final Long id);

}
