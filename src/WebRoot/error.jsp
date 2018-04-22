<%@ page language="java" contentType="text/html; charset=gbk" pageEncoding="gbk"%>
<%@page import="util.BaseRuntimeException"%>
<%@page import="util.RequestUtil"%>
<%@page import="org.apache.struts2.ServletActionContext"%> 
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="util.JackJson"%>
<%@page import="util.I18nConfig"%> 
<%@page import="pub.source.DatabaseUtil"%> 
<%@page import="util.MD5Tool"%>
<%@ page import="com.opensymphony.xwork2.*"%>
<%@ page import="com.opensymphony.xwork2.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ page import="util.SessionUtil" %>
<%
try{
//Throwable exception= (Throwable)stack.findValue("exception");
//系统异常内容
/*String content=exception.getMessage();*/
	String err=(String)request.getAttribute("err");
	if(err!=null){
		out.print("<h1>"+err+"</h1");
	}else{
		if("act".equals(request.getParameter("err"))){
			out.print("<h1>不能在地址栏直接访问此路径!</h1");
		}else{
			String staffId=(String)SessionUtil.getAttribute("USERID", session);//操作用户ID
			String params=RequestUtil.getMapByRequest(request).toString();//请求参数
			String defineContent="错误号:"+("404".equals(request.getParameter("err"))?"404,访问页面不存在":"500,访问页面不可用"); //自定义异常信息
			String url=request.getRequestURI();
			//异常发生路径
			StackTraceElement[] ste = new Throwable().getStackTrace();
			StringBuffer CallStack = new StringBuffer();
			for (int i = 0; i < ste.length; i++) {
				CallStack.append(ste[i].toString() + " | ");
				if (i > 2)break;
			}
			ste=null;
			String path= CallStack.toString();
			//System.out.println("staffId:"+staffId);
			//System.out.println("params:"+params);
			//System.out.println("content:"+content);
			//System.out.println("path:"+path);
			//System.out.println("defined:"+defineContent+"----"+err);
		}
	}
}catch(Exception e){
	//System.out.println("---------------mm");
}

%>
<%
String path = request.getContextPath();
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>出现未知错误</title>
<link type="text/css" rel="stylesheet" href="<%=path%>/demo/errorPage/css/404.css" />
</head>

<body>

<div id="error">
  <div id="faceBorder">
  <table width="80" cellspacing="0" cellpadding="0" border="0" align="center" style="overflow:hidden;" id="face">
  <tbody>
  <tr>
    <td>
      <div style="OVERFLOW: hidden; WIDTH: 80px; COLOR: #ffffff" id="demo">
      <table cellspacing="0" cellpadding="0" border="0" align="left" cellspace="0">
        <tbody>
        <tr>
          <td valign="top" id="demo1"><table width="640" cellspacing="0" cellpadding="0" border="0" id="faceTable">
            <tbody><tr>
              <td width="80"><div align="center"><img src="<%=path%>/demo/errorPage/images/1.gif"></div></td>
              <td width="80"><div align="center"><img src="<%=path%>/demo/errorPage/images/2.gif"></div></td>
              <td width="80"><div align="center"><img src="<%=path%>/demo/errorPage/images/3.gif"></div></td>
              <td width="80"><div align="center"><img src="<%=path%>/demo/errorPage/images/4.gif"></div></td>
              <td width="80"><div align="center"><img src="<%=path%>/demo/errorPage/images/5.gif"></div></td>
              <td width="80"><div align="center"><img src="<%=path%>/demo/errorPage/images/6.gif"></div></td>
              <td width="80"><div align="center"><img src="<%=path%>/demo/errorPage/images/7.gif"></div></td>
              <td width="80"><div align="center"><img src="<%=path%>/demo/errorPage/images/8.gif"></div></td>
            </tr>
          </tbody></table></td>
          <td valign="top" id="demo2"></td></tr></tbody></table>
      </div>        </td></tr></tbody></table>
      </div>
<div id="message">
      <p><strong>对不起，你所访问的页面不存在或暂时不可浏览。</strong></p>
      <p><strong>Sorry, the page you are visiting does not exist, or isn't available at this moment.</strong></p>
      </div>
	  <a href="<%=path%>/login1.jsp"><div id="button">
      <div id="back"><strong>返回首页</strong></div>
      </div>
</a>
  <script src="<%=path%>/demo/errorPage/js/scrollpic.js" language="JavaScript"></script>
</div>
</body>
</html>