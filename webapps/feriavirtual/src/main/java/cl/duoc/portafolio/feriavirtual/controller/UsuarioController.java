package cl.duoc.portafolio.feriavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.portafolio.dto.v10.feriavirtual.OutputUsuarioConsultar;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputUsuarioObtener;
import cl.duoc.portafolio.dto.v10.feriavirtual.UsuarioType;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;

@RestController
@RequestMapping("/usuario/v10")
public class UsuarioController {

	private UsuarioService usuarioService;

	@Autowired
	public UsuarioController(final UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping("/{id}")
	ResponseEntity<OutputUsuarioObtener> obtener(@PathVariable(name = "id") final Long id) {

		final Usuario usuario = usuarioService.obtener(id);

		final OutputUsuarioObtener outputDTO = new OutputUsuarioObtener();
		outputDTO.setID(usuario.getId());
		outputDTO.setIdentificacion(usuario.getIdentificacion());
		outputDTO.setTipoIdentificacion(usuario.getTipoIdentificacion());
		outputDTO.setNombre(usuario.getNombre());
		outputDTO.setApellido(usuario.getApellido());
		outputDTO.setTelefono(usuario.getTelefono());
		outputDTO.setRegistroInstante(usuario.getRegistroInstante());

		// TODO AGREGAR DIRECCIONES
		// TODO AGREGAR DIRECCIONES

		return ResponseEntity.ok(outputDTO);

	}

	@GetMapping("/identificacion/{identificacion}")
	ResponseEntity<OutputUsuarioObtener> obtenerPorIdentificacion(
			@PathVariable(name = "identificacion") final String identificacion) {

		final Usuario usuario = usuarioService.obtener(identificacion);

		final OutputUsuarioObtener outputDTO = new OutputUsuarioObtener();
		outputDTO.setID(usuario.getId());
		outputDTO.setIdentificacion(usuario.getIdentificacion());
		outputDTO.setTipoIdentificacion(usuario.getTipoIdentificacion());
		outputDTO.setNombre(usuario.getNombre());
		outputDTO.setApellido(usuario.getApellido());
		outputDTO.setTelefono(usuario.getTelefono());
		outputDTO.setRegistroInstante(usuario.getRegistroInstante());

		// TODO AGREGAR DIRECCIONES
		// TODO AGREGAR DIRECCIONES

		return ResponseEntity.ok(outputDTO);

	}
	
	@GetMapping
	ResponseEntity<OutputUsuarioConsultar> consultar() {

		Iterable<Usuario> usuarios = usuarioService.consultar();

		final OutputUsuarioConsultar outputDTO = new OutputUsuarioConsultar();
		
		UsuarioType usuarioType;
		for (Usuario usuario : usuarios) {
			usuarioType = new UsuarioType();
			usuarioType.setID(usuario.getId());
			usuarioType.setIdentificacion(usuario.getIdentificacion());
			usuarioType.setTipoIdentificacion(usuario.getTipoIdentificacion());
			usuarioType.setNombre(usuario.getNombre());
			usuarioType.setApellido(usuario.getApellido());
			usuarioType.setTelefono(usuario.getTelefono());
			usuarioType.setRegistroInstante(usuario.getRegistroInstante());
			
			outputDTO.getRegistro().add(usuarioType);
		}
		
		return ResponseEntity.ok(outputDTO);

	}
}
