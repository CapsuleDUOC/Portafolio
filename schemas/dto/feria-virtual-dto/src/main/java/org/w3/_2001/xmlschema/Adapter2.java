//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.07.06 a las 12:13:46 AM CLT 
//


package org.w3._2001.xmlschema;

import java.time.LocalDate;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter2
    extends XmlAdapter<String, LocalDate>
{


    public LocalDate unmarshal(String value) {
        return (cl.duoc.portafolio.dto.XMLGregorianCalendarAdapter.unmarshalDate(value));
    }

    public String marshal(LocalDate value) {
        return (cl.duoc.portafolio.dto.XMLGregorianCalendarAdapter.marshalDate(value));
    }

}
