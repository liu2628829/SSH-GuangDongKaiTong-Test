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
 * license过滤器(开发模式)
 * @author qiaoqide
 */
public class LicenseFilter implements Filter {

	private FilterConfig config;
	
	/**
	 * 初始化系统参数
	 */
	public void init(FilterConfig _config)
			throws javax.servlet.ServletException {
		config = _config;
		String prefix = RequestUtil.getRealPath(config.getServletContext());
		//System.out.println("resourcePath:========="+prefix);
		String file = config.getInitParameter("config");
		String filePath = prefix + file;
		try {
			// Properties Config=CacheUtil.getConfig();//枚举统一管理后
			// 基础配置
			/*FileInputStream istream = new FileInputStream(filePath);
			ConfigInit.Config.load(istream);
			istream.close();
			改成如下方式，才能兼容weblogic
			*/
			ConfigInit.Config.load(config.getServletContext().getResourceAsStream(file));
			// 模块与action关系信息缓存
			ConfigInit.initActionRel();
			// 所有权限菜单的数据
			ConfigInit.initRightMenuList();
			
			ConfigInit.Config.setProperty("classes", prefix+"/WEB-INF/classes/");
			
			//清空值的两头的空格，避免配置时可能没注意到加了多余的空格
			for(Iterator<Object> it = ConfigInit.Config.keySet().iterator(); it.hasNext();){
				String key = StringUtil.toString(it.next());
				String value = ConfigInit.Config.getProperty(key);
				if(value != null){
					ConfigInit.Config.setProperty(key, value.trim());
				}
			}
			
		} catch (IOException e) {
			LogOperateUtil.logException(e, "LicenseFilter 启动初始化系统参数时出错!");
		}
	}

	/**
	 * 请求过滤
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws java.io.IOException, ServletException {
		chain.doFilter(request, response);
	}
	
	public void destroy() {

	}
	
}
