package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoCarrito;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputCarritoProductoActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoOperacionCarrito;
import cl.duoc.portafolio.feriavirtual.domain.Carrito;
import cl.duoc.portafolio.feriavirtual.domain.Producto;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.repository.CarritoRepository;
import cl.duoc.portafolio.feriavirtual.service.CarritoService;
import cl.duoc.portafolio.feriavirtual.service.ProductoService;

@Service
public class CarritoServiceImpl implements CarritoService {

	private CarritoRepository carritoRepository;
	private ProductoService productoService;

	@Autowired
	public CarritoServiceImpl(final CarritoRepository carritoRepository, ProductoService productoService) {
		this.carritoRepository = carritoRepository;
		this.productoService = productoService;
	}

	@Override
	public List<Carrito> consultar(Usuario usuario) {
		return carritoRepository.findByCliente(usuario);
	}

	@Override
	public Carrito obtener(Usuario usuario, Long id) {

		Optional<Carrito> _carrito = carritoRepository.findByClienteAndId(usuario, id);
		Assert.isTrue(_carrito.isPresent(), "No existe carrito creado para el usuario");

		return _carrito.get();
	}

	@Override
	public Boolean actualizar(Usuario usuario, Long id, InputCarritoProductoActualizar inputDTO) {

		Carrito carrito;
		Optional<Carrito> _carrito = carritoRepository.findByClienteAndId(usuario, id);
		if (_carrito.isPresent()) {
			carrito = _carrito.get();
		} else {
			carrito = crear(usuario);
		}

		Producto producto = productoService.obtener(inputDTO.getProductoID());

		if (inputDTO.getOperacion().equals(TipoOperacionCarrito.AGREGAR) && !carrito.getProducto().contains(producto)) {
			carrito.getProducto().add(producto);

		} else if (inputDTO.getOperacion().equals(TipoOperacionCarrito.ELIMINAR)
				&& carrito.getProducto().contains(producto)) {

			carrito.getProducto().remove(producto);
		}

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
