package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**Request����Ĺ�����
 * modify on Jul 2, 2013 11:22:43 AM 
 * @author tangyj
 */
public class RequestUtil {

	/**�첽���󷵻�
	 * @param encoding �����ʽ
	 * @param data data
	 * @param response HttpServletResponse
	 */
	public static void responseOut(String encoding, String data,
			HttpServletResponse response) {
		response.setContentType("text/html; charset=" + encoding);
		try {
			PrintWriter pw = response.getWriter();
			pw.print(data);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**��ȡrequest���������в����������õ�map��
	 * @param request HttpServletRequest
	 * @return ��request�����������װ��map������URL����ͨ��form���ύ�Ĳ�����
	 */
	public static Map getMapByRequest(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String paraName = (String) enu.nextElement();
			String paraValue = request.getParameter(paraName);
			if (paraValue != null && !"".equals(paraValue)) {
				map.put(paraName, paraValue.trim());
			}
		}
		if (StringUtil.checkObj(request.getAttribute("sUrl"))) {
			map.put("sURLs", request.getAttribute("sUrl").toString());
		}
		return map;
	}

	// ����KEY��ȡsession��Ӧ�Ķ���
	public static Object getSessionObject(HttpServletRequest request, String key) {
		HttpSession session = request.getSession();
		return SessionUtil.getAttribute(key, session);
	}

	/**�����ķ�ʽ���ļ���Ӧ���ͻ��ˣ�һ�������ļ����� ��ʱ���ļ���
	 * �Ƿ�����Ӧ�÷���������ĵ�Ŀ¼����Ҫ�ȶ�����
	 * Ȼ����д��(������A��ǩ��ֱ�����ص�)
	 * @param path �ļ�·��
	 * @param fileName �ļ�����
	 * @param response HttpServletResponse
	 * @throws Exception
	 */
	public static void readFile(String path, String fileName,
			HttpServletResponse response) throws Exception {
		InputStream inStream = null;
		try {
			File pathsavefile = new File(path);
			if (!StringUtil.checkStr(fileName)) {
				String pths[] = path.replaceAll("/", "\\\\").split("\\\\");
				fileName = pths[pths.length - 1];// ���洰������ʾ���ļ���
			}
			inStream = new FileInputStream(pathsavefile);
			
			readFile(inStream, fileName, response);
		} catch (IOException ex) {
			throw ex;
		} 
	}
	
	/**�����ķ�ʽ���ļ���Ӧ���ͻ��ˣ�һ�������ļ����� ��ʱ���ļ���
	 * �Ƿ�����Ӧ�÷���������ĵ�Ŀ¼����Ҫ�ȶ�����
	 * Ȼ����д��(������A��ǩ��ֱ�����ص�)
	 * @param inStream �ļ�������
	 * @param fileName �ļ�����
	 * @param response HttpServletResponse
	 * @throws Exception
	 */
	public static void readFile(InputStream inStream, String fileName,
			HttpServletResponse response) throws Exception {
		ServletOutputStream out = null;
		try {
			
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setContentType("application/force-download;charset=GBK");
			// fileName=new
			// String(fileName.getBytes(),"UTF-8");//response.encodeURL();//ת��
			fileName = fileName.replace(";", "��"); 
			fileName = new String(fileName.getBytes("GBK"), "ISO8859-1");//
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			out = response.getOutputStream();
			byte[] b = new byte[1024];
			int len;
			while ((len = inStream.read(b)) > 0)
				out.write(b, 0, len);
			response.setStatus(response.SC_OK);
			response.flushBuffer();
		} catch (IOException ex) {
			throw ex;
		} finally {
			/*��ʱ���رգ�ӦΪ�쳣�����ף��׸�action��out������������
			if (out != null){
				out.close();
			}
			*/
			if (inStream != null){
				inStream.close();
			}
		}
	}

	/**���jquery��ajax����
	 * @param request HttpServletRequest
	 * @return true:��Ajax����
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		String header = request.getHeader("X-Requested-With");
		return (header != null && "XMLHttpRequest".equals(header));
	}

	/**�������ͻ��˵�IP
	 * @param request HttpServletRequest
	 * @return IP
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr(); //ȡֵ����Ϊ0:0:0:0:0:0:0:1
		}
		
		if (ip != null && (ip.indexOf("127.0.0.1") >= 0 || ip.indexOf("0:0:0:0:0:0:0:1") >= 0
				|| ip.indexOf("localhost") >= 0)) {
			ip = StringUtil.getIPAddress();
		}
		return ip;
	}
	
	/**
	 * ��ȡ����·��
	 * @param request HttpServletRequest����
	 * @return Ӧ�þ���·��
	 */
	public static String getRealPath(HttpServletRequest request){
		String realPath = RequestUtil.getRealPath(request.getSession().getServletContext());
		return realPath;
	}
	
	/**
	 * ��ȡ����·��
	 * @param context ServletContext����
	 * @return Ӧ�þ���·��
	 */
	public static String getRealPath(ServletContext context){
		//ϵͳ�ļ��ָ���
		String separator = System.getProperty("file.separator");
		String realPath = context.getRealPath("/");
		realPath = realPath.endsWith(separator) ? realPath : (realPath + separator);
		return realPath;
	}
	
	/**
	 * gmURL��URL���������ıȽ�
	 * @param targetUrl Ŀ��URL
	 * @param comparedUrl ��Ҫ�Ƚϵ�URL
	 * @param paramNames null������Բ���������֤��
	 * 										   size==0����Ҫ��comparedUrl�����в���������֤��
	 * 										   size>0��ֻ�豣֤���ֲ�����ֵһ��
	 * @return true��ƥ��ɹ�; false:ƥ��ʧ��
	 */
	public static boolean compareURL(String targetUrl, String comparedUrl,
			String[] paramNames) {
		boolean resultFlag = false;
		// ��������Ƚϵ�URL��һ��Ϊ����ֱ�ӷ���ʧ��
		if (!(StringUtil.checkStr(targetUrl) && StringUtil
				.checkStr(comparedUrl))) {
			return false;
		}
		// ����URL��ȫһ�¾�ֱ�ӷ�����
		// if(targetUrl.equals(comparedUrl)){
		// return true;
		// }
		// ĿǰURL�ͱȽ�URL
		String[] urlSplit_1 = getUrlAndParam(targetUrl);
		String url_1 = urlSplit_1[0];// URL
		String paramStr_1 = urlSplit_1[1];// ����
		String[] urlSplit_2 = getUrlAndParam(comparedUrl);
		String url_2 = urlSplit_2[0];// URL
		String paramStr_2 = urlSplit_2[1];// ����

		// ��URL�Ա�
		if (url_1.equals(url_2)) {
			// ���ñȽϲ���
			if (paramNames == null) {
				return true;
			}
		} else {// URL����һ�¾�ֱ�ӷ��أ����ٽ��в�����֤
			return false;
		}
		// ��ʼ���в����Ƚ�
		Map<String, String> paramMap_1 = getParamMap(paramStr_1, "&", "=");// ��������
		Map<String, String> paramMap_2 = getParamMap(paramStr_2, "&", "=");// ��������
		boolean paramAllEqual = true;
		if (paramNames.length == 0) {// ��Ҫȫ�������Ƚ�ʱ����Ҫȡ�Ƚ�URL�Ĳ���
			paramNames = paramMap_2.keySet().toArray(paramNames);
		}
		String paramMap2Value = null;
		for (String paramName : paramNames) {
			paramMap2Value = paramMap_2.get(paramName);
			// paramMap_2 û�и��ֶΣ������ֶε�ֵ����Ⱦ�Ϊ��
			if (paramMap2Value == null
					|| false == paramMap2Value
							.equals(paramMap_1.get(paramName))) {
				paramAllEqual = false;
				break;
			}
		}

		if (paramAllEqual) {
			resultFlag = true;
		}
		return resultFlag;
	}

	/**
	 * ���������͵ķ���������Map����
	 * 
	 * @param paramStr a=1�ָ���b=2
	 * @param paramSplitRegex �����ָ���
	 * @param paramValueSplitRegex  ������ֵ�ķָ���
	 * @return ��װ��map����
	 */
	public static Map<String, String> getParamMap(String paramStr,
			String paramSplitRegex, String paramValueSplitRegex) {
		Map<String, String> paramMap = new HashMap<String, String>();
		if (paramStr != null && paramStr.length() > 0) {
			String[] paramAndValueStrSplit = paramStr.split(paramSplitRegex);
			String[] paramAndValueSplit = null;
			for (String paramAndValueStr : paramAndValueStrSplit) {
				paramAndValueSplit = paramAndValueStr
						.split(paramValueSplitRegex);
				if (paramAndValueSplit.length == 2) {
					paramMap.put(
							paramAndValueSplit[0].trim().replace("\"", ""),
							paramAndValueSplit[1].trim().replace("\"", ""));
				}
			}
		}
		return paramMap;
	}

	/**
	 * ͨ��url��ȡurl�Ͳ���
	 * 
	 * @param url URL
	 * @return ��һ��Ԫ��ΪURL���ڶ���Ԫ��Ϊ����
	 */
	public static String[] getUrlAndParam(String url) {
		String[] returnValue = { "", "" };
		if (url != null) {
			String[] urlSplit = url.split("\\?");
			returnValue[0] = urlSplit[0].trim();// URL
			if (urlSplit.length > 1) {
				returnValue[1] = urlSplit[1].trim();
			}
		}
		return returnValue;
	}
	
	public static String mergeURIAndParma(HttpServletRequest request){
		StringBuilder sb = new StringBuilder();
		String appPath = request.getContextPath();
		String url = request.getRequestURI();
        String queryStr = request.getQueryString();
        if(StringUtil.checkStr(url)){
        	//�޸���Ӧ��������� tangyj 2014-11-28
			if(StringUtil.checkStr(appPath)){
				url = url.substring(appPath.length() + 1);
			}
			if(url.startsWith("/")){
				url = url.substring(1);
			}
			sb.append(url);
        }
        if(StringUtil.checkStr(url) && StringUtil.checkStr(queryStr)){
        	sb.append("?");
        	sb.append(StringUtil.checkStr(queryStr) ? queryStr : "");
        }
		return sb.toString();
		
	}
	
	
}