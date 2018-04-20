package pub.source;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import util.DateUtil;
import util.StringUtil;

/**ö�٣� java-���ݿ��������Ͷ�Ӧ*/
public enum DataType {
	
	/**��Ӧ���ݿ��������ͣ�VARCHAR,CHAR,LONGVARCHAR*/
	STRING, 
	
	/**��Ӧ���ݿ��������ͣ�CLOB,TEXT ����δʵ�ֶԴ��������͵Ĵ���,Ĭ�ϵ�����STRING��*/
	CLOB, 
	
	/**��Ӧ���ݿ��������ͣ�SMALLINT,TINYINT*/
	SHORT,  
	
	/**��Ӧ���ݿ��������ͣ�INT	*/
	INTEGER,
	
	/**��Ӧ���ݿ��������ͣ�BIGINT*/
	LONG,  
	
	/**��Ӧ���ݿ��������ͣ�FLOAT */
	FLOAT, 
	
	/**��Ӧ���ݿ��������ͣ�NUMBER,DECIMAL,NUMERIC*/
	DOUBLE, 
	
	/**��Ӧ���ݿ��������ͣ�BigDecimal(�������7λ��ʱ�ô�����)*/
	BIG_DECIMAL, 
	
	/**��Ӧ���ݿ��������ͣ�ȡ���ݿ�ϵͳʱ��*/
	SYSDATE, 	
	
	/**��Ӧ���ݿ��������ͣ�������*/
	DATE,   
	
	/**��Ӧ���ݿ��������ͣ�������ʱ����*/
	TIME_STAMP;
	
	/**�������ʹ���*/
	public Object getVal(Object obj){
		Object val = obj;
		
		if(StringUtil.checkObj(val)){
			switch(this){
				case STRING: 
				case CLOB: val = obj.toString(); break;
				case SHORT: val = Short.valueOf(obj.toString()); break;
				case INTEGER : val = Integer.valueOf(obj.toString()); break;
				case LONG : val = Long.valueOf(obj.toString()); break;
				case FLOAT: val = Float.valueOf(obj.toString()); break;
				case DOUBLE: val = Double.valueOf(obj.toString()); break;
				case BIG_DECIMAL: val = getBigDecimal(obj); break;
				case SYSDATE: val = null; break;
				case DATE: val = getDate(obj); break; 
				case TIME_STAMP: val = getTimestamp(obj); break; 
				default:val=null;
			}
		}
		
		return val;
	}
	
	/**����ֵת��*/
	private BigDecimal getBigDecimal(Object obj){
		BigDecimal data = null;
		if (obj instanceof BigDecimal) {
			data = (BigDecimal)obj;
		}else{
			data = new BigDecimal(obj.toString());
		}
		
		return data;
	}
	
	/**����ת��*/
	private Date getDate(Object obj){
		Date date = null;
		if (obj instanceof Date) {
			date = (Date) obj;
		}else if(obj instanceof java.util.Date){
			date = new Date(((java.util.Date)obj).getTime());
		}else if(obj instanceof Long){
			date = new Date((Long)obj);
		}else if(obj instanceof String){
			date = new Date(DateUtil.parseToDate(obj.toString(), DateUtil.yyyyMMdd).getTime());
		}
		return date;
	}
	
	/**ʱ���ת��*/
	private Timestamp getTimestamp(Object obj){
		Timestamp date =null;
		if (obj instanceof Timestamp) {
			date = (Timestamp) obj;
		}else if(obj instanceof java.util.Date){
			date = new Timestamp(((java.util.Date)obj).getTime());
		}else if(obj instanceof Long){
			date = new Timestamp((Long)obj);
		}else if(obj instanceof String){
			date = new Timestamp(DateUtil.parseToDate(obj.toString(), DateUtil.yyyyMMddHHmmss).getTime());
		}
		return date;
	}
}
