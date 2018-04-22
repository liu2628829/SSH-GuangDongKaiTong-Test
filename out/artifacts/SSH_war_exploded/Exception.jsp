<%@page language="java" contentType="text/html; charset=gbk" pageEncoding="gbk"%>
<%@page import="util.StringUtil"%>
<%@page import="pub.source.LogOperateUtil"%> 
<%@ page isELIgnored="false" %>
<%@page import="util.JackJson"%>
<%@ page import="com.opensymphony.xwork2.ActionContext"%>
<%@ page import="com.opensymphony.xwork2.util.ValueStack"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.io.IOException"%>
<%
    Throwable exception = null;
    String sSelfExceptionMessage = "";
    ActionContext act = ActionContext.getContext();
    
    //update by qiaoqd 2014-2-11
    if(act != null){ //从action跳转过来的
       ValueStack stack = act.getValueStack();
	   exception = (Throwable)stack.findValue("exception"); //获得异常
	   //记录异常并返回错误提示 tangyj 2013-05-23
       sSelfExceptionMessage = LogOperateUtil.logException(exception);
	   
    }else{ //说明是从过滤器跳转过来的
	   exception = (Throwable) request.getAttribute("exception"); //获得异常
	   sSelfExceptionMessage = LogOperateUtil.logException(exception, request);
    }
    
    //异常信息提示到界面
    String header = request.getHeader("X-Requested-With");
    if((header != null && "XMLHttpRequest".equals(header))){//异步发起的请求
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("SUCCESS", "false");
        jsonMap.put("MESSAGE", sSelfExceptionMessage);
        String json = JackJson.getBasetJsonData(jsonMap);
        response.setContentType("text/xml; charset=GBK");
        try {
            out.print(json);
        } catch (IOException e) {}
    }else{//http 请求
        out.print("您好!系统运行异常。<br><br>");
        if(StringUtil.checkObj(sSelfExceptionMessage)){
            out.print(sSelfExceptionMessage);
        }
    }
%>