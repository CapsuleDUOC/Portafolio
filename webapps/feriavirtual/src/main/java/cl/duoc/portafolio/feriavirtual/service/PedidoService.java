package cl.duoc.portafolio.feriavirtual.service;

import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Pedido;

public interface PedidoService {

	Pedido crear(final Direccion direccionOrigen, final Direccion direccionDestino);

}
