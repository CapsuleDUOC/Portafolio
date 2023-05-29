package cl.duoc.portafolio.feriavirtual.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

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
	
	@Lob
	private byte[] bytes;
	
	@Enumerated(EnumType.STRING)
	private EstadoArchivo estado;
	
	private LocalDateTime registroInstante;
}
