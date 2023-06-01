package cl.duoc.portafolio.feriavirtual.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import cl.duoc.portafolio.feriavirtual.domain.Cosecha;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;

public interface ICosechaDAO {
	List<Cosecha> search(List<SearchCriteria> params, Pageable pageable);
}
