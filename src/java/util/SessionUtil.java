package util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import pub.servlet.ConfigInit;

import vo.deptMgr.StaffRegister;
/**
 * @author gaotao
 * 对session进行包装，从session读写数据统一用此工具类进行操作
 * 如果到时将session数据存入分布式缓存服务器，有可以统一进行缓存读写操作
 */
public class SessionUtil {
    
	/**记录当前正登录系统的所有账号*/
	private static Map<String, Map<String, Object>> singleLogin = new HashMap<String, Map<String,Object>>();
	
	public static Map<String, Map<String, Object>> getSingleLogin() {
		return singleLogin;
	}

	public static void setSingleLogin(Map<String, Map<String, Object>> singleLogin) {
		SessionUtil.singleLogin = singleLogin;
	}

	/**
	 * 设置session属性值
	 * @param key
	 * @param obj
	 * @param session
	 */
	public static void  setAttribute(String key, Object obj, HttpSession session){
		session.setAttribute(key, obj);
	}
	
	/**
	 * 获取session属性值
	 * @param key
	 * @param session
	 * @return
	 */
	public static Object getAttribute(String key, HttpSession session){
		
		Object obj = session.getAttribute(key);
		if(StringUtil.checkObj(obj)){
			return obj;
		}
		
		//非检查session模式下，构造一些虚拟数据
		obj = getTestSessionObj(key, session);
		return obj;
	}
	
	/**
	 * 非检查session模式(一般是不需要安全管理的测试场景)下，取一些必要账号基本数据
	 * @param key
	 * @param session
	 * @return
	 */
	public static Object getTestSessionObj(String key, HttpSession session){
		//如果非检查session模式(一般是不需要安全管理的测试场景)
		String isCheckSession = ConfigInit.Config.getProperty("isCheckSession","1");
		if("0".equals(isCheckSession)){
			StaffRegister staff=new StaffRegister();
			staff.setIStaffId("1");
			staff.setSStaffAccount("nocjktest");
			staff.setSStaffName("nocjktest");
			staff.setSTelphone("18926239263");
			staff.setSMobile("18926239263");
			staff.setIDeptId("1");
			staff.setSDeptName("网络监控室");
			staff.setIDomainId("1030005");
			staff.setSDomainName("省公司");  
			
			SessionUtil.setAttribute("staff", staff, session); // 用户对象
	        SessionUtil.setAttribute("USERID", staff.getIStaffId(), session); // 用户帐号ID
	        SessionUtil.setAttribute("USERNAME", staff.getSStaffAccount(),session); // 登录名
	        SessionUtil.setAttribute("USERCNNAME", staff.getSStaffName(), session); // 用户姓名
	        SessionUtil.setAttribute("TELPHONE", staff.getSTelphone(), session); // 电话号码
	        SessionUtil.setAttribute("MOBILE", staff.getSMobile(), session); // 手机
	        SessionUtil.setAttribute("DEPTID", staff.getIDeptId(), session); // 部门ID
	        SessionUtil.setAttribute("DEPTNAME", staff.getSDeptName(), session); // 部门名称
	        SessionUtil.setAttribute("REGIONID", staff.getIDomainId(), session); // 域id
	        SessionUtil.setAttribute("REGIONNAME", staff.getSDomainName(), session); // 域名称称称
	        SessionUtil.setAttribute("COUNT", 1, session);
		}
		
		return session.getAttribute(key);
	}
	
}