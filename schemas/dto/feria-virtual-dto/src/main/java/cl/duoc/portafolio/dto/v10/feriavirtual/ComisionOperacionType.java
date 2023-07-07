//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.06 a las 11:32:24 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ComisionOperacionType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ComisionOperacionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="usuario" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}UsuarioType"/&gt;
 *         &lt;element name="comision" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}ComisionType"/&gt;
 *         &lt;element name="tipoOperacion" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}TipoOperacion"/&gt;
 *         &lt;element name="operacion" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}OperacionType"/&gt;
 *         &lt;element name="estado" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}EstadoComision"/&gt;
 *         &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComisionOperacionType", propOrder = {
    "id",
    "usuario",
    "comision",
    "tipoOperacion",
    "operacion",
    "estado",
    "valor"
})
public class ComisionOperacionType {

    @XmlElement(name = "ID")
    protected Long id;
    @XmlElement(required = true)
    protected UsuarioType usuario;
    @XmlElement(required = true)
    protected ComisionType comision;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected TipoOperacion tipoOperacion;
    @XmlElement(required = true)
    protected OperacionType operacion;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected EstadoComision estado;
    protected long valor;

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
     * Obtiene el valor de la propiedad usuario.
     * 
     * @return
     *     possible object is
     *     {@link UsuarioType }
     *     
     */
    public UsuarioType getUsuario() {
        return usuario;
    }

    /**
     * Define el valor de la propiedad usuario.
     * 
     * @param value
     *     allowed object is
     *     {@link UsuarioType }
     *     
     */
    public void setUsuario(UsuarioType value) {
        this.usuario = value;
    }

    /**
     * Obtiene el valor de la propiedad comision.
     * 
     * @return
     *     possible object is
     *     {@link ComisionType }
     *     
     */
    public ComisionType getComision() {
        return comision;
    }

    /**
     * Define el valor de la propiedad comision.
     * 
     * @param value
     *     allowed object is
     *     {@link ComisionType }
     *     
     */
    public void setComision(ComisionType value) {
        this.comision = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoOperacion.
     * 
     * @return
     *     possible object is
     *     {@link TipoOperacion }
     *     
     */
    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    /**
     * Define el valor de la propiedad tipoOperacion.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoOperacion }
     *     
     */
    public void setTipoOperacion(TipoOperacion value) {
        this.tipoOperacion = value;
    }

    /**
     * Obtiene el valor de la propiedad operacion.
     * 
     * @return
     *     possible object is
     *     {@link OperacionType }
     *     
     */
    public OperacionType getOperacion() {
        return operacion;
    }

    /**
     * Define el valor de la propiedad operacion.
     * 
     * @param value
     *     allowed object is
     *     {@link OperacionType }
     *     
     */
    public void setOperacion(OperacionType value) {
        this.operacion = value;
    }

    /**
     * Obtiene el valor de la propiedad estado.
     * 
     * @return
     *     possible object is
     *     {@link EstadoComision }
     *     
     */
    public EstadoComision getEstado() {
        return estado;
    }

    /**
     * Define el valor de la propiedad estado.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoComision }
     *     
     */
    public void setEstado(EstadoComision value) {
        this.estado = value;
    }

    /**
     * Obtiene el valor de la propiedad valor.
     * 
     */
    public long getValor() {
        return valor;
    }

    /**
     * Define el valor de la propiedad valor.
     * 
     */
    public void setValor(long value) {
        this.valor = value;
    }

}
