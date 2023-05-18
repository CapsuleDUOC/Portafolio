package cl.duoc.portafolio.feriavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hlthchck")
public class HealthCheckController {

	@Autowired
	public HealthCheckController() {
	}
	
	
	@GetMapping
	ResponseEntity<Boolean> crear() {
		
		try {
			Thread.sleep(1000);
			return ResponseEntity.ok(Boolean.TRUE);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e.getMessage());
		}
		
	}
}
