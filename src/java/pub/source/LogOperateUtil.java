package pub.source;


import com.opensymphony.xwork2.ActionContext;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import pub.dbDialectFactory.Dialect;
import pub.dbDialectFactory.DialectFactory;
import pub.servlet.ConfigInit;
import util.*;
import vo.deptMgr.StaffRegister;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import util.MD5Tool;

/**
 * ������־���������
 *
 * @author gaotao 2011-08-18
 *
 */
public class LogOperateUtil {
    /**��־�����������*/
	private static Logger log = Logger.getLogger(LogOperateUtil.class);
	/**�����Ѿ�����ƥ��Ĳ˵����ӣ�����ÿ�β˵�����Ȩ�ޱ�˵�����ƥ��**/
	private static Map<String, String> matchedMenu = new HashMap<String, String>();
	/**��ȫ����ģ������Դ	 */
	private static String safeMgrDataSource = ConfigInit.Config.getProperty(
	           "safeManagerDataSource", ""); 
	
	private static String SYSTEM_NAME = ConfigInit.Config.getProperty("SYSTEM_NAME");
	/**
	 * ��ȡ��������·��
	 * @return ��������·���ַ���
	 */
	public static String logCallStack() {
		Throwable ste = new Throwable();
		return logCallStack(ste);
	}

	/**��ȡ��������·��
	 * @param t �쳣����
	 * @return ��������·���ַ���
	 */
	public static String logCallStack(Throwable t){
		StringBuffer CallStack = new StringBuffer();
		if(t != null){
			StackTraceElement[] ste = t.getStackTrace();
			for (int i = 0; i < ste.length; i++) {
				String pth = ste[i].toString();
				if (pth.startsWith("pub.source.LogOperateUtil.log") 
						|| pth.startsWith("pub.source.DatabaseUtil.before")
						|| pth.startsWith("pub.servlet")
						|| pth.startsWith("util.commonModule.messageSource")
						|| (!pth.startsWith("com.catt")
						&& !pth.startsWith("pub.")
						&& !pth.startsWith("util.")
						&& !pth.startsWith("vo."))){
					continue;//break->continue;  by gt 2013/2/20
				}
				CallStack.append(ste[i].toString() + " | ");
			}
			ste = null;
		}
		return CallStack.toString();
	}
	
    /**��־��¼SQLִ��ǰ����Ϣ
     * @param sql ִ�е�SQL���
     * @param sMd5 ��ǰ��־��MD5��ʶ
     * @param domain ��ǰ���ӵ�����Դ
     * @return ����ִ��·��
     */
    public static String logSQLBefor(String sql, String sMd5, String domain) {
    	String path = logCallStack().toString();
    	String jdbcLog = ConfigInit.Config.getProperty("jdbcLog", "0");
        if (!("on".equals(jdbcLog) || "1".equals(jdbcLog))) {
            return path;
        }
        
        Map<String, String> sqlExcuteBeforeInfo = new HashMap<String, String>();
        sqlExcuteBeforeInfo.put("path", path);
        sqlExcuteBeforeInfo.put("sqlInfo", (StringUtil.checkStr(domain) ? domain
        		: DialectFactory.getDefaultDatasrc()) 
                + "]" + sql);
        LogPrintExt.getInstance().logSQLExcuteBefore(log, sMd5, sqlExcuteBeforeInfo);
        
        return path;
    }

    /**��־��¼SQLִ�к����Ϣ
     * @param sql ִ�е�SQL���
     * @param sMd5 ��ǰ��־��MD5��ʶ
     * @param timeBefore ִ��ǰ��¼��ʱ��
     * @return ִ��ʱ��
     */
    public static Long logSQLAfter(String sql, String sMd5, long timeBefore) {
    	long timeDiff = System.currentTimeMillis() - timeBefore;
    	String jdbcLog = ConfigInit.Config.getProperty("jdbcLog", "0");
        if (!("on".equals(jdbcLog) || "1".equals(jdbcLog))) {
            return timeDiff;
        }
        
        Map map = new HashMap<String, String>();
        map.put("excuteCostTime", timeDiff);
        map.put("rowCount", "0");//�÷������ᴫӰ��������Ĭ����0 tangyj
        LogPrintExt.getInstance().logSQLExcuteAfter(log, sMd5, map);
        
        return timeDiff;
    }
    
    /**��־��¼SQLִ�к����Ϣ
     *  ����һ����������¼Ӱ������  modify by tangyujun 2013-04-11
     * @param sql ִ�е�SQL���
     * @param sMd5 ��ǰ��־��MD5��ʶ
     * @param timeBefore ִ��ǰ��¼��ʱ��
     * @param rowCount Ӱ�����ݵ�����
     * @return ִ��ʱ��
     */
    public static Long logSQLAfter(String sql, String sMd5,long timeBefore, long rowCount) {
    	long timeDiff = System.currentTimeMillis() - timeBefore;
    	String jdbcLog = ConfigInit.Config.getProperty("jdbcLog", "0");
        if (!("on".equals(jdbcLog) || "1".equals(jdbcLog))) {
            return timeDiff;
        }
        
        Map map = new HashMap<String, String>();
        map.put("excuteCostTime", timeDiff);
        map.put("rowCount", rowCount);
        LogPrintExt.getInstance().logSQLExcuteAfter(log, sMd5, map);
        
        return timeDiff;
    }

    /**����SQL�쳣��Ϣ����־��¼����ӡ�����쳣
     * @param e �쳣����
     * @param domain ����Դ��ʶ
     * @param sql ִ�е�SQL���
     * @param path ִ��·��
     * @return ��װ���BaseRuntimeException����
     */
    public static BaseRuntimeException logSQLError(Exception e, String domain,
            String sql, String path) {
        String path1 = logCallStack(e); //�쳣����ʵ·��
        
        String msg = "�ײ������ݿ⽻��ʱ�����쳣!";
        domain = StringUtil.checkStr(domain) ? domain
                : DialectFactory.getDefaultDatasrc();
        Map<String, String> m = new HashMap<String, String>();
        m.put("sDataSource", domain);
        m.put("sSql", sql);
        m.put("sPath", StringUtil.checkStr(path1) ? path1 : path);
        m.put("exceptionMessage", msg); 
        throw new BaseRuntimeException(e, m);
    }

    /**��ִ�г�ʱһ��ʱ���SQL����¼
     * @param sMd5 ��ǰ��־��MD5��ʶ
     * @param sSql ִ�е�SQL���
     * @param iCostTime ִ��ʱ��
     * @param iRowCount Ӱ�����ݵ�����
     * @param sDataSource ����Դ��ʶ
     * @param sPath ����·��
     */
    @Deprecated
    public static void logSQLToDb(String sMd5, String sSql, long iCostTime,
            int iRowCount, String sDataSource, String sPath) {
        String SQL_LOG_DB = ConfigInit.Config.getProperty(
        		"SQL_LOG_DB", "off");// �Ƿ����
        int SQL_LOG_TIME = StringUtil.toInt(ConfigInit.Config.getProperty(
                "SQL_LOG_TIME", "1"));// SQLִ��ʱ��
        if (("on".equals(SQL_LOG_DB) ||  "1".equals(SQL_LOG_DB)) && iCostTime > SQL_LOG_TIME) {//
        	sDataSource = StringUtil.checkStr(sDataSource) ? sDataSource
                    : DialectFactory.getDefaultDatasrc();
            Dialect dia = DialectFactory.getDialect();
            String primarykeys = getKeyId();
            StringBuffer sb = new StringBuffer();
            sb.append("insert into tbJdbcLog(").append("iId,").append(
                    "sMd5,sSql,iCostTime,iRowCount,sDataSource").append(
                    ",sPath,dInsertTime,sSystemName) values (").append(primarykeys).append(
                    ",'").append(sMd5).append("',?,").append(iCostTime)
                    .append(",").append(iRowCount).append(",'").append(
                    sDataSource == null ? "" : sDataSource).append("','")
                    .append(sPath).append("',").append(dia.getDate()).append(",'").append(SYSTEM_NAME)
                    .append("')");
            ArrayList data = new ArrayList();
            if(sSql != null && sSql.length() > 2000){
            	sSql = sSql.substring(0, 2000)+"...�����ֶ���󳤶�(2000�ֽ�)���ѱ���ȡ��";
    		}
            data.add(sSql);
            // ����
            save(sb.toString(), data);
        }
    }

    /**���쳣��־�������־�ļ�
     * @param mp �쳣��־��Ϣ
     */
    private static void logException(Map<String, String> mp) {
    	String exceptionLog = ConfigInit.Config.getProperty(
    			"exceptionLog", "0");// �Ƿ����
        // ������ò������쳣��־
        if (!("on".equals(exceptionLog) || "1".equals(exceptionLog))) {
            return;
        }
        
    	String sMd5 = mp.get("sMd5");
        LogPrintExt.getInstance().logException(log, sMd5, mp);
        
        if(LogOperateUtil.isLogRuntime()){
        	logExceptionToDb(mp);
        }
        
    }
    
	/**���ú��¼�쳣��־
	 * �÷�����Ҫ���ڳ������Լ�catch�쳣��¼�쳣��־�������жϳ��������
	 * @param exception �쳣����
	 * @param message �Զ����쳣������Ϣ
	 * @return ���ؿͻ�����ʾ��Ϣ
	 */
	public static String logException(Throwable exception, String message, HttpServletRequest req) {
		String sMd5 = getUUID(); //MD5Tool.getMD5String();
		
		//���ص�������ʾ�쳣����
		String sSysExceptionMessage = "";
		String mKey = null; //��I18NConfig.properties�����õļ�
		String systemRunMode = ConfigInit.Config.getProperty("systemRunMode", "1");
		String[] sArgs = null; //��Ҫ�滻������
		//�쳣·��������Դ��ִ��sql��ϵͳ�쳣���쳣��Ϣ���쳣��ʾ
		String sPath = "", sDataSource = "", sSql = ""
			, sSysExceptionName = "", sSelfExceptionMessage = "", sqlExcetpionMsg = "";
		
		/**
		* ����ײ����÷�����Ʒ�������󣬲����÷���ʱ��
		* �䲻��ֱ�ӵõ���ԭʼ���쳣��
		* ���Ǳ���װ��һ���java.lang.reflect.InvocationTargetException
		* ��ʱͨ��getCause()�����õ���ԭʼ���쳣
		*/
		Throwable exception1 = (exception != null && 
				!(exception instanceof BaseRuntimeException)) ? 
						exception.getCause():exception;
		exception = (exception1 == null) ? exception : exception1;
		Map m = null;
		if(exception instanceof BaseRuntimeException){
			BaseRuntimeException bre = (BaseRuntimeException)exception;
			m = bre.getMArgs();
			if(m != null){
				sPath = (String)m.get("sPath"); 
				sDataSource = (String)m.get("sDataSource");
			    sDataSource = StringUtil.checkStr(sDataSource) ? sDataSource
	                    : DialectFactory.getDefaultDatasrc();
				sSql = (String)m.get("sSql"); 
				sSelfExceptionMessage = (String)m.get("exceptionMessage");  
				mKey = (String)m.get("i18n"); 
				//�������Ϊ��ӡ�����쳣������׼��
				m.remove("sPath");
				m.remove("sDataSource");
				m.remove("sSql");
				m.remove("exceptionMessage");
				m.remove("i18n");
			}
	        //���������ڿͻ�����ʾ�쳣������Ϣ
	        mKey = StringUtil.checkStr(mKey) ? mKey : bre.getI18nKey();
	        sArgs = bre.getSArgs();
			sSelfExceptionMessage = I18nConfig.getInstance(
					systemRunMode).getValue(mKey, sArgs, sSelfExceptionMessage);
			
			//��ԭʼ�쳣����������
			exception = bre.getCause();
			if(exception != null){
				sSysExceptionName = exception.getClass().getName();
				sSysExceptionMessage = exception.getMessage();
			}
		}else{
			sSysExceptionName = exception.getClass().getName();
			sSysExceptionMessage = exception.getMessage();
		}
		/**
		 * �Զ����쳣��Ϣ�����ȼ���
		 * ������messageΪ׼�������Throwable���Ϊ׼�������ϵͳ�����ļ��ڵ�Ϊ׼
		 * */
		message = StringUtil.checkObj(message)?"���쳣����Ϊ����δ�жϳ�������-->"+message : null;
		sSelfExceptionMessage =
			   StringUtil.checkStr(message) ? message : sSelfExceptionMessage;
		sSelfExceptionMessage =
				StringUtil.checkStr(sSelfExceptionMessage) ? sSelfExceptionMessage :
					(I18nConfig.getInstance(systemRunMode).getValue(null, null, null));
		
		//�쳣��ϸ����
		if( m == null ){m = new HashMap();}
		sSysExceptionMessage = StringUtil.toString(
				sSysExceptionMessage).replace("\n", " | ").replace("\r", " | ");
		m.put("sysExceptionMessage", sSysExceptionMessage);
		sSysExceptionMessage = JackJson.getBasetJsonData(m);
		//�쳣����·���ٴ�ȷ��
		sPath = (StringUtil.checkStr(sPath))? sPath : logCallStack(exception);

		Map<String, String> mp = new HashMap<String, String>();
		mp.put("sMd5", sMd5);
		mp.put("sPath", sPath);
		if(sSelfExceptionMessage != null && sSelfExceptionMessage.length() > 450){
			sSelfExceptionMessage = sSelfExceptionMessage.substring(0, 450)+"...�����ֶ���󳤶�(450�ֽ�)���ѱ���ȡ��";
		}
		mp.put("sSelfExceptionMessage", sSelfExceptionMessage);
		mp.put("sSysExceptionName", sSysExceptionName);
		mp.put("sSysExceptionMessage", sSysExceptionMessage);
		mp.put("sDataSource", sDataSource);
	    mp.put("sSql", sSql);
		
		
		HttpServletRequest request = null;
		if(req == null){
			ActionContext act = ActionContext.getContext();
			request = (act == null)? null: (HttpServletRequest)act.get(ServletActionContext.HTTP_REQUEST); 
		}else{
			request = req;
		}
		
		if(request != null){
			//��Ա��Ϣ
			String iStaffId = SessionUtil.getAttribute("USERID",
					request.getSession()) != null ? (String)SessionUtil.getAttribute(
							"USERID", request.getSession()):"-1";
			String sStaffAccount = SessionUtil.getAttribute("USERNAME", 
					request.getSession()) != null?(String)SessionUtil.getAttribute(
							"USERNAME", request.getSession()):"δ��¼�����˺�";
			String sStaffName = SessionUtil.getAttribute("USERCNNAME", 
					request.getSession()) != null ? (String)SessionUtil.getAttribute(
							"USERCNNAME", request.getSession()):"δ��¼��������";
			
			//����url,��ʼ����ʱ��,ģ��ID
			String sUrl = request.getAttribute("sUrl") == null ? "" : (String)request.getAttribute("sUrl");
			long iCostTime = 0l;
			if(request.getAttribute("beginTime") != null){//�����������־����������������û�е�
			 	iCostTime = System.currentTimeMillis() - (Long)request.getAttribute("beginTime");
			}
			
		    //�������в�����ֵ����500�ͽض�
			Map<String, String> reqeustParam = RequestUtil.getMapByRequest(request);
			truncateParam(reqeustParam, 500);
			//ͨ��action���������������Ȩ�ޱ��ܰ�ť����ƥ��
			String actionMethod = (String) request.getAttribute("actionMethod");
	        Map<String, String> rightInfoMap = createRightInfo(actionMethod,request.getHeader("referer"), reqeustParam);
		    //���ù�������
		    mp.put("iActionId", rightInfoMap.get("iActionId"));
			mp.put("iRightId", rightInfoMap.get("iRightId"));
			mp.put("iBtnRightId", rightInfoMap.get("iBtnRightId"));
			mp.put("sContent", StringUtil.toString(rightInfoMap.get("sContent")));
		    mp.put("rightInfo", JackJson.getBasetJsonData(rightInfoMap));
		    
		    mp.put("iStaffId", iStaffId);
		    mp.put("sStaffAccount", sStaffAccount);
		    mp.put("sStaffName", sStaffName);
		    mp.put("sParams",  JackJson.getBasetJsonData(reqeustParam));
		    mp.put("iCostTime", iCostTime + "");
		    mp.put("sUrl", sUrl);
		    mp.put("sReferer", request.getHeader("referer"));
		}
	    //�쳣��Ϣ�������־�ļ������ݿ�
    	logException(mp);
		//����ģʽ�£���ӡ�쳣
		if(exception != null && "1".equals(systemRunMode)){
	        exception.printStackTrace();
	        sSelfExceptionMessage += ("#%"+m.get("sysExceptionMessage")) + ("#%"+sPath) + ("#%"+sSysExceptionName);
	    }
		
		return sSelfExceptionMessage;
	}
    
	/**
	 * ��¼�쳣��־
	 * �÷���ֻ��Exception.jsp�����õ��������ط����Ҫ���쳣��־��������ط���������������
	 * @param exception �쳣����
	 * @return �Զ�����쳣��Ϣ
	 */
	public static String logException(Throwable exception) {
		return logException(exception, null, null);
	}
	
	/**
	 * ��¼�쳣��־ add by qiaoqd
	 * �����ⲿ����logException(Throwable exception, String msg)
	 * @param exception �쳣����
	 * @return �Զ�����쳣��Ϣ
	 */
	public static String logException(Throwable exception, String msg) {
		return logException(exception, msg, null);
	}
	
	/**
	 * ��¼�쳣��־ add by qiaoqd
	 * �÷�����Ҫ���Exception.jspҳ�洦��������쳣ʹ��.
	 * @param exception �쳣����
	 * @param req �������
	 * @return �Զ�����쳣��Ϣ
	 */
	public static String logException(Throwable exception, HttpServletRequest req) {
		return logException(exception, null, req);
	}
	
    /** �쳣��־�������
     *  ��Ҫ��config.properties��ActionRelatedToRight����Ϊ1
     * @param mp �쳣��־��Ϣ
     */
	@Deprecated
    public static void logExceptionToDb(Map<String, String> mp) {
        String ERR_LOG_DB = ConfigInit.Config.getProperty("ERR_LOG_DB", "off");// �Ƿ����
        // ������ò������쳣��־
        if (!("on".equals(ERR_LOG_DB) || "1".equals(ERR_LOG_DB))) {
            return;
        }

        Dialect dia = DialectFactory.getDialect();
        String primarykeys = getKeyId();
        StringBuffer sb = new StringBuffer();
        String refer = mp.get("sReferer");
        refer=StringUtil.checkStr(refer)?refer:"";
        sb.append("insert into tbExceptionLog(").append("iId,");
        sb.append("sMd5,dThrowTime,iStaffId,sStaffAccount,sStaffName,");
        sb.append("sSysExceptionName,sSysExceptionMessage,");
        sb.append("sSelfExceptionMessage,sPath,sParams,");
        sb.append("sDataSource,sSql,sUrl,iActionId,");
        sb.append("iRightId,iBtnRightId,sContent,");
        sb.append("iCostTime,sSystemName,sReferer) values(");
        sb.append(primarykeys);
        sb.append(",'").append(mp.get("sMd5")).append("',");
        sb.append(dia.getDate()).append(",");
        sb.append(mp.get("iStaffId")).append(",");
        sb.append("'").append(mp.get("sStaffAccount")).append("',");
        sb.append("'").append(mp.get("sStaffName")).append("',");
        sb.append("'").append(mp.get("sSysExceptionName")).append("',?,?,");
        sb.append("'").append(mp.get("sPath")).append("',?,");
        sb.append("'").append(mp.get("sDataSource")).append("',?,");
        sb.append("'").append(mp.get("sUrl")).append("',");
        sb.append(mp.get("iActionId")).append(",?,?,?,");
        sb.append(mp.get("iCostTime")).append(",'").append(SYSTEM_NAME).append("','")
        .append(refer.length()>255?refer.substring(0,255):refer).append("')");
        ArrayList data = new ArrayList();
        data.add(mp.get("sSysExceptionMessage"));
        data.add(mp.get("sSelfExceptionMessage"));
        data.add(mp.get("sParams").toString());
        data.add(mp.get("sSql"));
        data.add(mp.get("iRightId"));
        data.add(mp.get("iBtnRightId"));
        data.add(StringUtil.toString(mp.get("sContent")));
        
        // ����
        save(sb.toString(), data);
    }
	
    /**�˵������־�������־�ļ�  tangyj 2013-05-27
     * @param mp �˵������־��Ϣ
     */
    public static void logMenuClick(Map<String, String> mp) {
    	String menuClickLog = ConfigInit.Config.getProperty("menuClickLog", "0");
        if (!("on".equals(menuClickLog) || "1".equals(menuClickLog))) {
            return;
        }
    	
    	String sMd5 = getUUID(); //MD5Tool.getMD5String();;
        LogPrintExt.getInstance().logMenuClick(log, sMd5, mp);
        
        if(isLogRuntime()){
        	mp.put("sMd5", sMd5);
        	logMenuClickToDb(mp);
        }
    }
    
    /**������ֱ�Ӵ����ݿ�
     * @param mp keyֵ���£�
     * iDomainId
     * sDomainName
     * iDeptId
     * sDeptName
     * iStaffId
     * sStaffAccount
     * sStaffName
     * sIpAddress
     * url
     */
    public static void logMenuClickToDb(Map<String, String> mp) {
    	Dialect dia = DialectFactory.getDialect();
        String primarykeys = getKeyId();
        StringBuffer sb = new StringBuffer();
        
        String tableName = "tbLogMenuClick_"
                + DateUtil.parseToString(new Date(), "yyyyMM");
        // ƴSQL
        sb.append("insert into ").append(tableName);
        sb.append("(").append("iId,sMd5,");
        sb.append("iDomainId,sDomainName,iDeptId,sDeptName,");
        sb.append("iStaffId,sStaffAccount,sStaffName,");
        sb.append("sIpAddress,sUrl,iRightId,dLogTime,sSystemName)");
        sb.append(" values(");
        sb.append(primarykeys).append(",'");
        sb.append(mp.get("sMd5")).append("',");
        sb.append(mp.get("iDomainId")).append(",");
        sb.append("'").append(mp.get("sDomainName")).append("',");
        sb.append(mp.get("iDeptId")).append(",");
        sb.append("'").append(mp.get("sDeptName")).append("',");
        sb.append(mp.get("iStaffId")).append(",");
        sb.append("'").append(mp.get("sStaffAccount")).append("',");
        sb.append("'").append(mp.get("sStaffName")).append("',");
        sb.append("'").append(mp.get("sIpAddress")).append("',");
        sb.append("'").append(mp.get("sUrl")).append("',");
        sb.append("?,");
        sb.append(dia.stringToDatetime(DateUtil.parseToString(new Date(), DateUtil.yyyyMMddHHmmss)));
        sb.append(",'").append(SYSTEM_NAME).append("')");
        ArrayList data = new ArrayList();
        data.add(Long.parseLong(mp.get("iRightId")));
        // ����
        save(sb.toString(), data);
    	
    }
    
	/**
	 * ��¼�˵���־
	 * @param request HttpServletRequest����
	 */
	public static void logMenuClick(HttpServletRequest request) {
		String menuClickLog = ConfigInit.Config.getProperty("menuClickLog", "0");
        if (!("on".equals(menuClickLog) || "1".equals(menuClickLog))) {
            return;
        }
        
     	//�첽���󲻻�ǲ˵������־
		if(RequestUtil.isAjaxRequest(request)){
			return;
		}
		
		String URLRegExp = (String) ConfigInit.Config.getProperty("URLRegExp", "");
		if(!StringUtil.checkStr(URLRegExp)){
			return;
		}
		
		String urlValue = RequestUtil.mergeURIAndParma(request);// ȡ����·��
		
		//����URL���й��ˣ���ֹ��ͼƬ����Դ����Ҳ�����ǲ˵����
		Pattern pattern = Pattern.compile(URLRegExp);
		Matcher matcher = pattern.matcher(urlValue);
		if (!matcher.find()){
			return;
		 }
		
		//��¼��־�ļ�
		HttpSession session = request.getSession();
		Map<String, String> mp = getUserMsg(session);// ���û���Ϣ
		mp.put("sIpAddress", RequestUtil.getIpAddr(request));// ȡ�ͻ��˷���IP
		mp.put("sUrl", urlValue);// ȡ����·��
		mp.put("queryStr", request.getQueryString());// ���������Ĳ���
		
		if(isLogRuntime()){
			//ͨ������������Ȩ�ޱ�˵����бȽ�
			String iRightId = null;
			//ֱ�Ӵӻ�����ȡ
			if(matchedMenu.containsKey(urlValue)){
				iRightId = matchedMenu.get(urlValue);
	        }else{
	        	iRightId = getRightIdByUrl(urlValue);
	        	matchedMenu.put(urlValue, iRightId);
	        }
			
			if(!StringUtil.checkStr(iRightId)){
				return;
			}
			mp.put("iRightId", iRightId);// ���������Ĳ���
		}
		LogOperateUtil.logMenuClick(mp);
	}
	
	private  static String getRightIdByUrl(String url){
		//��ʼ��Ȩ������
		List<Map<String, String>> rightList  = ConfigInit.rightMenuList;
		if(url == null || url.length() == 0){return null;}
		
		//���paramNamesֵһֱΪ�����ʾֻƥ��URL����ƥ���������
		String[] paramNames = null;
		String paramStr = RequestUtil.getUrlAndParam(url)[1];
		Map<String, String> paramMap = RequestUtil.getParamMap(paramStr, "&", "=");
		
		//��Ըڹܰ�����
		if(url.indexOf("controlBus!getPage") != -1){
			String filedName = "";
			if(StringUtil.checkStr(paramMap.get("Fd_sTableName"))){
				filedName = "Fd_sTableName";
			}
			if(StringUtil.checkStr(paramMap.get("Fd_iFormId"))){
				filedName = "Fd_iFormId";
			}
			if(filedName == null){//���URL��û�иò�������ֱ����������
				return null;
			}
			paramNames = new String[2];//{"Fd_opt",filedName};
			paramNames[0] = "Fd_opt";
			paramNames[1] = filedName;
		}
		//�������ͨ�ñ�
		else if(url.indexOf("createPage!getPage.action") != -1){
			String filedName = null;
			if(StringUtil.checkStr(paramMap.get("S_PAGE_ID"))){
				filedName = "S_PAGE_ID";
			}
			if(StringUtil.checkStr(paramMap.get("S_TABLE_ID"))){
				filedName = "S_TABLE_ID";
			}
			if(filedName == null){//���URL��û�иò�������ֱ����������
				return null;
			}
			paramNames = new String[1];
			paramNames[0] = filedName;
		}else{//��Ҫ�������URL�����в���
			paramNames = new String[0];
		}
		
		String iRight = null;
		for(Map<String, String> rightMap : rightList){
			if(rightMap.get("sURL") != null 
					&& RequestUtil.compareURL(url,rightMap.get("sURL"), paramNames)){
				iRight = rightMap.get("iRight");
				break;
			}
		}
		return iRight;
	}
    
    /**������־�������־�ļ� tangyj 2013-03-31
     * @param mp ������־��Ϣ
     */
    public static void logOperate(Map<String, String> mp) {
    	String operateLog = ConfigInit.Config.getProperty("operateLog", "0");
        if (!("on".equals(operateLog) || "1".equals(operateLog))) {
            return;
        }
    	
    	String sMd5 = getUUID(); //MD5Tool.getMD5String();
    	LogPrintExt.getInstance().logOperate(log, sMd5, mp);
        
        if(LogOperateUtil.isLogRuntime()){
        	mp.put("sMd5", sMd5);
			LogOperateUtil.logOperateToTb(mp);
		}
    } 
    
    /**��¼������־
	 * @param request request����
	 */
	public static void logOperate(HttpServletRequest request) {
		String operateLog = ConfigInit.Config.getProperty("operateLog", "0");
        if (!("on".equals(operateLog) || "1".equals(operateLog))) {
            return;
        }
        HttpSession session = request.getSession();
		long endTime = System.currentTimeMillis();//����ʱ��
		Map<String, String> mp = getUserMsg(session);// ���û���Ϣ
		
		//�������в�����ֵ����500�ͽض�
		Map<String, String> reqeustParamMap = RequestUtil.getMapByRequest(request);
		truncateParam(reqeustParamMap, 500);
		//ͨ��action���������������Ȩ�ޱ��ܰ�ť����ƥ��
	 	String actionMethod = (String) request.getAttribute("actionMethod");
        Map<String, String> rightInfoMap = createRightInfo(actionMethod, request.getHeader("referer"), reqeustParamMap);
        //���û��ƥ�����򲻼���־
        if(rightInfoMap == null || rightInfoMap.size() == 0 || "-1".equals(rightInfoMap.get("isRelated"))){
        	return;
        }
        //���ù�������
        mp.put("iActionId", rightInfoMap.get("iActionId"));
		mp.put("iRightId", rightInfoMap.get("iRightId"));
		mp.put("iBtnRightId", rightInfoMap.get("iBtnRightId"));
		mp.put("sContent", StringUtil.toString(rightInfoMap.get("sContent")));
		
		mp.put("sIpAddress", RequestUtil.getIpAddr(request));// ȡ�ͻ��˷���IP
		mp.put("sUrl", RequestUtil.mergeURIAndParma(request));// ȡ����·��
		
		//ȡ�������
		mp.put("sParams", JackJson.getBasetJsonData(reqeustParamMap));
		mp.put("rightInfo", JackJson.getBasetJsonData(rightInfoMap));
		// ���㴦��ʱ��(����)
		mp.put("iCostTime", (endTime - (Long)request.getAttribute("beginTime")) + "");
		mp.put("sReferer", request.getHeader("referer"));
		
		LogOperateUtil.logOperate(mp);
		
	}
	
	/**����action��ȡ����ģ����Ϣ
	 * @param actionMethod action�༰������
	 * @param requestParamMap ������ز���
	 * @param referer �������ڽ����URL
	 * @return ƥ���Ľ����Ϣ
	 */
	private static  Map<String, String>  createRightInfo(
				String actionMethod, String referer, Map<String, String> requestParamMap){
        String actionRelatedToRight = ConfigInit.Config.getProperty("ActionRelatedToRight", "0");
		//��Ҫ��ǰ����Ȩ�޽��й���
        Map<String, String> rightParamMap = new HashMap<String, String>();
        if ("1".equals(actionRelatedToRight) || "on".equals(actionRelatedToRight)){
        	Map<String, String> matchResult = null;
        	Long iRelType = ConfigInit.actionRel.get(actionMethod);
    		if(iRelType != null){
    			//Map���ƣ����������ڽ���URL�Ĳ���Ҳ�ӵ�Map��
            	String paramStr = RequestUtil.getUrlAndParam(referer)[1];
        		Map<String, String> paramMap = RequestUtil.getParamMap(paramStr, "&", "=");
        		paramMap.putAll(requestParamMap);
    			//LogOperateMapping logOperateMapping = LogOperateMapping.getInstanceList().get(iRelType);
    			//matchResult = logOperateMapping.match(actionMethod, paramMap);
    		}
			if(matchResult != null){
				rightParamMap.put("isRelated", "1");//1�����ѹ���
				rightParamMap.put("iActionId", matchResult.get("IACTIONID"));
				rightParamMap.put("iRightId", matchResult.get("IRIGHTID"));
				rightParamMap.put("iBtnRightId", matchResult.get("IBTNRIGHTID"));
				rightParamMap.put("sContent", matchResult.get("SCONTENT") != null ? matchResult.get("SCONTENT"):"");
			}else{//����ʧ��
				rightParamMap.put("isRelated", "-1");//-1�������ʧ��
				rightParamMap.put("iActionId", "-1");
				rightParamMap.put("iRightId", "-1");
				rightParamMap.put("iBtnRightId", "-1");
				rightParamMap.put("sContent", "");
			}
		}else{//�ں�˹���
			rightParamMap.put("isRelated", "0");//0����δ����
			rightParamMap.put("actionMethod", actionMethod);//0����δ����
		}
        
        return rightParamMap;
	}
    
    /**������־ֱ�ӱ������
     * ��Ҫ��config.properties��ActionRelatedToRight����Ϊ1
     * @param mp ������־��Ϣ
     */
    @Deprecated
    public static void logOperateToTb(Map<String, String> mp) {
        Dialect dia = DialectFactory.getDialect();
        String primarykeys = getKeyId();
        StringBuffer sb = new StringBuffer();
        String refer = mp.get("sReferer");
        refer=StringUtil.checkStr(refer)?refer:"";
        
        String tableName = "tbLogManage_"
                + DateUtil.parseToString(new Date(), "yyyyMM");
        // ƴSQL
        sb.append("insert into ").append(tableName);
        sb.append("(").append("iId,");
        sb.append("iDomainId,sDomainName,iDeptId,sDeptName,");
        sb.append("iStaffId,sStaffAccount,sStaffName,iRoleId,sRoleName,");
        sb.append("sIpAddress,sUrl,sInputSearch,iActionId,");
        
        sb.append("sMD5,iRightId,iBtnRightId,sContent,");
        
        sb.append("iCostTime,dLogTime,sSystemName,sReferer) values(");
        sb.append(primarykeys).append(",");
        sb.append(mp.get("iDomainId")).append(",");
        sb.append("'").append(mp.get("sDomainName")).append("',");
        sb.append(mp.get("iDeptId")).append(",");
        sb.append("'").append(mp.get("sDeptName")).append("',");
        sb.append(mp.get("iStaffId")).append(",");
        sb.append("'").append(mp.get("sStaffAccount")).append("',");
        sb.append("'").append(mp.get("sStaffName")).append("',");
        sb.append(mp.get("iRoleId")).append(",");
        sb.append("'").append(mp.get("sRoleName")).append("',");
        sb.append("'").append(mp.get("sIpAddress")).append("',");
        sb.append("'").append(mp.get("sUrl")).append("',");
        sb.append("?,").append(mp.get("iActionId")).append(",");
        
        sb.append("'").append(mp.get("sMd5")).append("',");
    	sb.append(mp.get("iRightId")).append(",");
    	sb.append(mp.get("iBtnRightId")).append(",");
    	sb.append("'").append(StringUtil.toString(mp.get("sContent"))).append("',");
    	
        sb.append(mp.get("iCostTime")).append(",");
        sb.append(dia.stringToDatetime(DateUtil.parseToString(new Date(), DateUtil.yyyyMMddHHmmss)));
        sb.append(",'").append(SYSTEM_NAME).append("','").append(refer.length()>255?refer.substring(0, 255):refer).append("')");
        ArrayList data = new ArrayList();
        data.add(mp.get("sParams").toString());
        // ����
        save(sb.toString(), data);
    }
   
    /**
     * �������������޸����롢���������޸�ɾ������¼��־
     * @author tanjianwen 2012-5-22
     * @param mp ��־��Ϣ
     */
    public static void logOperationToDb(Map mp) {
        String OPT_LOG_DB = 
        	ConfigInit.Config.getProperty("OPT_LOG_DB", "off").trim();// �Ƿ����
        // ������ò����������־
        if (!("on".equals(OPT_LOG_DB)) && !("1".equals(OPT_LOG_DB))) {
            return;
        }
        Dialect dia = DialectFactory.getDialect();
        String primarykeys = getKeyId();
        StringBuffer sb = new StringBuffer();
        sb.append("insert into tbOsOperationNote(").append("iOptId,");
        sb.append("iOptObjId,iOptObjType,iOptType,sOptJson,sOptMsg,");
        sb.append("sIP,iUserId,sUserName,iUserDeptId,sUserDeptName,");
        sb.append("iUserDomainId,sUserDomainName,dOptDate) values(");
        sb.append(primarykeys);
        sb.append(",").append(mp.get("iOptObjId")).append(",");
        sb.append(mp.get("iOptObjType")).append(",");
        sb.append(mp.get("iOptType")).append(",?,");
        if (StringUtil.checkObj(mp.get("sOptMsg"))) {
            sb.append("'").append(mp.get("sOptMsg")).append("',");
        } else {
            sb.append("null,");
        }
        if (StringUtil.checkObj(mp.get("sIP"))) {
            sb.append("'").append(mp.get("sIP")).append("',");
        } else {
            sb.append("null,");
        }
        sb.append(mp.get("iUserId")).append(",");
        if (StringUtil.checkObj(mp.get("sUserName"))) {
            sb.append("'").append(mp.get("sUserName")).append("',");
        } else {
            sb.append("null,");
        }
        sb.append(mp.get("iUserDeptId")).append(",");
        if (StringUtil.checkObj(mp.get("sUserDeptName"))) {
            sb.append("'").append(mp.get("sUserDeptName")).append("',");
        } else {
            sb.append("null,");
        }
        sb.append(mp.get("iUserDomainId")).append(",");
        if (StringUtil.checkObj(mp.get("sUserDomainName"))) {
            sb.append("'").append(mp.get("sUserDomainName")).append("',");
        } else {
            sb.append("null,");
        }
        sb.append(dia.getDate()).append(")");
        ArrayList data = new ArrayList();
        if (StringUtil.checkObj(mp.get("sOptJson"))) {
            if (mp.get("sOptJson").toString().length() + 
            		StringUtil.getChineseCount(mp.get("sOptJson").toString())
            		> 4000) {
                data.add(StringUtil.getSubString(
                		mp.get("sOptJson").toString(), 0, 3980) 
                		+ "...���ݹ��������ض�!");
            } else {
                data.add(mp.get("sOptJson").toString());
            }
        } else {
            data.add("");
        }
        // ����
        save(sb.toString(), data);
    }

    /**
     * ����Ϊ��½��־��Ҫ�õ��ķ�����modify by ldc 2011-11-3
     * @param errCode ��¼״̬
     * @param userName ��¼������
     * @param sBrowserType ���������
     * @param sessionID ��¼SessionId
     * @param sLoginIP ��¼IP
     * @return  0: ����ʧ�� 1������ɹ�
     */
    public static int setLogin(String errCode, String userName,
    		String sBrowserType, String sessionID, String sLoginIP) {
    	 sBrowserType = 
         	StringUtil.checkStr(sBrowserType) ? sBrowserType : "";
        Dialect dialect = DialectFactory.getDialect();
        Date loginDate = new Date();
        //weblogic��sessionIDΪ54λ
        sessionID = sessionID.length() > 32? sessionID.substring(0,32):sessionID;
        //дһ���µĵ�¼��Ϣ
        if ("1".equals(ConfigInit.Config.getProperty("logonLog"))) {
        	String sMd5 = getUUID(); //MD5Tool.getMD5String();
        	//����ǵ�¼��Ϣ����Ҫд��־�ļ�
        	if("1".equals(errCode)){
        		Map<String, String> loginInfo = new HashMap<String, String>();
        		loginInfo.put("userName", userName);
        		loginInfo.put("sessionID", sessionID);
        		loginInfo.put("sBrowserType", sBrowserType);
        		loginInfo.put("sLoginIP", sLoginIP);
                
                LogPrintExt.getInstance().logLoginIn(log, sMd5, loginInfo);
        		
                if(LogOperateUtil.isLogRuntime()){
                	loginInfo.put("sMd5", sMd5);
                	logLoginToDb(loginInfo);
                }
                //1����ɹ�
                return 1;
        	}
        	//��¼ʧ�ܵ�������Ҫֱ�����
            String sql = "insert into tbLoginLog(sessionID,sLoginName" +
            		",iState,dLoginTime,sBrowserType,sLoginIP,sMd5,sSystemName) " +
            		" values('" + sessionID + "','" + userName +
            		"'," + errCode + "," + dialect.stringToDatetime(DateUtil.parseToString(loginDate, DateUtil.yyyyMMddHHmmss)) + 
            		",'" + sBrowserType + "','" + sLoginIP + "','"+sMd5+"','"+SYSTEM_NAME+"')";
            save(sql, null);
            return 1;
        }
        return 0;
    }
    
    /**
     * ��¼�ɹ�����ⷽ����map��key����
     *  userName
     *  sBrowserType
     *  sessionID
     * sLoginIP
     */
    public static void logLoginToDb(Map<String, String> params){
    	Dialect dialect = DialectFactory.getDialect();
    	Date loginDate = new Date();
    	//��¼�ɹ������ϴε�¼�Ƿ����˳���
    	//���û���˳���Ҫ�����εĵ�¼ʱ���¼Ϊ��һ�ε�¼�ĵǳ�ʱ��
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("select ");
		sbSQL.append(dialect.datetimeTostring("dLoginTime", null));
		sbSQL.append("  \"dLoginTime\" ");
		sbSQL.append(" from tbLoginLog where iState=1 and sLoginName = '");
		sbSQL.append(params.get("userName"));
		sbSQL.append("' and sSystemName='");
		sbSQL.append(SYSTEM_NAME);
		sbSQL.append("'");
		List notLogoutDate = DatabaseUtil.queryForList(sbSQL.toString(), null, safeMgrDataSource, null);
		if (notLogoutDate != null && notLogoutDate.size() > 0) {
			String lastLoginTimeStr = (String)((Map)notLogoutDate.get(0)).get("dLoginTime");
			Date lastLoginTime = DateUtil.parseToDate(lastLoginTimeStr, DateUtil.yyyyMMddHHmmss);
			long loginSeconds = (loginDate.getTime() - lastLoginTime.getTime()) / 1000;
			StringBuilder sbUpdateSQL = new StringBuilder();
			sbUpdateSQL.append("update tbLoginLog ");
			sbUpdateSQL.append(" set iState=2,dLoginOutTime=");
			sbUpdateSQL.append(dialect.stringToDatetime(DateUtil.parseToString(loginDate, DateUtil.yyyyMMddHHmmss)));
			sbUpdateSQL.append(",iLoginSeconds=");
			sbUpdateSQL.append(loginSeconds);
			sbUpdateSQL.append(" where iState=1 and sLoginName='");
			sbUpdateSQL.append(params.get("userName"));
			sbUpdateSQL.append("' and sSystemName='");
			sbUpdateSQL.append(SYSTEM_NAME);
			sbUpdateSQL.append("'");
			save(sbUpdateSQL.toString(), null);
			//DatabaseUtil.updateDateBase(sbUpdateSQL.toString());
		}
	  //��¼��¼�ɹ�������	
      String sql = "insert into tbLoginLog(sessionID,sLoginName" +
      		",iState,dLoginTime,sBrowserType,sLoginIP,sMd5,sSystemName) " +
      		" values('" + params.get("sessionID") + "','" + params.get("userName") +
      		"',1," + dialect.stringToDatetime(DateUtil.parseToString(loginDate, DateUtil.yyyyMMddHHmmss)) + 
      		",'" + params.get("sBrowserType") + "','" + params.get("sLoginIP") + "','"+params.get("sMd5") +"','"+SYSTEM_NAME+"')";
      save(sql, null);
      //DatabaseUtil.updateDateBase(sql);
    }
    
    /**��¼��¼��Ϣ
     * @param params keyֵ����
     * userName
     * sessionID
     */
    public static void logLogoutToDb(Map<String, String> params){
    	Dialect dialect = DialectFactory.getDialect();
    	Date logoutTime = new Date();
    	StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("select ");
		sbSQL.append(dialect.datetimeTostring("dLoginTime", null));
		sbSQL.append("  \"dLoginTime\" ");
		sbSQL.append(" from tbLoginLog where iState=1 and sLoginName = '");
		sbSQL.append(params.get("userName"));
		sbSQL.append("' and sessionID = '");
		sbSQL.append(params.get("sessionID"));
		sbSQL.append("' ");
		List notLogoutDate = DatabaseUtil.queryForList(sbSQL.toString(), null, safeMgrDataSource, null);
		if (notLogoutDate != null && notLogoutDate.size() > 0) {
			String lastLoginTimeStr = (String)((Map)notLogoutDate.get(0)).get("dLoginTime");
			Date lastLoginTime = DateUtil.parseToDate(lastLoginTimeStr, DateUtil.yyyyMMddHHmmss);
			long loginSeconds = (logoutTime.getTime() - lastLoginTime.getTime()) / 1000;
			StringBuilder sbUpdateSQL = new StringBuilder();
			sbUpdateSQL.append("update tbLoginLog ");
			sbUpdateSQL.append(" set iState=2,dLoginOutTime=");
			sbUpdateSQL.append(dialect.stringToDatetime(DateUtil.parseToString(logoutTime, DateUtil.yyyyMMddHHmmss)));
			sbUpdateSQL.append(",iLoginSeconds=");
			sbUpdateSQL.append(loginSeconds);
			sbUpdateSQL.append(" where iState=1 and sLoginName='");
			sbUpdateSQL.append(params.get("userName"));
			sbUpdateSQL.append("' and sessionID = '");
			sbUpdateSQL.append(params.get("sessionID"));
			sbUpdateSQL.append("' ");
			save(sbUpdateSQL.toString(), null);
			//DatabaseUtil.updateDateBase(sbUpdateSQL.toString());
		}
    }

    /**
     *��¼�ǳ���־
     * @param usr ��¼�˵�����
     * @param sessionID sessionIDֵ
     * @return 0,�޸ĳ���1�޸ĳɹ���2�޵�ǰsessionID,���ø����˳���¼
     */
    public static int setLogout(String usr, String sessionID) {
    	if(!StringUtil.checkStr(usr)){ return 1;}
    	//weblogic��sessionIDΪ54λ
		sessionID = sessionID.length() > 32? sessionID.substring(0,32):sessionID;
    	
        Dialect dialect = DialectFactory.getDialect();
        //�˳���¼����������ֶ�����
        if ("1".equals(ConfigInit.Config.getProperty("logonLog"))) {
        	String sMd5 = getUUID(); //MD5Tool.getMD5String();
        	Map params = new HashMap<String, String>();
        	params.put("userName", usr);
        	params.put("sessionID", sessionID);
            LogPrintExt.getInstance().logLoginOut(log, sMd5, params);
            if(LogOperateUtil.isLogRuntime()){
            	logLogoutToDb(params);
            }
            return 1;
        }
        return -1;
    }
    
    
    
    /**���������������Ϣ����־�ļ�  qiaoqide 2013-04-15
     * @param mp �쳣��־��Ϣ
     */
    public static void logPDMOperate(Map<String, String> mp) {
        log.error("[�쳣]�쳣���飺" + mp.get("sParams"));
    }  

    /**���ݵ�����־
     * @param iTemplateId ģ��ID
     * @param dBeginTime ���뿪ʼʱ��
     * @param iCostTime ����ʱ��
     * @param iStaffId ��ԱID
     * @param sStaffName ��Ա����
     * @param sStaffAccount ��Ա��¼��
     * @param iDeptId ��Ա����
     * @param sDeptName ��Ա��������
     * @param sContent ��ע
     * @param updateCount ���¼�¼����
     * @param insertCount �����¼����
     * @param allCount Ӱ����������
     * @return ���ؽ��
     */
    public static int addTbImportLog(long iTemplateId, Date dBeginTime,
    		long iCostTime, Double  iStaffId, String sStaffName, String sStaffAccount,
    		Double  iDeptId, String sDeptName, String sContent, int updateCount, 
    		int insertCount, int allCount) {
    	
    	// ������ò����������־
    	String operateLog = ConfigInit.Config.getProperty("IMPORT_LOG_DB", "0");
    	if (!("on".equals(operateLog) || "1".equals(operateLog))) {
            return 0;
        }
    	Dialect dialect = DialectFactory.getDialect();
        int iLogId = StringUtil.toInt(DatabaseUtil.getKeyId(safeMgrDataSource, ""));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String beginDate = dialect.stringToDatetime(sdf.format(dBeginTime));
        String endDate = dialect.stringToDatetime(sdf.format(new Date()));
        //дһ������־
        String sql = "insert into tbImportLog(iLogId,iTemplateId," +
        		"dBeginTime,dEndTime,iCostTime," +
        		"iStaffId,sStaffName,sStaffAccount," +
        		"iDeptId,sDeptName,sContent," +
        		"iAddCount,iEditCount,iAllCount) "
                + "values(" + iLogId + "," + iTemplateId + 
                "," + beginDate + "," + endDate + "," + iCostTime +
                "," + iStaffId + ",'" + sStaffName + "','" + sStaffAccount + 
                "'," + iDeptId + ",'" + sDeptName + "','" + sContent + 
                "'," + insertCount + "," + updateCount + "," + allCount + ")";
        save(sql, null);
        return 1;
        //return DatabaseUtil.updateDateBase(sql);
    }
    
    /**��������
     * @param sql SQL���
     * @param data �����б�
     */
    private static void save(String sql, ArrayList data) {
    	if(data == null){data = new ArrayList();}
        try {
            try {
                DatabaseUtil.updateDateBase(sql, data, safeMgrDataSource);
            } catch (Exception e) {// �汸�ÿ�
                String domain = ConfigInit.Config.getProperty("BACKUP_DATASRC");
                DatabaseUtil.updateDateBase(sql, data, domain);
            }
        } catch (Exception e) {
            // �����ﲻ���׳��쳣,��Ϊ�������������Ǳ��������־�������쳣��־��ѭ��
            e.printStackTrace();
        }
    }

    /**��ȡ����
     * @return ����ֵ
     */
    private static String getKeyId() {// Dialect dialect,String iId
        String primarykeys = null;
        try {
            try {
                primarykeys = DatabaseUtil.getKeyId(safeMgrDataSource, "������־ȡ����", "");
            } catch (Exception e) {// �ӱ��ÿ�ȡ
                String domain = ConfigInit.Config.getProperty("BACKUP_DATASRC");
                primarykeys = DatabaseUtil.getKeyId(domain, "������־ȡ����", "");
            }
        } catch (Exception e) {
            // �����ﲻ���׳��쳣,��Ϊ��Щ����������Ǳ�����־��������ѭ��
        }
        return primarykeys;
    }
    
    /**���ɲ������ݵ�json��ʽ�ַ���
     * @author tanjianwen
     * @param keys json��key����
     * @param values json��key��Ӧֵ������
     * @return ����ƴ�ճ�����json�ַ���
     */
    @Deprecated
    public static String buildOptJson(String[] keys, String[] values) {
        if (keys.length != values.length) {
            return "{}";
        }
        StringBuffer json = new StringBuffer();
        for (int i = 0; i < keys.length; i++) {
            if (json.length() > 0) {
                json.append(",");
            }
            json.append("\"").append(keys[i]).append("\":");
            if (!StringUtil.checkObj(keys[i])) {
                json.append("null");
                continue;
            }
            json.append("\"").append(values[i]).append("\"");
        }
        return "{" + json.toString() + "}";
    }
	
	/**��session�л�ȡ��¼��Ա��Ϣ
	 * @param session session����
	 * @return ������Ա��Ϣ����
	 */
	private static Map<String, String> getUserMsg(HttpSession session) {
		Map<String, String> mp = new HashMap<String, String>();
		StaffRegister staff = 
			(StaffRegister) SessionUtil.getAttribute("staff", session);
		if (staff != null) {
			mp.put("iDomainId", staff.getIDomainId());
			mp.put("sDomainName", staff.getSDomainName());
			mp.put("iDeptId", staff.getIDeptId());
			mp.put("sDeptName", staff.getSDeptName());
			mp.put("iStaffId", staff.getIStaffId());
			mp.put("sStaffAccount", staff.getSStaffAccount());
			mp.put("sStaffName", staff.getSStaffName());
			// ��ɫ��Ϣ(һ�����ж����ɫ��ȡ��һ����ɫ)
			List list = staff.getRoleList();
			if (list != null && list.size() > 0) {
				Map<String, String> role = (Map<String, String>) list.get(0);
				mp.put("iRoleId", role.get("iRoleId") == null ? "-1" : role
						.get("iRoleId"));
				mp.put("sRoleName", role.get("sRoleName") == null ? "�޽�ɫ"
						: role.get("sRoleName"));
			} else {
				mp.put("iRoleId", "-1");
				mp.put("sRoleName", "�޽�ɫ");
			}
		} else {
			mp.put("iDomainId", "-1");
			mp.put("sDomainName", "δ��¼��������");
			mp.put("iDeptId", "-1");
			mp.put("sDeptName", "δ��¼���޲���");
			mp.put("iStaffId", "-1");
			mp.put("sStaffAccount", "δ��¼�����˺�");
			mp.put("sStaffName", "δ��¼��������");
			mp.put("iRoleId", "-1");
			mp.put("sRoleName", "δ��¼���޽�ɫ");
		}
		return mp;
	}
	
	/**��ȡAction��Ȩ�ޱ�Ĺ�ϵ����
	 * @return Action��Ȩ�ޱ�Ĺ�ϵ����
	 */
	public static HashMap<String, Long> initActionRel() {
		HashMap<String, Long> resultActionRel = new HashMap<String, Long>();
		String operateLog = ConfigInit.Config.getProperty("operateLog", "0");
		if (!("1".equals(operateLog) || "on".equals(operateLog))){
			return resultActionRel;
		}
		//���������־����ǰ����Ȩ�ޱ���й���������Ҫ������Ӧӳ������
		String actionRelatedToRight = ConfigInit.Config	.getProperty("ActionRelatedToRight", "0");
		if (!("1".equals(actionRelatedToRight) || "on".equals(actionRelatedToRight))){
			return resultActionRel;
		}
		
		//������ӳ���ϵ��ӽ���(�����¾�ͨ�ñ��������config.properties���õ�Log_OperateMapping����)
		/*Map<Long, LogOperateMapping> allMapping = LogOperateMapping.getInstanceList();
		Collection<LogOperateMapping> mappings = allMapping.values();
		for(LogOperateMapping mapping : mappings){
			mapping.init();
			Map<String, Long> actionRelType = mapping.getActionRelType();
			if(actionRelType != null && actionRelType.size() > 0){
				Set<Entry<String, Long>> entrySet = actionRelType.entrySet();
				for(Entry<String, Long> entry : entrySet){
					String actionMethod = entry.getKey();
					Long newRelType = entry.getValue();
					Long oldRelType = resultActionRel.get(actionMethod);
					//���method������relType����ֵ�󣬸��ݶ������ȼ�����Ҫ����
					if(oldRelType == null || oldRelType < newRelType){
						resultActionRel.put(actionMethod, newRelType);
					}
				}
			}
		}*/
		return resultActionRel;
	}
	
	/**��Ȩ�ޱ�Ļ�ȡ���в˵�����
	 * @return 
	 */
	public static List< Map<String, String>> getRightMenuList() {
		String menuClickLog = ConfigInit.Config
			.getProperty("menuClickLog", "0");
		if (!("1".equals(menuClickLog) || "on".equals(menuClickLog))){
			return null;
		}
		
		//�������ʵʱ��⣬Ҳ��������Ŀ����ʱ����˵�����
		if(!LogOperateUtil.isLogRuntime()){
			return null;
		}
		
		String sql = "select iRight as \"iRight\",sURL as \"sURL\"  from tbOsRight where iType=1 and sURL is not null";
		 List<Map<String, String>> rightMenuList = DatabaseUtil.queryForList(sql);
		 List<Map<String, String>> menu = new ArrayList<Map<String,String>>();
		 //ȥ��urlΪ���ַ����ļ�¼
		 for(Map menuMap : rightMenuList){
			 if(StringUtil.checkStr(StringUtil.toString(menuMap.get("sURL")))){
				 menu.add(menuMap);
			 }
		 }
		 rightMenuList = null;
		 return menu;
	}
	
	
	/**
	 * ���������͵ķ���������Map����
	 * @param paramStr a=1�ָ���b=2
	 * @param paramSplitRegex �����ָ���
	 * @param paramValueSplitRegex ������ֵ�ķָ���
	 * @return �������װ��Map����
	 */
	public static  Map<String, String> getParamMap(String paramStr
			, String paramSplitRegex, String paramValueSplitRegex){
		Map<String, String> paramMap = new HashMap<String, String>();
		if(paramStr != null && paramStr.length() > 0){
			String[] paramAndValueStrSplit = paramStr.split(paramSplitRegex);
			String[] paramAndValueSplit = null;
			for(String paramAndValueStr : paramAndValueStrSplit){
				paramAndValueSplit = paramAndValueStr.split(paramValueSplitRegex);
				if(paramAndValueSplit.length == 2){
					paramMap.put(paramAndValueSplit[0].trim().replace("\"", "")
							, paramAndValueSplit[1].trim().replace("\"", ""));
				}
			}
		}
		return paramMap;
	}
	
	/**��־���������Ϊinfo
	 * @param msg ��־����
	 */
	public static void log(String msg){
		log(log, msg);
	}
	
	/**��־���������Ϊinfo
	 * @param log ��־����
	 * @param msg ��־����
	 */
	public static void log(Logger log1, String msg){
		log1 = (log1==null)? log : log1;
		log(log1, msg, "info");
	}
	
	/**��־���������������־�������
	 * @param log ��־����
	 * @param msg ��־����
	 * @param level ��־����
	 */
	public static void log(Logger log, String msg, String level){
		if("info".equals(level)){
			log.info(msg);
		}else if("error".equals(level)){
			log.error(msg);
		}else if("warn".equals(level)){
			log.warn(msg);
		}else if("fatal".equals(level)){
			log.fatal(msg);
		}else {
			log.debug(msg);
		}
	}
	
	/**ͨ����ʼʱ��ͽ���ʱ���ȡ�м�����Сʱ������
	 * ������־ģ���е�ͳ�ƽ���
	 * @param beginDate ��ʼʱ��
	 * @param endDate ����ʱ��
	 * @param type 1��Сʱ;��������
	 * @return
	 */
	public static Map getDataMap(String beginDate, String endDate, Integer type) {
		Map map=new LinkedHashMap();
		beginDate=beginDate;
		endDate=endDate;
		//Сʱ
		if(type==1){
			//map.put((beginDate.split(" ")[1]).substring(0,5), beginDate);
			Long beginLong=Long.valueOf(beginDate.replaceAll("[-\\s:]",""));
			Long endLong=Long.valueOf(endDate.replaceAll("[-\\s:]",""));
			String newDate = beginDate;//StringUtil.getNextTime(beginDate,60);
			 while(beginLong < endLong){
				 map.put((newDate.split(" ")[1]).substring(0,5), newDate);
				 newDate=StringUtil.getNextTime(newDate,60);
				 beginLong=Long.valueOf(newDate.replaceAll("[-\\s:]",""));
			 }
		}else{//��
			//map.put((beginDate.split(" ")[0]), (beginDate.split(" ")[0]));
			Long beginLong=Long.valueOf(beginDate.replaceAll("[-\\s:]",""));
			//��Ҫ�������ʱ�䣬��Ȼ��ʼʱ�����24Сʱ�ͻ���ڽ���ʱ�䣬��������Ͳ�����ͳ������
			Long endLong=Long.valueOf(((endDate.substring(0,11))+"23:59:59").replaceAll("[-\\s:]",""));
			String newDate=beginDate;//StringUtil.getNextTime(beginDate,1440);
			 while(beginLong < endLong){
				 map.put((newDate.split(" ")[0]), (newDate.split(" ")[0]));
				 newDate=StringUtil.getNextTime(newDate,1440);
				 beginLong=Long.valueOf(newDate.replaceAll("[-\\s:]",""));
			 }
		}
		return map;
	}
	
	/**�Ƿ���Ҫʵʱд���ݿ�
	 * @return
	 */
	public static boolean isLogRuntime(){
		String configValue = ConfigInit.Config.getProperty("logRuntime", "0");
		if("1".equals(configValue) || "on".equals(configValue)){
			return true;
		}
		return false;
	}
	
	
	/**��ȡmap������ֵ�ĳ���
	 * @param reqeustParamMap
	 * @param paramMaxLength
	 */
	private static void truncateParam(Map<String, String> reqeustParamMap,int paramMaxLength){
		if(reqeustParamMap != null && reqeustParamMap.size() > 0){
			Set<Entry<String, String>> entrySet = reqeustParamMap.entrySet();
			Iterator<Entry<String, String>> iterator = entrySet.iterator();
			while(iterator.hasNext()){
				 Entry<String, String> next = iterator.next();
				//��������500���ֽھͽ�ȡ��
				 if((next.getValue().length() + StringUtil.getChineseCount(next.getValue())) > paramMaxLength ){
					 reqeustParamMap.put(next.getKey() , StringUtil.getSubString(next.getValue(), 0, paramMaxLength)+"...���ݹ��������ض�!");
				 }
			}
		}
	}
	
	/**
	 * ȡUUID����
	 * @return UUID����
	 */
	public static String getUUID(){
		String uuId=java.util.UUID.randomUUID().toString().replaceAll("-", "");
		return uuId;
	}
	
}
