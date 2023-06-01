//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.05.31 a las 11:56:22 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import java.time.LocalDate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import org.w3._2001.xmlschema.Adapter2;


/**
 * <p>Clase Java para PedidoType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="PedidoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="despachador" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}UsuarioType" minOccurs="0"/&gt;
 *         &lt;element name="direccionOrigen" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}DireccionType"/&gt;
 *         &lt;element name="direccionDestino" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}DireccionType"/&gt;
 *         &lt;element name="montoDespacho" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="cantidad" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="unidadMedida" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}UnidadMedida"/&gt;
 *         &lt;element name="fecha" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="hora" type="{http://www.w3.org/2001/XMLSchema}time"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PedidoType", propOrder = {
    "id",
    "despachador",
    "direccionOrigen",
    "direccionDestino",
    "montoDespacho",
    "cantidad",
    "unidadMedida",
    "fecha",
    "hora"
})
public class PedidoType {

    @XmlElement(name = "ID")
    protected String id;
    protected UsuarioType despachador;
    @XmlElement(required = true)
    protected DireccionType direccionOrigen;
    @XmlElement(required = true)
    protected DireccionType direccionDestino;
    protected long montoDespacho;
    protected long cantidad;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected UnidadMedida unidadMedida;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate fecha;
    @XmlElement(required = true)
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar hora;

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getID() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.id = value;
    }

    /**
     * Obtiene el valor de la propiedad despachador.
     * 
     * @return
     *     possible object is
     *     {@link UsuarioType }
     *     
     */
    public UsuarioType getDespachador() {
        return despachador;
    }

    /**
     * Define el valor de la propiedad despachador.
     * 
     * @param value
     *     allowed object is
     *     {@link UsuarioType }
     *     
     */
    public void setDespachador(UsuarioType value) {
        this.despachador = value;
    }

    /**
     * Obtiene el valor de la propiedad direccionOrigen.
     * 
     * @return
     *     possible object is
     *     {@link DireccionType }
     *     
     */
    public DireccionType getDireccionOrigen() {
        return direccionOrigen;
    }

    /**
     * Define el valor de la propiedad direccionOrigen.
     * 
     * @param value
     *     allowed object is
     *     {@link DireccionType }
     *     
     */
    public void setDireccionOrigen(DireccionType value) {
        this.direccionOrigen = value;
    }

    /**
     * Obtiene el valor de la propiedad direccionDestino.
     * 
     * @return
     *     possible object is
     *     {@link DireccionType }
     *     
     */
    public DireccionType getDireccionDestino() {
        return direccionDestino;
    }

    /**
     * Define el valor de la propiedad direccionDestino.
     * 
     * @param value
     *     allowed object is
     *     {@link DireccionType }
     *     
     */
    public void setDireccionDestino(DireccionType value) {
        this.direccionDestino = value;
    }

    /**
     * Obtiene el valor de la propiedad montoDespacho.
     * 
     */
    public long getMontoDespacho() {
        return montoDespacho;
    }

    /**
     * Define el valor de la propiedad montoDespacho.
     * 
     */
    public void setMontoDespacho(long value) {
        this.montoDespacho = value;
    }

    /**
     * Obtiene el valor de la propiedad cantidad.
     * 
     */
    public long getCantidad() {
        return cantidad;
    }

    /**
     * Define el valor de la propiedad cantidad.
     * 
     */
    public void setCantidad(long value) {
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
     * Obtiene el valor de la propiedad fecha.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Define el valor de la propiedad fecha.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFecha(LocalDate value) {
        this.fecha = value;
    }

    /**
     * Obtiene el valor de la propiedad hora.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getHora() {
        return hora;
    }

    /**
     * Define el valor de la propiedad hora.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setHora(XMLGregorianCalendar value) {
        this.hora = value;
    }

}
