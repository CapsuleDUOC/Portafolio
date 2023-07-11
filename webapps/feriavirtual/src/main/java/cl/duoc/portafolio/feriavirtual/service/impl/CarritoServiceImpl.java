package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoCarrito;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputCarritoProductoActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoOperacionCarrito;
import cl.duoc.portafolio.feriavirtual.domain.Carrito;
import cl.duoc.portafolio.feriavirtual.domain.CarritoProducto;
import cl.duoc.portafolio.feriavirtual.domain.Producto;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.repository.CarritoRepository;
import cl.duoc.portafolio.feriavirtual.service.CarritoService;
import cl.duoc.portafolio.feriavirtual.service.ProductoService;

@Service
public class CarritoServiceImpl implements CarritoService {

	private CarritoRepository carritoRepository;
	private ProductoService productoService;
	private CarritoProductoRepository carritoProductoRepository;

	@Autowired
	public CarritoServiceImpl(final CarritoRepository carritoRepository, final ProductoService productoService,
			final CarritoProductoRepository carritoProductoRepository) {
		this.carritoRepository = carritoRepository;
		this.productoService = productoService;
		this.carritoProductoRepository = carritoProductoRepository;
	}

	@Override
	public List<Carrito> consultarPendiente(Usuario usuario) {
		return carritoRepository.findByClienteAndEstado(usuario, EstadoCarrito.PENDIENTE);
	}

	@Override
	public Carrito obtener(Usuario usuario, Long id) {

		Optional<Carrito> _carrito = carritoRepository.findByClienteAndId(usuario, id);
		Assert.isTrue(_carrito.isPresent(), "No existe carrito creado para el usuario");

		return _carrito.get();
	}

	@Override
	public Boolean actualizar(Usuario usuario, InputCarritoProductoActualizar inputDTO) {

		Carrito carrito;
		List<Carrito> carritos = consultarPendiente(usuario);
		if (!carritos.isEmpty()) {
			carrito = carritos.get(0);
		} else {
			carrito = crear(usuario);
		}

		Producto producto = productoService.obtener(inputDTO.getProductoID());
		List<Producto> productosCarrito = new ArrayList<>();

		for (CarritoProducto carritoProducto : carrito.getCarritoProducto())
			productosCarrito.add(carritoProducto.getProducto());

		if (productosCarrito.contains(producto))
			actualizarCarritoProducto(carrito, producto, inputDTO.getCantidad(), inputDTO.getOperacion());
		else
			crearCarritoProducto(carrito, producto, inputDTO.getCantidad(), inputDTO.getOperacion());

		carritoRepository.save(carrito);

		return true;
	}

	private void crearCarritoProducto(Carrito carrito, Producto producto, Long cantidad,
			TipoOperacionCarrito operacion) {

		if (operacion.equals(TipoOperacionCarrito.ELIMINAR))
			return;

		CarritoProducto carritoProducto = new CarritoProducto();
		carritoProducto.setCarrito(carrito);
		carritoProducto.setProducto(producto);
		carritoProducto.setCantidad(cantidad.doubleValue());

		carritoProductoRepository.save(carritoProducto);

	}

	private void actualizarCarritoProducto(Carrito carrito, Producto producto, Long cantidad,
			TipoOperacionCarrito operacion) {

		CarritoProducto carritoProducto = carritoProductoRepository.findByCarritoAndProducto(carrito, producto);

		if (operacion.equals(TipoOperacionCarrito.AGREGAR)) {
			carritoProducto.setCantidad(carritoProducto.getCantidad() + cantidad.doubleValue());
			carritoProductoRepository.save(carritoProducto);
		} else if (operacion.equals(TipoOperacionCarrito.ELIMINAR)) {
			carritoProducto.setCantidad(carritoProducto.getCantidad() - cantidad.doubleValue());
			if (carritoProducto.getCantidad() > 0)
				carritoProductoRepository.save(carritoProducto);
			else
				carritoProductoRepository.delete(carritoProducto);
		}
	}
	
	@Override
	public Boolean actualizarEstado(Usuario usuario, Long carritoID, EstadoCarrito estado) {
		
		Carrito carrito = obtener(usuario, carritoID);
		carrito.setEstado(estado);
		carritoRepository.save(carrito);
		
		return true;
	}

	private Carrito crear(Usuario usuario) {

		Carrito carrito = new Carrito();
		carrito.setCliente(usuario);
		carrito.setEstado(EstadoCarrito.PENDIENTE);
		carrito.setRegistroInstante(LocalDateTime.now());

		return carritoRepository.save(carrito);
	}
}
