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
 * �������������������ǰ�˲������ܣ�ͬʱ�������ܹ��˹���
 * @author pengjiewen
 * @version Aug 20, 2013
 */
public class EncodingFilter implements Filter {
	
	/** ����������ʽ */
	private Pattern pattern=Pattern.compile("[\\u4e00-\\u9fa5]");
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;  
		String queryStr = req.getQueryString();
		//�������ת��
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
		
		//��������
		try {
            decrypt(request, response);
        } catch (Exception e) {
            //�Ƿ�ǿ�Ƽ��ܣ�����δ���ܲ���ʱǿ���˳���
            if("1".equals(ConfigInit.Config
                    .getProperty("forceEncryptParams","0"))){
                forceEncryptFilter(request, response, chain);
                return;
            }
        }
		//ԭ����������ģ�������һ���Զ���������������ת�������޸ĵĲ������ں�����ת��ȡ����
		//ParameterRequestWrapper paramRequest = new ParameterRequestWrapper(req, request.getParameterMap());
		chain.doFilter(request, response);
	}

	/**
	 * �������ܲ���
	 * @param request request����
	 * @exception �����쳣����ڷ�ǰ�˼��ܲ���ʱ�׳��쳣
	 */
	public void decrypt(ServletRequest request, ServletResponse response)
	    throws Exception{
	    //��ǰ�˴���isforceEncrypt�������������Ϊǿ�Ƽ��ܣ�����isEncryptParams����
		if(request.getParameter("isforceEncryptParams")==null && 
		        !"1".equals(ConfigInit.Config.getProperty("isEncryptParams","0"))){
			return;
        }
		//�Ƿ�ǿ�Ƽ��ܣ�����δ���ܲ���ʱǿ���˳���
		boolean forceEncry = "1".equals(
		        ConfigInit.Config.getProperty("forceEncryptParams","0"));
    	Map<String, Object> params = request.getParameterMap();
    	Method method = null;
    	try{
    	    method = params.getClass().getMethod("setLocked", 
                    new Class[]{boolean.class});
    	    method.invoke(params, new Object[]{new Boolean(false)});  
    	}catch(NoSuchMethodException e){
    	    //websphere�����޴˷���
    	    method = null;
    	}
        //ParameterMapΪ����hashmap����ͨ������������
        for(String key : params.keySet()){
            String[] value = ((String[])(params.get(key)));
            for(int i = 0; i < value.length; i++){
                String v = DESTool.decryptURL(value[i]);
                if(v != null){//Ϊ���ܲ���
                    value[i] = URLDecoder.decode(v, "UTF-8");
                }else if(forceEncry){
                    throw new BaseRuntimeException("��������ʱ����", null);
                }
            }
            params.put(key, value);
        }
        //���ܲ�����Ϻ���������ParameterMap
        if(method != null){
            method.invoke(params, new Object[]{new Boolean(true)}); 
        }
	}
	
	/**
	 * ǰ�˲���ǿ�Ƽ���ʱ����ֹ���ʴ���
	 * @param request ����
	 * @param response ��Ӧ
	 * @param chain ���˴�����
	 */
	private void forceEncryptFilter(ServletRequest request, 
	        ServletResponse response, FilterChain chain) throws 
	        IOException, ServletException{
        //�쳣��Ϣ��ʾ������
        String header = ((HttpServletRequest) request).
            getHeader("X-Requested-With");
        //�첽��������󣨻��������֣�
        if((header != null && "XMLHttpRequest".equals(header))){
            Map<String, String> jsonMap = new HashMap<String, String>();
            jsonMap.put("SUCCESS", "false");
            jsonMap.put("MESSAGE", "��������Ƿ���������ֹ���ʣ�");
            String json = JackJson.getBasetJsonData(jsonMap);
            PrintWriter pw = response.getWriter();
            pw.print(json);
            pw.close();
        }else{//http����
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
