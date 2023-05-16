package cl.duoc.portafolio.feriavirtual.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import cl.duoc.portafolio.dto.v10.feriavirtual.TipoIdentificacion;
import lombok.Data;

@Entity
@Data
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nombre;
	
	private String apellido;
	
	private LocalDateTime registroInstante;
	
	private TipoIdentificacion tipoIdentificacion;
	
	private String identificacion;
	
	private String telefono;
}
