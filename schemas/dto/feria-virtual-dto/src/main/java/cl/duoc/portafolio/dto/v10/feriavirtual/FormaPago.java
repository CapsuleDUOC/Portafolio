//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.11 a las 01:40:32 AM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para FormaPago.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="FormaPago"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CONTADO"/&gt;
 *     &lt;enumeration value="CREDITO"/&gt;
 *     &lt;enumeration value="ENTREGA_GRATUITA"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "FormaPago")
@XmlEnum
public enum FormaPago {

    CONTADO,
    CREDITO,
    ENTREGA_GRATUITA;

    public String value() {
        return name();
    }

    public static FormaPago fromValue(String v) {
        return valueOf(v);
    }

}
