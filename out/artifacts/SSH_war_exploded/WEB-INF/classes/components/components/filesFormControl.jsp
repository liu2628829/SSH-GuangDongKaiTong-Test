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
	<%--�¿ؼ�--%>
	<!--����ʽ-->
	<link rel="stylesheet" type="text/css" href="css/base.css" />
	<link rel="stylesheet" type="text/css" href="css/main.css" />
	<!--�ؼ���ʽ  -->
	<link rel="stylesheet" type="text/css" href="css/compoentsBase.css" />
	<%--�¿ؼ�--%>
	<script type="text/javascript" >
        var path = "<%=path%>";
	</script>
</head>
<body style="overflow: hidden;">
<div id="main" region="center"  class="easyui-layout" fit="true" style="border:0px">
	<div region="center"  style="overflow: hidden;border:0px" id="mainCenter"  fit="true"><!-- title="�ļ�����" -->
		<%--�����--%>
		<form id="form2" name="form2" style="margin-bottom:0px;">
			<table width="100%" class="formbasic">
				<tr>
					<td>�ļ�����&nbsp;</td>
					<th>
						<input type="text" id="sFileName" name="sFileName" style="width:120px;" value="">
					</th>
					<td>�ļ�����&nbsp;</td>
					<th>
						<select id="sFileType" name="sFileType" style="width:120px;">
							<option value="" >ȫ��</option>
							<option value="1">.doc</option>
							<option value="2">.xls</option>
							<option value="3">.ppt</option>
							<option value="4">.docx</option>
							<option value="5">.xlsx</option>
							<option value="6">.pptx</option>
						</select>
						<input type="hidden" id="iDeptId" name="iDeptId" value="" style="width:120px;" />
						<input type="hidden" id="iDuty" name="iDuty" value="" style="width:120px;" />
					</th>
					<td>�ļ�·��&nbsp;</td>
					<th>
						<input type="text" id="sFilePath" name="sFilePath" maxlength="12" style="width:120px;" value="" >
					</th>
					<td>�ļ���С&nbsp;</td>
					<th>
						<input type="text" id="iFileSize" name="iFileSize" maxlength="12" style="width:120px;" value="">
					</th>

				</tr>
				<tr>

					<td>�ļ��ϴ���&nbsp;</td>
					<th>
						<input type="text" id="sUploadUser" name="sUploadUser" maxlength="12" style="width:120px;" value="">
					</th>
					<td>�ļ��ϴ�ʱ��&nbsp;</td>
					<th>
						<input type="text" id="creatTime" name="creatTime" value="" style="width:120px;" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" class="Wdate" readonly="readonly"/>
					</th>
					<td>~&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					<th>
						<input type="text" id="endTime" name="endTime" value="" style="width:120px;" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" class="Wdate" readonly="readonly"/>
					</th>
					<td>�ļ����&nbsp;</td>
					<th>
						<input type="text" id="sRemark" name="sRemark" maxlength="12" style="width:120px;" value="">
					</th>
				</tr>

			</table>
		</form>
		<div id="toolbar"></div>
		<table id="filesList" border="1px"></table>
	</div>
</div>
</body>

<!-- �����ڿؼ�js��������룬�������head�ﵼ�룬��ʱ�����򲻿� -->
<%@include file="/admin/common/commonJs.jsp" %>
<script type="text/javascript" src="<%=path%>/js/commonEnum.js"></script>
<script type="text/javascript" src="<%=path%>/components/components/filesFormControl.js"></script>
<script type="text/javascript" src="<%=path%>/calendar/WdatePicker.js"></script>


<script type="text/javascript" src="jquery-1.7.1.js"></script>
<script type="text/javascript" src="jquery.jqtransform.js"></script>
<script src="datagrid.js"></script>
<%--�¿ؼ�--%>
<script src="datePicker.js"></script>
<script src="fileUpload.js"></script>
<script src="menu.js"></script>
<script src="number.js"></script>
<script src="pagination.js" charset="utf-8"></script>
<script src="popupWindow.js"></script>
<script src="select.js" charset="utf-8"></script>
<script src="radio.js" charset="utf-8"></script>
<script src="Toolbar.js"></script>
<script src="detailgrid.js"></script>
<script src="blackboard.js"></script>
<script src="sidebar.js"></script>

<script src="./layer/layer.js" charset="utf-8"></script>
<script src="./base.js" charset="utf-8"></script>
<script src="./window.js"></script>

<script src="../../calendar/WdatePicker.js"></script>
</html>

