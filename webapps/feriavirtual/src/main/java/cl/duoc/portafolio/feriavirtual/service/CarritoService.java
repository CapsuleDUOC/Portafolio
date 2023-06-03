package cl.duoc.portafolio.feriavirtual.service;

import java.util.List;

import cl.duoc.portafolio.dto.v10.feriavirtual.InputCarritoProductoActualizar;
import cl.duoc.portafolio.feriavirtual.domain.Carrito;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface CarritoService {

	List<Carrito> consultar(final Usuario usuario);

	Carrito obtener(final Usuario usuario, final Long id);

	Boolean actualizar(final Usuario usuario, final Long id, final InputCarritoProductoActualizar inputDTO);

}
