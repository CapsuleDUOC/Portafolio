//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.05.21 a las 09:00:27 PM CLT 
//


package org.w3._2001.xmlschema;

import java.time.LocalDateTime;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter1
    extends XmlAdapter<String, LocalDateTime>
{


    public LocalDateTime unmarshal(String value) {
        return (cl.duoc.portafolio.dto.XMLGregorianCalendarAdapter.unmarshalDateTime(value));
    }

    public String marshal(LocalDateTime value) {
        return (cl.duoc.portafolio.dto.XMLGregorianCalendarAdapter.marshalDateTime(value));
    }

}
