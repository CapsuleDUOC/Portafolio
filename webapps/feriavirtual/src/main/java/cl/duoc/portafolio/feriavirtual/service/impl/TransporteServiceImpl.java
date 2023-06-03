package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoTransporte;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputTransporteActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputTransporteCrear;
import cl.duoc.portafolio.feriavirtual.domain.Transporte;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.repository.ITransporteDAO;
import cl.duoc.portafolio.feriavirtual.repository.TransporteRepository;
import cl.duoc.portafolio.feriavirtual.service.CosechaService;
import cl.duoc.portafolio.feriavirtual.service.TransporteService;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;

@Service
public class TransporteServiceImpl implements TransporteService {

	private TransporteRepository transporteRepository;
	private ITransporteDAO transporteDAO;
	private UsuarioService usuarioService;
	private CosechaService cosechaService;

	@Autowired
	public TransporteServiceImpl(final TransporteRepository transporteRepository, final ITransporteDAO transporteDAO,
			final UsuarioService usuarioService, final CosechaService cosechaService) {
		this.transporteRepository = transporteRepository;
		this.transporteDAO = transporteDAO;
		this.usuarioService = usuarioService;
		this.cosechaService = cosechaService;
	}

	@Override
	public Transporte crear(Usuario usuario, InputTransporteCrear inputDTO) {

		Transporte transporte = new Transporte();
		transporte.setAgricultor(usuarioService.obtener(inputDTO.getAgricultor()));
		transporte.setTransportista(usuario);
		transporte.setLocatario(usuarioService.obtener(inputDTO.getLocatario()));
		transporte.setEstado(EstadoTransporte.GENERADO);
		transporte.setDireccionOrigen(inputDTO.getDireccionOrigen());
		transporte.setDireccionDestino(inputDTO.getDireccionDestino());
		transporte.setFechaSalida(inputDTO.getFechaSalida());
		transporte.setFechaLlegada(inputDTO.getFechaLlegada());
		transporte.setCosto(inputDTO.getCosto());
		transporte.getCosechas()
				.add(cosechaService.obtener(usuarioService.obtener(inputDTO.getAgricultor()), inputDTO.getCosechaID()));

		return transporteRepository.save(transporte);
	}

	@Override
	public List<Transporte> consultar(Usuario usuario, String agricultorIdentificacion, String locatarioIdentificacion, EstadoTransporte estado,
			LocalDate fechaSalida, LocalDate fechaLlegada, Integer offset, Integer limit) {

		List<SearchCriteria> params = new ArrayList<>();
		params.add(new SearchCriteria("transportista", null, SearchCriteria.OPERATION.equal, usuario, null));
		if (agricultorIdentificacion != null) {
			Usuario agricultor = usuarioService.obtener(agricultorIdentificacion);
			params.add(new SearchCriteria("agricultor", null, SearchCriteria.OPERATION.equal, agricultor, null));
		}
		if (locatarioIdentificacion != null) {
			Usuario locatario = usuarioService.obtener(locatarioIdentificacion);
			params.add(new SearchCriteria("locatario", null, SearchCriteria.OPERATION.equal, locatario, null));
		}
		if (estado != null)
			params.add(new SearchCriteria("estado", null, SearchCriteria.OPERATION.equal, estado, null));
		if (fechaSalida != null)
			params.add(new SearchCriteria("fechaSalida", null, SearchCriteria.OPERATION.equal, fechaSalida, null));
		if (fechaLlegada != null)
			params.add(new SearchCriteria("fechaLlegada", null, SearchCriteria.OPERATION.equal, fechaLlegada, null));
		
		return transporteDAO.search(params, PageRequest.of(offset, limit));
	}

	@Override
	public Transporte obtener(Usuario usuario, Long id) {
		Optional<Transporte> _transporte = transporteRepository.findByTransportistaAndId(usuario, id);
		Assert.isTrue(_transporte.isPresent(), "No existe Transporte ID [" + id + "]");
		return _transporte.get();
	}

	@Override
	public Boolean actualizar(Transporte transporte, InputTransporteActualizar inputDTO) {
		
		transporte.setEstado(inputDTO.getEstado());
		transporte.setDireccionOrigen(inputDTO.getDireccionOrigen());
		transporte.setDireccionDestino(inputDTO.getDireccionDestino());
		transporte.setFechaSalida(inputDTO.getFechaSalida());
		transporte.setFechaLlegada(inputDTO.getFechaLlegada());
		transporte.setCosto(inputDTO.getCosto());
		
		transporteRepository.save(transporte);
		
		return true;
	}
}
