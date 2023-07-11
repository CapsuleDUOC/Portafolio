package cl.duoc.portafolio.feriavirtual.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoPedido;
import lombok.Data;

@Data
@Entity
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Usuario despachador;
	
	@Enumerated(EnumType.STRING)
	private EstadoPedido estado;
	
	private String patenteVehiculo;
	
	private String direccionOrigen;
	
	private String direccionDestino;
	
	private Long montoDespacho;
	
	private LocalDate fecha;
	
	private LocalTime hora;
}
