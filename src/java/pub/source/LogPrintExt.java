package pub.source;

import java.util.Map;

import org.apache.log4j.Logger;

import pub.servlet.ConfigInit;
import util.StringUtil;

/**该方法用于日志输出的扩展,可以通过继承该类重写日志输出格式
 * @author tangyj
 * @date Jul 4, 2013
 */
public  class LogPrintExt {
	private static LogPrintExt instance = null;
	
	private static String SYSTEM_NAME = ConfigInit.Config.getProperty("SYSTEM_NAME");
	
	 /**获取实例
	 * @return 实例
	 */
	public static LogPrintExt getInstance(){
		 if(instance == null){
			 createInstance();
		 }
		 return instance;
	 }

	/**
	 * 创建实例方法，该方法会根据config.properties配置创建实例
	 */
	private static void createInstance() {
		String clazzStr = (String) ConfigInit.Config.get("Log_PrintExt");
		if (!StringUtil.checkStr(clazzStr)) {// 默认是按action方法路径进行匹配
			clazzStr = "pub.source.LogPrintExt";
		}
		 try {
			 instance = (LogPrintExt) Class.forName(clazzStr).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
			
		}
	}
	
	/**操作日志输出扩展方法
	 * @param log  日志对象
	 * @param sMd5  日志标识码
	 * @param map 参数集合
	 */
	public  void logOperate(Logger log,String sMd5, Map mp){
		log.info(sMd5 + "[操作]用户区域：[" + mp.get("iDomainId") + "-"
                + mp.get("sDomainName") + "]");
        log.info(sMd5 + "[操作]用户部门：[" + mp.get("iDeptId") + "-"
                + mp.get("sDeptName") + "]");
        log.info(sMd5 + "[操作]用户角色：[" + mp.get("iRoleId") + "-"
                + mp.get("sRoleName") + "]");
        log.info(sMd5 + "[操作]用户信息：[" + mp.get("iStaffId") + "-"
                + mp.get("sStaffAccount") + "-" + mp.get("sStaffName") + "]");
        log.info(sMd5 + "[操作]请求IP：" + mp.get("sIpAddress"));
        log.info(sMd5 + "[操作]请求路径：" + mp.get("sUrl"));
        log.info(sMd5 + "[操作]请求时长：" + mp.get("iCostTime") + "毫秒");
        log.info(sMd5 + "[操作]请求参数：" + mp.get("sParams"));
        log.info(sMd5 + "[操作]关联模块：" + mp.get("rightInfo"));
        log.info(sMd5 + "[操作]当前应用："+ SYSTEM_NAME);
        log.info(sMd5 + "[操作]当前页面："+ mp.get("sReferer"));
	};
	
	/**异常日志输出扩展方法
	 * @param log  日志对象
	 * @param sMd5  日志标识码
	 * @param map 参数集合
	 */
	public void logException(Logger log,String sMd5, Map mp){
		log.error(sMd5 + "[异常]用户信息：[" + mp.get("iStaffId") + "-"
	                + mp.get("sStaffAccount") + "-" + mp.get("sStaffName") + "]");
        log.error(sMd5 + "[异常]关联模块：" + mp.get("rightInfo"));
        log.error(sMd5 + "[异常]请求路径：" + mp.get("sUrl"));
        log.error(sMd5 + "[异常]请求时长：" + mp.get("iCostTime") + "毫秒");
        log.error(sMd5 + "[异常]请求参数：" + mp.get("sParams"));
        log.error(sMd5 + "[异常]程序路径：" + mp.get("sPath"));
        log.error(sMd5 + "[异常]异常提示：" + mp.get("sSelfExceptionMessage"));
        log.error(sMd5 + "[异常]异常名称：" + mp.get("sSysExceptionName") );
        log.error(sMd5 + "[异常]异常详情：" + mp.get("sSysExceptionMessage"));
        log.error(sMd5 + "[异常]执行脚本：[" + mp.get("sDataSource") + "]" +  mp.get("sSql") );
        log.error(sMd5 + "[异常]当前应用："+ SYSTEM_NAME);
        log.error(sMd5 + "[异常]当前页面："+ mp.get("sReferer"));
	}
	
	/**JDBC日志输出扩展方法
	 * @param log  日志对象
	 * @param sMd5  日志标识码
	 * @param map 参数集合
	 */
	public void logSQLExcuteBefore(Logger log,String sMd5, Map map){
		log.info(sMd5 + "[JDBC]执行路径：" + map.get("path"));
        log.info(sMd5 + "[JDBC]执行脚本：["+ map.get("sqlInfo"));
	}
	
	/**JDBC日志输出扩展方法
	 * @param log  日志对象
	 * @param sMd5  日志标识码
	 * @param map 参数集合
	 */
	public void logSQLExcuteAfter(Logger log,String sMd5, Map map){
		String timeDiffResult =  map.get("excuteCostTime") + "毫秒" ;
        log.info(sMd5 + "[JDBC]影响行数：" + map.get("rowCount") + "行");
        log.info(sMd5 + "[JDBC]执行时间：" + timeDiffResult);
        log.info(sMd5 + "[JDBC]当前应用："+ SYSTEM_NAME);
	}
	
	/**菜单点击日志输出扩展方法
	 * @param log  日志对象
	 * @param sMd5  日志标识码
	 * @param map 参数集合
	 */
	public void logMenuClick(Logger log,String sMd5, Map mp){
		log.info(sMd5 + "[菜单]用户区域：[" + mp.get("iDomainId") + "-"
                + mp.get("sDomainName") + "]");
        log.info(sMd5 + "[菜单]用户部门：[" + mp.get("iDeptId") + "-"
                + mp.get("sDeptName") + "]");
        log.info(sMd5 + "[菜单]用户信息：[" + mp.get("iStaffId") + "-"
                + mp.get("sStaffAccount") + "-" + mp.get("sStaffName") + "]");
        log.info(sMd5 + "[菜单]请求IP：" + mp.get("sIpAddress"));
        log.info(sMd5 + "[菜单]请求路径：" +  mp.get("sUrl"));
        log.info(sMd5 + "[菜单]当前应用："+ SYSTEM_NAME);
	}
	
	/**用户登录日志输出扩展方法
	 * @param log  日志对象
	 * @param sMd5  日志标识码
	 * @param map 参数集合
	 */
	public void logLoginIn(Logger log,String sMd5, Map map){
		log.info(sMd5 + "[登录]用户帐号：" + map.get("userName") );
        log.info(sMd5 + "[登录]SessionID：" + map.get("sessionID") );
        log.info(sMd5 + "[登录]浏览器类型：" + map.get("sBrowserType") );
        log.info(sMd5 + "[登录]请求IP：" + map.get("sLoginIP"));
        log.info(sMd5 + "[登录]当前应用："+ SYSTEM_NAME);
	}
	
	/**用户登出日志输出扩展方法
	 * @param log
	 * @param sMd5
	 * @param map
	 */
	public void logLoginOut(Logger log,String sMd5, Map map){
		log.info(sMd5 + "[退出]用户帐号：" + map.get("userName") );
        log.info(sMd5 + "[退出]SessionID：" + map.get("sessionID") );
        log.info(sMd5 + "[退出]当前应用："+ SYSTEM_NAME);
	}
	
	
}
