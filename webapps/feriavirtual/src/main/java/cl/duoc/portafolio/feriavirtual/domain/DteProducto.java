package cl.duoc.portafolio.feriavirtual.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class DteProducto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private Dte dte;
	
	@ManyToOne
	private Producto producto;
	
	private Double volumen;
	
	private Double pesoKilogramos;
}
