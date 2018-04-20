/**
* 下拉列表
* gaotao 2010-07-18
* 
*/
 var MULIT=false;
 var WIDTH=null;
 var HEIGHT=null;
 var CURRENTINP=null;
 var VALOBJ=null;
 var TLIST,TTXT,TID;
 var ISFOCUSED=false;
 var DATASRC=true;//欠入资源来源，false当前页里的层，嵌入外页面
 var URL=null;

 var DIV=null;//下拉层
 var CBX=null;//全部复选框对象
 var IFRAME=null;//数据页
 var TIMER=null;
 var WIN=null;
 var LIS=null;

 var FG_FRIST=false;//标记是下拉列表否第一次点开
 
  //告警级别下拉框
function INIT(){
		
		if(DATASRC==false){
			DIV=document.getElementById(URL);
			LIS=document.getElementById(TLIST);		
			if(DIV.className!="seltDiv"){
			DIV.className="seltDiv";
			DIV.attachEvent("onmouseover",MOVER);
			DIV.attachEvent("onmouseout",MOUT);
			if (MULIT) {
				var divAllCheckbox=createAllCheckbox();
				DIV.replaceChild(divAllCheckbox,DIV.firstChild);
				CBX = divAllCheckbox.getElementsByTagName('input')[0];
			}
			if(typeof(HEIGHT)!="undefined"&&HEIGHT!=null&&HEIGHT!='')
			    LIS.setHeight(HEIGHT*1);
			else
			    LIS.setHeight(200);
			if(typeof(WIDTH)!="undefined"&&WIDTH!=null&&WIDTH!=''){
		 		LIS.setWidth(WIDTH);
			}else{
		  		WIDTH=CURRENTINP.clientWidth+18+"";
		   		LIS.setWidth(WIDTH*1);
			}
			LIS.attachEvent("onItemClick",DIVCLICK);
			LIS.attachEvent("onItemChecked",DIVCLICK);
			LIS.attachEvent("onItemDblClick",DIVDBCLICK);
			FG_FRIST=true;
			initValue();
			}
		}else{
			DIV=document.getElementById(VALOBJ.id+"Div");

			if(DIV==null||DIV==undefined){
				 DIV=createDiv();
				 document.body.appendChild(DIV);	
				 var divAllCheckbox=createAllCheckbox();
				 DIV.appendChild(divAllCheckbox);
				 CBX = divAllCheckbox.getElementsByTagName('input')[0];
				 IFRAME=createIfram();
				 IFRAME.src=URL;
				 IFRAME.width="100%";
				 IFRAME.height="100%";
				 DIV.appendChild(IFRAME);
				 FG_FRIST=true;
			}else{
				IFRAME=document.getElementById(VALOBJ.id+"Frame");
			}
			GETCONTENT();
		} 
		
		//设置层的高和宽
		if(typeof(WIDTH)!="undefined"&&WIDTH!=null&&WIDTH!=''){
		   DIV.style.width=WIDTH*1+"px";
		}else{
		   WIDTH=CURRENTINP.clientWidth+18+"";
		   DIV.style.width=(WIDTH*1)+"px";
		}
		if(typeof(HEIGHT)!="undefined"&&HEIGHT!=null&&HEIGHT!='')
		  DIV.style.height=HEIGHT+"px";
		else
		  DIV.style.height=200+"px";
	}
	
//如果是第一次点开，并且存储实际值的对象本身就有值,则将值初始入列表
function initValue(){
	if(FG_FRIST&&VALOBJ.value!=''){
		var vals=VALOBJ.value.split(",");
		var items=LIS.items;
		for(var i=0;i<vals.length;i++){
			for(var j=0;j<items.length;j++){
				var v=eval("items["+j+"]."+TID);
				if(vals[i]==v){
					items[j].setChecked(true);
					items[j].setSelected(true);
					break;
				}
			}
		}
	}
}

//创建层
function createDiv(){
		var div=document.createElement("DIV");
		div.id=VALOBJ.id+"Div";
		div.className="seltDiv";
		div.attachEvent("onmouseover",MOVER);
		div.attachEvent("onmouseout",MOUT);
		return div;
	}

//创建清空按钮
function createCleanBtn(){
	var div=document.createElement("DIV");
    div.className="cleanDiv";
    div.innerHTML="<a class='cleanBtnOut' onclick='CLEANCONT();' title='清空' onmouseover=this.className='cleanBtnOver' onmouseout=this.className='cleanBtnOut'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;清空</a>";
	return div;
}

function createAllCheckbox(){
	var div=document.createElement("DIV");
    with(div.style){backgroundColor='#D0E1F0';textAlign='left';marginRight:10;}
    div.innerHTML="<input type='checkbox' style='float:left' onclick='checkAll()' /><span style='line-height:20px'>全部</span>";
	return div;
}

//全选方法
function checkAll(){
	var items = LIS.items;
	var checkBox = getCheckBox();
	//全选或全不选
	for (var i = 0;i < items.length;i++) {
		with (items[i]) {
			if (checkBox.checked) {
				setChecked(true);
			} else {
				setChecked(false);
			}
		}
	}
	//文本框和隐藏域赋值
	var txt="";
	var ids="";
	for(var i=0;i<LIS.checkedItems.length;i++){
		with (LIS.checkedItems[i]) {
			if (txt.length > 0) {txt+=",";ids+=",";}
			txt += eval(TTXT);
			if(TID!=null&&TID!=undefined)
			ids+=eval(TID);
		}
	}
	CURRENTINP.value=txt;
	VALOBJ.value=ids;
}

//获取全选checkbox
function getCheckBox() {
	var inputs = DIV.getElementsByTagName('input');
	for (var i =0;i < inputs.length;i++) {
		if (inputs[i].type == 'checkbox') {
			return inputs[i];
		}
	}
	return null;
}

//清空文本框和隐藏域内容,以及取消“√”
function CLEANCONT(){
		CURRENTINP.value="";
		VALOBJ.value='';
		var items=LIS.checkedItems;
		if(items!=null&&items.length>0){
			for(var i=0;i<items.length;i++)
				items[i].setChecked(false);
		}
}

//创建iframe
function createIfram(){
		var fram=document.createElement("IFRAME");
		fram.id=VALOBJ.id+"Frame";	
		return fram;
	}

//获取iframe内嵌window
function GETCONTENT(){	
		WIN=IFRAME.contentWindow;
		LIS=WIN.document.getElementById(TLIST);
		if(LIS==null){
			TIMER=setTimeout("GETCONTENT()","500");
		}else{
			clearTimeout(TIMER);
			LIS.attachEvent("onItemClick",DIVCLICK);
			LIS.attachEvent("onItemChecked",DIVCLICK);
			LIS.attachEvent("onItemDblClick",DIVDBCLICK);
			initValue();
			
			//设置层的高和宽
			if(typeof(WIDTH)!="undefined"&&WIDTH!=null&&WIDTH!=''){
			   LIS.setWidth(WIDTH);
			}else{
			   WIDTH=CURRENTINP.clientWidth+18+"";
			   LIS.setWidth(WIDTH*1);
			}
		}
	}	
	
//光标移到层上
function MOVER(){
		ISFOCUSED=true;
	}
	
//光标移出层
function MOUT(){
		ISFOCUSED=false;
		if(CURRENTINP!=null)
		CURRENTINP.focus();
	}

//双击层
function DIVDBCLICK(){
	ISFOCUSED=false;
	hiddenSelectList(); 
}
	
//层单击
function DIVCLICK(){
		var items=null;
		if(MULIT==false||MULIT=="false"){//下拉单选
			items=new Array();
			items[0]=LIS.selectedItem;
		}else{//下拉多选
			items=LIS.checkedItems;
			if (items.length != LIS.items.length) {
				CBX.checked = false;
			}
		}
		var txt="";
		var id="";
		for(var i=0;i<items.length;i++){
			if(txt.length>0){txt+=",";id+=",";}
			txt+=eval("items["+i+"]."+TTXT);
			if(TID!=null&&TID!=undefined)
			id+=eval("items["+i+"]."+TID);
		}
		CURRENTINP.value=txt;
		//if(TID!=null&&TID!=undefined)
		//document.getElementById(CURRENTINP.id.split('_')[0]).value=id;
		VALOBJ.value=id;
	}
	
//根据不同的文本框,显示不同的下拉选项.
function showPosition() {
	 	var obj=CURRENTINP;
		var h = obj.clientHeight + 3;
		var left = 0;
		var top = 0;
		while (obj != document.body) {
			left += obj.offsetLeft;
			top += obj.offsetTop;
			obj = obj.offsetParent;
		}		
		DIV.style.top = (top + h) + "px";
		
		var cha=document.body.clientWidth-left;
		var cha2=cha-(WIDTH*1);
		if(cha2>5)
		 DIV.style.left = left + 1 + "px";
		else
		 DIV.style.left = (left-((WIDTH*1)-CURRENTINP.clientWidth-2)+18) + "px";
		DIV.style.display = "block";
		
		//如果页面下方空间不足
		if ((top + h + DIV.offsetHeight) > document.body.offsetHeight) {
			DIV.style.top = (top - DIV.offsetHeight);
		}
	}

/*
mulit：单选（false）,复选（true）,
url:数据所在的页面URL,
listId:数据列表ID,
txtCol:数,idCol,wid,height
*/
function showSelectList(valObj,mulit,datasrc,url,tlist,ttxt,tid,width,height){
			VALOBJ=document.getElementById(valObj);
			CURRENTINP=event.srcElement.nodeName=="SPAN"?event.srcElement.previousSibling:event.srcElement;
			MULIT=mulit;
			TLIST=tlist;
			TTXT=ttxt;
			TID=tid;
			WIDTH=width;
			HEIGHT=height;
			DATASRC=datasrc;
			URL=url;
			INIT();
			showPosition();
		}	

//隐藏下拉层		
function hiddenSelectList(){
			if (ISFOCUSED) {return;}
			var div =null;
			if(DATASRC==false){
			 	div=document.getElementById(URL);
			}else{
				if (VALOBJ == null) {
					return;
				}
				div = document.getElementById(VALOBJ.id+"Div");
			}
			div.style.display = "none";
			CURRENTINP=null;
			VALOBJ=null;
		}


//选择选项后隐藏下拉层
function hiddenAfterSelect(aDivName)
		{
				if(aDivName)
				{
					div = document.getElementById(aDivName);
				    div.style.display = "none";
				}
		}