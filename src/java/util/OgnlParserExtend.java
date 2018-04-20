package util;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 李海谅
 * @version 2013-3-6
 */
public class OgnlParserExtend extends OgnlParser {

	// private volatile OgnlParserExtend instance;

	private OgnlParserExtend(boolean isDefault) {
		super(isDefault);
	}

	/**
	 * 添加了操作：_sd_ ，用于时间的加减，返回秒； 添加了操作：_ct_ ，用于时间的设置，返回加秒数后的时间； 添加了方法：now
	 * ，取当前时间？小时后的时间； 添加了方法：stime ，判断在给定的时间格式中，两个时间是否一样；
	 */
	public static OgnlParserExtend getOgnlParser() {
		OgnlParserExtend obj = new OgnlParserExtend(false);
		try {
			obj.initMethods();
			obj.initOperates();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 参考 excuteOgnlExp
	 */
	public Boolean parseBoolean(String expression) {
		return (Boolean) excuteOgnlExp(expression);
	}

	/**
	 * 参考 excuteOgnlExp
	 */
	public Number parseNumber(String expression) {
		return (Number) excuteOgnlExp(expression);
	}

	/**
	 * 参考 excuteOgnlExp
	 */
	public String parseString(String expression) {
		return (String) excuteOgnlExp(expression);
	}

	/**
	 * 参考 excuteOgnlExp
	 */
	public Date parseDate(String expression) {
		return (Date) excuteOgnlExp(expression);
	}

	/**
	 * 参考 excuteOgnlExp。
	 */
	public Boolean parseBoolean(String expression, Map<String, Object> context) {
		return (Boolean) factory.create(expression).evaluate(context);
	}

	/**
	 * 参考 excuteOgnlExp。
	 */
	public String parseString(String expression, Map<String, Object> context) {
		return (String) factory.create(expression).evaluate(context);
	}

	/**
	 * 参考 excuteOgnlExp。
	 */
	public Number parseNumber(String expression, Map<String, Object> context) {
		return (Number) factory.create(expression).evaluate(context);
	}

	/**
	 * 参考 excuteOgnlExp。
	 */
	public Date parseDate(String expression, Map<String, Object> context) {
		return (Date) factory.create(expression).evaluate(context);
	}

	/**
	 * 根据同一个上下文解释多个表达式，返回结果列表。
	 * @param expressions
	 * @param context
	 * @return
	 */
	public List<Object> parseAllExp(List<String> expressions, Map<String, Object> context) {
		if (expressions != null) {
			ArrayList<Object> rsList = new ArrayList<Object>(expressions.size());
			for (String ex : expressions) {
				rsList.add(excuteOgnlExp(ex, context));
			}
			return rsList;
		} else {
			return null;
		}
	}

	/**
	 * 根据同一个上下文解释多个布尔运算的表达式，返回他们的与集
	 * @param expressions
	 * @param context
	 * @return
	 */
	public Boolean parseAllCondition(List<String> expressions, Map<String, Object> context) {
		if (expressions != null) {
			boolean rs = true;
			for (String ex : expressions) {
				rs &= parseBoolean(ex, context);
			}
			return rs;
		} else {
			return null;
		}
	}

	void initOperates() throws Exception {
		Method secondDiffer = SelfMethods.class.getMethod("secondDiffer", Date.class, Date.class);
		Method changeTime = SelfMethods.class.getMethod("changeTime", Date.class, Object.class);
		super.addOperation("_sd_", secondDiffer);
		super.addOperation("_ct_", changeTime);
	}

	void initMethods() throws Exception {
		Method now = SelfMethods.class.getMethod("now", Double.class);
		super.addMethod("now", now);
		Method isSameTimeFormat = SelfMethods.class.getMethod("isSameTimeFormat", Date.class, Date.class, String.class);
		super.addMethod("stime", isSameTimeFormat);
	}

	public static class SelfMethods {

		/**
		 * 添加了操作：_sd_ ，用于时间的加减，返回秒；
		 * @param date
		 * @param target
		 * @return
		 */
		public static Long secondDiffer(Date date, Date target) {
			if (date != null && target != null) {
				if (target instanceof Date) {
					Date t2 = (Date) target;
					return (date.getTime() - t2.getTime()) / 1000;
				}
			}
			return null;
		}

		/**
		 * 添加了操作：_ct_ ，用于时间的设置，返回加秒数后的时间；
		 * @param date
		 * @param value
		 * @return
		 */
		public static Date changeTime(Date date, Object value) {
			if (date != null && value != null) {
				if (value instanceof Number) {
					date.setTime(date.getTime() + ((Number) value).longValue() * 1000);
				} else if (value instanceof String) {
					try {
						return changeTime(date, Long.parseLong((String) value));
					} catch (Exception e) {
					}
				}
			}
			return date;
		}

		/**
		 * 添加了方法：stime ，判断在给定的时间格式中，两个时间是否一样；
		 */
		public static boolean isSameTimeFormat(Date date, Date target, String format) {
			if (date != null && target != null && format != null) {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				return sdf.format(date).equals(sdf.format(target));
			}
			return false;
		}

		/**
		 * 取当前时间？小时后的时间；
		 * @param time
		 * @return
		 */
		public static Date now(Double time) {
			Date now = new Date();
			if (time != null) {
				time = time * 86400000;
				now.setTime(now.getTime() + time.longValue());
			}
			return now;
		}
	}
}
