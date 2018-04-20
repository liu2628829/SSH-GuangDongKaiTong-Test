package pub.source;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import util.DateUtil;
import util.StringUtil;

/**枚举： java-数据库数据类型对应*/
public enum DataType {
	
	/**对应数据库数据类型：VARCHAR,CHAR,LONGVARCHAR*/
	STRING, 
	
	/**对应数据库数据类型：CLOB,TEXT （暂未实现对此数据类型的处理,默认当成了STRING）*/
	CLOB, 
	
	/**对应数据库数据类型：SMALLINT,TINYINT*/
	SHORT,  
	
	/**对应数据库数据类型：INT	*/
	INTEGER,
	
	/**对应数据库数据类型：BIGINT*/
	LONG,  
	
	/**对应数据库数据类型：FLOAT */
	FLOAT, 
	
	/**对应数据库数据类型：NUMBER,DECIMAL,NUMERIC*/
	DOUBLE, 
	
	/**对应数据库数据类型：BigDecimal(建议大于7位数时用此类型)*/
	BIG_DECIMAL, 
	
	/**对应数据库数据类型：取数据库系统时间*/
	SYSDATE, 	
	
	/**对应数据库数据类型：年月日*/
	DATE,   
	
	/**对应数据库数据类型：年月日时分秒*/
	TIME_STAMP;
	
	/**数据类型传换*/
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
	
	/**大数值转换*/
	private BigDecimal getBigDecimal(Object obj){
		BigDecimal data = null;
		if (obj instanceof BigDecimal) {
			data = (BigDecimal)obj;
		}else{
			data = new BigDecimal(obj.toString());
		}
		
		return data;
	}
	
	/**日期转换*/
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
	
	/**时间戳转换*/
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
