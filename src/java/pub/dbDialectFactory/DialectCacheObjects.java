package pub.dbDialectFactory;

import java.util.HashMap;
import java.util.Map;
/**
 * 主键缓对象
 * @author gaotao
 *
 */
public class DialectCacheObjects {
	/**
	 * 各个数据源节点缓存的主键
	 */
	public static Map<String,Map<String,Long>> primaryKeys=new HashMap<String, Map<String,Long>>();
}
