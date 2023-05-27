package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cl.duoc.portafolio.dto.v10.feriavirtual.DireccionType;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputDireccionActualizar;
import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.UsuarioBitacora;
import cl.duoc.portafolio.feriavirtual.repository.DireccionRepository;
import cl.duoc.portafolio.feriavirtual.repository.IDireccionDAO;
import cl.duoc.portafolio.feriavirtual.repository.UsuarioBitacoraRepository;
import cl.duoc.portafolio.feriavirtual.service.DireccionService;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;

@Service
public class DireccionServiceImpl implements DireccionService {

	private DireccionRepository direccionRepository;
	private IDireccionDAO direccionDAO;
	private UsuarioBitacoraRepository bitacoraRepository;

	@Autowired
	public DireccionServiceImpl(final DireccionRepository direccionRepository, final IDireccionDAO direccionDAO,
			final UsuarioBitacoraRepository bitacoraRepository) {
		this.direccionRepository = direccionRepository;
		this.direccionDAO = direccionDAO;
		this.bitacoraRepository = bitacoraRepository;
	}

	@Override
	public Direccion crear(final Usuario usuario, final DireccionType direccionType) {

		Direccion direccion = new Direccion();
		direccion.setUsuario(usuario);
		direccion.setDireccion(direccionType.getDireccion());
		direccion.setComuna(direccionType.getComuna());
		direccion.setCiudad(direccionType.getCiudad());

		if (direccionType.getUbigeo() != null) {
			direccion.setUbigeoLat(direccionType.getUbigeo().getLatitud());
			direccion.setUbigeoLong(direccionType.getUbigeo().getLongitud());
		}

		UsuarioBitacora bitacora = new UsuarioBitacora();
		bitacora.setUsuario(usuario);
		bitacora.setRegistroInstante(LocalDateTime.now());
		bitacora.setRegistro("Se registra direccion  [" + direccion.getDireccion() + "]");
		bitacoraRepository.save(bitacora);

		return direccionRepository.save(direccion);
	}

	@Override
	public Boolean eliminar(final Usuario usuario, final Direccion direccion) {

		Assert.isTrue(direccion.getUsuario().equals(usuario), "La direccion a eliminar no corresponde al usuario");

		UsuarioBitacora bitacora = new UsuarioBitacora();
		bitacora.setUsuario(usuario);
		bitacora.setRegistroInstante(LocalDateTime.now());
		bitacora.setRegistro("Se elimina direccion  [" + direccion.getDireccion() + "]");
		bitacoraRepository.save(bitacora);

		direccionRepository.delete(direccion);
		return true;
	}

	@Override
	public List<Direccion> consultar(Usuario usuario, String direccion, String comuna, String ciudad, Integer offset,
			Integer limit) {

		List<SearchCriteria> params = new ArrayList<>();

		params.add(new SearchCriteria("usuario", null, SearchCriteria.OPERATION.equal, usuario, null));
		if (direccion != null)
			params.add(new SearchCriteria("direccion", null, SearchCriteria.OPERATION.like, direccion, null));
		if (comuna != null)
			params.add(new SearchCriteria("comuna", null, SearchCriteria.OPERATION.like, comuna, null));
		if (ciudad != null)
			params.add(new SearchCriteria("ciudad", null, SearchCriteria.OPERATION.like, ciudad, null));

		return direccionDAO.search(params, PageRequest.of(offset, limit));
	}

	@Override
	public Direccion obtener(Usuario usuario, Long id) {
		Optional<Direccion> _direccion = direccionRepository.findByUsuarioAndId(usuario, id);
		Assert.isTrue(_direccion.isPresent(),
				"No existe la direccion para el usuario [" + usuario.getIdentificacion() + "]");
		return _direccion.get();
	}

	@Override
	public Boolean actualizar(Direccion direccion, InputDireccionActualizar inputDTO) {

		UsuarioBitacora bitacora = new UsuarioBitacora();
		bitacora.setUsuario(direccion.getUsuario());
		bitacora.setRegistroInstante(LocalDateTime.now());
		bitacora.setRegistro(
				"Se actualiza direccion  [" + direccion.getDireccion() + "] a [" + inputDTO.getDireccion() + "]");
		bitacoraRepository.save(bitacora);

		direccion.setDireccion(inputDTO.getDireccion());
		direccion.setComuna(inputDTO.getComuna());
		direccion.setCiudad(inputDTO.getCiudad());

		if (inputDTO.getUbigeo() != null) {
			direccion.setUbigeoLat(inputDTO.getUbigeo().getLatitud());
			direccion.setUbigeoLong(inputDTO.getUbigeo().getLongitud());
		}

		direccionRepository.save(direccion);
		return true;
	}

}
