<%@ page contentType="text/html; charset=GBK"%>
<%@ page import="pub.servlet.ConfigInit"%>
<%
String waterPic="";
String useURLRight = ConfigInit.Config.getProperty("useURLRight","");
String isEncryptParams = ConfigInit.Config.getProperty("isEncryptParams","0");

if(request.getAttribute("waterPic")!=null){
	waterPic = (String)request.getAttribute("waterPic");
}
%>
<!-- 设置meta -->
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">

<script type="text/javascript">
var CONTEXT_PATH_NAME = "<%=request.getContextPath()%>";
try{
	if(window.opener && window.opener.MSH){
		window.MSH = window.opener.MSH;
	}else if(window.parent && window.parent.MSH){
		window.MSH= window.parent.MSH;
	}else if(window.dialogArguments && window.dialogArguments.MSH){
		window.MSH= window.dialogArguments.MSH;
	}else if(window.top && window.top.MSH){
		window.MSH= window.top.MSH;
	}
}catch(e){}
//只是否显示试用版水印
var waterPic="<%=waterPic %>";
var useURLRight="<%=useURLRight %>";
var isEncryptParams="<%=isEncryptParams%>";
</script>
<!-- 常用JS -->
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.cookie.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/Toolbar.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonInterface.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/common.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/ajax-request.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/validator.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/cattMsg.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/watermark.js"></script>
<!--<script type="text/javascript" src="<%=request.getContextPath() %>/js/catt_huaChiHelp.js"></script>-->

