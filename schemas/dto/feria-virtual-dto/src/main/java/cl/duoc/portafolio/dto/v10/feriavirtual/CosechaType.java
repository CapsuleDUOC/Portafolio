//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.11 a las 01:40:32 AM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para CosechaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CosechaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="codigoProducto" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cantidad" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="unidadMedida" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}UnidadMedida"/&gt;
 *         &lt;element name="estado" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}EstadoCosecha" minOccurs="0"/&gt;
 *         &lt;element name="costo" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CosechaType", propOrder = {
    "id",
    "codigoProducto",
    "cantidad",
    "unidadMedida",
    "estado",
    "costo"
})
@XmlSeeAlso({
    OutputCosechaObtener.class,
    OutputCosechaCrear.class,
    InputCosechaCrear.class
})
public class CosechaType {

    @XmlElement(name = "ID")
    protected Long id;
    @XmlElement(required = true)
    protected String codigoProducto;
    @XmlElement(required = true)
    protected BigDecimal cantidad;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected UnidadMedida unidadMedida;
    @XmlSchemaType(name = "string")
    protected EstadoCosecha estado;
    protected long costo;

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getID() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setID(Long value) {
        this.id = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoProducto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoProducto() {
        return codigoProducto;
    }

    /**
     * Define el valor de la propiedad codigoProducto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoProducto(String value) {
        this.codigoProducto = value;
    }

    /**
     * Obtiene el valor de la propiedad cantidad.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCantidad() {
        return cantidad;
    }

    /**
     * Define el valor de la propiedad cantidad.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCantidad(BigDecimal value) {
        this.cantidad = value;
    }

    /**
     * Obtiene el valor de la propiedad unidadMedida.
     * 
     * @return
     *     possible object is
     *     {@link UnidadMedida }
     *     
     */
    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    /**
     * Define el valor de la propiedad unidadMedida.
     * 
     * @param value
     *     allowed object is
     *     {@link UnidadMedida }
     *     
     */
    public void setUnidadMedida(UnidadMedida value) {
        this.unidadMedida = value;
    }

    /**
     * Obtiene el valor de la propiedad estado.
     * 
     * @return
     *     possible object is
     *     {@link EstadoCosecha }
     *     
     */
    public EstadoCosecha getEstado() {
        return estado;
    }

    /**
     * Define el valor de la propiedad estado.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoCosecha }
     *     
     */
    public void setEstado(EstadoCosecha value) {
        this.estado = value;
    }

    /**
     * Obtiene el valor de la propiedad costo.
     * 
     */
    public long getCosto() {
        return costo;
    }

    /**
     * Define el valor de la propiedad costo.
     * 
     */
    public void setCosto(long value) {
        this.costo = value;
    }

}
