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
import javax.persistence.OneToOne;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoVenta;
import lombok.Data;

@Entity
@Data
public class Venta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Usuario locatario;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Usuario cliente;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Pedido pedido;
	
	@OneToOne
	private Dte dte;
	
	@Enumerated(EnumType.STRING)
	private EstadoVenta estado;
	
	private Long montoVenta;
	
	private LocalDate fecha;
	
	private LocalTime hora;
}
