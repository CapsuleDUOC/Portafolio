package cl.duoc.portafolio.feriavirtual.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;

public interface IUsuarioDAO {

	List<Usuario> search(List<SearchCriteria> params, Pageable pageable);
}
