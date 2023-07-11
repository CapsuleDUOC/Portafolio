package cl.duoc.portafolio.feriavirtual.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoCarrito;
import cl.duoc.portafolio.feriavirtual.domain.Carrito;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface CarritoRepository extends CrudRepository<Carrito, Long> {

	List<Carrito> findByCliente(final Usuario usuario);

	Optional<Carrito> findByClienteAndId(final Usuario usuario, final Long id);

	List<Carrito> findByClienteAndEstado(final Usuario usuario, final EstadoCarrito pendiente);

}
