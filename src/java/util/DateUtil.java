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
    
    //ת��oraleʱ������Ҫʹ�õ�ʱ���ʽ
    public static final String Oracl_yyMMdd="yy-mm-dd";
    public static final String Oracl_yyyyMMdd="yyyy-mm-dd";
    public static final String Oracl_HHmmss="hh24:mi:ss";
    public static final String Oracl_yyyyMMddHHmmss="yyyy-mm-dd hh24:mi:ss";
    public static final String Oracl_yyyyMMddHHmm="yyyy-mm-dd hh24:mi";
    public static final String Oracl_yyMMddHHmmss="yy-mm-dd hh24:mi:ss";
    
    /**
     * �������ݿⲻͬ��ת��ʱ���ʽ
     * ��Ҫ�������ݵ�ʱ���ֶεıȽϣ�ͨ���ڲ�ѯʱʹ��
     * @param sDate
     * @param sFormat
     * @param iDBType
     * ע�⣺sDate��sFormat��ʱ���ʽҪƥ��
     *   ���sDate�ĸ�ʽ��"2009-09-09" ,��sFormatʹ��"yyyy-mm-dd"
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
     * �����ṩ��ʱ���ʽ���ַ�������������
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
     * ��ʱ��ת����ָ���ĸ�ʽ���ַ���
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
     * ��������ʱ�����ʽ���ص�ǰʱ����ַ���
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
     * ��ָ����ʽ���ص�ǰʱ����ַ���
     * @param format ���ڸ�ʽ
     * @return formatΪ���򷵻�yyyy-MM-dd��ʽ���ַ���
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
     * ��������ʱ�ָ�ʽ���ص�ǰʱ����ַ���
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
     * �������ո�ʽ���ص�ǰ���ڵ��ַ���
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
     * �������ո�ʽ���ص��µ�һ��������ڵ��ַ���
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
     * ��õ�ǰ�����뱾������������ 
     * @param day
     * @return
     */   
    private static int getMondayPlus(int day) {    
        Calendar cd = Calendar.getInstance();    
        // ��ý�����һ�ܵĵڼ��죬�������ǵ�һ�죬���ڶ��ǵڶ���......    
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK)-day;         //��Ϊ����Ҫ���Ǵ��������������������ڶ���Ϊ��һ�����������3,����������Ϊ��ʼ����    
        if (dayOfWeek == 1) {    
            return 0;    
        } else {    
            return 1 - dayOfWeek;    
        }    
    }   
    
    /**
     * ��ñ��������յ�����      
     * @param week
     * @param day
     * @return
     */
    public static String getCurrentWeekday2(int week,int day) {    //��һ��������Ϊ������һ�ܣ��ڶ�����������������һ����Ϊ��ʼ����
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
     * ����������ڼ���������
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
     * �Ը���ʱ����·ݽ����������������ز�����ʱ���yyyy-MM-dd HH:mm:ss��ʽ�ַ���
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
     * ��ȡ��ǰ���ں��N���º������(����������)�����ز�����ʱ���yyyy-MM-dd HH:mm:ss��ʽ�ַ���
     * @param ts  yyyy-MM-dd HH:mm:ss
     * @param i  �·�
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
     * ����ʱ�����ʱ��
     * @param startTime
     * @param endTime
     * endTime>startTime
     * @return ����XX��XXСʱXX��
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
        	resultStr=resultStr+day+"��";
        }
        if(hour>0){
        	resultStr=resultStr+hour+"Сʱ";
        }
        if(min>0){
        	resultStr=resultStr+min+"��";
        }  
		return resultStr;
    }
    /**
     * ����ʱ�����ʱ��
     * @param startTime
     * @param endTime
     * endTime>startTime
     * @return ����XX��
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
        	resultStr=resultStr+day+"��";
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

		if ((start.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) == 1)) {// ǰ�󶼲�����
			return 0;
		} else if ((start.get(Calendar.DATE) != 1)
				&& (temp.get(Calendar.DATE) == 1)) {// ǰ���º�����
			return getDayP(start);
		} else if ((start.get(Calendar.DATE) == 1)
				&& (temp.get(Calendar.DATE) != 1)) {// ǰ�����º�����
			return end.get(Calendar.DATE);
		} else {// ǰ���º�����
			if (start.get(Calendar.MONTH) == end.get(Calendar.MONTH)
					&& start.get(Calendar.YEAR) == end.get(Calendar.YEAR)) {
				return end.get(Calendar.DATE) - start.get(Calendar.DATE) + 1;
			} else {
				return getDayP(start) + end.get(Calendar.DATE);
			}
		}
	}
    
    /**
     * �����·�����
     * @param s
     * @return
     */
    public static int getDayP(Calendar s) {
		int d;
		if (s.get(Calendar.MONTH) == 1 && s.get(Calendar.YEAR) % 4 == 0
				&& s.get(Calendar.YEAR) % 100 != 0) {// ����2��
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