package cl.duoc.portafolio.feriavirtual.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.portafolio.dto.v10.feriavirtual.FormaPago;
import cl.duoc.portafolio.feriavirtual.domain.CarritoProducto;
import cl.duoc.portafolio.feriavirtual.domain.Dte;
import cl.duoc.portafolio.feriavirtual.domain.Usuario;
import cl.duoc.portafolio.feriavirtual.repository.DteRepository;
import cl.duoc.portafolio.feriavirtual.service.DteService;

@Service
public class DteServiceImpl implements DteService {

	private DteRepository dteRepository;

	@Autowired
	public DteServiceImpl(final DteRepository dteRepository) {
		this.dteRepository = dteRepository;
	}

	@Override
	public Dte crear(Usuario locatario, Usuario cliente, List<CarritoProducto> productos) {

		Long factorIVA = 19L;
		BigDecimal totalBruto = BigDecimal.ZERO;
		for (CarritoProducto carritoProducto : productos) {
			totalBruto = totalBruto.add(BigDecimal.valueOf(carritoProducto.getProducto().getPrecio())
					.add(BigDecimal.valueOf(carritoProducto.getCantidad())));
		}

		BigDecimal iva = totalBruto.multiply(BigDecimal.valueOf(factorIVA).divide(BigDecimal.valueOf(100)));

		Dte dte = new Dte();
		dte.setEmisor(locatario);
		dte.setReceptor(cliente);
		dte.setXml(null);
		dte.setTipoDte(39);
		dte.setFolio(1L);
		dte.setFormaPago(FormaPago.CONTADO);
		dte.setFechaEmision(LocalDate.now());
		dte.setTotalNeto(totalBruto.subtract(iva).longValue());
		dte.setTotalBruto(totalBruto.longValue());

		return dteRepository.save(dte);
	}
}
