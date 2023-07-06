//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.06 a las 12:13:46 AM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para EstadoUsuario.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="EstadoUsuario"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ACTIVO"/&gt;
 *     &lt;enumeration value="INACTIVO"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "EstadoUsuario")
@XmlEnum
public enum EstadoUsuario {

    ACTIVO,
    INACTIVO;

    public String value() {
        return name();
    }

    public static EstadoUsuario fromValue(String v) {
        return valueOf(v);
    }

}
