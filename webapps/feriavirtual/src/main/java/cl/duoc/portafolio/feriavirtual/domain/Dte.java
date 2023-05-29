package cl.duoc.portafolio.feriavirtual.domain;

import java.time.LocalDate;

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
import cl.duoc.portafolio.dto.v10.feriavirtual.FormaPago;
import lombok.Data;

@Entity
@Data
public class Dte {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Usuario emisor;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Usuario receptor;
	
	@ManyToOne
	@JoinColumn(name = "xml")
	private Archivo xml;
	
	private Integer tipoDte;
	
	private Long folio;
	
	@Enumerated(EnumType.STRING)
	private FormaPago formaPago;
	
	private LocalDate fechaEmision;
	
	private Long totalNeto;
	
	private Long totalBruto;
	
	
}
