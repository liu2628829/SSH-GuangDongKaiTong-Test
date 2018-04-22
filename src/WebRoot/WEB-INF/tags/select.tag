<%-- 
    Document   : ��ѡö������
    Created on : 2010-12-01
    Author     : �����
--%>

<%@tag description="��ѡ����" pageEncoding="GBK"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- ���� --%>
<%@attribute name="id" description="id(����)" required="true"%>
<%@attribute name="key" description="ö��key(����)" required="true"%>
<%@attribute name="width" description="���" required="false"%>
<%@attribute name="text" description="��ʼ����ʾ" required="false"%>
<%@attribute name="style" description="��ʽ" required="false"%>
<%@attribute name="onChange" description="ֵ�ı��¼�" required="false"%>
<%@attribute name="datatype" description="��֤��Ϣ" required="false" rtexprvalue="true" %>
<%@attribute name="msg" description="��֤ʧ����ʾ��Ϣ" required="false" rtexprvalue="true" %>

<%-- ö��ֵ�ı���ID--%>
<c:set var="selectValueId" value="${id}"/>
<%-- ö�������ı���ID--%>
<c:set var="selectNameId" value="${id}_text"/>  
<%-- TreeList�б�ID--%>
<c:set var="listId" value="${id}List"/>    
<%-- DIV��ID--%>
<c:set var="divId" value="${id}Div"/>        
<%-- ������ID--%>
<c:set var="searchId" value="${id}_search"/>  
<%-- ö�����ݱ��� --%>
<c:set var="listData" value="${id}Data"/>  


<%-- ��ʾ select ���� --%>
   	   
<input  type="text" id="${selectNameId}" name="${selectNameId}" readonly="readonly" class="PickList" 
		style="${style}" onchange="${onChange }" style="width:${width}" value="${text}"
		<c:if test="${not empty datatype}">datatype="${datatype}"</c:if> <c:if test="${not empty msg}">msg="${msg}"</c:if>  
		  onclick="showSelectList('${selectValueId}',true,false,'${divId}','${listId}','text','id','','');"
		  onfocus="showSelectList('${selectValueId}',true,false,'${divId}','${listId}','text','id','','');"
		  onblur="hiddenSelectList();"
		  >
<input type="hidden" id="${selectValueId}" name="${selectValueId}" />

<!-- ��-->

<div id="${divId}" style="display:none;">
	<div></div><!-- ��div������ -->	
	
	 <table width="100%" cellpadding="0" cellspacing="0" class="formbasic">
			  <td>����&nbsp;</td>
			  <th>
		      	  <input type="text" id="${searchId}" name="${searchId}" value="" style="width:60px" />
		      </th>
			  <th>
                  <a href="javascript:void(0)" title="��ѯ" class="search_btn" onclick="listSearch('${searchId}',${listData},'${listId}')"></a>
			  <th>
	 </table>	
	<div>
	<CATTSOFT:treelist id="${listId}" class="treelist" sameReload="true" width="100%" height="100" border="1" 
	                   pageSize="10" pagiServer="false" pagiFunction="qryPagination" pagiPositon="buttom,right" 
					   menuServer="false" showHead="false" onItemClick="" onItemDblClick="" showImage="true" 
					   showCheck="true">
		<CATTSOFT:columns>
			<CATTSOFT:column width="100%" display="true" propertyName="text" displayTip="true" />
			<CATTSOFT:column  display="false" displayText="ID" propertyName="id" />
		</CATTSOFT:columns>
	</CATTSOFT:treelist>
	</div>
	
</div>



<script type="text/javascript">
var ${listData}="";
//�����б�
var ${listId}= document.getElementById('${listId}');   //��ȡTreeList����
EnumRequest.getEnum('${key}',function(backdata){
	 ${listData}=backdata;
     ${listId}.loadByData(backdata);
});


</script>