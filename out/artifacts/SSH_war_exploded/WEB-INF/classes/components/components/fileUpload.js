 /**弹窗选控件*/
(function($) { 

	$.fn.fileUpload= function(method, options) {
		if(typeof(method)=='object'){
			create(method, this); //{onClick:function, onClean:function}
		}else if(method=='options'){
			return $(this).data();
		}else if(method=='getValue'){
			return getValue(this); //{id:"",text:""} 
		}else if(method=='setValue'){
			setValue(this, options);//{id:"",text:""}
		}
	};	
	
	/**构建*/
	function create(opts, obj){
		var inp = $(obj);
		if(inp.parent().hasClass("specie")){return;}
		
		initAttr(inp, opts);
		var inpHtml = inp.prop("outerHTML").replace(/<input/i, '<input class="deInput" readonly style="width:100%;" ');
		var id=inp.attr('id');
	    var divId = 'fileUpload_'+id;
        
	    var html = [];
	    html.push('<div class="specie" id="'+divId+'">');
	    html.push(inpHtml);
		html.push('<div class="specieFileUpload"></div><div class="inputClear" style="right:24px;display:none;"></div></div>');
		
		inp.after(html.join(''));
		inp.remove();  
		
		$("#"+id).hover(
				function(){ if( $("#"+id).val() ){$(".specieFileUpload").next().show(1);}}, 
				function(){ if( !$("#"+id).val() ){$(".specieFileUpload").next().hide();}}
			);
		
		var fastDFS_path = Base.getFullPath();
		var iTableId = IsEmpty(opts.iTableId)?"":opts.iTableId;
		var sTableName = IsEmpty(opts.sTableName)?"":opts.sTableName;
		var isToLocal = IsEmpty(opts.isToLocal)?"":opts.isToLocal;
		var isSaveToDB = IsEmpty(opts.isSaveToDB)?"1":opts.isSaveToDB;
		var directory = IsEmpty(opts.directory)?"":opts.directory;
		directory = encodeURIComponent(directory); //编码，\为转义符
		var limitReg = IsEmpty(opts.limitReg)?"":opts.limitReg;
		var maxUpSize = IsEmpty(opts.maxUpSize)?"":opts.maxUpSize;
		var ifm = document.createElement("iframe");
		$(ifm).attr({
		   width:26,height:26,frameborder:0,title:'上传文件',scrolling:"no",
		   src:fastDFS_path+'/admin/commonModule/fastdfs/singleUpload.jsp?cssFlag=1&eId='
		   	+opts.eId+"&sTableName="+sTableName+"&fileSize="+maxUpSize+"&extension="
		   	+limitReg+"&isToLocal="+isToLocal+"&directory="+directory
		 });
		$(".specieFileUpload").append(ifm);
		
		inp = $("#"+id);
		$(inp).unbind("click");
		inp.data(opts);
		 
		if(!IsEmpty(opts.iTableId)){
			$(window).data("fileUploadTableId_"+opts.eId, opts.iTableId);//将模块主键缓存起来
		}
		if (!IsEmpty(opts.afterUpload)) {
			$(window).data("afterUpload_"+opts.eId,opts.afterUpload);//将上传回调函数缓存起来
		}
		if (!IsEmpty(opts.afterClean)) {
			$(window).data("afterClean_"+opts.eId,opts.afterClean);//将清空回调函数缓存起来
		}
		 
		var div = $("#"+divId);
	    div.find(".inputClear").click(function(){ //清空事件
		 		commonUtil.jqFileUpload_cleanFile(opts.eId); 
		}); 
		
	}
	
	
	//以下重绘文件上传控件
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
	
	/**取值*/
	function getValue(obj){
		
	}
	
	/**设值*/
	function setValue(obj, data){
		
	}
	
})(jQuery);

