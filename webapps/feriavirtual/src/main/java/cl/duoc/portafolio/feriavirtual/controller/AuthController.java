package cl.duoc.portafolio.feriavirtual.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.portafolio.dto.JAXBUtil;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputAuthCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputAuthLogin;

@RestController
@RequestMapping("/auth/v10")
public class AuthController {

	@PostMapping("/login")
	ResponseEntity<Boolean> login(@RequestBody InputAuthLogin inputDTO) {

		JAXBUtil.validarSchema(InputAuthLogin.class, inputDTO);

		String token = "";

		return ResponseEntity.ok().header("Authorizarion", "Bearer " + token).build();
	}
	
	@PostMapping
	ResponseEntity<Boolean> crear(@RequestBody InputAuthCrear inputDTO) {
		
		JAXBUtil.validarSchema(InputAuthCrear.class, inputDTO);
		
		

		return ResponseEntity.ok(true);
	}
}
