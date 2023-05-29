package cl.duoc.portafolio.feriavirtual.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import cl.duoc.portafolio.dto.v10.feriavirtual.TipoVehiculo;
import lombok.Data;

@Entity
@Data
public class Vehiculo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Usuario usuario;
	
	@Enumerated(EnumType.STRING)
	private TipoVehiculo tipo;
	
	private String patente;
	
	private String marca;
	
	private String modelo;
	
	private String agno;
	
	private LocalDateTime registroInstante;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(name = "vehiculo_archivo", joinColumns = {
			@JoinColumn(name = "vehiculo_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "archivo_id", referencedColumnName = "id") })
	private List<Archivo> archivos = new ArrayList<>();
}
