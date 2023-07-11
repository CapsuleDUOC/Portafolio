//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.11 a las 01:40:32 AM CLT 
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
 *         &lt;element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="nombre" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="tipo" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}TipoProducto"/&gt;
 *         &lt;element name="unidadMedida" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}UnidadMedida"/&gt;
 *         &lt;element name="precio" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="estado" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}EstadoProducto"/&gt;
 *         &lt;element name="registroInstante" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="imagen" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
    "codigo",
    "nombre",
    "tipo",
    "unidadMedida",
    "precio",
    "estado",
    "registroInstante",
    "imagen"
})
@XmlSeeAlso({
    CarritoProductoType.class,
    OutputProductoObtener.class,
    InputProductoActualizar.class,
    OutputProductoCrear.class,
    InputProductoCrear.class
})
public class ProductoType {

    @XmlElement(name = "ID")
    protected Long id;
    @XmlElement(required = true)
    protected String codigo;
    @XmlElement(required = true)
    protected String nombre;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TipoProducto tipo;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected UnidadMedida unidadMedida;
    protected long precio;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected EstadoProducto estado;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime registroInstante;
    protected String imagen;

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
     * Obtiene el valor de la propiedad codigo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Define el valor de la propiedad codigo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigo(String value) {
        this.codigo = value;
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

    /**
     * Obtiene el valor de la propiedad estado.
     * 
     * @return
     *     possible object is
     *     {@link EstadoProducto }
     *     
     */
    public EstadoProducto getEstado() {
        return estado;
    }

    /**
     * Define el valor de la propiedad estado.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoProducto }
     *     
     */
    public void setEstado(EstadoProducto value) {
        this.estado = value;
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
     *     {@link String }
     *     
     */
    public String getImagen() {
        return imagen;
    }

    /**
     * Define el valor de la propiedad imagen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImagen(String value) {
        this.imagen = value;
    }

}
