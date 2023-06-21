//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.06.21 a las 04:54:35 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para TipoUsuario.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="TipoUsuario"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="SUPER_ADMIN"/&gt;
 *     &lt;enumeration value="ADMIN"/&gt;
 *     &lt;enumeration value="PROVEEDOR"/&gt;
 *     &lt;enumeration value="LOCATARIO"/&gt;
 *     &lt;enumeration value="TRANSPORTISTA"/&gt;
 *     &lt;enumeration value="CLIENTE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TipoUsuario")
@XmlEnum
public enum TipoUsuario {

    SUPER_ADMIN,
    ADMIN,
    PROVEEDOR,
    LOCATARIO,
    TRANSPORTISTA,
    CLIENTE;

    public String value() {
        return name();
    }

    public static TipoUsuario fromValue(String v) {
        return valueOf(v);
    }

}
