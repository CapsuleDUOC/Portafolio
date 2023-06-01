package cl.duoc.portafolio.feriavirtual.service;

import java.util.List;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoCosecha;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputCosechaCrear;
import cl.duoc.portafolio.feriavirtual.domain.Cosecha;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface CosechaService {

	Cosecha crear(final Usuario usuario, final InputCosechaCrear inputDTO);

	List<Cosecha> consultar(final Usuario usuario, final String codProducto, final EstadoCosecha estado,
			final Integer offset, final Integer limit);

	Cosecha obtener(final Usuario usuario, final Long id);

}
