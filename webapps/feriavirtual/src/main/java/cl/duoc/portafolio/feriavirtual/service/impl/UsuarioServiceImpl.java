package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cl.duoc.portafolio.dto.v10.feriavirtual.DireccionType;
import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoUsuario;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputAuthCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputUsuarioActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.PropiedadType;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoIdentificacion;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoUsuario;
import cl.duoc.portafolio.dto.v10.feriavirtual.UsuarioType;
import cl.duoc.portafolio.dto.v10.feriavirtual.VehiculoType;
import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.UsuarioAuth;
import cl.duoc.portafolio.feriavirtual.domain.UsuarioBitacora;
import cl.duoc.portafolio.feriavirtual.domain.Vehiculo;
import cl.duoc.portafolio.feriavirtual.repository.IUsuarioDAO;
import cl.duoc.portafolio.feriavirtual.repository.UsuarioAuthRepository;
import cl.duoc.portafolio.feriavirtual.repository.UsuarioBitacoraRepository;
import cl.duoc.portafolio.feriavirtual.repository.UsuarioRepository;
import cl.duoc.portafolio.feriavirtual.service.DireccionService;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;
import cl.duoc.portafolio.feriavirtual.service.VehiculoService;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository usuarioRepository;
	private UsuarioAuthRepository usuarioAuthRepository;
	private UsuarioBitacoraRepository bitacoraRepository;
	private IUsuarioDAO usuarioDAO;

	@Value("${jwt.secret}")
	private String jwtSecret;

	public UsuarioServiceImpl(final UsuarioRepository usuarioRepository,
			final UsuarioAuthRepository usuarioAuthRepository, final UsuarioBitacoraRepository bitacoraRepository,
			final IUsuarioDAO usuarioDAO) {
		this.usuarioRepository = usuarioRepository;
		this.usuarioAuthRepository = usuarioAuthRepository;
		this.bitacoraRepository = bitacoraRepository;
		this.usuarioDAO = usuarioDAO;
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

		final String jwtToken = Jwts.builder().setIssuer(_auth.get().getUsuario().getIdentificacion())
				.setSubject(username).addClaims(claims).setIssuedAt(new Date()).setExpiration(cal.getTime())
				.signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes()).compact();

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
		usuario.setEstado(EstadoUsuario.ACTIVO);
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
	public Usuario obtener(final Long id) {
		Optional<Usuario> _usuario = usuarioRepository.findById(id);
		Assert.isTrue(_usuario.isPresent(), "No existe usuario");

		return _usuario.get();
	}

	@Override
	public Usuario obtener(final String identificacion) {
		Optional<Usuario> _usuario = usuarioRepository.findByIdentificacion(identificacion);
		Assert.isTrue(_usuario.isPresent(), "No existe usuario");

		return _usuario.get();
	}
	
	@Override
	public Usuario obtenerPorEmail(String email) {
		Optional<UsuarioAuth> _auth = usuarioAuthRepository.findByEmail(email);
		Assert.isTrue(_auth.isPresent(), "No existe usuario");

		return _auth.get().getUsuario();
	}

	@Override
	public List<Usuario> consultar(final String nombre, final TipoIdentificacion tipoIdentificacion,
			final String identificacion, final EstadoUsuario estado, final String telefono, final Integer offset,
			final Integer limit) {

		List<SearchCriteria> params = new ArrayList<>();

		if (nombre != null)
			params.add(new SearchCriteria("nombre", null, SearchCriteria.OPERATION.like, nombre, null));
		if (tipoIdentificacion != null)
			params.add(new SearchCriteria("tipoIdentificacion", null, SearchCriteria.OPERATION.equal,
					tipoIdentificacion, null));
		if (identificacion != null)
			params.add(new SearchCriteria("identificacion", null, SearchCriteria.OPERATION.like, identificacion, null));
		if (estado != null)
			params.add(new SearchCriteria("estado", null, SearchCriteria.OPERATION.equal, estado, null));
		if (telefono != null)
			params.add(new SearchCriteria("telefono", null, SearchCriteria.OPERATION.like, telefono, null));

		return usuarioDAO.search(params, PageRequest.of(offset, limit));

	}

	@Override
	public Boolean actualizar(Usuario usuario, InputUsuarioActualizar inputDTO) {

		usuario.setNombre(inputDTO.getNombre());
		usuario.setTelefono(inputDTO.getTelefono());

		for (PropiedadType propiedadType : inputDTO.getPropiedades())
			usuario.getPropiedades().put(propiedadType.getLlave(), propiedadType.getValor());

		usuario = usuarioRepository.save(usuario);

		return true;
	}

	@Override
	public List<UsuarioBitacora> consultarBitacora(Usuario usuario) {
		return bitacoraRepository.findByUsuario(usuario);
	}
}
