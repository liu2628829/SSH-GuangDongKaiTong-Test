/**
author:gaotao
2011-9-24
��ܰ��ʾtip������,��Ծ���title��msg���Եı�ǩ����tip��ʾ
�˹���������jquery
*/
$(window).bind("load", function(){
	//����tooltips�ؼ�wz_tooltip.js����tip_balloon.jsǰ�������
	var script1= document.createElement('script');   
	script1.type= 'text/javascript';   
	script1.src= getPathName(window)+'/components/tips/js/wz_tooltip.js';    
	document.body.appendChild(script1);
	//tip��ʾ�Ŀ��
	function getW(val){
		var w = mousePosition() - 40;
		if(val.length<=5){ w = 0;}
		else if(val.length<=18){ w = 100;}
		else if(val.length<=150){ w = 200;}
		return w;
	}
	//�õ���ǰ������λ��
	function mousePosition(evt){
		evt = evt || window.event;
		if(!evt){evt = window.parent.event;}
		if(evt.pageX){//Mozilla
			return evt.pageX;
		}else{//IE
			return evt.clientX;// + document.body.scrollLeft - document.body.clientLeft
		}
	}
	//���¶���title��msg����Ϊ�յģ�����select��ǩ������ֻ�����ı��򣬶�����tip��ʾ(select��ǩ����Ч����Ȼ���������ѡ��)
	//��Ծ���title���Եı�ǩ
	var tags=$("*[title]").not("[region]").not(".panel-tool>*").not(".PopupWindow *").not(".Digit *").not(".filterTable *").not(".fileUpload *").not(".datagrid *").not(".JQSelect *");
	tags.bind("mouseover",function(event){
		event.stopPropagation();
		var obj=$(event.target);
		var val=obj.attr("title")?obj.attr("title"):'';		
		//�µ�tooltips���ٸ���selectѡ��
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
	//��Ծ���msgԪ�صı�ǩ
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