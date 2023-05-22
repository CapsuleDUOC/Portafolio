package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cl.duoc.portafolio.dto.v10.feriavirtual.InputVehiculoActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoVehiculo;
import cl.duoc.portafolio.dto.v10.feriavirtual.VehiculoType;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.Vehiculo;
import cl.duoc.portafolio.feriavirtual.repository.IVehiculoDAO;
import cl.duoc.portafolio.feriavirtual.repository.VehiculoRepository;
import cl.duoc.portafolio.feriavirtual.service.VehiculoService;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;

@Service
public class VehiculoServiceImpl implements VehiculoService {

	private VehiculoRepository vehiculoRepository;
	private IVehiculoDAO vehiculoDAO;

	@Autowired
	public VehiculoServiceImpl(final VehiculoRepository vehiculoRepository, final IVehiculoDAO vehiculoDAO) {
		this.vehiculoRepository = vehiculoRepository;
		this.vehiculoDAO = vehiculoDAO;
	}

	@Override
	public Vehiculo crear(Usuario usuario, VehiculoType vehiculoType) {

		Optional<Vehiculo> _vehiculo = vehiculoRepository.findByUsuarioAndPatente(usuario, vehiculoType.getPatente());
		if (_vehiculo.isPresent())
			return _vehiculo.get();

		Vehiculo vehiculo = new Vehiculo();
		vehiculo.setUsuario(usuario);
		vehiculo.setTipo(vehiculoType.getTipo());
		vehiculo.setPatente(vehiculoType.getPatente());
		vehiculo.setMarca(vehiculoType.getMarca());
		vehiculo.setModelo(vehiculoType.getModelo());
		vehiculo.setAgno(vehiculoType.getAgno());
		vehiculo.setRegistroInstante(LocalDateTime.now());

		return vehiculoRepository.save(vehiculo);
	}

	@Override
	public Boolean eliminar(Usuario usuario, Vehiculo vehiculo) {

		Assert.isTrue(vehiculo.getUsuario().equals(usuario), "El vehiculo a eliminar no corresponde al usuario");
		vehiculoRepository.delete(vehiculo);
		return true;

	}

	@Override
	public List<Vehiculo> consultar(Usuario usuario, TipoVehiculo tipoVehiculo, String marca, String modelo,
			String agno, String patente, Integer offset, Integer limit) {

		List<SearchCriteria> params = new ArrayList<>();

		params.add(new SearchCriteria("usuario", null, SearchCriteria.OPERATION.equal, usuario, null));
		if (tipoVehiculo != null)
			params.add(new SearchCriteria("tipoVehiculo", null, SearchCriteria.OPERATION.equal, tipoVehiculo, null));
		if (marca != null)
			params.add(new SearchCriteria("marca", null, SearchCriteria.OPERATION.like, marca, null));
		if (modelo != null)
			params.add(new SearchCriteria("modelo", null, SearchCriteria.OPERATION.like, modelo, null));
		if (agno != null)
			params.add(new SearchCriteria("agno", null, SearchCriteria.OPERATION.equal, agno, null));
		if (patente != null)
			params.add(new SearchCriteria("patente", null, SearchCriteria.OPERATION.like, patente, null));

		return vehiculoDAO.search(params, PageRequest.of(offset, limit));
	}

	@Override
	public Vehiculo obtener(Usuario usuario, Long id) {
		Optional<Vehiculo> _vehiculo = vehiculoRepository.findByUsuarioAndId(usuario, id);
		Assert.isTrue(_vehiculo.isPresent(),
				"No existe el vehiculo para el Usuario [" + usuario.getIdentificacion() + "]");
		return _vehiculo.get();
	}

	@Override
	public Vehiculo obtener(Usuario usuario, String patente) {
		Optional<Vehiculo> _vehiculo = vehiculoRepository.findByUsuarioAndPatente(usuario, patente);
		Assert.isTrue(_vehiculo.isPresent(),
				"No existe el vehiculo para el Usuario [" + usuario.getIdentificacion() + "]");
		return _vehiculo.get();
	}

	@Override
	public Boolean actualizar(Vehiculo vehiculo, InputVehiculoActualizar inputDTO) {

		vehiculo.setMarca(inputDTO.getMarca());
		vehiculo.setModelo(inputDTO.getModelo());
		vehiculo.setAgno(inputDTO.getAgno());
		vehiculo.setPatente(inputDTO.getPatente());
		vehiculo.setTipo(inputDTO.getTipo());

		vehiculoRepository.save(vehiculo);
		return true;
	}
}
