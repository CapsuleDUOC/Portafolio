package cl.duoc.portafolio.feriavirtual.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoCosecha;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputCosechaCrear;
import cl.duoc.portafolio.feriavirtual.domain.Cosecha;
import cl.duoc.portafolio.feriavirtual.domain.Producto;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.repository.CosechaRepository;
import cl.duoc.portafolio.feriavirtual.repository.ICosechaDAO;
import cl.duoc.portafolio.feriavirtual.service.CosechaService;
import cl.duoc.portafolio.feriavirtual.service.ProductoService;
import cl.duoc.portafolio.feriavirtual.util.SearchCriteria;

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
		
		Cosecha cosecha = new Cosecha();
		cosecha.setAgricultor(usuario);
		cosecha.setCantidad(inputDTO.getCantidad().doubleValue());
		cosecha.setUnidadMedida(inputDTO.getUnidadMedida());
		cosecha.setEstado(EstadoCosecha.GENERADA);
		cosecha.setCosto(inputDTO.getCosto());
		cosecha.setProducto(productoService.obtener(usuario, inputDTO.getCodigoProducto()));
		
		return cosechaRepository.save(cosecha);
	}

	@Override
	public List<Cosecha> consultar(Usuario usuario, String codProducto, EstadoCosecha estado, Integer offset,
			Integer limit) {
		
		List<SearchCriteria> params = new ArrayList<>();
		params.add(new SearchCriteria("usuario", null, SearchCriteria.OPERATION.equal, usuario, null));
		if (codProducto != null) {
			Producto producto = productoService.obtener(usuario, codProducto);
			params.add(new SearchCriteria("producto", null, SearchCriteria.OPERATION.equal, producto, null));
		}
		if (estado != null)
			params.add(new SearchCriteria("estado", null, SearchCriteria.OPERATION.equal, estado, null));
		
		return cosechaDAO.search(params, PageRequest.of(offset, limit));
	}

	@Override
	public Cosecha obtener(Usuario usuario, Long id) {
		
		Optional<Cosecha> _cosecha = cosechaRepository.findByAgricultorAndId(usuario, id);
		Assert.isTrue(_cosecha.isPresent(),  "No existe cosecha ID [" + id + "]");
		
		return _cosecha.get();
	}
}
