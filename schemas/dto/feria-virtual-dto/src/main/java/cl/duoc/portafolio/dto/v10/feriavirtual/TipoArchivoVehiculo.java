//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.06.21 a las 04:47:40 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para TipoArchivoVehiculo.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <pre>
 * &lt;simpleType name="TipoArchivoVehiculo"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="INSCRIPCION"/&gt;
 *     &lt;enumeration value="PERMISO"/&gt;
 *     &lt;enumeration value="REV_TECNICA"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TipoArchivoVehiculo")
@XmlEnum
public enum TipoArchivoVehiculo {

    INSCRIPCION,
    PERMISO,
    REV_TECNICA;

    public String value() {
        return name();
    }

    public static TipoArchivoVehiculo fromValue(String v) {
        return valueOf(v);
    }

}
