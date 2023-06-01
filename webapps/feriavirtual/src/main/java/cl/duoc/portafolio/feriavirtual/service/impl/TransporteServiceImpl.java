package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoTransporte;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputTransporteActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputTransporteCrear;
import cl.duoc.portafolio.feriavirtual.domain.Transporte;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.repository.ITransporteDAO;
import cl.duoc.portafolio.feriavirtual.repository.TransporteRepository;
import cl.duoc.portafolio.feriavirtual.service.TransporteService;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;

@Service
public class TransporteServiceImpl implements TransporteService {

	private TransporteRepository transporteRepository;
	private ITransporteDAO transporteDAO;
	private UsuarioService usuarioService;

	@Autowired
	public TransporteServiceImpl(final TransporteRepository transporteRepository, final ITransporteDAO transporteDAO,
			final UsuarioService usuarioService) {
		this.transporteRepository = transporteRepository;
		this.transporteDAO = transporteDAO;
		this.usuarioService = usuarioService;
	}

	@Override
	public Transporte crear(Usuario usuario, InputTransporteCrear inputDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transporte> consultar(Usuario usuario, String agricultor, String transportista, String locatario,
			EstadoTransporte estado, LocalDate fechaSalida, LocalDate fechaLlegada, Integer offset, Integer limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transporte obtener(Usuario usuario, Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean actualizar(Transporte transporte, InputTransporteActualizar inputDTO) {
		// TODO Auto-generated method stub
		return null;
	}
}
