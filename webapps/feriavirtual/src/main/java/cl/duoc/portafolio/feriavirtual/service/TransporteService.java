package cl.duoc.portafolio.feriavirtual.service;

import java.time.LocalDate;
import java.util.List;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoTransporte;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputTransporteActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputTransporteCrear;
import cl.duoc.portafolio.feriavirtual.domain.Transporte;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface TransporteService {

	Transporte crear(final Usuario usuario, final InputTransporteCrear inputDTO);

	List<Transporte> consultar(final Usuario usuario, final String agricultor, final String transportista,
			final String locatario, final EstadoTransporte estado, final LocalDate fechaSalida,
			final LocalDate fechaLlegada, final Integer offset, final Integer limit);

	Transporte obtener(final Usuario usuario, final Long id);

	Boolean actualizar(final Transporte transporte, final InputTransporteActualizar inputDTO);

}
