package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoPedido;
import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Pedido;
import cl.duoc.portafolio.feriavirtual.repository.PedidoRepository;
import cl.duoc.portafolio.feriavirtual.service.PedidoService;

@Service
public class PedidoServiceImpl implements PedidoService {

	private PedidoRepository pedidoRepository;

	@Autowired
	public PedidoServiceImpl(final PedidoRepository pedidoRepository) {
		this.pedidoRepository = pedidoRepository;
	}

	@Override
	public Pedido crear(Direccion direccionOrigen, Direccion direccionDestino) {
		
		Pedido pedido = new Pedido();
		pedido.setEstado(EstadoPedido.GENERADO);
		pedido.setDireccionOrigen(direccionOrigen.getDireccion() + ", " + direccionOrigen.getComuna() + ", "
				+ direccionOrigen.getCiudad());
		pedido.setDireccionDestino(direccionDestino.getDireccion() + ", " + direccionDestino.getComuna() + ", "
				+ direccionDestino.getCiudad());
		pedido.setMontoDespacho(1000L);
		pedido.setFecha(LocalDate.now());
		pedido.setHora(LocalTime.now());
		
		return pedidoRepository.save(pedido);
	}
}
