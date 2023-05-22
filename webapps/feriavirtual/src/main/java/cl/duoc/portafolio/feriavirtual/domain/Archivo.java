package cl.duoc.portafolio.feriavirtual.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoArchivo;
import lombok.Data;

@Entity
@Data
public class Archivo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nombre;
	
	private String path;
	
	private byte[] bytes;
	
	private EstadoArchivo estado;
	
	private LocalDateTime registroInstante;
}
