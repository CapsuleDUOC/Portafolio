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
 * <p>Clase Java para TipoOperacionCarrito.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="TipoOperacionCarrito"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AGREGAR"/&gt;
 *     &lt;enumeration value="ELIMINAR"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TipoOperacionCarrito")
@XmlEnum
public enum TipoOperacionCarrito {

    AGREGAR,
    ELIMINAR;

    public String value() {
        return name();
    }

    public static TipoOperacionCarrito fromValue(String v) {
        return valueOf(v);
    }

}
