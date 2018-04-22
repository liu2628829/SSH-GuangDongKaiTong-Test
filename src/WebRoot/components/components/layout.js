 /**布局
var opt1=$('#lay').layout('panel','center').panel("options");
 */
(function($) { 
	$.fn.layout= function(method, options) {
		if(typeof(method)=='object'){
			create(method, this); //{onClick:function, onClean:function}
		}else if(method=='options'){
			return $(this).data();
		}else if(method=='panel'){
		    var divs = $(this).children();
		    var div;
		    divs.each(function(){
		    	if($(this).attr("region")==options){
		    		div = $(this);
		    	}
		    })
		    return $(div);
		}else if(method=='resize'){
			resize(this);
		}
	};	
	
	/**构建*/
	function create(opts, obj){
		$(obj).children().each(function(){
			var div = $(this);
			if(div.attr("region")){
				$(div).panel({collapsible:false});
			}
		});
		
		resize(obj);
		$(window).bind("resize", function(){resize(obj)});
	}
	
	function resize(obj){
		var h=0;
		var centerDiv;
		$(obj).children().each(function(){
			var div = $(this);
			if(div.attr("region")){
				if(div.attr("region")!='center'){
					h+=$(div).height();
				}else{
					centerDiv=div;
				}
			}
		});
		centerDiv.height($(window).height()-h);
	}
})(jQuery);

