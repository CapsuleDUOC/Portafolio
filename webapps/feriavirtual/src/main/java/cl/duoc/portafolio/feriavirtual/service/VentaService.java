package cl.duoc.portafolio.feriavirtual.service;

import cl.duoc.portafolio.feriavirtual.domain.Carrito;
import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.Venta;

public interface VentaService {

	Venta crear(final Usuario usuario, final Carrito carrito, final Direccion direccion);

}
