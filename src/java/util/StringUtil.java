package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class StringUtil {

    /**
     * �ж��ַ�����Ϊ��
     * @param str
     * @return
     */
	public static boolean checkStr(String str) {
		boolean bool = true;
		if (str == null || "".equals(str.trim()))
			bool = false;
		return bool;
	}

	/**
	 * �ж϶���Ϊ��
	 * @param obj
	 * @return
	 */
	public static boolean checkObj(Object obj) {
		boolean bool = true;
		//if (obj == null || "".equals(obj.toString().trim())) //�������ת�ַ�����Ч�ʵͣ���Ӱ������
		if (obj == null || ( obj instanceof String  &&  "".equals(obj.toString().trim())) )
			bool = false;
		return bool;
	}

	/**
	 * ���ض���Ϊ�յ�toString����
	 * ʹ�ó�����
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {
		return obj != null ? obj.toString().trim() : "";
	}
	
	/**
	 * ����ת��ֵ
	 * String s = "20"; toInteger(s); // 20;
	 * Man man = new Man();
	 * man.toString(); // "15"
	 * toInteger(man); // 15
	 * @param obj
	 * @return
	 */
	public static Integer toInteger(Object obj) {
		return obj != null ? Integer.parseInt(obj.toString()) : 0;
	}

	/**
	 * �ַ���ת��ֵ������ַ���Ϊ�գ��򷵻�-1��
	 * String s = "";
	 * toInt(s); // -1
	 * @param str
	 * @return
	 */
	public static int toInt(String str) {
		return "".equals(str) ? -1 : Integer.parseInt(str);
	}

	/**
	 * �ַ��������ISO8859_1ת��GBK
	 * @param str
	 * @return
	 */
	public static String getISOToGBK(String str) {
		String strName = "";
		try {
			if (str != null) {
				strName = new String(str.getBytes("ISO8859_1"), "GBK");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strName;
	}

	/**
	 * �ַ��������ISO8859_1ת��UTF8
	 * @param str
	 * @return
	 */
	public static String getISOToUTF8(String str) {
		String strName = "";
		try {
			if (str != null) {
				strName = new String(str.getBytes("ISO8859_1"), "UTF8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strName;
	}

	/**
	 * ���ص�ǰʱ���yyyy-MM-dd��ʽ�ַ���
	 * @return str
	 */
	public static String getNowDate() {
		Date nowDate = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowDate);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String str = formatter.format(now.getTime());
		// str=getNextDate(str,-1);
		return str;
	}

	/**
	 * ��ǰʱ���ȥ 30 ��
	 * @return
	 */
	public static String getNowTimeLittleDate2() {
		Calendar c = Calendar.getInstance();
		c.add(c.DATE, -30);
		String time = "" + c.get(c.YEAR) + "-" + (c.get(c.MONTH) + 1) + "-"
				+ c.get(c.DATE) + " " + c.get(c.HOUR_OF_DAY) + ":"
				+ c.get(c.MINUTE) + ":" + c.get(c.SECOND);
		String returnstr = "";
		try {
			Date d = StringUtil.parses(time, "yyyy-MM-dd HH:mm:ss");
			// System.out.println(SimpleDateFormat(d,"yyyy-MM-dd HH:mm:ss"));
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd 00:00:00");
			returnstr = formatter.format(d);
		} catch (Exception e) {

		}
		return returnstr;
	}

	/**
	 * ���ص�ǰʱ���yyyy-MM-dd HH:mm:ss��ʽ�ַ���
	 * @return str
	 */
	public static String getNowTime() {
		Date nowDate = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowDate);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = formatter.format(now.getTime());
		return str;
	}

	/**
	 * ��������ʱ��ĺ����ֵ
	 * @param sDate
	 * @param eDate
	 * @return long
	 */
	public static long getTimeInMillis(String sDate, String eDate) {
		Timestamp sd = Timestamp.valueOf(sDate);
		Timestamp ed = Timestamp.valueOf(eDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sd);
		long timethis = calendar.getTimeInMillis();

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(ed);
		long timeend = calendar2.getTimeInMillis();
		long thedaymillis = timeend - timethis;
		return thedaymillis;
	}

	/**
	 * ��������ʱ���ַ�����ʽ��Ϊyyyy-MM-dd HH:mm��ʽ���ַ���
	 * @param dTime
	 * @return str
	 */
	public static String formatDateTime(String dTime) {
		String dateTime = "";
		if (dTime != null && !"".equals(dTime)
				&& !dTime.startsWith("1900-01-01")) {
			Timestamp t = Timestamp.valueOf(dTime);
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			dateTime = formatter.format(t);
		}
		return dateTime;
	}

	/**
	 * ��������ʱ���ַ�����ʽ��ΪHH:mm:ss��ʽ���ַ���
	 * @param dTime
	 * @return str
	 */
	public static String formatTime(String dTime) {
		String dateTime = "";
		if (dTime != null && !"".equals(dTime)) {
			Timestamp t = Timestamp.valueOf(dTime);
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			dateTime = formatter.format(t);
		}
		return dateTime;
	}

	/**
	 * ��������ʽ�����ַ���Ϊʱ�����
	 * @param strDate
	 * @param pattern
	 * @return Date
	 * @throws ParseException
	 */
	public static Date parses(String strDate, String pattern)
			throws ParseException {
		return new SimpleDateFormat(pattern).parse(strDate);
	}

	/**
	 * ��ǰ�����ǵڼ���
	 * @return str
	 */
	public static String getWeekOfYear() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		String week = calendar.get(Calendar.WEEK_OF_YEAR) + "";
		return week;
	}

	/**
	 * ��ǰʱ���ȥһ�� ����yyyy-MM-dd HH:mm:ss��ʽ�ַ���
	 * @return str
	 */
	public static String getNowTimeLittle() {
		Calendar c = Calendar.getInstance();
		c.add(c.YEAR, +1);
		String time = "" + c.get(c.YEAR) + "-" + (c.get(c.MONTH) + 1) + "-"
				+ c.get(c.DATE) + " " + c.get(c.HOUR_OF_DAY) + ":"
				+ c.get(c.MINUTE) + ":" + c.get(c.SECOND);
		String returnstr = "";
		try {
			Date d = StringUtil.parses(time, "yyyy-MM-dd HH:mm:ss");
			// System.out.println(SimpleDateFormat(d,"yyyy-MM-dd HH:mm:ss"));
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			returnstr = formatter.format(d);
		} catch (Exception e) {
		}
		return returnstr;
	}

	/**
	 * ��ǰʱ���ȥһ��,����yyyy-MM-dd 00:00:00��ʽ�ַ���
	 * @return str
	 */
	public static String getNowTimeLittleDate() {
		Calendar c = Calendar.getInstance();
		c.add(c.DATE, +1);
		String time = "" + c.get(c.YEAR) + "-" + (c.get(c.MONTH) + 1) + "-"
				+ c.get(c.DATE) + " " + c.get(c.HOUR_OF_DAY) + ":"
				+ c.get(c.MINUTE) + ":" + c.get(c.SECOND);
		String returnstr = "";
		try {
			Date d = StringUtil.parses(time, "yyyy-MM-dd HH:mm:ss");
			// System.out.println(SimpleDateFormat(d,"yyyy-MM-dd HH:mm:ss"));
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd 00:00:00");
			returnstr = formatter.format(d);
		} catch (Exception e) {

		}
		return returnstr;
	}

	/**
	 * ���ݲ�����ȡ���ֵ����λ��
	 * @param num
	 * @return str
	 */
	public static String getRandom(int num) {
		return (Math.random() + "").substring(2, num + 2);
	}

	/**
	 * ��������ʱ��Ĳ�ֵ,С�����ú�������λ
	 * @param sDate
	 * @param eDate
	 * @return str
	 */
	public static String getTimeInMillis(Date sDate, Date eDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sDate);
		long timethis = calendar.getTimeInMillis();

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(eDate);
		long timeend = calendar2.getTimeInMillis();
		long thedaymillis = timeend - timethis;
		return thedaymillis < 1000 ? thedaymillis + "����!"
				: (thedaymillis / 1000) + "����!";
	}

	public static String showTrace() {
		StackTraceElement[] ste = new Throwable().getStackTrace();
		StringBuffer CallStack = new StringBuffer();

		for (int i = 1; i < ste.length; i++) {
			CallStack.append(ste[i].toString() + "\n");
			if (i > 4)
				break;
		}
		return CallStack.toString();
	}

	public static String checkTableDefKey(String[] key, String[] value,
			String name) {
		String str = "";
		for (int i = 0; i < key.length; i++) {
			if (name.equals(key[i])) {
				str = value[i];
				break;
			}
		}
		return str;
	}

	/**
	 * �ж��ַ����Ƿ�����
	 * @param str
	 * @return
	 */
	public static boolean isChinese(String str) {
		String regEx = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.find();
	}

	public static String getStrToGbk(String str) {
		String strName = "";
		try {
			if (str != null) {
				strName = new String(str.getBytes("UTF-8"), "GBK");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strName;
	}

	public static String getNextDate(String ts, int i) {
		Calendar now = Calendar.getInstance();
		Timestamp t = Timestamp.valueOf(ts + " 00:00:00.000");
		now.setTime(t);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		now.add(Calendar.DAY_OF_MONTH, +(i));
		String dt = formatter.format(now.getTime());
		return dt;
	}
	
	/**
	 * ��ʱ��ķ��Ӳ��ֽ��мӼ�����������yyyy-MM-dd HH:mm:ss��ʽ�ַ���
	 * @param ts
	 * @param i
	 * @return
	 */
	public static String getNextTime(String ts, int i) {
		Calendar now = Calendar.getInstance();
		Timestamp t = Timestamp.valueOf(ts);
		now.setTime(t);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		now.add(Calendar.MINUTE, +(i));
		String dt = formatter.format(now.getTime());
		return dt;
	}

	/**
	 * ��ʱ����·ݲ��ֽ��мӼ�����������yyyy-MM-dd HH:mm:ss��ʽ�ַ���
	 * @param ts
	 * @param i
	 * @return
	 */
	public static String getNextMonth(String ts, int i) {
		Calendar now = Calendar.getInstance();
		Timestamp t = Timestamp.valueOf(ts);
		now.setTime(t);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		now.add(Calendar.MONTH, +(i));
		String dt = formatter.format(now.getTime());
		return dt;
	}

	/**
	 * ȡUnixʱ���
	 * @param dateTime
	 * @return
	 */
	public static long getUnixTime(String dateTime) {
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
			date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse("1970-01-01 08:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long l = (date1.getTime() - date2.getTime()) / 1000;
		return l;
	}

	/**
	 * �ַ�������ĸ��д
	 * @param str
	 * @return
	 */
	public static String toFirstUpperCase(String str) {
		if (str == null || "".equals(str.trim()))
			return "";
		String firstChar = str.substring(0, 1).toUpperCase();
		String lastStr = str.substring(1);
		return firstChar + lastStr;
	}

	/**
	 * �ж�һ���ַ����ǲ�������;
	 * ������-��+�����ֻ�.��Ϊ��ͷ�����������ֻ�С�����β����������null,���ַ������ֻ��.�� ;
	 * ��֮����Double.parseDoubleת�������쳣����;
	 * �� -1.��.1��1.��-.123��1.1��-12.12�� ���᷵��true ��
	 *    .1.��1 123��1-123��..1��1..��..��.�� ���᷵��false��
	 * @param str �ַ���
	 * @return ����ֵ����true,����false
	 */
	public static boolean isNum(String str) {
		boolean flg = false;
		
		if(str!=null && (str = str.trim()).length() >0){
			String dbl = "((^(\\-|\\+)?([0-9]+)?(\\.)?)([0-9]+$))|((^(\\-|\\+)?([0-9]+)?)[0-9]+([0-9\\.]$))";
			flg = Pattern.compile(dbl).matcher(str).matches();
		}
		
		return flg;
	}

	/**
	 * �ж�һ���ַ����ǲ�������
	 * @param str �ַ���
	 * @return ����ֵ����true,����false
	 */
	public static boolean isInteger(String str){
		str = str.trim();
		String integer = "[\\-\\+]?[0-9]+$";
		boolean flg = Pattern.compile(integer).matcher(str).matches();
		return flg;
	}
	
	/**
	 * ȥ���ַ��������е��ظ�ֵ
	 * @param stringArray �ַ�������
	 * @return ȥ�ظ����ַ�������
	 */
	public static String[] filterRepeat(String[] stringArray) {
		ArrayList arrayList = new ArrayList();
		for (String str : stringArray) {
			if (!arrayList.contains(str)) {
				arrayList.add(str);
			}
		}
		return (String[]) arrayList.toArray(new String[] {});
	}

	/**
	 * �õ�ID��in�־� ��e.id in (1,3,4)
	 * 
	 * @param ids
	 *            ��"1,2,3,"��
	 * @param alias
	 *            ��"e.id"��
	 * @return
	 */
	public static String getIn300Ids(String ids, String alias) {
		String tempS[] = ids.split(",");
		int len = tempS.length;
		int which = 0;
		boolean isAnd = alias.indexOf("not") > 0;
		StringBuffer idsStr = new StringBuffer();
		idsStr.append("(");
		if (len > 300) {
			if (len % 300 > 0) {
				which = len / 300 + 1;
			} else {
				which = len / 300;
			}
			for (int i = 0; i < which; i++) {
				idsStr.append(alias + " in (");
				for (int j = 300 * i; j < 300 * i + 300; j++) {
					if (j < len) {
						idsStr.append(tempS[j] + ",");
					} else {
						break;
					}
				}
				idsStr = idsStr.replace(idsStr.lastIndexOf(","), idsStr
						.length(), "");
				if (i < which - 1) {
					if (isAnd) {
						idsStr.append(") and ");
					} else {
						idsStr.append(") or ");
					}
				} else {
					idsStr.append(")");
				}
			}
		} else {
			idsStr.append(alias + " in (");
			if (ids.lastIndexOf(",") == ids.length() - 1) {
				idsStr.append(ids.substring(0, ids.length() - 1));
			} else {
				idsStr.append(ids);
			}
			idsStr.append(" )");
		}
		idsStr.append(")");
		return idsStr.toString();
	}

	/**
	 * ��ʽ�ַ��� ��ʽ��a,b,v====>'a','b','v'
	 * 
	 * @param str
	 * @return
	 */
	public static String getFormatString(String str) {
		return "'" + str.replaceAll(",", "','") + "'";
	}

	public static String strRound(double value, int decimalPlaces) {
		// ����һ����
		String rval;

		// ���С��λ��0
		if (decimalPlaces == 0) {
			rval = String.valueOf(Math.round(value));
			return rval;
		}

		// �Ƚ�����ֵתΪString��,���ҵ�С��������λ��
		DecimalFormat dformat = new DecimalFormat("0.0000000");
		String str = dformat.format(value);
		int point = str.indexOf(".");
		// �ֱ�õ�С����֮ǰ���ַ���С����֮����ַ�
		String beforePoint = str.substring(0, point);
		String afterPoint = str.substring(point + 1);

		// ���С��λ������Ҫ���С��λ��
		if (afterPoint.length() == decimalPlaces) {
			rval = String.valueOf(value);
		} else if (afterPoint.length() < decimalPlaces) {
			// ���С��λ������Ҫ���С��λ��,���ں��油��
			StringBuffer sb = new StringBuffer(afterPoint);
			for (int i = 0; i < decimalPlaces - afterPoint.length(); i++) {
				sb.append("0");
			}
			// ����
			sb.insert(0, ".").insert(0, beforePoint);
			rval = sb.toString();
		} else {
			// ���С��λ������Ҫ���С��λ��,��Ҫ��������

			// ��������,����
			StringBuffer sb = null;
			sb = new StringBuffer(beforePoint);
			sb.append(".").append(afterPoint.substring(0, decimalPlaces));
			String val = sb.toString();
			// �õ�Ҫ�������λ��
			int temp = Integer.valueOf(afterPoint.charAt(decimalPlaces) + "");
			// ���Ҫ���λ�õ���<4
			if (temp < 4) {
				rval = val;
			} else {
				// ���Ҫ�����λ��>4

				// �����Ҫ�ӵ�o.XX1,���ڶ���2.588,����2λС��,��Ҫ��2.5�Ļ����ϼ�0.01
				sb = new StringBuffer("0.1");
				for (int i = 1; i < decimalPlaces; i++) {
					sb.insert(2, "0");
				}

				// ���Ѿ����������¼��ϸò����0.XX1
				double dbl = Double.valueOf(val)
						+ Double.valueOf(sb.toString());
				val = String.valueOf(dbl);
				// ��ʱ�������ܱ��1,��0.99,����1λС��,��������ͱ��1��,����Ҫ���ж��Ƿ�С��λ��λ��
				// ���û��С��λ,����С��λ
				if (val.indexOf(".") == -1) {
					val += ".";
					for (int i = 0; i < decimalPlaces; i++) {
						val += "0";
					}

					rval = val;
				} else {
					// �����С��λ,��������������С��λ
					val = val.substring(val.indexOf(".") + 1);
					sb = new StringBuffer(dbl + "");
					for (int i = 0; i < decimalPlaces - val.length(); i++) {
						sb.append("0");
					}
					rval = sb.toString();
				}
			}
		}

		// ��Ϊdouble�Ͳ���ȷ,��������ٽ�һ��
		point = rval.indexOf(".");
		return rval.substring(0, point + decimalPlaces + 1);

	}

	/**
	 * ɾ��input�ַ����е�html��ʽ
	 * 
	 * @param input
	 * @return
	 */
	public static String splitAndFilterString(String input) {
		if (input == null || input.trim().equals("")) {
			return "";
		}
		// ȥ������htmlԪ��,
		String str = input.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(
				"<[^>]*>", "");
		// String str = input.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>",
		// "").replaceAll("</[a-zA-Z]+[1-9]?>", "");
		str = str.replaceAll("[(/>)<]", "");
		return str;
	}

	/**
	 * �������ݿ��������͵ı��
	 * @param typeName
	 * @param colScale
	 * @return
	 */
	public static int getColType(String typeName, String colScale) {
		int type = 0;
		if ("varchar".equalsIgnoreCase(typeName)
				|| "varchar2".equalsIgnoreCase(typeName)
				|| "text".equalsIgnoreCase(typeName)) {
			type = 1;
		} else if ("char".equalsIgnoreCase(typeName)) {
			type = 2;
		} else if ("datetime".equalsIgnoreCase(typeName)) {
			type = 6;
		} else if ("date".equalsIgnoreCase(typeName)) {
			type = 7;
		} else if ("time".equalsIgnoreCase(typeName)) {
			type = 8;
		}

		if (Integer.parseInt(colScale) > 0) {
			type = 5;
		} else {
			if ("numeric".equalsIgnoreCase(typeName)
					|| "long".equalsIgnoreCase(typeName)) {
				type = 3;
			} else if ("smallint".equalsIgnoreCase(typeName)
					|| "int".equalsIgnoreCase(typeName)
					|| "integer".equalsIgnoreCase(typeName)) {
				type = 4;
			}
		}
		return type;
	}

	public static String getDBDefault(int type, String def) {
		String deft = "";
		if (def != null && !"".equals(def)) {
			if (type == 1 || type == 2 || type == 6 || type == 7 || type == 8)
				deft = "default " + def;
			else
				deft = "default '" + def + "'";
		}
		return deft;
	}

	/**
	 * ��ȡ���ݿ���������
	 * @param type
	 * @param len
	 * @param flt
	 * @return
	 */
	public static String getDBColType(int type, int len, int flt) {
		String typeName = "";
		if (type == 1) {
			typeName = "varchar(" + len + ")";
		} else if (type == 2) {
			typeName = "char(" + len + ")";
		} else if (type == 3) {
			typeName = "numeric(" + len + ")";
		} else if (type == 4) {
			typeName = "int";
		} else if (type == 5) {
			typeName = "numeric(" + len + "," + flt + ")";
		} else if (type == 6) {
			typeName = "datetime";
		} else if (type == 7) {
			typeName = "date";
		} else if (type == 8) {
			typeName = "time";
		}
		return typeName;
	}

	/**
	 * ��ȡjava��������
	 * @param type
	 * @return
	 */
	public static String getStrColType(int type) {
		String typeName = "";
		if (type == 1) {
			typeName = "String";
		} else if (type == 2) {
			typeName = "Char";
		} else if (type == 3) {
			typeName = "Long";
		} else if (type == 4) {
			typeName = "Integer";
		} else if (type == 5) {
			typeName = "Double";
		} else if (type == 6) {
			typeName = "Date";
		} else if (type == 7) {
			typeName = "Date";
		} else if (type == 8) {
			typeName = "Date";
		}
		return typeName;
	}

	/**
	 * ��ȡhtc�б������ֶ���������
	 * @param type
	 * @return
	 */
	public static String getGridColType(int type) {
		String typeName = "";
		if (type == 1) {
			typeName = "String";
		} else if (type == 2) {
			typeName = "String";
		} else if (type == 3) {
			typeName = "Number";
		} else if (type == 4) {
			typeName = "Number";
		} else if (type == 5) {
			typeName = "Number";
		} else if (type == 6) {
			typeName = "Date";
		} else if (type == 7) {
			typeName = "Date";
		} else if (type == 8) {
			typeName = "Date";
		}
		return typeName;
	}

	/**
	 * ��ȡ�ַ��������ĸ���
	 * @author tanjianwen
	 * @return ���ĸ���
	 */
	public static int getChineseCount(String str) {
		return str.getBytes().length - str.length();
	}

	/**
	 * ��ȡ�������ַ����ķ���(����ռ2���ַ�)
	 * @author tanjianwen
	 * @param �ַ���
	 * @param ��ȡ��ʼλ��
	 * @param ��ȡ����
	 * @return ����ַ���
	 */
	public static String getSubString(String str, int pstart, int pend) {
		if(str == null || str.length()==0)return "";
		String resu = "";
		int beg = 0;
		int end = 0;
		int count1 = 0;
		char[] temp = new char[str.length()];
		str.getChars(0, str.length(), temp, 0);
		boolean[] bol = new boolean[str.length()];
		for (int i = 0; i < temp.length; i++) {
			bol[i] = false;
			if ((int) temp[i] > 255) {// ˵��������
				count1++;
				bol[i] = true;
			}
		}

		if (pstart > str.length() + count1) {
			resu = null;
		}
		if (pstart > pend) {
			resu = null;
		}
		if (pstart < 1) {
			beg = 0;
		} else {
			beg = pstart - 1;
		}
		if (pend > str.length() + count1) {
			end = str.length() + count1;
		} else {
			end = pend;// ��substring��ĩβһ��
		}
		// ���濪ʼ��Ӧ�÷��ص��ַ���
		if (resu != null) {
			if (beg == end) {
				int count = 0;
				if (beg == 0) {
					if (bol[0] == true)
						resu = null;
					else
						resu = new String(temp, 0, 1);
				} else {
					int len = beg;// zheli
					for (int y = 0; y < len; y++) {// ��ʾ��ǰ���Ƿ�������,�����Լ�
						if (bol[y] == true)
							count++;
						len--;// ������Ϊʲôlen--
					}
					// forѭ��������Ϻ�len��ֵ�ʹ����������ַ����У�Ŀ��beg����һ�ַ�������ֵ
					if (count == 0) {// ˵��ǰ��û������
						if ((int) temp[beg] > 255)// ˵���Լ�������
							resu = null;// ���ؿ�
						else
							resu = new String(temp, beg, 1);
					} else {// ǰ�������ģ���ôһ������Ӧ��2���ַ����
						if ((int) temp[len + 1] > 255)// ˵���Լ�������
							resu = null;// ���ؿ�
						else
							resu = new String(temp, len + 1, 1);
					}
				}
			} else {// ��������������µıȽ�
				int temSt = beg;
				int temEd = end - 1;// �������һ
				for (int i = 0; i < temSt; i++) {
					if (bol[i] == true)
						temSt--;
				}// ѭ����Ϻ�temSt��ʾǰ�ַ�����������
				for (int j = 0; j < temEd; j++) {
					if (bol[j] == true)
						temEd--;
				}// ѭ����Ϻ�temEd-1��ʾ����ַ�����������
				if (bol[temSt] == true)// ˵�����ַ���˵�����������Ǻ��ֵĺ�벿�֣���ôӦ���ǲ���ȡ��
				{
					int cont = 0;
					for (int i = 0; i <= temSt; i++) {
						cont++;
						if (bol[i] == true)
							cont++;
					}
					if (pstart == cont)// ��ż����Ӧ����,���pstart<cont��Ҫ����
						temSt++;// ����һλ��ʼ
				}
				if (bol[temEd] == true) {// ��ΪtemEd��ʾsubstring
											// ������������˴���һ�����֣�����Ҫȷ���Ƿ�Ӧ�ú��������
					int cont = 0;
					for (int i = 0; i <= temEd; i++) {
						cont++;
						if (bol[i] == true)
							cont++;
					}
					if (pend < cont)// �Ǻ��ֵ�ǰ�벿�ֲ�Ӧ����
						temEd--;// ����ֻȡ��ǰһ��
				}
				if (temSt == temEd) {
					resu = new String(temp, temSt, 1);
				} else if (temSt > temEd) {
					resu = null;
				} else {
					resu = str.substring(temSt, temEd + 1);
				}
			}
		}
		return resu;// ���ؽ��
	}
	
	/**
	 * �ж�����IP�Ƿ������ip������
	 * @param requestIP ��ǰIP
	 * @param ips һ��IP
	 * @return true����;false������
	 */
	public static boolean checkIP(String requestIP, String[] ips) {
		for (String ip : ips) {
			if (requestIP.equals(ip)) return true;
			if ((ip.indexOf("-")) >= 0) {//x.x.x.x-x.x.x.x
				String[] ipPart = requestIP.split("\\.");
				String[] ipOpenPart = ip.split("\\-")[0].split("\\.");
				String[] ipEndPart = ip.split("\\-")[1].split("\\.");
				if (ipOpenPart.length != ipPart.length || ipEndPart.length != ipPart.length) continue;
				boolean flag = true;
				for (int i = 0; i < ipPart.length; i++) {
					if (ipPart[i].compareTo(ipOpenPart[i]) < 0 || ipPart[i].compareTo(ipEndPart[i]) > 0) {
						flag = false;
					} 
				}
				if (flag) return true;
			}
		}
		return false;
	}
	
	/**
	 *  �������ͻ��˵�IP
	 * @param request �������
	 * @return IP�ַ���
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	/**
	 * Ч���ַ����Ƿ�����������ʽ��ʽ
	 * @param value ֵ
	 * @param regexp ����ʽ
	 * @return �Ƿ����Ҫ��
	 */
	public static boolean checkRegexp(String value, String regexp) {
		return Pattern.compile(regexp).matcher(value).matches();
	}

	/**
	 * ת�������ͣ�ת����ֵ��������SQL �������
	 * @param fieldType 
	 *  1||NUMBER:������(Double||BigDecimal);
	 *  2||DIGIT: ����(Integer||Long||BigDecimal);
	 *  4||DATE: ����,����ʱ����(Timestamp);
	 *  7||DATETIME�����ڣ�����ʱ����(Timestamp);
	 *  5||STRING���ı���(String);
	 * @param value ��ת����ֵ
	 * @return ת���õ�ֵ
	 * */ 
	public static Object parseObj(String fieldType, String value){
		return parseObj(fieldType,value,null,null);
	}
	
	/**
	 * ת�������ͣ�ת����ֵ��������SQL �������
	 * @param fieldType 
	 *  1||NUMBER:������(Double||BigDecimal);
	 *  2||DIGIT: ����(Integer||Long||BigDecimal);
	 *  4||DATE: Ĭ�Ϸ���������,���format,flg����ʹ��,���format,flg��Ϊ�գ��򷵻�һ������ʱ���������(Timestamp);
	 *  7||DATETIME�����ڣ�����ʱ����(Timestamp);
	 *  8||TIMESTAMP���Զ���������ʱ���룬��format��������(Timestamp);
	 *  3||5||6||STRING���ı���(String);
	 * @param value ��ת����ֵ
	 * @param format ����ת����ʽ
	 * @param flg �Ƿ���value��׷��" 23:59:59"�����fieldType=4ʹ��
	 *        ���:flg!=false������valueֵ��� 23:59:59��Ȼ��format��ʽ����ת����formatΪ�գ���Ĭ��yyyy-MM-dd HH:mm:ss
	 *            ʹ�ó�����ʱ�������ѯ������ʱ����û��ʱ���룬����һ�����һ��Ϊ׼
	 *        ����:ֱ�Ӱ�format��ʽ����ת��,formatΪ�գ���Ĭ��yyyy-MM-dd
	 * @return ת���õ�ֵ
	 * */ 
	public static Object parseObj(String fieldType, String value, String format, Boolean flg){
		Object obj = null;
		try{
			int ftype = getFiledType(fieldType);
			
			if (StringUtil.checkStr(value)){
				switch (ftype) {
				case 2:
					obj = (value.length()>=18)? new BigDecimal(value) : toDigit(value);
					break;// ����
				case 1:
					obj = (value.length()>10)? new BigDecimal(value) : Double.valueOf(value);
					break;// С��
				case 4:
					obj = getSqlDate(value, format, flg);
					break;// ����
				case 7:
					obj = getSqlDateTime(value, format);
					break;// ����ʱ��
				case 8:
					obj = getSqlDateTime(value, format);
					break;// ʱ�䣬�Ժ���չ�õ�
				default:
					obj = value;
				}
			}
		}catch(Exception ex){
			obj = value;
		}
		return obj;
	}
	
	/**
	 * ��������ö���ı�תö��ֵ
	 * @param fieldType ��������
	 * @return ����ֵ
	 */
	private static int getFiledType(String fieldType){
		String type;
		if(isInteger(fieldType)){
			type = fieldType;
		}else if("NUMBER".equals(fieldType)){
			type = "1";
		}else if("DIGIT".equals(fieldType)){
			type = "2";
		}else if("DATE".equals(fieldType)){
			type = "4";
		}else if("DATETIME".equals(fieldType)){
			type = "7";
		}else if("TIMESTAMP".equals(fieldType)){
			type = "8";
		}else{
			type = "5";
		}
		return  toInt(type);
	}
	
	/**
	 * ת����,����10λת��Long,����Integer
	 * @param value ��ת����ֵ
	 * @return ����
	 */
	private static Object toDigit(String value){
		return (value.length()>=10) ? Long.valueOf(value) : StringUtil.toInt(value);
	}
	
	/**
	 * ��������
	 * @param value ��ת����ֵ
	 * @param format ����ת����ʽ
	 * @param flg �Ƿ���value��׷��" 23:59:59" 
	 * @return ����ʱ�����
	 * @throws Exception
	 */
	private static Timestamp getSqlDate(String value, String format, Boolean flg) throws Exception {
		String str;
		if(flg != null && flg == false){
		  str = StringUtil.checkStr(format) ? format : "yyyy-MM-dd";
		}else {
		  value += " 23:59:59";
		  str = StringUtil.checkStr(format) ? format : "yyyy-MM-dd HH:mm:ss";
		}
		return getSqlDateTime(value, str);
	}
	
	private static Timestamp getSqlDateTime(String value) throws Exception {
		return getSqlDateTime(value, null);
	}

	/**
	 * ����ʽת�����ڶ���
	 * @param value ��ת����ֵ
	 * @param format ����ת����ʽ
	 * @return ����ʱ�����
	 * @throws Exception
	 */
	private static Timestamp getSqlDateTime(String value,String format) throws Exception {
		String str = StringUtil.checkStr(format) ? format : "yyyy-MM-dd HH:mm:ss";
		Timestamp timestamp = null;
		try{
			str = StringUtil.checkStr(format) ? format : "yyyy-MM-dd HH:mm:ss";
			timestamp = new Timestamp(StringUtil.parses(value,str).getTime());
		}catch(Exception e){
			str = "yyyy-MM-dd";
			timestamp = new Timestamp(StringUtil.parses(value,str).getTime());
		}
		return timestamp;
	}
	
	/**
	 * ��ȡIP��ַ
	 * @return IP��ַ
	 */
	public static String getIPAddress(){
		String ip = null;
		try {
			String os = System.getProperty("os.name");
			ip = os.startsWith("Windows") ? getIPOnWindows() : getIPOnLinux();
		} catch (Exception e) {
		}
		
		return ip;
	}
	
	/**
     * Windows�»�ȡ����IP��ַ
     * @return IP��ַ
     */
    private static String getIPOnWindows() throws Exception{
        String ip = "";
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("ipconfig");
            bufferedReader = new BufferedReader(new InputStreamReader(process
                    .getInputStream()));
            String line = null;
            int index = -1;
            boolean isLocal = false;
            while ((line = bufferedReader.readLine()) != null) {
            	//�ҵ�����������Ϣ
            	if(line.toLowerCase().indexOf("ethernet adapter ��������:") >= 0 ||
                		line.indexOf("��̫�������� ��������:") >=0 ||
                		line.indexOf("��̫�������� ��̫��:") >=0){
                    isLocal = true;
                }
            	
            	//�ҵ�IP��Ϣ
            	if(line.toLowerCase().indexOf("ipv4") >= 0 || 
            			line.toLowerCase().indexOf("ip address") >= 0){
            		if(isLocal){
            			index = line.indexOf(":");
            			ip = line.substring(index + 1).trim();
                        break;
                    }
            	}
            }
            //û��ȡ�����������е�IP��Ϣ����ͨ��Java API����ȡ
            if(!StringUtil.checkStr(ip)){
            	InetAddress inet = InetAddress.getLocalHost();
                ip = inet.getHostAddress();
            }
        } catch (IOException e) {
        	throw e;
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
            }
            bufferedReader = null;
            process = null;
        }

        return ip;   
    }
    
    /**
     * Linux�»�ȡ����IP��ַ
     * @return IP��ַ
     */
    private static String getIPOnLinux() throws Exception{
    	String ip = "";
		try {
			Enumeration<?> e1 = (Enumeration<?>) NetworkInterface
					.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				if (!ni.getName().equals("eth0")) {
					continue;
				} else {
					Enumeration<?> e2 = ni.getInetAddresses();
					while (e2.hasMoreElements()) {
						InetAddress ia = (InetAddress) e2.nextElement();
						if (ia instanceof Inet6Address)
							continue;
						ip = ia.getHostAddress();
					}
					break;
				}
			}
		} catch (Exception e) {
		}
		
		return ip; 
    }
    
	/**
	 * ���ַ����е�ȫ���ַ��滻�ɰ��
	 * @param sourceStr �������
	 * @return �滻����ַ���
	 */
	public static String sqlDataRevert(String sourceStr){
		Map invalidMap = (Map)ComminEnumUtil.getEnumMeaning(
				"Defense_SQL_Injection_invalidString", null);
		if( !StringUtil.checkStr(sourceStr) || invalidMap == null){
			return sourceStr;
		}
		
		Set<Entry<String, String>> set = invalidMap.entrySet();
		for (Entry<String, String> entry : set) {
			String lowValue = sourceStr;
			if (!"".equals(entry.getKey()) && !"'".equals(entry.getKey())) {
				String sCheckInvalidChar = entry.getValue().trim(); //�ɵ�
				sCheckInvalidChar += " ";
						
				String sReplaceValidChar = entry.getKey().trim(); //�µ�
				sReplaceValidChar = " " + sReplaceValidChar + " ";
						
				if(!" ".equals(sCheckInvalidChar.substring(
						sCheckInvalidChar.length() - 1))){ //����ַ���ĩβ�ո�
					sCheckInvalidChar += " ";
		        }
				if(!" ".equals(sReplaceValidChar.substring(
						sReplaceValidChar.length() - 1))){//����ַ���ĩβ�ո�
					sReplaceValidChar += " ";
		        }
				int iBeforeLen = sCheckInvalidChar.length() + 1
				- (sCheckInvalidChar + ".").trim().length();
						
				while (sourceStr.indexOf(sCheckInvalidChar) >= 0) {
					sourceStr = sourceStr.substring(0, lowValue.indexOf(sCheckInvalidChar)
							+ iBeforeLen)
							+ sReplaceValidChar.trim()
							+ sourceStr.substring(lowValue.indexOf(sCheckInvalidChar)
									+ iBeforeLen
									+ sCheckInvalidChar.trim().length());
					lowValue = lowValue.substring(0, lowValue.indexOf(sCheckInvalidChar)
							+ iBeforeLen)
							+ sReplaceValidChar.trim()
							+ lowValue.substring(lowValue.indexOf(sCheckInvalidChar)
									+ iBeforeLen
									+ sCheckInvalidChar.trim().length());
					}
				}
			}
		return sourceStr;
	}
	
	/**
	 * ��list�еĺ���ȫ���ַ��������滻�ɰ��
	 * @param list ��ԭ������
	 */
	public static void sqlDataRevert(List list){
		if(list == null || list.size() == 0){
			return;
		}
		int size = list.size();
		for(int i = 0; i < size; i++){
			Map map = (Map) list.get(i);
			Set<Entry<String, String>> set = map.entrySet();
			for (Entry<String, String> entry : set) {
				if ("".equals(entry.getKey())) {
					continue;
				}
				String key = entry.getKey().trim();
				String value = entry.getValue().trim();
				map.put(key, sqlDataRevert(value)); //�滻map�е�ֵ
			}
		}
	}
	
	/**
	 * decode����
	 * @param chars Ҫ��decode������
	 * */
	public static String urlDecode(String chars){
		try{
			if(chars==null){chars="";}
			chars = chars.replace("��", "%"); //SSH3��XSS������������ת��ȫ�Ǻ󣬵���ת������
			chars = URLDecoder.decode(chars,"utf-8");
			try{
				chars = URLDecoder.decode(chars,"utf-8");  //decode 2�Σ���ΪSSH3������Ĵ�����decode����Σ�����Խ����Ӱ��
			}catch(Exception ex){
				//"������������8%.doc" �������������ٽ�һ�λᱨ������Ϊ%.d���� ,��2�ν���������򲻴���
			}
		}catch(Exception ex){
			return "";
		}
		return chars;
	}
	
	/**
	 * Encode����
	 * @param chars Ҫ��Encode������
	 * */
	public static String urlEncode(String chars){
		try{
			if(chars==null){chars="";}
			chars = URLEncoder.encode(chars, "utf-8");  //decode 2�Σ���ΪSSH3������Ĵ�����decode����Σ�����Խ����Ӱ��
		}catch(Exception ex){
			return "";
		}
		return chars;
	}
	
	/**
	 * ��list<map>���ĳ������ֵƴ����
	 * @param list list����
	 * @param key map���keyֵ
	 * @param split �ָ���
	 * @return ��list��ȡ��ÿ��map��key��Ӧ��ֵ �÷ָ���ƴ���������ַ���
	 */
	public static String appendBySplit(List<Map> list, String key, String split) {
		return appendBySplit(list, key, ",", false);
	}
	
	/**
	 * ��list<map>���ĳ������ֵƴ����
	 * @param list list����
	 * @param key map���keyֵ
	 * @param split �ָ���
	 * @param ignoreRepeat �Ƿ�����ظ�����
	 * @return ��list��ȡ��ÿ��map��key��Ӧ��ֵ �÷ָ���ƴ���������ַ���
	 */
	public static String appendBySplit(List list, String key, String split, boolean ignoreRepeat) {
		if (list == null || list.size() < 1) {
			return "";
		}
		StringBuilder str = new StringBuilder();
		int size = list.size();
		Map<String, Object> row = null;
		for (int i = 0; i < size; i++) {
			row = (Map<String, Object>)list.get(i);
			Object value = row.get(key);
			if (!StringUtil.checkObj(value)) {
				continue;
			}
			String strValue = value.toString().trim();
			if ((ignoreRepeat && str.indexOf(strValue) <= -1) || !ignoreRepeat) {
				str.append("," + strValue);		//ע�����������return��ʱ��str.substring(1) ��ȥ����һ������
			}
		}
		
		return str.length()>=1 ? str.substring(1) : str.toString();
	}
	
	public static void main(String[] args) {
		// System.out.println(splitAndFilterString("<html><body>&nbsp;<1><3><img
		// src='test' width='35' height='32'><a
		// href='http://www.163.com'>test</a>test</body></html>"));
		//System.out.println(a.length() + getChineseCount(a));
		/*String a =" ";
		String b = StringUtil.getSubString(a, 0, 980) + "...���ݹ��������ض�!";
		System.out.println(b+StringUtil.getChineseCount(b));*/
		
		/*System.out.println(isNum("-100"));
		System.out.println(isInteger("-100"));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("SURL", "m/a/c");
		map.put("kk", "kkk");
		System.out.println(map.toString());
		System.out.print(JackJson.getBasetJsonData(map));
		
		String a = "1";
		System.out.println(isInteger(a));
		System.out.println(isNum(a));*/
		
/*		String sql = "������� * from testTb���䡡where A.cc = 1 ���� A.dd = 2�������";
		sql = dataRevert(sql);
		System.out.println("sql=" + sql);*/
		
		
		String fileName = "%E4%B8%9C%E8%8E%9E%E7%94%B5%E4%BF%A1%E5%88%B0%E6%B7%B1%E5%9C%B3%E3%80%81%E4%B8%8A%E6%B5%B7%E3%80%81%E5%A4%A9%E6%B4%A5%E7%94%B5%E4%BF%A1%E5%A4%96%E7%BD%91%E4%B8%A2%E5%8C%858%25.doc";
		try {
			 fileName = URLDecoder.decode(fileName.replaceAll("��", "%"),"utf-8");
		     fileName = URLDecoder.decode(fileName ,"utf-8").replace(";", "��");
			 
		} catch (Exception e) {
			//e.printStackTrace();
			//System.out.println(e.getMessage());
		}
		
		System.out.println(fileName);
		
	}

}