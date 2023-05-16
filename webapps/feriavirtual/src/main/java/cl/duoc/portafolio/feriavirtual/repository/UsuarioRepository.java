package cl.duoc.portafolio.feriavirtual.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

	Optional<Usuario> findByIdentificacion(final String identificacion);

}
