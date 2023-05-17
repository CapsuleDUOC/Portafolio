package cl.duoc.portafolio.feriavirtual.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoIdentificacion;
import lombok.Data;

@Entity
@Data
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	private String apellido;

	private LocalDateTime registroInstante;

	private TipoIdentificacion tipoIdentificacion;

	private String identificacion;

	private String telefono;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "usuario_rol", joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id"))
	@Column(name = "rol", columnDefinition = "VARCHAR(25)")
	private List<String> roles = new ArrayList<>();
}
