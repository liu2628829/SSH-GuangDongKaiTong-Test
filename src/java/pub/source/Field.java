package pub.source;

/**字段段定义*/
public interface Field {
	
	/**获得字段名称，当实现类是enum类型时，此方法默认就实现了*/
	public String name();
	
	/**获取字段数据类型*/
	public DataType getDataType();
}
