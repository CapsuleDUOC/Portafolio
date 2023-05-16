package cl.duoc.portafolio.dto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

@SuppressWarnings("restriction")
public class JAXBUtil {

	public static void validarSchema(final byte[] xml) {
		try {
			
			final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			final Schema schema = schemaFactory.newSchema(new Source[] {
					  new StreamSource(JAXBUtil.class.getResourceAsStream("/WEB-INF/xsd/facturacion/Facturacion_v10.xsd")),
					  new StreamSource(JAXBUtil.class.getResourceAsStream("/WEB-INF/xsd/facturacion/Partner_v10.xsd")),
					  new StreamSource(JAXBUtil.class.getResourceAsStream("/WEB-INF/xsd/facturacion/Contrato_v10.xsd")),
					  new StreamSource(JAXBUtil.class.getResourceAsStream("/WEB-INF/xsd/facturacion/Tarea_v10.xsd"))
					});

		    final Validator validator = schema.newValidator();
		    final Source source = new StreamSource(new ByteArrayInputStream(xml));
		    validator.validate(source);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Si schema no corresponde libera un RunTimeException indicando el motivo.
	 * 
	 * @param klass
	 * @param dto
	 */
	public static void validarSchema(final Class<?> klass, final Object dto) {
		
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final JAXBContext jc = JAXBContext.newInstance(new Class[] { klass });
			final Marshaller ma = jc.createMarshaller();

			ma.marshal(dto, baos);

			validarSchema(baos.toByteArray());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}