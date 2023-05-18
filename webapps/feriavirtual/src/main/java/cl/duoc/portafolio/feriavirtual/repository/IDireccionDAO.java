package cl.duoc.portafolio.feriavirtual.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;

public interface IDireccionDAO {

	List<Direccion> search(List<SearchCriteria> params, Pageable pageable);

}
