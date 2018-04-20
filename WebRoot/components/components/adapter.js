/**************************************************定义commonUtil对象、属性**************************************************/
$.commonUtil = {};

$(window).bind("load",function(){
	var lis=$(".fileUpload").each(function(){
		if ((this.className.indexOf("fileUpload") >= 0 && this.tagName != 'TABLE')) {
			var uploadEvt = $(this).attr("afterUpload");//上传回调
			var cleanEvt = $(this).attr("afterClean");//清空回调
			var afterUpload;
			if (!IsEmpty(uploadEvt)) {
				try{
					eval("afterUpload = "+uploadEvt);
				}catch(ee){
					alert(ee);
				}
			}
			var afterClean;
			if (!IsEmpty(cleanEvt)) {
				try{
					eval("afterClean = "+cleanEvt);
				}catch(ee){}
			}
			var iTableId = IsEmpty($(this).attr("iTableId"))?"":$(this).attr("iTableId");
			var iTableId1=iTableId1;
			if(!IsEmpty(iTableId)){
				iTableId = decode(iTableId);
			}
			var sTableName = IsEmpty($(this).attr("sTableName"))?"":$(this).attr("sTableName");
			var isToLocal = IsEmpty($(this).attr("isToLocal"))?"":$(this).attr("isToLocal");
			var isSaveToDB = IsEmpty($(this).attr("isSaveToDB"))?"1":$(this).attr("isSaveToDB");
			var directory = IsEmpty($(this).attr("directory"))?"":$(this).attr("directory");
			var limitReg = IsEmpty($(this).attr("limitReg"))?"":$(this).attr("limitReg");
			var displayName = IsEmpty($(this).attr("displayName"))?"serverFileDir":$(this).attr("displayName");
			var iDeptId = IsEmpty($(this).attr("iDeptId"))?"":$(this).attr("iDeptId");
			var maxUpSize = IsEmpty($(this).attr("maxUpSize"))?"":$(this).attr("maxUpSize");
			var option = {eId : $(this).attr("id") , iTableId : iTableId, iTableId1 : iTableId1, sTableName : sTableName,isToLocal : isToLocal,isSaveToDB : isSaveToDB,
									directory : directory,limitReg : limitReg, maxUpSize : maxUpSize, displayName : displayName,iDeptId : iDeptId,afterUpload : afterUpload,afterClean : afterClean};
				
			$.commonUtil.initFileUpload(option);
		}
	});
	
});


/***************************fileUpload*****************************/
/*
update by gaotao 2013-12-08

eId:指定要生成单文件上传的input标签id
iTableId:模块中对应的主键。参数类型可以是string,object,function
		 string类型，直接传入主键id
		 object类型，tId属性为表单元素的id,如{tId:'eleId'},可以通过某个表单元素为主键赋值
		 function类型，自定义函数，返回主键id
sTableName:模块名称（对应的表名），服务端根据这个存放到不同目录和入库
isToLocal:0-文件上传至fastDFS文件系统服务器；1-文件上传至本地应用服务器。不设置将以config.properties中的为准
isSaveToDB:是否存储到数据库。0-否，1-是，默认为是
directory:临时目录名称。在系统指定的目录中新建一个子目录
limitReg:文件后缀限制。格式为*.xxx,多个限制以","号分隔
displayName:文件上传成功后在文本框中显示的信息。枚举值为fileId,localFileName,serverFileName,serverFileDir,默认为文件路径
afterUpload:上传按钮回调函数
afterClean:清空按钮回调函数
*/         
$.commonUtil.initFileUpload = function(options) 
{
	//把json属性值，赋给input对象属性
	function initAttr(obj,options){
		if(obj){
	       $(obj).attr({
	       	    iTableId :IsEmpty(options.iTableId1)?"":options.iTableId1,
				sTableName:IsEmpty(options.sTableName)?"":options.sTableName,
				isToLocal: IsEmpty(options.isToLocal)?"":options.isToLocal,
				isSaveToDB: IsEmpty(options.isSaveToDB)?"1":options.isSaveToDB,
				directory :encodeURIComponent(IsEmpty(options.directory)?"":options.directory),  //编码，\为转义符
				limitReg : IsEmpty(options.limitReg)?"":options.limitReg,
				displayName: IsEmpty(options.displayName)?"1":options.displayName,
				maxUpSize:IsEmpty(options.maxUpSize)?"":options.maxUpSize
	       });
		}
	}
	
	var fastDFS_path = Base.getFullPath();
	var iTableId = IsEmpty(options.iTableId)?"":options.iTableId;
	var sTableName = IsEmpty(options.sTableName)?"":options.sTableName;
	var isToLocal = IsEmpty(options.isToLocal)?"":options.isToLocal;
	var isSaveToDB = IsEmpty(options.isSaveToDB)?"1":options.isSaveToDB;
	var directory = IsEmpty(options.directory)?"":options.directory;
	directory = encodeURIComponent(directory); //编码，\为转义符
	var limitReg = IsEmpty(options.limitReg)?"":options.limitReg;
	var maxUpSize = IsEmpty(options.maxUpSize)?"":options.maxUpSize;
	
	var displayName = IsEmpty(options.displayName)?"serverFileDir":options.displayName;
	var iDeptId = IsEmpty(options.iDeptId)?"":options.iDeptId;
	if(!IsEmpty(options.iTableId)){
		$(window).data("fileUploadTableId_"+options.eId, options.iTableId);//将模块主键缓存起来
	}
	
	var text = document.getElementById(options.eId);
	if (IsEmpty(text)) return;
	
	var obj = text.cloneNode(true);//重新生成一个文本框
	//tangyj 2013-04-27
	//将原来input框的width值设置到oldWidth属性上，用于在窗口改变时实时计算宽度
	$(obj).attr("oldWidth",$(text).css("width"));
	initAttr(obj,options);
	
	if ($(text).attr("class") == "txt") {
		$(text).removeClass("txt").css("width", Base.getElementWidth(text) + 17);
		$("#"+options.eId+"_span")[0].parentNode.replaceChild(text,$("#"+options.eId+"_span")[0]);
	}
	var div = document.createElement("span");
	div.id = options.eId+"_span";
	
	var width = Base.getElementWidth(text);
	var table = document.createElement("table");
	$(table).addClass("fileUpload").attr("cellpadding", "0").attr("cellspacing", "0");
	$(table).css("width",width);
	$(div).css({"display":$(obj).css("display")});
	var tbody = document.createElement("tbody");
	var tr_1 = document.createElement("tr");
	var td_11 = document.createElement("td");
	$(td_11).attr("rowspan", "7");
	$(obj).addClass("txt").css("width", width - 17).attr("readonly", "readonly");
	if(this.className == 'fileUpload') {
		$(obj).attr("onclick", "");//如果是弹窗文本框,去掉原始事件
	}
	td_11.appendChild(obj);
	
	//上传按钮
//	var td_12 = document.createElement("td");
//	var ifm = document.createElement("iframe");
//	$(ifm).attr({
//	   width:16,height:11,frameborder:0,title:'上传文件',scrolling:"no",
//	   src:fastDFS_path+'/admin/commonModule/fastdfs/singleUpload.jsp?eId='+options.eId+"&sTableName="+sTableName+"&fileSize="+maxUpSize+"&extension="+limitReg+"&isToLocal="+isToLocal+"&directory="+directory
//	 });
//	$(ifm).css({height:11});
//	td_12.appendChild(ifm);
//	$(tr_1).append(td_11).append(td_12);
	$(tr_1).append(td_11);
	
//	//清空文件按钮
//	var tr_2 = document.createElement("tr");
//	var td_22 = document.createElement("td");
//	with(td_22.style){textAlign="left";backgroundColor="transparent";}
//	var cleanSpan=document.createElement("span");
//	$(cleanSpan).html("&nbsp;&nbsp;&nbsp;&nbsp;").addClass("cleanBtn").attr("title","清空");//对span绑定事件原input框的值
	
//	//对span绑定事件原input框的值
//	$(cleanSpan).bind("click", function(){
//	    $.commonUtil.jqFileUpload_cleanFile(options.eId);
//	});
	
//	$(td_22).append(cleanSpan);
//	$(tr_2).append(td_22);
	//$(tbody).append(tr_1).append(tr_2);
	$(tbody).append(tr_1);
	$(table).append(tbody);
	$(div).append(table);
	text.parentNode.replaceChild(div,text);//将原文框替成当前的span
	if (!IsEmpty(options.afterUpload)) {
		$(window).data("afterUpload_"+options.eId,options.afterUpload);//将上传回调函数缓存起来
	}
	if (!IsEmpty(options.afterClean)) {
		$(window).data("afterClean_"+options.eId,options.afterClean);//将清空回调函数缓存起来
	}
	
	try{//此部分仅适用于通用导入
	 	var ul = $(".btn_hover");
	 	ul.addClass("business");
	 	ul.css({width:"220px","margin": "0 auto", "float": "none"});
	 	ul.find("li").last().css({borderRight:'0px'});
	 	
	 	var tb = $('.formbasic');
	 	tb.attr("height",30);
	 	tb.css("height",30);
	 	tb.addClass("orderTable");
	 	tb.find("td").last().css({padding:0,paddingRight:20});
	 //	$("#Catt_importFile_div .specie").width(200);
 	}catch(e){}
 	
 	$("#"+options.eId).fileUpload(options); //初始化文件上传框
};

$.commonUtil.jqFileCallBack = function(eId, jsonData){
    var obj = document.getElementById(eId);
	if(obj){
			var display = jsonData.oldFileName; 
			var displayName = $(obj).attr("displayName");
	       	if(displayName == "serverFileName"){//服务端新文件名称
	       		display = jsonData.newFileName;
	       	}else if(displayName == "serverFileDir"){//服务端文件全路径
	       		display = jsonData.fileId;
	       	}
	       	$(obj).val(display);
	       	$(obj).attr(jsonData);
	       	
	       	var tableIdObj = $(window).data("fileUploadTableId_"+eId);//模块中对应的主键参数
			var type = typeof(tableIdObj);//string,object,function
			var iTableId = "";
			if(type == "string"){
				iTableId = tableIdObj;
			}else if(type == "object"){
				iTableId = document.getElementById(tableIdObj.tId).value;
			}else if(type == "function"){
				iTableId = tableIdObj.call(this);
			}
		    jsonData.tableId = iTableId; //业务数据ID
		    jsonData.tableName = $(obj).attr("sTableName");//业务表名
		    jsonData.sTableName = $(obj).attr("sTableName");//业务表名
	        jsonData.serverPath = jsonData.fileId;
	        
	       	var isSaveToDB = $(obj).attr("isSaveToDB");
	       	if(isSaveToDB == 1 || isSaveToDB == '1'){ //是否入库
				var path = Base.getFullPath();
				//保存上传文件信息到数据库
				AjaxRequest.doRequest(null, path + "/fastDfs/fastDfs!addUpload.action?nd="+new Date(), jsonData, function (backData) {
					if (backData != 0) {//成功
						jsonData.iFileId = backData; 
						jsonData.fId = backData; //附件数据id
					}
				},false);
			}
	       
	       	//回调函数
	       	var afterUpload = $(window).data("afterUpload_"+eId);
			if (!IsEmpty(afterUpload)) {
				//回调
				afterUpload.call(this, jsonData);
			}
	}
};

$.commonUtil.jqFileUpload_getSingleFileObj = function(iTablePKId, sTableName, cSvrFilePath)
{//获取附件对象
	var jsonData = null;
	AjaxRequest.doRequest(null, path+'/fastDfs/fastDfs!getUploadList.action',
			{iTablePKId:iTablePKId, sTableName:sTableName, cSvrFilePath:cSvrFilePath, pageNo:1, limit:3000},function(backData){
		jsonData = decode(backData);
		if(jsonData[0]){
			jsonData = jsonData[0]; 
		}
	}, false);
	return jsonData;
};
/***************************fileUpload结束**************************/


/**************************************************定义commonUtil的公共方法**************************************************/
//在些定义的方法相当于java对象的私有方法，但是js中无法定义对象的私有方法，所以就提升到对象的公有方法。
//该方法会被commonUtil的jqFileUpload_submit、initFileUpload方法访问，
//清空附件
//isToLocal,isSaveToDB有赋默认值。如果innerCall为false，则为点击清空按钮触发，需要调用清空后的事件
$.commonUtil.jqFileUpload_cleanFile = function(eId)
{
 var obj = $("#"+eId);
 if (obj.attr("disabled") == true) return;
 if(IsEmpty(obj.val())) return;//文本框没有文件，不触发清空事件

	var pathname = Base.getPathName();
	var fastDFS_path = Base.getFullPath();
	var tableName = IsEmpty(obj.attr("sTableName"))?"default":obj.attr("sTableName");
	var isToLocal = obj.attr("isToLocal");
	var isSaveToDB = obj.attr("isSaveToDB");
	var fileId = $("#"+eId).attr("fileId");
	var newFileName = obj.attr("newFileName");
	
	//删除服务端文件
	AjaxRequest.doRequest('',
			fastDFS_path+'/fastDfs/singleFileUpload!cleanFile.action',//上传调用的Action,即是上传提交的页面url
	    {fileid:fileId, isToLocal:isToLocal},
	    function(backData) {
	        //删除服务端数据
	    	if ((backData == 1 || backData == "1") && (isSaveToDB == 1 || isSaveToDB == "1")) {
	    		AjaxRequest.doRequest('', fastDFS_path + '/fastDfs/fastDfs!deleteUploadByFileId.action',{fileid :newFileName}, function(backData) {
					if (backData == '1' || backData == 1) {
						var afterClean = $(window).data("afterClean_"+eId);
						//if(!innerCall){
							if(!IsEmpty(afterClean)){
								afterClean.call(this);
							}
						//}
					}
				});
	    	}else if(backData == 1 || backData == "1"){
	    		var afterClean = $(window).data("afterClean_"+eId);
				//if(!innerCall){
					if(!IsEmpty(afterClean)){
						afterClean.call(this);
					}
				//}
	    	}
	    	
	    	//清空界面上的值
	    	obj.val('');
	    	obj.attr({
		    	fileSize:'',
		    	fileId:'',
		    	iFileId:'',
		    	oldFileName:'',
		    	newFileName:''
		    });
	    }
  ,false);
};




function getTreegridItems(tree){
	var items = new Array();
	for ( var i = 0; i < tree.length; i++) {
		pushTreegridItems(items,tree[i]);
	}
	return items;
}

function pushTreegridItems(items,tree){
	items.push(tree);
	var childs = tree.children;
	if(childs!=null){
		for ( var i = 0; i < childs.length; i++) {
			pushTreegridItems(items,childs[i]);
		}
	}
}
