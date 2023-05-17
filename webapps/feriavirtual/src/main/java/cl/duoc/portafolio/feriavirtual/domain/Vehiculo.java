package cl.duoc.portafolio.feriavirtual.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import cl.duoc.portafolio.dto.v10.feriavirtual.TipoVehiculo;
import lombok.Data;

@Entity
@Data
public class Vehiculo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private Usuario usuario;
	
	private TipoVehiculo tipo;
	
	private String patente;
	
	private String marca;
	
	private String modelo;
	
	private String agno;
	
	private LocalDateTime registroInstante;
}
