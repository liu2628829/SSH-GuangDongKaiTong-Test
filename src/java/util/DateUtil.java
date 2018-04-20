package util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author BWeiMing
 *  
 */
public class DateUtil {
    public static final int DB_TYPE_SYBASE=1;
    public static final int DB_TYPE_ORACLE=2;    
    public static final int DB_TYPE_MSSQL=3;
    
    public static final String yyMMdd="yy-MM-dd";
    public static final String yyyyMMdd="yyyy-MM-dd";
    public static final String HHmmss="HH:mm:ss";
    public static final String yyyyMMddHHmmss="yyyy-MM-dd HH:mm:ss";
    public static final String yyyyMMddHHmm="yyyy-MM-dd HH:mm";
    public static final String yyMMddHHmmss="yy-MM-dd HH:mm:ss";
    
    //转换orale时间是需要使用的时间格式
    public static final String Oracl_yyMMdd="yy-mm-dd";
    public static final String Oracl_yyyyMMdd="yyyy-mm-dd";
    public static final String Oracl_HHmmss="hh24:mi:ss";
    public static final String Oracl_yyyyMMddHHmmss="yyyy-mm-dd hh24:mi:ss";
    public static final String Oracl_yyyyMMddHHmm="yyyy-mm-dd hh24:mi";
    public static final String Oracl_yyMMddHHmmss="yy-mm-dd hh24:mi:ss";
    
    /**
     * 根据数据库不同，转换时间格式
     * 主要用于数据的时间字段的比较，通常在查询时使用
     * @param sDate
     * @param sFormat
     * @param iDBType
     * 注意：sDate和sFormat的时间格式要匹配
     *   如果sDate的格式是"2009-09-09" ,则sFormat使用"yyyy-mm-dd"
     * @return
     */
    public static String to_DBDate(String sDate, String sFormat,int iDBType) {
       String sValue="";
       switch(iDBType){
         case DB_TYPE_ORACLE:
        	 sValue ="to_date('"+sDate+"','"+sFormat+"')";
        	 break;
         case DB_TYPE_SYBASE:
        	 sValue ="convert(datetime,'"+sDate+"')";
        	 break;
         case DB_TYPE_MSSQL:
        	 sValue ="cast('"+sDate+"' as datetime) ";
        	 break;
         default:
            sValue=sDate;
           break;
       }
       return sValue;
    }

    /**
     * 根据提供的时间格式将字符串解析成日期
     * @param s
     * @param style
     * @return Date
     */
    public static Date parseToDate(String s, String style) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(style);
        Date date = null;
        if(s==null||s.length()<8)
            return null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String parseToString(String s, String style) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(style);
        Date date = null;
        String str=null;
        if(s==null||s.length()<8)
            return null;
        try {
            date = simpleDateFormat.parse(s);
            str=simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
    
    /**
     * 将时间转换成指定的格式的字符串
     * @param date
     * @param style
     * @return
     */
    public static String parseToString(Date date, String style) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(style);
        String str=null;
        if(date==null)
            return null;
        str=simpleDateFormat.format(date);
        return str;
    }
    
    /**
     * 以年月日时分秒格式返回当前时间的字符串
     * @return String
     */
    public static String getNowTime(){
		Date nowDate = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowDate);
	    SimpleDateFormat formatter = new SimpleDateFormat(yyyyMMddHHmmss);
	    String str = formatter.format(now.getTime());
		return str;
	}
    
    /**
     * 以指定格式返回当前时间的字符串
     * @param format 日期格式
     * @return format为空则返回yyyy-MM-dd格式的字符串
     */
    public static String getNowTime(String format){
    	
    	if(!StringUtil.checkObj(format)){
    		format = "yyyy-MM-dd";
    	}
		
		Date nowDate = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowDate);
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String str = formatter.format(now.getTime());
		return str;
    }
    
    /**
     * 以年月日时分格式返回当前时间的字符串
     * @return String
     */
    public static String getNowTime2(){
		Date nowDate = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowDate);
	    SimpleDateFormat formatter = new SimpleDateFormat(yyyyMMddHHmm);
	    String str = formatter.format(now.getTime());
		return str;
	}
    
    /**
     * 以年月日格式返回当前日期的字符串
     * @return String
     */
    public static String getShortNowTime(){
		Date nowDate = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowDate);
	    SimpleDateFormat formatter = new SimpleDateFormat(yyyyMMdd);
	    String str = formatter.format(now.getTime());
		return str;
	}
      
    /**
     * 以年月日格式返回当月第一天这个日期的字符串
     * @return String
     */
    public static String getMonthFirstDay() { 
        Calendar calendar = Calendar.getInstance();    
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));    
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String str = formatter.format((calendar.getTime())); 
        return str;
    }   
    
    /**
     * 获得当前日期与本周日相差的天数 
     * @param day
     * @return
     */   
    private static int getMondayPlus(int day) {    
        Calendar cd = Calendar.getInstance();    
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......    
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK)-day;         //因为所需要的是从上周星期三到本周星期二所为第一天所以这里减3,即星期三作为开始日期    
        if (dayOfWeek == 1) {    
            return 0;    
        } else {    
            return 1 - dayOfWeek;    
        }    
    }   
    
    /**
     * 获得本周星期日的日期      
     * @param week
     * @param day
     * @return
     */
    public static String getCurrentWeekday2(int week,int day) {    //第一个参数据为控制哪一周，第二个参数用来控制哪一天作为开始日期
        int weeks = week ;    
        int mondayPlus = getMondayPlus(day);    
        GregorianCalendar currentDate = new GregorianCalendar();    
        currentDate.add(GregorianCalendar.DATE, mondayPlus+ 7 * weeks);    
        Date monday = currentDate.getTime();    	            
        DateFormat df = DateFormat.getDateInstance();    
     //   String preMonday = df.format(monday) + " 00:00";	
        String preMonday = df.format(monday) + "";	
        return preMonday;    
    }     
    
    /**
     * 获得两个日期间相差的天数
     * @param endtime
     * @param starttime
     * @return
     */
    public static long getsubdates(String endtime,String starttime){			
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); 
		long days=0;
    	try { 
    	    Date d1 = df.parse(endtime); 
    	    Date d2 = df.parse(starttime); 
    	    long diff = d1.getTime() - d2.getTime(); 
    	    days = diff / (1000 * 60 * 60 * 24); 
    	} catch (Exception e) { } 
    	return days;
	}
    /**
     * 对给定时间的月份进行增减操作，返回操作后时间的yyyy-MM-dd HH:mm:ss格式字符串
     * @param ts
     * @param i
     * @return
     */
    public static String getNextMonthDay(String ts, int i){
		Calendar now = Calendar.getInstance();
		Timestamp t = Timestamp.valueOf(ts);
		now.setTime(t);
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    now.add(Calendar.MONTH, i); 
		String dt = formatter.format(now.getTime());
		return dt;
	}
    
    /**
     * 获取当前日期后第N个月后的日期(不包括当天)，返回操作后时间的yyyy-MM-dd HH:mm:ss格式字符串
     * @param ts  yyyy-MM-dd HH:mm:ss
     * @param i  月份
     * @return
     */
    public static String getMonthLastDay(String ts, int i){
		Calendar now = Calendar.getInstance();
		Timestamp t = Timestamp.valueOf(ts);
		now.setTime(t);
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    now.add(Calendar.MONTH, i); 
	    now.add(Calendar.SECOND, -1);
		String dt = formatter.format(now.getTime());
		return dt;
	}
    
    /**
     * 两个时间相差时间
     * @param startTime
     * @param endTime
     * endTime>startTime
     * @return 返回XX天XX小时XX分
     */
    public static String getTwoTimeDayHourMin(String startTime, String endTime){
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date starDate=null; //2004-01-02 11:30:24
        Date endDate=null;//2004-03-26 13:31:40

		try {
			starDate = df.parse(startTime);
			endDate=df.parse(endTime);
		} catch (ParseException e) {			
			e.printStackTrace();
		}       
        long l=endDate.getTime()-starDate.getTime();
        long day=l/(24*60*60*1000);
        long hour=(l/(60*60*1000)-day*24);
        long min=((l/(60*1000))-day*24*60-hour*60);
        //long s=(l/1000-day*24*60*60-hour*60*60-min*60);
        String resultStr="";
        if(day>0){
        	resultStr=resultStr+day+"天";
        }
        if(hour>0){
        	resultStr=resultStr+hour+"小时";
        }
        if(min>0){
        	resultStr=resultStr+min+"分";
        }  
		return resultStr;
    }
    /**
     * 两个时间相差时间
     * @param startTime
     * @param endTime
     * endTime>startTime
     * @return 返回XX天
     */
    public static String getTwoTimeDay(String startTime, String endTime){
    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date starDate=null; //2004-01-02 11:30:24
        Date endDate=null;//2004-03-26 13:31:40

		try {
			starDate = df.parse(startTime);
			endDate=df.parse(endTime);
		} catch (ParseException e) {			
			e.printStackTrace();
		}       
        long l=endDate.getTime()-starDate.getTime();
        long day=l/(24*60*60*1000);
        String resultStr="0";
        if(day>0){
        	resultStr=resultStr+day+"天";
        }
		return resultStr;
    }
    
    public static int getDay(Date s, Date e) {
		if (s.after(e)) {
			Date t = s;
			s = e;
			e = t;
		}
		Calendar start = Calendar.getInstance();
		start.setTime(s);
		Calendar end = Calendar.getInstance();
		end.setTime(e);
		Calendar temp = Calendar.getInstance();
		temp.setTime(e);
		temp.add(Calendar.DATE, 1);

		if ((start.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) == 1)) {// 前后都不破月
			return 0;
		} else if ((start.get(Calendar.DATE) != 1)
				&& (temp.get(Calendar.DATE) == 1)) {// 前破月后不破月
			return getDayP(start);
		} else if ((start.get(Calendar.DATE) == 1)
				&& (temp.get(Calendar.DATE) != 1)) {// 前不破月后破月
			return end.get(Calendar.DATE);
		} else {// 前破月后破月
			if (start.get(Calendar.MONTH) == end.get(Calendar.MONTH)
					&& start.get(Calendar.YEAR) == end.get(Calendar.YEAR)) {
				return end.get(Calendar.DATE) - start.get(Calendar.DATE) + 1;
			} else {
				return getDayP(start) + end.get(Calendar.DATE);
			}
		}
	}
    
    /**
     * 返回月份天数
     * @param s
     * @return
     */
    public static int getDayP(Calendar s) {
		int d;
		if (s.get(Calendar.MONTH) == 1 && s.get(Calendar.YEAR) % 4 == 0
				&& s.get(Calendar.YEAR) % 100 != 0) {// 闰年2月
			d = 29;
		} else {
			Map<Integer, Integer> m = new HashMap<Integer, Integer>();
			m.clear();
			m.put(1, 31);
			m.put(3, 31);
			m.put(5, 31);
			m.put(7, 31);
			m.put(8, 31);
			m.put(10, 31);
			m.put(12, 31);
			m.put(4, 30);
			m.put(6, 30);
			m.put(9, 30);
			m.put(11, 30);
			m.put(2, 28);
			d = m.get(s.get(Calendar.MONTH) + 1);
		}
		return d - s.get(Calendar.DATE) + 1;
	}
    
    
    public static void main(String[] args) {
    	//System.out.println(getCurrentWeekday2(-1,3));                
    	//System.out.println(getCurrentWeekday2(0,3));
    	//System.out.println(getNowTime());
    	//System.out.println(getTwoTimeDayHourMin("2004-01-02 11:30:24.496","2004-03-26 13:31:40.496"));
    	String currTime=DateUtil.parseToString(DateUtil.getNowTime(),"yyyy-MM");

    	//String beginDate=(StringUtil.getNextTime(currTime,-60*24)).split(" ")[0];
    	//String endDate=(StringUtil.getNextTime(currTime,60*24*365)).split(" ")[0];  
    	System.out.println("currTime=="+currTime);
    	//System.out.println("endDate=="+endDate);
    	
    	System.out.println(DateUtil.getMonthLastDay("2013-07-01 00:00:00", 1));
    }
    
}