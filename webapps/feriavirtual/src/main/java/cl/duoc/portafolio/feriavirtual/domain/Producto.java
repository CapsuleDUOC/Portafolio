package cl.duoc.portafolio.feriavirtual.domain;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

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
	
	private TipoProducto tipo;
	
	private UnidadMedida unidadMedida;
	
	private Long precio;
	
	private EstadoProducto estado;
	
	private LocalDateTime registroInstante;
	
	@OneToOne
	private Archivo archivoImagen;
}
