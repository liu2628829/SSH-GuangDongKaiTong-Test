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
    if(act != null){ //��action��ת������
       ValueStack stack = act.getValueStack();
	   exception = (Throwable)stack.findValue("exception"); //����쳣
	   //��¼�쳣�����ش�����ʾ tangyj 2013-05-23
       sSelfExceptionMessage = LogOperateUtil.logException(exception);
	   
    }else{ //˵���Ǵӹ�������ת������
	   exception = (Throwable) request.getAttribute("exception"); //����쳣
	   sSelfExceptionMessage = LogOperateUtil.logException(exception, request);
    }
    
    //�쳣��Ϣ��ʾ������
    String header = request.getHeader("X-Requested-With");
    if((header != null && "XMLHttpRequest".equals(header))){//�첽���������
        Map<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("SUCCESS", "false");
        jsonMap.put("MESSAGE", sSelfExceptionMessage);
        String json = JackJson.getBasetJsonData(jsonMap);
        response.setContentType("text/xml; charset=GBK");
        try {
            out.print(json);
        } catch (IOException e) {}
    }else{//http ����
        out.print("����!ϵͳ�����쳣��<br><br>");
        if(StringUtil.checkObj(sSelfExceptionMessage)){
            out.print(sSelfExceptionMessage);
        }
    }
%>