//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.05 a las 06:53:11 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import java.util.ArrayList;
import java.util.List;
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
 *     &lt;extension base="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}TransporteType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cosecha" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}CosechaType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "cosecha"
})
@XmlRootElement(name = "OutputTransporteObtener")
public class OutputTransporteObtener
    extends TransporteType
{

    protected List<CosechaType> cosecha;

    /**
     * Gets the value of the cosecha property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cosecha property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCosecha().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CosechaType }
     * 
     * 
     */
    public List<CosechaType> getCosecha() {
        if (cosecha == null) {
            cosecha = new ArrayList<CosechaType>();
        }
        return this.cosecha;
    }

}
