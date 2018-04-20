package pub.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import util.StringUtil;

/**
 * log4j 监听器
 * 
 * @author gaotao 2014-10-13
 */
public class Log4jListener  implements ServletContextListener{

	public void contextDestroyed(ServletContextEvent context) {
		
	}

	/**初始化log4j.properties需要的目录参数*/
	public void contextInitialized(ServletContextEvent context) {
		String path = context.getServletContext().getContextPath();
		String fileDir = context.getServletContext().getInitParameter("Log_savePath");
		
		//web.xml中有指定目录则以指定为准，否则默认会生成在应用中间件所在安装目录的根目录
		String prefix = ((StringUtil.checkStr(fileDir)) ? fileDir : "/RunLog")+path;
		
		//版本不同，可能参数有以下3种情况
		String systemName = path.replace("/", "");
        //版本不同，log4j.properties里可能参数有以下3种情况,所以都处理一下
        System.setProperty("SSH_WORKDIR", prefix);
        System.setProperty("SSH3.root", prefix);
        System.setProperty(systemName+".root", prefix);
       
        if(StringUtil.checkStr(systemName)){
      	  System.setProperty("SYSTEM_NAME", systemName);
        }
	}
	
}
