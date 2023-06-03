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

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoCarrito;
import lombok.Data;

@Entity
@Data
public class Carrito {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Usuario cliente;
	
	@Enumerated(EnumType.STRING)
	private EstadoCarrito estado;
	
	private LocalDateTime registroInstante;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(name = "carrito_producto", joinColumns = {
			@JoinColumn(name = "carrito_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "producto_id", referencedColumnName = "id") })
	List<Producto> producto = new ArrayList<>();
}
