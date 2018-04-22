 /**窗体边窗*/
(function($) { 
	$.fn.sidebar = function(opt){
		loadData(this, opt);//初始化
	};
	
	/**初始化*/
	function loadData(obj, opts){
		var body = $(obj);
		
		var opts = $.extend(true, {}, $.fn.sidebar.defaults, opts);
		var openType = opts.openType;
		var tp=open_close_type[openType];
		var id = "sidebar_params_"+opts.openType;
		var id2 = id+"_toolbar";
		body.data(id, opts);
		
		if($("#"+id).length<=0){
			var html = [];
			html.push('<div id="'+id+'" class="bottom_menu_bar">');  
			html.push('<span class="expand_span">'+tp[1][0]+'</span>'); 
			html.push('<div id="'+id2+'" class="content_div"></div>');  	
			html.push('</div>');  
		    body.append(html.join(""));
		    
		    var dv = $("#"+id);
		    
		    var position = $.extend(tp[2], opts.position);
		    dv.css(position);
		    
		    var sideKey = tp[0];
		    var sideValue= 15-((sideKey=="bottom"||sideKey=="top") ? dv.height() : dv.width());
		    var whKey = (sideKey=="bottom"||sideKey=="top") ? "height" : "width";
		    position[sideKey]=sideValue;
		    dv.css(position);
		    
		    position={};
		    position[whKey]=Math.abs(sideValue);
		    dv.find("#"+id2).css( $.extend(tp[3], position) );
		    dv.find(".expand_span").css(tp[4]);
		   
		    //工具栏配置
			if(opts.items && opts.items.length>0){
			     var toolbarSettings = {
					renderTo : id2,
					align : 'right',  //仅支持right,left
					cssName: opts.openType.indexOf("b")>=0 ? "business2" : "business3",
					items : Toolbar.getItemsByRightOrder(opts.items) //按权限管理顺序显示按钮
			     };
			     var toolbar = new Toolbar(toolbarSettings).render();
			}else if(opts.content){//自定义内容
				$("#"+id2).html(opts.content);
				if(opts.onSetContent){
					opts.onSetContent( $("#"+id2) );
				}
			}
		    
		    var sideShow = {}, sideHide = {};
		    sideShow[sideKey]=0;
		    sideHide[sideKey]=sideValue;
		    
		    if(opts.eventType=="hover"){
			    dv.hover(
			    	function(){
			    		var span = $(dv).find(".expand_span");
			    		span.text(tp[1][1]);
			    		dv.animate(sideShow,500);
				    },
				    function(){
				    	var span = $(dv).find(".expand_span");
			    		span.text(tp[1][0]);
			    		dv.animate(sideHide,500);
				    }
			    );
		    }
		    
		    dv.find(".expand_span").click(function(){
		    	var span = $(this);
		    	if(span.text()==tp[1][0]){
		    		span.text(tp[1][1]);
		    		dv.animate(sideShow,500);
		    	}else{
		    		span.text(tp[1][0]);
	    			dv.animate(sideHide,500);
		    	}
		    });
		    
		    if(opts.showOnInit){//初始化显示
		    	dv.find(".expand_span").trigger('click');
		    }
		}
	}
	
	/***默认配置**/
	$.fn.sidebar.defaults = {
		openType:"bt", //bottom,top
		position:{}, //内容区默认居中，如果想想置底，则把top:"auto"
		items:[], //按钮定义，同Toolbar.js的定义
		
		content:"", //自定义内容，如有items属性且items内有元素，此属性不生效
		onSetContent:function(contentDiv){}, //此属性在content有效时才生效
		
		showOnInit:false, //初始化时是否显示 false不显示，true显示
		eventType:"hover" //hover：鼠标移入显示,移出或点击小按钮隐藏 ; click 鼠标点击小按钮显示再点击隐藏 
	};
	
	/**开窗关窗动画方式
	 * 以下位置数据的作用
	 * 0，显示所在方位
	 * 1，开、关图标
	 * 2，第一层容器样式
	 * 3，第二层容器样式
	 * 4，小按钮样式
	 * */
	var open_close_type = {
		lr:["left", ["》","《"], {bottom:"0px",top:"0px",left:"-135px",right:"none",width:"150px",height:"50%"}, {bottom:0, top:0, left:0, width:"135", height:"100%"},
		    {top:0, bottom:0, right:0, width:"15px", height:"60px", "line-height":"60px", "border-top-right-radius":"50px", "border-bottom-right-radius":"50px"}
		],
		rl:["right", ["《","》"], {bottom:"0px",top:"0px",left:"none",right:"-135px",width:"150px",height:"50%"}, {bottom:0, top:0, right:0, width:"135", height:"100%"},
		    {top:0, bottom:0, left:0, width:"15px", height:"60px", "line-height":"60px","border-top-left-radius":"50px", "border-bottom-left-radius":"50px"}
		],
		bt:["bottom", ["︽","︾"], {bottom:"-55px",top:"none",left:"0px",right:"0px",width:"50%",height:"70px"}, {bottom:0, left:0, right:0, width:"100%", height:"55px"},
		    {top:0, left:0, right:0, width:"60px", height:"15px", "border-top-left-radius":"50px", "border-top-right-radius":"50px", "margin-bottom":"-20px"}
		],
		tb:["top", ["︾","︽"], {bottom:"none",top:"-55px",left:"0px",right:"0px",width:"50%",height:"70px"}, {top:0, left:0, right:0, width:"100%", height:"55px"},
		    {bottom:0, left:0, right:0, width:"60px", height:"15px", "border-bottom-left-radius":"50px", "border-bottom-right-radius":"50px"}
		]
	};
})(jQuery);

