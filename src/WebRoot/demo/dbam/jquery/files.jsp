<%@ page contentType="text/html; charset=GBK"%>
<%@ page isELIgnored="false" %>
<%@ page import="util.StringUtil" %>
<%
	String path = request.getContextPath();
%>
<html>
<head>
	<title>文件管理</title>
	<meta http-equiv="Content-Type" content="text/html; charset=GBK">
	<%@include file="/admin/common/skinCss.jsp" %>
	<link rel="stylesheet" type="text/css" href="<%=path%>/css/find.css">
	<link rel="stylesheet" type="text/css" href="<%=path%>/css/core.css">
	<link rel="stylesheet" type="text/css" href="<%=path %>/css/button.css">
	<script type="text/javascript" >
	   var path = "<%=path%>";
	</script>
</head>
<body style="overflow: hidden;">
<div id="main" region="center"  class="easyui-layout" fit="true" style="border:0px">
<div region="center"  style="overflow: hidden;border:0px" id="mainCenter"  fit="true"><!-- title="人员管理" -->
	<%--输入表单--%>
	<form id="form1" name="form1" style="margin-bottom:0px;">
		<table width="100%" class="formbasic">
		   <tr>
			  <td>人员名称&nbsp;</td>
			  <th>
 		  		 <input type="text" id="sName" name="sName" style="width:120px;" value="">
		      </th>
			  <td>人员性别&nbsp;</td>
			  <th>
		      	  <select id="sSex" name="sSex" style="width:120px;">
		      	  	<option value="" >全部</option>
		      	  	<option value="1">男</option>
		      	  	<option value="2">女</option>
		      	  </select>
		      	   
		      	   <input type="hidden" id="iDeptId" name="iDeptId" value="" style="width:120px;" />
		      	   <input type="hidden" id="iDuty" name="iDuty" value="" style="width:120px;" />
		      </th>
			  <td>联系电话&nbsp;</td>
			  <th>
 		  		 <input type="text" id="sTel" name="sTel" maxlength="12" style="width:120px;" value="" >
		      </th>
		      </tr>
			  <tr>
			  <td>人员备注&nbsp;</td>
			  <th>
 		  		 <input type="text" id="remark" name="remark" maxlength="12" style="width:120px;" value="">
		      </th>
			  <td>入职日期&nbsp;</td>
			  <th>
		      	  <input type="text" id="creatTime" name="creatTime" value="" style="width:120px;" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" class="Wdate" readonly="readonly"/>
		      </th>
			  <td>~&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
			  <th>
		      	  <input type="text" id="endTime" name="endTime" value="" style="width:120px;" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" class="Wdate" readonly="readonly"/>
		      </th>
			</tr>
		 </table>
     </form>
	 <div id="toolbar"></div>
     <table id="employeeList" border="1px"></table>
</div>
</div>
</body>

<!-- 此日期控件js请再最后导入，如果放在head里导入，有时会界面打不开 -->
<%@include file="/admin/common/commonJs.jsp" %>
<script type="text/javascript" src="<%=path%>/js/commonEnum.js"></script>
<script type="text/javascript" src="<%=path%>/demo/dbam/jquery/js/index.js"></script>
<script type="text/javascript" src="<%=path%>/calendar/WdatePicker.js"></script>

</html>

