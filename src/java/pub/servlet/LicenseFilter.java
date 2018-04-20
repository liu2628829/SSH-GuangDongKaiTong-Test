package pub.servlet;

//import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import pub.source.LogOperateUtil;

import util.RequestUtil;
import util.StringUtil;

/**
 * license������(����ģʽ)
 * @author qiaoqide
 */
public class LicenseFilter implements Filter {

	private FilterConfig config;
	
	/**
	 * ��ʼ��ϵͳ����
	 */
	public void init(FilterConfig _config)
			throws javax.servlet.ServletException {
		config = _config;
		String prefix = RequestUtil.getRealPath(config.getServletContext());
		//System.out.println("resourcePath:========="+prefix);
		String file = config.getInitParameter("config");
		String filePath = prefix + file;
		try {
			// Properties Config=CacheUtil.getConfig();//ö��ͳһ�����
			// ��������
			/*FileInputStream istream = new FileInputStream(filePath);
			ConfigInit.Config.load(istream);
			istream.close();
			�ĳ����·�ʽ�����ܼ���weblogic
			*/
			ConfigInit.Config.load(config.getServletContext().getResourceAsStream(file));
			// ģ����action��ϵ��Ϣ����
			ConfigInit.initActionRel();
			// ����Ȩ�޲˵�������
			ConfigInit.initRightMenuList();
			
			ConfigInit.Config.setProperty("classes", prefix+"/WEB-INF/classes/");
			
			//���ֵ����ͷ�Ŀո񣬱�������ʱ����ûע�⵽���˶���Ŀո�
			for(Iterator<Object> it = ConfigInit.Config.keySet().iterator(); it.hasNext();){
				String key = StringUtil.toString(it.next());
				String value = ConfigInit.Config.getProperty(key);
				if(value != null){
					ConfigInit.Config.setProperty(key, value.trim());
				}
			}
			
		} catch (IOException e) {
			LogOperateUtil.logException(e, "LicenseFilter ������ʼ��ϵͳ����ʱ����!");
		}
	}

	/**
	 * �������
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws java.io.IOException, ServletException {
		chain.doFilter(request, response);
	}
	
	public void destroy() {

	}
	
}
