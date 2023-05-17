package cl.duoc.portafolio.feriavirtual.service;

import cl.duoc.portafolio.dto.v10.feriavirtual.DireccionType;
import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface DireccionService {

	Direccion crear(final Usuario usuario, final DireccionType direccionType);

	Boolean eliminar(final Usuario usuario, final Direccion direccion);

}
