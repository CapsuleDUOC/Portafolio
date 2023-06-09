package cl.duoc.portafolio.feriavirtual.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.portafolio.dto.JAXBUtil;
import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoCarrito;
import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoVenta;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputVentaCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputVentaConsultar;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputVentaCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.PedidoType;
import cl.duoc.portafolio.dto.v10.feriavirtual.UsuarioType;
import cl.duoc.portafolio.dto.v10.feriavirtual.VentaType;
import cl.duoc.portafolio.feriavirtual.domain.Carrito;
import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.Venta;
import cl.duoc.portafolio.feriavirtual.service.CarritoService;
import cl.duoc.portafolio.feriavirtual.service.DireccionService;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;
import cl.duoc.portafolio.feriavirtual.service.VentaService;

@RestController
@RequestMapping("/{usuarioIdentificacion}/venta/cliente/v10")
public class VentaClienteController {

	private UsuarioService usuarioService;
	private CarritoService carritoService;
	private DireccionService direccionService;
	private VentaService ventaService;

	@Autowired
	public VentaClienteController(final UsuarioService usuarioService, final CarritoService carritoService,
			final DireccionService direccionService, final VentaService ventaService) {
		this.usuarioService = usuarioService;
		this.carritoService = carritoService;
		this.direccionService = direccionService;
		this.ventaService = ventaService;

	}

	@PostMapping
	ResponseEntity<OutputVentaCrear> crear(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestBody final InputVentaCrear inputDTO) {

		JAXBUtil.validarSchema(InputVentaCrear.class, inputDTO);
		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Carrito carrito = carritoService.obtener(usuario, inputDTO.getCarritoID());
		final Direccion direccion = direccionService.obtener(usuario, inputDTO.getDireccionID());

		final Venta venta = ventaService.crear(usuario, carrito, direccion);

		carritoService.actualizarEstado(usuario, inputDTO.getCarritoID(), EstadoCarrito.COMPLETADO);

		final OutputVentaCrear outputDTO = new OutputVentaCrear();

		final UsuarioType locatario = new UsuarioType();
		locatario.setID(venta.getLocatario().getId());
		locatario.setTipoIdentificacion(venta.getLocatario().getTipoIdentificacion());
		locatario.setIdentificacion(venta.getLocatario().getIdentificacion());
		locatario.setEstado(venta.getLocatario().getEstado());
		locatario.setNombre(venta.getLocatario().getNombre());
		locatario.setTelefono(venta.getLocatario().getTelefono());
		locatario.setRegistroInstante(venta.getLocatario().getRegistroInstante());

		final UsuarioType cliente = new UsuarioType();
		cliente.setID(venta.getCliente().getId());
		cliente.setTipoIdentificacion(venta.getCliente().getTipoIdentificacion());
		cliente.setIdentificacion(venta.getCliente().getIdentificacion());
		cliente.setEstado(venta.getCliente().getEstado());
		cliente.setNombre(venta.getCliente().getNombre());
		cliente.setTelefono(venta.getCliente().getTelefono());
		cliente.setRegistroInstante(venta.getCliente().getRegistroInstante());

		if (venta.getPedido() != null) {
			final PedidoType pedido = new PedidoType();

			if (venta.getPedido().getDespachador() != null) {
				final UsuarioType despachador = new UsuarioType();
				despachador.setID(venta.getPedido().getDespachador().getId());
				despachador.setTipoIdentificacion(venta.getPedido().getDespachador().getTipoIdentificacion());
				despachador.setIdentificacion(venta.getPedido().getDespachador().getIdentificacion());
				despachador.setEstado(venta.getPedido().getDespachador().getEstado());
				despachador.setNombre(venta.getPedido().getDespachador().getNombre());
				despachador.setTelefono(venta.getPedido().getDespachador().getTelefono());
				despachador.setRegistroInstante(venta.getPedido().getDespachador().getRegistroInstante());

				pedido.setDespachador(despachador);

			}

			pedido.setID(venta.getPedido().getId());
			pedido.setEstado(venta.getPedido().getEstado());
			pedido.setPatenteVehiculo(venta.getPedido().getPatenteVehiculo());
			pedido.setDireccionOrigen(venta.getPedido().getDireccionOrigen());
			pedido.setDireccionDestino(venta.getPedido().getDireccionDestino());
			pedido.setMontoDespacho(venta.getPedido().getMontoDespacho());
			pedido.setRegistroInstante(LocalDateTime.of(venta.getPedido().getFecha(), venta.getPedido().getHora()));

			outputDTO.setPedido(pedido);
		}

		outputDTO.setID(venta.getId());
		outputDTO.setLocatario(locatario);
		outputDTO.setCliente(cliente);
		outputDTO.setMonto(venta.getMontoVenta());
		outputDTO.setRegistroInstante(LocalDateTime.of(venta.getFecha(), venta.getHora()));

		return ResponseEntity.ok(outputDTO);
	}

	@GetMapping
	ResponseEntity<OutputVentaConsultar> consultar(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestParam(name = "estado", required = false) final EstadoVenta estado,
			@RequestParam(name = "fechaDesde", required = false) final @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
			@RequestParam(name = "fechaHasta", required = false) final @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
			@RequestParam(name = "offset", defaultValue = "0") Integer offset,
			@RequestParam(name = "limit", defaultValue = "100") Integer limit) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);

		final List<Venta> ventas = ventaService.consultar(null, usuario, estado, fechaDesde, fechaHasta, offset, limit);

		final OutputVentaConsultar outputDTO = new OutputVentaConsultar();
		VentaType ventaType;
		Long total = 0L;
		for (Venta venta : ventas) {

			ventaType = new VentaType();

			final UsuarioType locatario = new UsuarioType();
			locatario.setID(venta.getLocatario().getId());
			locatario.setTipoIdentificacion(venta.getLocatario().getTipoIdentificacion());
			locatario.setIdentificacion(venta.getLocatario().getIdentificacion());
			locatario.setEstado(venta.getLocatario().getEstado());
			locatario.setNombre(venta.getLocatario().getNombre());
			locatario.setTelefono(venta.getLocatario().getTelefono());
			locatario.setRegistroInstante(venta.getLocatario().getRegistroInstante());

			final UsuarioType cliente = new UsuarioType();
			cliente.setID(venta.getCliente().getId());
			cliente.setTipoIdentificacion(venta.getCliente().getTipoIdentificacion());
			cliente.setIdentificacion(venta.getCliente().getIdentificacion());
			cliente.setEstado(venta.getCliente().getEstado());
			cliente.setNombre(venta.getCliente().getNombre());
			cliente.setTelefono(venta.getCliente().getTelefono());
			cliente.setRegistroInstante(venta.getCliente().getRegistroInstante());

			if (venta.getPedido() != null) {
				final PedidoType pedido = new PedidoType();

				if (venta.getPedido().getDespachador() != null) {
					final UsuarioType despachador = new UsuarioType();
					despachador.setID(venta.getPedido().getDespachador().getId());
					despachador.setTipoIdentificacion(venta.getPedido().getDespachador().getTipoIdentificacion());
					despachador.setIdentificacion(venta.getPedido().getDespachador().getIdentificacion());
					despachador.setEstado(venta.getPedido().getDespachador().getEstado());
					despachador.setNombre(venta.getPedido().getDespachador().getNombre());
					despachador.setTelefono(venta.getPedido().getDespachador().getTelefono());
					despachador.setRegistroInstante(venta.getPedido().getDespachador().getRegistroInstante());

					pedido.setDespachador(despachador);

				}

				pedido.setID(venta.getPedido().getId());
				pedido.setEstado(venta.getPedido().getEstado());
				pedido.setPatenteVehiculo(venta.getPedido().getPatenteVehiculo());
				pedido.setDireccionOrigen(venta.getPedido().getDireccionOrigen());
				pedido.setDireccionDestino(venta.getPedido().getDireccionDestino());
				pedido.setMontoDespacho(venta.getPedido().getMontoDespacho());
				pedido.setRegistroInstante(LocalDateTime.of(venta.getPedido().getFecha(), venta.getPedido().getHora()));

				total = total + venta.getPedido().getMontoDespacho();

				ventaType.setPedido(pedido);
			}

			ventaType.setID(venta.getId());
			ventaType.setLocatario(locatario);
			ventaType.setCliente(cliente);
			ventaType.setMonto(venta.getMontoVenta());
			ventaType.setRegistroInstante(LocalDateTime.of(venta.getFecha(), venta.getHora()));

			total = total + venta.getMontoVenta();

			outputDTO.getRegistro().add(ventaType);
		}

		outputDTO.setCantidad(ventas.size());
		outputDTO.setTotal(total);

		return ResponseEntity.ok(outputDTO);
	}

}
