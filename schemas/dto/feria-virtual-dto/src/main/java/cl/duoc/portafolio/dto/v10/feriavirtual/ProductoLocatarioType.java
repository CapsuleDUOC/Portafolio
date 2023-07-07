//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.06 a las 11:32:24 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ProductoLocatarioType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ProductoLocatarioType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="producto" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}ResumenProductoType"/&gt;
 *         &lt;element name="locatarios" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}LocatarioPrecioType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductoLocatarioType", propOrder = {
    "producto",
    "locatarios"
})
public class ProductoLocatarioType {

    @XmlElement(required = true)
    protected ResumenProductoType producto;
    @XmlElement(required = true)
    protected List<LocatarioPrecioType> locatarios;

    /**
     * Obtiene el valor de la propiedad producto.
     * 
     * @return
     *     possible object is
     *     {@link ResumenProductoType }
     *     
     */
    public ResumenProductoType getProducto() {
        return producto;
    }

    /**
     * Define el valor de la propiedad producto.
     * 
     * @param value
     *     allowed object is
     *     {@link ResumenProductoType }
     *     
     */
    public void setProducto(ResumenProductoType value) {
        this.producto = value;
    }

    /**
     * Gets the value of the locatarios property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the locatarios property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocatarios().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocatarioPrecioType }
     * 
     * 
     */
    public List<LocatarioPrecioType> getLocatarios() {
        if (locatarios == null) {
            locatarios = new ArrayList<LocatarioPrecioType>();
        }
        return this.locatarios;
    }

}
