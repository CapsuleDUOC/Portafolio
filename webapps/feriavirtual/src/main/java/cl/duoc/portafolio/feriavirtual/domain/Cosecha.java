package cl.duoc.portafolio.feriavirtual.domain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import cl.duoc.portafolio.dto.v10.feriavirtual.UnidadMedida;
import lombok.Data;

@Entity
@Data
public class Cosecha {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Usuario agricutor;

	@OneToMany
	private Producto producto;

	private Double cantidad;

	private UnidadMedida unidadMedida;

	private BigInteger costo;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(name = "transporte_cosecha", joinColumns = {
			@JoinColumn(name = "cosecha_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "transporte_id", referencedColumnName = "id") })
	List<Transporte> transportes = new ArrayList<>();
}
