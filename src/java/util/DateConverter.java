package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import ognl.DefaultTypeConverter;

public class DateConverter extends DefaultTypeConverter {

	private static final DateFormat[] ACCEPT_DATE_FORMATS = {
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
			new SimpleDateFormat("yyyy-MM-dd"),
			new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"),
			new SimpleDateFormat("yyyyƒÍMM‘¬dd»’"),
			new SimpleDateFormat("yyyy/MM/dd") };

	@Override
	public Object convertValue(Map context, Object value, Class toType) {
		if (toType == Date.class) {
			String dateString = null;
			String[] params = (String[]) value;
			dateString = params[0];
			for (DateFormat format : ACCEPT_DATE_FORMATS) {
				try {
					return format.parse(dateString);
				} catch (Exception e) {
					continue;
				}
			}
			return null;
		} else if (toType == String.class) {
			Date date = (Date) value;
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		}
		return null;
	}
}
