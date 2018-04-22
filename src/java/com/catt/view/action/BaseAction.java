package com.catt.view.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@SuppressWarnings("serial")
public abstract class BaseAction extends ActionSupport implements ModelDriven {
	/**
	 * 获取request内置对象
	 * @return
	 * @throws Exception
	 */
	public HttpServletRequest getRequest() throws Exception {
		ActionContext ctx = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);
		return request;
	}

	/**
	 * 获取response内置对象
	 * @return
	 * @throws Exception
	 */
	public HttpServletResponse getResponse() throws Exception {
		ActionContext ctx = ActionContext.getContext();
		HttpServletResponse response = (HttpServletResponse)ctx.get(ServletActionContext.HTTP_RESPONSE);
		return response;
	}

	/**
	 * 获取session内置对象
	 * @return
	 * @throws Exception
	 */
	public HttpSession getSession() throws Exception {
		ActionContext ctx = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);
		HttpSession session = request.getSession();
		return session;
	}



	/**
	 * 记录操作日志
	 * @param iLogType   操作模块
	 * @param systemId   系统ID
	 * @param remark     操作信息
	 * @throws Exception

	@Deprecated
	public void logOperation(int iLogType, int systemId, String remark) throws Exception {
		HttpSession session = getSession();
		StaffRegister logStaff = (StaffRegister) SessionUtil.getAttribute("staff", session);
		if(logStaff != null) {
			LogUtil.setLogForStaff(logStaff, iLogType, remark, systemId);
		}
	}*/

	/**
	 * 记录操作日志，最后一个参数5固定为省集中告警系统
	 * Added by Zhanweibin 2011-12-20
	 * @param iLogType
	 * @param remark
	 
	public void logOperation(int iLogType, String remark) {
		try {
			HttpSession session = getSession();
			StaffRegister logStaff = (StaffRegister) session.getAttribute("staff");
			if(logStaff != null) {
				LogUtil.setLogForStaff(logStaff, iLogType, remark, 5);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

}
