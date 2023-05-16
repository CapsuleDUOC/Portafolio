package cl.duoc.portafolio.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XMLGregorianCalendarAdapter {
	
	public static String marshalDateTime(LocalDateTime dateTime) {
		return dateTime.toString();
	}
	
    public static LocalDateTime unmarshalDateTime(String xmlGregorianCalendar) {
        try {
            LocalDateTime result = LocalDateTime.parse(xmlGregorianCalendar);
            return result;
        } catch (DateTimeParseException ex) {
            Logger.getLogger(XMLGregorianCalendarAdapter.class.getName()).log(Level.WARNING, "No puede parsear LocalDateTime: " + xmlGregorianCalendar);
            return null;
        }
    }
	
	public static String marshalDate(LocalDate date) {
		return date.toString();
	}
	
    public static LocalDate unmarshalDate(String xmlGregorianCalendar) {
        try {
            LocalDate result = LocalDate.parse(xmlGregorianCalendar);
            return result;
        } catch (DateTimeParseException ex) {
            Logger.getLogger(XMLGregorianCalendarAdapter.class.getName()).log(Level.WARNING, "No puede parsear LocalDate: " + xmlGregorianCalendar);
            return null;
        }
    }
}