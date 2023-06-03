package cl.duoc.portafolio.feriavirtual.service;

import java.util.List;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoProducto;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputProductoActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputProductoCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoProducto;
import cl.duoc.portafolio.dto.v10.feriavirtual.UnidadMedida;
import cl.duoc.portafolio.feriavirtual.domain.Producto;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface ProductoService {

	Producto crear(final Usuario usuario, final InputProductoCrear inputDTO);

	Producto obtener(final Usuario usuario, final Long id);

	Boolean actualizar(final Producto producto, final InputProductoActualizar inputDTO);

	List<Producto> consultar(final Usuario usuario, final TipoProducto tipoProducto, final String partCodigo,
			final String partNombre, final UnidadMedida unidadMedida, final EstadoProducto estado, final Integer offset,
			final Integer limit);

	Producto obtener(final Usuario usuario, final String codigo);

	Producto obtener(final Long productoID);

}
