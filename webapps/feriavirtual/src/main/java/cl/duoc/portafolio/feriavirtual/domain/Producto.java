package cl.duoc.portafolio.feriavirtual.domain;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoProducto;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoProducto;
import cl.duoc.portafolio.dto.v10.feriavirtual.UnidadMedida;
import lombok.Data;

@Entity
@Data
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Usuario usuario;
	
	private String codigo;
	
	private String nombre;
	
	@Enumerated(EnumType.STRING)
	private TipoProducto tipo;
	
	@Enumerated(EnumType.STRING)
	private UnidadMedida unidadMedida;
	
	private Long precio;
	
	@Enumerated(EnumType.STRING)
	private EstadoProducto estado;
	
	private LocalDateTime registroInstante;
	
	@ManyToOne
	@JoinColumn(name = "archivo_imagen")
	private Archivo archivoImagen;
}
