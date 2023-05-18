package cl.duoc.portafolio.feriavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cl.duoc.portafolio.dto.JAXBUtil;
import cl.duoc.portafolio.dto.v10.feriavirtual.DireccionType;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputDireccionActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputDireccionCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputDireccionConsultar;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputDireccionCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputDireccionObtener;
import cl.duoc.portafolio.dto.v10.feriavirtual.UbigeoType;
import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.service.DireccionService;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;

@RestController
@RequestMapping("/{usuarioIdentificacion}/direccion/v10")
public class DireccionController {

	private UsuarioService usuarioService;
	private DireccionService direccionService;

	@Autowired
	public DireccionController(final UsuarioService usuarioService, final DireccionService direccionService) {
		this.usuarioService = usuarioService;
		this.direccionService = direccionService;
	}

	@PostMapping
	ResponseEntity<OutputDireccionCrear> crear(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestBody final InputDireccionCrear inputDTO) {

		JAXBUtil.validarSchema(InputDireccionCrear.class, inputDTO);

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Direccion direccion = direccionService.crear(usuario, inputDTO);

		final OutputDireccionCrear outputDTO = new OutputDireccionCrear();
		outputDTO.setID(direccion.getId());
		outputDTO.setDireccion(direccion.getDireccion());
		outputDTO.setComuna(direccion.getComuna());
		outputDTO.setCiudad(direccion.getCiudad());

		if (direccion.getUbigeoLat() != null && direccion.getUbigeoLong() != null) {
			final UbigeoType ubigeoType = new UbigeoType();
			ubigeoType.setLatitud(direccion.getUbigeoLat());
			ubigeoType.setLongitud(direccion.getUbigeoLong());
			outputDTO.setUbigeo(ubigeoType);
		}

		return ResponseEntity.created(
				ServletUriComponentsBuilder.fromCurrentRequest().path(direccion.getId().toString()).build().toUri())
				.body(outputDTO);
	}

	@PostMapping("/{id}")
	ResponseEntity<Boolean> actualizar(@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@PathVariable(name = "id") final Long id, @RequestBody final InputDireccionActualizar inputDTO) {

		JAXBUtil.validarSchema(InputDireccionActualizar.class, inputDTO);

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Direccion direccion = direccionService.obtener(usuario, id);
		final Boolean actualizar = direccionService.actualizar(direccion, inputDTO);

		return ResponseEntity.ok(actualizar);
	}

	@GetMapping
	ResponseEntity<OutputDireccionConsultar> consultar(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestParam(name = "partDireccion", required = false) final String partDireccion,
			@RequestParam(name = "partComuna", required = false) final String partComuna,
			@RequestParam(name = "partCiudad", required = false) final String partCiudad,
			@RequestParam(name = "offset", defaultValue = "0") Integer offset,
			@RequestParam(name = "limit", defaultValue = "100") Integer limit) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		List<Direccion> direcciones = direccionService.consultar(usuario, partDireccion, partComuna, partCiudad, offset,
				limit);

		final OutputDireccionConsultar outputDTO = new OutputDireccionConsultar();

		DireccionType direccionType;
		for (Direccion direccion : direcciones) {
			direccionType = new DireccionType();
			direccionType.setID(direccion.getId());
			direccionType.setDireccion(direccion.getDireccion());
			direccionType.setComuna(direccion.getComuna());
			direccionType.setCiudad(direccion.getCiudad());

			if (direccion.getUbigeoLat() != null && direccion.getUbigeoLong() != null) {
				final UbigeoType ubigeoType = new UbigeoType();
				ubigeoType.setLatitud(direccion.getUbigeoLat());
				ubigeoType.setLongitud(direccion.getUbigeoLong());
				direccionType.setUbigeo(ubigeoType);
			}

			outputDTO.getRegistro().add(direccionType);
		}

		return ResponseEntity.ok(outputDTO);
	}

	@GetMapping("/{id}")
	ResponseEntity<OutputDireccionObtener> obtener(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@PathVariable(name = "id") final Long id) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Direccion direccion = direccionService.obtener(usuario, id);

		final OutputDireccionObtener outputDTO = new OutputDireccionObtener();
		outputDTO.setID(direccion.getId());
		outputDTO.setDireccion(direccion.getDireccion());
		outputDTO.setComuna(direccion.getComuna());
		outputDTO.setCiudad(direccion.getCiudad());

		if (direccion.getUbigeoLat() != null && direccion.getUbigeoLong() != null) {
			final UbigeoType ubigeoType = new UbigeoType();
			ubigeoType.setLatitud(direccion.getUbigeoLat());
			ubigeoType.setLongitud(direccion.getUbigeoLong());
			outputDTO.setUbigeo(ubigeoType);
		}

		return ResponseEntity.ok(outputDTO);
	}

	@DeleteMapping("/{id}")
	ResponseEntity<Boolean> eliminar(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@PathVariable(name = "id") final Long id) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Direccion direccion = direccionService.obtener(usuario, id);
		final Boolean eliminar = direccionService.eliminar(usuario, direccion);
		
		return ResponseEntity.ok(eliminar);
	}
}
