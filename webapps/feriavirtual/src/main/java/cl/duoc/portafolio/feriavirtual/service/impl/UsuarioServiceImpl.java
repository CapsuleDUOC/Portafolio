package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.duoc.portafolio.dto.v10.feriavirtual.InputAuthCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.UsuarioType;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.UsuarioAuth;
import cl.duoc.portafolio.feriavirtual.repository.UsuarioAuthRepository;
import cl.duoc.portafolio.feriavirtual.repository.UsuarioRepository;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository usuarioRepository;
	private UsuarioAuthRepository usuarioAuthRepository;

	public UsuarioServiceImpl(final UsuarioRepository usuarioRepository,
			final UsuarioAuthRepository usuarioAuthRepository) {
		this.usuarioRepository = usuarioRepository;
		this.usuarioAuthRepository = usuarioAuthRepository;
	}

	@Override
	public Usuario crear(final InputAuthCrear inputDTO) {
		
		Usuario usuario = crearUsuario(inputDTO.getUsuario());
		crearAuth(usuario, inputDTO.getEmail(), inputDTO.getPassword());

		return usuario;
	}

	private Usuario crearUsuario(final UsuarioType usuarioType) {
		
		Optional<Usuario> _usuario = usuarioRepository.findByIdentificacion(usuarioType.getIdentificacion());
		if (_usuario.isPresent())
			return _usuario.get();

		Usuario usuario = new Usuario();
		usuario.setId(null);
		usuario.setTipoIdentificacion(usuarioType.getTipoIdentificacion());
		usuario.setIdentificacion(usuarioType.getIdentificacion());
		usuario.setNombre(usuarioType.getNombre());
		usuario.setApellido(usuarioType.getApellido());
		usuario.setTelefono(usuarioType.getTelefono());
		usuario.setRegistroInstante(LocalDateTime.now());

		return usuarioRepository.save(usuario);
	}

	private UsuarioAuth crearAuth(final Usuario usuario, final String email, final String password) {
		
		Optional<UsuarioAuth> _auth = usuarioAuthRepository.findByEmail(email);
		if (_auth.isPresent())
			return _auth.get();

		UsuarioAuth auth = new UsuarioAuth();
		auth.setId(null);
		auth.setUsuario(usuario);
		auth.setEmail(email);
		auth.setPassword(password);

		return usuarioAuthRepository.save(auth);
	}

	@Override
	public Usuario obtener(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Usuario obtener(String identificacion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Usuario> consultar() {
		// TODO Auto-generated method stub
		return null;
	}
}
