package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

import pub.source.DatabaseUtil;

/**
 * 
 * @author ���
 * @datetime 2011-05-20
 * update:gaotao 2012-03-22 ����FORM_CACHE���������
 */
public class ComminEnumUtil{
	
	private static Map<String,List> COMMON_ENUM = new HashMap<String, List>();
	private static Map<String, Map> ENUM_MAP = null;//��������ת����һ��map��key����ö��ֵ��value����ö����
	private static Map<String, List> ENUM_LIST = null;//�������ݻ��Ƕ��map������һ��list��,���ѯ����һ��
	private static Object obj = new Object();
	
	/**
	 * ��ö��ֵ����Ϊö������
	 * 1���������key,value�򷵻ؾ���ĳ��ö�ٷ���
	 * 2�����ֻ����key�򷵻ظ��ֶε�����ö�ٷ����б�
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
	 * ��ö�����ķ�����Ϊö��ֵ
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
	 * ����ö�ٱ�
	 *
	 */
	private static void init() {
		synchronized(obj){
			if(ENUM_MAP!=null)
				return;
			/*Ϊͳһö�ٱ�T_ENUMERATE����ʹ��
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
				
				//��sEnumTblName��ö�ٹ�������sEnumColName��ö�ٹ������ֶΣ�ȷ��һ��ö��
				Map<String, String> m;
				List<Map<String, String>> lis;
				String enumMapKey = sEnumTblName+"_"+sEnumColName;
				if(ENUM_MAP.containsKey(enumMapKey)){//�������
					m = (Map)ENUM_MAP.get(enumMapKey);
					lis=(List<Map<String, String>>)ENUM_LIST.get(enumMapKey);
				}else{//�����û�У��򴴽�
					m = new HashMap<String, String>();
					lis=new LinkedList<Map<String, String>>();
				}
				//׷��
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
	 * ����SQL��ȡö��
	 * @param key ö����
	 * @param sql ö��SQL
	 * @param reload false,�������¼��أ������棬true�������¼��أ���������
	 * @return
	 */
	public static List getEnum(String key, String sql, boolean reload){ 
		return getEnum(key, sql, reload, null);
	}
	
	/**
	 * ����SQL��ȡö��
	 * @param key ö����
	 * @param sql ö��SQL
	 * @param reload false,�������¼��أ������棬true�������¼��أ���������
	 * @return
	 */
	public static List getEnum(String key, String sql, boolean reload, String domain){ 
		if(reload) COMMON_ENUM.remove(key);
		List temp = COMMON_ENUM.get(key);
		if(temp == null){
			temp = DatabaseUtil.queryForList(sql, domain);
			if(!reload)//��������¼��أ��򻺴棬����ÿ�����¼��أ�Ҳ��û��Ҫ����
			COMMON_ENUM.put(key, temp);
		}		
		return temp;
	}
	
	/**
	 * �������ĸ��
	 * key ��ֵ�����key��ö�ٻ��棬���û�о��������ö�ٻ���
	 */
	public static void cleanEnum(String key){
		if(StringUtil.checkStr(key))
			COMMON_ENUM.put(key, null);
		else
			COMMON_ENUM.clear();
	}
	
	/**
	 * ɨ���ö�ٻ���keyֵ
	 * @return
	 */
	public static String getAllCache(){
		//Set set = COMMON_ENUM.keySet();
		StringBuffer sb = new StringBuffer();
		int i = 1;
		sb.append("[{\"iId\":\"0\",\"sName\":\"ö��Keyֵ\",\"sParent\":\"-1\"}");
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
	 * ����keyֵ�����Ӧ��������
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
	
	/**������ͨ�ñ�����************************************************************/
	//ͨ�ñ�form������Ϣ����
	private static Map<String,Map> FORM_CACHE=new HashMap<String, Map>();
	//ͨ�ñ������һ���ֶε�����ö��ֵ���л���(����+�ֶ�����<ֵ���ı�>)
	private static Map<String,Map<String,String>> VALUE_TEXT_MAP=new HashMap<String, Map<String,String>>();
	//ͨ�ñ����������ID�ļ�ֵ����ϵ
	private static Map<String,String> TABLENAME_FORMID=new HashMap<String, String>();
	/**
	 * ��ȡ�����û���
	 * @param formId
	 * @return
	 */
	public static Map<String, Map> getFORM_CACHE_byFormId(String formId) {
		return FORM_CACHE.get(formId);
	}
    
	/**
	 * �����������Ϣ
	 * @param formId
	 * @param form_cache
	 */
	public static void setFORM_CACHE_byFormId(String formId,Map form_cache) {
		FORM_CACHE.put(formId, form_cache);
	}
	
	/*
	 * �жϸ��ֶ��Ƿ���ö���ֶ�(����ö��)
	 */
	public static boolean containsKeyAll(String key) {
		return ENUM_MAP.containsKey(key);
	}

	/**
	 * ����
	 * �������ݷ�ID�ֶη���
	 * m����������ݣ�key��ö��ֵ��value��ö���ı�
	 */
	public static void setEnum_Map(String k,Map m){
		VALUE_TEXT_MAP.put(k, m);
	}
	
	/**
	 * ȡ��
	 * @param k
	 * @return
	 */
	public static Map getEnum_Map(String k){
		return VALUE_TEXT_MAP.get(k);
	}
	
	//ȡ
	public static Map<String,String> get_TABLENAME_FORMID(){
		return TABLENAME_FORMID;
	}
	//��
	public static void set_TABLENAME_FORMID(Map m){
		TABLENAME_FORMID=m;
	}
	/**������ͨ�ñ�����**************************************************************/
	
	
	public static void main(String[] args) {
		Object obj = getEnumMeaning("I_EQUIPMENT_CODE", null);
		List list = getEnum("T_ENUMERATE_I_EQUIPMENT_CODE", "select t.I_ENUM_VALUE as value, t.S_ENUM_MEANING as text from T_ENUMERATE t where S_ENUM_TYPE = 'I_EQUIPMENT_CODE'", true);
		String json=JackJson.getBasetJsonData(list);
		System.out.println(json);
	}
}
