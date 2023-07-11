package cl.duoc.portafolio.feriavirtual.controller;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.portafolio.dto.JAXBUtil;
import cl.duoc.portafolio.dto.v10.feriavirtual.ArchivoUsuarioType;
import cl.duoc.portafolio.dto.v10.feriavirtual.BitacoraType;
import cl.duoc.portafolio.dto.v10.feriavirtual.DireccionType;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputUsuarioActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputUsuarioCargarArchivo;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputBitacoraConsultar;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputUsuarioObtener;
import cl.duoc.portafolio.dto.v10.feriavirtual.PropiedadType;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoArchivoUsuario;
import cl.duoc.portafolio.dto.v10.feriavirtual.UbigeoType;
import cl.duoc.portafolio.dto.v10.feriavirtual.VehiculoType;
import cl.duoc.portafolio.feriavirtual.domain.Archivo;
import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.UsuarioBitacora;
import cl.duoc.portafolio.feriavirtual.domain.Vehiculo;
import cl.duoc.portafolio.feriavirtual.service.ArchivoService;
import cl.duoc.portafolio.feriavirtual.service.DireccionService;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;
import cl.duoc.portafolio.feriavirtual.service.VehiculoService;

@RestController
@RequestMapping("/{usuarioIdentificacion}/usuario/v10")
public class UsuarioController {

	private UsuarioService usuarioService;
	private ArchivoService archivoService;
	private VehiculoService vehiculoService;
	private DireccionService direccionService;

	@Autowired
	public UsuarioController(final UsuarioService usuarioService, final ArchivoService archivoService,
			final VehiculoService vehiculoService, final DireccionService direccionService) {
		this.usuarioService = usuarioService;
		this.archivoService = archivoService;
		this.vehiculoService = vehiculoService;
		this.direccionService = direccionService;
	}

	@GetMapping
	ResponseEntity<OutputUsuarioObtener> obtener(
			@PathVariable(name = "identificacion") final String usuarioIdentificacion) {
		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);

		final OutputUsuarioObtener outputDTO = new OutputUsuarioObtener();
		outputDTO.setID(usuario.getId());
		outputDTO.setIdentificacion(usuario.getIdentificacion());
		outputDTO.setTipoIdentificacion(usuario.getTipoIdentificacion());
		outputDTO.setEstado(usuario.getEstado());
		outputDTO.setNombre(usuario.getNombre());
		outputDTO.setTelefono(usuario.getTelefono());
		outputDTO.setRegistroInstante(usuario.getRegistroInstante());

		List<Vehiculo> vehiculos = vehiculoService.consultar(usuario, null, null, null, null, null, 0, 100);

		VehiculoType vehiculoType;
		for (Vehiculo vehiculo : vehiculos) {
			vehiculoType = new VehiculoType();
			vehiculoType.setID(vehiculo.getId());
			vehiculoType.setMarca(vehiculo.getMarca());
			vehiculoType.setModelo(vehiculo.getModelo());
			vehiculoType.setAgno(vehiculo.getAgno());
			vehiculoType.setPatente(vehiculo.getPatente());
			vehiculoType.setRegistroInstante(vehiculo.getRegistroInstante());
			vehiculoType.setTipo(vehiculo.getTipo());

			outputDTO.getVehiculos().add(vehiculoType);
		}

		List<Direccion> direcciones = direccionService.consultar(usuario, null, null, null, 0, 100);

		DireccionType direccionType;
		for (Direccion direccion : direcciones) {
			direccionType = new DireccionType();
			direccionType.setID(direccion.getId());
			direccionType.setDireccion(direccion.getDireccion());
			direccionType.setComuna(direccion.getComuna());
			direccionType.setCiudad(direccion.getCiudad());

			if (direccion.getUbigeoLat() != null && direccion.getUbigeoLong() != null) {
				UbigeoType ubigeoType = new UbigeoType();
				ubigeoType.setLatitud(direccion.getUbigeoLat());
				ubigeoType.setLongitud(direccion.getUbigeoLong());

				direccionType.setUbigeo(ubigeoType);
			}

			outputDTO.getDirecciones().add(direccionType);
		}

		PropiedadType propiedadType;
		for (Entry<String, String> propiedad : usuario.getPropiedades().entrySet()) {
			propiedadType = new PropiedadType();
			propiedadType.setLlave(propiedad.getKey());
			propiedadType.setValor(propiedad.getValue());

			outputDTO.getPropiedades().add(propiedadType);
		}

		ArchivoUsuarioType archivoUsuarioType;
		for (Archivo archivo : usuario.getArchivos()) {
			archivoUsuarioType = new ArchivoUsuarioType();
			archivoUsuarioType.setTipo(TipoArchivoUsuario.fromValue(archivo.getNombre().split("_")[0]));
			archivoUsuarioType.setBytes(archivo.getBytes());

			outputDTO.getArchivos().add(archivoUsuarioType);
		}

		return ResponseEntity.ok(outputDTO);
	}

	@PostMapping
	ResponseEntity<Boolean> actualizar(@PathVariable(name = "identificacion") final String usuarioIdentificacion,
			@RequestBody InputUsuarioActualizar inputDTO) {

		JAXBUtil.validarSchema(InputUsuarioActualizar.class, inputDTO);

		Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		Boolean actualizar = usuarioService.actualizar(usuario, inputDTO);

		return ResponseEntity.ok(actualizar);
	}

	@PostMapping("/archivo")
	ResponseEntity<Boolean> cargarArchivo(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestBody InputUsuarioCargarArchivo inputDTO) {

		JAXBUtil.validarSchema(InputUsuarioCargarArchivo.class, inputDTO);

		Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		archivoService.crear(inputDTO.getTipo().name() + "_" + usuario.getIdentificacion(), inputDTO.getBytes());

		return ResponseEntity.ok(true);
	}

	@GetMapping("/bitacora")
	ResponseEntity<OutputBitacoraConsultar> consultarBitacora(
			@PathVariable(name = "identificacion") final String usuarioIdentificacion) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final List<UsuarioBitacora> bitacoras = usuarioService.consultarBitacora(usuario);
		final OutputBitacoraConsultar outputDTO = new OutputBitacoraConsultar();

		BitacoraType bitacoraType;
		for (UsuarioBitacora bitacora : bitacoras) {
			bitacoraType = new BitacoraType();
			bitacoraType.setRegistroInstante(bitacora.getRegistroInstante());
			bitacoraType.setNota(bitacora.getRegistro());

			outputDTO.getRegistro().add(bitacoraType);
		}

		return ResponseEntity.ok(outputDTO);
	}
}
