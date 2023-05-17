package cl.duoc.portafolio.feriavirtual.service;

import cl.duoc.portafolio.dto.v10.feriavirtual.InputAuthCrear;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface UsuarioService {

	Usuario crear(final InputAuthCrear inputDTO);
	
	Usuario obtener(final Long id);
	
	Usuario obtener(final String identificacion);
	
	String authenticate(final String username, final String password);
	
	Iterable<Usuario> consultar();

}
