package pub.source;

import java.util.Map;

import org.apache.log4j.Logger;

import pub.servlet.ConfigInit;
import util.StringUtil;

/**�÷���������־�������չ,����ͨ���̳и�����д��־�����ʽ
 * @author tangyj
 * @date Jul 4, 2013
 */
public  class LogPrintExt {
	private static LogPrintExt instance = null;
	
	private static String SYSTEM_NAME = ConfigInit.Config.getProperty("SYSTEM_NAME");
	
	 /**��ȡʵ��
	 * @return ʵ��
	 */
	public static LogPrintExt getInstance(){
		 if(instance == null){
			 createInstance();
		 }
		 return instance;
	 }

	/**
	 * ����ʵ���������÷��������config.properties���ô���ʵ��
	 */
	private static void createInstance() {
		String clazzStr = (String) ConfigInit.Config.get("Log_PrintExt");
		if (!StringUtil.checkStr(clazzStr)) {// Ĭ���ǰ�action����·������ƥ��
			clazzStr = "pub.source.LogPrintExt";
		}
		 try {
			 instance = (LogPrintExt) Class.forName(clazzStr).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
			
		}
	}
	
	/**������־�����չ����
	 * @param log  ��־����
	 * @param sMd5  ��־��ʶ��
	 * @param map ��������
	 */
	public  void logOperate(Logger log,String sMd5, Map mp){
		log.info(sMd5 + "[����]�û�����[" + mp.get("iDomainId") + "-"
                + mp.get("sDomainName") + "]");
        log.info(sMd5 + "[����]�û����ţ�[" + mp.get("iDeptId") + "-"
                + mp.get("sDeptName") + "]");
        log.info(sMd5 + "[����]�û���ɫ��[" + mp.get("iRoleId") + "-"
                + mp.get("sRoleName") + "]");
        log.info(sMd5 + "[����]�û���Ϣ��[" + mp.get("iStaffId") + "-"
                + mp.get("sStaffAccount") + "-" + mp.get("sStaffName") + "]");
        log.info(sMd5 + "[����]����IP��" + mp.get("sIpAddress"));
        log.info(sMd5 + "[����]����·����" + mp.get("sUrl"));
        log.info(sMd5 + "[����]����ʱ����" + mp.get("iCostTime") + "����");
        log.info(sMd5 + "[����]���������" + mp.get("sParams"));
        log.info(sMd5 + "[����]����ģ�飺" + mp.get("rightInfo"));
        log.info(sMd5 + "[����]��ǰӦ�ã�"+ SYSTEM_NAME);
        log.info(sMd5 + "[����]��ǰҳ�棺"+ mp.get("sReferer"));
	};
	
	/**�쳣��־�����չ����
	 * @param log  ��־����
	 * @param sMd5  ��־��ʶ��
	 * @param map ��������
	 */
	public void logException(Logger log,String sMd5, Map mp){
		log.error(sMd5 + "[�쳣]�û���Ϣ��[" + mp.get("iStaffId") + "-"
	                + mp.get("sStaffAccount") + "-" + mp.get("sStaffName") + "]");
        log.error(sMd5 + "[�쳣]����ģ�飺" + mp.get("rightInfo"));
        log.error(sMd5 + "[�쳣]����·����" + mp.get("sUrl"));
        log.error(sMd5 + "[�쳣]����ʱ����" + mp.get("iCostTime") + "����");
        log.error(sMd5 + "[�쳣]���������" + mp.get("sParams"));
        log.error(sMd5 + "[�쳣]����·����" + mp.get("sPath"));
        log.error(sMd5 + "[�쳣]�쳣��ʾ��" + mp.get("sSelfExceptionMessage"));
        log.error(sMd5 + "[�쳣]�쳣���ƣ�" + mp.get("sSysExceptionName") );
        log.error(sMd5 + "[�쳣]�쳣���飺" + mp.get("sSysExceptionMessage"));
        log.error(sMd5 + "[�쳣]ִ�нű���[" + mp.get("sDataSource") + "]" +  mp.get("sSql") );
        log.error(sMd5 + "[�쳣]��ǰӦ�ã�"+ SYSTEM_NAME);
        log.error(sMd5 + "[�쳣]��ǰҳ�棺"+ mp.get("sReferer"));
	}
	
	/**JDBC��־�����չ����
	 * @param log  ��־����
	 * @param sMd5  ��־��ʶ��
	 * @param map ��������
	 */
	public void logSQLExcuteBefore(Logger log,String sMd5, Map map){
		log.info(sMd5 + "[JDBC]ִ��·����" + map.get("path"));
        log.info(sMd5 + "[JDBC]ִ�нű���["+ map.get("sqlInfo"));
	}
	
	/**JDBC��־�����չ����
	 * @param log  ��־����
	 * @param sMd5  ��־��ʶ��
	 * @param map ��������
	 */
	public void logSQLExcuteAfter(Logger log,String sMd5, Map map){
		String timeDiffResult =  map.get("excuteCostTime") + "����" ;
        log.info(sMd5 + "[JDBC]Ӱ��������" + map.get("rowCount") + "��");
        log.info(sMd5 + "[JDBC]ִ��ʱ�䣺" + timeDiffResult);
        log.info(sMd5 + "[JDBC]��ǰӦ�ã�"+ SYSTEM_NAME);
	}
	
	/**�˵������־�����չ����
	 * @param log  ��־����
	 * @param sMd5  ��־��ʶ��
	 * @param map ��������
	 */
	public void logMenuClick(Logger log,String sMd5, Map mp){
		log.info(sMd5 + "[�˵�]�û�����[" + mp.get("iDomainId") + "-"
                + mp.get("sDomainName") + "]");
        log.info(sMd5 + "[�˵�]�û����ţ�[" + mp.get("iDeptId") + "-"
                + mp.get("sDeptName") + "]");
        log.info(sMd5 + "[�˵�]�û���Ϣ��[" + mp.get("iStaffId") + "-"
                + mp.get("sStaffAccount") + "-" + mp.get("sStaffName") + "]");
        log.info(sMd5 + "[�˵�]����IP��" + mp.get("sIpAddress"));
        log.info(sMd5 + "[�˵�]����·����" +  mp.get("sUrl"));
        log.info(sMd5 + "[�˵�]��ǰӦ�ã�"+ SYSTEM_NAME);
	}
	
	/**�û���¼��־�����չ����
	 * @param log  ��־����
	 * @param sMd5  ��־��ʶ��
	 * @param map ��������
	 */
	public void logLoginIn(Logger log,String sMd5, Map map){
		log.info(sMd5 + "[��¼]�û��ʺţ�" + map.get("userName") );
        log.info(sMd5 + "[��¼]SessionID��" + map.get("sessionID") );
        log.info(sMd5 + "[��¼]��������ͣ�" + map.get("sBrowserType") );
        log.info(sMd5 + "[��¼]����IP��" + map.get("sLoginIP"));
        log.info(sMd5 + "[��¼]��ǰӦ�ã�"+ SYSTEM_NAME);
	}
	
	/**�û��ǳ���־�����չ����
	 * @param log
	 * @param sMd5
	 * @param map
	 */
	public void logLoginOut(Logger log,String sMd5, Map map){
		log.info(sMd5 + "[�˳�]�û��ʺţ�" + map.get("userName") );
        log.info(sMd5 + "[�˳�]SessionID��" + map.get("sessionID") );
        log.info(sMd5 + "[�˳�]��ǰӦ�ã�"+ SYSTEM_NAME);
	}
	
	
}
