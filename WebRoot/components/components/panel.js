 /**面板
 panel({tools:tools})
 .panel("options")
 .panel({onResize:setH}
 .panel("resize",{width:options.width,height:options.height});
 */
(function($) { 
	$.fn.panel= function(method, options) {
		if(typeof(method)=='object'){
		
			var oldOpts = $(this).data();
			if(oldOpts){$.extend(oldOpts, method);}else{oldOpts=method;}
			create(oldOpts, this); 
		}else if(method=='options'){
			return $(this).data();
		}
	};	
	
	/**构建*/
	function create(opts, obj){
	
		var div = $(obj);
		
		var boo = div.find("div").first().hasClass("positionDiv");
		var title = boo ? div.find("div").first().find("h2").text() : div.attr("title")
		div.attr("title","");
		if(boo){div.find("div").first().remove();} //存在则先删除
		if(!title){return;}
	    var html = [];
		html.push('<div class="positionDiv">');
		html.push('	<span></span>');
		html.push('	<h2>'+title+'</h2>');
		html.push('</div>');
		
		div.prepend(html.join("")) ;// append或appendTo
		
		//div.find(".positionDiv").attr("kk","100"); 
		//div.find(".positionDiv").css({marginBottom:0, color:"red", float:'none'});  // margin-bottom: 5px; float: right;
		
		//工具栏
		var tools = opts.tools;//自定义工具栏
		if(!(tools&&tools.length>0)){tools=[];}
		//收缩按钮
		if(opts.collapsible){
			tools.push({title:'▽',style:"font-size:25px;", handler:function(target){ 
					var txt = target.text();
					boo = txt=="△";
					target.text( boo ? "▽" : "△"); 
					
					var barDiv = target.closest(".positionDiv");
					var contentDiv = barDiv.parent(); 
					var children = contentDiv.children(); 
					
					if(boo){
						contentDiv.find("*[displayFlg='1']").slideDown('fast');
						contentDiv.find("*[displayFlg='1']").attr("displayFlg","");
					}else{
						children.each(function(){
							if($(this).css("display")!="none" && !$(this).hasClass("positionDiv")){
								$(this).attr("displayFlg","1");
							}	
						});
						
						contentDiv.find("*[displayFlg='1']").slideUp('fast');
					}
					
				}
			});
		}
		for(var i=0;i<tools.length; i++){
			tools[i].text=tools[i].title;
		}
		
		var toolbar = new Toolbar({renderTo : div.children().first(), items:tools, cssName:"business1", marginBottom:"0"}).render();
		
		opts.tools=tools;
		
		div.data(opts);
		
	}
})(jQuery);

