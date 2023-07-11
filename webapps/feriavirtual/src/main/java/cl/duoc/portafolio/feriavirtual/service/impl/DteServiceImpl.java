package cl.duoc.portafolio.feriavirtual.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.portafolio.feriavirtual.domain.Dte;
import cl.duoc.portafolio.feriavirtual.domain.Producto;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.repository.DteRepository;
import cl.duoc.portafolio.feriavirtual.service.DteService;

@Service
public class DteServiceImpl implements DteService{

	private DteRepository dteRepository;
	
	@Autowired
	public DteServiceImpl(final DteRepository dteRepository) {
		this.dteRepository = dteRepository;
	}

	@Override
	public Dte crear(Usuario locatario, Usuario cliente, List<Producto> productos) {
		// TODO Auto-generated method stub
		return null;
	}
}
