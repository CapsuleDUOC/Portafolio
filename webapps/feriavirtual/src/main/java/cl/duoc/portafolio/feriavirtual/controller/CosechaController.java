package cl.duoc.portafolio.feriavirtual.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cl.duoc.portafolio.dto.JAXBUtil;
import cl.duoc.portafolio.dto.v10.feriavirtual.CosechaType;
import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoCosecha;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputCosechaCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputCosechaConsultar;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputCosechaCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputCosechaObtener;
import cl.duoc.portafolio.dto.v10.feriavirtual.TransporteType;
import cl.duoc.portafolio.feriavirtual.domain.Cosecha;
import cl.duoc.portafolio.feriavirtual.domain.Transporte;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.service.CosechaService;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;

@RestController
@RequestMapping("/{usuarioIdentificacion}/cosecha/v10")
public class CosechaController {

	private UsuarioService usuarioService;
	private CosechaService cosechaService;

	@Autowired
	public CosechaController(final UsuarioService usuarioService, final CosechaService cosechaService) {
		this.usuarioService = usuarioService;
		this.cosechaService = cosechaService;
	}

	@PostMapping
	ResponseEntity<OutputCosechaCrear> crear(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestBody final InputCosechaCrear inputDTO) {

		JAXBUtil.validarSchema(InputCosechaCrear.class, inputDTO);

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Cosecha cosecha = cosechaService.crear(usuario, inputDTO);

		final OutputCosechaCrear outputDTO = new OutputCosechaCrear();
		outputDTO.setID(cosecha.getId());
		outputDTO.setCodigoProducto(cosecha.getProducto().getCodigo());
		outputDTO.setCantidad(new BigDecimal(cosecha.getCantidad()));
		outputDTO.setUnidadMedida(cosecha.getUnidadMedida());
		outputDTO.setEstado(cosecha.getEstado());
		outputDTO.setCosto(cosecha.getCosto());

		return ResponseEntity.created(
				ServletUriComponentsBuilder.fromCurrentRequest().path(cosecha.getId().toString()).build().toUri())
				.body(outputDTO);
	}

	@GetMapping
	ResponseEntity<OutputCosechaConsultar> consultar(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestParam(name = "codProducto", required = false) final String codProducto,
			@RequestParam(name = "estado", required = false) final EstadoCosecha estado,
			@RequestParam(name = "offset", defaultValue = "0") Integer offset,
			@RequestParam(name = "limit", defaultValue = "100") Integer limit) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		List<Cosecha> cosechas = cosechaService.consultar(usuario, codProducto, estado, offset, limit);

		final OutputCosechaConsultar outputDTO = new OutputCosechaConsultar();
		CosechaType cosechaType;
		for (Cosecha cosecha : cosechas) {
			cosechaType = new CosechaType();
			cosechaType.setID(cosecha.getId());
			cosechaType.setCodigoProducto(cosecha.getProducto().getCodigo());
			cosechaType.setCantidad(new BigDecimal(cosecha.getCantidad()));
			cosechaType.setUnidadMedida(cosecha.getUnidadMedida());
			cosechaType.setEstado(cosecha.getEstado());
			cosechaType.setCosto(cosecha.getCosto());
			
			outputDTO.getRegistro().add(cosechaType);
		}

		return ResponseEntity.ok(outputDTO);
	}
	
	@GetMapping("/{id}")
	ResponseEntity<OutputCosechaObtener> obtener(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@PathVariable(name = "id") final Long id) {
		
		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Cosecha cosecha = cosechaService.obtener(usuario, id);
		
		final OutputCosechaObtener outputDTO = new OutputCosechaObtener();
		outputDTO.setID(cosecha.getId());
		outputDTO.setCodigoProducto(cosecha.getProducto().getCodigo());
		outputDTO.setCantidad(new BigDecimal(cosecha.getCantidad()));
		outputDTO.setUnidadMedida(cosecha.getUnidadMedida());
		outputDTO.setEstado(cosecha.getEstado());
		outputDTO.setCosto(cosecha.getCosto());
		
		TransporteType transporteType;
		for (Transporte transporte : cosecha.getTransportes()) {
			transporteType = new TransporteType();
			transporteType.setID(transporte.getId());
			transporteType.setAgricultor(transporte.getAgricultor().getIdentificacion());
			transporteType.setTransportista(transporte.getTransportista().getIdentificacion());
			transporteType.setLocatario(transporte.getLocatario().getIdentificacion());
			transporteType.setEstado(transporte.getEstado());
			transporteType.setDireccionOrigen(transporte.getDireccionOrigen());
			transporteType.setDireccionDestino(transporte.getDireccionDestino());
			transporteType.setFechaSalida(transporte.getFechaSalida());
			transporteType.setFechaLlegada(transporte.getFechaLlegada());
			transporteType.setCosto(transporte.getCosto());
			
			outputDTO.getTransporte().add(transporteType);
		}
		
		return ResponseEntity.ok(outputDTO);
	}

}
