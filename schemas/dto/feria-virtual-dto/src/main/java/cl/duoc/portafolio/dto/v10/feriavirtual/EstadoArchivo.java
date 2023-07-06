//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.06 a las 12:24:52 AM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para EstadoArchivo.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="EstadoArchivo"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="LOCAL"/&gt;
 *     &lt;enumeration value="BASE_DATOS"/&gt;
 *     &lt;enumeration value="ELIMINADO"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "EstadoArchivo")
@XmlEnum
public enum EstadoArchivo {

    LOCAL,
    BASE_DATOS,
    ELIMINADO;

    public String value() {
        return name();
    }

    public static EstadoArchivo fromValue(String v) {
        return valueOf(v);
    }

}
