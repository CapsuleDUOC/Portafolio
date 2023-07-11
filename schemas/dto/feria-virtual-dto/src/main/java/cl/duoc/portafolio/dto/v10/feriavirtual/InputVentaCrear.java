//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.11 a las 12:49:46 AM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element name="carritoID" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="direccionID" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
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
    "carritoID",
    "direccionID"
})
@XmlRootElement(name = "InputVentaCrear")
public class InputVentaCrear {

    protected long carritoID;
    protected long direccionID;

    /**
     * Obtiene el valor de la propiedad carritoID.
     * 
     */
    public long getCarritoID() {
        return carritoID;
    }

    /**
     * Define el valor de la propiedad carritoID.
     * 
     */
    public void setCarritoID(long value) {
        this.carritoID = value;
    }

    /**
     * Obtiene el valor de la propiedad direccionID.
     * 
     */
    public long getDireccionID() {
        return direccionID;
    }

    /**
     * Define el valor de la propiedad direccionID.
     * 
     */
    public void setDireccionID(long value) {
        this.direccionID = value;
    }

}
