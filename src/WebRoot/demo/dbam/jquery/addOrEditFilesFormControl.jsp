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
		<%if("0".equals(flag)){ %>�����ļ�
		<%}else{ %>�޸��ļ�<%} %>
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
		var deptData = new Array();//���в��ż���
		var dutyData = new Array();//����ְ�񼯺�
		var jsonDuty = new Array();//�뵱ǰ���Ŷ�Ӧ��ְ��
	    function doSubmit( mode) {
	    	Validator.validate(logForm, 2, function(data){
				 if(data == '1' || data == 1) {
					alert("�������!");
					closeWin();
                   	window.parent.initLoadData(true,'<%=iId %>');
				 }else {
					 alert("����ʧ��!");
				 }
			});
			return false; 
		}
	    function closeWin() {
	    	SysCloseJqueryWin();
//            window.close()
	    }
//		function getEnumDept(){//��ȡ�������ݣ���ʼ������������
//			EnumRequest.getEnum("dept", function(backdata){deptData = backdata});
//			EnumRequest.initSelect("dept", "s_Dept", null, true, "��ѡ��");
//		}
//		function getEnumDuty(){//��ȡְ������
//			EnumRequest.getEnum("duty",function(backdata){
//				dutyData = backdata;
//			});
//		}
//		function initDuty(){//ʵ������������Ӧ��ְ����Ϣ
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
//			var options = "<option value=''>��ѡ��</option>";
//			$(jsonDuty).each(function(i){
//				options += "<option value='" + jsonDuty[i].id + "'>" + jsonDuty[i].text + "</option>";
//			});
//			$("#s_Duty").html(options);
//		}
		
		function init() {
			//�����ģʽ���� ��������window.dialogArguments���ø����ڵ����з����ͱ��� 
			//ǰ���Ǵ�ģʽ����ʱҪ��window ������Ϊ�������ݹ���
//			getEnumDept();
//			getEnumDuty();
			//��ֵ��ؼ�
			commonUtil.initDigit({eId:'iFileSize', limit:1, max:100, min:0});
			
//			//�������onchange�¼�
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
					initSel();//��ʼ������ְ���б�
				});
			}
		}
		
//		function initSel(){
//			if(document.getElementById("iDeptId").value != '' && document.getElementById("iDeptId").value != null){
//				document.getElementById("deptid").value = document.getElementById("iDeptId").value;
//				//�������ϣ�ƥ�䲿����Ϣ
//				for(var i=0; i<deptData.length; i++){
//					if(deptData[i].id == document.getElementById("iDeptId").value){
//						var idutyId = document.getElementById("iDutyId").value;
//						$("#s_Dept option[value="+deptData[i].id+"]").attr("selected", "selected");
//						initDuty();
//
//						//�������ϣ�ƥ�䲿����Ϣ
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
		  <td width="15%">�ļ�����&nbsp;</td>
		  <th width="35%">
		  	  <input type="hidden" value="<%=iId %>" id="iId" name="iId">
	      	  <input type="text" id="sFileName" name="sFileName" value="" style="width: 120px;" maxlength="30" datatype="require" msg="�ļ����Ʋ�����Ϊ��,30���ַ�����"/>
	      	  <font style="padding-left:5px;color:#f00;">*</font>
	      </th>
		  <td width="15%">�ļ�����&nbsp;</td>
		  <th width="35%">
	      	  <select id="sFileType" style="width: 120px;" name="sFileType">
				  <option value="" >ȫ��</option>
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
		   <td width="15%">�ļ�·��&nbsp;</td>
		   <th width="35%">
			   <input type="text" id="sFilePath" name="sFilePath" value="" style="width: 120px;" maxlength="30" datatype="require" msg="�ļ�·��������Ϊ��"/>
			   <font style="padding-left:5px;color:#f00;">*</font>
		   </th>
		   <%--<td width="15%">�ļ���С&nbsp;</td>--%>
		   <%--<th width="35%">--%>
			   <%--<input type="text" id="iFileSize" name="iFileSize" value="" style="width: 120px;" maxlength="30" datatype="require" msg="�ļ���С������Ϊ��"/>--%>
			   <%--<font style="padding-left:5px;color:#f00;">*</font>--%>
		   <%--</th>--%>
		   <td>�ļ���С&nbsp;</td>
		   <th>
			   <input type="text" id="iFileSize" style="width: 120px;" name="iFileSize" datatype="digit" require="true" msg="�ļ���С������Ϊ�ղ���Ϊ����"/>
			   <font style="padding-left:5px;color:#f00;">*</font>
		   </th>
		   </th>

	   </tr>
	   <tr>
		  <td>�ļ��ϴ�ʱ��&nbsp;</td>
		  <th>
	      	  <input type="text" id="dUploadTime" name="dUploadTime" value="" style="width: 120px;" onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})" class="Wdate" readonly="readonly"/>
	      </th>
		   <td width="15%">�ļ��ϴ���&nbsp;</td>
		   <th width="35%">
			   <input type="text" id="sUploadUser" name="sUploadUser" value="" style="width: 120px;" maxlength="30" datatype="require" msg="�ļ��ϴ��˲�����Ϊ��"/>
			   <font style="padding-left:5px;color:#f00;">*</font>
		   </th>
	   </tr>
	   <tr>
		  <td>�ļ����&nbsp;</td>
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
	            <div class="ok">����</div>
	            </span></a></li>
	          <%--<li onClick="closeWin()"><a href="#"><span>--%>
			  <li onClick="closeWin()"><a href="#"><span>
	            <div class="no">ȡ��</div>
	            </span></a></li>
	        </ul></td>
	    </tr>
	 </table>
  </div>
<input type="hidden" value="" id="dutyid" name="dutyid">
<input type="hidden" value="" id="deptid" name="deptid">
</body>
</html>