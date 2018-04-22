 /**消息框，div窗口*/
(function($) { 

	//消息提示框，确认提示框
	$.messager={};
	$.messager.defaults={};
	$.messager.confirm= function(title, msg, fun, opts) {
		Base.confirm(msg, function(){fun(true);}, function(){fun(false);}); 
	};	
	$.messager.alert=function(title, msg, icon, fn, opts){
		Base.alert(msg,fn);
	};
    
    //div窗口
	$.fn.window= function(method, options) {
		if(typeof(method)=='object'){
			create(this, method);
		}else if(method=='close'){
			var data = $(this).data();
			layer.close(data.index); 
		}
	};	
	
	function create(obj, opts){
		var content = (opts.url) ? null : $(obj).prop("outerHTML");
		var id= $(obj).attr("id");
		if(content){ $(obj).remove(); }
		
		var options = { 
					  closeButtonEnable: false,
		              confirmButtonEnable: false,	
				      title: opts.title, //标题 
				      width: opts.width,   //宽度 
				      height: opts.height,   //高度 
				      content: content, //'内容（可以支持html）',   //如果url属性值没配则打开这个内容 
				      url : opts.url 
				   }; 
		
		var index = Base.dialog(options); 
		$("#"+id).data({index:index});
	}
})(jQuery);

