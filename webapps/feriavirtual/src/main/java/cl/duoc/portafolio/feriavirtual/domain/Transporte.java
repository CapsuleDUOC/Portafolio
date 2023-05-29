package cl.duoc.portafolio.feriavirtual.domain;

import java.time.LocalDate;
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

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoTransporte;
import lombok.Data;

@Entity
@Data
public class Transporte {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Usuario agricultor;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Usuario transportista;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Usuario locatario;

	@ManyToOne
	private Dte dte;
	
	@Enumerated(EnumType.STRING)
	private EstadoTransporte estado;

	private String direccionOrigen;

	private String direccionDestino;

	private LocalDate fechaSalida;

	private LocalDate fechaLlegada;

	private Long costo;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(name = "transporte_cosecha", joinColumns = {
			@JoinColumn(name = "transporte_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "cosecha_id", referencedColumnName = "id") })
	List<Cosecha> cosechas = new ArrayList<>();
}
