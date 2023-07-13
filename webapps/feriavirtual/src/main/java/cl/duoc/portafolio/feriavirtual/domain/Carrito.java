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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
	
	@OneToMany(mappedBy = "carrito", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    private List<CarritoProducto> carritoProducto = new ArrayList<>();
}
