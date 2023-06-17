//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.06.17 a las 04:36:40 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para TipoOperacion.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="TipoOperacion"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="COSECHA"/&gt;
 *     &lt;enumeration value="DESPACHO"/&gt;
 *     &lt;enumeration value="VENTA"/&gt;
 *     &lt;enumeration value="TRANSPORTE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TipoOperacion")
@XmlEnum
public enum TipoOperacion {

    COSECHA,
    DESPACHO,
    VENTA,
    TRANSPORTE;

    public String value() {
        return name();
    }

    public static TipoOperacion fromValue(String v) {
        return valueOf(v);
    }

}
