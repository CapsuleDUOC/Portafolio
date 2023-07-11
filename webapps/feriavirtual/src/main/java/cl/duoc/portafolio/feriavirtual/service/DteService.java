package cl.duoc.portafolio.feriavirtual.service;

import java.util.List;

import cl.duoc.portafolio.feriavirtual.domain.CarritoProducto;
import cl.duoc.portafolio.feriavirtual.domain.Dte;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface DteService {

	Dte crear(final Usuario locatario, final Usuario cliente, final List<CarritoProducto> productos);

}
