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

/**Request对象的工具类
 * modify on Jul 2, 2013 11:22:43 AM 
 * @author tangyj
 */
public class RequestUtil {

	/**异步请求返回
	 * @param encoding 编码格式
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

	/**获取request对象中所有参数，并设置到map中
	 * @param request HttpServletRequest
	 * @return 将request的请求参数封装成map（包括URL传和通过form表单提交的参数）
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

	// 根据KEY获取session对应的对象
	public static Object getSessionObject(HttpServletRequest request, String key) {
		HttpSession session = request.getSession();
		return SessionUtil.getAttribute(key, session);
	}

	/**以流的方式将文件响应到客户端，一般用于文件下载 此时的文件，
	 * 是放在了应用服务器以外的的目录，需要先读到，
	 * 然后再写出(即不是A标签能直接下载的)
	 * @param path 文件路径
	 * @param fileName 文件名称
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
				fileName = pths[pths.length - 1];// 保存窗口中显示的文件名
			}
			inStream = new FileInputStream(pathsavefile);
			
			readFile(inStream, fileName, response);
		} catch (IOException ex) {
			throw ex;
		} 
	}
	
	/**以流的方式将文件响应到客户端，一般用于文件下载 此时的文件，
	 * 是放在了应用服务器以外的的目录，需要先读到，
	 * 然后再写出(即不是A标签能直接下载的)
	 * @param inStream 文件流对象
	 * @param fileName 文件名称
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
			// String(fileName.getBytes(),"UTF-8");//response.encodeURL();//转码
			fileName = fileName.replace(";", "；"); 
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
			/*此时不关闭，应为异常会外抛，抛给action后，out还有其它作用
			if (out != null){
				out.close();
			}
			*/
			if (inStream != null){
				inStream.close();
			}
		}
	}

	/**针对jquery的ajax请求
	 * @param request HttpServletRequest
	 * @return true:是Ajax请求
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		String header = request.getHeader("X-Requested-With");
		return (header != null && "XMLHttpRequest".equals(header));
	}

	/**获得请求客户端的IP
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
			ip = request.getRemoteAddr(); //取值可能为0:0:0:0:0:0:0:1
		}
		
		if (ip != null && (ip.indexOf("127.0.0.1") >= 0 || ip.indexOf("0:0:0:0:0:0:0:1") >= 0
				|| ip.indexOf("localhost") >= 0)) {
			ip = StringUtil.getIPAddress();
		}
		return ip;
	}
	
	/**
	 * 获取绝对路径
	 * @param request HttpServletRequest对象
	 * @return 应用绝对路径
	 */
	public static String getRealPath(HttpServletRequest request){
		String realPath = RequestUtil.getRealPath(request.getSession().getServletContext());
		return realPath;
	}
	
	/**
	 * 获取绝对路径
	 * @param context ServletContext对象
	 * @return 应用绝对路径
	 */
	public static String getRealPath(ServletContext context){
		//系统文件分隔符
		String separator = System.getProperty("file.separator");
		String realPath = context.getRealPath("/");
		realPath = realPath.endsWith(separator) ? realPath : (realPath + separator);
		return realPath;
	}
	
	/**
	 * gmURL和URL所带参数的比较
	 * @param targetUrl 目标URL
	 * @param comparedUrl 需要比较的URL
	 * @param paramNames null：不需对参数进行验证；
	 * 										   size==0：需要对comparedUrl中所有参数进行验证；
	 * 										   size>0：只需保证部分参数的值一致
	 * @return true：匹配成功; false:匹配失败
	 */
	public static boolean compareURL(String targetUrl, String comparedUrl,
			String[] paramNames) {
		boolean resultFlag = false;
		// 如果两个比较的URL有一个为空则直接返回失败
		if (!(StringUtil.checkStr(targetUrl) && StringUtil
				.checkStr(comparedUrl))) {
			return false;
		}
		// 两个URL完全一致就直接返回真
		// if(targetUrl.equals(comparedUrl)){
		// return true;
		// }
		// 目前URL和比较URL
		String[] urlSplit_1 = getUrlAndParam(targetUrl);
		String url_1 = urlSplit_1[0];// URL
		String paramStr_1 = urlSplit_1[1];// 参数
		String[] urlSplit_2 = getUrlAndParam(comparedUrl);
		String url_2 = urlSplit_2[0];// URL
		String paramStr_2 = urlSplit_2[1];// 参数

		// 纯URL对比
		if (url_1.equals(url_2)) {
			// 不用比较参数
			if (paramNames == null) {
				return true;
			}
		} else {// URL都不一致就直接返回，不再进行参数验证
			return false;
		}
		// 开始进行参数比较
		Map<String, String> paramMap_1 = getParamMap(paramStr_1, "&", "=");// 参数集合
		Map<String, String> paramMap_2 = getParamMap(paramStr_2, "&", "=");// 参数集合
		boolean paramAllEqual = true;
		if (paramNames.length == 0) {// 需要全部参数比较时，先要取比较URL的参数
			paramNames = paramMap_2.keySet().toArray(paramNames);
		}
		String paramMap2Value = null;
		for (String paramName : paramNames) {
			paramMap2Value = paramMap_2.get(paramName);
			// paramMap_2 没有该字段，或两字段的值不相等就为假
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
	 * 将参数类型的符串解析成Map集合
	 * 
	 * @param paramStr a=1分隔符b=2
	 * @param paramSplitRegex 参数分隔符
	 * @param paramValueSplitRegex  参数与值的分隔符
	 * @return 封装的map数据
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
	 * 通过url获取url和参数
	 * 
	 * @param url URL
	 * @return 第一个元素为URL；第二个元素为参数
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
        	//修复无应用名的情况 tangyj 2014-11-28
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