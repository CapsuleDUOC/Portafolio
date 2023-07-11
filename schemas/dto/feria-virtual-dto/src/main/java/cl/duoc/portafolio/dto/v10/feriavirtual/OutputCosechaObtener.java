//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.10 a las 10:09:47 PM CLT 
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
 *     &lt;extension base="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}CosechaType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transporte" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}TransporteType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "transporte"
})
@XmlRootElement(name = "OutputCosechaObtener")
public class OutputCosechaObtener
    extends CosechaType
{

    protected List<TransporteType> transporte;

    /**
     * Gets the value of the transporte property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transporte property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransporte().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransporteType }
     * 
     * 
     */
    public List<TransporteType> getTransporte() {
        if (transporte == null) {
            transporte = new ArrayList<TransporteType>();
        }
        return this.transporte;
    }

}
