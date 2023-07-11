package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoCarrito;
import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoVenta;
import cl.duoc.portafolio.feriavirtual.domain.Carrito;
import cl.duoc.portafolio.feriavirtual.domain.CarritoProducto;
import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Dte;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.Venta;
import cl.duoc.portafolio.feriavirtual.repository.VentaRepository;
import cl.duoc.portafolio.feriavirtual.service.DireccionService;
import cl.duoc.portafolio.feriavirtual.service.DteService;
import cl.duoc.portafolio.feriavirtual.service.PedidoService;
import cl.duoc.portafolio.feriavirtual.service.VentaService;

@Service
public class VentaServiceImpl implements VentaService {

	private VentaRepository ventaRepository;
	private DireccionService direccionService;
	private PedidoService pedidoService;
	private DteService dteService;

	@Autowired
	public VentaServiceImpl(final VentaRepository ventaRepository, final DireccionService direccionService,
			final PedidoService pedidoService, final DteService dteService) {
		this.ventaRepository = ventaRepository;
		this.direccionService = direccionService;
		this.pedidoService = pedidoService;
		this.dteService = dteService;
	}

	@Override
	public Venta crear(Usuario usuario, Carrito carrito, Direccion direccionDestino) {

		Assert.isTrue(!carrito.getEstado().equals(EstadoCarrito.COMPLETADO),
				"No puede realizar la compra porque el Carro de Compras ya ha sido procesado en otra venta");

		List<Venta> ventas = new ArrayList<>();

		// LISTA LOS LOATARIOS DE PRODUCTOS
		List<Usuario> locatarios = new ArrayList<>();
		for (CarritoProducto carritoProducto : carrito.getCarritoProducto())
			if (!locatarios.contains(carritoProducto.getProducto().getUsuario()))
				locatarios.add(carritoProducto.getProducto().getUsuario());

		// GENERA VENTA POR LOCATARIO
		for (Usuario locatario : locatarios) {

			List<CarritoProducto> carritoProductos = new ArrayList<>();
			for (CarritoProducto carritoProducto : carrito.getCarritoProducto())
				if (carritoProducto.getProducto().getUsuario().equals(locatario))
					carritoProductos.add(carritoProducto);

			Direccion direccionOrigen = direccionService.consultar(locatario, null, null, null, 0, 1).get(0);
			Dte dte = dteService.crear(locatario, carrito.getCliente(), carritoProductos);

			Venta venta = new Venta();
			venta.setLocatario(locatario);
			venta.setCliente(carrito.getCliente());
			venta.setPedido(pedidoService.crear(direccionOrigen, direccionDestino));
			venta.setDte(dte);
			venta.setEstado(EstadoVenta.GENERADA);
			venta.setMontoVenta(dte.getTotalBruto());
			venta.setFecha(LocalDate.now());
			venta.setHora(LocalTime.now());

			ventas.add(ventaRepository.save(venta));
		}

		return ventas.get(0);
	}
}
