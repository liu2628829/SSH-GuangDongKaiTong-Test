package pub.source;

import java.util.Map;

/**数据对象定义*/
public class Data {

	/**字段英文名*/
	private String filedName;
	/**数据类型*/
	private DataType dataType;
	/**值*/
	private Object val;
	/**构造*/
	private Data(String filedName, DataType dataType, Object val){
		this.filedName = filedName;
		this.dataType = dataType;
		this.val = val;
	}

		/**得实例*/
	public static Data get(String filedName, DataType dataType, Object val){
		return new Data(filedName, dataType, val);
	}

		/**得实例*/
	public static Data get(Field field, Object val){
		return get(field.name(), field.getDataType(), val);
	}

		/**得实例*/
	public static Data get(Field field, Map map){
		return get(field.name(), field.getDataType(), map.get(field.name()));
	}

	public String getFiledName() {
		return filedName;
	}

	public DataType getDataType() {
		return dataType;
	}

	public Object getVal() {
		return this.dataType.getVal(this.val);
	}

	public void setFiledName(String filedName) {
		this.filedName = filedName;
	}
}