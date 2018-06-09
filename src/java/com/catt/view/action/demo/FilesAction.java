package com.catt.view.action.demo;

import com.catt.model.service.demo.FilesService;
import com.catt.view.action.BaseAction;
import org.apache.struts2.config.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import util.JackJson;
import util.RequestUtil;
import util.StringUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
@Component("FilesAction")
@Scope("prototype")
@ParentPackage("struts-base")
public class FilesAction extends BaseAction{

	@Resource
	private FilesService filesService;

	private final String encoding = "GBK";

	public Object getModel() {
		return null;
	}

	/**以下3个方法用于实现增删改查功能**************************/

	/**
	 * 获取人员列表信息
	 * 同是也可用于导出
	 */
	@SuppressWarnings("SameReturnValue")
	public String getFilesList() throws Exception{
		@SuppressWarnings("unchecked") Map<String, String> mp=RequestUtil.getMapByRequest(this.getRequest());
		List list = filesService.getFilesList(mp);
		String json = JackJson.getBasetJsonData(list);
		RequestUtil.responseOut(encoding, json, this.getResponse());

		return null;
	}

	/**
	 * 新增、修改人员信息
	 */
	@SuppressWarnings("SameReturnValue")
	public String addEditFiles() throws Exception{
		int num;
		Map map=RequestUtil.getMapByRequest(this.getRequest());

		if(StringUtil.checkObj(map.get("iId"))){//数据对象已经有ID：修改
			num = filesService.editFiles(map);
		}else{//新增
			//map.put("dEmployDate",new Date());
			num = filesService.addFiles(map);
		}
		RequestUtil.responseOut(encoding, num+"", this.getResponse());
		return null;
	}

	/**
	 * 删除人员信息
	 */
	@SuppressWarnings("SameReturnValue")
	public String deleteFiles() throws Exception{
		@SuppressWarnings("unchecked") Map<String, String> mp=RequestUtil.getMapByRequest(this.getRequest());
		int num = filesService.deleteFiles(mp.get("iId"));
		RequestUtil.responseOut(encoding, num+"", this.getResponse());
		return null;
	}
}
