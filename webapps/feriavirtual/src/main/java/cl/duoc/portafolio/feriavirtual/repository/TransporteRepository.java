package cl.duoc.portafolio.feriavirtual.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cl.duoc.portafolio.feriavirtual.domain.Transporte;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface TransporteRepository extends CrudRepository<Transporte, Long>{

	Optional<Transporte> findByTransportistaAndId(Usuario transportista, Long id);

}
