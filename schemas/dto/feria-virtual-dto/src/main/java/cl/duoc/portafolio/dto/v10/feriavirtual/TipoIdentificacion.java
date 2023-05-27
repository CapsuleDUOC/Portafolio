//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.05.27 a las 12:39:41 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para TipoIdentificacion.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="TipoIdentificacion"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="RUN"/&gt;
 *     &lt;enumeration value="PASAPORTE"/&gt;
 *     &lt;enumeration value="REGISTRO_SAG"/&gt;
 *     &lt;enumeration value="PATENTE_MUNICIPAL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TipoIdentificacion")
@XmlEnum
public enum TipoIdentificacion {

    RUN,
    PASAPORTE,
    REGISTRO_SAG,
    PATENTE_MUNICIPAL;

    public String value() {
        return name();
    }

    public static TipoIdentificacion fromValue(String v) {
        return valueOf(v);
    }

}
