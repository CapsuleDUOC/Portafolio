package cl.duoc.portafolio.feriavirtual.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cl.duoc.portafolio.feriavirtual.domain.Producto;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface ProductoRepository extends CrudRepository<Producto, Long>{

	Optional<Producto> findByUsuarioAndCodigo(final Usuario usuario, final String codigo);

	Optional<Producto> findByUsuarioAndId(final Usuario usuario, final Long id);

}
