package cl.duoc.portafolio.feriavirtual.service;

import java.util.List;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoCarrito;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputCarritoProductoActualizar;
import cl.duoc.portafolio.feriavirtual.domain.Carrito;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface CarritoService {

	List<Carrito> consultarPendiente(final Usuario usuario);

	Carrito obtener(final Usuario usuario, final Long id);

	Boolean actualizar(final Usuario usuario, final InputCarritoProductoActualizar inputDTO);

	Boolean actualizarEstado(final Usuario usuario, final Long id, final EstadoCarrito completado);

}
