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
import cl.duoc.portafolio.dto.v10.feriavirtual.ArchivoVehiculoType;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputVehiculoActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputVehiculoCargarArchivo;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputVehiculoCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputVehiculoConsultar;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputVehiculoCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputVehiculoObtener;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoArchivoVehiculo;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoVehiculo;
import cl.duoc.portafolio.dto.v10.feriavirtual.VehiculoType;
import cl.duoc.portafolio.feriavirtual.domain.Archivo;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.Vehiculo;
import cl.duoc.portafolio.feriavirtual.service.ArchivoService;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;
import cl.duoc.portafolio.feriavirtual.service.VehiculoService;

@RestController
@RequestMapping("/{usuarioIdentificacion}/vehiculo/v10")
public class VehiculoController {

	private UsuarioService usuarioService;
	private VehiculoService vehiculoService;
	private ArchivoService archivoService;

	@Autowired
	public VehiculoController(final UsuarioService usuarioService, final VehiculoService vehiculoService,
			final ArchivoService archivoService) {
		this.usuarioService = usuarioService;
		this.vehiculoService = vehiculoService;
		this.archivoService = archivoService;
	}

	@PostMapping
	ResponseEntity<OutputVehiculoCrear> crear(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestBody final InputVehiculoCrear inputDTO) {

		JAXBUtil.validarSchema(InputVehiculoCrear.class, inputDTO);

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Vehiculo vehiculo = vehiculoService.crear(usuario, inputDTO);

		final OutputVehiculoCrear outputDTO = new OutputVehiculoCrear();
		outputDTO.setID(vehiculo.getId());
		outputDTO.setMarca(vehiculo.getMarca());
		outputDTO.setModelo(vehiculo.getModelo());
		outputDTO.setAgno(vehiculo.getAgno());
		outputDTO.setPatente(vehiculo.getPatente());
		outputDTO.setTipo(vehiculo.getTipo());
		outputDTO.setRegistroInstante(vehiculo.getRegistroInstante());

		return ResponseEntity.created(
				ServletUriComponentsBuilder.fromCurrentRequest().path(vehiculo.getId().toString()).build().toUri())
				.body(outputDTO);
	}

	@PostMapping("/{id}")
	ResponseEntity<Boolean> actualizar(@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@PathVariable(name = "id") final Long id, @RequestBody final InputVehiculoActualizar inputDTO) {

		JAXBUtil.validarSchema(InputVehiculoActualizar.class, inputDTO);

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Vehiculo vehiculo = vehiculoService.obtener(usuario, id);
		final Boolean actualizar = vehiculoService.actualizar(vehiculo, inputDTO);

		return ResponseEntity.ok(actualizar);
	}

	@GetMapping
	ResponseEntity<OutputVehiculoConsultar> consultar(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestParam(name = "tipoVehiculo", required = false) final TipoVehiculo tipoVehiculo,
			@RequestParam(name = "partMarca", required = false) final String partMarca,
			@RequestParam(name = "partModelo", required = false) final String partModelo,
			@RequestParam(name = "agno", required = false) final String agno,
			@RequestParam(name = "patente", required = false) final String patente,
			@RequestParam(name = "offset", defaultValue = "0") Integer offset,
			@RequestParam(name = "limit", defaultValue = "100") Integer limit) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		List<Vehiculo> vehiculos = vehiculoService.consultar(usuario, tipoVehiculo, partMarca, partModelo, agno,
				patente, offset, limit);

		final OutputVehiculoConsultar outputDTO = new OutputVehiculoConsultar();

		VehiculoType vehiculoType;
		for (Vehiculo vehiculo : vehiculos) {
			vehiculoType = new VehiculoType();
			vehiculoType.setID(vehiculo.getId());
			vehiculoType.setMarca(vehiculo.getMarca());
			vehiculoType.setModelo(vehiculo.getModelo());
			vehiculoType.setAgno(vehiculo.getAgno());
			vehiculoType.setPatente(vehiculo.getPatente());
			vehiculoType.setTipo(vehiculo.getTipo());
			vehiculoType.setRegistroInstante(vehiculo.getRegistroInstante());

			outputDTO.getRegistro().add(vehiculoType);
		}

		return ResponseEntity.ok(outputDTO);
	}

	@GetMapping("/{id}")
	ResponseEntity<OutputVehiculoObtener> obtener(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@PathVariable(name = "id") final Long id) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Vehiculo vehiculo = vehiculoService.obtener(usuario, id);

		final OutputVehiculoObtener outputDTO = new OutputVehiculoObtener();
		outputDTO.setID(vehiculo.getId());
		outputDTO.setMarca(vehiculo.getMarca());
		outputDTO.setModelo(vehiculo.getModelo());
		outputDTO.setAgno(vehiculo.getAgno());
		outputDTO.setPatente(vehiculo.getPatente());
		outputDTO.setTipo(vehiculo.getTipo());
		outputDTO.setRegistroInstante(vehiculo.getRegistroInstante());

		ArchivoVehiculoType archivoVehiculoType;
		for (Archivo archivo : vehiculo.getArchivos()) {
			archivoVehiculoType = new ArchivoVehiculoType();
			archivoVehiculoType.setTipo(TipoArchivoVehiculo.fromValue(archivo.getNombre().split("_")[0]));
			archivoVehiculoType.setBytes(archivo.getBytes());

			outputDTO.getArchivos().add(archivoVehiculoType);
		}

		return ResponseEntity.ok(outputDTO);
	}

	@DeleteMapping("/{id}")
	ResponseEntity<Boolean> eliminar(@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@PathVariable(name = "id") final Long id) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Vehiculo vehiculo = vehiculoService.obtener(usuario, id);
		final Boolean eliminar = vehiculoService.eliminar(usuario, vehiculo);

		return ResponseEntity.ok(eliminar);
	}

	@PostMapping("/archivo/{id}")
	ResponseEntity<Boolean> cargarArchivo(@PathVariable(name = "id") final Long id,
			@RequestBody InputVehiculoCargarArchivo inputDTO) {

		JAXBUtil.validarSchema(InputVehiculoCargarArchivo.class, inputDTO);

		Usuario usuario = usuarioService.obtener(id);
		archivoService.crear(inputDTO.getTipo().name() + "_" + usuario.getIdentificacion(), inputDTO.getBytes());

		return ResponseEntity.ok(true);
	}
}
