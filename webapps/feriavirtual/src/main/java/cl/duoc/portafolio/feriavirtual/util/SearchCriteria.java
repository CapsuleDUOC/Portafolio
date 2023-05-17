package cl.duoc.portafolio.feriavirtual.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

	private String key;
	private String key2;
	private OPERATION operation;
	private Object value;
	private Object value2;

	public static enum OPERATION {
		like, equal, lessThanOrEqualTo, greaterThanOrEqualTo, in, between, exist;
	}
}
