package cl.duoc.portafolio.feriavirtual.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.UsuarioBitacora;

public interface UsuarioBitacoraRepository extends CrudRepository<UsuarioBitacora, Long>{

	List<UsuarioBitacora> findByUsuario(final Usuario usuario);

}
