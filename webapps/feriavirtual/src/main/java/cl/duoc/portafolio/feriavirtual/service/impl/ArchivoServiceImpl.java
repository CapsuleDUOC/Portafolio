package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoArchivo;
import cl.duoc.portafolio.feriavirtual.domain.Archivo;
import cl.duoc.portafolio.feriavirtual.repository.ArchivoRepository;
import cl.duoc.portafolio.feriavirtual.service.ArchivoService;

@Service
public class ArchivoServiceImpl implements ArchivoService{

	private ArchivoRepository archivoRepository;
	
	@Autowired
	public ArchivoServiceImpl(final ArchivoRepository archivoRepository) {
		this.archivoRepository = archivoRepository;
	}

	@Override
	public Archivo crear(String nombreArchivo, byte[] bytes) {
		
		Archivo archivo = new Archivo();
		archivo.setId(null);
		archivo.setEstado(EstadoArchivo.BASE_DATOS);
		archivo.setNombre(nombreArchivo + "_" + System.currentTimeMillis());
		archivo.setBytes(bytes);
		archivo.setRegistroInstante(LocalDateTime.now());
		
		return archivoRepository.save(archivo);
	}

	@Override
	public Boolean eliminar(Archivo archivo) {
		
		archivo.setPath(null);
		archivo.setBytes(null);
		archivo.setEstado(EstadoArchivo.ELIMINADO);
		
		archivoRepository.save(archivo);
		return true;
	}
}
