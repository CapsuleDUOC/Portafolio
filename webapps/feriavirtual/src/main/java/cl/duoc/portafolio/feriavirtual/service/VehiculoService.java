package cl.duoc.portafolio.feriavirtual.service;

import java.util.List;

import cl.duoc.portafolio.dto.v10.feriavirtual.TipoVehiculo;
import cl.duoc.portafolio.dto.v10.feriavirtual.VehiculoType;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.Vehiculo;

public interface VehiculoService {

	Vehiculo crear(final Usuario usuario, final VehiculoType vehiculoType);

	Boolean eliminar(final Usuario usuario, final Vehiculo vehiculo);

	List<Vehiculo> consultar(Usuario usuario, TipoVehiculo tipoVehiculo, String marca, String modelo, String agno,
			String patente, Integer limit, Integer offset);

}
