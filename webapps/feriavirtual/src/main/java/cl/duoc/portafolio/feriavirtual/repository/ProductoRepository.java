package cl.duoc.portafolio.feriavirtual.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cl.duoc.portafolio.dto.v10.feriavirtual.TipoProducto;
import cl.duoc.portafolio.feriavirtual.domain.Producto;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface ProductoRepository extends CrudRepository<Producto, Long> {

	Optional<Producto> findByUsuarioAndCodigo(final Usuario usuario, final String codigo);

	Optional<Producto> findByUsuarioAndId(final Usuario usuario, final Long id);

	@Query("SELECT DISTINCT(nombre) FROM Producto")
	List<String> findDistinctNombre();

	Optional<Producto> findFirstByNombreContaining(final String nombre);

	Optional<Producto> findFirstByNombreContainingAndTipo(final String nombre, final TipoProducto tipoProducto);

}
