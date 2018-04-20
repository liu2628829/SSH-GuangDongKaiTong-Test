package com.catt.view.action.demo;

import java.util.List;
import java.util.Map;

import org.apache.struts2.config.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import util.JackJson;
import util.RequestUtil;
import util.StringUtil;

import com.catt.model.service.demo.DemoService;
import com.catt.view.action.BaseAction;

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

	/**����3����������ʵ����ɾ�Ĳ鹦��**************************/

	/**
	 * ��ȡ��Ա�б���Ϣ
	 * ͬ��Ҳ�����ڵ���
	 */
	public String getEmployeeList() throws Exception{
		Map<String, String> mp=RequestUtil.getMapByRequest(this.getRequest());
		List list = demoService.getEmployeeList(mp);
		String json = JackJson.getBasetJsonData(list); 
		RequestUtil.responseOut(encoding, json, this.getResponse());
		
		return null;
	}
	
	/**
	 * �������޸���Ա��Ϣ
	 */
	public String addEditEmployee() throws Exception{
		int num = 0;
		Map map=RequestUtil.getMapByRequest(this.getRequest());
		
		if(StringUtil.checkObj(map.get("iEmployeeId"))){//���ݶ����Ѿ���ID���޸�
			num = demoService.editEmployee(map);
		}else{//����
			//map.put("dEmployDate",new Date());
			num = demoService.addEmployee(map);
		}
		RequestUtil.responseOut(encoding, num+"", this.getResponse());
		return null;
	}
	
	/**
	 * ɾ����Ա��Ϣ
	 */
	public String deleteEmployee() throws Exception{
		Map<String, String> mp=RequestUtil.getMapByRequest(this.getRequest());
		int num = demoService.deleteEmployee(mp.get("iEmployeeId"));
		RequestUtil.responseOut(encoding, num+"", this.getResponse());
		return null;
	}
}
