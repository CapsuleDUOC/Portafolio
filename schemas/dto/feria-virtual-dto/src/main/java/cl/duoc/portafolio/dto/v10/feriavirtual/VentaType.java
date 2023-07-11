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
 * <p>Clase Java para VentaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="VentaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="locatario" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}UsuarioType"/&gt;
 *         &lt;element name="cliente" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}UsuarioType"/&gt;
 *         &lt;element name="pedido" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}PedidoType" minOccurs="0"/&gt;
 *         &lt;element name="monto" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
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
@XmlType(name = "VentaType", propOrder = {
    "id",
    "locatario",
    "cliente",
    "pedido",
    "monto",
    "registroInstante"
})
@XmlSeeAlso({
    OutputVentaCrear.class
})
public class VentaType {

    @XmlElement(name = "ID")
    protected Long id;
    @XmlElement(required = true)
    protected UsuarioType locatario;
    @XmlElement(required = true)
    protected UsuarioType cliente;
    protected PedidoType pedido;
    protected long monto;
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
     * Obtiene el valor de la propiedad cliente.
     * 
     * @return
     *     possible object is
     *     {@link UsuarioType }
     *     
     */
    public UsuarioType getCliente() {
        return cliente;
    }

    /**
     * Define el valor de la propiedad cliente.
     * 
     * @param value
     *     allowed object is
     *     {@link UsuarioType }
     *     
     */
    public void setCliente(UsuarioType value) {
        this.cliente = value;
    }

    /**
     * Obtiene el valor de la propiedad pedido.
     * 
     * @return
     *     possible object is
     *     {@link PedidoType }
     *     
     */
    public PedidoType getPedido() {
        return pedido;
    }

    /**
     * Define el valor de la propiedad pedido.
     * 
     * @param value
     *     allowed object is
     *     {@link PedidoType }
     *     
     */
    public void setPedido(PedidoType value) {
        this.pedido = value;
    }

    /**
     * Obtiene el valor de la propiedad monto.
     * 
     */
    public long getMonto() {
        return monto;
    }

    /**
     * Define el valor de la propiedad monto.
     * 
     */
    public void setMonto(long value) {
        this.monto = value;
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
