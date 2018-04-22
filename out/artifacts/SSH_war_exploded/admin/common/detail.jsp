<%@ page language="java" import="java.util.*" pageEncoding="gbk"%>
<%@ page import="util.StringUtil" %>
<%
String path = request.getContextPath();
String title = "";
if (StringUtil.checkObj(request.getParameter("title"))){
	title = request.getParameter("title");
}
String key = "";
if (StringUtil.checkObj(request.getParameter("key"))){
	key = request.getParameter("key");
}
%>

<html>
  <head>
    <title><%= title %></title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<%@include file="/admin/common/skinCss.jsp" %>
	<%@include file="/admin/common/commonJs.jsp" %>
	<script type="text/javascript" src="<%=path %>/js/showDetail.js"></script>
	<script type="text/javascript">
		$(function(){
			var data = window.parent.commonUtil.getDetailData("<%= key %>");
			commonUtil.initDetail(data);
		});
	</script>
  </head>
  
  <body>
    
  </body>
</html>
