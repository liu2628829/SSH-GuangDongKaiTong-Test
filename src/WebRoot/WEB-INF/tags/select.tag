<%-- 
    Document   : 多选枚举下拉
    Created on : 2010-12-01
    Author     : 林秀刚
--%>

<%@tag description="多选下拉" pageEncoding="GBK"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- 参数 --%>
<%@attribute name="id" description="id(必填)" required="true"%>
<%@attribute name="key" description="枚举key(必填)" required="true"%>
<%@attribute name="width" description="宽度" required="false"%>
<%@attribute name="text" description="初始化显示" required="false"%>
<%@attribute name="style" description="样式" required="false"%>
<%@attribute name="onChange" description="值改变事件" required="false"%>
<%@attribute name="datatype" description="验证信息" required="false" rtexprvalue="true" %>
<%@attribute name="msg" description="验证失败提示信息" required="false" rtexprvalue="true" %>

<%-- 枚举值文本框ID--%>
<c:set var="selectValueId" value="${id}"/>
<%-- 枚举描述文本框ID--%>
<c:set var="selectNameId" value="${id}_text"/>  
<%-- TreeList列表ID--%>
<c:set var="listId" value="${id}List"/>    
<%-- DIV层ID--%>
<c:set var="divId" value="${id}Div"/>        
<%-- 搜索框ID--%>
<c:set var="searchId" value="${id}_search"/>  
<%-- 枚举数据变量 --%>
<c:set var="listData" value="${id}Data"/>  


<%-- 显示 select 内容 --%>
   	   
<input  type="text" id="${selectNameId}" name="${selectNameId}" readonly="readonly" class="PickList" 
		style="${style}" onchange="${onChange }" style="width:${width}" value="${text}"
		<c:if test="${not empty datatype}">datatype="${datatype}"</c:if> <c:if test="${not empty msg}">msg="${msg}"</c:if>  
		  onclick="showSelectList('${selectValueId}',true,false,'${divId}','${listId}','text','id','','');"
		  onfocus="showSelectList('${selectValueId}',true,false,'${divId}','${listId}','text','id','','');"
		  onblur="hiddenSelectList();"
		  >
<input type="hidden" id="${selectValueId}" name="${selectValueId}" />

<!-- 层-->

<div id="${divId}" style="display:none;">
	<div></div><!-- 此div必须有 -->	
	
	 <table width="100%" cellpadding="0" cellspacing="0" class="formbasic">
			  <td>名称&nbsp;</td>
			  <th>
		      	  <input type="text" id="${searchId}" name="${searchId}" value="" style="width:60px" />
		      </th>
			  <th>
                  <a href="javascript:void(0)" title="查询" class="search_btn" onclick="listSearch('${searchId}',${listData},'${listId}')"></a>
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
//加载列表
var ${listId}= document.getElementById('${listId}');   //获取TreeList对象
EnumRequest.getEnum('${key}',function(backdata){
	 ${listData}=backdata;
     ${listId}.loadByData(backdata);
});


</script>