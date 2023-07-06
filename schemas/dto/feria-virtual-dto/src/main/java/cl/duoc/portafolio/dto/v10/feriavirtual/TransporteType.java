//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.06 a las 12:13:46 AM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import java.time.LocalDate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
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
 *         &lt;element name="agricultor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="transportista" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="locatario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="estado" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}EstadoTransporte" minOccurs="0"/&gt;
 *         &lt;element name="direccionOrigen" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="direccionDestino" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
    "estado",
    "direccionOrigen",
    "direccionDestino",
    "fechaSalida",
    "fechaLlegada",
    "costo"
})
@XmlSeeAlso({
    InputTransporteActualizar.class,
    OutputTransporteObtener.class,
    OutputTransporteCrear.class,
    InputTransporteCrear.class
})
public class TransporteType {

    @XmlElement(name = "ID")
    protected Long id;
    protected String agricultor;
    protected String transportista;
    protected String locatario;
    @XmlSchemaType(name = "string")
    protected EstadoTransporte estado;
    @XmlElement(required = true)
    protected String direccionOrigen;
    @XmlElement(required = true)
    protected String direccionDestino;
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
     *     {@link String }
     *     
     */
    public String getAgricultor() {
        return agricultor;
    }

    /**
     * Define el valor de la propiedad agricultor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgricultor(String value) {
        this.agricultor = value;
    }

    /**
     * Obtiene el valor de la propiedad transportista.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransportista() {
        return transportista;
    }

    /**
     * Define el valor de la propiedad transportista.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransportista(String value) {
        this.transportista = value;
    }

    /**
     * Obtiene el valor de la propiedad locatario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocatario() {
        return locatario;
    }

    /**
     * Define el valor de la propiedad locatario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocatario(String value) {
        this.locatario = value;
    }

    /**
     * Obtiene el valor de la propiedad estado.
     * 
     * @return
     *     possible object is
     *     {@link EstadoTransporte }
     *     
     */
    public EstadoTransporte getEstado() {
        return estado;
    }

    /**
     * Define el valor de la propiedad estado.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoTransporte }
     *     
     */
    public void setEstado(EstadoTransporte value) {
        this.estado = value;
    }

    /**
     * Obtiene el valor de la propiedad direccionOrigen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDireccionOrigen() {
        return direccionOrigen;
    }

    /**
     * Define el valor de la propiedad direccionOrigen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDireccionOrigen(String value) {
        this.direccionOrigen = value;
    }

    /**
     * Obtiene el valor de la propiedad direccionDestino.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDireccionDestino() {
        return direccionDestino;
    }

    /**
     * Define el valor de la propiedad direccionDestino.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDireccionDestino(String value) {
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
