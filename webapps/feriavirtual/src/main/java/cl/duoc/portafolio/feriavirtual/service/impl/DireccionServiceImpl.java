package cl.duoc.portafolio.feriavirtual.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cl.duoc.portafolio.dto.v10.feriavirtual.DireccionType;
import cl.duoc.portafolio.feriavirtual.domain.Direccion;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.repository.DireccionRepository;
import cl.duoc.portafolio.feriavirtual.service.DireccionService;

@Service
public class DireccionServiceImpl implements DireccionService {

	private DireccionRepository direccionRepository;

	@Autowired
	public DireccionServiceImpl(final DireccionRepository direccionRepository) {
		this.direccionRepository = direccionRepository;
	}

	@Override
	public Direccion crear(Usuario usuario, DireccionType direccionType) {

		// TODO REVVISAR SI EXISTE CAMPO UNIQUE EN BD PARA DESARROLLAR VALIDACION
		
		Direccion direccion = new Direccion();
		direccion.setUsuario(usuario);
		direccion.setDireccion(direccionType.getDireccion());
		direccion.setComuna(direccionType.getComuna());
		direccion.setCiudad(direccionType.getCiudad());

		if (direccionType.getUbigeo() != null) {
			direccion.setUbigeoLat(direccionType.getUbigeo().getLatitud());
			direccion.setUbigeoLong(direccionType.getUbigeo().getLongitud());
		}

		return direccionRepository.save(direccion);
	}

	@Override
	public Boolean eliminar(Usuario usuario, Direccion direccion) {
		
		Assert.isTrue(direccion.getUsuario().equals(usuario), "La direccion a eliminar no corresponde al usuario");
		direccionRepository.delete(direccion);
		return true;
	}

}
