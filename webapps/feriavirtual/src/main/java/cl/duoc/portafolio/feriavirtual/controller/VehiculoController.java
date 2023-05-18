package cl.duoc.portafolio.feriavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.portafolio.feriavirtual.service.UsuarioService;
import cl.duoc.portafolio.feriavirtual.service.VehiculoService;

@RestController
@RequestMapping("/{clienteIdentificacion}/vehiculo/v10")
public class VehiculoController {

	private UsuarioService usuarioService;
	private VehiculoService vehiculoService;
	
	@Autowired
	public VehiculoController(final UsuarioService usuarioService, final VehiculoService vehiculoService) {
		this.usuarioService = usuarioService;
		this.vehiculoService = vehiculoService;
	}
}
