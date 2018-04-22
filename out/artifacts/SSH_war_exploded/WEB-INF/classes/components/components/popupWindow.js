 /**弹窗选控件
  $("#XX").popupWindow({
		onClick:function, //点击"..."开窗按钮事件, 此函数要求反回值为:{id:"",text:""}
		onClean:function, //点击"X"清空按钮事件(非必须)
	});
  **/
(function($) { 

	$.fn.popupWindow= function(method, options) {
		if(typeof(method)=='object'){
			create(method, this); //{onClick:function, onClean:function}
		}else if(method=='options'){
			return $(this).data();
		}else if(method=='getValue'){
			return getValue(this); //返回结果：{id:"",text:""} 
		}else if(method=='setValue'){
			setValue(this, options);//options格式： {id:"",text:""}
		}
	};	
	
	/**构建* */
	function create(opts, obj){
		 var inp = $(obj);
		 var id = inp.attr("id");
	     var divId = 'popup_'+id;
	     var newId = id+'_text';
	     if(inp.parent().attr("id")==divId){return;} //说明已经构建过了,不能再构建
	     opts.newId = newId;
	     inp.data(opts);//给新对象绑定数据
	     
	     inp.hide(); //隐藏旧的
	     var html = [];
	     
	     if(inp.next().attr('id')==newId){inp.next().remove();}
	     else if(inp.prev().attr('id')==newId){inp.prev().remove();}
	     
	     if(inp.next().hasClass("specie")){//不被重复生成
	    	 inp.next().remove();
	     }
	     
	     html.push('<div class="specie" id="'+divId+'">');
	     html.push('<input readonly class="deInput" style="width:100%" type="text" id="'+newId+'" name="'+newId+'"/>');
	     //html.push(inp.prop("outerHTML").replace("<input", "<input readonly "));
		 html.push('<div class="specieSelect"></div><div class="inputClear" style="display:none;"></div></div>');
		 inp.after(html.join(''));  //构建新的	  
		 
		 var div = $("#"+divId);
		 div.find(".specieSelect").click(function(){ //点击事件
		 		if(inp.attr("disabled") || $("#"+newId).attr("disabled")){return false;}
		        var rs = getValue(inp);
		 		rs = opts.onClick.call(this, rs);
		 		
		 		setValue(inp, rs);
		 });   
		 
		 div.hover(
				 function(){if(inp.val() && !(inp.attr("disabled") || $("#"+newId).attr("disabled") )){div.find(".inputClear").show();}}, 
				 function(){div.find(".inputClear").hide();}
				 ); 
		 div.find(".inputClear").click(function(){ //清空事件
		 		//if(inp.attr("disabled") || $("#"+newId).attr("disabled")){return false;}
		 		var rs = getValue(inp);
		 		
		 		setValue(inp, {id:"",text:""});
		 		if(opts.onClean && rs.id){opts.onClean.call(this,rs);}
		 }); 
	}
	
	/**取值*/
	function getValue(obj){
		var inp = $(obj);
		var newId = inp.attr("id")+"_text";
		return {
				id:inp.val(),
				text:$("#"+newId).val()
		};
	}
	
	/**设值*/
	function setValue(obj, data){
		if(data){
			var inp = $(obj);
			var newId = inp.attr("id")+"_text";
			
			inp.val(data.id);
			$("#"+newId).val(data.text);
		}
	}
	
})(jQuery);

