 /**黑板窗*/
(function($) { 
	$.fn.blackboard = function(opt, options){
		if(typeof opt != "object"){
			if(opt == "resize"){ //重置大小 {width:"",height:""}
				
			}else if(opt == "close"){//关闭
				open_close(this, 1);
			}else if(opt == "setContent"){//重置内容
				setContent(this, options);
			}
		}else{
			loadData(this, opt);//初始化
		}
	};
	
	/**初始化*/
	function loadData(obj, opts){
		
		var parent = $(obj);
		var opts = $.extend(true, {}, $.fn.blackboard.defaults, opts)
		parent.data("blackboard_options", opts);
		
		var isBody = parent[0].nodeName == "BODY" ;
		var className = isBody ? "blackboard_fixed" : "blackboard_absolute";
		var closeType = opts.closeType == 1;
		var tips = closeType ? '' : 'title="点击背景区域可关闭"';
		
		if(!isBody){parent.addClass("blackboard_absolute_parent");}
		
		var blackboard = $(parent).children(".blackboard");
		
		if(blackboard.length<=0){
			//在当前容器下增加1个顶层容器
			var html = [];
			html.push('<div class="blackboard '+className+'">');
			if(opts.title){
				html.push('<span class="blackboard_title" title="'+opts.title+'">'+opts.title+'</span>');	 
			}
			html.push('<span class="blackboard_close_button" title="关闭">×</span>');	
			html.push('<div class="blackboard_content_parent" '+tips+'>');	
			html.push('<div class="blackboard_content">');	 
			if(opts.content){html.push(opts.content);}
			html.push('</div></div>');	 
			html.push('</div>');	
			
			parent.append(html.join(""));
			blackboard = $(parent).children(".blackboard");
			
			opts.parentPosition = $.extend(true, opts.parentPosition, open_close_type[opts.openType][2]);
			blackboard.css(opts.parentPosition);
			
			var contentParent = blackboard.children(".blackboard_content_parent");
			var content = contentParent.children(".blackboard_content");
			content.css(opts.position);
			
			var closeEvtObj = closeType ? blackboard.children(".blackboard_close_button") : blackboard;
			
			if(!closeType){ $(contentParent).css({cursor:"pointer"}); }
			
			$(closeEvtObj).click(function(){
				var evt = $(event.target);
				if(!closeType && (!evt.hasClass("blackboard_content_parent") && !evt.hasClass("blackboard_close_button") )){return;}
				open_close(obj, 1);
			});
		}
		
		open_close(obj, 0);
	}
	
	/**开与关*/
	function open_close(obj, oc){
		var parent = $(obj);
		var opts = parent.data("blackboard_options");
		var openType = opts.openType;
		var silde = open_close_type[openType][oc];
		var blackboard =  $(parent).children(".blackboard");
			
		/**如果有标题，且内容区会覆盖到标题，应减少内容区高度*/
		setTimeout(function(){
			var contentParent = blackboard.children(".blackboard_content_parent");
			var content = contentParent.children(".blackboard_content");
			var blackboardHeight = blackboard.height()
			var titleHeight = opts.title ? blackboard.children(".blackboard_title").outerHeight() : 0;
			var contentHeight = content.height();
			var h = blackboardHeight- titleHeight;
			contentParent.height(h);
			content.height( contentHeight > h ? h : contentHeight); 
		},1100); //必须延时，因为blackboard容器初始设置是不可见，即一开始没高度
		
		function callback(){
			if(oc==0 && opts.onOpened){
				opts.onOpened($(blackboard).children(".blackboard_content"));
			}else if(oc==1 && opts.onClosed){
				opts.onClosed($(blackboard).children(".blackboard_content"));
			}
		}
		var txt = "$(blackboard)."+silde+"('1000',callback)";
		eval(txt);
	}
	
	/**重置内容*/
	function setContent(obj, content){
		var contentDiv = $(obj).children(".blackboard").children(".blackboard_content_parent").children(".blackboard_content");
		contentDiv.html(content);
		var parent = $(obj);
		var opts = parent.data("blackboard_options");
		if(opts.onSetContent){
			opts.onSetContent(contentDiv);
		}
	}
	
	/***默认配置**/
	$.fn.blackboard.defaults = {
		title:"", //标题
		openType:"io", //打开动画效果：io渐进渐出，tb从上至下，bt从下至上，lr:从左至右，rl:从右至左
		parentPosition:{}, //黑底板位置设置
		position:{width:"80%",height:"80%",left:0,right:0,top:0,bottom:0}, //内容区默认居中，如果想想置底，则把top:"auto",
		closeType:1,//1点关闭按钮才关闭，2点关闭按钮或暴露在外的黑面板区域都可关闭
		
		content:"",//要在小黑板内呈现的内容,文本或html
		
		onOpened:function(contentDiv){},//开窗完成触发
		onClosed:function(contentDiv){},//关闭完成触发
		onSetContent:function(contentDiv){}//当重置内容后触发
	};
	
	/**开窗关窗动画方式*/
	var open_close_type = {
		io:["fadeIn","fadeOut",{}],
		lr:["slideLeftShow","slideLeftHide",{left:"0px",right:"none"}],
		rl:["slideLeftShow","slideLeftHide",{}],
		bt:["slideDown","slideUp",{bottom:"0px",top:"none"}],
		tb:["slideDown","slideUp", {bottom:"none",top:"0px"}]
	};
	
	jQuery.fn.slideLeftHide = function( speed, callback ) {  
        this.animate({  
           width : "hide", paddingLeft : "hide",  paddingRight : "hide", marginLeft : "hide", marginRight : "hide"  
        }, speed, callback );  
    };  
    jQuery.fn.slideLeftShow = function( speed, callback ) {  
        this.animate({  
            width : "show",paddingLeft : "show",  paddingRight : "show", marginLeft : "show", marginRight : "show"  
        }, speed, callback );  
    };   
})(jQuery);

