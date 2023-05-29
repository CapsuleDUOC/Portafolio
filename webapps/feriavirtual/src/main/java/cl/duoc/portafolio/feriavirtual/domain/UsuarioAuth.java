package cl.duoc.portafolio.feriavirtual.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity
@Data
public class UsuarioAuth {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	private Usuario usuario;

	private String email;

	private String password;

	private LocalDateTime ultimoAcceso;
	
	@Column(columnDefinition="TEXT")
	private String token;
}
