package cl.duoc.portafolio.feriavirtual.service;

import java.util.List;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoUsuario;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputAuthCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputUsuarioActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoIdentificacion;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.UsuarioBitacora;

public interface UsuarioService {

	Usuario crear(final InputAuthCrear inputDTO);

	Usuario obtener(final Long id);

	Usuario obtener(final String identificacion);

	String authenticate(final String username, final String password);

	List<Usuario> consultar(final String nombre, final TipoIdentificacion tipoIdentificacion,
			final String identificacion, final EstadoUsuario estado, final String telefono, final Integer offset,
			final Integer limit);

	Boolean actualizar(final Usuario usuario, final InputUsuarioActualizar inputDTO);

	List<UsuarioBitacora> consultarBitacora(final Usuario usuario);

}
