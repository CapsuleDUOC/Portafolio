package cl.duoc.portafolio.feriavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.portafolio.dto.JAXBUtil;
import cl.duoc.portafolio.dto.v10.feriavirtual.CarritoType;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputCarritoProductoActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputCarritoConsultar;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputCarritoObtener;
import cl.duoc.portafolio.dto.v10.feriavirtual.ProductoType;
import cl.duoc.portafolio.feriavirtual.domain.Carrito;
import cl.duoc.portafolio.feriavirtual.domain.Producto;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.service.CarritoService;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;

@RestController
@RequestMapping("/{usuarioIdentificacion}/carrito/v10")
public class CarritoController {

	private UsuarioService usuarioService;
	private CarritoService carritoService;

	@Autowired
	public CarritoController(final UsuarioService usuarioService, final CarritoService carritoService) {
		this.usuarioService = usuarioService;
		this.carritoService = carritoService;
	}

	@GetMapping
	ResponseEntity<OutputCarritoConsultar> consultar(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final List<Carrito> carritos = carritoService.consultar(usuario);

		final OutputCarritoConsultar outputDTO = new OutputCarritoConsultar();

		CarritoType carritoType;
		for (Carrito carrito : carritos) {
			carritoType = new CarritoType();
			carritoType.setID(carrito.getId());
			carritoType.setEstado(carrito.getEstado());
			carritoType.setRegistroInstante(carrito.getRegistroInstante());
			
			outputDTO.getRegistro().add(carritoType);
		}

		return ResponseEntity.ok(outputDTO);
	}

	@GetMapping("/{id}")
	ResponseEntity<OutputCarritoObtener> obtener(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@PathVariable(name = "id") final Long id) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Carrito carrito = carritoService.obtener(usuario, id);

		final OutputCarritoObtener outputDTO = new OutputCarritoObtener();
		outputDTO.setID(carrito.getId());
		outputDTO.setEstado(carrito.getEstado());
		outputDTO.setRegistroInstante(carrito.getRegistroInstante());

		ProductoType productoType;
		for (Producto producto : carrito.getProducto()) {
			productoType = new ProductoType();
			productoType.setID(producto.getId());
			productoType.setCodigo(producto.getCodigo());
			productoType.setTipo(producto.getTipo());
			productoType.setNombre(producto.getNombre());
			productoType.setUnidadMedida(producto.getUnidadMedida());
			productoType.setPrecio(producto.getPrecio());
			productoType.setEstado(producto.getEstado());
			productoType.setRegistroInstante(producto.getRegistroInstante());

			if (producto.getArchivoImagen() != null)
				productoType.setImagen(true);

			outputDTO.getProducto().add(productoType);
		}

		return ResponseEntity.ok(outputDTO);
	}

	@PostMapping
	ResponseEntity<Boolean> actualizar(@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestBody final InputCarritoProductoActualizar inputDTO) {

		JAXBUtil.validarSchema(InputCarritoProductoActualizar.class, inputDTO);
		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Boolean actualizar = carritoService.actualizar(usuario, inputDTO);

		return ResponseEntity.ok(actualizar);

	}
}
