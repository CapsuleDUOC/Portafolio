//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.10 a las 10:47:57 PM CLT 
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
 *     &lt;extension base="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}ProductoType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bytesImagen" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "bytesImagen"
})
@XmlRootElement(name = "InputProductoActualizar")
public class InputProductoActualizar
    extends ProductoType
{

    protected byte[] bytesImagen;

    /**
     * Obtiene el valor de la propiedad bytesImagen.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getBytesImagen() {
        return bytesImagen;
    }

    /**
     * Define el valor de la propiedad bytesImagen.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setBytesImagen(byte[] value) {
        this.bytesImagen = value;
    }

}
