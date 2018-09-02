package com.catt.view.action.demo;

import com.catt.model.service.demo.DemoService;
import com.catt.view.action.BaseAction;
import org.apache.struts2.config.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import util.JackJson;
import util.RequestUtil;
import util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
@Component("demoAction")
@Scope("prototype")
@ParentPackage("struts-base")
public class DemoAction extends BaseAction{

	@Autowired
	private DemoService demoService;

	private final String encoding = "GBK";

	public Object getModel() {
		return null;
	}

//	public Map<String,Object> Obj2Map(Object obj) throws Exception{
//		Map<String,Object> map=new HashMap<String, Object>();
//		Field[] fields = obj.getClass().getDeclaredFields();
//		for(Field field:fields){
//			field.setAccessible(true);
//			map.put(field.getName(), field.get(obj));
//		}
//		return map;
//	}
//
//	public Map<String,Object> obj2Map(Object obj) throws Exception{
//		Map<String,Object> map=new HashMap<String, Object>();
//		BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
//		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
//		for (PropertyDescriptor property : propertyDescriptors) {
//			String key = property.getName();
//			if (key.compareToIgnoreCase("class") == 0) {
//				continue;
//			}
//			Method getter = property.getReadMethod();
//			Object value = getter!=null ? getter.invoke(obj) : null;
//			map.put(key, value);
//		}
//		return map;
//	}

	/**以下3个方法用于实现增删改查功能**************************/

	/**
	 * 获取人员列表信息
	 * 同是也可用于导出
	 */
	public String getEmployeeList() throws Exception{
		Map<String, String> mp=RequestUtil.getMapByRequest(this.getRequest());
		List list = demoService.getEmployeeList(mp);

		// 为适应topjui的分页，从map中取page,rows
		if (mp.get("page") != null && mp.get("rows") != null) {
			Map<String,Object> resultMap = new HashMap<>();
			resultMap.put("rows",list);

			/*
			Object map2 = list.get(0);
			String map2String =map2.toString();
			Map map3 =JackJson.getMapByJsonString(map2String);
			Object total = map3.get("totalCount");
			Log.info("map2" + map2);
			Log.info("map2String" + map2String);
			// 为适应topjui的分页，往resultMap中放入total
			resultMap.put("total",total);
			*/
			resultMap.put("total",24);

			String json = JackJson.getBasetJsonData(resultMap);
			RequestUtil.responseOut(encoding, json, this.getResponse());
			return null;
		}
		String json = JackJson.getBasetJsonData(list);
		RequestUtil.responseOut(encoding, json, this.getResponse());
		return null;
	}

	/**
	 * 新增、修改人员信息
	 */
	public String addEditEmployee() throws Exception{
		int num = 0;
		Map map=RequestUtil.getMapByRequest(this.getRequest());

		if(StringUtil.checkObj(map.get("iEmployeeId"))){//数据对象已经有ID：修改
			num = demoService.editEmployee(map);
		}else{//新增
			//map.put("dEmployDate",new Date());
			num = demoService.addEmployee(map);
		}
		RequestUtil.responseOut(encoding, num+"", this.getResponse());
		return null;
	}

	/**
	 * 删除人员信息
	 */
	public String deleteEmployee() throws Exception{
		Map<String, String> mp=RequestUtil.getMapByRequest(this.getRequest());
		int num = demoService.deleteEmployee(mp.get("iEmployeeId"));
		RequestUtil.responseOut(encoding, num+"", this.getResponse());
		return null;
	}
}
