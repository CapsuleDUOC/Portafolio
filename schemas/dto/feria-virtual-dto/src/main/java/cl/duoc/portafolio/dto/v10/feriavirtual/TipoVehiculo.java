//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.05.21 a las 10:06:43 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para TipoVehiculo.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="TipoVehiculo"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AUTOMOVIL"/&gt;
 *     &lt;enumeration value="MOTOCICLETA"/&gt;
 *     &lt;enumeration value="CAMIONETA"/&gt;
 *     &lt;enumeration value="CAMION"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TipoVehiculo")
@XmlEnum
public enum TipoVehiculo {

    AUTOMOVIL,
    MOTOCICLETA,
    CAMIONETA,
    CAMION;

    public String value() {
        return name();
    }

    public static TipoVehiculo fromValue(String v) {
        return valueOf(v);
    }

}
