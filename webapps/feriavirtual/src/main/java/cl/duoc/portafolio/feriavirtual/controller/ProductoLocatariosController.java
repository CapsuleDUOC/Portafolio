package cl.duoc.portafolio.feriavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.portafolio.dto.v10.feriavirtual.LocatarioPrecioType;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputProductoLocatariosConsultar;
import cl.duoc.portafolio.dto.v10.feriavirtual.ProductoLocatarioType;
import cl.duoc.portafolio.dto.v10.feriavirtual.ResumenProductoType;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoProducto;
import cl.duoc.portafolio.feriavirtual.domain.Producto;
import cl.duoc.portafolio.feriavirtual.service.ProductoService;

@RestController
@RequestMapping("/locales/productos/v10")
public class ProductoLocatariosController {

	private ProductoService productoService;

	@Autowired
	public ProductoLocatariosController(final ProductoService productoService) {
		this.productoService = productoService;
	}

	@GetMapping
	ResponseEntity<OutputProductoLocatariosConsultar> consultar(
			@RequestParam(name = "tipoProducto", required = false) final TipoProducto tipoProducto,
			@RequestParam(name = "partNombre", required = false) final String partNombre,
			@RequestParam(name = "offset", defaultValue = "0") Integer offset,
			@RequestParam(name = "limit", defaultValue = "100") Integer limit){

		final List<Producto> productos = productoService.consultarDistintosNombres(tipoProducto, partNombre, offset,
				limit);

		final OutputProductoLocatariosConsultar outputDTO = new OutputProductoLocatariosConsultar();

		for (Producto producto : productos) {
			ProductoLocatarioType productoLocatarioType = new ProductoLocatarioType();
			ResumenProductoType resumenProducto = new ResumenProductoType();
			resumenProducto.setNombre(producto.getNombre());
			resumenProducto.setTipo(producto.getTipo());
			productoLocatarioType.setProducto(resumenProducto);

			for (Producto registro : productoService.consultarPorNombre(producto.getNombre())) {

				LocatarioPrecioType locatarioPrecio = new LocatarioPrecioType();
				locatarioPrecio.setID(registro.getUsuario().getId());
				locatarioPrecio.setIdentificacion(registro.getUsuario().getIdentificacion());
				locatarioPrecio.setNombre(registro.getUsuario().getNombre());
				locatarioPrecio.setEstado(registro.getUsuario().getEstado());
				locatarioPrecio.setPrecio(registro.getPrecio());

				productoLocatarioType.getLocatarios().add(locatarioPrecio);
			}

			outputDTO.getRegistro().add(productoLocatarioType);
		}

		return ResponseEntity.ok(outputDTO);
	}
}
