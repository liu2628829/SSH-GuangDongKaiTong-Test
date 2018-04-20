package util;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

/** 
 * @author ���
 * @version 1.0
 * @datetime 2010-8-11 ����03:23:18 
 * ��˵�� 
 */
public class JackJson {
	/**
	 * ����תjson��
	 * @param obj ����������
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
				throw new BaseRuntimeException("����תjson��ʱ����!", e);
			}
		}
		return writer.toString();
    } 

	/**
	 * json�����ı���ת����
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
			throw new BaseRuntimeException("json�����ı���ת����ʱ����!", e);
		}
		return list;
	}
	
	/**
	 * json��תָ������
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
			throw new BaseRuntimeException("json��תָ������ʱ����!", e);
		}
		return obj;
	}
	
	/**
	 * ��JSON�ַ���ת��ΪMapʵ�������
	 * @param jsonStr json��
	 * @return
	 */
	public static Map getMapByJsonString(String jsonStr){
		String CallStack=getCallStack();
		HashMap m=null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			m = mapper.readValue(jsonStr, HashMap.class); 
		} catch (Exception e) {
			throw new BaseRuntimeException("��JSON�ַ���ת��ΪMapʵ�������ʱ����", e);
		}
		return m;
	}
	
	/**
	 * ��JSON�ַ���ת��Ϊָ��Mapʵ�������
	 * zhanweibin 2013-4-25
	 * @param jsonStr json��
	 * @param c map��������
	 * @return
	 */
	public static Map getMapByJsonString(String jsonStr, Class<? extends Map> c){
		String CallStack=getCallStack();
		Map m=null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			m = mapper.readValue(jsonStr, c); 
		} catch (Exception e) {
			throw new BaseRuntimeException("��JSON�ַ���ת��Ϊָ��Mapʵ�������ʱ����!", e);
		}
		return m;
	}
	
	/**
	 * ��JSON�ַ���ת��ΪMapʵ�������
	 * @param json ��:{one:1,tow:2,.....}
	 * @return
	 */
	@Deprecated
	public static Map getMapFromSimpleJson(String json) {
		return getMapByJsonString(json);
	}
	
	/**
     * ��ȡ��������·��
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
	 * ����
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/*System.out.println("����תjson��-------------------------------");
		Date d1 = new Date();
		Map map = new HashMap();
		map.put("a", "tes1");
		map.put("b", "12");
		List list = new ArrayList();
		list.add(map);
		Date d2 = new Date();
		System.out.println("װ�ض���" + StringUtil.getTimeInMillis(d1, d2));
		
		Date d3 = new Date();
		String str = getBasetJsonData(list);
		Date d4 = new Date();
	    System.out.println("ת��json��" + StringUtil.getTimeInMillis(d3, d4) + str);
	    
	    String json = "[{\"address\": \"address2\",\"name\":\"haha2\",\"id\":2,\"email\":\"email2\"},"+"{\"address\":\"address\",\"name\":\"haha\",\"id\":1,\"email\":\"email\"}]";
	    List lis=getListByJsonArray(json);
	    System.out.println("json�����ı���ת����-------------------------------�ܹ���"+lis.size()+"����!\n");
	    for(int i=0;i<lis.size();i++){
	    	System.out.println("--------------����_"+i+"-----------------------");
	    	LinkedHashMap<String, Object> m=(LinkedHashMap<String, Object>)lis.get(i);
	    	for(Iterator<String> it=m.keySet().iterator();it.hasNext();){
	    		String key=it.next();
	    		System.out.println(key+"===="+m.get(key));
	    	}
	    	
	    }*/
	    
	    /*System.out.println("\njson��ת����-------------------------------");
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
		 + "datas:{sql:\"select iDutyId as \"id\", cDutyName as \"text\",iDeptId as \"iDeptId\" from tbDuty\"}," //����
		 + "refTarget:{iDeptId:\"I_SELECT1\"}//������Ԫ�ص�ֵ����,iDeptIdҪ�������еļ�һ�£�I_SELECT1�Ǳ��о����Ԫ�ص�ID"+
		"}";
		
		/*String kk="{"
			 + "evtTarget:\"I_SELECT2\","
			 + "datas:{key:\"TBFDTEST_I_SELECT2\",val:[{id:\"1\",text:\"aa\"},{id:\"2\",text:\"bb\"}]}," //����
			 + "refTarget:{iDeptId:\"I_SELECT1\"}//������Ԫ�ص�ֵ����,iDeptIdҪ�������еļ�һ�£�I_SELECT1�Ǳ��о����Ԫ�ص�ID"+
			"}";*/
		
		 //�õ�datas��һ��
		//ControlBusServiceImpl.getTransLateMap("", kk);
		
	}
}