package cl.duoc.portafolio.feriavirtual.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cl.duoc.portafolio.dto.JAXBUtil;
import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoArchivo;
import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoProducto;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputProductoActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputProductoCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputProductoConsultar;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputProductoCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputProductoObtener;
import cl.duoc.portafolio.dto.v10.feriavirtual.ProductoType;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoProducto;
import cl.duoc.portafolio.dto.v10.feriavirtual.UnidadMedida;
import cl.duoc.portafolio.feriavirtual.domain.Producto;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.service.ProductoService;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;

@RestController
@RequestMapping("/{usuarioIdentificacion}/producto/v10")
public class ProductoController {

	private UsuarioService usuarioService;
	private ProductoService productoService;

	@Autowired
	public ProductoController(final UsuarioService usuarioService, final ProductoService productoService) {
		this.usuarioService = usuarioService;
		this.productoService = productoService;
	}

	@PostMapping
	ResponseEntity<OutputProductoCrear> crear(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestBody final InputProductoCrear inputDTO) {

		JAXBUtil.validarSchema(InputProductoCrear.class, inputDTO);

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Producto producto = productoService.crear(usuario, inputDTO);

		final OutputProductoCrear outputDTO = new OutputProductoCrear();
		outputDTO.setID(producto.getId());
		outputDTO.setCodigo(producto.getCodigo());
		outputDTO.setTipo(producto.getTipo());
		outputDTO.setNombre(producto.getNombre());
		outputDTO.setUnidadMedida(producto.getUnidadMedida());
		outputDTO.setPrecio(producto.getPrecio());
		outputDTO.setEstado(producto.getEstado());
		outputDTO.setRegistroInstante(producto.getRegistroInstante());

		if (producto.getArchivoImagen() != null)
			outputDTO.setImagen(ServletUriComponentsBuilder.fromCurrentRequest().path("/img/")
					.path(producto.getId().toString()).build().toUriString());

		return ResponseEntity.created(
				ServletUriComponentsBuilder.fromCurrentRequest().path(producto.getId().toString()).build().toUri())
				.body(outputDTO);
	}

	@PostMapping("/{id}")
	ResponseEntity<Boolean> actualizar(@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@PathVariable(name = "id") final Long id, @RequestBody final InputProductoActualizar inputDTO) {

		JAXBUtil.validarSchema(InputProductoActualizar.class, inputDTO);

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Producto producto = productoService.obtener(usuario, id);
		final Boolean actualizar = productoService.actualizar(producto, inputDTO);

		return ResponseEntity.ok(actualizar);
	}

	@GetMapping
	ResponseEntity<OutputProductoConsultar> consultar(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestParam(name = "tipoProducto", required = false) final TipoProducto tipoProducto,
			@RequestParam(name = "partCodigo", required = false) final String partCodigo,
			@RequestParam(name = "partNombre", required = false) final String partNombre,
			@RequestParam(name = "unidadMedida", required = false) final UnidadMedida unidadMedida,
			@RequestParam(name = "estado", required = false) final EstadoProducto estado,
			@RequestParam(name = "offset", defaultValue = "0") Integer offset,
			@RequestParam(name = "limit", defaultValue = "100") Integer limit) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final List<Producto> productos = productoService.consultar(usuario, tipoProducto, partCodigo, partNombre,
				unidadMedida, estado, offset, limit);

		final OutputProductoConsultar outputDTO = new OutputProductoConsultar();

		ProductoType productoType;
		for (Producto producto : productos) {
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
				productoType.setImagen(ServletUriComponentsBuilder.fromCurrentRequest().path("/img/")
						.path(producto.getId().toString()).replaceQuery(null).build().toUriString());

			outputDTO.getRegistro().add(productoType);
		}

		return ResponseEntity.ok(outputDTO);
	}

	@GetMapping("/{id}")
	ResponseEntity<OutputProductoObtener> obtener(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@PathVariable(name = "id") final Long id) throws IOException {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Producto producto = productoService.obtener(usuario, id);

		final OutputProductoObtener outputDTO = new OutputProductoObtener();
		outputDTO.setID(producto.getId());
		outputDTO.setCodigo(producto.getCodigo());
		outputDTO.setTipo(producto.getTipo());
		outputDTO.setNombre(producto.getNombre());
		outputDTO.setUnidadMedida(producto.getUnidadMedida());
		outputDTO.setPrecio(producto.getPrecio());
		outputDTO.setEstado(producto.getEstado());
		outputDTO.setRegistroInstante(producto.getRegistroInstante());

		if (producto.getArchivoImagen() != null) {
			outputDTO.setImagen(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString().replace("/" + id,
					"/img/" + id));
		}

		return ResponseEntity.ok(outputDTO);
	}

	@GetMapping("/img/{id}")
	ResponseEntity<byte[]> obtenerImagen(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@PathVariable(name = "id") final Long id) throws Exception {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Producto producto = productoService.obtener(usuario, id);

		if (producto.getArchivoImagen() == null
				|| producto.getArchivoImagen().getEstado().equals(EstadoArchivo.ELIMINADO)) {
			throw new Exception("Producto no posee imagen");
		}

		byte[] imageBytes;
		if (producto.getArchivoImagen().getEstado().equals(EstadoArchivo.BASE_DATOS))
			imageBytes = producto.getArchivoImagen().getBytes();

		else
			imageBytes = Files.readAllBytes(Paths.get(producto.getArchivoImagen().getPath()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		headers.setContentLength(imageBytes.length);

		return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	}
}
