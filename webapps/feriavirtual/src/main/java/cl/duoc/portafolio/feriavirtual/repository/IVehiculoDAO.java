package cl.duoc.portafolio.feriavirtual.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import cl.duoc.portafolio.feriavirtual.domain.Vehiculo;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;

public interface IVehiculoDAO {

	List<Vehiculo> search(List<SearchCriteria> params, Pageable pageable);
}
