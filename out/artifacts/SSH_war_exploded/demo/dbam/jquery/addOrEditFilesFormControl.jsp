<%@page contentType="text/html; charset=GBK"%>
<%@page import="util.StringUtil"%>
<%
	String path = request.getContextPath();
	String iId = "";
	if(StringUtil.checkStr(request.getParameter("iId"))){
		iId = request.getParameter("iId");
	}
	String flag = request.getParameter("flag");
%>
<html>
<head>
<title>
		<%if("0".equals(flag)){ %>新增文件
		<%}else{ %>修改文件<%} %>
</title>
	<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
	<%@include file="/admin/common/skinCss.jsp" %>
	<link rel="stylesheet" type="text/css" href="<%=path %>/css/find.css">	
	<%@include file="/admin/common/commonJs.jsp" %>
	<script type="text/javascript" src="<%=path %>/js/commonEnum.js"></script>
	<script type="text/javascript" src="<%=path %>/js/select.js"></script>
	<%--<script type="text/javascript" src="<%=path %>/js/showDetail.js"></script>--%>
	<script type="text/javascript" src="<%=path%>/calendar/WdatePicker.js"></script>
	<script type="text/javascript">
		var deptData = new Array();//所有部门集合
		var dutyData = new Array();//所有职务集合
		var jsonDuty = new Array();//与当前部门对应的职务
	    function doSubmit( mode) {
	    	Validator.validate(logForm, 2, function(data){
				 if(data == '1' || data == 1) {
					alert("操作完成!");
					closeWin();
                   	window.parent.initLoadData(true,'<%=iId %>');
				 }else {
					 alert("操作失败!");
				 }
			});
			return false; 
		}
	    function closeWin() {
	    	SysCloseJqueryWin();
//            window.close()
	    }
//		function getEnumDept(){//获取部门数据，初始化部门下拉框
//			EnumRequest.getEnum("dept", function(backdata){deptData = backdata});
//			EnumRequest.initSelect("dept", "s_Dept", null, true, "请选择");
//		}
//		function getEnumDuty(){//获取职务数据
//			EnumRequest.getEnum("duty",function(backdata){
//				dutyData = backdata;
//			});
//		}
//		function initDuty(){//实例化部门所对应的职务信息
//			jsonDuty = new Array();
//			document.getElementById("iDutyId").value = "";
//
//			var iDeptId = document.getElementById("iDeptId").value;
//			if(iDeptId.length==0)return;
//			for(var i=0;i<dutyData.length;i++){
//				var duty = dutyData[i];
//				if(duty.iDeptId == iDeptId){
//					var json = {id:duty.id,text:duty.text};
//					jsonDuty.push(json);
//				}
//			}
//			var options = "<option value=''>请选择</option>";
//			$(jsonDuty).each(function(i){
//				options += "<option value='" + jsonDuty[i].id + "'>" + jsonDuty[i].text + "</option>";
//			});
//			$("#s_Duty").html(options);
//		}
		
		function init() {
			//如果是模式窗口 可以利用window.dialogArguments调用父窗口的所有方法和变量 
			//前提是打开模式窗口时要将window 对象做为参数传递过来
//			getEnumDept();
//			getEnumDuty();
			//数值框控件
			commonUtil.initDigit({eId:'iFileSize', limit:1, max:100, min:0});
			
//			//下拉框的onchange事件
//			$("#s_Dept").bind("change", function(){
//				var deptId = $(this).val();
//				$("#iDeptId").val(deptId);
//				initDuty();
//			});
//			$("#s_Duty").bind("change", function(){
//				var dutyId = $(this).val();
//				$("#iDutyId").val(dutyId);
//			});
				
			if("1" == <%=flag %>){
				AjaxRequest.initForm('logForm', '<%=path %>/demo/files!getFilesList.action', {iId: '<%=iId %>'}, true,function(backdata){
					initSel();//初始化所属职务列表
				});
			}
		}
		
//		function initSel(){
//			if(document.getElementById("iDeptId").value != '' && document.getElementById("iDeptId").value != null){
//				document.getElementById("deptid").value = document.getElementById("iDeptId").value;
//				//遍历集合，匹配部门信息
//				for(var i=0; i<deptData.length; i++){
//					if(deptData[i].id == document.getElementById("iDeptId").value){
//						var idutyId = document.getElementById("iDutyId").value;
//						$("#s_Dept option[value="+deptData[i].id+"]").attr("selected", "selected");
//						initDuty();
//
//						//遍历集合，匹配部门信息
//						for(var j=0; j<jsonDuty.length; j++){
//							if(jsonDuty[j].id == idutyId){
//								document.getElementById("iDutyId").value = jsonDuty[j].id;
//								$("#s_Duty option[value="+jsonDuty[j].id+"]").attr("selected", "selected");
//							}
//						}
//					}
//				}
//			}else{
//				window.setTimeout('initSel()',1000);
//			}
//		}
		
		window.onload = init;
	</script>
</head>
<body class="easyui-layout" fit="true" style="border: 0px;overflow:hidden;">
	<div region="center" class="defaultColor" style="border:0" >
	<form  action="<%=path %>/demo/files!addEditFiles.action" name="logForm" id="logForm" method="POST" style="margin:0;padding:0;border:0;">
	<table width="100%" class="formbasic">
      <tr>
		  <td width="15%">文件名称&nbsp;</td>
		  <th width="35%">
		  	  <input type="hidden" value="<%=iId %>" id="iId" name="iId">
	      	  <input type="text" id="sFileName" name="sFileName" value="" style="width: 120px;" maxlength="30" datatype="require" msg="文件名称不允许为空,30个字符以内"/>
	      	  <font style="padding-left:5px;color:#f00;">*</font>
	      </th>
		  <td width="15%">文件类型&nbsp;</td>
		  <th width="35%">
	      	  <select id="sFileType" style="width: 120px;" name="sFileType">
				  <option value="" >全部</option>
				  <option value="1">.doc</option>
				  <option value="2">.xls</option>
				  <option value="3">.ppt</option>
				  <option value="4">.docx</option>
				  <option value="5">.xlsx</option>
				  <option value="6">.pptx</option>
		      </select>
		      <font style="padding-left:5px;color:#f00;">*</font>
	      </th>
	   </tr>
	   
	   <tr>
		   <td width="15%">文件路径&nbsp;</td>
		   <th width="35%">
			   <input type="text" id="sFilePath" name="sFilePath" value="" style="width: 120px;" maxlength="30" datatype="require" msg="文件路径不允许为空"/>
			   <font style="padding-left:5px;color:#f00;">*</font>
		   </th>
		   <%--<td width="15%">文件大小&nbsp;</td>--%>
		   <%--<th width="35%">--%>
			   <%--<input type="text" id="iFileSize" name="iFileSize" value="" style="width: 120px;" maxlength="30" datatype="require" msg="文件大小不允许为空"/>--%>
			   <%--<font style="padding-left:5px;color:#f00;">*</font>--%>
		   <%--</th>--%>
		   <td>文件大小&nbsp;</td>
		   <th>
			   <input type="text" id="iFileSize" style="width: 120px;" name="iFileSize" datatype="digit" require="true" msg="文件大小不允许为空并且为整数"/>
			   <font style="padding-left:5px;color:#f00;">*</font>
		   </th>
		   </th>

	   </tr>
	   <tr>
		  <td>文件上传时间&nbsp;</td>
		  <th>
	      	  <input type="text" id="dUploadTime" name="dUploadTime" value="" style="width: 120px;" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" class="Wdate" readonly="readonly"/>
	      </th>
		   <td width="15%">文件上传人&nbsp;</td>
		   <th width="35%">
			   <input type="text" id="sUploadUser" name="sUploadUser" value="" style="width: 120px;" maxlength="30" datatype="require" msg="文件上传人不允许为空"/>
			   <font style="padding-left:5px;color:#f00;">*</font>
		   </th>
	   </tr>
	   <tr>
		  <td>文件简介&nbsp;</td>
		  <th colspan="3">
	      	  <textarea rows="5" id="sRemark" name="sRemark" value="aaaa" style="overflow: auto;width: 362px;" ></textarea>
	      </th>
	   </tr>
	</table>
	</form>
	</div>
	<div region="south" style="height:40px;border:0px;" class="defaultColor">
	<table id="btngroup" width="100%" height="100%" class="formbasic">
	    <tr>
	      <td  style="text-align:center;border:0px;"><ul class="btn_hover" style="margin-right:auto;margin-left:auto;">
	          <li onClick="return doSubmit(2)"><a href="#"><span>
	            <div class="ok">保存</div>
	            </span></a></li>
	          <%--<li onClick="closeWin()"><a href="#"><span>--%>
			  <li onClick="closeWin()"><a href="#"><span>
	            <div class="no">取消</div>
	            </span></a></li>
	        </ul></td>
	    </tr>
	 </table>
  </div>
<input type="hidden" value="" id="dutyid" name="dutyid">
<input type="hidden" value="" id="deptid" name="deptid">
</body>
</html>