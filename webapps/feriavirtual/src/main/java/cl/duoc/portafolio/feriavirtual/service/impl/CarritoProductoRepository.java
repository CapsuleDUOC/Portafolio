package cl.duoc.portafolio.feriavirtual.service.impl;

import org.springframework.data.repository.CrudRepository;

import cl.duoc.portafolio.feriavirtual.domain.Carrito;
import cl.duoc.portafolio.feriavirtual.domain.CarritoProducto;
import cl.duoc.portafolio.feriavirtual.domain.Producto;

public interface CarritoProductoRepository extends CrudRepository<CarritoProducto, Long>{

	CarritoProducto findByCarritoAndProducto(final Carrito carrito, final Producto producto);

}
