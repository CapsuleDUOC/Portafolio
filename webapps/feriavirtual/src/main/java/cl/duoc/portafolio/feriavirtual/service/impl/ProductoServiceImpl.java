package cl.duoc.portafolio.feriavirtual.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoProducto;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputProductoActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputProductoCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.TipoProducto;
import cl.duoc.portafolio.dto.v10.feriavirtual.UnidadMedida;
import cl.duoc.portafolio.feriavirtual.domain.Producto;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.domain.UsuarioBitacora;
import cl.duoc.portafolio.feriavirtual.repository.IProductoDAO;
import cl.duoc.portafolio.feriavirtual.repository.ProductoRepository;
import cl.duoc.portafolio.feriavirtual.repository.UsuarioBitacoraRepository;
import cl.duoc.portafolio.feriavirtual.service.ArchivoService;
import cl.duoc.portafolio.feriavirtual.service.ProductoService;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;

@Service
public class ProductoServiceImpl implements ProductoService {

	private ProductoRepository productoRepository;
	private IProductoDAO productoDAO;
	private ArchivoService archivoService;
	private UsuarioBitacoraRepository bitacoraRepository;

	@Autowired
	public ProductoServiceImpl(final ProductoRepository productoRepository, final IProductoDAO productoDAO,
			final ArchivoService archivoService, final UsuarioBitacoraRepository bitacoraRepository) {
		this.productoRepository = productoRepository;
		this.productoDAO = productoDAO;
		this.archivoService = archivoService;
		this.bitacoraRepository = bitacoraRepository;
	}

	@Override
	public Producto crear(Usuario usuario, InputProductoCrear inputDTO) {

		Optional<Producto> _producto = productoRepository.findByUsuarioAndCodigo(usuario, inputDTO.getCodigo());
		if (_producto.isPresent())
			return _producto.get();

		Producto producto = new Producto();
		producto.setId(null);
		producto.setUsuario(usuario);
		producto.setCodigo(inputDTO.getCodigo());
		producto.setNombre(inputDTO.getNombre());
		producto.setTipo(inputDTO.getTipo());
		producto.setUnidadMedida(inputDTO.getUnidadMedida());
		producto.setPrecio(inputDTO.getPrecio());
		producto.setEstado(inputDTO.getEstado());
		producto.setRegistroInstante(LocalDateTime.now());

		if (inputDTO.getBytesImagen() != null) {
			producto.setArchivoImagen(archivoService.crear(usuario.getIdentificacion() + "_" + inputDTO.getCodigo(),
					inputDTO.getBytesImagen()));
		}

		UsuarioBitacora bitacora = new UsuarioBitacora();
		bitacora.setUsuario(usuario);
		bitacora.setRegistroInstante(LocalDateTime.now());
		bitacora.setRegistro("Se registra producto  [" + producto.getCodigo() + "]");
		bitacoraRepository.save(bitacora);

		return productoRepository.save(producto);
	}

	@Override
	public Producto obtener(Usuario usuario, Long id) {

		Optional<Producto> _producto = productoRepository.findByUsuarioAndId(usuario, id);
		Assert.isTrue(_producto.isPresent(), "No existe producto");

		return _producto.get();
	}

	@Override
	public Boolean actualizar(Producto producto, InputProductoActualizar inputDTO) {

		producto.setNombre(inputDTO.getNombre());
		producto.setTipo(inputDTO.getTipo());
		producto.setPrecio(inputDTO.getPrecio());
		producto.setUnidadMedida(inputDTO.getUnidadMedida());
		producto.setEstado(inputDTO.getEstado());

		if (inputDTO.getBytesImagen() != null) {

			if (producto.getArchivoImagen() != null)
				archivoService.eliminar(producto.getArchivoImagen());

			producto.setArchivoImagen(archivoService.crear(
					producto.getUsuario().getIdentificacion() + "_" + inputDTO.getCodigo(), inputDTO.getBytesImagen()));
		}

		UsuarioBitacora bitacora = new UsuarioBitacora();
		bitacora.setUsuario(producto.getUsuario());
		bitacora.setRegistroInstante(LocalDateTime.now());
		bitacora.setRegistro("Se actualiza producto  [" + producto.getCodigo() + "]");
		bitacoraRepository.save(bitacora);

		productoRepository.save(producto);
		return true;
	}

	@Override
	public List<Producto> consultar(Usuario usuario, TipoProducto tipoProducto, String partCodigo, String partNombre,
			UnidadMedida unidadMedida, EstadoProducto estado, Integer offset, Integer limit) {

		List<SearchCriteria> params = new ArrayList<>();
		params.add(new SearchCriteria("usuario", null, SearchCriteria.OPERATION.equal, usuario, null));
		if (tipoProducto != null)
			params.add(new SearchCriteria("tipo", null, SearchCriteria.OPERATION.equal, tipoProducto, null));
		if (partCodigo != null)
			params.add(new SearchCriteria("codigo", null, SearchCriteria.OPERATION.like, partCodigo, null));
		if (partNombre != null)
			params.add(new SearchCriteria("nombre", null, SearchCriteria.OPERATION.like, partNombre, null));
		if (unidadMedida != null)
			params.add(new SearchCriteria("unidadMedida", null, SearchCriteria.OPERATION.equal, unidadMedida, null));
		if (estado != null)
			params.add(new SearchCriteria("estado", null, SearchCriteria.OPERATION.equal, estado, null));

		return productoDAO.search(params, PageRequest.of(offset, limit));
	}

	@Override
	public List<Producto> consultarDistintosNombres(TipoProducto tipoProducto, String partNombre, Integer offset,
			Integer limit) {

		List<String> nombres;
		if (partNombre != null)
			nombres = Collections.singletonList(partNombre);
		else
			nombres = productoRepository.findDistinctNombre();

		List<Producto> productos = new ArrayList<>();
		for (String nombre : nombres) {
			if (tipoProducto == null) {

				Optional<Producto> _producto = productoRepository.findFirstByNombreContaining(nombre);

				if (_producto.isPresent())
					productos.add(_producto.get());

			} else {

				Optional<Producto> _producto = productoRepository.findFirstByNombreContainingAndTipo(nombre,
						tipoProducto);
				if (_producto.isPresent())
					productos.add(_producto.get());
			}
		}

		return productos;
	}

	@Override
	public List<Producto> consultarPorNombre(String nombre) {

		List<SearchCriteria> params = new ArrayList<>();
		params.add(new SearchCriteria("nombre", null, SearchCriteria.OPERATION.like, nombre, null));

		return productoDAO.search(params, PageRequest.of(0, 500));
	}

	@Override
	public Producto obtener(Usuario usuario, String codigo) {

		Optional<Producto> _producto = productoRepository.findByUsuarioAndCodigo(usuario, codigo);
		Assert.isTrue(_producto.isPresent(), "No existe producto con codigo [" + codigo + "]");

		return _producto.get();
	}

	@Override
	public Producto obtener(Long productoID) {

		Optional<Producto> _producto = productoRepository.findById(productoID);
		Assert.isTrue(_producto.isPresent(), "No existe producto ID [" + productoID + "]");

		return _producto.get();
	}
}
