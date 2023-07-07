package cl.duoc.portafolio.feriavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cl.duoc.portafolio.dto.JAXBUtil;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputAuthCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputAuthLogin;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputAuthCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputAuthLogin;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoUsuario;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;

@RestController
@RequestMapping("/auth/v10")
public class AuthController {

	private UsuarioService usuarioService;

	@Autowired
	public AuthController(final UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@PostMapping("/login")
	ResponseEntity<OutputAuthLogin> login(@RequestBody final InputAuthLogin inputDTO) {

		JAXBUtil.validarSchema(InputAuthLogin.class, inputDTO);

		String token = usuarioService.authenticate(inputDTO.getUsername(), inputDTO.getPassword());

		Usuario usuario = usuarioService.obtenerPorEmail(inputDTO.getUsername());

		final OutputAuthLogin outputDTO = new OutputAuthLogin();
		outputDTO.setIdentificacion(usuario.getIdentificacion());
		outputDTO.setTipoUsuario(TipoUsuario.fromValue(usuario.getRoles().get(0)));

		return ResponseEntity.ok().header("Authorizarion", "Bearer " + token).body(outputDTO);
	}

	@PostMapping
	ResponseEntity<OutputAuthCrear> crear(@RequestBody final InputAuthCrear inputDTO) {

		JAXBUtil.validarSchema(InputAuthCrear.class, inputDTO);

		Usuario usuario = usuarioService.crear(inputDTO);

		final OutputAuthCrear outputDTO = new OutputAuthCrear();
		outputDTO.setID(usuario.getId());
		outputDTO.setIdentificacion(usuario.getIdentificacion());
		outputDTO.setTipoIdentificacion(usuario.getTipoIdentificacion());
		outputDTO.setEstado(usuario.getEstado());
		outputDTO.setNombre(usuario.getNombre());
		outputDTO.setTelefono(usuario.getTelefono());
		outputDTO.setRegistroInstante(usuario.getRegistroInstante());

		return ResponseEntity.created(
				ServletUriComponentsBuilder.fromCurrentRequest().path(usuario.getId().toString()).build().toUri())
				.body(outputDTO);
	}
}
