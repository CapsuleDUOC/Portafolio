package cl.duoc.portafolio.feriavirtual.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import cl.duoc.portafolio.feriavirtual.domain.Transporte;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;

public interface ITransporteDAO {
	List<Transporte> search(List<SearchCriteria> params, Pageable pageable);
}
