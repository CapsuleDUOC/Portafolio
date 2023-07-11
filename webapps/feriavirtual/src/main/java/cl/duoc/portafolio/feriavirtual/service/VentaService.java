package cl.duoc.portafolio.feriavirtual.service;

import java.time.LocalDate;
import java.util.List;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoVenta;
import cl.duoc.portafolio.feriavirtual.domain.Carrito;
import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.Venta;

public interface VentaService {

	Venta crear(final Usuario usuario, final Carrito carrito, final Direccion direccion);

	List<Venta> consultar(final Usuario locatario, final Usuario cliente, final EstadoVenta estado, final LocalDate fechaDesde,
			final LocalDate fechaHasta, final Integer offset, final Integer limit);

}
