//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.05.21 a las 08:40:24 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import java.time.LocalDate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter2;


/**
 * <p>Clase Java para TransporteType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="TransporteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="agricultor" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}UsuarioType" minOccurs="0"/&gt;
 *         &lt;element name="transportista" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}UsuarioType" minOccurs="0"/&gt;
 *         &lt;element name="locatario" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}UsuarioType" minOccurs="0"/&gt;
 *         &lt;element name="direccionOrigen" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}DireccionType"/&gt;
 *         &lt;element name="direccionDestino" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}DireccionType"/&gt;
 *         &lt;element name="fechaSalida" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="fechaLlegada" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="costo" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransporteType", propOrder = {
    "id",
    "agricultor",
    "transportista",
    "locatario",
    "direccionOrigen",
    "direccionDestino",
    "fechaSalida",
    "fechaLlegada",
    "costo"
})
public class TransporteType {

    @XmlElement(name = "ID")
    protected Long id;
    protected UsuarioType agricultor;
    protected UsuarioType transportista;
    protected UsuarioType locatario;
    @XmlElement(required = true)
    protected DireccionType direccionOrigen;
    @XmlElement(required = true)
    protected DireccionType direccionDestino;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate fechaSalida;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "date")
    protected LocalDate fechaLlegada;
    protected Long costo;

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
     * Obtiene el valor de la propiedad agricultor.
     * 
     * @return
     *     possible object is
     *     {@link UsuarioType }
     *     
     */
    public UsuarioType getAgricultor() {
        return agricultor;
    }

    /**
     * Define el valor de la propiedad agricultor.
     * 
     * @param value
     *     allowed object is
     *     {@link UsuarioType }
     *     
     */
    public void setAgricultor(UsuarioType value) {
        this.agricultor = value;
    }

    /**
     * Obtiene el valor de la propiedad transportista.
     * 
     * @return
     *     possible object is
     *     {@link UsuarioType }
     *     
     */
    public UsuarioType getTransportista() {
        return transportista;
    }

    /**
     * Define el valor de la propiedad transportista.
     * 
     * @param value
     *     allowed object is
     *     {@link UsuarioType }
     *     
     */
    public void setTransportista(UsuarioType value) {
        this.transportista = value;
    }

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
     * Obtiene el valor de la propiedad fechaSalida.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    /**
     * Define el valor de la propiedad fechaSalida.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaSalida(LocalDate value) {
        this.fechaSalida = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaLlegada.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getFechaLlegada() {
        return fechaLlegada;
    }

    /**
     * Define el valor de la propiedad fechaLlegada.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaLlegada(LocalDate value) {
        this.fechaLlegada = value;
    }

    /**
     * Obtiene el valor de la propiedad costo.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCosto() {
        return costo;
    }

    /**
     * Define el valor de la propiedad costo.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCosto(Long value) {
        this.costo = value;
    }

}
