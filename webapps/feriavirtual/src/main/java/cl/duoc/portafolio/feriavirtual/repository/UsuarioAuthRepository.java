package cl.duoc.portafolio.feriavirtual.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import cl.duoc.portafolio.feriavirtual.domain.UsuarioAuth;

public interface UsuarioAuthRepository extends CrudRepository<UsuarioAuth, Long> {

	Optional<UsuarioAuth> findByEmail(final String email);

}
