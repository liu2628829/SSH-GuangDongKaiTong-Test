/**
var toolbarSettings = {
	renderTo : 'toolbar',
	slideDown : true, //ʹ�������˵���ʾ����Ĺ�������ť
	items : Toolbar.getItemsByRightOrder(toolJson) //��Ȩ�޹���˳����ʾ��ť
};
new Toolbar(toolbarSettings).render();
getItemsByRightOrder 

 //��ѯ��ť����ʾ������
    var tb = Toolbar.getIntance("toolbar");
	if(flg*1){
		tb.showBtnByText("��ѯ");
	}else{
	    tb.hideBtnByText("��ѯ"); 
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

/**��ȡʵ��*/
Toolbar.getIntance=function(id){
	return {
		/**
		 * ����ָ���ı��İ�ť
		 * text �ı�ֵ
		 */
		disableBtnByText:function(txt){
		},
		/**
		 * ����ָ���ı��İ�ť
		 * text �ı�ֵ
		 * handler ���¼�
		 */
		useableBtnByText: function(obj){
		},
		/**
		 * ����ָ��˳��İ�ť
		 * text ��ť��˳��
		 */
		disableBtnByNum: function(obj){
		},
		/**
		 * ����ָ�������İ�ť
		 * text ��ť��˳��
		 * handler ���¼�
		 */
		useableBtnByNum: function(obj){
		},
		/**
		 * ����ָ���ı��İ�ť
		 * text �ı�ֵ
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
		 * ��ʾָ���ı��İ�ť
		 * text �ı�ֵ
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
		 * ����ָ��˳��İ�ť
		 * text ��ť��˳��
		 */
		hideBtnByNum: function(obj){
		},
		/**
		 * ��ʾָ�������İ�ť
		 * text ��ť��˳��
		 */
		showBtnByNum: function(obj){
		},
		/**
		 * ����ָ���ı��İ�ť
		 * text �ı�ֵ
		 * bodyStyle ͼ����ʽ
		 * handler ���¼�
		 */
		addBtnByText: function(obj){
		},
		/**
		 * ����ָ���ı��İ�ť
		 * text �ı�ֵ
		 * bodyStyle ͼ����ʽ
		 * handler ���¼�
		 */
		addBtnByText: function(obj){
		},
		/**
		 * ����ָ��˳��İ�ť
		 * text �ı�ֵ
		 * num ��ť��˳��
		 * bodyStyle ͼ����ʽ
		 * handler ���¼�
		 */
		addBtnByNum: function(obj){
		},
		/**
		 * ɾ��ָ��˳��İ�ť
		 * text ��ť��˳��
		 */
		deleteBtnByNum: function(obj){
		},
		/**
		 * �������ƻ�ȡitem����
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
		 * �������ƻ�ȡ��������item����
		 * text �ı�ֵ
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
		 * ������Ż�ȡ������item����
		 * text ��ť��˳��
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
 * ����Ȩ�޹���˳��Թ������������������Դϵͳͨ�ñ�����
 * rights: ��ǰ�û�Ȩ������
 * toolItem: ������json����
 */
Toolbar.getItemsByRightOrder = function(rights, toolItem) {
	
	//���ֻ��1�������������Զ��� Ȩ�޶�������
	if (arguments.length == 1) { 
		toolItem = rights;
		
		rights=null;
		try{
			 /*
			  * �������������"/admin/common/right.jsp?type=1",��ػ���sysRightList����
			  * sysRightList��ʽ�� {"ringhtNo_key1":1,"ringhtNo_key2":0,....}   ringhtNo_key ��Ȩ�޶����sRightNo, 1��Ȩ�ޣ�0��Ȩ��
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
	
	if(!rights || typeof(rights) != "object"){ //˵����Ȩ�޿���
		return toolItem;
	}
	
	for(var i=toolItem.length-1; i>=0; i--){
		var item = toolItem[i];
		if(typeof(item)!="object"){continue;}
		
		var sRightNo = item.rightNo;
		if(!sRightNo){continue;}//˵����ǰ��ť����Ҫ����Ȩ�޿���
		
		if( !(sRightNo && rights[sRightNo]==1)){//˵��ûȨ��
			toolItem.splice(i,1);
		}
	}
	
	return toolItem;
};
