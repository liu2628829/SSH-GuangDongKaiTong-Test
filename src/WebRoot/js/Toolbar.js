/**
var toolbarSettings = {
	renderTo : 'toolbar',
	slideDown : true, //使用下拉菜单显示多余的工具栏按钮
	items : Toolbar.getItemsByRightOrder(toolJson) //按权限管理顺序显示按钮
};
new Toolbar(toolbarSettings).render();
getItemsByRightOrder 

 //查询按钮的显示与隐藏
    var tb = Toolbar.getIntance("toolbar");
	if(flg*1){
		tb.showBtnByText("查询");
	}else{
	    tb.hideBtnByText("查询"); 
	}

*/

Toolbar = function(config){
    
    this.config = config;
    
	this.render=function(){
		items = this.config.items;
		
		var html=['<ul class="'+(this.config.cssName ? this.config.cssName : 'business') +'">'];
		for(var i=0; i<items.length; i++){
			var item = items[i];
		 	if(check(item)){
       			html.push('<li NO="'+i+'" '+(item["style"] ? 'style="'+item["style"]+'"': '')+'>'+item["text"]+'</li>');
            }
        }
        html.push('</ul>');
        
        var div = typeof(this.config.renderTo)=="string" ? $("#"+this.config.renderTo) : $(this.config.renderTo);
        div.append(html.join(""));
        div.find("li").bind("click", this.click);
        div.find("li").last().css({borderRight:'0px'});
        
        div.css({marginBottom:(this.config.marginBottom?this.config.marginBottom:"5")+"px",float:config.align?config.align:'right'});
	};
	
	this.click=function(event){
		var target = $(event.target);
		var i = $(target).attr("NO"); 
		var fun = items[i].handler;
		if(fun){fun(target);}
	};
	
	var items = null;
	
	function check(item){
		var boo = false;
		
		boo = typeof(item)=="object";
		
		return boo;
	}
};

/**获取实例*/
Toolbar.getIntance=function(id){
	return {
		/**
		 * 禁用指定文本的按钮
		 * text 文本值
		 */
		disableBtnByText:function(txt){
		},
		/**
		 * 启用指定文本的按钮
		 * text 文本值
		 * handler 绑定事件
		 */
		useableBtnByText: function(obj){
		},
		/**
		 * 禁用指定顺序的按钮
		 * text 按钮的顺序
		 */
		disableBtnByNum: function(obj){
		},
		/**
		 * 启用指定号数的按钮
		 * text 按钮的顺序
		 * handler 绑定事件
		 */
		useableBtnByNum: function(obj){
		},
		/**
		 * 隐藏指定文本的按钮
		 * text 文本值
		 */
		hideBtnByText:function(txt){
			var div = typeof(id)=="string" ? $("#"+id) : $(id);
	         div.find("li").each(function(){
	        	if($(this).text()==txt){
	        		$(this).hide();
	        	}
	        });
		},
		/**
		 * 显示指定文本的按钮
		 * text 文本值
		 */
		showBtnByText:function(txt){
			var div = typeof(id)=="string" ? $("#"+id) : $(id);
	        div.find("li").each(function(){
	        	if($(this).text()==txt){
	        		$(this).show();
	        	}
	        });
		},		
		/**
		 * 隐藏指定顺序的按钮
		 * text 按钮的顺序
		 */
		hideBtnByNum: function(obj){
		},
		/**
		 * 显示指定号数的按钮
		 * text 按钮的顺序
		 */
		showBtnByNum: function(obj){
		},
		/**
		 * 增加指定文本的按钮
		 * text 文本值
		 * bodyStyle 图标样式
		 * handler 绑定事件
		 */
		addBtnByText: function(obj){
		},
		/**
		 * 增加指定文本的按钮
		 * text 文本值
		 * bodyStyle 图标样式
		 * handler 绑定事件
		 */
		addBtnByText: function(obj){
		},
		/**
		 * 增加指定顺序的按钮
		 * text 文本值
		 * num 按钮的顺序
		 * bodyStyle 图标样式
		 * handler 绑定事件
		 */
		addBtnByNum: function(obj){
		},
		/**
		 * 删除指定顺序的按钮
		 * text 按钮的顺序
		 */
		deleteBtnByNum: function(obj){
		},
		/**
		 * 根据名称获取item对象
		 */
		getItem: function(textValue){
			var items = this.items;
			var returnObj = {};
			for(var i=0; i<items.length; i++){
				if(textValue == items[i].text){
					returnObj = items[i]; break;
				}
			}
			return returnObj;
		},
		/**
		 * 根据名称获取工具栏中item对象
		 * text 文本值
		 */
		getItemByText: function(obj){
			var allBt = this.slideDown ? this.startSlideToolbar.renderObj.find("ul.newul>li")
							: this.startScrollbar.dependID.find("ul.newul>li");
			var textVal = typeof(obj) == "object" ? obj.text : obj;
			var returnObj = {};
			for(var i=0; i<allBt.length; i++){
				var btText = allBt.eq(i).find("button").html();
				if(textVal == btText){
					returnObj = this.getItem(textVal);
					break;
				}
			}
			return returnObj;
		},
		/**
		 * 根据序号获取工具栏item对象
		 * text 按钮的顺序
		 */
		getItemByNum: function(obj){
			var allBt = this.startScrollbar.dependID.find("ul.newul>li");
			var textVal = typeof(obj) == "object" ? obj.text : obj;
			if(textVal == 0 || textVal == 1){textVal = 0;}else{textVal = (textVal*2-1)-1;}
			for(var i=0; i<allBt.length; i++){
				if(textVal == i){
					var text = allBt.eq(i).find("button").val();
					return this.getItemByText(text);
				}
			}
		}
	};
};

/**
 * 根据权限管理顺序对工具栏进行排序（针对资源系统通用表单处理）
 * rights: 当前用户权限数据
 * toolItem: 工具栏json数组
 */
Toolbar.getItemsByRightOrder = function(rights, toolItem) {
	
	//如果只有1个参数，偿试自动找 权限定义数据
	if (arguments.length == 1) { 
		toolItem = rights;
		
		rights=null;
		try{
			 /*
			  * 如果界面有引用"/admin/common/right.jsp?type=1",则必会有sysRightList定义
			  * sysRightList格式如 {"ringhtNo_key1":1,"ringhtNo_key2":0,....}   ringhtNo_key 是权限定义的sRightNo, 1有权限，0无权限
			  */
			if(sysRightList){
				rights = sysRightList;
			}else if(window.opener && window.opener.sysRightList){
				rights = window.opener.sysRightList;
			}else if(window.parent && window.parent.sysRightList){
				rights = window.parent.sysRightList;
			}else if(window.dialogArguments && window.dialogArguments.sysRightList){
				rights = window.dialogArguments.sysRightList;
			}else if(window.top && window.top.sysRightList){
				rights = window.top.sysRightList;
			}
		}catch(e){}
	}
	
	if(!rights || typeof(rights) != "object"){ //说明无权限控制
		return toolItem;
	}
	
	for(var i=toolItem.length-1; i>=0; i--){
		var item = toolItem[i];
		if(typeof(item)!="object"){continue;}
		
		var sRightNo = item.rightNo;
		if(!sRightNo){continue;}//说明当前按钮不需要进行权限控制
		
		if( !(sRightNo && rights[sRightNo]==1)){//说明没权限
			toolItem.splice(i,1);
		}
	}
	
	return toolItem;
};
