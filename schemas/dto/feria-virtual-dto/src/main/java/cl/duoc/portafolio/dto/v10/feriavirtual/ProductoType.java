//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.05.21 a las 09:00:27 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * <p>Clase Java para ProductoType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ProductoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="tipo" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}TipoProducto"/&gt;
 *         &lt;element name="registroInstante" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="imagen" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductoType", propOrder = {
    "id",
    "nombre",
    "tipo",
    "registroInstante",
    "imagen"
})
@XmlSeeAlso({
    OutputProductoObtener.class,
    InputProductoActualizar.class,
    OutputProductoCrear.class,
    InputProductoCrear.class
})
public class ProductoType {

    @XmlElement(name = "ID")
    protected Long id;
    @XmlElement(required = true)
    protected String nombre;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TipoProducto tipo;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime registroInstante;
    protected Boolean imagen;

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
     * Obtiene el valor de la propiedad nombre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Define el valor de la propiedad nombre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombre(String value) {
        this.nombre = value;
    }

    /**
     * Obtiene el valor de la propiedad tipo.
     * 
     * @return
     *     possible object is
     *     {@link TipoProducto }
     *     
     */
    public TipoProducto getTipo() {
        return tipo;
    }

    /**
     * Define el valor de la propiedad tipo.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoProducto }
     *     
     */
    public void setTipo(TipoProducto value) {
        this.tipo = value;
    }

    /**
     * Obtiene el valor de la propiedad registroInstante.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getRegistroInstante() {
        return registroInstante;
    }

    /**
     * Define el valor de la propiedad registroInstante.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistroInstante(LocalDateTime value) {
        this.registroInstante = value;
    }

    /**
     * Obtiene el valor de la propiedad imagen.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isImagen() {
        return imagen;
    }

    /**
     * Define el valor de la propiedad imagen.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setImagen(Boolean value) {
        this.imagen = value;
    }

}
