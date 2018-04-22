var isLogin = 0;
//有水印才会注册jquery事件
if(waterPic!=""){
  //窗体加载完毕的时候执行
  $(window).load(function(){
    if(top==self&&isLogin==0){
    	showWp();
    }
  });
  //窗体大小改变的时候执行
  $(window).resize(function(){
    if(top==self){
       $("#divWp").remove();
       showWp();
    }
  });
}
  
//创建水印div
function showWp(flg){
     var w=250;
     var h=80;
     var width=(document.body.clientWidth)-w;
     if(flg)width=(document.body.clientWidth);
     var height=(document.body.clientHeight)-h;
     var _html = "<div id='divWp' style='Z-INDEX:999999;text-align:center;position:absolute;width:"+w+"px;height:"+h+"px;"+
	 "top:"+height+";left:"+(width)+";background:none;opacity:0.8;filter:alpha(opacity=60);'>"+
	 "<div id='message' style='opacity:0.8;filter:alpha(opacity=50);font-size:16px;background:none;color:red;font-weight:bold;text-align:left;width:"+w+"px;height:"+h+"px;"+"'>"+waterPic+"</div></div>";
	 $(_html).appendTo("body");
	 $("div#message").mouseover(function(e){
     	if(document.getElementById("divWp").style.left == ((document.body.clientWidth)-w+"px")){
     		showDiv(1);
    	}
     });
     $("div#message").mousemove(function(e){
     	if(document.getElementById("divWp").style.left == ((document.body.clientWidth)-w+"px")){
     		showDiv(1);
    	}
     });
}
//创建水印以外区域div
function showOt(){
     var w=250;
     var h=100;
     var width=(document.body.clientWidth)-w;
     var height=document.body.clientHeight;
     var _html = "<div id='divOt1' style='Z-INDEX:9999999;text-align:center;position:absolute;width:"+width+"px;height:"+height+"px;"+
	 "top:"+0+";left:"+0+";background:white;opacity:0;filter:alpha(opacity=0);'></div>"+
	 "<div id='divOt2' style='Z-INDEX:9999999;text-align:center;position:absolute;width:"+w+"px;height:"+(height-h)+"px;"+
	 "top:"+0+";left:"+width+";background:white;opacity:0;filter:alpha(opacity=0);'></div>";
	 $(_html).appendTo("body");
	 $("div#divOt1").mouseover(function(e){eventOt();});
	 $("div#divOt2").mouseover(function(e){eventOt();});
	 $("div#divOt1").mousemove(function(e){eventOt();});
	 $("div#divOt2").mousemove(function(e){eventOt();});
}

//控制水印显示位置
var ss=null;
var itl="2";
var bForN=100;
var isWorking = false;
var isDone = false;
function showDiv(op){
  if(!isWorking){
  	  isWorking = true;
	  if(op==1){
		bForN=0;  
	    ss= setInterval("moveDivUp()", 5);
	    showOt();
	  }else{
	  	showWp(1);
	  	bForN=0;
	  	itl= setInterval("moveDivDown()", 5);
	  }
  }
}
function moveDivUp(){
   if(bForN>250){
     clearInterval(ss);
     isWorking = false;
     $("#divWp").remove();
     return;
   }
   var width=(document.body.clientWidth)-250+bForN;
   var divWp=document.getElementById("divWp");
   divWp.style.left=width;
   bForN+=5;
   
}

function moveDivDown(){
   if(bForN>=250){
     clearInterval(itl);
     isWorking = false;
   }
   var width=(document.body.clientWidth)-bForN;
   var divWp=document.getElementById("divWp");
   divWp.style.left=width;
   bForN+=5;
}

function setDown(){
	if(!isDone){
		if(isWorking)setTimeout("setDown()",50);
		else{
			showDiv(2);
			isDone = true;
		}
	}
}
function eventOt(){
	 isDone = false;
	 setDown();
	 $("#divOt1").remove();
	 $("#divOt2").remove();
}
//设置是否显示水印
function setLogin(flg){
	isLogin = flg;
}