package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDateTime;
import java.util.Iterator;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CarritoServiceImpl implements CarritoService {

	private CarritoRepository carritoRepository;
	private ProductoService productoService;

	@Autowired
	public CarritoServiceImpl(final CarritoRepository carritoRepository, final ProductoService productoService) {
		this.carritoRepository = carritoRepository;
		this.productoService = productoService;
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

		try {
			Carrito carrito;
			List<Carrito> carritos = consultarPendiente(usuario);
			if (!carritos.isEmpty()) {
				carrito = carritos.get(0);
			} else {
				carrito = crear(usuario);
			}

			Producto producto = productoService.obtener(inputDTO.getProductoID());
			Boolean isProducto = false;

			Iterator<CarritoProducto> iterator = carrito.getCarritoProducto().iterator();
			while (iterator.hasNext()) {
				CarritoProducto carritoProducto = iterator.next();

				// CARRITO TIENE EL PRODUCTO
				if (carritoProducto.getProducto().equals(producto)
						&& inputDTO.getOperacion().equals(TipoOperacionCarrito.AGREGAR)) {

					carritoProducto.setCantidad(
							carritoProducto.getCantidad() + Long.valueOf(inputDTO.getCantidad()).doubleValue());
					isProducto = true;

				} else if (carritoProducto.getProducto().equals(producto)
						&& inputDTO.getOperacion().equals(TipoOperacionCarrito.ELIMINAR)) {
					carritoProducto.setCantidad(
							carritoProducto.getCantidad() - Long.valueOf(inputDTO.getCantidad()).doubleValue());

					if (carritoProducto.getCantidad() <= 0)
						carritoProducto.setCantidad(Long.valueOf(0).doubleValue());
					iterator.remove();
					isProducto = true;
				}
			}

			// CARRITO NO TIENE EL PRODUCTO
			if (!isProducto && inputDTO.getOperacion().equals(TipoOperacionCarrito.AGREGAR))
				carrito.getCarritoProducto().add(getCarritoProducto(carrito, producto, inputDTO.getCantidad()));

			carritoRepository.save(carrito);

			return true;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException("Error actualizando carrito: " + e.getMessage(), e);
		}

	}

	private CarritoProducto getCarritoProducto(Carrito carrito, Producto producto, Long cantidad) {

		CarritoProducto carritoProducto = new CarritoProducto();
		carritoProducto.setCarrito(carrito);
		carritoProducto.setProducto(producto);
		carritoProducto.setCantidad(cantidad.doubleValue());

		return carritoProducto;

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
