package util;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

/** 
 * @author 李军
 * @version 1.0
 * @datetime 2010-8-11 下午03:23:18 
 * 类说明 
 */
public class JackJson {
	/**
	 * 对象转json串
	 * @param obj 任意对象对型
	 * @return
	 */
	public static String getBasetJsonData(Object obj){
		String CallStack=getCallStack();
		StringWriter writer = new StringWriter();
		if(obj != null){
			ObjectMapper mapper = new ObjectMapper();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			mapper.getSerializationConfig().setDateFormat(sdf);
			try {
				mapper.writeValue(writer, obj);
			} catch(Exception e){
				throw new BaseRuntimeException("对象转json串时出错!", e);
			}
		}
		return writer.toString();
    } 

	/**
	 * json数组文本串转集合
	 * @param json
	 * @return
	 */
	public static List getListByJsonArray(String json){
		String CallStack=getCallStack();
		List<LinkedHashMap<String, Object>> list=null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			list = mapper.readValue(json, List.class); 
		} catch (Exception e) {
			throw new BaseRuntimeException("json数组文本串转集合时出错!", e);
		}
		return list;
	}
	
	/**
	 * json串转指定对象
	 * @param json
	 * @param c
	 * @return
	 */
	public static Object getObjectByJson(String json, Class c){
		String CallStack=getCallStack();
		Object obj = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			obj=mapper.readValue(json, c);  
		} catch (Exception e) {
			throw new BaseRuntimeException("json串转指定对象时出错!", e);
		}
		return obj;
	}
	
	/**
	 * 将JSON字符串转换为Map实现类对象
	 * @param jsonStr json串
	 * @return
	 */
	public static Map getMapByJsonString(String jsonStr){
		String CallStack=getCallStack();
		HashMap m=null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			m = mapper.readValue(jsonStr, HashMap.class); 
		} catch (Exception e) {
			throw new BaseRuntimeException("将JSON字符串转换为Map实现类对象时出错", e);
		}
		return m;
	}
	
	/**
	 * 将JSON字符串转换为指定Map实现类对象
	 * zhanweibin 2013-4-25
	 * @param jsonStr json串
	 * @param c map对象类型
	 * @return
	 */
	public static Map getMapByJsonString(String jsonStr, Class<? extends Map> c){
		String CallStack=getCallStack();
		Map m=null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			m = mapper.readValue(jsonStr, c); 
		} catch (Exception e) {
			throw new BaseRuntimeException("将JSON字符串转换为指定Map实现类对象时出错!", e);
		}
		return m;
	}
	
	/**
	 * 将JSON字符串转换为Map实现类对象
	 * @param json 如:{one:1,tow:2,.....}
	 * @return
	 */
	@Deprecated
	public static Map getMapFromSimpleJson(String json) {
		return getMapByJsonString(json);
	}
	
	/**
     * 获取程序运行路径
     * @return
     */
	private static String getCallStack(){
    	StackTraceElement[] ste = new Throwable().getStackTrace();
		StringBuffer CallStack = new StringBuffer();
		for (int i = 1; i < ste.length; i++) {
			CallStack.append(ste[i].toString() + " | ");
			if (i > 2)break;
		}
		ste=null;
		return CallStack.toString();
    }
	
	/**
	 * 样例
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/*System.out.println("集合转json串-------------------------------");
		Date d1 = new Date();
		Map map = new HashMap();
		map.put("a", "tes1");
		map.put("b", "12");
		List list = new ArrayList();
		list.add(map);
		Date d2 = new Date();
		System.out.println("装载对象：" + StringUtil.getTimeInMillis(d1, d2));
		
		Date d3 = new Date();
		String str = getBasetJsonData(list);
		Date d4 = new Date();
	    System.out.println("转换json：" + StringUtil.getTimeInMillis(d3, d4) + str);
	    
	    String json = "[{\"address\": \"address2\",\"name\":\"haha2\",\"id\":2,\"email\":\"email2\"},"+"{\"address\":\"address\",\"name\":\"haha\",\"id\":1,\"email\":\"email\"}]";
	    List lis=getListByJsonArray(json);
	    System.out.println("json数组文本串转集合-------------------------------总共有"+lis.size()+"对象!\n");
	    for(int i=0;i<lis.size();i++){
	    	System.out.println("--------------对象_"+i+"-----------------------");
	    	LinkedHashMap<String, Object> m=(LinkedHashMap<String, Object>)lis.get(i);
	    	for(Iterator<String> it=m.keySet().iterator();it.hasNext();){
	    		String key=it.next();
	    		System.out.println(key+"===="+m.get(key));
	    	}
	    	
	    }*/
	    
	    /*System.out.println("\njson串转对象-------------------------------");
	    json="{\"itemCode\":\"aa\",\"itemName\":\"bb\",\"itemValue\":\"cc\"}";
	    Object obj=getObjectByJson(json,EnumItem.class);
	    EnumItem e=(EnumItem)obj;
	    System.out.println(e.getItemCode()+"--"+e.getItemName()+"--"+e.getItemValue());*/
		/*String str="{\"max\":100,\"min\":0}";
		Map m=JackJson.getMapByJsonString(str);
		System.out.println("max:"+m.get("max"));
		System.out.println("min:"+m.get("min"));*/
		
		//val:[{\"id\":\"1\",\"text\":\"aa\"},{\"id\":\"2\",\"text\":\"bb\"}]
		//,val:[{\"id\":\"1\",\"text\":\"aa\"},{\"id\":\"2\",\"text\":\"bb\"}]
		//key:\"TBFDTEST_I_SELECT2\",
		String kk="{"
		 + "evtTarget:\"I_SELECT2\","
		 + "datas:{sql:\"select iDutyId as \"id\", cDutyName as \"text\",iDeptId as \"iDeptId\" from tbDuty\"}," //必须
		 + "refTarget:{iDeptId:\"I_SELECT1\"}//受其它元素的值限制,iDeptId要与数据中的键一致，I_SELECT1是表单中具体表单元素的ID"+
		"}";
		
		/*String kk="{"
			 + "evtTarget:\"I_SELECT2\","
			 + "datas:{key:\"TBFDTEST_I_SELECT2\",val:[{id:\"1\",text:\"aa\"},{id:\"2\",text:\"bb\"}]}," //必须
			 + "refTarget:{iDeptId:\"I_SELECT1\"}//受其它元素的值限制,iDeptId要与数据中的键一致，I_SELECT1是表单中具体表单元素的ID"+
			"}";*/
		
		 //拿到datas的一段
		//ControlBusServiceImpl.getTransLateMap("", kk);
		
	}
}