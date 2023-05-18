package cl.duoc.portafolio.feriavirtual.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoComision;
import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoUsuario;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoIdentificacion;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoOperacion;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoProducto;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoUsuario;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoVehiculo;
import cl.duoc.portafolio.dto.v10.feriavirtual.UnidadMedida;

@RestController
@RequestMapping("/keys/v10")
public class KeysController {

	@Autowired
	public KeysController() {

	}
	
	@GetMapping("/vehiculo/tipo")
	ResponseEntity<List<String>> getTipoVehiculo() {
		List<String> response = new ArrayList<>();

		for (TipoVehiculo e : TipoVehiculo.values())
			response.add(e.name());

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/comision/estado")
	ResponseEntity<List<String>> getEstadoComision() {
		List<String> response = new ArrayList<>();

		for (EstadoComision e : EstadoComision.values())
			response.add(e.name());

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/usuario/estado")
	ResponseEntity<List<String>> getEstadoUsuario() {
		List<String> response = new ArrayList<>();

		for (EstadoUsuario e : EstadoUsuario.values())
			response.add(e.name());

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/identificacion/tipo")
	ResponseEntity<List<String>> getTipoIdentificacion() {
		List<String> response = new ArrayList<>();

		for (TipoIdentificacion e : TipoIdentificacion.values())
			response.add(e.name());

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/operacion/tipo")
	ResponseEntity<List<String>> getTipoOperacion() {
		List<String> response = new ArrayList<>();

		for (TipoOperacion e : TipoOperacion.values())
			response.add(e.name());

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/producto/tipo")
	ResponseEntity<List<String>> getTipoProducto() {
		List<String> response = new ArrayList<>();

		for (TipoProducto e : TipoProducto.values())
			response.add(e.name());

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/usuario/tipo")
	ResponseEntity<List<String>> getTipoUsuario() {
		List<String> response = new ArrayList<>();

		for (TipoUsuario e : TipoUsuario.values())
			response.add(e.name());

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/producto/medida")
	ResponseEntity<List<String>> getUnidadMedida() {
		List<String> response = new ArrayList<>();

		for (UnidadMedida e : UnidadMedida.values())
			response.add(e.name());

		return ResponseEntity.ok(response);
	}
	
}
