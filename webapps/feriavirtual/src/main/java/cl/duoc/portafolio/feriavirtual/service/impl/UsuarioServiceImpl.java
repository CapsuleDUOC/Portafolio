package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cl.duoc.portafolio.dto.v10.feriavirtual.InputAuthCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoUsuario;
import cl.duoc.portafolio.dto.v10.feriavirtual.UsuarioType;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.UsuarioAuth;
import cl.duoc.portafolio.feriavirtual.domain.UsuarioBitacora;
import cl.duoc.portafolio.feriavirtual.repository.UsuarioAuthRepository;
import cl.duoc.portafolio.feriavirtual.repository.UsuarioBitacoraRepository;
import cl.duoc.portafolio.feriavirtual.repository.UsuarioRepository;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository usuarioRepository;
	private UsuarioAuthRepository usuarioAuthRepository;
	private UsuarioBitacoraRepository bitacoraRepository;

	@Value("${jwt.secret}")
	private String jwtSecret;

	public UsuarioServiceImpl(final UsuarioRepository usuarioRepository,
			final UsuarioAuthRepository usuarioAuthRepository, final UsuarioBitacoraRepository bitacoraRepository) {
		this.usuarioRepository = usuarioRepository;
		this.usuarioAuthRepository = usuarioAuthRepository;
		this.bitacoraRepository = bitacoraRepository;
	}

	@Override
	public Usuario crear(final InputAuthCrear inputDTO) {

		Usuario usuario = crearUsuario(inputDTO.getUsuario(), inputDTO.getTipoUsuario());

		UsuarioBitacora bitacora = new UsuarioBitacora();
		bitacora.setUsuario(usuario);
		bitacora.setRegistroInstante(LocalDateTime.now());
		bitacora.setRegistro("Se crea usuario con tipo [" + inputDTO.getTipoUsuario().name() + "]");
		bitacoraRepository.save(bitacora);

		String salt = BCrypt.gensalt();
		crearAuth(usuario, inputDTO.getEmail(), BCrypt.hashpw(inputDTO.getPassword(), salt));

		return usuario;
	}

	@Override
	public String authenticate(String username, String password) {

		Optional<UsuarioAuth> _auth = usuarioAuthRepository.findByEmail(username);
		if (!_auth.isPresent()) {

			// TODO RESPONSE HTTP 401
			throw new IllegalArgumentException("Usuario no existe");
		}

		if (!BCrypt.checkpw(password, _auth.get().getPassword())) {
			// TODO RESPONSE HTTP 401
			throw new IllegalArgumentException("Contraseña inválida");
		}

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, 12);
		
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("ROLES", _auth.get().getUsuario().getRoles());
		
		final String jwtToken = Jwts.builder().setSubject(username).addClaims(claims).setIssuedAt(new Date()).setExpiration(cal.getTime())
				.signWith(SignatureAlgorithm.HS512,
						jwtSecret.getBytes()).compact();

		return jwtToken;

	}

	private Usuario crearUsuario(final UsuarioType usuarioType, final TipoUsuario tipoUsuario) {

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
		usuario.getRoles().add(tipoUsuario.name());

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
		auth.setUltimoAcceso(null);

		return usuarioAuthRepository.save(auth);
	}

	@Override
	public Usuario obtener(Long id) {
		Optional<Usuario> _usuario = usuarioRepository.findById(id);
		Assert.isTrue(_usuario.isPresent(), "No existe usuario");
		
		return _usuario.get();
	}

	@Override
	public Usuario obtener(String identificacion) {
		Optional<Usuario> _usuario = usuarioRepository.findByIdentificacion(identificacion);
		Assert.isTrue(_usuario.isPresent(), "No existe usuario");
		
		return _usuario.get();
	}

	@Override
	public Iterable<Usuario> consultar() {
		
		return usuarioRepository.findAll();
	}
}
