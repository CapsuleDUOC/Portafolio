package cl.duoc.portafolio.feriavirtual.service;

import java.util.List;

import cl.duoc.portafolio.feriavirtual.domain.Carrito;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface CarritoService {

	List<Carrito> consultar(final Usuario usuario);

	Carrito obtener(final Usuario usuario, final Long id);

}
