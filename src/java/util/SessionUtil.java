package util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import pub.servlet.ConfigInit;

import vo.deptMgr.StaffRegister;
/**
 * @author gaotao
 * ��session���а�װ����session��д����ͳһ�ô˹�������в���
 * �����ʱ��session���ݴ���ֲ�ʽ������������п���ͳһ���л����д����
 */
public class SessionUtil {
    
	/**��¼��ǰ����¼ϵͳ�������˺�*/
	private static Map<String, Map<String, Object>> singleLogin = new HashMap<String, Map<String,Object>>();
	
	public static Map<String, Map<String, Object>> getSingleLogin() {
		return singleLogin;
	}

	public static void setSingleLogin(Map<String, Map<String, Object>> singleLogin) {
		SessionUtil.singleLogin = singleLogin;
	}

	/**
	 * ����session����ֵ
	 * @param key
	 * @param obj
	 * @param session
	 */
	public static void  setAttribute(String key, Object obj, HttpSession session){
		session.setAttribute(key, obj);
	}
	
	/**
	 * ��ȡsession����ֵ
	 * @param key
	 * @param session
	 * @return
	 */
	public static Object getAttribute(String key, HttpSession session){
		
		Object obj = session.getAttribute(key);
		if(StringUtil.checkObj(obj)){
			return obj;
		}
		
		//�Ǽ��sessionģʽ�£�����һЩ��������
		obj = getTestSessionObj(key, session);
		return obj;
	}
	
	/**
	 * �Ǽ��sessionģʽ(һ���ǲ���Ҫ��ȫ����Ĳ��Գ���)�£�ȡһЩ��Ҫ�˺Ż�������
	 * @param key
	 * @param session
	 * @return
	 */
	public static Object getTestSessionObj(String key, HttpSession session){
		//����Ǽ��sessionģʽ(һ���ǲ���Ҫ��ȫ����Ĳ��Գ���)
		String isCheckSession = ConfigInit.Config.getProperty("isCheckSession","1");
		if("0".equals(isCheckSession)){
			StaffRegister staff=new StaffRegister();
			staff.setIStaffId("1");
			staff.setSStaffAccount("nocjktest");
			staff.setSStaffName("nocjktest");
			staff.setSTelphone("18926239263");
			staff.setSMobile("18926239263");
			staff.setIDeptId("1");
			staff.setSDeptName("��������");
			staff.setIDomainId("1030005");
			staff.setSDomainName("ʡ��˾");  
			
			SessionUtil.setAttribute("staff", staff, session); // �û�����
	        SessionUtil.setAttribute("USERID", staff.getIStaffId(), session); // �û��ʺ�ID
	        SessionUtil.setAttribute("USERNAME", staff.getSStaffAccount(),session); // ��¼��
	        SessionUtil.setAttribute("USERCNNAME", staff.getSStaffName(), session); // �û�����
	        SessionUtil.setAttribute("TELPHONE", staff.getSTelphone(), session); // �绰����
	        SessionUtil.setAttribute("MOBILE", staff.getSMobile(), session); // �ֻ�
	        SessionUtil.setAttribute("DEPTID", staff.getIDeptId(), session); // ����ID
	        SessionUtil.setAttribute("DEPTNAME", staff.getSDeptName(), session); // ��������
	        SessionUtil.setAttribute("REGIONID", staff.getIDomainId(), session); // ��id
	        SessionUtil.setAttribute("REGIONNAME", staff.getSDomainName(), session); // �����ƳƳ�
	        SessionUtil.setAttribute("COUNT", 1, session);
		}
		
		return session.getAttribute(key);
	}
	
}