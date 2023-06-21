//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.06.21 a las 04:47:40 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="productoID" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="operacion" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}TipoOperacionCarrito"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "productoID",
    "operacion"
})
@XmlRootElement(name = "InputCarritoProductoActualizar")
public class InputCarritoProductoActualizar {

    protected long productoID;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TipoOperacionCarrito operacion;

    /**
     * Obtiene el valor de la propiedad productoID.
     * 
     */
    public long getProductoID() {
        return productoID;
    }

    /**
     * Define el valor de la propiedad productoID.
     * 
     */
    public void setProductoID(long value) {
        this.productoID = value;
    }

    /**
     * Obtiene el valor de la propiedad operacion.
     * 
     * @return
     *     possible object is
     *     {@link TipoOperacionCarrito }
     *     
     */
    public TipoOperacionCarrito getOperacion() {
        return operacion;
    }

    /**
     * Define el valor de la propiedad operacion.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoOperacionCarrito }
     *     
     */
    public void setOperacion(TipoOperacionCarrito value) {
        this.operacion = value;
    }

}
