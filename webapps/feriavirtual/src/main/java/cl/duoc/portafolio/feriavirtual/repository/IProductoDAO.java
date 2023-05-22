package cl.duoc.portafolio.feriavirtual.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import cl.duoc.portafolio.feriavirtual.domain.Producto;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;

public interface IProductoDAO {

	List<Producto> search(List<SearchCriteria> params, Pageable pageable);
}
