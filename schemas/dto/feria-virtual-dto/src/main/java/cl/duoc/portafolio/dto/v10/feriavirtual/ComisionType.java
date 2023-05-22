//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.05.21 a las 09:00:27 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ComisionType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ComisionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="tipoOperacion" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}TipoOperacion"/&gt;
 *         &lt;element name="factor" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComisionType", propOrder = {
    "id",
    "tipoOperacion",
    "factor"
})
public class ComisionType {

    @XmlElement(name = "ID")
    protected Long id;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TipoOperacion tipoOperacion;
    protected double factor;

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
     * Obtiene el valor de la propiedad tipoOperacion.
     * 
     * @return
     *     possible object is
     *     {@link TipoOperacion }
     *     
     */
    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    /**
     * Define el valor de la propiedad tipoOperacion.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoOperacion }
     *     
     */
    public void setTipoOperacion(TipoOperacion value) {
        this.tipoOperacion = value;
    }

    /**
     * Obtiene el valor de la propiedad factor.
     * 
     */
    public double getFactor() {
        return factor;
    }

    /**
     * Define el valor de la propiedad factor.
     * 
     */
    public void setFactor(double value) {
        this.factor = value;
    }

}
