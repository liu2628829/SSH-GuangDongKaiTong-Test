/*
var items = [
			{type:"button",text:"列表全部字段",bodyStyle:"chargeback",useable:"T",
				handler:function(){Catt_Export_Direct(option);}
			}, 
			{type:"button",text:"二次筛选字段",bodyStyle:"gongdang",useable:"T",
				handler:function(){Catt_Export_FilterColums(option);}
			},
            {type:"button",text:"导出并用于导入",bodyStyle:"chargeback",useable:"T",
				handler:function(){Catt_Export_InOut_Colums(option);}
			}
		];
		
for(var i=0;i<items.length;i++){
	var tempMenu=document.createElement("div");
	$(tempMenu).text(items[i].text);//tempMenu.innerText=items[i].text;
	tempMenu.iconCls=items[i].bodyStyle;
	$(tempMenu).bind("click",items[i].handler);
	div.appendChild(tempMenu);
}
document.body.appendChild(div);

var ev = option.ev;
if(ev==null){ 
   try{ev = event;}catch(e){}
}
var point = getMousePoint(ev);
//弹出菜单定位问题
$("#optMenu_"+option.formId).menu({});
$("#optMenu_"+option.formId).menu("show", { left:point.x,  top:point.y  });
*/


/**下拉菜单*/
(function($) {
		$.fn.menu = function(opt,options){
			if(typeof opt != 'object'){
				if(opt == 'loadData'){
				
				}else if(opt=='show'){
					var div = $(this);
					var point = whenOutScrmee({x:options.left,y:options.top},div);
					div.css(point);
					div.slideDown();
				}
			}else{
				loadData(this, opt);
			}
		};
		
		//生成
		function loadData(obj, opt){
			var div = $(obj);
			div.addClass("piXiala piXiala1")
			div.mouseleave(function(){
				div.slideUp();
			})
		}
		
})(jQuery);

//获取鼠标的坐标
function getMousePoint(ev,obj) {
	// 定义鼠标在视窗中的位置
	var point = { x:0, y:0 };
       if(ev!=null){
           // 如果浏览器支持 pageYOffset, 通过 pageXOffset 和 pageYOffset 获取页面和视窗之间的距离
           if(typeof window.pageYOffset != 'undefined') {
               point.x = window.pageXOffset;
               point.y = window.pageYOffset;
           }
           // 如果浏览器支持 compatMode, 并且指定了 DOCTYPE, 通过 documentElement 获取滚动距离作为页面和视窗间的距离
           // IE 中, 当页面指定 DOCTYPE, compatMode 的值是 CSS1Compat, 否则 compatMode 的值是 BackCompat
           else if(typeof document.compatMode != 'undefined' && document.compatMode != 'BackCompat') {
               point.x = document.documentElement.scrollLeft;
               point.y = document.documentElement.scrollTop;
           }
           // 如果浏览器支持 document.body, 可以通过 document.body 来获取滚动高度
           else if(typeof document.body != 'undefined') {
               point.x = document.body.scrollLeft;
               point.y = document.body.scrollTop;
           }
           // 加上鼠标在视窗中的位置
           point.x += ev.clientX;
           point.y += ev.clientY;
       }
	// 返回鼠标在视窗中的位置
	return point;
}

/**超出屏幕时的处理*/
function whenOutScrmee(point,obj){
	  if(obj){
    	   var w = $(obj).width();
    	   var h = $(obj).children().length*40;
    	   var w1 = $(window).width();
    	   var h1 = $(window).height();
    	   
    	   if(point.x+w>w1){
    		   point.x = point.x - ((point.x+w)-w1); 
    		   }
    	   if(point.y+h>h1){
    		   point.y = point.y - ((point.y+h)-h1); 
    	   }
       }
	  return  {left:point.x,top:point.y};
}

/**
 * 初始化下拉按钮
 * pId  触发下拉列表的对象或对象ID
 * listContainerId 按钮列表的容器对象或容器ID
 * btnList 按钮列表 [{text:'按钮1'}]
 * fn 回调函数 function fn(btnText){}
 */
function initDropdownList(pId, listContainerId, btnList, width, fn){
	var mtimeout = null;
	
	var btnListId = pId+'_ddl';
	
	//添加按钮dom
	var html = '<div class="piXiala" id="'+btnListId+'" style="width:'+width+'px;">';
	html += '<div class="piArrow"></div><dl>';
	var len = btnList.length; 
	for(var i = 0; i < len; i++){
		var obj = btnList[i];
		html += '<dt style="border-bottom:none;"><h3>'+obj.text+'</h3></dt>';
	}
	html += '</dl></div>';
	$("#"+listContainerId).append(html);
	
	$("#"+pId).off('hover').hover(function(){
		try{clearTimeout(mtimeout);}catch(e){}
		var offset = $("#"+pId).offset();
		var h = $("#"+pId).outerHeight();
		$('#'+btnListId).css({left:offset.left, top:offset.top + h}).slideDown();
	});
	
	$("#"+pId).off('mouseleave').mouseleave(function(){
		try{clearTimeout(mtimeout);}catch(e){}
		mtimeout = setTimeout(function(){
			$('#'+btnListId).slideUp();
		}, 100);
	});
	
	$('#'+btnListId).hover(function(){
		try{clearTimeout(mtimeout);}catch(e){}
	});
	
	$('#'+btnListId).mouseleave(function(){
		//$("#"+btnListId).slideUp();
		try{clearTimeout(mtimeout);}catch(e){}
		mtimeout = setTimeout(function(){
			$('#'+btnListId).slideUp();
		}, 100);
	});
	
	//注册按钮点击事件
	$('#'+btnListId+" dt").click(function(){
		var text = $(this).text();
		fn.call(this, text);
	});
	
}
