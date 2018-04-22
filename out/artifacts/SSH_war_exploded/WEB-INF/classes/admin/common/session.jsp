<%@ page contentType="text/html; charset=GBK"%>
<%@ page import="util.RequestUtil" %>
<%
	//读取当前登录用户的基本信息
	String USERID = (String)RequestUtil.getSessionObject(request, "USERID");
	String USERNAME = (String)RequestUtil.getSessionObject(request, "USERNAME");
	String USERCNNAME = (String)RequestUtil.getSessionObject(request, "USERCNNAME");
	String DEPTID = (String)RequestUtil.getSessionObject(request, "DEPTID");
	String DEPTNAME = (String)RequestUtil.getSessionObject(request, "DEPTNAME");
	String REGIONID = (String)RequestUtil.getSessionObject(request, "REGIONID");
	String REGIONNAME = (String)RequestUtil.getSessionObject(request, "REGIONNAME");
%>