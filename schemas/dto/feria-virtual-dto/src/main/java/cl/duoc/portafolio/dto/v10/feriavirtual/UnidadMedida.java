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
 * <p>Clase Java para UnidadMedida.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="UnidadMedida"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="GRAMOS"/&gt;
 *     &lt;enumeration value="KILOGRAMOS"/&gt;
 *     &lt;enumeration value="MILILITROS"/&gt;
 *     &lt;enumeration value="LITROS"/&gt;
 *     &lt;enumeration value="UNIDADES"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "UnidadMedida")
@XmlEnum
public enum UnidadMedida {

    GRAMOS,
    KILOGRAMOS,
    MILILITROS,
    LITROS,
    UNIDADES;

    public String value() {
        return name();
    }

    public static UnidadMedida fromValue(String v) {
        return valueOf(v);
    }

}
