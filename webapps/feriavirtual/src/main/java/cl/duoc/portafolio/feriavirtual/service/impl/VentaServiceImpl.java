package cl.duoc.portafolio.feriavirtual.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.portafolio.feriavirtual.domain.Carrito;
import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.Venta;
import cl.duoc.portafolio.feriavirtual.repository.VentaRepository;
import cl.duoc.portafolio.feriavirtual.service.VentaService;

@Service
public class VentaServiceImpl implements VentaService{

	private VentaRepository ventaRepository;
	
	@Autowired
	public VentaServiceImpl(final VentaRepository ventaRepository) {
		this.ventaRepository = ventaRepository;
	}

	@Override
	public Venta crear(Usuario usuario, Carrito carrito, Direccion direccion) {
		// TODO Auto-generated method stub
		return null;
	}
}
