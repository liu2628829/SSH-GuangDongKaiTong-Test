package pub.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import util.StringUtil;

/**
 * log4j ������
 * 
 * @author gaotao 2014-10-13
 */
public class Log4jListener  implements ServletContextListener{

	public void contextDestroyed(ServletContextEvent context) {
		
	}

	/**��ʼ��log4j.properties��Ҫ��Ŀ¼����*/
	public void contextInitialized(ServletContextEvent context) {
		String path = context.getServletContext().getContextPath();
		String fileDir = context.getServletContext().getInitParameter("Log_savePath");
		
		//web.xml����ָ��Ŀ¼����ָ��Ϊ׼������Ĭ�ϻ�������Ӧ���м�����ڰ�װĿ¼�ĸ�Ŀ¼
		String prefix = ((StringUtil.checkStr(fileDir)) ? fileDir : "/RunLog")+path;
		
		//�汾��ͬ�����ܲ���������3�����
		String systemName = path.replace("/", "");
        //�汾��ͬ��log4j.properties����ܲ���������3�����,���Զ�����һ��
        System.setProperty("SSH_WORKDIR", prefix);
        System.setProperty("SSH3.root", prefix);
        System.setProperty(systemName+".root", prefix);
       
        if(StringUtil.checkStr(systemName)){
      	  System.setProperty("SYSTEM_NAME", systemName);
        }
	}
	
}
