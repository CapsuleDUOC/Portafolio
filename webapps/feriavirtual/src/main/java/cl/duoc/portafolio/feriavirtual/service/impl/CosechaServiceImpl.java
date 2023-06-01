package cl.duoc.portafolio.feriavirtual.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoCosecha;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputCosechaCrear;
import cl.duoc.portafolio.feriavirtual.domain.Cosecha;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.repository.CosechaRepository;
import cl.duoc.portafolio.feriavirtual.repository.ICosechaDAO;
import cl.duoc.portafolio.feriavirtual.service.CosechaService;
import cl.duoc.portafolio.feriavirtual.service.ProductoService;

@Service
public class CosechaServiceImpl implements CosechaService {

	private CosechaRepository cosechaRepository;
	private ICosechaDAO cosechaDAO;
	private ProductoService productoService;

	@Autowired
	public CosechaServiceImpl(final CosechaRepository cosechaRepository, final ICosechaDAO cosechaDAO,
			final ProductoService productoService) {
		this.cosechaRepository = cosechaRepository;
		this.cosechaDAO = cosechaDAO;
		this.productoService = productoService;
	}

	@Override
	public Cosecha crear(Usuario usuario, InputCosechaCrear inputDTO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Cosecha> consultar(Usuario usuario, String codProducto, EstadoCosecha estado, Integer offset,
			Integer limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cosecha obtener(Usuario usuario, Long id) {
		// TODO Auto-generated method stub
		return null;
	}
}
