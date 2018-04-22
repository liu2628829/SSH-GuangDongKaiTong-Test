<%@ page contentType="text/html; charset=GBK"%>
<%
    String style="default";  
    Cookie cookies[]=request.getCookies(); 
    if(cookies!=null) 
    for(int i = 0;i<cookies.length;i++){ 
    	if("style".equalsIgnoreCase(cookies[i].getName())){
    		style=cookies[i].getValue();break;
    	}
    }
%>
<!-- »»·ôÑùÊ½ -->
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/css/default.css">
<link rel="stylesheet" id="R_Toolbar" type="text/css" href="<%=request.getContextPath() %>/skin/<%=style %>/Toolbar.css">
<link rel="stylesheet" id="R_easyui" type="text/css" href="<%=request.getContextPath() %>/skin/<%=style %>/easyui.css">
<link rel="stylesheet" id="R_default" type="text/css" href="<%=request.getContextPath() %>/skin/<%=style %>/default.css">
<link rel="stylesheet" id="R_style" type="text/css" href="<%=request.getContextPath() %>/skin/<%=style %>/style.css" id="compStyle">

