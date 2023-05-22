package cl.duoc.portafolio.feriavirtual.service;

import cl.duoc.portafolio.feriavirtual.domain.Archivo;

public interface ArchivoService {

	Archivo crear(final String nombreArchivo, final byte[] bytes);

	Boolean eliminar(final Archivo archivoImagen);

}
