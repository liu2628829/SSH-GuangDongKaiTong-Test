<%@ page contentType="text/html; charset=GBK"%>
<%@ page isELIgnored="false" %>
<%@ page import="util.StringUtil" %>
<%
	String path = request.getContextPath();
%>
<html>
<head>
	<title>�ļ�����</title>
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
<div region="center"  style="overflow: hidden;border:0px" id="mainCenter"  fit="true"><!-- title="��Ա����" -->
	<%--�����--%>
	<form id="form1" name="form1" style="margin-bottom:0px;">
		<table width="100%" class="formbasic">
		   <tr>
			  <td>��Ա����&nbsp;</td>
			  <th>
 		  		 <input type="text" id="sName" name="sName" style="width:120px;" value="">
		      </th>
			  <td>��Ա�Ա�&nbsp;</td>
			  <th>
		      	  <select id="sSex" name="sSex" style="width:120px;">
		      	  	<option value="" >ȫ��</option>
		      	  	<option value="1">��</option>
		      	  	<option value="2">Ů</option>
		      	  </select>
		      	   
		      	   <input type="hidden" id="iDeptId" name="iDeptId" value="" style="width:120px;" />
		      	   <input type="hidden" id="iDuty" name="iDuty" value="" style="width:120px;" />
		      </th>
			  <td>��ϵ�绰&nbsp;</td>
			  <th>
 		  		 <input type="text" id="sTel" name="sTel" maxlength="12" style="width:120px;" value="" >
		      </th>
		      </tr>
			  <tr>
			  <td>��Ա��ע&nbsp;</td>
			  <th>
 		  		 <input type="text" id="remark" name="remark" maxlength="12" style="width:120px;" value="">
		      </th>
			  <td>��ְ����&nbsp;</td>
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

<!-- �����ڿؼ�js��������룬�������head�ﵼ�룬��ʱ�����򲻿� -->
<%@include file="/admin/common/commonJs.jsp" %>
<script type="text/javascript" src="<%=path%>/js/commonEnum.js"></script>
<script type="text/javascript" src="<%=path%>/demo/dbam/jquery/js/index.js"></script>
<script type="text/javascript" src="<%=path%>/calendar/WdatePicker.js"></script>

</html>

