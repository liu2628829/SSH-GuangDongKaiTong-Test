package pub.source;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import pub.dbDialectFactory.Dialect;
import pub.dbDialectFactory.DialectFactory;
import pub.servlet.ConfigInit;
import util.BaseRuntimeException;
import util.DateUtil;
import util.I18nConfig;
import util.JackJson;
//import util.MD5Tool;
import util.RequestUtil;
import util.SessionUtil;
import util.StringUtil;
import vo.deptMgr.StaffRegister;
import com.opensymphony.xwork2.ActionContext;

/**
 * 所有日志保存操作类
 *
 * @author gaotao 2011-08-18
 *
 */
public class LogOperateUtil {
    /**日志输出操作对象*/
	private static Logger log = Logger.getLogger(LogOperateUtil.class);
	/**缓存已经做了匹配的菜单链接，不用每次菜单都和权限表菜单进行匹配**/
	private static Map<String, String> matchedMenu = new HashMap<String, String>();
	/**安全管理模块数据源	 */
	private static String safeMgrDataSource = ConfigInit.Config.getProperty(
	           "safeManagerDataSource", ""); 
	
	private static String SYSTEM_NAME = ConfigInit.Config.getProperty("SYSTEM_NAME");
	/**
	 * 获取程序运行路径
	 * @return 程序运行路径字符串
	 */
	public static String logCallStack() {
		Throwable ste = new Throwable();
		return logCallStack(ste);
	}

	/**获取程序运行路径
	 * @param t 异常对象
	 * @return 程序运行路径字符串
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
	
    /**日志记录SQL执行前的信息
     * @param sql 执行的SQL语句
     * @param sMd5 当前日志的MD5标识
     * @param domain 当前连接的数据源
     * @return 程序执行路径
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

    /**日志记录SQL执行后的信息
     * @param sql 执行的SQL语句
     * @param sMd5 当前日志的MD5标识
     * @param timeBefore 执行前记录的时间
     * @return 执行时长
     */
    public static Long logSQLAfter(String sql, String sMd5, long timeBefore) {
    	long timeDiff = System.currentTimeMillis() - timeBefore;
    	String jdbcLog = ConfigInit.Config.getProperty("jdbcLog", "0");
        if (!("on".equals(jdbcLog) || "1".equals(jdbcLog))) {
            return timeDiff;
        }
        
        Map map = new HashMap<String, String>();
        map.put("excuteCostTime", timeDiff);
        map.put("rowCount", "0");//该方法不会传影响条数，默认用0 tangyj
        LogPrintExt.getInstance().logSQLExcuteAfter(log, sMd5, map);
        
        return timeDiff;
    }
    
    /**日志记录SQL执行后的信息
     *  增加一个参数，记录影响条数  modify by tangyujun 2013-04-11
     * @param sql 执行的SQL语句
     * @param sMd5 当前日志的MD5标识
     * @param timeBefore 执行前记录的时间
     * @param rowCount 影响数据的条数
     * @return 执行时长
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

    /**处理SQL异常信息，日志记录、打印、抛异常
     * @param e 异常对象
     * @param domain 数据源标识
     * @param sql 执行的SQL语句
     * @param path 执行路径
     * @return 封装后的BaseRuntimeException对象
     */
    public static BaseRuntimeException logSQLError(Exception e, String domain,
            String sql, String path) {
        String path1 = logCallStack(e); //异常的真实路径
        
        String msg = "底层与数据库交互时发生异常!";
        domain = StringUtil.checkStr(domain) ? domain
                : DialectFactory.getDefaultDatasrc();
        Map<String, String> m = new HashMap<String, String>();
        m.put("sDataSource", domain);
        m.put("sSql", sql);
        m.put("sPath", StringUtil.checkStr(path1) ? path1 : path);
        m.put("exceptionMessage", msg); 
        throw new BaseRuntimeException(e, m);
    }

    /**将执行超时一定时间的SQL入库记录
     * @param sMd5 当前日志的MD5标识
     * @param sSql 执行的SQL语句
     * @param iCostTime 执行时长
     * @param iRowCount 影响数据的条数
     * @param sDataSource 数据源标识
     * @param sPath 程序路径
     */
    @Deprecated
    public static void logSQLToDb(String sMd5, String sSql, long iCostTime,
            int iRowCount, String sDataSource, String sPath) {
        String SQL_LOG_DB = ConfigInit.Config.getProperty(
        		"SQL_LOG_DB", "off");// 是否入库
        int SQL_LOG_TIME = StringUtil.toInt(ConfigInit.Config.getProperty(
                "SQL_LOG_TIME", "1"));// SQL执行时长
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
            	sSql = sSql.substring(0, 2000)+"...超出字段最大长度(2000字节)，已被截取！";
    		}
            data.add(sSql);
            // 保存
            save(sb.toString(), data);
        }
    }

    /**将异常日志输出到日志文件
     * @param mp 异常日志信息
     */
    private static void logException(Map<String, String> mp) {
    	String exceptionLog = ConfigInit.Config.getProperty(
    			"exceptionLog", "0");// 是否入库
        // 如果配置不保存异常日志
        if (!("on".equals(exceptionLog) || "1".equals(exceptionLog))) {
            return;
        }
        
    	String sMd5 = mp.get("sMd5");
        LogPrintExt.getInstance().logException(log, sMd5, mp);
        
        if(LogOperateUtil.isLogRuntime()){
        	logExceptionToDb(mp);
        }
        
    }
    
	/**调用后记录异常日志
	 * 该方法主要用于程序中自己catch异常记录异常日志，不会中断程序的运行
	 * @param exception 异常对象
	 * @param message 自定义异常描述信息
	 * @return 返回客户端提示信息
	 */
	public static String logException(Throwable exception, String message, HttpServletRequest req) {
		String sMd5 = getUUID(); //MD5Tool.getMD5String();
		
		//反回到界面显示异常描述
		String sSysExceptionMessage = "";
		String mKey = null; //从I18NConfig.properties中配置的键
		String systemRunMode = ConfigInit.Config.getProperty("systemRunMode", "1");
		String[] sArgs = null; //需要替换的数据
		//异常路径，数据源，执行sql，系统异常，异常信息，异常提示
		String sPath = "", sDataSource = "", sSql = ""
			, sSysExceptionName = "", sSelfExceptionMessage = "", sqlExcetpionMsg = "";
		
		/**
		* 如果底层有用反射机制反射出对象，并调用方法时，
		* 其不能直接得到最原始的异常，
		* 而是被包装了一层的java.lang.reflect.InvocationTargetException
		* 此时通过getCause()才能拿到最原始的异常
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
				//清理掉，为打印其它异常描述作准备
				m.remove("sPath");
				m.remove("sDataSource");
				m.remove("sSql");
				m.remove("exceptionMessage");
				m.remove("i18n");
			}
	        //得最终用于客户端提示异常描述信息
	        mKey = StringUtil.checkStr(mKey) ? mKey : bre.getI18nKey();
	        sArgs = bre.getSArgs();
			sSelfExceptionMessage = I18nConfig.getInstance(
					systemRunMode).getValue(mKey, sArgs, sSelfExceptionMessage);
			
			//得原始异常类名与描述
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
		 * 自定义异常信息的优先级：
		 * 优先以message为准，其次以Throwable里的为准，最后以系统配置文件内的为准
		 * */
		message = StringUtil.checkObj(message)?"此异常被人为捕获，未中断程序运行-->"+message : null;
		sSelfExceptionMessage =
			   StringUtil.checkStr(message) ? message : sSelfExceptionMessage;
		sSelfExceptionMessage =
				StringUtil.checkStr(sSelfExceptionMessage) ? sSelfExceptionMessage :
					(I18nConfig.getInstance(systemRunMode).getValue(null, null, null));
		
		//异常详细描述
		if( m == null ){m = new HashMap();}
		sSysExceptionMessage = StringUtil.toString(
				sSysExceptionMessage).replace("\n", " | ").replace("\r", " | ");
		m.put("sysExceptionMessage", sSysExceptionMessage);
		sSysExceptionMessage = JackJson.getBasetJsonData(m);
		//异常程序路径再次确认
		sPath = (StringUtil.checkStr(sPath))? sPath : logCallStack(exception);

		Map<String, String> mp = new HashMap<String, String>();
		mp.put("sMd5", sMd5);
		mp.put("sPath", sPath);
		if(sSelfExceptionMessage != null && sSelfExceptionMessage.length() > 450){
			sSelfExceptionMessage = sSelfExceptionMessage.substring(0, 450)+"...超出字段最大长度(450字节)，已被截取！";
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
			//人员信息
			String iStaffId = SessionUtil.getAttribute("USERID",
					request.getSession()) != null ? (String)SessionUtil.getAttribute(
							"USERID", request.getSession()):"-1";
			String sStaffAccount = SessionUtil.getAttribute("USERNAME", 
					request.getSession()) != null?(String)SessionUtil.getAttribute(
							"USERNAME", request.getSession()):"未登录，无账号";
			String sStaffName = SessionUtil.getAttribute("USERCNNAME", 
					request.getSession()) != null ? (String)SessionUtil.getAttribute(
							"USERCNNAME", request.getSession()):"未登录，无姓名";
			
			//请求url,开始处理时间,模块ID
			String sUrl = request.getAttribute("sUrl") == null ? "" : (String)request.getAttribute("sUrl");
			long iCostTime = 0l;
			if(request.getAttribute("beginTime") != null){//如果不经过日志过滤器，此数据是没有的
			 	iCostTime = System.currentTimeMillis() - (Long)request.getAttribute("beginTime");
			}
			
		    //处理所有参数，值超出500就截断
			Map<String, String> reqeustParam = RequestUtil.getMapByRequest(request);
			truncateParam(reqeustParam, 500);
			//通过action方法和请求参数与权限表功能按钮进行匹配
			String actionMethod = (String) request.getAttribute("actionMethod");
	        Map<String, String> rightInfoMap = createRightInfo(actionMethod,request.getHeader("referer"), reqeustParam);
		    //设置关联数据
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
	    //异常信息输出到日志文件和数据库
    	logException(mp);
		//开发模式下，打印异常
		if(exception != null && "1".equals(systemRunMode)){
	        exception.printStackTrace();
	        sSelfExceptionMessage += ("#%"+m.get("sysExceptionMessage")) + ("#%"+sPath) + ("#%"+sSysExceptionName);
	    }
		
		return sSelfExceptionMessage;
	}
    
	/**
	 * 记录异常日志
	 * 该方法只在Exception.jsp中有用到，其它地方如果要记异常日志请调用重载方法（两个参数）
	 * @param exception 异常对象
	 * @return 自定义的异常信息
	 */
	public static String logException(Throwable exception) {
		return logException(exception, null, null);
	}
	
	/**
	 * 记录异常日志 add by qiaoqd
	 * 兼容外部调用logException(Throwable exception, String msg)
	 * @param exception 异常对象
	 * @return 自定义的异常信息
	 */
	public static String logException(Throwable exception, String msg) {
		return logException(exception, msg, null);
	}
	
	/**
	 * 记录异常日志 add by qiaoqd
	 * 该方法主要针对Exception.jsp页面处理过滤器异常使用.
	 * @param exception 异常对象
	 * @param req 请求对象
	 * @return 自定义的异常信息
	 */
	public static String logException(Throwable exception, HttpServletRequest req) {
		return logException(exception, null, req);
	}
	
    /** 异常日志保存入库
     *  需要将config.properties的ActionRelatedToRight设置为1
     * @param mp 异常日志信息
     */
	@Deprecated
    public static void logExceptionToDb(Map<String, String> mp) {
        String ERR_LOG_DB = ConfigInit.Config.getProperty("ERR_LOG_DB", "off");// 是否入库
        // 如果配置不保存异常日志
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
        
        // 保存
        save(sb.toString(), data);
    }
	
    /**菜单点击日志输出到日志文件  tangyj 2013-05-27
     * @param mp 菜单点击日志信息
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
    
    /**将数据直接存数据库
     * @param mp key值如下：
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
        // 拼SQL
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
        // 保存
        save(sb.toString(), data);
    	
    }
    
	/**
	 * 记录菜单日志
	 * @param request HttpServletRequest对象
	 */
	public static void logMenuClick(HttpServletRequest request) {
		String menuClickLog = ConfigInit.Config.getProperty("menuClickLog", "0");
        if (!("on".equals(menuClickLog) || "1".equals(menuClickLog))) {
            return;
        }
        
     	//异步请求不会记菜单点击日志
		if(RequestUtil.isAjaxRequest(request)){
			return;
		}
		
		String URLRegExp = (String) ConfigInit.Config.getProperty("URLRegExp", "");
		if(!StringUtil.checkStr(URLRegExp)){
			return;
		}
		
		String urlValue = RequestUtil.mergeURIAndParma(request);// 取请求路径
		
		//根据URL进行过滤，防止将图片等资源请求也当成是菜单点击
		Pattern pattern = Pattern.compile(URLRegExp);
		Matcher matcher = pattern.matcher(urlValue);
		if (!matcher.find()){
			return;
		 }
		
		//记录日志文件
		HttpSession session = request.getSession();
		Map<String, String> mp = getUserMsg(session);// 得用户信息
		mp.put("sIpAddress", RequestUtil.getIpAddr(request));// 取客户端访问IP
		mp.put("sUrl", urlValue);// 取请求路径
		mp.put("queryStr", request.getQueryString());// 链接所带的参数
		
		if(isLogRuntime()){
			//通过请求链接与权限表菜单进行比较
			String iRightId = null;
			//直接从缓存中取
			if(matchedMenu.containsKey(urlValue)){
				iRightId = matchedMenu.get(urlValue);
	        }else{
	        	iRightId = getRightIdByUrl(urlValue);
	        	matchedMenu.put(urlValue, iRightId);
	        }
			
			if(!StringUtil.checkStr(iRightId)){
				return;
			}
			mp.put("iRightId", iRightId);// 链接所带的参数
		}
		LogOperateUtil.logMenuClick(mp);
	}
	
	private  static String getRightIdByUrl(String url){
		//初始化权限数据
		List<Map<String, String>> rightList  = ConfigInit.rightMenuList;
		if(url == null || url.length() == 0){return null;}
		
		//如果paramNames值一直为空则表示只匹配URL，不匹配任务参数
		String[] paramNames = null;
		String paramStr = RequestUtil.getUrlAndParam(url)[1];
		Map<String, String> paramMap = RequestUtil.getParamMap(paramStr, "&", "=");
		
		//针对岗管版流程
		if(url.indexOf("controlBus!getPage") != -1){
			String filedName = "";
			if(StringUtil.checkStr(paramMap.get("Fd_sTableName"))){
				filedName = "Fd_sTableName";
			}
			if(StringUtil.checkStr(paramMap.get("Fd_iFormId"))){
				filedName = "Fd_iFormId";
			}
			if(filedName == null){//如果URL中没有该参数，则直接作废数据
				return null;
			}
			paramNames = new String[2];//{"Fd_opt",filedName};
			paramNames[0] = "Fd_opt";
			paramNames[1] = filedName;
		}
		//针对流程通用表单
		else if(url.indexOf("createPage!getPage.action") != -1){
			String filedName = null;
			if(StringUtil.checkStr(paramMap.get("S_PAGE_ID"))){
				filedName = "S_PAGE_ID";
			}
			if(StringUtil.checkStr(paramMap.get("S_TABLE_ID"))){
				filedName = "S_TABLE_ID";
			}
			if(filedName == null){//如果URL中没有该参数，则直接作废数据
				return null;
			}
			paramNames = new String[1];
			paramNames[0] = filedName;
		}else{//需要满足后面URL的所有参数
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
    
    /**操作日志输出到日志文件 tangyj 2013-03-31
     * @param mp 操作日志信息
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
    
    /**记录操作日志
	 * @param request request对象
	 */
	public static void logOperate(HttpServletRequest request) {
		String operateLog = ConfigInit.Config.getProperty("operateLog", "0");
        if (!("on".equals(operateLog) || "1".equals(operateLog))) {
            return;
        }
        HttpSession session = request.getSession();
		long endTime = System.currentTimeMillis();//结束时间
		Map<String, String> mp = getUserMsg(session);// 得用户信息
		
		//处理所有参数，值超出500就截断
		Map<String, String> reqeustParamMap = RequestUtil.getMapByRequest(request);
		truncateParam(reqeustParamMap, 500);
		//通过action方法和请求参数与权限表功能按钮进行匹配
	 	String actionMethod = (String) request.getAttribute("actionMethod");
        Map<String, String> rightInfoMap = createRightInfo(actionMethod, request.getHeader("referer"), reqeustParamMap);
        //如果没有匹配结果则不记日志
        if(rightInfoMap == null || rightInfoMap.size() == 0 || "-1".equals(rightInfoMap.get("isRelated"))){
        	return;
        }
        //设置关联数据
        mp.put("iActionId", rightInfoMap.get("iActionId"));
		mp.put("iRightId", rightInfoMap.get("iRightId"));
		mp.put("iBtnRightId", rightInfoMap.get("iBtnRightId"));
		mp.put("sContent", StringUtil.toString(rightInfoMap.get("sContent")));
		
		mp.put("sIpAddress", RequestUtil.getIpAddr(request));// 取客户端访问IP
		mp.put("sUrl", RequestUtil.mergeURIAndParma(request));// 取请求路径
		
		//取请求参数
		mp.put("sParams", JackJson.getBasetJsonData(reqeustParamMap));
		mp.put("rightInfo", JackJson.getBasetJsonData(rightInfoMap));
		// 计算处理时长(毫秒)
		mp.put("iCostTime", (endTime - (Long)request.getAttribute("beginTime")) + "");
		mp.put("sReferer", request.getHeader("referer"));
		
		LogOperateUtil.logOperate(mp);
		
	}
	
	/**根据action获取所属模块信息
	 * @param actionMethod action类及方法名
	 * @param requestParamMap 请求相关参数
	 * @param referer 请求所在界面的URL
	 * @return 匹配后的结果信息
	 */
	private static  Map<String, String>  createRightInfo(
				String actionMethod, String referer, Map<String, String> requestParamMap){
        String actionRelatedToRight = ConfigInit.Config.getProperty("ActionRelatedToRight", "0");
		//需要在前端与权限进行关联
        Map<String, String> rightParamMap = new HashMap<String, String>();
        if ("1".equals(actionRelatedToRight) || "on".equals(actionRelatedToRight)){
        	Map<String, String> matchResult = null;
        	Long iRelType = ConfigInit.actionRel.get(actionMethod);
    		if(iRelType != null){
    			//Map复制，将请求所在界面URL的参数也加到Map中
            	String paramStr = RequestUtil.getUrlAndParam(referer)[1];
        		Map<String, String> paramMap = RequestUtil.getParamMap(paramStr, "&", "=");
        		paramMap.putAll(requestParamMap);
    			//LogOperateMapping logOperateMapping = LogOperateMapping.getInstanceList().get(iRelType);
    			//matchResult = logOperateMapping.match(actionMethod, paramMap);
    		}
			if(matchResult != null){
				rightParamMap.put("isRelated", "1");//1代表已关联
				rightParamMap.put("iActionId", matchResult.get("IACTIONID"));
				rightParamMap.put("iRightId", matchResult.get("IRIGHTID"));
				rightParamMap.put("iBtnRightId", matchResult.get("IBTNRIGHTID"));
				rightParamMap.put("sContent", matchResult.get("SCONTENT") != null ? matchResult.get("SCONTENT"):"");
			}else{//关联失败
				rightParamMap.put("isRelated", "-1");//-1代表关联失败
				rightParamMap.put("iActionId", "-1");
				rightParamMap.put("iRightId", "-1");
				rightParamMap.put("iBtnRightId", "-1");
				rightParamMap.put("sContent", "");
			}
		}else{//在后端关联
			rightParamMap.put("isRelated", "0");//0代表未关联
			rightParamMap.put("actionMethod", actionMethod);//0代表未关联
		}
        
        return rightParamMap;
	}
    
    /**操作日志直接保存入库
     * 需要将config.properties的ActionRelatedToRight设置为1
     * @param mp 操作日志信息
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
        // 拼SQL
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
        // 保存
        save(sb.toString(), data);
    }
   
    /**
     * 锁定、解锁、修改密码、对象新增修改删除，记录日志
     * @author tanjianwen 2012-5-22
     * @param mp 日志信息
     */
    public static void logOperationToDb(Map mp) {
        String OPT_LOG_DB = 
        	ConfigInit.Config.getProperty("OPT_LOG_DB", "off").trim();// 是否入库
        // 如果配置不保存操作日志
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
                		+ "...数据过长，被截断!");
            } else {
                data.add(mp.get("sOptJson").toString());
            }
        } else {
            data.add("");
        }
        // 保存
        save(sb.toString(), data);
    }

    /**
     * 以下为登陆日志需要用到的方法，modify by ldc 2011-11-3
     * @param errCode 登录状态
     * @param userName 登录人名称
     * @param sBrowserType 浏览器类型
     * @param sessionID 登录SessionId
     * @param sLoginIP 登录IP
     * @return  0: 代表失败 1：代表成功
     */
    public static int setLogin(String errCode, String userName,
    		String sBrowserType, String sessionID, String sLoginIP) {
    	 sBrowserType = 
         	StringUtil.checkStr(sBrowserType) ? sBrowserType : "";
        Dialect dialect = DialectFactory.getDialect();
        Date loginDate = new Date();
        //weblogic的sessionID为54位
        sessionID = sessionID.length() > 32? sessionID.substring(0,32):sessionID;
        //写一条新的登录信息
        if ("1".equals(ConfigInit.Config.getProperty("logonLog"))) {
        	String sMd5 = getUUID(); //MD5Tool.getMD5String();
        	//如果是登录信息，需要写日志文件
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
                //1代表成功
                return 1;
        	}
        	//登录失败的数据需要直接入库
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
     * 登录成功的入库方法，map的key如下
     *  userName
     *  sBrowserType
     *  sessionID
     * sLoginIP
     */
    public static void logLoginToDb(Map<String, String> params){
    	Dialect dialect = DialectFactory.getDialect();
    	Date loginDate = new Date();
    	//登录成功后检查上次登录是否有退出，
    	//如果没有退出则要将本次的登录时间记录为上一次登录的登出时间
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
	  //记录登录成功的数据	
      String sql = "insert into tbLoginLog(sessionID,sLoginName" +
      		",iState,dLoginTime,sBrowserType,sLoginIP,sMd5,sSystemName) " +
      		" values('" + params.get("sessionID") + "','" + params.get("userName") +
      		"',1," + dialect.stringToDatetime(DateUtil.parseToString(loginDate, DateUtil.yyyyMMddHHmmss)) + 
      		",'" + params.get("sBrowserType") + "','" + params.get("sLoginIP") + "','"+params.get("sMd5") +"','"+SYSTEM_NAME+"')";
      save(sql, null);
      //DatabaseUtil.updateDateBase(sql);
    }
    
    /**记录登录信息
     * @param params key值如下
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
     *记录登出日志
     * @param usr 登录人的名称
     * @param sessionID sessionID值
     * @return 0,修改出错，1修改成功，2无当前sessionID,不用更新退出记录
     */
    public static int setLogout(String usr, String sessionID) {
    	if(!StringUtil.checkStr(usr)){ return 1;}
    	//weblogic的sessionID为54位
		sessionID = sessionID.length() > 32? sessionID.substring(0,32):sessionID;
    	
        Dialect dialect = DialectFactory.getDialect();
        //退出登录，更改相关字段内容
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
    
    
    
    /**导入库表输出错误信息到日志文件  qiaoqide 2013-04-15
     * @param mp 异常日志信息
     */
    public static void logPDMOperate(Map<String, String> mp) {
        log.error("[异常]异常详情：" + mp.get("sParams"));
    }  

    /**数据导入日志
     * @param iTemplateId 模板ID
     * @param dBeginTime 导入开始时间
     * @param iCostTime 导入时长
     * @param iStaffId 人员ID
     * @param sStaffName 人员姓名
     * @param sStaffAccount 人员登录贴
     * @param iDeptId 人员部门
     * @param sDeptName 人员部门名称
     * @param sContent 备注
     * @param updateCount 更新记录总数
     * @param insertCount 插入记录总数
     * @param allCount 影响数据条数
     * @return 返回结果
     */
    public static int addTbImportLog(long iTemplateId, Date dBeginTime,
    		long iCostTime, Double  iStaffId, String sStaffName, String sStaffAccount,
    		Double  iDeptId, String sDeptName, String sContent, int updateCount, 
    		int insertCount, int allCount) {
    	
    	// 如果配置不保存操作日志
    	String operateLog = ConfigInit.Config.getProperty("IMPORT_LOG_DB", "0");
    	if (!("on".equals(operateLog) || "1".equals(operateLog))) {
            return 0;
        }
    	Dialect dialect = DialectFactory.getDialect();
        int iLogId = StringUtil.toInt(DatabaseUtil.getKeyId(safeMgrDataSource, ""));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String beginDate = dialect.stringToDatetime(sdf.format(dBeginTime));
        String endDate = dialect.stringToDatetime(sdf.format(new Date()));
        //写一条新日志
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
    
    /**保存数据
     * @param sql SQL语句
     * @param data 数据列表
     */
    private static void save(String sql, ArrayList data) {
    	if(data == null){data = new ArrayList();}
        try {
            try {
                DatabaseUtil.updateDateBase(sql, data, safeMgrDataSource);
            } catch (Exception e) {// 存备用库
                String domain = ConfigInit.Config.getProperty("BACKUP_DATASRC");
                DatabaseUtil.updateDateBase(sql, data, domain);
            }
        } catch (Exception e) {
            // 在这里不再抛出异常,因为这个操作本身就是保存各种日志，避免异常日志死循环
            e.printStackTrace();
        }
    }

    /**获取主键
     * @return 主键值
     */
    private static String getKeyId() {// Dialect dialect,String iId
        String primarykeys = null;
        try {
            try {
                primarykeys = DatabaseUtil.getKeyId(safeMgrDataSource, "保存日志取主键", "");
            } catch (Exception e) {// 从备用库取
                String domain = ConfigInit.Config.getProperty("BACKUP_DATASRC");
                primarykeys = DatabaseUtil.getKeyId(domain, "保存日志取主键", "");
            }
        } catch (Exception e) {
            // 在这里不再抛出异常,因为这些操作本身就是保存日志，避免死循环
        }
        return primarykeys;
    }
    
    /**生成操作数据的json格式字符串
     * @author tanjianwen
     * @param keys json的key数组
     * @param values json的key对应值的数组
     * @return 最络拼凑出来的json字符串
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
	
	/**从session中获取登录人员信息
	 * @param session session对象
	 * @return 返回人员信息集合
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
			// 角色信息(一个人有多个角色，取第一个角色)
			List list = staff.getRoleList();
			if (list != null && list.size() > 0) {
				Map<String, String> role = (Map<String, String>) list.get(0);
				mp.put("iRoleId", role.get("iRoleId") == null ? "-1" : role
						.get("iRoleId"));
				mp.put("sRoleName", role.get("sRoleName") == null ? "无角色"
						: role.get("sRoleName"));
			} else {
				mp.put("iRoleId", "-1");
				mp.put("sRoleName", "无角色");
			}
		} else {
			mp.put("iDomainId", "-1");
			mp.put("sDomainName", "未登录，无区域");
			mp.put("iDeptId", "-1");
			mp.put("sDeptName", "未登录，无部门");
			mp.put("iStaffId", "-1");
			mp.put("sStaffAccount", "未登录，无账号");
			mp.put("sStaffName", "未登录，无姓名");
			mp.put("iRoleId", "-1");
			mp.put("sRoleName", "未登录，无角色");
		}
		return mp;
	}
	
	/**获取Action与权限表的关系数据
	 * @return Action与权限表的关系数据
	 */
	public static HashMap<String, Long> initActionRel() {
		HashMap<String, Long> resultActionRel = new HashMap<String, Long>();
		String operateLog = ConfigInit.Config.getProperty("operateLog", "0");
		if (!("1".equals(operateLog) || "on".equals(operateLog))){
			return resultActionRel;
		}
		//如果操作日志不在前端与权限表进行关联，则不需要加载相应映射数据
		String actionRelatedToRight = ConfigInit.Config	.getProperty("ActionRelatedToRight", "0");
		if (!("1".equals(actionRelatedToRight) || "on".equals(actionRelatedToRight))){
			return resultActionRel;
		}
		
		//将所有映射关系添加进来(包括新旧通用表单，具体见config.properties配置的Log_OperateMapping属性)
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
					//如果method存在且relType的数值大，根据定的优先级就需要覆盖
					if(oldRelType == null || oldRelType < newRelType){
						resultActionRel.put(actionMethod, newRelType);
					}
				}
			}
		}*/
		return resultActionRel;
	}
	
	/**从权限表的获取所有菜单数据
	 * @return 
	 */
	public static List< Map<String, String>> getRightMenuList() {
		String menuClickLog = ConfigInit.Config
			.getProperty("menuClickLog", "0");
		if (!("1".equals(menuClickLog) || "on".equals(menuClickLog))){
			return null;
		}
		
		//如果不是实时入库，也不用在项目开启时缓存菜单数据
		if(!LogOperateUtil.isLogRuntime()){
			return null;
		}
		
		String sql = "select iRight as \"iRight\",sURL as \"sURL\"  from tbOsRight where iType=1 and sURL is not null";
		 List<Map<String, String>> rightMenuList = DatabaseUtil.queryForList(sql);
		 List<Map<String, String>> menu = new ArrayList<Map<String,String>>();
		 //去掉url为空字符串的记录
		 for(Map menuMap : rightMenuList){
			 if(StringUtil.checkStr(StringUtil.toString(menuMap.get("sURL")))){
				 menu.add(menuMap);
			 }
		 }
		 rightMenuList = null;
		 return menu;
	}
	
	
	/**
	 * 将参数类型的符串解析成Map集合
	 * @param paramStr a=1分隔符b=2
	 * @param paramSplitRegex 参数分隔符
	 * @param paramValueSplitRegex 参数与值的分隔符
	 * @return 解析后封装的Map集合
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
	
	/**日志输出，级别为info
	 * @param msg 日志内容
	 */
	public static void log(String msg){
		log(log, msg);
	}
	
	/**日志输出，级别为info
	 * @param log 日志对象
	 * @param msg 日志内容
	 */
	public static void log(Logger log1, String msg){
		log1 = (log1==null)? log : log1;
		log(log1, msg, "info");
	}
	
	/**日志输出，可以设置日志输出级别
	 * @param log 日志对象
	 * @param msg 日志内容
	 * @param level 日志级别
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
	
	/**通过开始时间和结束时间获取中间所隔小时或天数
	 * 用于日志模块中的统计界面
	 * @param beginDate 开始时间
	 * @param endDate 结束时间
	 * @param type 1：小时;其它：天
	 * @return
	 */
	public static Map getDataMap(String beginDate, String endDate, Integer type) {
		Map map=new LinkedHashMap();
		beginDate=beginDate;
		endDate=endDate;
		//小时
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
		}else{//天
			//map.put((beginDate.split(" ")[0]), (beginDate.split(" ")[0]));
			Long beginLong=Long.valueOf(beginDate.replaceAll("[-\\s:]",""));
			//需要扩大结束时间，不然开始时间加上24小时就会大于结束时间，这样当天就不会有统计数据
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
	
	/**是否需要实时写数据库
	 * @return
	 */
	public static boolean isLogRuntime(){
		String configValue = ConfigInit.Config.getProperty("logRuntime", "0");
		if("1".equals(configValue) || "on".equals(configValue)){
			return true;
		}
		return false;
	}
	
	
	/**截取map集合中值的长度
	 * @param reqeustParamMap
	 * @param paramMaxLength
	 */
	private static void truncateParam(Map<String, String> reqeustParamMap,int paramMaxLength){
		if(reqeustParamMap != null && reqeustParamMap.size() > 0){
			Set<Entry<String, String>> entrySet = reqeustParamMap.entrySet();
			Iterator<Entry<String, String>> iterator = entrySet.iterator();
			while(iterator.hasNext()){
				 Entry<String, String> next = iterator.next();
				//参数超过500个字节就截取掉
				 if((next.getValue().length() + StringUtil.getChineseCount(next.getValue())) > paramMaxLength ){
					 reqeustParamMap.put(next.getKey() , StringUtil.getSubString(next.getValue(), 0, paramMaxLength)+"...数据过长，被截断!");
				 }
			}
		}
	}
	
	/**
	 * 取UUID主键
	 * @return UUID主键
	 */
	public static String getUUID(){
		String uuId=java.util.UUID.randomUUID().toString().replaceAll("-", "");
		return uuId;
	}
	
}
