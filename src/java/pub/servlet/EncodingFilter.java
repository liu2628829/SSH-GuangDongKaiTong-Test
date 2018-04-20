package pub.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.BaseRuntimeException;
import util.DESTool;
import util.JackJson;

/**
 * 编码过滤器，若开启了前端参数加密，同时担当解密过滤功能
 * @author pengjiewen
 * @version Aug 20, 2013
 */
public class EncodingFilter implements Filter {
	
	/** 中文正则表达式 */
	private Pattern pattern=Pattern.compile("[\\u4e00-\\u9fa5]");
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;  
		String queryStr = req.getQueryString();
		//请求参数转码
		if(queryStr != null){
			queryStr = new String(queryStr.getBytes("ISO-8859-1"),"GBK");
			Matcher matcher=pattern.matcher(queryStr);
			if(matcher.find()){
				request.setCharacterEncoding("GBK");
			}else{
				request.setCharacterEncoding("UTF-8");
			}
		}else{
			request.setCharacterEncoding("UTF-8");
		}
		
		//参数解密
		try {
            decrypt(request, response);
        } catch (Exception e) {
            //是否强制加密（存在未加密参数时强制退出）
            if("1".equals(ConfigInit.Config
                    .getProperty("forceEncryptParams","0"))){
                forceEncryptFilter(request, response, chain);
                return;
            }
        }
		//原请求参数被改，必需以一个自定的请求对象继续流转，否则被修改的参数，在后续流转中取不到
		//ParameterRequestWrapper paramRequest = new ParameterRequestWrapper(req, request.getParameterMap());
		chain.doFilter(request, response);
	}

	/**
	 * 参数解密操作
	 * @param request request对象
	 * @exception 解密异常或存在非前端加密参数时抛出异常
	 */
	public void decrypt(ServletRequest request, ServletResponse response)
	    throws Exception{
	    //若前端传入isforceEncrypt参数，则此请求为强制加密，无视isEncryptParams配置
		if(request.getParameter("isforceEncryptParams")==null && 
		        !"1".equals(ConfigInit.Config.getProperty("isEncryptParams","0"))){
			return;
        }
		//是否强制加密（存在未加密参数时强制退出）
		boolean forceEncry = "1".equals(
		        ConfigInit.Config.getProperty("forceEncryptParams","0"));
    	Map<String, Object> params = request.getParameterMap();
    	Method method = null;
    	try{
    	    method = params.getClass().getMethod("setLocked", 
                    new Class[]{boolean.class});
    	    method.invoke(params, new Object[]{new Boolean(false)});  
    	}catch(NoSuchMethodException e){
    	    //websphere环境无此方法
    	    method = null;
    	}
        //ParameterMap为加锁hashmap，须通过反射解除锁定
        for(String key : params.keySet()){
            String[] value = ((String[])(params.get(key)));
            for(int i = 0; i < value.length; i++){
                String v = DESTool.decryptURL(value[i]);
                if(v != null){//为加密参数
                    value[i] = URLDecoder.decode(v, "UTF-8");
                }else if(forceEncry){
                    throw new BaseRuntimeException("参数解密时出错！", null);
                }
            }
            params.put(key, value);
        }
        //解密参数完毕后，重新锁定ParameterMap
        if(method != null){
            method.invoke(params, new Object[]{new Boolean(true)}); 
        }
	}
	
	/**
	 * 前端参数强制加密时，禁止访问处理
	 * @param request 请求
	 * @param response 响应
	 * @param chain 过滤处理器
	 */
	private void forceEncryptFilter(ServletRequest request, 
	        ServletResponse response, FilterChain chain) throws 
	        IOException, ServletException{
        //异常信息提示到界面
        String header = ((HttpServletRequest) request).
            getHeader("X-Requested-With");
        //异步发起的请求（基本不出现）
        if((header != null && "XMLHttpRequest".equals(header))){
            Map<String, String> jsonMap = new HashMap<String, String>();
            jsonMap.put("SUCCESS", "false");
            jsonMap.put("MESSAGE", "请求包含非法参数，禁止访问！");
            String json = JackJson.getBasetJsonData(jsonMap);
            PrintWriter pw = response.getWriter();
            pw.print(json);
            pw.close();
        }else{//http请求
            ((HttpServletResponse) response)
                .sendRedirect(((HttpServletRequest)request)
                        .getContextPath() + "/error.jsp");
            chain.doFilter(request, response);
        }
	}

	public void destroy() {
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
