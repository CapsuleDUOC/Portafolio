package cl.duoc.portafolio.feriavirtual.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoUsuario;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoIdentificacion;
import lombok.Data;

@Entity
@Data
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private TipoIdentificacion tipoIdentificacion;

	private String identificacion;

	private EstadoUsuario estado;

	private String nombre;

	private String telefono;

	private LocalDateTime registroInstante;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "usuario_rol", joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id"))
	@Column(name = "rol", columnDefinition = "VARCHAR(25)")
	private List<String> roles = new ArrayList<>();

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "usuario_propiedad", joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id"))
	@MapKeyColumn(name = "llave", columnDefinition = "VARCHAR(25)")
	@Column(name = "valor", columnDefinition = "TEXT")
	private Map<String, String> propiedades = new HashMap<String, String>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinTable(name = "usuario_archivo", joinColumns = {
			@JoinColumn(name = "usuario_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "archivo_id", referencedColumnName = "id") })
	private List<Archivo> archivos = new ArrayList<>();
}
