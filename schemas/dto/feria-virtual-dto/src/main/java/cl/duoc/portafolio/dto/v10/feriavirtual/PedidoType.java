//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.10 a las 10:09:47 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


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
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="despachador" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}UsuarioType" minOccurs="0"/&gt;
 *         &lt;element name="estado" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}EstadoPedido" minOccurs="0"/&gt;
 *         &lt;element name="patenteVehiculo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="direccionOrigen" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="direccionDestino" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="montoDespacho" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="registroInstante" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
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
    "estado",
    "patenteVehiculo",
    "direccionOrigen",
    "direccionDestino",
    "montoDespacho",
    "registroInstante"
})
public class PedidoType {

    @XmlElement(name = "ID")
    protected Long id;
    protected UsuarioType despachador;
    @XmlSchemaType(name = "string")
    protected EstadoPedido estado;
    protected String patenteVehiculo;
    @XmlElement(required = true)
    protected String direccionOrigen;
    @XmlElement(required = true)
    protected String direccionDestino;
    protected long montoDespacho;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime registroInstante;

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
     * Obtiene el valor de la propiedad estado.
     * 
     * @return
     *     possible object is
     *     {@link EstadoPedido }
     *     
     */
    public EstadoPedido getEstado() {
        return estado;
    }

    /**
     * Define el valor de la propiedad estado.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoPedido }
     *     
     */
    public void setEstado(EstadoPedido value) {
        this.estado = value;
    }

    /**
     * Obtiene el valor de la propiedad patenteVehiculo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatenteVehiculo() {
        return patenteVehiculo;
    }

    /**
     * Define el valor de la propiedad patenteVehiculo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatenteVehiculo(String value) {
        this.patenteVehiculo = value;
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

}
