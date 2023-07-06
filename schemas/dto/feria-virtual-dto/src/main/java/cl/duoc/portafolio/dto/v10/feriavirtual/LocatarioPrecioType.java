//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.06 a las 12:13:46 AM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para LocatarioPrecioType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LocatarioPrecioType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="locatario" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}UsuarioType"/&gt;
 *         &lt;element name="precio" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocatarioPrecioType", propOrder = {
    "locatario",
    "precio"
})
public class LocatarioPrecioType {

    @XmlElement(required = true)
    protected UsuarioType locatario;
    protected long precio;

    /**
     * Obtiene el valor de la propiedad locatario.
     * 
     * @return
     *     possible object is
     *     {@link UsuarioType }
     *     
     */
    public UsuarioType getLocatario() {
        return locatario;
    }

    /**
     * Define el valor de la propiedad locatario.
     * 
     * @param value
     *     allowed object is
     *     {@link UsuarioType }
     *     
     */
    public void setLocatario(UsuarioType value) {
        this.locatario = value;
    }

    /**
     * Obtiene el valor de la propiedad precio.
     * 
     */
    public long getPrecio() {
        return precio;
    }

    /**
     * Define el valor de la propiedad precio.
     * 
     */
    public void setPrecio(long value) {
        this.precio = value;
    }

}
