package cl.duoc.portafolio.feriavirtual.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import cl.duoc.portafolio.dto.v10.feriavirtual.EstadoTransporte;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputTransporteActualizar;
import cl.duoc.portafolio.dto.v10.feriavirtual.InputTransporteCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputTransporteConsultar;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputTransporteCrear;
import cl.duoc.portafolio.dto.v10.feriavirtual.OutputTransporteObtener;
import cl.duoc.portafolio.dto.v10.feriavirtual.TransporteType;
import cl.duoc.portafolio.feriavirtual.domain.Cosecha;
import cl.duoc.portafolio.feriavirtual.domain.Transporte;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.service.TransporteService;
import cl.duoc.portafolio.feriavirtual.service.UsuarioService;

@RestController
@RequestMapping("/{usuarioIdentificacion}/transporte/v10")
public class TransporteController {

	private UsuarioService usuarioService;
	private TransporteService transporteService;

	@Autowired
	public TransporteController(final UsuarioService usuarioService, final TransporteService transporteService) {
		this.usuarioService = usuarioService;
		this.transporteService = transporteService;
	}

	@PostMapping
	ResponseEntity<OutputTransporteCrear> crear(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestBody final InputTransporteCrear inputDTO) {

		JAXBUtil.validarSchema(InputTransporteCrear.class, inputDTO);

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Transporte transporte = transporteService.crear(usuario, inputDTO);

		final OutputTransporteCrear outputDTO = new OutputTransporteCrear();
		outputDTO.setID(transporte.getId());
		outputDTO.setAgricultor(transporte.getAgricultor().getIdentificacion());
		outputDTO.setTransportista(transporte.getTransportista().getIdentificacion());
		outputDTO.setLocatario(transporte.getLocatario().getIdentificacion());
		outputDTO.setEstado(transporte.getEstado());
		outputDTO.setDireccionOrigen(transporte.getDireccionOrigen());
		outputDTO.setDireccionDestino(transporte.getDireccionDestino());
		outputDTO.setFechaSalida(transporte.getFechaSalida());
		outputDTO.setFechaLlegada(transporte.getFechaLlegada());
		outputDTO.setCosto(transporte.getCosto());

		return ResponseEntity.created(
				ServletUriComponentsBuilder.fromCurrentRequest().path(transporte.getId().toString()).build().toUri())
				.body(outputDTO);
	}

	@GetMapping
	ResponseEntity<OutputTransporteConsultar> consultar(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@RequestParam(name = "agricultor", required = false) final String agricultor,
			@RequestParam(name = "transportista", required = false) final String transportista,
			@RequestParam(name = "locatario", required = false) final String locatario,
			@RequestParam(name = "estado", required = false) final EstadoTransporte estado,
			@RequestParam(name = "fechaSalida", required = false) final LocalDate fechaSalida,
			@RequestParam(name = "fechaLlegada", required = false) final LocalDate fechaLlegada,
			@RequestParam(name = "offset", defaultValue = "0") Integer offset,
			@RequestParam(name = "limit", defaultValue = "100") Integer limit) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		List<Transporte> transportes = transporteService.consultar(usuario, agricultor, transportista, locatario,
				estado, fechaSalida, fechaLlegada, offset, limit);

		final OutputTransporteConsultar outputDTO = new OutputTransporteConsultar();

		TransporteType transporteType;
		for (Transporte transporte : transportes) {
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

			outputDTO.getRegistro().add(transporteType);
		}

		return ResponseEntity.ok(outputDTO);
	}

	@PostMapping("/{id}")
	ResponseEntity<Boolean> actualizar(@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@PathVariable(name = "id") final Long id, @RequestBody final InputTransporteActualizar inputDTO) {

		JAXBUtil.validarSchema(InputTransporteCrear.class, inputDTO);
		
		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Transporte transporte = transporteService.obtener(usuario, id);
		final Boolean actualizar = transporteService.actualizar(transporte, inputDTO);

		return ResponseEntity.ok(actualizar);
	}

	@GetMapping("/{id}")
	ResponseEntity<OutputTransporteObtener> obtener(
			@PathVariable(name = "usuarioIdentificacion") final String usuarioIdentificacion,
			@PathVariable(name = "id") final Long id) {

		final Usuario usuario = usuarioService.obtener(usuarioIdentificacion);
		final Transporte transporte = transporteService.obtener(usuario, id);

		final OutputTransporteObtener outputDTO = new OutputTransporteObtener();
		outputDTO.setID(transporte.getId());
		outputDTO.setAgricultor(transporte.getAgricultor().getIdentificacion());
		outputDTO.setTransportista(transporte.getTransportista().getIdentificacion());
		outputDTO.setLocatario(transporte.getLocatario().getIdentificacion());
		outputDTO.setEstado(transporte.getEstado());
		outputDTO.setDireccionOrigen(transporte.getDireccionOrigen());
		outputDTO.setDireccionDestino(transporte.getDireccionDestino());
		outputDTO.setFechaSalida(transporte.getFechaSalida());
		outputDTO.setFechaLlegada(transporte.getFechaLlegada());
		outputDTO.setCosto(transporte.getCosto());

		CosechaType cosechaType;
		for (Cosecha cosecha : transporte.getCosechas()) {
			cosechaType = new CosechaType();
			cosechaType.setID(cosecha.getId());
			cosechaType.setCodigoProducto(cosecha.getProducto().getCodigo());
			cosechaType.setCantidad(new BigDecimal(cosecha.getCantidad()));
			cosechaType.setUnidadMedida(cosecha.getUnidadMedida());
			cosechaType.setEstado(cosecha.getEstado());
			cosechaType.setCosto(cosecha.getCosto());

			outputDTO.getCosecha().add(cosechaType);
		}

		return ResponseEntity.ok(outputDTO);

	}
}
