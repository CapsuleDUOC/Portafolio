package cl.duoc.portafolio.feriavirtual.service;

import java.util.List;

import cl.duoc.portafolio.dto.v10.feriavirtual.TipoVehiculo;
import cl.duoc.portafolio.dto.v10.feriavirtual.VehiculoType;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.Vehiculo;

public interface VehiculoService {

	Vehiculo crear(final Usuario usuario, final VehiculoType vehiculoType);

	Boolean eliminar(final Usuario usuario, final Vehiculo vehiculo);

	List<Vehiculo> consultar(final Usuario usuario, final TipoVehiculo tipoVehiculo, final String marca, final String modelo, final String agno,
			final String patente, final Integer limit, final Integer offset);
	
	Vehiculo obtener(final Usuario usuario, final Long id);
	
	Vehiculo obtener(final Usuario usuario, final String patente);

}
