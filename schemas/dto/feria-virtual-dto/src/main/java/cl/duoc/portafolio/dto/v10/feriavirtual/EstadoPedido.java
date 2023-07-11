//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.11 a las 12:49:46 AM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para EstadoPedido.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="EstadoPedido"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="GENERADO"/&gt;
 *     &lt;enumeration value="ACEPTADO"/&gt;
 *     &lt;enumeration value="RECHAZADO"/&gt;
 *     &lt;enumeration value="ENTREGADO"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "EstadoPedido")
@XmlEnum
public enum EstadoPedido {

    GENERADO,
    ACEPTADO,
    RECHAZADO,
    ENTREGADO;

    public String value() {
        return name();
    }

    public static EstadoPedido fromValue(String v) {
        return valueOf(v);
    }

}
