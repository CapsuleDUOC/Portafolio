package cl.duoc.portafolio.feriavirtual.service;

import java.util.List;

import cl.duoc.portafolio.dto.v10.feriavirtual.InputAuthCrear;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;

public interface UsuarioService {

	Usuario crear(final InputAuthCrear inputDTO);
	
	Usuario obtener(final Long id);
	
	Usuario obtener(final String identificacion);
	
	List<Usuario> consultar();

}
