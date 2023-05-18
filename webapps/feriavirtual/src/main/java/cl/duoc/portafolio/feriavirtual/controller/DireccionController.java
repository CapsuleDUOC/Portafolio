package cl.duoc.portafolio.feriavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.portafolio.feriavirtual.service.DireccionService;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;

@RestController
@RequestMapping("/{clienteIdentificacion}/direccion/v10")
public class DireccionController {

	private UsuarioService usuarioService;
	private DireccionService direccionService;
	
	@Autowired
	public DireccionController(final UsuarioService usuarioService, final DireccionService direccionService) {
		this.usuarioService = usuarioService;
		this.direccionService = direccionService;
	}
	
	
}
