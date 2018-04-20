package com.catt.view.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.struts2.config.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import util.JackJson;
import util.RequestUtil;
import util.ComminEnumUtil;

@SuppressWarnings("serial")
@Component("commonEnumAction")
@Scope("prototype")
@ParentPackage("struts-base")
public class CommonEnumAction extends BaseAction {
	
	private final String encoding = "GBK";
	
	/**
	 * ��ȡö�ٱ���ĳ���ֶε�����ö�ټ���
	 * @return
	 * @throws Exception  
	 */
	public String getEnumMeaning() throws Exception {
		
		//�����ӣ����첽���󣬲�����
		boolean boo = RequestUtil.isAjaxRequest(this.getRequest());
		if(!boo){
			return null;
		}
		
		String json = null;
		String key = this.getRequest().getParameter("key");
		String sql = this.getRequest().getParameter("sql");
		String reload = this.getRequest().getParameter("reload");
		String db = this.getRequest().getParameter("db");
		if(sql == null || "".equals(sql.trim())) {
			List<Map> list = new ArrayList<Map>();
			Map<String, String> m = (Map)ComminEnumUtil.getEnumMeaning(key, null);
			for(Map.Entry<String, String> entry : m.entrySet()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("value", entry.getKey());
				map.put("text", entry.getValue());
				list.add(map);
			}
			json = JackJson.getBasetJsonData(list);
		}else {
			boolean bool = (reload == null || "".equals(reload.trim()))?false:true;
			List list = ComminEnumUtil.getEnum(key, sql, bool, db);
			json = JackJson.getBasetJsonData(list);
		}
		RequestUtil.responseOut(encoding, json, getResponse());
		return null;
	}
	
	/**
	 * ��ȡö�ٱ���ĳ���ֶ�ĳ��ö��ֵ��ö�ٷ���
	 * @return
	 * @throws Exception
	 */
	public String getEnumValue() throws Exception {
		String key = this.getRequest().getParameter("key");
		String value = this.getRequest().getParameter("value");
		String meaning = (String)ComminEnumUtil.getEnumMeaning(key, value);
		RequestUtil.responseOut(encoding, meaning, getResponse());
		return null;
	}

	/**
	 * ���ĳ��ö��
	 * @return
	 * @throws Exception
	 */
	public String cleanEnum() throws Exception {
		int flg=1;
		try{
			ComminEnumUtil.cleanEnum(this.getRequest().getParameter("key"));
		}catch(Exception e){
			flg=0;
		}
		RequestUtil.responseOut(encoding, flg+"", getResponse());
		return null;
	}
	
	/**
	 * ȡ����ǰ�ѻ����ö��keyֵ
	 * @return
	 * @throws Exception
	 */
	public String getEnumKeys() throws Exception {
		String json = (String)ComminEnumUtil.getAllCache();
		RequestUtil.responseOut(encoding, json, getResponse());
		return null;
	}
	
	/**
	 * ����keyֵ�������ö��
	 * @return
	 * @throws Exception
	 */
	public String cleanEnumCacheByKey() throws Exception {
		int flag = 0;
		String key = this.getRequest().getParameter("key");
		ComminEnumUtil.cleanEnumCacheByKey(key);
		flag = 1;
		RequestUtil.responseOut(encoding, flag + "", getResponse());
		return null;
	}
	
	public Object getModel() {
		return null;
	}
	
	public static void main(String[] args) {
		try {
			Runtime.getRuntime().exec("F:/tools/getColor.exe");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
