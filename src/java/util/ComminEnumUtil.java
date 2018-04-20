package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

import pub.source.DatabaseUtil;

/**
 * 
 * @author 李军
 * @datetime 2011-05-20
 * update:gaotao 2012-03-22 增加FORM_CACHE表单缓存对象
 */
public class ComminEnumUtil{
	
	private static Map<String,List> COMMON_ENUM = new HashMap<String, List>();
	private static Map<String, Map> ENUM_MAP = null;//多行数据转成了一个map，key就是枚举值，value就是枚举名
	private static Map<String, List> ENUM_LIST = null;//多行数据还是多个map，放在一个list里,与查询操作一致
	private static Object obj = new Object();
	
	/**
	 * 将枚举值翻译为枚举中文
	 * 1、如果传入key,value则返回具体某个枚举翻译
	 * 2、如果只传入key则返回该字段的所有枚举翻译列表
	 */
	public static Object getEnumMeaning(String key, String value) {
		Object obj = null;
		if(ENUM_MAP == null) {
			init();
		}
		if(value == null)
			obj = ENUM_MAP.get(key);
		else
			obj = ENUM_MAP.get(key).get(value);
		return obj;
	}
	
	/**
	 * 将枚举中文反翻译为枚举值
	 */
	public static String getEnumType(String key, String meaning) {
		String type = "";
		if(ENUM_MAP == null) {
			init();
		}
		if(meaning != null) {
			Map<String, String> m = ENUM_MAP.get(key);
			if(m == null)
				return type;
			for(Map.Entry<String, String> entry : m.entrySet()) {
				String I_ENUM_VALUE = entry.getKey();
				String S_ENUM_MEANING = entry.getValue();
				if(S_ENUM_MEANING.equals(meaning))
					return I_ENUM_VALUE;
			}
		}
		return type;
	}
	
	/**
	 * 缓存枚举表
	 *
	 */
	private static void init() {
		synchronized(obj){
			if(ENUM_MAP!=null)
				return;
			/*为统一枚举表，T_ENUMERATE表不再使用
			String sql = "select t.S_ENUM_TYPE, t.I_ENUM_VALUE, t.S_ENUM_MEANING from T_ENUMERATE t";
			ENUM_MAP = new HashMap<String, Map>();
			List tempList = DatabaseUtil.queryForList(sql);
			for(int i=0; i<tempList.size(); i++) {
				Map map = (Map)tempList.get(i);
				String S_ENUM_TYPE = (String)map.get("S_ENUM_TYPE");
				String I_ENUM_VALUE = (String)map.get("I_ENUM_VALUE");
				String S_ENUM_MEANING = (String)map.get("S_ENUM_MEANING");
				if(ENUM_MAP.containsKey(S_ENUM_TYPE)) {
					Map<String, String> mp = (Map)ENUM_MAP.get(S_ENUM_TYPE);
					mp.put(I_ENUM_VALUE, S_ENUM_MEANING);
					ENUM_MAP.remove(S_ENUM_TYPE);
					ENUM_MAP.put(S_ENUM_TYPE, mp);
				}else {
					Map<String, String> mp = new HashMap<String, String>();
					mp.put(I_ENUM_VALUE, S_ENUM_MEANING);
					ENUM_MAP.put(S_ENUM_TYPE, mp);
				}
			}*/
			String sql="select sEnumTblName as \"sEnumTblName\", sEnumColName as \"sEnumColName\", iEnumValue as \"iEnumValue\", sEnumName as \"sEnumName\" FROM tbCtEnumTbl2 where isEnabled=1 order by sEnumTblName,sEnumColName,iEnumValue";
			ENUM_MAP = new HashMap<String, Map>();
			ENUM_LIST = new HashMap<String, List>();
			List tempList = DatabaseUtil.queryForList(sql);
			for(int i=0;i<tempList.size();i++){
				Map map = (Map)tempList.get(i);
				String sEnumTblName = (String)map.get("sEnumTblName");
				String sEnumColName = (String)map.get("sEnumColName");
				String iEnumValue = (String)map.get("iEnumValue");
				String sEnumName = (String)map.get("sEnumName");
				
				//以sEnumTblName（枚举归属表）和sEnumColName（枚举归属表字段）确定一组枚举
				Map<String, String> m;
				List<Map<String, String>> lis;
				String enumMapKey = sEnumTblName+"_"+sEnumColName;
				if(ENUM_MAP.containsKey(enumMapKey)){//如果已有
					m = (Map)ENUM_MAP.get(enumMapKey);
					lis=(List<Map<String, String>>)ENUM_LIST.get(enumMapKey);
				}else{//如果还没有，则创建
					m = new HashMap<String, String>();
					lis=new LinkedList<Map<String, String>>();
				}
				//追加
				m.put(iEnumValue, sEnumName);
				ENUM_MAP.put(enumMapKey, m);
				
				Map<String, String> mp = new HashMap<String, String>();
				mp.put("val", iEnumValue);
				mp.put("text", sEnumName);
				lis.add(mp);
				ENUM_LIST.put(enumMapKey, lis);
			}
		}
	}
	/**
	 * 根据SQL获取枚举
	 * @param key 枚举名
	 * @param sql 枚举SQL
	 * @param reload false,代表不重新加载，即缓存，true代表重新加载，即不缓存
	 * @return
	 */
	public static List getEnum(String key, String sql, boolean reload){ 
		return getEnum(key, sql, reload, null);
	}
	
	/**
	 * 根据SQL获取枚举
	 * @param key 枚举名
	 * @param sql 枚举SQL
	 * @param reload false,代表不重新加载，即缓存，true代表重新加载，即不缓存
	 * @return
	 */
	public static List getEnum(String key, String sql, boolean reload, String domain){ 
		if(reload) COMMON_ENUM.remove(key);
		List temp = COMMON_ENUM.get(key);
		if(temp == null){
			temp = DatabaseUtil.queryForList(sql, domain);
			if(!reload)//如果不重新加载，则缓存，否则每次重新加载，也就没必要缓存
			COMMON_ENUM.put(key, temp);
		}		
		return temp;
	}
	
	/**
	 * 清除缓存的格举
	 * key 有值就清除key的枚举缓存，如果没有就清除所有枚举缓存
	 */
	public static void cleanEnum(String key){
		if(StringUtil.checkStr(key))
			COMMON_ENUM.put(key, null);
		else
			COMMON_ENUM.clear();
	}
	
	/**
	 * 扫描出枚举缓存key值
	 * @return
	 */
	public static String getAllCache(){
		//Set set = COMMON_ENUM.keySet();
		StringBuffer sb = new StringBuffer();
		int i = 1;
		sb.append("[{\"iId\":\"0\",\"sName\":\"枚举Key值\",\"sParent\":\"-1\"}");
		for(Iterator<String> iterator = COMMON_ENUM.keySet().iterator(); 
		iterator.hasNext();){
			String key = iterator.next();
			sb.append(",{\"iId\":\""+i+"\",\"sName\":\""+key+"\",\"sParent\":\"0\"}");
			i++;
		}
		sb.append("]");
		List list = JackJson.getListByJsonArray(sb.toString());
		list = BuildTree.createTree(list, "sParent", "iId");
		String jsonStr = JackJson.getBasetJsonData(list);
		//System.out.println("key="+sb.toString());
		return jsonStr;
	}
	
	/**
	 * 根据key值清理对应缓存数据
	 * @param key
	 */
	public static void cleanEnumCacheByKey(String key){
		
		if(StringUtil.checkStr(key)){
			String[] keys = key.split(",");
			for(int i = 0; i < keys.length; i++){
				COMMON_ENUM.remove(keys[i]);
			}
		}
	}
	
	/**以下是通用表单缓存************************************************************/
	//通用表单form配置信息缓存
	private static Map<String,Map> FORM_CACHE=new HashMap<String, Map>();
	//通用表单，针对一个字段的所有枚举值进行缓存(表名+字段名，<值，文本>)
	private static Map<String,Map<String,String>> VALUE_TEXT_MAP=new HashMap<String, Map<String,String>>();
	//通用表单，表名与表单ID的键值化关系
	private static Map<String,String> TABLENAME_FORMID=new HashMap<String, String>();
	/**
	 * 获取表单配置缓存
	 * @param formId
	 * @return
	 */
	public static Map<String, Map> getFORM_CACHE_byFormId(String formId) {
		return FORM_CACHE.get(formId);
	}
    
	/**
	 * 缓存表单配置信息
	 * @param formId
	 * @param form_cache
	 */
	public static void setFORM_CACHE_byFormId(String formId,Map form_cache) {
		FORM_CACHE.put(formId, form_cache);
	}
	
	/*
	 * 判断该字段是否是枚举字段(所有枚举)
	 */
	public static boolean containsKeyAll(String key) {
		return ENUM_MAP.containsKey(key);
	}

	/**
	 * 存入
	 * 用于数据翻ID字段翻译
	 * m对象里的数据，key是枚举值，value是枚举文本
	 */
	public static void setEnum_Map(String k,Map m){
		VALUE_TEXT_MAP.put(k, m);
	}
	
	/**
	 * 取出
	 * @param k
	 * @return
	 */
	public static Map getEnum_Map(String k){
		return VALUE_TEXT_MAP.get(k);
	}
	
	//取
	public static Map<String,String> get_TABLENAME_FORMID(){
		return TABLENAME_FORMID;
	}
	//存
	public static void set_TABLENAME_FORMID(Map m){
		TABLENAME_FORMID=m;
	}
	/**以上是通用表单缓存**************************************************************/
	
	
	public static void main(String[] args) {
		Object obj = getEnumMeaning("I_EQUIPMENT_CODE", null);
		List list = getEnum("T_ENUMERATE_I_EQUIPMENT_CODE", "select t.I_ENUM_VALUE as value, t.S_ENUM_MEANING as text from T_ENUMERATE t where S_ENUM_TYPE = 'I_EQUIPMENT_CODE'", true);
		String json=JackJson.getBasetJsonData(list);
		System.out.println(json);
	}
}
