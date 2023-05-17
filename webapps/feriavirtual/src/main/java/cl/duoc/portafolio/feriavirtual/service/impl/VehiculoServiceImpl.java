package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cl.duoc.portafolio.dto.v10.feriavirtual.VehiculoType;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.Vehiculo;
import cl.duoc.portafolio.feriavirtual.repository.VehiculoRepository;
import cl.duoc.portafolio.feriavirtual.service.VehiculoService;

@Service
public class VehiculoServiceImpl implements VehiculoService{

	private VehiculoRepository vehiculoRepository;
	
	@Autowired
	public VehiculoServiceImpl (final VehiculoRepository vehiculoRepository) {
		this.vehiculoRepository = vehiculoRepository;
	}

	@Override
	public Vehiculo crear(Usuario usuario, VehiculoType vehiculoType) {
		
		//TODO REVISAR SI PATENTE ES UNIQUE EN BD PARA INVLUIR VALIDACION
		
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
}
