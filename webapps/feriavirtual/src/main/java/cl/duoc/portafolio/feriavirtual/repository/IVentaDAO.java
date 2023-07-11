package cl.duoc.portafolio.feriavirtual.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import cl.duoc.portafolio.feriavirtual.domain.Venta;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;

public interface IVentaDAO {

	List<Venta> search(List<SearchCriteria> params, Pageable pageable);
}
