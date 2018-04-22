/**
author:gaotao
2011-9-24
温馨提示tip工具类,针对具有title与msg属性的标签进行tip提示
此工具类依赖jquery
*/
$(window).bind("load", function(){
	//加载tooltips控件wz_tooltip.js须在tip_balloon.js前加载完毕
	var script1= document.createElement('script');   
	script1.type= 'text/javascript';   
	script1.src= getPathName(window)+'/components/tips/js/wz_tooltip.js';    
	document.body.appendChild(script1);
	//tip显示的宽度
	function getW(val){
		var w = mousePosition() - 40;
		if(val.length<=5){ w = 0;}
		else if(val.length<=18){ w = 100;}
		else if(val.length<=150){ w = 200;}
		return w;
	}
	//得到当前光标衡向位置
	function mousePosition(evt){
		evt = evt || window.event;
		if(!evt){evt = window.parent.event;}
		if(evt.pageX){//Mozilla
			return evt.pageX;
		}else{//IE
			return evt.clientX;// + document.body.scrollLeft - document.body.clientLeft
		}
	}
	//以下对于title，msg属性为空的，对于select标签，对于只读的文本框，都不做tip提示(select标签不生效，不然干挠鼠标点击选择)
	//针对具有title属性的标签
	var tags=$("*[title]").not("[region]").not(".panel-tool>*").not(".PopupWindow *").not(".Digit *").not(".filterTable *").not(".fileUpload *").not(".datagrid *").not(".JQSelect *");
	tags.bind("mouseover",function(event){
		event.stopPropagation();
		var obj=$(event.target);
		var val=obj.attr("title")?obj.attr("title"):'';		
		//新的tooltips不再干扰select选择
		if(!(val||val.replace(/ /g,'').length>0)||obj.attr("readOnly")){return;}
		if(('undefined'!=typeof JUMPHORZ)){
			Tip(val, JUMPHORZ, true, JUMPVERT, true, WIDTH, getW(val), PADDING, 5, FOLLOWMOUSE, false, OFFSETX, -10);
		}
	});

	tags.bind("mouseout",function(event){
		UnTip();
		event.stopPropagation();
		}
	);
	//针对具有msg元素的标签
	var tags2=$("*[msg][showDefault!=true]").not(".JQSelect *");
	var interval;
	var onFocus=function(event){
		var obj=$(event.target);
		var val=obj.attr("msg")?obj.attr("msg"):'';
		if(!(val||val.replace(/ /g,'').length>0)||obj.attr("readOnly")||obj.is("SELECT")){return;}
		if(('undefined'!=typeof JUMPHORZ)){
			Tip(val, JUMPHORZ, true, JUMPVERT, true,WIDTH, getW(val), PADDING, 5, FOLLOWMOUSE, false, OFFSETX, -10);
		}
		interval=window.setTimeout(function(){UnTip();},10000);
	};
	tags2.bind("focus",onFocus);
	tags2.bind("click",onFocus);
	tags2.bind("blur",function(){UnTip();window.clearTimeout(interval);});
});