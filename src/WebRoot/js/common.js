/**************************************************����ϵͳȫ�ֶ���ͷ���**************************************************/
/**��ʾ�����صȴ���*/
WaitBar=function(){
	var bCreate = false;
	var initDiv = 'if(false == bCreate){document.body.appendChild(pDiv);bCreate = true;}';
	var pDiv=document.createElement("DIV"); 
	pDiv.className = 'bar_div';
	pDiv.innerHTML ='<div id="waitbar_icons" class="bar_icons"></div>';
	pDiv.innerHTML+='<div class="bar_font">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;���Ե�...</div>';
	//����������
	pDiv.style.width='230px';
	pDiv.style.zIndex=100001;
	var pgDiv=document.createElement('DIV');
	pDiv.appendChild(pgDiv);
	var pgA=document.createElement('A');
	var pgUrl=document.createElement('span');
	pgDiv.appendChild(pgA);
	pgDiv.appendChild(pgUrl);
	
	//WaitBar������show()������hide()�Լ�������Ϊ0ʱ����WaitBar
	var waitBarCount = 0;//if(flg!=2){waitBarCount++;} if(flg!=2){waitBarCount--;}if(waitBarCount > 0){return;}
	
	return{
	    //waitBarCount : 0,
		show:function(flg){ 
		    if(flg!=2){waitBarCount++;}
		    try{if(window.parent&&window.parent.WaitBar){window.parent.WaitBar.hide(2);}}catch(e){}//�����ǰ������jquery div���������Ƚ�����������أ������ʼ��ʱ����������������
		    eval(initDiv);
		    pDiv.className = 'bar_div';document.getElementById('waitbar_icons').className='bar_icons';//����Ӵ��У���Ȼtabҳ�л�ʱ��������
		    pDiv.style.display = 'block';
		    if(flg){WaitBar.showModal();}
		},//��ʾ�ȴ���
		hide:function(flg){
		   if(flg!=2){waitBarCount--;}
		   //���waitBarCountΪ��������λ
		   if(waitBarCount < 0){waitBarCount = 0;}
		   if(waitBarCount > 0){return;}
		   
		   eval(initDiv);
		   if(flg==2){
		      pDiv.style.display = 'none';
		      WaitBar.hideModal();
		   }else{ 
		      setTimeout(function(){
		       if(waitBarCount > 0){return;} 
		       pDiv.className = ''; document.getElementById('waitbar_icons').className='';//����Ӵ��У���Ȼtabҳ�л�ʱ��������
		       pDiv.style.display = 'none';
		       WaitBar.hideModal();
		      },500);
		   }
		},//���صȴ���
		hideProgress:function(){pgDiv.style.display = 'none';pDiv.style.background = 'transparent';},//���ؽ���
		setProgress:function(progress,url){//���ý���������a��ǩ
			pDiv.className = 'bar_div';document.getElementById('waitbar_icons').className='bar_icons';
			pDiv.style.display = 'block';//Ϊ������;��������̨�����������µȴ���������
			pgA.innerHTML='&nbsp;&nbsp;���ȣ�<font color="green">'+progress+'%</font>&nbsp;';
			pgDiv.style.display = 'block';
			if(url){
				pgUrl.innerHTML='����<a href="'+url+'" onclick="WaitBar.hideProgress();WaitBar.hide();"><font style="font-weight: bold;color:red">�������</font></a>�����ļ�';
			}else if(progress=="99"){
				pgUrl.innerHTML='���ļ������...';
			}
		},
		setMsg:function(progress){//���ý���������a��ǩ
			pDiv.className = 'bar_div';document.getElementById('waitbar_icons').className='bar_icons';
			pDiv.style.display = 'block';//Ϊ������;��������̨�����������µȴ���������
			pgDiv.innerHTML=progress;
			pDiv.style.background = 'white';
			with(pgDiv.style){display = 'block';background = 'white';paddingLeft='10px';}
			WaitBar.showModal();
		},
		showModal:function(){//��������
			 var div=document.getElementById('EXPORT_MODAL_DIV');
			 if(!div){
			 	div=document.createElement('div');
			 	div.id='EXPORT_MODAL_DIV';
			 	document.body.appendChild(div);
				with(div.style){
				 	width=document.body.clientWidth;
				    height=document.body.clientHeight;
				    position='absolute';left='0px';top='0px';zIndex=100000;
				    backgroundColor='black';filter='alpha(opacity='+1+');';
				    opacity=0.01; 
				 }   
			}
			div.style.backgroundColor='black';//����Ӵ��У���Ȼtabҳ�л�ʱ��������
			div.style.display='block';
		},
		hideModal:function(){//��������
			if(document.getElementById('EXPORT_MODAL_DIV')){
				document.getElementById('EXPORT_MODAL_DIV').style.backgroundColor='transparent'; //����Ӵ��У���Ȼtabҳ�л�ʱ��������
				document.getElementById('EXPORT_MODAL_DIV').style.display='none';
			}
		},
		isExporting:function(){
			return (pDiv.style.display == 'block'&&pgDiv.style.display == 'block'&&pgDiv.innerText.indexOf('100%')<0);
		}		
	}
}();

/**�ַ���ת���json����*/
function decode(str) {try{return (typeof(str)!="string")?str:(str.trim()=='')?'':eval('('+str+')');}catch(e){return str;}}

/**��Stringע��һ�������β�ַ��ķ���*/
String.prototype.trim = function() {return this.replace(/^\s+/g,"").replace(/\s+$/g,"");} 

/*��firefox֧��outerHTML*/
if(typeof HTMLElement !== "undefined" && !("outerHTML" in HTMLElement.prototype)) { 
	HTMLElement.prototype.__defineSetter__("outerHTML",function(str){ 
	var fragment = document.createDocumentFragment(); 
	var div = document.createElement("div"); 
	div.innerHTML = str; 
	for(var i=0, n = div.childNodes.length; i<n; i++){ 
	fragment.appendChild(div.childNodes[i]); 
	} 
	this.parentNode.replaceChild(fragment, this); 
	}); 
	// 
	HTMLElement.prototype.__defineGetter__("outerHTML",function(){ 
	var tag = this.tagName; 
	var attributes = this.attributes; 
	var attr = []; 
	//for(var name in attributes){//����ԭ�����ϳ�Ա 
	for(var i=0,n = attributes.length; i<n; i++){//nָ�������Ը��� 
	if(attributes[i].specified){ 
	attr.push(attributes[i].name + '="' + attributes[i].value + '"'); 
	} 
	} 
	return ((!!this.innerHTML) ? 
	'<' + tag + ' ' + attr.join(' ')+'>'+this.innerHTML+'</'+tag+'>' : 
	'<' + tag + ' ' +attr.join(' ')+'/>'); 
	}); 
} 

/**Ϊ��֤ȥ��ext.js��ǰ�ĳ�����ã�ģ��ext��decode����*/
try{if(!Ext.util.JSON.decode)var Ext={util:{JSON:{decode:function(str){return decode(str);}}}};
}catch(e){var Ext={util:{JSON:{decode:function(str){return decode(str);}}}};}

/** ����IE���ڸ߶�*/
function setSize(){
	var h =GetIEVersion();
	var hei=(parseInt(tav.offsetHeight)+h) + "px";
	var w=(parseInt(tav.offsetWidth)+h) + "px";
	window.dialogHeight=hei+"px";
}

/** ��ȡie�汾 Ȼ�󷵻�Ҫ�����ĸ߶�,���ڵ�����С��*/
function GetIEVersion(){
	var ua = navigator.userAgent.toLowerCase();
   	var s= ua.match(/msie ([\d.]+)/)[1]
    var hei=0;
    if(s=='6.0'){
    	hei=55
    }
	return hei;
}

/** �滻ȫ��*/
String.prototype.replaceAll  = function(s1,s2){return this.replace(new RegExp(s1,"gm"),s2);}  

var AllWINDOW=[];
window.document.onkeydown =  onKeyDown;

/**
*����һ��url�õ�����ʱ�����URL
*url����ʱ���,Ϊ�˽����������ʱͬһurlʱ,session����������(�ر����д���������)
*/
function getNewUrl(url){
    if(/.*[\u4e00-\u9fa5]+.*$/.test(url)){
		url=encodeURI(url); //������ʱתһ����
	}
	url+=((url.indexOf("?")>0?"&urlPKIGuid=":"?urlPKIGuid="))+(new Date().getTime());
	return encryptURL(url);
}

/*
** ����: SystemWindow
** ����: url,width,height
** ���: handle
** ����: ����
*/
function SystemWindow(url,width,height,name,resizable,scrollable)
{	
	var msg=checkSessionAlive(url);
    if(msg.indexOf("1") != 0){openMinLoginWin(function(){SystemWindow(url,width,height,name,resizable)},msg);return;}
        
	var left, top;
	if(IsEmpty(width))width=screen.width;//���û����ȣ�������Ļ���
	if(IsEmpty(height))height=screen.height;//���û���߶ȣ�������Ļ�߶�
	if(IsEmpty(resizable)){resizable="yes";}//���û���Ƿ����ô�С����Ĭ�Ͽ���
	if(IsEmpty(scrollable)){scrollable="no";}//�Ƿ��й�������Ĭ����
	left = parseInt((screen.width - width)*0.5);
	top  = parseInt((screen.height - height)*0.5);
	var newWindown= window.open(getNewUrl(url), "_blank", "top="+top+",left="+left+",width="+width+",height="+height+",location=no,scrollbars = "+scrollable+",resizable="+resizable);
	
	//���´�������
	if(name)newWindown.name=name;
	//��ǰ���ڱ����Ӵ���
	AllWINDOW.push(newWindown);
	//���������Ӵ���
	var parent=window;
	while(true){
	    try{
		  if(parent.opener.AllWINDOW){parent.opener.AllWINDOW.push(newWindown);}
		  parent=parent.opener;
		}catch(e){
			break;
		}
	}	
	//���ص�ǰ����
	return newWindown;
}

//��һ���´���,�����ָ�����,���ȫ����,��ȫ����SystemWindow���棬���Բ�����ʹ�ã�֮���Ա�����Ϊ�˼�������ϵͳ
function WindowOpen(url,width,height,name){
    var msg=checkSessionAlive(url);
	if(msg.indexOf("1") != 0){openMinLoginWin(function(){WindowOpen(url,width,height,name)},msg);return;}
    var left, top,width,height;
    width=width||window.screen.availWidth;
    height=height||window.screen.availHeight;
    left = (window.screen.availWidth-width)/2;
    top  = (window.screen.availHeight-height)/2;
    var newWindown = window.open(getNewUrl(url), "_blank", "top="+top+",left="+left+",width="+width+",height="+height+",location=no,resizable=yes,scrollbars=yes");
	newWindown.resizeTo(screen.availWidth,screen.availHeight); 
	//���´�������
	if(name)newWindown.name=name;
	//��ǰ���ڱ����Ӵ���
	AllWINDOW.push(newWindown);
	//���������Ӵ���
	var parent=window;
	while(parent.opener){
		if(parent.opener.AllWINDOW){parent.opener.AllWINDOW.push(newWindown);}
		parent=parent.opener;
	}		
	//���ص�ǰ����
	return newWindown;
}

//resizeΪno����ʾ�������,,��ȫ����SystemWindow���棬���Բ�����ʹ�ã�֮���Ա�����Ϊ�˼�������ϵͳ
function WindowOpenDetail(url,width,height,name,resize){
    var msg=checkSessionAlive(url);
	if(msg.indexOf("1") != 0){openMinLoginWin(function(){WindowOpenDetail(url,width,height,name,resize)},msg);return;}
    var left, top,width,height;
    width=width||window.screen.availWidth;
    height=height||window.screen.availHeight;
    left = (window.screen.availWidth-width)/2;
    top  = (window.screen.availHeight-height)/2;
    var newWindown = window.open(getNewUrl(url), "_blank", "top="+top+",left="+left+",width="+width+",height="+height+",location=no,resizable="+resize+",scrollbars=yes");
	//newWindown.resizeTo(screen.availWidth,screen.availHeight); 
	//���´�������
	if(name)newWindown.name=name;
	//��ǰ���ڱ����Ӵ���
	AllWINDOW.push(newWindown);
	//���������Ӵ���
	var parent=window;
	while(parent.opener){
		if(parent.opener.AllWINDOW){parent.opener.AllWINDOW.push(newWindown);}
		parent=parent.opener;
	}		
	//���ص�ǰ����
	return newWindown;
}

/**
*�ر������Ӵ���
*���عص����ڵ�����
*/
function SystemCloseWin(){
	var cnt=0;
	for(var i=AllWINDOW.length-1;i>=0;i--){
		if(!IsEmpty(AllWINDOW[i])&&!AllWINDOW[i].closed){
			AllWINDOW[i].close();
			cnt++;
		}
	}
	return cnt;
}

/**
����رշ���,��window������juquery��
closeFlag:�������н��ͣ�����ͨ�ñ��ڲ�
beforeCloseFun:�ش�ǰ�¼�����������ֵΪfalse�Ļ������������ش�����
*/
function SysCloseJqueryWin(closeFlag,beforeCloseFun){
    if(beforeCloseFun&&typeof(beforeCloseFun)=="function"){
    	var boo=beforeCloseFun(closeFlag);
    	if(!boo)return;
    }
	if(closeFlag+""=="1"||closeFlag+""=="2"){//ͨ�ñ���Ϊ�������飬��������ʱ,Ҫ��Ƕ��ҳ������iframeֻ��ֱ��Ƕ����east���ֿ���
		try{
			if(window.frameElement&&window.parent.$(".easyui-layout").html()&&window.parent.$(".easyui-layout").layout('panel','east')){
				window.parent.$(".easyui-layout").layout('collapse','east');
				document.body.style.display="none";
				return;
			}
		}catch(e){}
	}
	if(closeFlag+""=="3"){//ͨ�ñ���Ϊ�����޸Ľ���ʱ
		//if(window.initUpdatePage)window.initUpdatePage();
		window.location.href=window.location.href.replace("update","singleLine");
		return;
	}
	try{
	if(window.frameElement){//��ǰ�������ڵ�iframe
		try{
			if(window.parent.$(window.frameElement.parentNode.parentNode).hasClass("window")){//��Ϊjquery����windowʱ
				try{
					window.parent.$(window.frameElement.parentNode).window("close");//�������ڸ������
				}catch(e){
					try{getTopWin(window).$(window.frameElement.parentNode).window("close");}catch(e){}//����Ӷ��������
				}
			}else{//��ͨ��iframe��Ƕ
				getTopWin(window).SystemCloseWin();
				if(!getTopWin(window).closed)getTopWin(window).close();
			}
		}catch(e){
			SystemCloseWin();
			if(!window.closed){window.close();}
		}
	}else{
	    //��ǰ���岻�Ǳ�iframeǶ��
		SystemCloseWin();
		if(!window.closed)window.close();
	}
	}catch(e){
	 	 window.close();
	}
}

/*
** ����: SystemModalDialog
** url,width,height,resizable
** dailogArgs:�����Ӵ���Ĳ��������Ӵ�����ͨ��window.dialogArguments��õ�
** ���: handle
** ����: ����ģʽ����
*/
function SystemModalDialog(url,width,height,dailogArgs,resizable)
{
	if(IsEmpty(width))width=screen.width;//���û����ȣ�������Ļ���
	if(IsEmpty(height))height=screen.height;//���û���߶ȣ�������Ļ�߶�
	if(IsEmpty(resizable))resizable='yes';
    var msg=checkSessionAlive(url);
	if(msg.indexOf("1") != 0){openMinLoginWin(function(){SystemModalDialog(url,width,height,dailogArgs,resizable)},msg);return;}
	return window.showModalDialog(getNewUrl(url), dailogArgs, "dialogWidth:" + width + "px;dialogHeight:" + height + "px;help:no;minimize:no;maximize:"+resizable+";resizable:"+resizable+";scroll:no;status:no");
}

/**
*ajax���session�Ƿ����,0��ʾ�Ѿ�����
*ͬ�����ƣ�������Щ�����������к�ߴ���
*ǰ�������ҳ��Ҫ����jquery js
*��Ϊ�����в˵��򷵻� '2:�˵�Ȩ��id'
*/
function checkSessionAlive(url){
	 var isCheckSession = getSystemParams("isCheckSession","1");
	 if(isCheckSession=="0" || isCheckSession.toLowerCase() == "off"){//ϵͳû���ð�ȫ��������Ҫ���session
	 	return "1";
	 }
	 var msg="0"
	 var pathname=getPathName();
	 var path=getFullPath();
	 var param = new Object();
	 if(url){
		if(url.indexOf(".") == 0){
			var fullUrl = window.document.location.pathname;
			fullUrl = fullUrl.replace(pathname, "");
			var sps = fullUrl.split("/");
			fullUrl = '';
			var l = sps.length - 1;
			var s = 1;
			if(url.indexOf("..") == 0){
				l = sps.length - 2;
				s = 2;
			}
			for(var i = 0; i < l; i++){
				if(sps[i] == ''){continue;}
				fullUrl += ('/' + sps[i]);
			}		
			url = pathname + fullUrl + url.substring(s); 
		}
	 	param.url = url;
	 }
       //IT֧�������·�ʽ���У�ͳһ���±߷�ʽ
       msg = $.ajax({
            url: path + "/safeMgr/login!checkSession.action",
            data:encryptParams(param),
            type: 'post',
            async: false      //ajaxͬ��
        }).responseText;
     return msg;
}

/**
*�������¼��
*ǰ�������ҳ��Ҫ����jquery js
*/
function openMinLoginWin(fn, msg, opts){
	
	var hasInterface = (typeof(commonInterface) != "undefined");
	var hasSessionTimeoutInterface = (hasInterface && commonInterface.whenSessionTimeout);
	var isSensit = (msg && msg.indexOf("2") == 0) ? true : false; 
	var hasSensitInterface = (hasInterface && commonInterface.whenIsSensit);
	
	if(isSensit && hasSensitInterface){
		commonInterface.whenIsSensit(fn, msg, opts);
	}else if(!isSensit && hasSessionTimeoutInterface){
		commonInterface.whenSessionTimeout(fn, opts);
	}else{
		ssh3_openMinLoginWin(fn, msg);
	}
}

/**
SSH3����������¼�����в˵���¼Ч��
*/
function ssh3_openMinLoginWin(fn, msg){
	var obj=getTopWin(window)==null?window:getTopWin(window);
	var pathname=getPathName();
	var path=getFullPath();
	var temp=getTopWin(window).document.getElementsByTagName("frameset");
	var isSensit = (msg && msg.indexOf("2") == 0)?true:false;//�Ƿ����в˵���¼��֤����
	var menuId = "";
	if(temp&&temp.length>0){//���obj��һ��frameset���ҳ��
		var url = path+'/login1.jsp?isFrameSet=1';
		if(isSensit){
			menuId = msg.split(":")[1];
			url += '&sensitive=1&menuId='+menuId;
		}
		obj.location = url;
	}else{//obj�Ǹ���ͨҳ��
		var options={
			title:"�Ự����,�����µ�¼",width:340,	height:230,
			modal:true,resizable:false,collapsible:false,
	 		minimizable:false,maximizable:false,closable:false
	 		,baseWin:obj,url:path+'/minlogin1.jsp',id:"login_Win"
		};
		//�̶��Ļص�����myAppFunction:�����Լ��Ĵ����ﶨ��һ�������Ƶĺ������������¼���ر�ʱ���ͻ���ô˺���
		options.onClose=function(){
			//�ж��Ƿ��ֶ��ر������¼��
			if(typeof(window.top.MinLoginSuc)!='undefined' && window.top.MinLoginSuc){
				if(window.myAppFunction){window.myAppFunction();}
				if(fn){fn.call(this);}
			}
		}
		options.checkLogin=false;
		if(isSensit){
			menuId = msg.split(":")[1];
			options.title="�����ڷ����������ݣ�������֤";
			options.url = options.url + '?sensitive=1&menuId='+menuId;
			options.closable=true;
		}
		showJqueryWindow(options);
	}
}

/*
Jquery window����,����������Զ����
����:
obj��һ��json����Ӧ�ð���jquery window��Ҫ�Ļ�������(�ο� jquery window API),
obj������������������ԣ��������⴦��
	id:ָ��id��html����ת��Ϊjquery window�����ָ��id�����������ڣ��򴴽�һ���µ�div�������Ա�����
	baseWin:�������������ָ������Ļ����Ͽ����������˲�����Ĭ���ڵ�ǰ����򿪣�������ָ�������ϴ�
	url:�����url�������ָ��id�Ķ����ڣ�����һ��iframe��iframe��src���Ǵ�url
	refresh:�Ƿ���Ҫǿ��ˢ�£�Ĭ��ˢ�� update by Zhanweibin 2012-03-07
*/
function showJqueryWindow(obj){
	obj.minimizable=false;//������С����ť����Ϊ��С���͸��رմ���һ��
	obj.baseWin=(obj.baseWin)?obj.baseWin:window; //
	
	/**������ͨ�ó����������������*/
	var windowTop=getTopWin(obj.baseWin);
    if(obj.baseWin==windowTop){windowTop.basewin=this.window;}
	
	if(IsEmpty(obj.refresh))obj.refresh=true;
	
	//���㿪�������߶�
    var o = commonUtil.computerWH(obj.width, obj.height,obj.baseWin);
    obj.width=o.w; obj.height=o.h;
    //���þ���
    obj.left=($(obj.baseWin).width()-obj.width)*0.5;
    obj.top=($(obj.baseWin).height()-obj.height)*0.5;
    
	var div=obj.baseWin.$("#"+obj.id);
	if(div.length>0)div=div[0];else div=null;
	if(!div){//����DIV
		div=obj.baseWin.document.createElement("div");
		div.id=obj.id;
		div.style.overflow="hidden";
		obj.baseWin.document.body.appendChild(div);
	}
	var waitbarFlag=false;
	if(obj.url){//����iframe
		obj.url = encryptURL(obj.url);
		var iframe=obj.baseWin.$("#"+obj.id+">iframe");
		if(!(obj.refresh==false&&iframe[0]&&$(iframe[0]).attr("src"))){//���󴰿ڵ�¼ҳ��ʱ������ִ˳���
			if(iframe[0]){
				$(iframe).hide();
				$(iframe[0]).remove();
			}
			obj.baseWin.WaitBar.show(2);
			var html='<iframe scrolling="no" frameborder="0"  src="'+obj.url+'" style="width:100%;height:100%;overflow:hidden;border:0px;"></iframe>';
			$(div).append(html);
			waitbarFlag=true;
		}else waitbarFlag=false;
	}
	
	obj.collapsible=false;
	obj.baseWin.$(div).window(obj);
	if(waitbarFlag){
		obj.baseWin.$("#"+obj.id+">iframe").bind("load",function(){obj.baseWin.WaitBar.hide(2);})
		var theOpenedWin = obj.baseWin.document.getElementById(obj.id).getElementsByTagName("iframe")[0];//obj.baseWin.$("#"+obj.id+">iframe").contents();
		if(theOpenedWin){theOpenedWin=theOpenedWin.contentWindow;if(theOpenedWin)theOpenedWin.jqOpener = window;}
	}
}

/**
*��Ϣ��ʾ��ͳһ���
*�˷����е�һ������flg��������ȫ�ֿ��ƣ�true��������������Դ���Ϣ����false��jquery��Ϣ��
*����˵��:
*message������һ��Ϣ�ַ�������������£�ֻ����alert()��ʽ��ʾ
*messageҲ�����Ƕ��󣬰�����������:
*   title����Ϣ������,Ĭ��ֵ"��ʾ"
*   icon����Ϣ����ʾͼʾ����,���õ�ֵ�ǣ� error��question��info��warning��Ĭ��ֵ��"info"
*   ok��ȷ����ť���ı�,Ĭ����"ȷ��"
*   cancel��ȡ����ť���ı�,Ĭ����"ȡ��"
*	opts:���������������ʾ���width,��opts:{width:400} ������ô��ڿ��Ӵ���Ŀ�ȣ�������Ϊ"auto";����������Ϊ300
*(�������Խ���jquery������Ч)
*   msg����ʾ���ݣ����������Ҫ�黻�У�����"\n"
*   type����Ϣ�����ͣ�1��'alert',2��'confirm',3��prompt��Ĭ����1��alert��
*   fn���ص�����������alert����ֱ�ӵ��ã�����confirm,���fn���true��ȷ������false��ȡ����������prompt�����fn�������д���ı���
*   fnCancel�������confirm��Ϣ��,��ȡ����ťʱ�Ļص�����
*/
function simpleAlert(message){
     if(!message)return;
     var flg=false;
     if(typeof(simpleAlert_flg)!="undefined"&&simpleAlert_flg!=null)flg=simpleAlert_flg;//ĳҳ��������simpleAlert_flg���ԣ����������ҳ�浥������
     if(GetBrowserType().indexOf("IE 6")>=0)flg=true;//IE6һ����������Դ��ģ���Ϊ�������select����ʾЧ���ᱻ��͸
     var options={};
     if(typeof(message)=="object"){
     	options=message;
     	options.title=options.title?options.title:"��ʾ";
     	options.icon=options.icon?options.icon:"info";
     	options.msg =flg?options.msg:'<div style="margin:10px 0 0 20px;*margin:10px 0 0 65px;font-faimly:����;font-size:12px;text-align:left;word-wrap:break-word;word-break:break-all;">'+options.msg.replace(/\n/g,"<br>")+'</div>';
     	//�ж��Ƿ��趨��width
		if(IsEmpty(options.opts)){options.opts={};options.opts.width=300;}
     	if(!IsEmpty(options.opts) && options.opts.width > $(window).width()){options.opts.width=$(window).width()-30;}
     }else if(typeof(message)=="string"){
        options.title="��ʾ";
        options.icon="info";
     	options.msg =flg?message:'<div style="margin:10px 0 0 20px;*margin:10px 0 0 65px; font-faimly:����;font-size:12px;text-align:left;">'+message.replace(/\n/g,"<br>")+'</div>';
     }
     //���confirm,��ûע��ȡ����ť���������һ���շ���,
    if(!options.fnCancel&&(options.type=="confirm"||"2"==options.type+"")){options.fnCancel=function(){}}
    
    if(flg){//�����Ĭ����ʾ��
        options.msg=options.msg.replace(/<br>/g,"\n");
    	if(options.type&&(options.type=="confirm"||"2"==options.type+"")){
    		var boo=window.confirm(options.msg);
    		if(options.fn){if(boo)options.fn(boo);else options.fnCancel(boo);}
	    }else if(options.type&&(options.type=="prompt"||"3"==options.type+"")){
	        var txt=window.prompt(options.msg);
	        if(options.fn){options.fn(txt);}
	    }else{
	    	window.alert(options.msg);
    		if(options.fn){options.fn();}
	   	}
    }else{//jquery��ʾ��
        options.msg=options.msg.replace(/\n/g,"<br>").replace(/\r/g,"<br>");
	    $.messager.defaults.ok=options.ok?options.ok:'ȷ��';
	    $.messager.defaults.cancel=options.cancel?options.cancel:"ȡ��";
	    if(options.type&&(options.type=="confirm"||"2"==options.type+""))
	    	$.messager.confirm(options.title, options.msg, function(r){if(r)options.fn(r);else options.fnCancel(r);}, options.opts);
	    else if(options.type&&(options.type=="prompt"||"3"==options.type+""))
	    	$.messager.prompt(options.title, options.msg, function(r){options.fn(r);}, options.opts);
	    else
	   		$.messager.alert(options.title,options.msg,options.icon,options.fn, options.opts);
	   	//��ťע������¼�
	   	var btns = $(".messager-button .l-btn");
	   	if (btns.length != 0) {
	   		btns.eq(0).focus();
	   		window.messagerBtnIndex = 0;
	   		btns.bind('keydown', function(event){
	   			if (event.keyCode == '37' && window.messagerBtnIndex - 1 >= 0) {
	   				window.messagerBtnIndex--;
	   				btns.eq(window.messagerBtnIndex).focus();
	   			} else if (event.keyCode == '39' && window.messagerBtnIndex + 1 < btns.length) {
	   				window.messagerBtnIndex++;
	   				btns.eq(window.messagerBtnIndex).focus();
	   			}
	   			return true;
	   		});
	   	}
   	}
}

/*
** ����: SystemIsEmpty
** ����: str
** ���: bool
** ����: ��֤�ַ����Ƿ�Ϊ��
*/
function SystemIsEmpty(str)
{
	var regExp = /^\s*$/;
	if (str == null) return true;
	if (regExp.test(str)) return true;
	return false;
}

/*
**�������δ���壬����Ϊ�գ������ǿ��ַ����������棬���򷵻ؼ�
*/
function IsEmpty(obj){
	if(typeof(obj)=="undefined"||obj==null||(typeof(obj)!="object"&&(obj+"").replace(/ /g,"")=="")){//||obj.length==0
		return true;
	}
	return false;
}

/*
** ����: SystemTrim
** ����: str
** ���: string
** ����: ȥ���ַ�����β�ո�
*/
function SystemTrim(str)
{
	var regExp = /(^\s*)|(\s*$)/;
	return str.replace(regExp,"");
}

/**
**��ȡ�����������汾
**/
function GetBrowserType(){
	var agent = navigator.userAgent.toLowerCase() ;
    var browserType = "";
    
    var regStr_ie = /msie [\d.]+;/gi ;
    var regStr_ff = /firefox\/[\d]+/gi
    var regStr_chrome = /chrome\/[\d]+/gi ;
    var regStr_saf = /safari\/[\d]+/gi ;
	  //IE<10
	 if(agent.indexOf("msie") > 0)
	 {
	    browserType = agent.match(regStr_ie)+"";
	    browserType = browserType.replace("msie","IE");
	    browserType = browserType.replace(";","");
	 }
	 //IE11
	 if(("ActiveXObject" in window) && agent.indexOf("rv:11")>-1){
	 	regStr_ie = /rv:[\d.]+\)/gi ;
	 	browserType = agent.match(regStr_ie)+"";
	 	browserType = browserType.replace("rv:","IE");
	 	browserType = browserType.replace(")","");
	 }
	 //firefox
	 if(agent.indexOf("firefox") > 0)
	 {
	    browserType = agent.match(regStr_ff)+"";
	    browserType = browserType.replace("firefox","FIREFOX");
	    browserType = browserType.replace("/"," ");
	 }
	 //Chrome
	 if(agent.indexOf("chrome") > 0)
	 {
	    browserType = agent.match(regStr_chrome)+"";
	    browserType = browserType.replace("chrome","CHROME");
	    browserType = browserType.replace("/"," ");
	 }
	 //Safari
	 if(agent.indexOf("safari") > 0 && agent.indexOf("chrome") < 0)
	 {
	    browserType = agent.match(regStr_saf) ;
	    browserType = browserType.replace("safari","SAFARI");
	    browserType = browserType.replace("/"," ");
	 }
	 return browserType;
}

/**
**���һ������ľ���λ��
**/ 
function position(obj){
	var left = 0;
	var top = 0;
	while (obj != document.body) {
		left += obj.offsetLeft;
		top += obj.offsetTop;
		obj = obj.offsetParent;
	}
	return {x:left,y:top};
}

/**
**formת��json,�����ڼ򵥱�ת��
**/
function form2Json(formId)
{
	var form = document.getElementById(formId);
	var count = form.elements.length;
	var json = {};
	for(var i = 0;i < count;i++){
		var ele = form.elements[i];
		if(ele == null) continue;
		var tagName = ele.tagName;
		var name = ele.name;
		if(name == null || name == '') continue;
		var value = ele.value;
		eval("json."+name+"='"+value+"'");	
	}
	return json;
}

/*
** ����: SystemCancelEvent
** ����: void
** ���: bool
** ����: ���ؼ�
*/
function SystemCancelEvent()
{
	return false;
}

/*
�����������ȡevent����
author:tanjianwen
*/
function getEvent(){
	if (typeof event == "undefined") {//firefox
		return getCallerEvent(getEvent);
	} else {//IE
		return event;
	}
}

function getCallerEvent(obj) {
	for (var i = 0;i < obj.caller.arguments.length;i++) {
		if (obj.caller.arguments[i] == Event) 
			continue;
		return obj.caller.arguments[i];
	}
	return getCallerEvent(obj.caller);
}

/*
**����ϼ�CTRL+Nʱ
*/
function SystemKillPopupWin()
{
	var evt = getEvent();
	if (evt.ctrlKey && evt.keyCode == 78) evt.returnValue = SystemCancelEvent();
}
//��������ӣ���Ԫ��Ϊreadonlyʱ����backspace����ת��ǰһ��ҳ�棬�⺯���������ε�
function keyDown(){
	var evt = getEvent();
	var target = evt.srcElement ? evt.srcElement : evt.target;
	if(evt.keyCode==8 && target.readOnly==true){//backspace��KEYCODE��ֵ��8��ͨ���������Կ��ơ�
		evt.returnValue= false;
	}
}

function onKeyDown(){
	SystemKillPopupWin();
	keyDown();
}

function ToExcel()
{
	var sPostContent = ContainId.innerHTML;
	var sTmp  = ContainId.innerHTML;
	var reg1 = /<a (.*)>(.*)<\/a>/gi;
	var reg2 = /width="(.*)"/gi;
	var reg4 = /<img (.*?)src=\\?\"(.*?)\"(.*?)>/gi;
	var reg5 = /(\<script.*\>)(.[^\[]*?)(\<\/script\>)/ig;
	sPostContent=sPostContent.replace(reg4,"<img $1 src=$2 $3>");
	sPostContent=sPostContent.replace(reg5, "");
	sPostContent = sPostContent.replace(reg1, "$2");
	sPostContent = sPostContent.replace(reg2, "width=$1");
	ContainId.innerHTML = sPostContent;
	try{
		var oXL = new ActiveXObject("Excel.Application");
		var oWB = oXL.Workbooks.Add();
		var oSheet = oWB.ActiveSheet; 
		var sel=document.body.createTextRange();
		sel.moveToElementText(ContainId);
		sel.select();
		sel.execCommand("Copy");
		oSheet.Paste();
		oXL.Visible = true;
	}catch(e){}
	ContainId.innerHTML = sTmp;
}
     
function ToWord()
{
	var sPostContent = ContainId.innerHTML;
	var sTmp  = ContainId.innerHTML;
	var reg1 = /<a (.*)>(.*)<\/a>/gi;
	var reg2 = /width="(.*)"/gi;
	var reg4 = /<img (.*?)src=\\?\"(.*?)\"(.*?)>/gi;
	var reg5 = /(\<script.*\>)(.[^\[]*?)(\<\/script\>)/ig;
	sPostContent=sPostContent.replace(reg4,"<img $1 src=$2 $3>");
	sPostContent=sPostContent.replace(reg5, "");
	sPostContent = sPostContent.replace(reg1, "$2");
	sPostContent = sPostContent.replace(reg2, "width=$1");
	ContainId.innerHTML = sPostContent;
	try{
		var oWD = new ActiveXObject("Word.Application");
		var oDC = oWD.Documents.Add("",0,1);
		var oRange =oDC.Range(0,1);
		var sel = document.body.createTextRange();
		sel.moveToElementText(ContainId);
		sel.select();
		sel.execCommand("Copy");
		oRange.Paste();
		oWD.Application.Visible = true;
	}catch(e){}
	ContainId.innerHTML = sTmp;
}

(function($){
    $.toJSON=function(o){
        if(typeof(JSON)=='object'&&JSON.stringify)return JSON.stringify(o);
        var type=typeof(o);
        if(o===null)return"null";
        if(type=="undefined")return undefined;
        if(type=="number"||type=="boolean")return o+"";
        if(type=="string")return $.quoteString(o);
        if(type=='object'){
            if(typeof o.toJSON=="function")return $.toJSON(o.toJSON());
            if(o.constructor===Date){
                var month=o.getUTCMonth()+1;
                if(month<10)month='0'+month;
                var day=o.getUTCDate();
                if(day<10)day='0'+day;
                var year=o.getUTCFullYear();
                var hours=o.getUTCHours();
                if(hours<10)hours='0'+hours;
                var minutes=o.getUTCMinutes();
                if(minutes<10)minutes='0'+minutes;
                var seconds=o.getUTCSeconds();
                if(seconds<10)seconds='0'+seconds;
                var milli=o.getUTCMilliseconds();
                if(milli<100)milli='0'+milli;
                if(milli<10)milli='0'+milli;
                return'"'+year+'-'+month+'-'+day+'T'+hours+':'+minutes+':'+seconds+'.'+milli+'Z"';
            }
            //if(o.constructor===Array){
            if(o.constructor.toString().indexOf("Array")>0){
                var ret=[];
                for(var i=0;i<o.length;i++)
                    ret.push($.toJSON(o[i])||"null");
                return ("["+ret.join(",")+"]").replaceAll(":undefined",":null");
            }
            var pairs=[];
            for(var k in o){
                var name;
                var type=typeof k;
                if(type=="number")name='"'+k+'"';
                else if(type=="string")name=$.quoteString(k);
                else continue;
                if(typeof o[k]=="function")continue;
                var val=$.toJSON(o[k]);
                pairs.push(name+":"+val);
            }
            return ("{"+pairs.join(", ")+"}").replaceAll(":undefined",":null");
        }
    };
    $.evalJSON=function(src){
        if(typeof(JSON)=='object'&&JSON.parse)return JSON.parse(src);
        return eval("("+src+")");
    };
    $.secureEvalJSON=function(src){
        if(typeof(JSON)=='object'&&JSON.parse)return JSON.parse(src);
        var filtered=src;
        filtered=filtered.replace(/\\["\\\/bfnrtu]/g,'@');
        filtered=filtered.replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,']');
        filtered=filtered.replace(/(?:^|:|,)(?:\s*\[)+/g,'');
        if(/^[\],:{}\s]*$/.test(filtered))return eval("("+src+")");
        else throw new SyntaxError("Error parsing JSON, source is not valid.");
    };
    $.quoteString=function(string){
        if(string.match(_escapeable)){
            return'"'+string.replace(_escapeable,function(a){
                var c=_meta[a];
                if(typeof c==='string')return c;
                c=a.charCodeAt();
                return'\\u00'+Math.floor(c/16).toString(16)+(c%16).toString(16);})+'"';
        }
        return'"'+string+'"';
    };
    var _escapeable=/["\\\x00-\x1f\x7f-\x9f]/g;
    var _meta={'\b':'\\b','\t':'\\t','\n':'\\n','\f':'\\f','\r':'\\r','"':'\\"','\\':'\\\\'};
})(jQuery);

/**
**����
**URL : ����ʱ��ȡ����·��
**headConfig : ��ͷ���ø�ʽ[{name:'',text:'',width:''},{...}]
**queryForm : ��ѯ��ID
**contextPath : contextpath
**/
function exportList(url,headConfig,queryForm,contextPath)
{	
	var form = {};
	if(typeof(queryForm) === 'string')
		form = form2Json(queryForm);
	else if(typeof(queryForm) === 'object')
		form = queryForm;
	else return;

	form.pageNo = 1;
	form.limit = 1000;
	var data = [];
	var returnHtml = "";
	if(!confirm("��ʾ��һ�����ֻ�ܵ���1000����¼�������밴��ȷ������"))return;
	AjaxRequest.doRequest(null, url,form, function(backData){
		backData = backData.replace(/<p>/gi,'');
		backData = backData.replace(/<\\\/p>/gi,'');
		backData = backData.replace(/<br \/>/gi,'');
		data = Ext.util.JSON.decode(backData);
		transEnum(data);
		var tableHtml = "<table border=1 style=\"font-size:14px\"><tr style=\"font-weight:600;height:25px\">";
		for(var i = 0;i < headConfig.length;i++){
			config = headConfig[i];
			var text = config.text;
			var width = config.width;
			tableHtml += "<td width='"+width+"' style='background-color:#0099FF;'>";
			tableHtml += text;
			tableHtml += "</td>";
		}
		tableHtml += "</tr>";
		for(var i = 0;i < data.length;i++){
			var row = "<tr>";
			for(var j = 0;j < headConfig.length;j++){
				row += "<td>";
				var name = headConfig[j].name;
				var content = eval("data[i]."+name);
				if(content=="null"||content==null)content="";
				row += "&nbsp;"+content;
				row += "</td>";
			}
			row += "</tr>";
			tableHtml += row;
		}
		tableHtml += "</table>";
		document.getElementById('sContent').value = tableHtml;
		document.getElementById('excelform').submit();
	});
	return returnHtml;
}

//����ö��ֵ
function transEnum(jsonData){
	for(var i = 0; i < jsonData.length; i++){
		traverseTree(jsonData[i]);	
	}
}

//�ݹ����Ȩ����
function traverseTree(tree) { 
	transValue(tree,'logType','iLogType');
	transValue(tree,'rightAppFor','iAppFor');
    var children = tree.children;   
    if (children != null) {   
        for(var n = 0; n < children.length; n++){
        	traverseTree(children[n]);
        }
    }  
}  

function transValue(tree,name,id)
{
	EnumRequest.getEnum(name, function(data){
	 		if(data == null) return;
		for(var j = 0; j < data.length; j++){
			if(data[j].id == tree[id]){
				tree[id] = data[j].text;
				break;
			}
		}
	});
}

//�ж��ǲ����������ַ�
function isInvalidChar(str){
	var aInvalidChar = ["<", ">","'","\""];
	var aInvalidChar_SIZE = aInvalidChar.length;
	
	var len = str.length;
	var isHas = false;
	for(var i = 0 ; i < aInvalidChar_SIZE ; i ++)
	{
		for(var t = 0 ; t < len ; t ++)
		{
			var sechar = str.charAt(t);
			if(sechar == aInvalidChar[i])
			{
				isHas = true;
				break;
			}
		}
	}
	return isHas;
}

/**
ͳһ��ȡɾ�����޸������
author:gaotao 2011-12-19
ԭ��:1����ɾ���У�ֻ�Թ�ѡ����Ϊ׼;2�����޸��У�����й�ѡ���У������޸�;����й�ѡ���У��Թ�ѡ����Ϊ׼�����û�й�ѡ�κ��У�����ѡΪ׼��
���:obj�Ǹ����������,��������:
    listType:�б�����,1��"htc"��htc�б�2��"flex"��flex�б�3��"jquery":jquery�б�(Ĭ��)��
    optType:�������ͣ�1��"delete"����ȡҪɾ�����У�2��"update"����ȡҪ�޸ĵ���(Ĭ��)
    listId���б�ID�����flex�б���Ӵ������Ǿ����б����(�����Ա�����)
    keyCol����������
    fn:�ص�����
����ֵ��
    ����д���keyCol����,�򷵻�ID�ַ����������","�����ӣ�
    ���δ����keyCol���ԣ�ɾ��������������json��������޸Ĳ�����������json����
    ����޸�ʱ���й�ѡ���У�����"notOnly"��
    û��Ҫɾ�����޸ĵ��У�����null��
*/
function getDelOrUpdItems(obj){
	try{
		var listObj=null;
		var tempItems=null;
		var item=null;
		var returnValue=null;
		if(obj.optType==1||obj.optType=="delete"){//��ȡɾ���� 
			if(obj.listType==1||obj.listType=="htc"){
				listObj=(typeof(obj.listId)=="string")?document.getElementById(obj.listId):obj.listId;
				if(listObj)tempItems=listObj.checkedItems;
			}else if(obj.listType==2||obj.listType=="flex"){
				listObj=obj.listId;
				if(listObj)tempItems=listObj.checkedItems();
			}else{
				listObj=(typeof(obj.listId)=="string")?$('#'+obj.listId):obj.listId;
				if(listObj)tempItems=$(listObj).datagrid("getSelections");
			}
		}else{//��ȡҪ�޸���
			if(obj.listType==1||obj.listType=="htc"){
				listObj=(typeof(obj.listId)=="string")?document.getElementById(obj.listId):obj.listId;
				if(listObj){tempItems=listObj.checkedItems;item=listObj.selectedItem;}
			}else if(obj.listType==2||obj.listType=="flex"){
				listObj=obj.listId;
				if(listObj){tempItems=listObj.checkedItems();item=listObj.getSelectedItem();}
			}else{
				listObj=(typeof(obj.listId)=="string")?$('#'+obj.listId):obj.listId;
				if(listObj){tempItems=$(listObj).datagrid("getSelections");item=$(listObj).datagrid("getSelected");}
			}
			if(tempItems){
				if(tempItems.length>1){//����й�ѡ����
					tempItems=null;returnValue="notOnly";
				}else if(tempItems.length==0&&item){//û�й�ѡ�У�������ѡ��������ѡΪ׼
					tempItems.push(item);
				}
			}
		}
		//����id�������Ƕ�������
		if(tempItems&&tempItems.length>0){
			if(obj.keyCol){//����id��
				returnValue="";
				for(var i=0;i<tempItems.length;i++){
					if(returnValue.length>0)returnValue+=",";
					returnValue+=eval("tempItems["+i+"]."+obj.keyCol);
				}
			}else{//���ض�������
				if(obj.optType==2||obj.optType=="update"){returnValue=tempItems[0];}
				else returnValue=tempItems;
			}
		}		
		//����лص�����
		if(obj.fn){obj.fn.call(this,returnValue);}
		return returnValue;
	}catch(e){
		simpleAlert("ȡ�޸��л�ɾ���г���,��鿴js/common.js���getItemRecords����!");
		return null;
	}
}

//ϵͳ���� tanjianwen
function system_Lock() {
	var firstWin = getFirstWindow(window);
	var myWins = [getTopWin(window)];
	//�����ǰϵͳ��open������Ҫ�ſ��±����
	if(firstWin.AllWINDOW)for(var i=0;i<firstWin.AllWINDOW.length;i++){myWins.push(firstWin.AllWINDOW[i]);}
	for (var i = 0;i < myWins.length; i++) {
		var w = myWins[i];
		showJqueryWindow({id:'systemLockDiv', baseWin:w, url:path + '/systemLock.jsp', 
					  title: 'ϵͳ����', minimizable: false, maximizable: false, resizable: false,
					  closable: false, collapsible: false, width: 380, height: 150, modal: true,
					  onOpen: function(){
					  	if ($("#systemLockDiv iframe")[0])$("#systemLockDiv iframe")[0].src = path + '/systemLock.jsp';
					  }
					  });
	}
}

//��ȡ���Ȳ����Ĵ���
function getFirstWindow(win){
	if (!IsEmpty(getTopWin(window).opener)) {
		getFirstWindow(getTopWin(window).opener);
	} else {
		return getTopWin(window);
	}
}

/**��ȡ��㴰��
*win:window����
*������ʱ�Զ�ȡ��ǰwindow����
*/
function getTopWin(win){
	if(win==null)win=window;
	var name = getPathName(win);
	if(name==getPathName(win.parent)){
		if(win.parent!=window.top){return getTopWin(win.parent);}
		else{return win.parent;}
	}else{
		return win;
	}
}

function getSystemTop(){
	return getTopWin();
}

/**��ȡ������
*win:window����
*������ʱ�Զ�ȡ��ǰwindow����
*/
function getParentWin(win){
	if(win==null)win=window;
	var name = getPathName(win) == "/"?"":getPathName(win);
	var parentName = getPathName(win.parent) 
		== "/"?"":getPathName(win.parent);
	if(name==parentName)return win.parent;
	else return win;
}

/**��ȡҳ������Ӧ������
*win:window����
*������ʱ�Զ�ȡ��ǰwindow����
*/
function getPathName(win){
	if(win==null)win=window;
	//commonJS.jsp�ж�����ȫ�ֱ���
	if(win.CONTEXT_PATH_NAME){
	   return win.CONTEXT_PATH_NAME;
	}else {//�Ǳ���Ŀ
	    try{
			var contextPath = win.document.location.pathname; 
		  	var index =contextPath.substr(1).indexOf("/"); 
		  	contextPath = contextPath.substr(0,index+1); 
		  	//��ģ̬������ҳ�棬IE��û��ǰ"/"������open����ҳ����õ�/SSH3����ģ̬����ֻ�ܵõ�SSH3
		  	if(contextPath && contextPath.indexOf("/")!=0){contextPath=("/"+contextPath);} 
		  	delete index; 
		  	return contextPath; 
	  	}catch(e){
	  		return CONTEXT_PATH_NAME.replace("/","");
	  	}
	}
}

/**��ȡҳ������Ӧ�õĸ�·��
*win:window����
*������ʱ�Զ�ȡ��ǰwindow����
*/
function getFullPath(win){
	if(win==null)win=window;
	var pathName = getPathName(win);
	var fullPath = win.location.protocol+"//"+win.location.host;
	if(pathName){
		fullPath+="/"+pathName.replace("/","");
	}
	return fullPath;
}

function getTreegridItems(tree){
	var items = new Array();
	for ( var i = 0; i < tree.length; i++) {
		pushTreegridItems(items,tree[i]);
	}
	return items;
}

function pushTreegridItems(items,tree){
	items.push(tree);
	var childs = tree.children;
	if(childs!=null){
		for ( var i = 0; i < childs.length; i++) {
			pushTreegridItems(items,childs[i]);
		}
	}
}


/** 
*��ȡ��ǰԪ�صĿ�ȣ����Ԫ�صĸ��ڵ������ص�������ʾ����ȡ����Ⱥ����������ظ��ڵ㡣
*tangyj 2013-3-23
*/
function getElementWidth(element){
	var hiddenNodes = [];//�������飬�������صĽڵ㡣������ȡ��Ԫ�ؿ�Ⱥ���������
	
	/**
	*�ݹ鸸�ڵ㣬�Ӹ��ڵ����������ʾ�ڵ�
	*/
	function  dispalyElement(element){
		var $ele = $(element);
		if( !element || $ele.is("body") ){//�жϵ�ǰ�ڵ��Ƿ�ɼ�����body�ڵ�ֱ�ӷ���
			return;
		}else{//������body�ڵ㣬���ҳ�����displayΪnone�Ľڵ㣬Ȼ��Ӹ��ڵ��������
			dispalyElement($ele[0].parentNode);
			//�жϵ�ǰ�ڵ��Ƿ����ó�displayΪnone,�������������ʾ
			if($ele.css("display") == 'none'){
				$ele.show();	
				//alert($ele[0].tagName+"-"+$ele.css("display"));
				hiddenNodes.push(element);
			}
		}
	}
	
	if($(element).width() == 0 && false == $(element).is(":visible")){
		dispalyElement(element);
	}
	var elementWidth =element.offsetWidth;//��ȡԪ�ؿ��
	//��ȡԪ�ؿ��֮����Ҫ��ԭ�ڵ���������
	for(var i =0;i<hiddenNodes.length;i++){
		$(hiddenNodes[i]).hide();
	}
	return elementWidth;
}


/**************************************************����commonUtil��������**************************************************/
var commonUtil = {};
//�ڴ˶���ı����൱��java�ĳ�Ա����������commonUtil�����ͷ����ڵķ�������
commonUtil.jqSel_optArray  = []; 
commonUtil.jqSel_id = 1;//����ID
commonUtil.jqSel_mouseLeave = true;

commonUtil.digit_arr = [];
commonUtil.digit_id = 1;

commonUtil.detail_dataArray = [];

commonUtil.enter_id = 1;
commonUtil.objParamArray = [];

commonUtil.hintOptions = [];//�����������ʾ��Ϣ������
commonUtil.hintId = 1;
commonUtil.hintColor = "#9c9a97";

commonUtil.prompt_id = 1;//��ʾ��ID���ֱ�ʶ(һ��ҳ����ܶ����ʾ��)
commonUtil.prompt_fullData;
commonUtil.prompt_currentDataIndex = 0;

/**************************************************����commonUtil�Ĺ�������**************************************************/
//��Щ����ķ����൱��java�����˽�з���������js���޷���������˽�з��������Ծ�����������Ĺ��з�����
//�÷����ᱻcommonUtil��jqFileUpload_submit��initFileUpload�������ʣ�
 //��ո���
//isToLocal,isSaveToDB�и�Ĭ��ֵ�����innerCallΪfalse����Ϊ�����հ�ť��������Ҫ������պ���¼�
commonUtil.jqFileUpload_cleanFile = function(eId)
{
   var obj = $("#"+eId);
   if (obj.attr("disabled") == true) return;
   if(IsEmpty(obj.val())) return;//�ı���û���ļ�������������¼�

	var pathname = getPathName();
	var fastDFS_path = getFullPath();
	var tableName = IsEmpty(obj.attr("sTableName"))?"default":obj.attr("sTableName");
	var isToLocal = obj.attr("isToLocal");
	var isSaveToDB = obj.attr("isSaveToDB");
	var fileId = $("#"+eId).attr("fileId");
	var newFileName = obj.attr("newFileName");
	
	//ɾ��������ļ�
	AjaxRequest.doRequest('',
  			fastDFS_path+'/fastDfs/singleFileUpload!cleanFile.action',//�ϴ����õ�Action,�����ϴ��ύ��ҳ��url
	    {fileid:fileId, isToLocal:isToLocal},
	    function(backData) {
	        //ɾ�����������
	    	if ((backData == 1 || backData == "1") && (isSaveToDB == 1 || isSaveToDB == "1")) {
	    		AjaxRequest.doRequest('', fastDFS_path + '/fastDfs/fastDfs!deleteUploadByFileId.action',{fileid :newFileName}, function(backData) {
					if (backData == '1' || backData == 1) {
						var afterClean = $(window).data("afterClean_"+eId);
						//if(!innerCall){
							if(!IsEmpty(afterClean)){
								afterClean.call(this);
							}
						//}
					}
				});
	    	}else if(backData == 1 || backData == "1"){
	    		var afterClean = $(window).data("afterClean_"+eId);
				//if(!innerCall){
					if(!IsEmpty(afterClean)){
						afterClean.call(this);
					}
				//}
	    	}
	    	
	    	//��ս����ϵ�ֵ
	    	obj.val('');
	    	obj.attr({
		    	fileSize:'',
		    	fileId:'',
		    	iFileId:'',
		    	oldFileName:'',
		    	newFileName:''
		    });
	    }
    ,false);
};

/***************************JquerySelect**************************/
/*
��ʼ��jquery������
�����������������:
eId:��Ҫ��ʼ�����ı���id,
desc: ��ʾ��Ϣ(�����Ϊ����Ĭ����"ȫ��")
clickFn: ���ѡ��ʱִ�еķ���
changeFn: ֵ�ı�ʱִ�еķ���
*/
commonUtil.initJquerySelect = function(option)
{
	var text = document.getElementById(option.eId);
	if (IsEmpty(text)) return;
	
	try{
		var tableNode = text.parentNode.parentNode.parentNode.parentNode;
		if(tableNode && $(tableNode).attr("class") == "JQSelect"){
			return;//���ɽ���������JQSelect��classʱ������ֱ�ӷ���
		}
	}catch(e){
	}
	if($(text).attr("class")==""){
		$(text).addClass("JQSelect");//����JQSelect��class������ѡ������ȡ
	}
	var div = document.createElement("span");
	var table = document.createElement("table");
	$(table).addClass("JQSelect").attr("cellpadding", "0").attr("cellspacing", "0");
	
	var tbody = document.createElement("tbody");
	var tr = document.createElement("tr");
	var td_1 = document.createElement("td");
	var obj = text.cloneNode(true);//��������һ���ı���
	//tangyj 2013-04-27
	//��ԭ��input���widthֵ���õ�oldWidth�����ϣ������ڴ��ڸı�ʱʵʱ������
	$(obj).attr("oldWidth",$(text).css("width"));
	
	$(div).css({"display":$(obj).css("display")});
	if(option.clickFn){
		$(obj).bind("click", option.clickFn);
	}
	if (!IsEmpty(option.changeFn)){
		$(obj).bind("change", option.changeFn);
	}
	$(obj).addClass("txt").css("width",getElementWidth(text) - 15)
	$(tr).append($(td_1).append(obj));
	
	var td_2=document.createElement("td");
	var span = document.createElement("span");
	span.className = "btn";
	$(span).css("width", 15).html("&nbsp;&nbsp;&nbsp;&nbsp;");//��span���¼�ԭinput���ֵ
	if ($(obj).attr("disabled") == false) {
		if ($(obj).attr("disabled") == true) return;
		if(option.clickFn){
			$(span).bind("click", option.clickFn);
		}else{
			$(span).bind("click", function(){obj.click();});//�����ťʱ�൱�ڵ�����ı��������Զ���ʼ������ĵ�����
		}
		$(span).bind("click", function(){obj.focus()});
	}
	$(document.body).append($(div).append($(table).append($(tbody).append($(tr).append($(td_2).append(span))))));
	
	var desc = "ȫ��";
	if (option.desc || (typeof option.desc !='undefined' && option.desc=='')) desc = option.desc;
	$(obj).attr("desc", desc);
	if ($(obj).val() == "") {
		$(obj).val(desc);
	}
	text.parentNode.replaceChild(div,text);//��ԭ�Ŀ���ɵ�ǰ��span
};

/*��ʾ������,�������������������:
		{vId:�洢ʵ��ֵ�Ķ����ID,
		 dataSrc:[true|false]�Ƿ��ⲿǶ��ҳ��,Ĭ��false,
		 url:���dataSrcΪtrue,�������ⲿǶ��ҳ��url,�����false����Զ�̻�ȡ����Դ��url,
		 data:��������Դ,��url��ѡһ,
		 div:dataSrcΪtrueʱ,Ƕ��ҳ�а���grid��div��id
		 dataColumns: ���dataSrcΪfalse,����������������������,
		 textColumn: �б��ı�ֵ��ID,
		 idColumn:�б��ʶֵ��ID,
		 isTree: [true|false] �Ƿ�������,Ĭ��false,
		 checkParent: ��isTreeΪtrueʱ,�����ӽڵ㹴ѡ,���ڵ㶼��ѡ,Ĭ��Ϊfalse
		 checkChildren: ��isTreeΪtrueʱ,���ø��ڵ㹴ѡ��ȡ��,�ӽڵ�Ҳȫ����ѡ��ȡ��,Ĭ��Ϊfalse
		 isJsInit: �Ƿ�Ϊjs��ʼ���������б�,Ĭ��Ϊfalse
		 desc:��ʾ��Ϣ,
		 width:�����б��,
		 height:�����б��}
*/
commonUtil.showJquerySelectList = function(option)
{
/*******************************�ڲ�������ʼ************************************/
	//var jqSel_optArray = [];
	//var jqSel_id = 1;//����ID
	//��ʼ��������
	function jqSel_init(option){
		if (option.dataSrc == false) {//Ƕ��DIV
			if (IsEmpty(option.dId)) {//��δ����DIV
				$(option.obj).bind("blur", function(){jqSel_hiddenSelectList(option);});
				var div = jqSel_createDiv(option);
				var table = jqSel_createTb(option);
				div.appendChild(table);
				document.body.appendChild(div);
				jqSel_buildDataGrid(option);
				jqSel_loadData(option);
				commonUtil.jqSel_id++;
			} else {//�Ѵ���DIV�������б�ȶ���,��ʾ����
				jqSel_loadData(option);
				$('#'+option.dId).show();
			}
		} else {//Ƕ���ⲿҳ
			if (IsEmpty(option.dId)) {//��δ����DIV
				$(option.obj).bind("blur", function(){jqSel_hiddenSelectList(option);});
				var div = jqSel_createDiv(option);
				div.style.width = option.width;
				div.style.height = option.height;
				var iFrame = jqSel_createIFrame(option);
				div.appendChild(iFrame);
				document.body.appendChild(div);
				commonUtil.jqSel_id++;
			} else {//�Ѵ���DIV�������б�ȶ���,��ʾ����
				$('#'+option.dId).show();
				jqSel_initValue(option);
			}
			jqSel_getContent(option);
		}
	}
	
	function jqSel_createDiv(option){
		var div = document.createElement("div");//��������������DIV
		div.id = "JQSelect_div_"+option.vId.replace(".","_");
		//�ж��Ƿ��ظ�����div tangyj 2013-03-23
		var idIndex = 1;
		while(true){
			if($("#"+div.id).size() == 0){
				break;
			}
			div.id += "_"+idIndex;
		}
		//end
		div.jqSel_mouseLeave="false";
		div.className = "JQSelect_div";
		$(div).bind("mouseenter", jqSel_enter).bind("mouseleave", jqSel_leave);
		option.dId = div.id;
		$(div).css("zIndex", "99999");
		return div;
	}
	//����table����װ��jquery��datagrid
	function jqSel_createTb(option){
		var table = document.createElement("table");
		table.id = "JQSelect_tb_"+option.vId.replace(".","_");
		//�ж��Ƿ��ظ�����table tangyj 2013-03-23
		var idIndex = 1;
		while(true){
			if($("#"+table.id).size() == 0){
				break;
			}
			table.id += "_"+idIndex;
		}
		//end
		option.tId = table.id;
		return table;
	}
	//����һ����iframe
	function jqSel_createIFrame(option){
		var iframe = document.createElement("iframe");
		iframe.id = "JQSelect_frm_"+option.vId.replace(".","_");
		//�ж��Ƿ��ظ�����table tangyj 2013-03-23
		var idIndex = 1;
		while(true){
			if($("#"+iframe.id).size() == 0){
				break;
			}
			iframe.id += "_"+idIndex;
		}
		//end
		iframe.className = "JQSelect_iframe";
		iframe.src = option.url + (option.url.indexOf("?") >= 0 ? "&" : "?") +"loadSuccessCall="+option.loadSuccessCall;
		iframe.width = "100%";
		iframe.height = "100%";
		option.fId = iframe.id;
		return iframe;
	}
	//����datagrid
	function jqSel_buildDataGrid(option){
		var selCol = [{field:'ck',checkbox:true}];
		if (!option.isMultiSelect){selCol = []}
		if (!option.isTree) {
			$('#'+option.tId).datagrid({
				columns:[option.dataColumns],
				frozenColumns:[selCol],//�����У��Ҵ���Ϊ��ѡ��
				nowrap:true,
				fitColumns: true, width: option.width,height: option.height,
				rownumbers:false,
				showHeader:(option.showHeader&&(option.showHeader+"")!="false")?true:false,//����ʾ�����б�ı�ͷ
				onDblClickRow:function(row,rowData){
					$("#"+option.dId).hide();
				}
			});
		} else {
			$('#'+option.tId).treegrid({
				columns:[option.dataColumns],
				frozenColumns:[selCol],//�����У��Ҵ���Ϊ��ѡ��
				nowrap:true,
				fitColumns: true, width: option.width,height: option.height,
				idField:option.idColumn,
				treeField:option.textColumn,
				onBeforeExpand:function(row) {
					jqSel_loadNodeData(option, row);
				},
				rownumbers:false,singleSelect:false,
				showHeader:(option.showHeader&&(option.showHeader+"")!="false")?true:false,//����ʾ�����б�ı�ͷ
				checkParent:option.checkParent,//add by zwb 2012-08-28
				checkChildren:option.checkChildren,//add by zwb 2012-08-28
				onDblClickRow:function(rowData){
					$("#"+option.dId).hide();
				}
			});
		}
	}
	function jqSel_selectData(option){
		var grid = option.isTree?"treegrid":"datagrid";
		var selRows = eval("$('#'+option.tId)."+grid+"('getSelections')");
		var id = "", text = "";
		for (var i = 0;i < selRows.length;i++) {
			if (id.length > 0) {id += ",";text += ",";}
			id += eval('selRows[i].'+option.idColumn);
			text += eval('selRows[i].'+option.textColumn);
		}
		$(document.getElementById(option.vId)).val(id);
		if (text == "") text = IsEmpty($(option.obj).attr("desc")) ? "ȫ��" : $(option.obj).attr("desc");
		$(option.obj).val(text).focus();//.change();
	}
	function jqSel_loadNodeData(option, row) {
		if (IsEmpty(option.loadNodeUrl)) return;
		var childItems = $('#'+option.tId).treegrid("getChildren",eval("row."+option.idColumn));
	   	if(childItems == null)return;
	   	if(childItems != null && childItems.length>0){
	   		return;
	   	}
		var param = {};
		if (IsEmpty(option.loadNodeParam)) {
			param = option.loadNodeParam;
		}
		param.iParentId = eval("row."+option.idColumn);
		AjaxRequest.doRequest(null,option.loadNodeUrl,param,function(backData){
			var jsonData = decode(backData);
			//�ж��Ƿ����ӽڵ�
			jqSel_setDataChildNodes(jsonData);
			jqSel_changeJson(jsonData);
			$('#'+option.tId).treegrid("collapse", eval("row."+option.idColumn));
			$('#'+option.tId).treegrid("append", {parent:eval("row."+option.idColumn),data:jsonData});
			$('#'+option.tId).treegrid("expand", eval("row."+option.idColumn));
			jqSel_initValue(option);
			jqSel_bindSelectEvent(option);
		});
	}
	function jqSel_setDataChildNodes(jsonData) {
		for(var i = 0; i < jsonData.length; i++){
			jqSel_doSetChildNodes(jsonData[i]);	
		}
	}
	//�ݹ����Ȩ����,�ж��Ƿ����ӽڵ�
	function jqSel_doSetChildNodes(node) {
 		var children = node.children;   
    	if (children != null) {   
        	for(var n = 0; n < children.length; n++){
        		jqSel_doSetChildNodes(children[n]);
        	}
    	} 
		if(node.childCount && !node.children)node.children = [];
	}
	function jqSel_changeJson(json){
		for(var j =0 ;j<json.length; j++){
			if(json[j].children!=null){
				changeJson(json[j].children);
				if(json[j].iParentId!= null){
					json[j].state='closed';
				}
			}
			if(json[j].iDeptType==3){
				json[j].iconCls="team_bumen";
			}else{
				json[j].iconCls="fengpei2";				
			}
		}
	}
	//var jqSel_mouseLeave = true;
	//������������
	function jqSel_enter(){
		commonUtil.jqSel_mouseLeave = false;
	}
	//����뿪������
	function jqSel_leave(){
		commonUtil.jqSel_mouseLeave = true;
		//��ȡ��ǰ����Ƴ�������DIV
		var evt = GetBrowserType().indexOf("IE") >= 0 ? event : commonUtil.showJquerySelectList.caller.arguments[0];
		var target = IsEmpty(evt.srcElement) ? evt.target : evt.srcElement;
		while(target.className!="JQSelect_div" && target!=document.body){
			target = target.parentNode;
		}
		//�õ���ǰDIV��Ӧ���ı���
		var option = window.jqSelectOption[target.id];
		//�����ı���õ����㣬��������κη��ı��������ı���ʧȥ���㣬����DIV�ͻ��Զ�������
		option.obj.focus();
	}
	//����������
	function jqSel_hiddenSelectList(option){
		if (!commonUtil.jqSel_mouseLeave) {
			return;
		}
		$('#'+option.dId).hide();
		
		if(option.isJsInit)return;//�����js��ʼ���ģ���ִ�к�����߼�
		//�и�ѡ���������ı����ֵ�ı�ʱ����change�¼�
		if(option.isMultiSelect && option.initValue != $(option.obj).val()){
			$(option.obj).change();
		}
	}
	//��������
	function jqSel_loadData(option){
		var grid = option.isTree ? "treegrid" : "datagrid";
		if (IsEmpty(option.data)) {
			AjaxRequest.doRequest('', option.url, option.urlParams, function(backData){
				var jsonData = Ext.util.JSON.decode(backData);
				if (option.isTree == true && option.loadNodeUrl && option.loadNodeParam) {
					jqSel_setDataChildNodes(jsonData);
					jqSel_changeJson(jsonData);
				}
				eval("$('#'+option.tId)."+grid+"('loadData', jsonData)");
				jqSel_initValue(option);
				jqSel_bindSelectEvent(option);
			});
		} else {
			if (option.isTree == true && option.loadNodeUrl && option.loadNodeParam) {
				jqSel_setDataChildNodes(option.data);
				jqSel_changeJson(option.data);
			}
			eval("$('#'+option.tId)."+grid+"('loadData', option.data)");
			//�����б�checkboxƫ�Ƶ�����
			if (option.isTree) {
				if(GetBrowserType().indexOf("IE") >= 0)
					$('#'+option.dId+' .datagrid-view1 .datagrid-cell-check').css("width", "").css("height", "").css("padding-top","6px");
				else
					$('#'+option.dId+' .datagrid-view1 .datagrid-cell-check').css("width", "").css("height", "").css("padding-top","7px");
			}else{
				if(GetBrowserType().indexOf("IE") >= 0)
					$('#'+option.dId+' .datagrid-view1 .datagrid-cell-check').css("width", "").css("height", "").css("padding-top","2px");
				else
					$('#'+option.dId+' .datagrid-view1 .datagrid-cell-check').css("width", "").css("height", "").css("padding-top","5px");
			}
			jqSel_initValue(option);
			jqSel_bindSelectEvent(option);
		}
	}
	//���¼�
	function jqSel_bindSelectEvent(option) {
		//���treegrid���ܴ���onSelect�¼�,�������checkbox���¼�
		if(option.isMultiSelect!=false){//if a by gt
			$(document.getElementById(option.dId)).find(":checkbox").each(function(){
				$(this).bind("click", function(){jqSel_selectData(option);});
			});
		}else{
		//����е�ѡ����
			$("#"+option.dId+" .datagrid-view2 .datagrid-body tr").each(function(){
				$(this).bind("click", function(event){
					event.stopPropagation();
					var selId = $(this).find("td[field="+option.idColumn+"] div").eq(0).html();
					var selText = $(this).find("td[field="+option.textColumn+"] div "+(option.isTree?".tree-title":"")).eq(0).html();
					$(document.getElementById(option.vId)).val(selId);
					
					if(option.initValue != selText){//�������ֵ�ı�ʱ�Ŵ���change�¼�
						if(option.isJsInit)//ʹ��js��ʼ��
							$(option.obj).val(selText).focus();
						else
							$(option.obj).val(selText).focus().change();
					}
					$("#"+option.dId).hide();
				});
			});
		}
		return;
		$(document.getElementById(option.dId)).find("div").bind("click", function(){
			event.stopPropagation();
			return false;
		});
	}
	//��ʾ��ѡ����
	function jqSel_initValue(option){
		option.initValue = $(option.obj).val();//��¼������ĳ�ʼֵ add by zwb
		var value = $(document.getElementById(option.vId)).val();
		var vals = value.split(',');
		//ȡ��ѡ�������� //$('#'+option.tId).datagrid('unselectAll');
		if (option.isMultiSelect) {
			$('#'+option.dId+' .datagrid-view1 .datagrid-body-inner :checkbox').each(function(){
				this.checked = false;
			});
		} else {
			$('#'+option.dId+' .datagrid-view2 tr').removeClass("datagrid-row-selected");
		}
		for (var j = 0;j < vals.length;j++) {
			var id = vals[j];
			$('#'+option.dId+' .datagrid-view2 tr').each(function(){
				//�ҵ�idֵ��ͬ����,�õ���������checkboxѡ��
				if ($(this).children('td[field='+option.idColumn+']').children('.datagrid-cell').html() == id) {
					var rowIndex = $(this).attr('datagrid-row-index');
					var nodeId = $(this).attr('node-id');
					if (option.isMultiSelect) {
						if (option.isTree)
							$('#'+option.dId+' .datagrid-view1 tr[node-id='+nodeId+'] :checkbox')[0].checked = true;
						else
							$('#'+option.dId+' .datagrid-view1 tr[datagrid-row-index='+rowIndex+'] :checkbox')[0].checked = true;
					}
					else {
						$('#'+option.dId+' .datagrid-view2 tr[datagrid-row-index='+rowIndex+']').eq(0).addClass("datagrid-row-selected")
					};
				}
			});
		}
	}
	//������λ
	function jqSel_showPosition(option) {
		var e = option.obj;
		var div = document.getElementById(option.dId);
		var left = $(e).offset().left;
		var top = $(e).offset().top;
		if (left + $(div).outerWidth() <= $(document.body).outerWidth()) {
			$(div).css("left",left);
		} else {
			$(div).css("left",left + $(e).outerWidth() + 15 - $(div).outerWidth());
		}
		if (top + $(e).outerHeight() + option.height <= $(document.body).outerHeight()) {
			$(div).css("top", top + $(e).outerHeight());
		} else {//���ҳ���·��ؼ�����
			$(div).css("top", top - option.height);
		}
	}
	function jqSel_getContent(option){
		$('#'+option.fId).bind("load", function(){//frameҳ��������ʱ
			window.jqSel_option = option;
			commonUtil.jqSel_wait();
		});
	}
	//��������
	function jqSel_saveOption(option){
		var isFind = false;
		for (var i = 0;i < commonUtil.jqSel_optArray.length;i++) {
			if (commonUtil.jqSel_optArray[i].obj.id != option.obj.id) continue;
			for (var idx in option) {
				if (typeof commonUtil.jqSel_optArray[i][idx] == "undefined") continue;
				commonUtil.jqSel_optArray[i][idx] = option[idx];
			}
			isFind = true;
			return commonUtil.jqSel_optArray[i];
		}
		if (!isFind) {
			commonUtil.jqSel_optArray.push(option);
			return option;
		}
	}
/*******************************�ڲ���������************************************/
	/*obj���ı���*/
	
	var evt = getEvent(event);//GetBrowserType().indexOf("IE") >= 0 ? event : commonUtil.showJquerySelectList.caller.arguments[0];
	var target = IsEmpty(evt.srcElement) ? evt.target : evt.srcElement;
	option.obj = target.nodeName=="SPAN"?$(target).parent().parent().find(":text")[0]:target;
	option.isTree = !IsEmpty(option.isTree) && option.isTree;
	//add by zhanweibin 2012-08-27
	option.checkParent = !IsEmpty(option.checkParent) ? option.checkParent : false;
	option.checkChildren = !IsEmpty(option.checkChildren) ? option.checkChildren : false;
	option.isJsInit = !IsEmpty(option.isJsInit) ? option.isJsInit : false;
	//add end
	option.isMultiSelect = IsEmpty(option.isMultiSelect) ? true : option.isMultiSelect;
	//option.selectParent = IsEmpty(option.selectParent) ? true : option.selectParent; 
	option.width = IsEmpty(option.width) ? $(option.obj).width() + 19 : option.width;
	option.height = IsEmpty(option.height) ? 132 : option.height;
	option = jqSel_saveOption(option);
	jqSel_init(option);
	jqSel_showPosition(option);
	$("#"+option.dId).find("div").bind('click', function(){
		$(option.obj).focus();
	});
	
	window.jqSelectOption=window.jqSelectOption?window.jqSelectOption:{};
	window.jqSelectOption[option.dId]=option;
};

//����������Ƕҳʱ��Ƕҳ���������ӳٴ���
/*
 * update by zhanweibin 2012-08-27
 * ԭ�е�ʵ�ַ�ʽ��ͨ��ҳ���ǩ����ȡ��ѡ����ѡ������
 * �޸�Ϊͨ��easyui API����ȡ�������������Ҫ����gridId���ԣ��б��id���ԣ�
 */
commonUtil.jqSel_wait = function()
{
/*******************************�ڲ�������ʼ************************************/
	//add by zhanweibin 2012-08-27
	//Ƕ��ҳ��datagrid�������¼�
	function jqSel_clickDatagridRow(rowIndex, rowData, option){
		//������ѡ��ֵ
		$(document.getElementById(option.vId)).val(rowData[option.idColumn]);
		$(option.obj).val(rowData[option.textColumn]).focus();
		jqSel_clickCommon(option);
	}
	//Ƕ��ҳ��datagrid˫�����¼�
	function jqSel_dblClickDatagridRow(rowIndex, rowData, option){
		jqSel_dblClickCommon(option);
	}
	//Ƕ��ҳ��treegrid�������¼�
	function jqSel_clickTreegridRow(row, option){
		//������ѡ��ֵ
		$(document.getElementById(option.vId)).val(row[option.idColumn]);
		$(option.obj).val(row[option.textColumn]).focus();
		jqSel_clickCommon(option);
	}
	//Ƕ��ҳ��treegrid˫�����¼�
	function jqSel_dblClickTreegridRow(row, option){
		jqSel_dblClickCommon(option);
	}
	//�������¼����Ĺ�������
	function jqSel_clickCommon(option){
		//�������ֵ�����ı�ʱ����change�¼�
		if(option.initValue != $(option.obj).val()){
			$(option.obj).change();
		}
		$("#"+option.dId).hide();
	}
	//˫�����¼����Ĺ�������
	function jqSel_dblClickCommon(option){
		$("#"+option.dId).hide();//˫��������div
	}
	
	//Ϊcheckbox���¼� tangyj 2013-05-24
	function jqSel_bindCheckboxEvt(option){
		var div = $($('#'+option.fId)[0].contentWindow.document);
			var cbxs = div.find(":checkbox");
			//��ѡ������¼�
			for (var i = 0;i < cbxs.length;i++) {
				//if ($(cbxs[i]).parent().attr("className") == "datagrid-header-check") continue;//�ų�ͷ��checkbox
				$(cbxs[i]).unbind('change');//�����֮ǰ���¼�����������¼�
				$(cbxs[i]).bind("change", function(){
					$(window).data("jqSel_option", option);
					setTimeout('commonUtil.jqSel_changeEvt()', 100);
				});
			}
	}
	//add end
/*******************************�ڲ���������************************************/
	var option = window.jqSel_option;
	option.initValue = $(option.obj).val();
	var isTree = option.isTree ? option.isTree : false;
	var initOption = null;//�б��ʼ�������
	var extendOption = null;//�б��¼��������
	
	//�������б��targetʧȥ����
	$("#"+option.fId)[0].contentWindow.$("#"+option.gridId).parent().bind('click', function(){
		$(option.obj).focus();
	});
	//option.div�������κ�ֱ��ȥ��iframe�е�grid divԪ��
	if ($("#"+option.fId)[0].contentWindow.$(".datagrid-view2 .datagrid-body tr").length != 0){
		//���ڸ�ѡ�������ε������¼�
		if(option.isMultiSelect){
			if(isTree){//treegrid����checkParent,checkChildren����,onDblClickRow�¼�
				initOption = $("#"+option.fId)[0].contentWindow.$("#"+option.gridId).treegrid("options");
				extendOption = {
					checkParent : option.checkParent, checkChildren : option.checkChildren,
					onClickRow : function(){},
					onDblClickRow : function(row){jqSel_dblClickTreegridRow(row, option);}
				};
			}else{//datagrid����onDblClickRow�¼�
				initOption = $("#"+option.fId)[0].contentWindow.$("#"+option.gridId).datagrid("options");
				extendOption = {
					onClickRow : function(){},
					onDblClickRow : function(rowIndex, rowData){jqSel_dblClickDatagridRow(rowIndex, rowData, option);}
				};
			}
			$.extend(initOption, extendOption);
			//tangyj 2012-5-8 ��onLoadSuccess�¼�����ӶԿ�ѡ��ѡ�ļ���
			jqSel_bindCheckboxEvt(option);//ҳ�����ʱ��һ��
			initOption.onLoadSuccess = function(row,data){
				jqSel_bindCheckboxEvt(option);//չ���ڵ�ʱ��Ҫ���ӽڵ��
			 }
			return;
		}else{
			//���¼�
			if(isTree){//treegrid
				initOption = $("#"+option.fId)[0].contentWindow.$("#"+option.gridId).treegrid("options");
				extendOption = {
					onClickRow : function(row){jqSel_clickTreegridRow(row, option);},
					onDblClickRow : function(row){jqSel_dblClickTreegridRow(row, option);}
				};
				
			}else{//datagrid
				initOption = $("#"+option.fId)[0].contentWindow.$("#"+option.gridId).datagrid("options");
				extendOption = {
					onClickRow : function(rowIndex, rowData){jqSel_clickDatagridRow(rowIndex, rowData, option);},
					onDblClickRow : function(rowIndex, rowData){jqSel_dblClickDatagridRow(rowIndex, rowData, option);}
				};
			}
			$.extend(initOption, extendOption);
		}
	}
	window.setTimeout("commonUtil.jqSel_wait()", 100);
};

commonUtil.jqSel_changeEvt = function() 
{
	option = $(window).data("jqSel_option");
	var isTree = option.isTree ? option.isTree : false;
	var selections = "";
	var id = "",text = "";
	if(isTree){//treegrid
		selections = $("#"+option.fId)[0].contentWindow.$("#"+option.gridId).treegrid("getSelections");
	}else{//datagrid
		selections = $("#"+option.fId)[0].contentWindow.$("#"+option.gridId).datagrid("getSelections");
	}
	if(selections.length > 0){
		for(var i=0; i<selections.length; i++){
			if (id.length > 0) {id += ","; text += ",";}
			id += selections[i][option.idColumn];
			text += selections[i][option.textColumn];
		}
		$(document.getElementById(option.vId)).val(id);
		if (text == "") text = IsEmpty($(option.obj).attr("desc")) ? "ȫ��" : $(option.obj).attr("desc");
		$(option.obj).val(text).focus();
		$(option.obj)[0].focus();
	}else{
		//���id��textֵ
		$(document.getElementById(option.vId)).val("");
		text = IsEmpty($(option.obj).attr("desc")) ? "ȫ��" : $(option.obj).attr("desc");
		$(option.obj).val(text).focus();
		$(option.obj)[0].focus();
	}
};
/***************************JquerySelect����**************************/

/***************************popupWindow******************************/
/**
 * ��ʼ�������ı���
 * update by zhanweibin 2013-1-7
 * ���������Ϊ������ʽ���ַ����ͣ�������
 * 	�ַ����ͣ�
 * 	  popObj: ���textֵ�ı�ǩid(����)
 *    clickFnObj: ���������ť����Ӧ�¼�(����)
 * 	  valIdsObj�����idֵ�ı�ǩid
 *    cleanFnObj: ��պ�ִ�еķ���
 * 	������ {textId: xxx, clickFn: xxx, valIds: xxx, cleanFn: xxx}
 */
commonUtil.initPopupWindow = function(popObj, clickFnObj, valIdsObj, cleanFnObj) 
{
	var textId = null;
	var clickFn = null;
	var valIds = null;
	var cleanFn = null;
	
	if(arguments.length == 1 && typeof(textId) == "object"){//�Զ�����ʽ�������
		textId = popObj.textId;
		clickFn = popObj.clickFn;
		valIds = popObj.valIds;
		cleanFn = popObj.cleanFn;
	}else if(arguments.length > 1){//���ַ�����ʽ�������
		textId = popObj;
		clickFn = clickFnObj;
		valIds = valIdsObj;
		cleanFn = cleanFnObj;
	}
	var text = document.getElementById(textId);
	if (IsEmpty(text)) return;
	try{
		var tableNode = text.parentNode.parentNode.parentNode.parentNode;
		if(tableNode && $(tableNode).attr("class") == "PopupWindow"){
			return;//���ɽ���������PopupWindow��classʱ������ֱ�ӷ���
		}
	}catch(e){
	}
	if($(text).attr("class")==""){
		$(text).addClass("PopupWindow");//����PopupWindow��class������ѡ������ȡ
	}
	var obj = text.cloneNode(true);//��������һ���ı���
	
	//tangyj 2013-04-27
	//��ԭ��input���widthֵ���õ�oldWidth�����ϣ������ڴ��ڸı�ʱʵʱ������
	$(obj).attr("oldWidth",$(text).css("width"));
	
	if ($(text).attr("class") == "txt") {
		$(text).removeClass("txt").css("width", getElementWidth(text) + 16);
		$("#"+textId+"_span")[0].parentNode.replaceChild(text,$("#"+textId+"_span")[0]);
	}
	//$(text).parent().parent().css("display","none");
	var div = document.createElement("span");
	div.id = textId+"_span";
	var table = document.createElement("table");
	$(table).addClass("PopupWindow").attr("cellpadding", "0").attr("cellspacing", "0");
	$(div).css({"display":$(obj).css("display")});
	var tbody = document.createElement("tbody");
	var tr_1 = document.createElement("tr");
	var td_11 = document.createElement("td");
	$(td_11).attr("rowspan", "2");
	$(obj).addClass("txt").css("width", getElementWidth(text) - 16);
//	if(this.className == 'PopupWindow') {
	if($(text).attr("className") == 'PopupWindow') {
		$(obj).attr("onclick", "");//����ǵ����ı���,ȥ��ԭʼ�¼�
	}
	td_11.appendChild(obj);
	
	//������ť
	var td_12 = document.createElement("td");
	var openWinSpan=document.createElement("span");
	$(openWinSpan).html("&nbsp;&nbsp;&nbsp;&nbsp;").addClass("openWinBtn").attr("title","����ѡ");//��span���¼�ԭinput���ֵ
	td_12.appendChild(openWinSpan);
	$(tr_1).append(td_11).append(td_12);
	
	//��հ�ť
	var tr_2 = document.createElement("tr");
	var td_22 = document.createElement("td");
	var cleanSpan=document.createElement("span");
	$(cleanSpan).html("&nbsp;&nbsp;&nbsp;&nbsp;").addClass("cleanBtn").attr("title","���");//��span���¼�ԭinput���ֵ
	
	var thisObj = this;
	if ($(obj).attr("disabled") == false) {
		$(openWinSpan).bind("click", function(){
			if ($(obj).attr("disabled") == true) return;
			clickFn(obj);
		});//��span���¼�ԭinput���ֵ
		$(cleanSpan).bind("click", function(){
			if ($(obj).attr("disabled") == true) return;
			$(obj).val("");
			if(valIds) {
				var ids = valIds.split(",");
				for (var i = 0;i < ids.length;i++) {
					//$("#"+ids[i]).val("");
					document.getElementById(ids[i]).value = "";
				}
			}
			if(cleanFn && typeof(cleanFn) == "function")cleanFn();
		});
	}
	$(td_22).append(cleanSpan);
	$(tr_2).append(td_22);
	$(tbody).append(tr_1).append(tr_2);
	$(table).append(tbody);
	$(div).append(table);
	
	text.parentNode.replaceChild(div,text);//��ԭ�Ŀ���ɵ�ǰ��span
	if ($(div).next().html() == "*")
	$(div).next().css("line-height", "2");
};
/***************************popupWindow����**************************/

/***************************TextareaWindow******************************/
/**
 * ��ʼ�������ı���
 * add by qiaoqide 2013-6-17
 * ���������Ϊ������ʽ���ַ����ͣ�������
 * 	�ַ����ͣ�
 * 	  popObj: ���textֵ�ı�ǩid(����)
 *    clickFnObj: ���������ť����Ӧ�¼�(����)
 * 	  valIdsObj�����idֵ�ı�ǩid
 *    cleanFnObj: ��պ�ִ�еķ���
 *    openPicObj: ������ťͼ������
 *    cleanPicObj: ��հ�ťͼ������
 * 	������ {textId: xxx, clickFn: xxx, valIds: xxx, cleanFn: xxx, openPic:xx.jpg, cleanPic:xx.gif}
 */
commonUtil.initTextareaWindow = function(popObj, clickFnObj, valIdsObj, cleanFnObj, openPicObj, cleanPicObj) 
{
	var textId = null;
	var clickFn = null;
	var valIds = null;
	var cleanFn = null;
	
	if(arguments.length == 1 && typeof(textId) == "object"){//�Զ�����ʽ�������
		textId = popObj.textId;
		clickFn = popObj.clickFn;
		valIds = popObj.valIds;
		cleanFn = popObj.cleanFn;
		openPic = popObj.openPic;
		cleanPic = popObj.cleanPic;
	}else if(arguments.length > 1){//���ַ�����ʽ�������
		textId = popObj;
		clickFn = clickFnObj;
		valIds = valIdsObj;
		cleanFn = cleanFnObj;
		openPic = openPicObj;
		cleanPic = cleanPicObj;
	}
	var text = document.getElementById(textId);
	if (IsEmpty(text)) return;
	try{
		var tableNode = text.parentNode.parentNode.parentNode.parentNode;
		if(tableNode && $(tableNode).attr("class") == "TextAreaPopupWindow"){
			return;//���ɽ���������TextAreaPopupWindow��classʱ������ֱ�ӷ���
		}
	}catch(e){
	}
	if($(text).attr("class")==""){
		$(text).addClass("TextAreaPopupWindow");//����TextAreaPopupWindow��class������ѡ������ȡ
	}
	var obj = text.cloneNode(true);//��������һ���ı���
	
	//��ԭ��input���widthֵ���õ�oldWidth�����ϣ������ڴ��ڸı�ʱʵʱ������
	$(obj).attr("oldWidth",$(text).css("width"));
	
	if ($(text).attr("class") == "txt") {
		$(text).removeClass("txt").css("width", getElementWidth(text) + 16);
		$("#"+textId+"_span")[0].parentNode.replaceChild(text,$("#"+textId+"_span")[0]);
	}
	//$(text).parent().parent().css("display","none");
	var div = document.createElement("span");
	div.id = textId+"_span";
	var table = document.createElement("table");
	$(table).addClass("TextAreaPopupWindow").attr("cellpadding", "0").attr("cellspacing", "0");
	$(div).css({"display":$(obj).css("display")});
	var tbody = document.createElement("tbody");
	var tr_1 = document.createElement("tr");
	var td_11 = document.createElement("td");

	$(obj).addClass("txt").css("width", getElementWidth(text) - 16);
	if(this.className == 'TextAreaPopupWindow') {
		$(obj).attr("onclick", "");//����ǵ����ı���,ȥ��ԭʼ�¼�
	}
	td_11.appendChild(obj);
	
	//������ť
	var td_12 = document.createElement("td");
	$(td_12).attr("style", "width:25px;text-align:center;vertical-align:bottom;");
	var openWinSpan=document.createElement("span");
	if(openPic){
	   $(openWinSpan).attr("style", "background:url("+path+"/images/find/"+openPic+") no-repeat right 0px;");
	}
	//$(openWinSpan).attr("style","padding-bottom:15px;");
	$(openWinSpan).html("&nbsp;&nbsp;&nbsp;&nbsp;").addClass("openTxtAreaWinBtn").attr("title","����ѡ");//��span���¼�ԭinput���ֵ
	
	//��հ�ť
	var cleanSpan=document.createElement("span");
	if(cleanPic){
	   $(cleanSpan).attr("style", "background:url("+path+"/images/find/"+cleanPic+") no-repeat right 0px;");
	}
	$(cleanSpan).html("&nbsp;&nbsp;&nbsp;&nbsp;").addClass("cleanTxtAreaBtn").attr("title","���");//��span���¼�ԭinput���ֵ
	td_12.appendChild(openWinSpan);
	$(td_12).append("<br/><br/>");
	td_12.appendChild(cleanSpan);
	$(tr_1).append(td_11).append(td_12); //����Ԫ���������
	
	var thisObj = this;
	if ($(obj).attr("disabled") == false) {
		$(openWinSpan).bind("click", function(){
			if ($(obj).attr("disabled") == true) return;
			clickFn();
		});//��span���¼�ԭinput���ֵ
		$(cleanSpan).bind("click", function(){
			if ($(obj).attr("disabled") == true) return;
			$(obj).val("");
			if(valIds) {
				var ids = valIds.split(",");
				for (var i = 0;i < ids.length;i++) {
					//$("#"+ids[i]).val("");
					document.getElementById(ids[i]).value = "";
				}
			}
			if(cleanFn && typeof(cleanFn) == "function")cleanFn();
		});
	}
	$(tbody).append(tr_1);
	$(table).append(tbody);
	$(div).append(table);
	
	text.parentNode.replaceChild(div,text);//��ԭ�Ŀ���ɵ�ǰ��span
	if ($(div).next().html() == "*")
	$(div).next().css("line-height", "2");
};
/***************************TextareaWindow����**************************/


/***************************fileUpload*****************************/
/*
update by gaotao 2013-12-08

eId:ָ��Ҫ���ɵ��ļ��ϴ���input��ǩid
iTableId:ģ���ж�Ӧ���������������Ϳ�����string,object,function
		 string���ͣ�ֱ�Ӵ�������id
		 object���ͣ�tId����Ϊ��Ԫ�ص�id,��{tId:'eleId'},����ͨ��ĳ����Ԫ��Ϊ������ֵ
		 function���ͣ��Զ��庯������������id
sTableName:ģ�����ƣ���Ӧ�ı�����������˸��������ŵ���ͬĿ¼�����
isToLocal:0-�ļ��ϴ���fastDFS�ļ�ϵͳ��������1-�ļ��ϴ�������Ӧ�÷������������ý���config.properties�е�Ϊ׼
isSaveToDB:�Ƿ�洢�����ݿ⡣0-��1-�ǣ�Ĭ��Ϊ��
directory:��ʱĿ¼���ơ���ϵͳָ����Ŀ¼���½�һ����Ŀ¼
limitReg:�ļ���׺���ơ���ʽΪ*.xxx,���������","�ŷָ�
displayName:�ļ��ϴ��ɹ������ı�������ʾ����Ϣ��ö��ֵΪfileId,localFileName,serverFileName,serverFileDir,Ĭ��Ϊ�ļ�·��
afterUpload:�ϴ���ť�ص�����
afterClean:��հ�ť�ص�����
*/         
commonUtil.initFileUpload = function(options) 
{
	//��json����ֵ������input��������
	function initAttr(obj,options){
		if(obj){
	       $(obj).attr({
	       	    iTableId :IsEmpty(options.iTableId1)?"":options.iTableId1,
				sTableName:IsEmpty(options.sTableName)?"":options.sTableName,
				isToLocal: IsEmpty(options.isToLocal)?"":options.isToLocal,
				isSaveToDB: IsEmpty(options.isSaveToDB)?"1":options.isSaveToDB,
				directory :encodeURIComponent(IsEmpty(options.directory)?"":options.directory),  //���룬\Ϊת���
				limitReg : IsEmpty(options.limitReg)?"":options.limitReg,
				displayName: IsEmpty(options.displayName)?"1":options.displayName,
				maxUpSize:IsEmpty(options.maxUpSize)?"":options.maxUpSize
	       });
		}
	}
	
	var fastDFS_path = getFullPath();
	var iTableId = IsEmpty(options.iTableId)?"":options.iTableId;
	var sTableName = IsEmpty(options.sTableName)?"":options.sTableName;
	var isToLocal = IsEmpty(options.isToLocal)?"":options.isToLocal;
	var isSaveToDB = IsEmpty(options.isSaveToDB)?"1":options.isSaveToDB;
	var directory = IsEmpty(options.directory)?"":options.directory;
	directory = encodeURIComponent(directory); //���룬\Ϊת���
	var limitReg = IsEmpty(options.limitReg)?"":options.limitReg;
	var maxUpSize = IsEmpty(options.maxUpSize)?"":options.maxUpSize;
	
	var displayName = IsEmpty(options.displayName)?"serverFileDir":options.displayName;
	var iDeptId = IsEmpty(options.iDeptId)?"":options.iDeptId;
	if(!IsEmpty(options.iTableId)){
		$(window).data("fileUploadTableId_"+options.eId, options.iTableId);//��ģ��������������
	}
	
	var text = document.getElementById(options.eId);
	if (IsEmpty(text)) return;
	
	var obj = text.cloneNode(true);//��������һ���ı���
	//tangyj 2013-04-27
	//��ԭ��input���widthֵ���õ�oldWidth�����ϣ������ڴ��ڸı�ʱʵʱ������
	$(obj).attr("oldWidth",$(text).css("width"));
	initAttr(obj,options);
	
	if ($(text).attr("class") == "txt") {
		$(text).removeClass("txt").css("width", getElementWidth(text) + 17);
		$("#"+options.eId+"_span")[0].parentNode.replaceChild(text,$("#"+options.eId+"_span")[0]);
	}
	var div = document.createElement("span");
	div.id = options.eId+"_span";
	
	var width = getElementWidth(text);
	var table = document.createElement("table");
	$(table).addClass("fileUpload").attr("cellpadding", "0").attr("cellspacing", "0");
	$(table).css("width",width);
	$(div).css({"display":$(obj).css("display")});
	var tbody = document.createElement("tbody");
	var tr_1 = document.createElement("tr");
	var td_11 = document.createElement("td");
	$(td_11).attr("rowspan", "2");
	$(obj).addClass("txt").css("width", width - 17).attr("readonly", "readonly");
	if(this.className == 'fileUpload') {
		$(obj).attr("onclick", "");//����ǵ����ı���,ȥ��ԭʼ�¼�
	}
	td_11.appendChild(obj);
	
	//�ϴ���ť
	var td_12 = document.createElement("td");
	var ifm = document.createElement("iframe");
	$(ifm).attr({
	   width:16,height:11,frameborder:0,title:'�ϴ��ļ�',scrolling:"no",
	   src:fastDFS_path+'/admin/commonModule/fastdfs/singleUpload.jsp?eId='+options.eId+"&sTableName="+sTableName+"&fileSize="+maxUpSize+"&extension="+limitReg+"&isToLocal="+isToLocal+"&directory="+directory
	 });
	$(ifm).css({height:11});
	td_12.appendChild(ifm);
	$(tr_1).append(td_11).append(td_12);
	
	//����ļ���ť
	var tr_2 = document.createElement("tr");
	var td_22 = document.createElement("td");
	with(td_22.style){textAlign="left";backgroundColor="transparent";}
	var cleanSpan=document.createElement("span");
	$(cleanSpan).html("&nbsp;&nbsp;&nbsp;&nbsp;").addClass("cleanBtn").attr("title","���");//��span���¼�ԭinput���ֵ
	
	//��span���¼�ԭinput���ֵ
	$(cleanSpan).bind("click", function(){
	    commonUtil.jqFileUpload_cleanFile(options.eId);
	});
	
	$(td_22).append(cleanSpan);
	$(tr_2).append(td_22);
	$(tbody).append(tr_1).append(tr_2);
	$(table).append(tbody);
	$(div).append(table);
	text.parentNode.replaceChild(div,text);//��ԭ�Ŀ���ɵ�ǰ��span
	if (!IsEmpty(options.afterUpload)) {
		$(window).data("afterUpload_"+options.eId,options.afterUpload);//���ϴ��ص�������������
	}
	if (!IsEmpty(options.afterClean)) {
		$(window).data("afterClean_"+options.eId,options.afterClean);//����ջص�������������
	}
};

commonUtil.jqFileCallBack = function(eId, jsonData){
    var obj = document.getElementById(eId);
	if(obj){
			var display = jsonData.oldFileName; 
			var displayName = $(obj).attr("displayName");
	       	if(displayName == "serverFileName"){//��������ļ�����
	       		display = jsonData.newFileName;
	       	}else if(displayName == "serverFileDir"){//������ļ�ȫ·��
	       		display = jsonData.fileId;
	       	}
	       	$(obj).val(display);
	       	$(obj).attr(jsonData);
	       	
	       	var tableIdObj = $(window).data("fileUploadTableId_"+eId);//ģ���ж�Ӧ����������
			var type = typeof(tableIdObj);//string,object,function
			var iTableId = "";
			if(type == "string"){
				iTableId = tableIdObj;
			}else if(type == "object"){
				iTableId = document.getElementById(tableIdObj.tId).value;
			}else if(type == "function"){
				iTableId = tableIdObj.call(this);
			}
		    jsonData.tableId = iTableId; //ҵ������ID
		    jsonData.tableName = $(obj).attr("sTableName");//ҵ�����
		    jsonData.sTableName = $(obj).attr("sTableName");//ҵ�����
	        jsonData.serverPath = jsonData.fileId;
	        
	       	var isSaveToDB = $(obj).attr("isSaveToDB");
	       	if(isSaveToDB == 1 || isSaveToDB == '1'){ //�Ƿ����
				var path = getFullPath();
				//�����ϴ��ļ���Ϣ�����ݿ�
				AjaxRequest.doRequest(null, path + "/fastDfs/fastDfs!addUpload.action?nd="+new Date(), jsonData, function (backData) {
					if (backData != 0) {//�ɹ�
						jsonData.iFileId = backData; 
						jsonData.fId = backData; //��������id
					}
				},false);
			}
	       
	       	//�ص�����
	       	var afterUpload = $(window).data("afterUpload_"+eId);
			if (!IsEmpty(afterUpload)) {
				//�ص�
				afterUpload.call(this, jsonData);
			}
	}
};

commonUtil.jqFileUpload_getSingleFileObj = function(iTablePKId, sTableName, cSvrFilePath)
{//��ȡ��������
	var jsonData = null;
	AjaxRequest.doRequest(null, path+'/fastDfs/fastDfs!getUploadList.action',
			{iTablePKId:iTablePKId, sTableName:sTableName, cSvrFilePath:cSvrFilePath, pageNo:1, limit:3000},function(backData){
		jsonData = decode(backData);
		if(jsonData[0]){
			jsonData = jsonData[0]; 
		}
	}, false);
	return jsonData;
};
/***************************fileUpload����**************************/

/***************************digit��ֵ��*****************************/
/*
option��һ��js����,
�����а�����������:
eId : ָ��Ҫ�����ֵ���Ԫ��id
limit : ��ֵ�����Ӽ��ٵĲ���
dataType : 
max : ���������ֵ
min : ������С��ֵ
*/
commonUtil.initDigit = function(option)
{
/*******************************�ڲ�������ʼ************************************/
	/*
	author:tanjianwen
	2012-1-9
	��ֵ��ֻ������������
	�˹���������jquery
	*/
	//var digit_arr = [];
	//var digit_id = 1;
	//�Ƿ��ܼ�
	function digit_isFunKey(code) {   
	  //  8 --> Backspace   
	  // 35 --> End   
	  // 36 --> Home   
	  // 37 --> Left Arrow   
	  // 39 --> Right Arrow   
	  // 46 --> Delete   
	  // 112~123 --> F1~F12   
	  var funKeys = [8, 37, 39, 46];   
	  for(var i = 0; i < funKeys.length; i++) {   
	    if(funKeys[i] == code) {   
	      return true;   
	    }   
	  }   
	  return false;   
	}
	//��¼ÿ����ֵ���ڼ�ʱ��ʱ���е�ֵ(�ڷ�IE�������ʹ��)
	function digit_setValue(digit){
		var isHave = false;
		for (var i = 0;i < commonUtil.digit_arr.length;i++) {
			if (commonUtil.digit_arr[i].id == $(digit).attr("digitId")) {
				commonUtil.digit_arr[i].value = $(digit).val();
				isHave = true;
			}
		}
		if (!isHave) {
			commonUtil.digit_arr.push({id:$(digit).attr("digitId"), value:$(digit).val()});
		}
	}
	//��ȡÿ����ֵ���ڼ�ʱ��ʱ���е�ֵ(�ڷ�IE�������ʹ��)
	function digit_getValue(id){
		for (var i = 0;i < commonUtil.digit_arr.length;i++) {
			if (commonUtil.digit_arr[i].id == id) {
				return commonUtil.digit_arr[i].value;
			}
		}
		return "";
	}
	function digit_check(e) {
		  var target = e.srcElement || e.target;  
		  var input=$(target); 
		  e.stopPropagation();
		  var k = e.keyCode == 0 ? e.charCode : e.keyCode;
		  if (digit_isFunKey(k) && String.fromCharCode(k) != '.') {
		  	return true;
		  }
		  var c = String.fromCharCode(k);
		  var boo=c == '-'&&((!IsEmpty(input.attr("min"))&& parseFloat(input.attr("min"))<0));
		  if(target.value.length == '' && boo) {
		    return true;
		  }
		  if(c >= '0' && c <= '9') {
		  	if(document.selection && document.selection.createRange().text){//��ѡ���ı�ʱ
				document.selection.clear();	
			}
			var num = parseFloat(target.value + c);
			if ((!IsEmpty($(target).attr("maxLength")) && $(target).attr("maxLength") != -1 && num >= Math.pow(10,$(target).attr("maxLength"))) ||
			   (!IsEmpty($(target).attr("max")) && num > parseFloat($(target).attr("max"))) ||
		       (!IsEmpty($(target).attr("min")) && num < parseFloat($(target).attr("min")))) {
			       //�����������С����Сֵ�����ֵʱ�������ó�Ĭ��ֵ tangyj 2013-03-23
			       if((!IsEmpty($(target).attr("min")) && num < parseFloat($(target).attr("min")))){
			       		$(target).val($(target).attr("min"));
			       }
			       if((!IsEmpty($(target).attr("max")) && num > parseFloat($(target).attr("max")))){
			       		$(target).val($(target).attr("max"));
			       }
			       //end
				   return false;
		    }
		    
		  } else if (c == '.' && target.value.indexOf('.') < 0) {
		  	 if (!IsEmpty($(target).attr("dataType")) && $(target).attr("dataType") == "digit") {
		  	 	return false;//�������������ֻ��������,��������"."
		  	 }
		  	return true;
		  } else if (boo) {
		  	return true;
		  } else{
		  	return false;
		  }
		  return true;
		}
		function digit_keyup(e){
			var target = e.srcElement || e.target;
			var input = $(target);
			//if (input.val() == "+" || input.val() == "-") return;
			//tangyj 2013-5-7 �滻�������ʵ�ַ�ʽ
			if(input.val() == "-" && ((!IsEmpty(input.attr("min"))&& parseFloat(input.attr("min"))<0))) return;
			if(input.val() == "+" && ((!IsEmpty(input.attr("max"))&& parseFloat(input.attr("max"))>0))) return;
			
			var num = input.val() == "" || isNaN(input.val()) ? isNaN(parseFloat(input.val())) ? "" : parseFloat(input.val()) : input.val();
			if (!IsEmpty(input.attr("maxLength")) && $(target).attr("maxLength") != -1 && parseFloat(num) >= Math.pow(10,input.attr("maxLength"))) {
				num = Math.pow(10,input.attr("maxLength")) - 1;
			}
			num=max_min(input,num);
			if (!IsEmpty(input.attr("decimals")) && input.val().split(".").length > 1 &&
		  			  input.val().split(".")[1].length > input.attr("decimals")){
		  		num = parseFloat(input.val()).toFixed(input.attr("decimals"));
		  		input.val(num);
		  	}
			if (isNaN(input.val())) {
				input.val(num);
			} else if (input.val() != num.toString()) {
				input.val(num);
			}
			
		}
		function digit_up(obj){
			var input = $(obj).parent().prev().children(":text")[0];
			input = $(input);
			if (input.val() == "") input.val(0);
			var num = parseFloat((parseFloat(input.val()) + parseFloat(input.attr("limit"))).toFixed(2));
			if (!IsEmpty(input.attr("maxLength")) && input.attr("maxLength") != -1 && parseFloat(num) >= Math.pow(10,input.attr("maxLength"))) {
				num = Math.pow(10,input.attr("maxLength")) - 1;
			}
			num=max_min(input,num);
			input.val(num);
		}
		function digit_down(obj){
			var input = $(obj).parent().parent().prev().find(":text")[0];
			input = $(input);
			if (input.val() == "") input.val(0);
			var num = parseFloat((parseFloat(input.val()) - parseFloat(input.attr("limit"))).toFixed(2));
			num=max_min(input,num);
			input.val(num);
		}
		function digit_handle(){
			$(".Digit .txt").each(function(){
				if (IsEmpty($(this).attr("digitId"))) {
					$(this).attr("digitId", "digit"+(commonUtil.digit_id++).toString());
				}
				if (digit_getValue($(this).attr("digitId")) != $(this).val()) {//���ʱ��ε�ֵ�����ڵ�ǰ��ֵ,����keyup��֤
					digit_keyup({target:this});
				}
				digit_setValue(this);
			});
		}
		function max_min(input,num){
		   if (!IsEmpty(input.attr("max")) && parseFloat(num) > parseFloat(input.attr("max"))) {
				num = input.attr("max");
			}
			if (!IsEmpty(input.attr("min")) && parseFloat(num) < parseFloat(input.attr("min"))) {
				num = input.attr("min");
			}
			return num;
		}
		
/*******************************�ڲ���������************************************/
	if (option.eId == undefined || option.eId == null)return;
	$(input).val("");
	var input = typeof(option.eId)=="string" ? document.getElementById(option.eId) : option.eId;
	if ($(input).attr("class") == "txt") return;
	try{
		var tableNode = input.parentNode.parentNode.parentNode.parentNode;
		if(tableNode && $(tableNode).attr("class") == "Digit"){
			return;//���ɽ���������Digit��classʱ������ֱ�ӷ���
		}
	}catch(e){
	}
	if($(input).attr("class")==""){
		$(input).addClass("Digit");//����Digit��class������ѡ������ȡ
	}
	var jqInput = $(input);
	if (!IsEmpty(option.limit))
		jqInput.attr("limit", option.limit);
	else
		jqInput.attr("limit", 1);
	if (!IsEmpty(option.max))
		jqInput.attr("max", option.max);
	if (!IsEmpty(option.min))
		jqInput.attr("min", option.min);
	if (!IsEmpty(option.dataType)) {
		jqInput.attr("dataType", option.dataType);}
	if (!IsEmpty(option.decimals))
		jqInput.attr("decimals", option.decimals);
	
	var span = document.createElement("span");
	var table = document.createElement("table");//����һ��div�������ı����span��ť
	$(table).addClass("Digit").attr("cellpadding", "0").attr("cellspacing", "0");
	$(table).css({width:"auto"});
	var tbody = document.createElement("tbody");
	
	var tr_1 = document.createElement("tr");
	var td_11 = document.createElement("td");
	$(td_11).attr("rowspan", "2");
	var obj=input.cloneNode(true);//��������һ��inp,����span��
	var isDisabled = (option.disabled == true || option.disabled=="true");
	$(obj).attr("disabled",isDisabled);
	//tangyj 2013-04-27
	//��ԭ��input���widthֵ���õ�oldWidth�����ϣ������ڴ��ڸı�ʱʵʱ������
	$(obj).attr("oldWidth",$(input).css("width"));
	
	$(span).css({"display":$(obj).css("display"),width:"auto"});
	$(obj).addClass("txt").css("width", getElementWidth(input) - 16)
						  //tangyj 2013-5-7 ע�����漸���¼���������Ȼ��IE�����������������޷�ճ������ֵ��֤���ö�ʱ���ķ�ʽ
						  .keypress(function(event){event.stopPropagation();return digit_check(event);})
						  .keyup(function(event){event.stopPropagation();digit_keyup(event);});
						  //.bind("paste", function(){digit_check(event);});
	if (GetBrowserType().indexOf("IE") >= 0){//IE�����
		//���θþ䣬���óɶ�ʱ���ķ�ʽ����Ȼ���ܶ�ѡ�е����ֽ����޸� tangyj 2013-03-23
		$(obj).bind("propertychange", function(){digit_keyup({target:this});});
		//$(obj).bind("propertychange", function(){digit_handle()});
		//window.setInterval(digit_handle, 50);
	}else {//���������
		//window.setInterval(digit_handle, 50);
	}
	$(td_11).append(obj);
	
	var td_12 = document.createElement("td");
	var upSpan=document.createElement("span");
	$(upSpan).addClass("upBtn").attr("title","�ϵ�").html("&nbsp;&nbsp;&nbsp;&nbsp;");//��span���¼�ԭinput���ֵ
	td_12.appendChild(upSpan);
	$(td_12).append(upSpan);
	$(tr_1).append(td_11).append(td_12);
	
	var tr_2 = document.createElement("tr");
	var td_22 = document.createElement("td");
	var downSpan=document.createElement("span");
	$(downSpan).addClass("downBtn").attr("title","�µ�").html("&nbsp;&nbsp;&nbsp;&nbsp;");//��span���¼�ԭinput���ֵ
	if ($(obj).attr("disabled") == false) {
		$(upSpan).bind("click", function(){
			if ($(obj).attr("disabled") == true) return;
			digit_up(this);
			$(obj).change();//add by gt
		});//��span���¼�ԭinput���ֵ
		$(downSpan).bind("click", function(){
			if ($(obj).attr("disabled") == true) return;
			digit_down(this);
			$(obj).change();//add by gt
		});
	}
	$(td_22).append(downSpan);
	$(tr_2).append(td_22);
	$(tbody).append(tr_1).append(tr_2);
	$(table).append(tbody);
	$(span).append(table);
	input.parentNode.replaceChild(span,input);//��ԭ�Ŀ���ɵ�ǰ��span
	if ($(span).next().html() == "*")
	$(span).next().css("line-height", "2");
};
/***************************digit����******************************/

/***************************menuButton jquery����******************/
commonUtil.initMenuButton = function(option)
{
/*******************************�ڲ�������ʼ************************************/
	//����jquery�����Ľṹ
	function menu_buildMenu(option){
		//����children���ԣ����������˵�
		if(!IsEmpty(option.json)){
			var rootDiv = menu_createDiv({iRight:option.vId, style:option.dStyle});
			menu_addChild({parentDiv:rootDiv, children:option.json, firstChildLevel:true});
			document.body.appendChild(rootDiv);
		}
	}
	function menu_buildA(option){
		
		var targetEle = document.getElementById(option.eId);
		for (var i = 0;i < option.json.length;i++){
			//���ӷָ���
			if(i > 0){
				var spn = document.createElement("span");
				$(spn).addClass("spacer").css("display", "inline");
				$(targetEle).append(spn);
			}
			
			var aJson = option.json[i];
			var a = document.createElement("a");
			a.id = aJson.iRight;
			a.innerHTML = aJson.sRightName;
			$(a).attr("iconCls", aJson.sIcon);
			if (typeof aJson.sURL != "undefined" && aJson.sURL != ""){$(a).attr("href", aJson.sURL);}
			if (typeof aJson.clickEvent != "undefined" && aJson.clickEvent != null) {
				$(a).bind("click", aJson.clickEvent);
			}
			$(a).addClass("easyui-menubutton").attr("menu","#"+aJson.menu);
			$(targetEle).append(a);
		}
	}
	//����div
	function menu_createDiv(opt){
		var div = document.createElement("div");
		if (IsEmpty(opt))
			return div;
		if (!IsEmpty(opt.isSeq) && opt.isSeq){//����Ƿָ���
			$(div).addClass("menu-sep");
			return div;
		}
		if (!IsEmpty(opt.iRight))//�����id
			$(div).attr("id", opt.iRight);
		if (!IsEmpty(opt.sRightName))//����нڵ���
			$(div).html(opt.sRightName);
		if (!IsEmpty(opt.sIcon))//�����ͼ��
			$(div).attr("iconCls", opt.sIcon);
		if (!IsEmpty(opt.clickEvent))//����е���¼�
			$(div).bind("click", opt.clickEvent);
		if (!IsEmpty(opt.style))//�������ʽ
			$(div).attr("style", opt.style);
		if (!IsEmpty(opt.sURL) && opt.sURL != "")//���������
			$(div).html("<a href='"+opt.sURL+"' style='display:-moz-inline-box;display:inline-block;width:100%;height:100%'>"+opt.sRightName+"</a>");
		else if (!IsEmpty(opt.sRightName))
			$(div).html(opt.sRightName);
		return div;
	}
	//����ӽڵ�
	function menu_addChild(option){
		
		if(IsEmpty(option.children)){return;}
		
		if (!IsEmpty(option.firstChildLevel) && option.firstChildLevel){
			for (var i = 0;i < option.children.length;i++) {
				var item = option.children[i];
				if (!IsEmpty(item.isSeq) && item.isSeq) {//����Ƿָ���
					option.parentDiv.appendChild(menu_createDiv({isSeq:true}));
					continue;
				}
				if (!IsEmpty(item.children) && item.children.items.length > 0) {//���ӽڵ�
					var pDiv = menu_createDiv();
					var span = document.createElement("span");
					span.innerHTML = item.sRightName;
					pDiv.appendChild(span);
					menu_addChild({parentDiv:pDiv, children:item.children});
					option.parentDiv.appendChild(pDiv);
					continue;
				}
				//��ͨ�ڵ�
				option.parentDiv.appendChild(menu_createDiv(item));
			}
		} else {
			var div = menu_createDiv({style:option.children.style});
			for (var i = 0;i < option.children.items.length;i++) {
				var item = option.children.items[i];
				if (!IsEmpty(item.isSeq) && item.isSeq) {//����Ƿָ���
					div.appendChild(menu_createDiv({isSeq:true}));
					continue;
				}
				if (!IsEmpty(item.children) && item.children.items.length > 0) {//���ӽڵ�
					var pDiv = menu_createDiv();
					var span = document.createElement("span");
					span.innerHTML = item.sRightName;
					pdiv.appendChild(span);
					menu_addChild({parentDiv:pDiv, children:item.children});
					div.appendChild(pDiv);
					continue;
				}
				div.appendChild(menu_createDiv(item));
			}
			option.parentDiv.appendChild(div);
		}
	}
/*******************************�ڲ���������************************************/
	if (IsEmpty(option)) return;
	menu_buildA(option);
	for (var i = 0;i < option.json.length;i++) {
		var aJson = option.json[i];
		var aOption = {aId:aJson.iRight, vId:aJson.menu, dStyle:aJson.dStyle, json:aJson.children};
		menu_buildMenu(aOption);
		$('#'+aJson.iRight).menubutton({});
		if(IsEmpty(aJson.children)){
			//����û��children���Ե�һ����ť�ұߵ�����ͼ��
			$('#'+aJson.iRight).find(".m-btn-downarrow").hide();
		}else{
			//��children���Ե�һ����ť�����������˵���ͼ����ʽ
			$('#'+aJson.menu).children().each(function(){
				$(this).find(".menu-icon").css({"height": "20px", "top": "0px"});
			});
		}
		$('#'+aJson.iRight).find(".l-btn-text").css({"height": "22px", "line-height": "22px"});
		$('#'+aJson.iRight).find(".m-btn-downarrow").css("line-height", "22px");
	}
	
	$("#"+option.eId).children(".easyui-menubutton").children("span.l-btn-left").css({"padding-top": "0px","padding-bottom": "0px"});
	$("#"+option.eId).css({"height": "25px", "text-align": "left"}).addClass("toolbar");
};
//ת�������� ID��������iRightId,��ID��������iParentId
commonUtil.initMenuButton.getTreeJson = function(array){
	
	return menu_getTreeData(array);
	
	/*******************************�ڲ�������ʼ************************************/
	//����json���飬������������ iRightId:����ID, iParentId:����ID
	function menu_getTreeData(array){
	    var tree = [];
	    for(var i = 0; i < array.length; i++){
	        var obj = array[i];
	        if(obj == null){
	            continue;
	        }
	        menu_setChild(array, obj);    
	    }
	    
	    for(var j=0; j<array.length; j++){
	        var obj = array[j];
	        if(obj == null){
	            continue;
	        }
	        if(typeof(obj) == "string" && obj == "-"){
	            continue;
	        }
	        
	        //�����滻
	        obj.iRight = obj.iRightId
	        obj.sRightName = obj.text;
	        obj.sIcon = obj.bodyStyle;
	        obj.clickEvent = obj.handler;
	        obj.menu = obj.iRightId+"Menu";
	        delete obj.iRightId;
	        delete obj.text;
	        delete obj.bodyStyle;
	        delete obj.handler;
	        
	        tree.push(obj);
	    }
	    return tree;
	}
	
	//���ýڵ㣬����children����
	function menu_setChild(array, obj){
	    var sId = obj.iRightId;
	    for(var i=0; i<array.length; i++){
	        var temp = array[i];
	        if(temp == null){
	            continue;
	        }
	        
	        if(typeof(temp) == "string" && temp == "-"){
	            continue;
	        }
	        
	        var pId = temp.iParentId;
	        if(sId == pId){
	            menu_setChild(array, temp);
	            
	            var arr = obj.children;
	            if(arr == null || arr == undefined){
	                arr = new Array();
	            }
	            
	            temp.iRight = temp.iRightId;
	            temp.sRightName = temp.text;
	            temp.sIcon = temp.bodyStyle;
	            temp.clickEvent = temp.handler;
	            temp.menu = temp.iRightId+"Menu";
	            delete temp.iRightId;
	            delete temp.text;
	            delete temp.bodyStyle;
	            delete temp.handler;
	            
	            arr.push(temp);
	            obj.children = arr;
	            array[i] = null;
	        }    
	    }
	}
	/*******************************�ڲ���������************************************/
};

/***************************menuButtonjquery��������**************/

/***************************moreValue��ֵ��*****************************/
/*
option��һ��js����,
�����а�����������:
mId : ָ��Ҫ������ֵ��input���Ԫ��id
setValue : Ҫ�������ֵ
*/
commonUtil.setMoreValue = function(option)
{
	if (IsEmpty(option)) return;
	$('#'+option.mId).val(option.setValue);
};

/*
option��һ��js����,
�����а�����������:
mId : ָ��Ҫ������input���Ԫ��id
*/
commonUtil.changeMoreValue = function(option)
{
	function openMoreValueDialog(eventOption){
			var obj=eventOption.mId;
	        var baseWin=getTopWin(window);//window.top?window.top:window;
	       
	       /**�ж��Ƿ���Ҫ��ʾAPI�����Լ���ȡAPI�ı�*/
	        var desc = "";
	        var isAPI = eventOption.isAPI;
	        if((isAPI+"")!= "false"){isAPI = true;}
	        if(isAPI && eventOption.apiText){ 
	        	desc = eventOption.apiText;
	        }else if(isAPI && eventOption.apiFun && typeof(eventOption.apiFun)=="function"){
	        	try{desc = eventOption.apiFun(eventOption);}catch(e){} 
	        }
	        if(!desc){//û��API�ı�����߾Ͳ���ʾAPI��
	           isAPI = false;
	        }
	        
	        if(!baseWin.document.getElementById("event_edit")){
				var apiArea = "";
				var h = "100%";
				//API��ʾ��
				apiArea = "<textarea style='overflow-x:hidden;width:100%;height:30%;border-left:0px;border-top:2px solid #C4E6E6;background:#EEEEEE;padding-left:10px;padding-top:5px;' id='api_area' readonly='readonly'></textarea>";
				
				var html="<div id='event_edit' style='overflow:hidden'><div id='event_layout' fit='true'>"+
		        "<div region='center' style='border:0;overflow:hidden;' ><textarea style='width:100%;height:"+h+";border:0px' id='event_edit_area'></textarea>"+apiArea+"</div>"+
		        "<div region='south' style='height:40px;border:0'>"+
				"<table width='100%' height='100%' class='formbasic'><tr>"+
				"<td style='text-align:center;border:0px;'><ul class='btn_hover' style='margin-left:auto;margin-right:auto;'>"+
				"<li onClick='window.event_edit_conform=1;$(\"#event_edit\").window(\"close\")'><a href='javascript:void(0)' onclick='return false'><span><div class='ok'>ȷ��</div></span></a></li>"+
				"<li onClick='$(\"#event_edit\").window(\"close\")'><a href='javascript:void(0)'  onclick='return false'><span><div class='no'>ȡ��</div></span></a></li>"+
				"</ul></td></tr></table></div></div></div>";
	            baseWin.$("body").append(html);
	        }
	        var divOption = {
	            baseWin:baseWin,
				title:eventOption.dialogTitle,
				id:"event_edit",
				width: eventOption.dialogWidth,
				height:eventOption.dialogHeight,
				zIndex: 9000,
				draggable: true, //�϶�
				resizable: true, //�ı��С
				modal: true, //��̨ҳ��ɱ༭
				closed: false, //�Ƿ�رգ�
				minimizable: false,//��С����ť
				maximizable: true,//��󻯰�ť
				closable: true, //�رհ�ť
				collapsible: false, //������ť 
				nowrap: false,
				onClose:function(){
				    if(baseWin.event_edit_conform){
				       var v1 = obj.value;
				       var v2 = baseWin.$("#event_edit_area").val();
					   obj.value = v2;
					   if(eventOption.onChange && (v1 != v2) ){eventOption.onChange(obj);}
					}
					baseWin.$("#event_edit_area").val("");
					baseWin.event_edit_conform=0;
					
				}
			};
	        showJqueryWindow(divOption);
	        baseWin.$("#event_edit_area").val(obj.value?obj.value:(eventOption.defaultEdit));//obj.id+":function(){}"
	        baseWin.$("#event_layout").layout({});
	        if(isAPI){
	           baseWin.document.getElementById("event_edit_area").style.height="70%";
	           baseWin.$("#api_area").val(desc.replace(/^(\s|\u00A0)+/,'').replace(/(\s|\u00A0)+$/,''));
	           baseWin.$("#api_area").show();
	        }else{
	           baseWin.document.getElementById("event_edit_area").style.height="100%";
	           var api_area = baseWin.document.getElementById("api_area");
	           if(api_area){baseWin.$("#api_area").hide();}
	        }
	}

	var checkInputId = (typeof(option.mId)=="string")?$('#'+option.mId):$(option.mId);
	if(checkInputId.length>0){
		checkInputId.each(function() {
			var thisinput = this.cloneNode();
			var clickFunc = $(this).attr("onclick");//�ÿ�����ť����¼�
			var onChange = $(this).attr("onChange");//��onChange��ť����¼�
			var thisId = thisinput.id ? thisinput.id : '';
			var thisName = thisinput.name?thisinput.name : '';
			var thisValue = this.value?this.value:'';
			var atexteare = document.createElement('textarea');
			
			//tangyj 2013-04-27
			//��ԭ��input���widthֵ���õ�oldWidth�����ϣ������ڴ��ڸı�ʱʵʱ������
			$(atexteare).attr("oldWidth",$(this).css("width"));
			
			//��������
			atexteare.id = thisId;
			atexteare.name = thisName;
			atexteare.value = this.value;
			$(atexteare).css("overflow",'hidden');
			$(atexteare).attr("dialogTitle",$(this).attr("dialogTitle"));
			$(atexteare).attr("dialogWidth",$(this).attr("dialogWidth"));
			$(atexteare).attr("dialogHeight",$(this).attr("dialogHeight"));
			$(atexteare).attr("defaultEdit",$(this).attr("defaultEdit"));
			$(atexteare).attr("isAPI",$(this).attr("isAPI"));
			$(atexteare).attr("apiText",$(this).attr("apiText"));
			$(atexteare).attr("apiFun",$(this).attr("apiFun"));
			
			var adiv = document.createElement('span');
			//��ȡ�ı������ʽ��width���ԣ������õ�span��Ĭ��Ϊ100%
			if(IsEmpty($(thisinput).css("width"))){
				$(atexteare).css("width",105);
			}else{
				$(atexteare).css("width",getElementWidth(this)-15);
			}
			var atag = document.createElement('a');
			atag.onclick = clickFunc;
			
			function openMore(){
				var eventOption = {};
				eventOption.mId = atexteare;
				eventOption.id = atexteare.id;
				eventOption.onChange = option.onChange ? option.onChange : onChange;
				
				eventOption.dialogTitle = option.dialogTitle ? option.dialogTitle : (IsEmpty($(atexteare).attr("dialogTitle")) ? "�༭����" : $(atexteare).attr("dialogTitle"));
				eventOption.dialogWidth = option.dialogWidth ? parseInt(option.dialogWidth) : (parseInt(IsEmpty($(atexteare).attr("dialogWidth")) ? "450" : $(atexteare).attr("dialogWidth")));
				eventOption.dialogHeight = option.dialogHeight ? parseInt(option.dialogHeight) : (parseInt(IsEmpty($(atexteare).attr("dialogHeight")) ? "350" : $(atexteare).attr("dialogHeight")));
				eventOption.defaultEdit = option.defaultEdit ? option.defaultEdit : (IsEmpty($(atexteare).attr("defaultEdit"))?"" : $(atexteare).attr("defaultEdit"));
				
				eventOption.isAPI = option.isAPI ? option.isAPI : $(atexteare).attr("isAPI");
				eventOption.apiText = option.apiText ? option.apiText : $(atexteare).attr("apiText");
				
				var fntxt = $(atexteare).attr("apiFun");
				
				eventOption.apiFun = option.apiFun ? option.apiFun :(fntxt ? window[fntxt] : "");
				openMoreValueDialog(eventOption);
			}
			//Ϊͼ�갴ť���¼�
			$(atag).unbind("click").bind("click",openMore);
			var keepTextEvent = IsEmpty($(thisinput).attr("keepTextEvent")) ? false : $(thisinput).attr("keepTextEvent");
			if(option.keepTextEvent){//����ȡ��ʼ��������ֵ
				keepTextEvent = option.keepTextEvent;
			}
			if(!(keepTextEvent=="false"||keepTextEvent==false)){
				$(atexteare).unbind("click").bind("click",openMore);
				if(clickFunc){$(atexteare).bind("click",clickFunc);}
			}
			
			
			var table = document.createElement('table');
			$(table).addClass("moreVal").attr("cellpadding", "0").attr("cellspacing", "0");
			
			//��span�µ�table��width���ó�100%
			var tbody = document.createElement("tbody");
			var tr_1 = document.createElement('tr');
			var td_11 = document.createElement('td');
			$(atexteare).addClass("moreVal txt");
			$(td_11).append(atexteare);
			var td_12 = document.createElement('td');
			$(td_12).append(atag);
			//��ͼƬ�������ù̶�����
			$(tr_1).append(td_11).append(td_12);
			$(tbody).append($(table).append(tr_1));
			
			adiv.appendChild(table);
			this.parentNode.replaceChild(adiv,this);
			$(adiv).css({"display":$(thisinput).css("display")});
		});
	 }
};
/***************************moreValue��ֵ�����**************/

/***************************�ɰ�*********************************/
/*
�ɰ�,��������Ƕ���,option���������������:
mId:�ɰ�div��id(������ҳ����û��,���߻�������id����)
container:�ɰ���������(ָ���ɰ帲�ǵķ�Χ����Ҫ)
*/
commonUtil.showMask = function(option)
{
/*******************************�ڲ�������ʼ************************************/
	  //�������룬��ʾ�ɰ�
	  function mask_show(option) {
	  	var mask = document.getElementById(option.mId);
	  	mask.style.width = option.container.clientWidth;
	  	mask.style.height = option.container.clientHeight;
	  	mask.style.display = "block";
	  }
/*******************************�ڲ���������************************************/
	var mask = document.getElementById(option.mId);
	if (IsEmpty(mask)) {
		mask = document.createElement("div");
		mask.id = option.mId;
		document.body.appendChild(mask);
	}
	mask.className = "Mask";
	mask_show(option);
};

commonUtil.hideMask = function(id)
{
	document.getElementById(id).style.display = "none";
};
/***************************�ɰ����*****************************/

/***************************ͨ������*****************************/
commonUtil.showDetail = function() 
{
	//ɾ������ҳ���в�����ʾ��Ԫ��
	function detail_cleanSomeThing() {
		//��'����'��'ȡ��'��ťɾ��
		$("table").each(function(){
			if ($(this).find(".btn_hover")[0] != undefined) {
				//$(this).remove();
			} else {
				if (this.className == 'formbasic') {
					//$(this).height("100%");
				}
			}
		});
	}
	
	detail_cleanSomeThing();
	$(".formbasic").css("height","auto");
	$("div").each(function(){
		if(this.region != null && this.region == 'center'){
			$(this).css("overflow", "auto");
			$(this).addClass("defaultColor");
		}
	});
	var inputs = $(':input');
	for (var i = 0;i < inputs.length;i++) {
		var input = inputs[i];
		//�����򲻴���
		if (input.type == "hidden") {
			continue;
		}
		var parent = $(input).parent();
		//htc������Ĵ���
		/*if (input.parentElement.className == "PickList") {
			   parent = parent.parent();
		}*/
		//�Զ���������򼰿���ȡ����Ҫ�����ҵ�th����
		var th=input.parentElement;
		while(th.nodeName!="TH"){th=th.parentElement;parent = $(th);}
		
		//��ȡinput��ֵ
		var value = $(input).val();//.replace(/[\n,\r]/g,"<br>");//�Ի��д���
		if(value) {
			value=value.replace(/[\<]/g, "&lt;");
			value=value.replace(/[\>]/g, "&gt;");
			value=value.replace(/[\n,\r]/g,"<br>");
		}
		if (input.type == "select-one") {//�����select��ǩ��Ҫ��ȡ��Ӧ���ı�ֵ
			for (var j = 0;j < input.options.length;j++) {
				if (input.options[j].value == value) {
					value = input.options[j].innerHTML;
					break;
				}
			}
		}
		//��ֵ����td��
		parent.css("font-weight", "normal");
		parent.html(value);
	}
};

//��ʼ������ҳ��
/*
*/
commonUtil.initDetail = function(data) 
{
/*******************************�ڲ�������ʼ************************************/
	function detail_createTable(){
		var table = document.createElement("table");
		$(table).attr("width","100%")//.attr("height","100%")
				.attr("cellpadding","0").attr("cellspacing","0")
				.attr("border","0").addClass("formbasic").css("height","auto");
		return table;
	}
	function detail_createTR(){
		return document.createElement("tr");
	}
	function detail_createTD(column){
		//column.text = IsEmpty(column.text) ? "" : column.text.replace(/[\n\r]/g,"<br>&nbsp;&nbsp;"); 
		var td;
		if (IsEmpty(column.isKey) || !column.isKey){
			td = document.createElement("th");
		} else { 
			td = document.createElement("td");
		}
		$(td).css("vertical-align", "middle");
		if (!IsEmpty(column.width))
			$(td).attr("width", column.width);
		if (!IsEmpty(column.height)){//$(td).attr("height", column.height);
			}
		if (!IsEmpty(column.colspan))
			$(td).attr("colspan", column.colspan);
		if (!IsEmpty(column.rowspan))
			$(td).attr("rowspan", column.rowspan);
		if (!IsEmpty(column.isKey) && column.isKey) {
			//$(td).html(column.text+"&nbsp;");
			$(td).html(((column.text+"").replace(/ /g,"")=="null"?"&nbsp;":column.text.replace(/ /g,"&nbsp;&nbsp;").replace(/\r/g,"<br>").replace(/\n/g,"<br>")));
		} else {
			$(td).css("font-weight", "normal");
			$(td).html("&nbsp;&nbsp;"+(column.text+"").replace(/ /g,"")=="null"?"&nbsp;":column.text);
		}
		return td;
	}
/*******************************�ڲ���������************************************/
	$(document.body).addClass("easyui-layout").addClass("defaultColor");
	var div = document.createElement("div");
	var tb = detail_createTable();
	//$(tb).css("table-layout", "fixed");
	var tbody = document.createElement("tbody");
	for (var i = 0;i < data.length;i++) {
		var tr = detail_createTR();
		$(tr).css("vertical-align", "middle");
		for (var j = 0;j < data[i].length;j++) {
			var column = data[i][j];
			tr.appendChild(detail_createTD(column));
		}
		tbody.appendChild(tr);
	}
	tb.appendChild(tbody);
	div.appendChild(tb);
	document.body.appendChild(div);
	$("div").css("overflow", "auto").css("width","100%").css("height","100%");
};

commonUtil.setDetailData = function(key, data)
{
	//var detail_dataArray = [];
	var isHave = false;
	for (var i = 0;i < commonUtil.detail_dataArray.length;i++) {
		if (commonUtil.detail_dataArray[i].key == key){
			commonUtil.detail_dataArray[i] = {key: key, data: data};
			isHave = true;
		}
	}
	if (!isHave) {
		commonUtil.detail_dataArray.push({key: key, data: data});
	}
};

commonUtil.getDetailData = function(key)
{
	for (var i = 0;i <commonUtil.detail_dataArray.length;i++) {
		if (commonUtil.detail_dataArray[i].key == key){
			return commonUtil.detail_dataArray[i].data;
		}
	}
	return null;
};
/***************************ͨ���������*************************/

/***************************Ԫ������Ӧ��С**********************/
//option�������������
//eId:������ӦԪ��id
//eType:������ӦԪ������,1��"htc"��htc�б�2��"flex"��flex�б�,3��jquery:jquery�б�
//rId:��������id(װ�ر�����ӦԪ�ص�����)
//width:��ȵ���ֵ
//height:�߶ȵ���ֵ
commonUtil.setWH = function(option)
{
	if (option == null && option == undefined){
		return;
	}
	var element = document.getElementById(option.eId);//��ȡ������ӦԪ�ض���
	var container = document.getElementById(option.rId);//��ȡ����
	var width = IsEmpty(option.width) ? 0 : option.width;//��ȵ���ֵ
	var height = IsEmpty(option.height) ? 0 : option.height;//�߶ȵ���ֵ
	
	container = IsEmpty(container) ? document.body : container;
	var eWidth = width == 'fixed' ? $(element).outerWidth() : $(container).outerWidth() + width;
	var eHeight = height == 'fixed' ? $(element).outerHeight() : $(container).outerHeight() + height;
	if (eWidth < 0 || eHeight < 0) {
		//simpleAlert('Ԫ�ؾ�������ֵ��,�߶ȺͿ�Ȳ���Ϊ����!');
		return;
	}
	if (option.eType == '1' || option.eType == 'htc') {
		element.setWidth(eWidth);
		element.setHeight(eHeight);
	} else if (option.eType == '2' || option.eType == 'flex') {
		element.width = eWidth;
		element.height = eHeight;
	} else if (option.eType == '3' || option.eType == 'jquery') {
		try {
			$(element).datagrid("resize", {height:eHeight,width:eWidth});
		} catch (err) {
			$(element).treegrid("resize", {height:eHeight,width:eWidth});
		}
	} else {
		$(element).width(eWidth);
		$(element).height(eHeight);
	}
};
/***************************Ԫ������Ӧ��С����**********************/

/***************************������ʾ��***************************/
/*
 * option����:
 * 	eId:Ԫ��ID
 * 	vId:��ű�ʶ(id)�������id
 * 	url:Զ�̻�ȡ���ݵ�����
 * 	data:�������ݶ���(��url��ѡһ)
 * 	dataColumns:��ʾ����������
 * 	selectColumn:ָ��ƥ����
 * 	selectColumnId:��ʶ�ֶ���
 * 	width:��ʾ����
 * 	height:��ʾ��߶�
 *  showHeader:�Ƿ���ʾ��ͷ,Ĭ��false
 *  customParam:�Զ������
 *  beforeInput:����ǰִ�к���������true����ִ�У�false�����������Ϊ�����customParam,δ��������ն���
 *  clean:�������ֵ��ִ�к���
 * 	callback:�ص����������Ϊ��ѯ�õ���data
*/
commonUtil.initPrompt = function(option)
{
	//var prompt_id = 1;//��ʾ��ID���ֱ�ʶ(һ��ҳ����ܶ����ʾ��)
	var timeOutObj; //setTimeout����
	setOption(option);
/*******************************�ڲ�������ʼ************************************/
	//����optionӳ���ϵ
	function setOption(option) {
		commonUtil.initPrompt.options[option.eId] = option;
	}
	//onKeyup�¼�ִ�з���
	function prompt_onKeyupFn(option){

		clearTimeout(timeOutObj);
		timeOutObj = setTimeout(function(){
			
			if (IsEmpty($("#" + option.eId).val())) {
				
				if (!IsEmpty(option.clean) && typeof(option.clean) == "function") {
					option.clean.call(this);
				}
				
				prompt_hiddenPrompt(option);
				return;
			}
			var div = document.getElementById(option.dId);
			$(div).show();
			
			if (IsEmpty(table)) {
				//����table������ӵ�div��
				var table = prompt_createTable(option);
				div.appendChild(table);
				//����datagrid
				$('#' + option.tId).datagrid({
					columns: [option.dataColumns],
					nowrap: true,
					fitColumns: true, width: option.width, height: option.height,
					rownumbers: false, singleSelect: true, 
					showHeader: IsEmpty(option.showHeader) ? false : option.showHeader,
					onClickRow: function(rowIndex, rowData){
						commonUtil.prompt_currentDataIndex = rowIndex;
						//prompt_selectData(option, null);
						prompt_setConfirmValue(option);
						prompt_hiddenPrompt(option);
					}
				});
			}
			
			prompt_filterData(option);//�������ݹ���
			prompt_initData(option);//��ʼ�����ݵ�datagrid��
			prompt_showPosition(option);//��ʾ��λ
		
		}, 500);
	}
	//����table
	function prompt_createTable(option) {
		var div = document.getElementById(option.dId);
		var table = document.createElement("table");
		table.id = option.tId;
		option.width = IsEmpty(option.width) ? $("#"+option.eId).outerWidth(): option.width;
		option.height = IsEmpty(option.height) ? 150 : option.height;
		div.style.offsetWidth = option.width;
		div.style.offsetHeight = option.height;
		table.style.offsetWidth = option.width;
		table.style.offsetHeight = option.height;
		return table;
	}
	//��ʼ������
	//var prompt_fullData;
	function prompt_initData(option){
		
		if (IsEmpty(option.data)) {
			prompt_lock(option);
			
			AjaxRequest.doRequest('', option.url, option.customParam, function(backData){
				var jsonData = Ext.util.JSON.decode(backData);
				$('#'+option.tId).datagrid("loadData", jsonData);
				option.data = jsonData;
				commonUtil.prompt_fullData = jsonData;
				
				prompt_unlock(option); //��ѯ����������������
				//prompt_selectFirstData(option);
			});
		} else {
			$('#'+option.tId).datagrid("loadData", option.data);
			commonUtil.prompt_fullData = option.data;
			prompt_selectFirstData(option);
		}
	}
	//ѡ�е�һ������
	function prompt_selectFirstData(option){
		$('#'+option.tId).datagrid("selectRow", 0);
		commonUtil.prompt_currentDataIndex = 0;
	}
	//��������,��ȡ�ı�����ֵ,��ȡ����ֵ���һ��,�ź����ֵ,��ģ��ƥ��,�б���ʾƥ����
	function prompt_filterData(option){
		var e = document.getElementById(option.eId);
		var value = e.value.indexOf(",") < 0 ? e.value : e.value.split(',')[e.value.split(',').length - 1];
		$('#'+option.tId).datagrid({loadFilter: function(data){
			var filterData = [];
			for (var i = 0;i < data.length;i++) {
				var temp = data[i][option.selectColumn];
				
				if (!IsEmpty(temp) && temp.indexOf(value) >= 0) {
					filterData.push(data[i]);
				}
//				if (eval("data[i]."+option.selectColumn+".indexOf(value) >= 0 ")) {
//					filterData.push(data[i]);
//				}
			}
			
			return {total:filterData.length, rows:filterData};
		}});
	}
	//��ʾ��λ
	function prompt_showPosition(option) {
		var e = document.getElementById(option.eId);
		var div = document.getElementById(option.dId);
		var left = 1;
		var top = 1;
		var obj = e;
		while (obj != document.body) {
			left += obj.offsetLeft;
			top += obj.offsetTop;
			obj = obj.offsetParent;
		}
		
		if (top + e.offsetHeight + option.height <= $("body").outerHeight()) {
			div.style.top = top + e.offsetHeight;
		} else { //���ҳ���·��ռ䲻��
			div.style.top = top - option.height;
		}
		
		if (left + option.width <= $("body").outerWidth()) {
			div.style.left = left;
		} else { //ҳ���ҷ��ռ䲻��
			var pe = e;
			//��װ�Ŀؼ��ҵ�����������Ԫ��
			if (!IsEmpty($(e).attr("class")) && $(e).attr("class").indexOf("PopupWindow") >= 0) {
				pe = $(e).closest("span");
			}
			var dis = $("body").outerWidth() - (left + $(pe).outerWidth());
			div.style.left = $("body").outerWidth() - option.width - dis;
		}
	}
	//����ѡ������
	//var prompt_currentDataIndex = 0;
	function prompt_selectData(option, keyCode){
		if (IsEmpty(document.getElementById(option.tId)))return;
		
		if (keyCode == 38) {//��
			commonUtil.prompt_currentDataIndex = commonUtil.prompt_currentDataIndex - 1 >= 0 ? commonUtil.prompt_currentDataIndex - 1 : commonUtil.prompt_currentDataIndex;
		} else if(keyCode == 40) {//��
			var maxIndex = $('#'+option.tId).datagrid("getRows", null).length - 1;
			commonUtil.prompt_currentDataIndex = commonUtil.prompt_currentDataIndex + 1 <= maxIndex ? commonUtil.prompt_currentDataIndex + 1 : maxIndex;
		}
		
		var existEle = document.getElementById(option.tId);
		if (existEle) {
			$('#'+option.tId).datagrid("selectRow", commonUtil.prompt_currentDataIndex);
			prompt_setValue(option);
		}
		//prompt_enterConfirm(option);
	}
	//ȷ��ѡ��
	function prompt_setConfirmValue(option){
		prompt_setValue(option);
		prompt_doCallback(option);
	}
	/*
	//�س�ȷ��ѡ��
	function prompt_enterConfirm(option){//cEmployeeName
		var row = $('#'+option.tId).datagrid("getSelected",null);
		if (row == null) return;
		var value = $(document.getElementById(option.eId)).val();
		var index = value.lastIndexOf(",");
		var str = "";
		if (index >= 0) {
			str = value.substr(0, index + 1);
		}
		if (eval('value.indexOf(row.'+option.selectColumn+')') >= 0) {
			return;//�Ѿ����ڵĲ���ֵ
		}
		$(document.getElementById(option.eId)).val(eval('str += row.'+option.selectColumn));
		prompt_setValue(option);
	}
	*/
	//��ʾ��ʾ��
	function prompt_showPrompt(option){
		$('#'+option.dId).show();
	}
	//������ʾ��
	function prompt_hiddenPrompt(option) {
		$('#'+option.dId).hide();
	}
	//id��ʶ��ֵ
	function prompt_setValue(option) {
		
		if (!IsEmpty(option.tId)) {
			var sItem = $('#' + option.tId).datagrid("getSelected");
			
			if (sItem) {
				$("#" + option.eId).val(sItem[option.selectColumn])
				$("#" + option.vId).val(sItem[option.selectColumnId]);
			}
		}
	}
	//���id��ʶ��ֵ
	function prompt_clearIdValue(option) {
		if (!IsEmpty(option.vId)) {
			$("#" + option.vId).val("");
		}
	}
	//ִ������ǰ�ķ���
	function prompt_doBeforeInput(option) {
		
		if (IsEmpty(option.beforeInput)) {
			return true;
		} else if (!IsEmpty(option.beforeInput) && typeof(option.beforeInput) == "function") {
			//����befoeInput����������customParam������δ��������ն���
			if (IsEmpty(option.customParam)) {
				option.customParam = {};
			}
			return option.beforeInput.call(this, option.customParam);
		}
	}
	//ִ��ѡ���Ļص�����
	function prompt_doCallback(option) {
		
		if (!IsEmpty(option.callback) && typeof(option.callback) == "function") {
			var sItem = {};
			
			if (!IsEmpty(option.tId)) {
				sItem = $('#' + option.tId).datagrid("getSelected");
			}
			option.callback.call(this, option.data, sItem);
		}
	}
	//����
	function prompt_lock(option) {
		option.locked = true;
		$("#" + option.eId).attr("readonly", "readonly");
	}
	//����
	function prompt_unlock(option) {
		option.locked = false;
		$("#" + option.eId).removeAttr("readonly").focus();
	}
	//�ж��Ƿ���ס������keyup�¼�
	function prompt_isLocked(option) {
		if (IsEmpty(option.locked)) {
			option.locked = false;
		}
		return option.locked;
	}
	/*
	function prompt_setValueOld(option){
		if (commonUtil.prompt_fullData == null) return;
		var iVal = "";
		var sVal = $(document.getElementById(option.eId)).val();
		if (sVal == "") {
			$(document.getElementById(option.vId)).val("");
			return;
		}
		for (var i = 0;i < sVal.split(',').length;i++) {
			var val = sVal.split(',')[i];
			var id = "";
			for (var j = 0;j < commonUtil.prompt_fullData.length;j++) {
				if (eval('commonUtil.prompt_fullData[j].'+option.selectColumn+' != val')) continue;
				id = eval('commonUtil.prompt_fullData[j].'+option.selectColumnId);
			}
			if (iVal.length > 0) iVal += ",";
			iVal += id;
		}
		$("#"+option.vId).val(iVal);
	}
	*/
/*******************************�ڲ���������************************************/
	//������ʾdiv��,����divId��option��������objArray����
	var div = document.createElement("div");
	div.id = "prompt_"+commonUtil.prompt_id;
	div.className = "prompt_Div";
	option.dId = div.id;
	option.tId = "promptTb_"+commonUtil.prompt_id;
	document.body.appendChild(div);
	//��ȡԪ�ز����onkeyup�¼�,�¼�ִ�з�������option����
	var e = document.getElementById(option.eId);
	$(e).keyup(function(event){
		switch(event.keyCode){
			case 8:
				//�˸��
				prompt_clearIdValue(option);
				if (IsEmpty($(this).val())) {
					prompt_onKeyupFn(option);
				} else {
					prompt_hiddenPrompt(option);
				}
				break;
			case 38:
			case 40:
				//����ѡ������
				//prompt_showPrompt(option);
				try{
					prompt_selectData(option, event.keyCode);
				}catch(e){}
				break;
			case 13:
				//�س�ȷ��ѡ��
				//prompt_enterConfirm(option);
				var existEle = document.getElementById(option.tId);
				if (existEle) {
					prompt_setConfirmValue(option);
					prompt_hiddenPrompt(option);
				}
				break;
			default:
				if (prompt_doBeforeInput(option)) {
					
					if (!prompt_isLocked(option)) {
						prompt_onKeyupFn(option);
					}
					//prompt_setValue(option);
				}
				
				break;
		}
	});
	commonUtil.prompt_id++;
	
	return {
		getOption: function(eId) {
			return commonUtil.PromptOptions;
		}
	}
};
//������ʾ�����ݽ��滺��
commonUtil.initPrompt.options = {}
commonUtil.initPrompt.reload = function(option) {
	if (!IsEmpty(option.eId)) {
		var eIds = option.eId.split(',');
		
		for (var i = 0; i < eIds.length; i++) {
			var opt = commonUtil.initPrompt.options[eIds[i]];
			
			if (opt) {
				delete opt.data;
			}
		}
	}
}
/***************************������ʾ�����************************/

/***************************Enter��ݼ�*************************/
/*
eId�����ÿ�ݼ���Ԫ��
fn: ��ݼ�ִ�еķ���
*/
commonUtil.setFastKey = function(option)
{
	//var enter_id = 1;
	var e = document.getElementById(option.eId);
	//��ֹ����ֻ��һ��input������ύ��
	var input = document.createElement("input");
	input.id = "enter_Input"+commonUtil.enter_id.toString();commonUtil.enter_id++;
	input.style.display = "none";
	e.appendChild(input);
	$(e).bind("keydown", function(event){
		var evt = getEvent();
		if (event.keyCode == 13) {//�س�
			window.setFastKeyOption = option;
			setTimeout("setFastKeyOption.fn()",100);
		}
	});
};
/***************************Enter��ݼ�����*********************/

/***************************������ת������ʾ*****************************/
commonUtil.singleRowToView = function(obj)
{//��ڷ���
/*******************************�ڲ�������ʼ************************************/
	//�����д����obj����
	//var objParamArray = [];
	function setLayoutParam(obj){
		var isHave = false;
		for (var i = 0;i < commonUtil.objParamArray.length;i++) {
			if (commonUtil.objParamArray[i].listId == obj.listId) {
				commonUtil.objParamArray[i] = obj;
				isHave = true;
			}
		}
		if (!isHave) {
			commonUtil.objParamArray.push(obj);
		}
	}
	/*
		ԭ������ѡΪ׼
		��Σ�objΪ��������Σ���������
			 listType:�б����ͣ�1��"htc"��htc�б�2��"flex"��flex�б�3��"jquery":jquery�б�(Ĭ��)��
			 listId:�б�ID�����flex�б�����ն����Ǿ����б����
			 column:��������Ϣ��jquery��flex�б�Ϊ�������飬htc�б�ͨ��������ȡ����Ϣ��

			 �������Ա�����
			 baseWin:�������������ָ������Ļ����Ͽ����������˲�����Ĭ����ǰ����򿪣�������ָ�������ϴ�
			 winW:���ɴ���Ŀ�ȣ�Ĭ��ֵΪ450
			 winH:���ɴ���ĸ߶ȣ�Ĭ��ֵΪ310
			 fn:����ķ����������Զ��������Ϊ��װ�õ�json����
		����ֵ�����ط�װ�õ�json�������
	*/
	function getDataListItem(obj){//��ȡ����
		var listObj = null;
		var tempItem = null;
		var item = null;//��ѡ����
		var returnValue = null;
		
		if(obj.listType == 1 || obj.listType == "htc"){
			listObj = (typeof(obj.listId)=="string") ? document.getElementById(obj.listId) : obj.listId;
			if(listObj){tempItem = listObj.checkedItems;item = listObj.selectedItem;}
		}else if(obj.listType == 2 || obj.listType == "flex"){
			listObj = obj.listId;
			if(listObj){tempItem = listObj.checkedItems();item = listObj.getSelectedItem();}
		}else{
			listObj = (typeof(obj.listId)=="string") ? $('#'+obj.listId) : obj.listId;
			if(listObj){tempItem = $(listObj).datagrid("getSelections");item = $(listObj).datagrid("getSelected");}
		}
		
		if(item){
			//��װ���ݣ���ʽΪ{field:xxx, title:yyy, text:zzz}
			var fld = "";//��ͷfield����
			var title = "";//��ͷtitle����
			var text = "";//ѡ�����ı�ֵ
			if(obj.listType == 1 || obj.listType == "htc"){
				var jsons = "[";
				var objs = "{";
				//��htc�ؼ��ÿ�����:�ֶ������ֶ��������ı�ֵ
				var i=0;var j=0;
				for(var p in item){	//������������
					if(typeof (item[p]) != "function"){
						if(i<3){i++;continue;}
						if(listObj.getDisplayText(j)== undefined){continue;}
						if(item[p]==undefined){item[p]="";}
						if(j > 0){objs+=",{";}
						fld = p;
						title = listObj.getDisplayText(j);
						text = item[p];
						text = text.replace(/\\/g, "\\\\");//��"\"�滻Ϊ"\\"
						objs += "field:\""+fld+"\", title:\""+(title?title.replace(/\n/g,"<br>").replace(/\r/,"<br>").replace(/ /g,"&nbsp;&nbsp;"):"")+"\", text:\""+text+"\"}";
						j++;
					}
				}
				jsons += objs + "]";
				returnValue = jsons;
			}else if(obj.listType == 2 || obj.listType == "flex"){
				var jsons = "[";
				var objs = "{";
				for(var i=0; i<obj.column.length; i++){
					if(obj.column[i].visible == "false")
						continue;
					if(i>0)objs += ",{";
					fld = obj.column[i].dataField;
					title = obj.column[i].headerText;
					text = eval('(' + "item."+fld + ')');//text
					if(text == null)text="";
					text = text.replace(/\\/g, "\\\\");//��"\"�滻Ϊ"\\"
					objs += "field:\""+fld+"\", title:\""+title+"\", text:\""+text+"\"}";
				}
				jsons += objs + "]";
				returnValue = jsons;
			}else{
				var jsons = "[";
				var objs = "{";
				for(var i=0; i<obj.column.length; i++){
					for(var j=0; j<obj.column[i].length; j++){
						if(obj.column[i][j].hidden == 'true')//����װ�����������
							continue;
						if(j>0)objs += ",{";
						
						fld = obj.column[i][j].field;
						title = obj.column[i][j].title;
						text = eval('(' + "item."+fld + ')');//text
						if(text == null)text="";
						text = text.replace(/\\/g, "\\\\");//��"\"�滻Ϊ"\\"
						objs += "field:\""+fld+"\", title:\""+title+"\", text:\""+text+"\"}";
					}
				}
				jsons += objs + "]";
				returnValue = jsons;
			}
		}
		return returnValue;
	}
	
	/*
		����������
		obj:�����Ͳ��������Բμ�getDataListItem����
		jsons����:��װ�õ�json�������
	*/
	function setVerticalTbl(obj){
		
		var jsons = decode(obj.jsons);
		if (typeof(obj.fn) == "function"){obj.fn.call(this, jsons);return;}//����лص�����
		var divHtml	= "";
		var tabHtml = "";
		
		obj.baseWin = (obj.baseWin) ? obj.baseWin:window;
		var div = obj.baseWin.$("#"+obj.listId+"_singleRow");
		if(div.length>0)div=div[0];else div=null;
		if(!div){//����div
			
			var divHtml	= "<div region='center' class='defaultColor' style='border:0'>" +
						  "<table id='singleRowTbl' width='100%' class='formbasic'>";
						
			for(var i=0; i<jsons.length; i++){
				tabHtml += "<tr><td width='30%'>" + jsons[i].title + "</td><th width='70%'>" + jsons[i].text + "</th></tr>";
			}
			divHtml += tabHtml + "</table></div>";
			//��ť����
			divHtml += "<div region='south' style='height:35px;border:0px;' class='defaultColor'><div id='singleRow_btns'></div></div>";
			
			div = obj.baseWin.document.createElement("div");
			div.id = obj.listId+"_singleRow";
			div.className = "easyui-layout";
			div.style.overflow = "hidden";
			div.innerHTML = divHtml;
			obj.baseWin.document.body.appendChild(div);
			
			var buttons =[
				{
					btnId: 'pre', btnPicName: 'arrow-up-top.gif', btnName: '��һ��', btnFun: function(){
						commonUtil.preRow(commonUtil.getLayoutParam(obj.listId));
					}
				},
				{
				 	btnId: 'next', btnPicName: 'arrow-down-bottom.gif', btnName: '��һ��', btnFun: function(){
				 		commonUtil.nextRow(commonUtil.getLayoutParam(obj.listId));
				 	}
				}
			];
			var btnJson = {eId: 'singleRow_btns', btnAlign: 'center', btnOptions: buttons};
			commonUtil.initButtonDiv(btnJson);
			$("#singleRow_btns").css("margin-top", "5px");
			
			$(div).window({
				title: "��������", 
				width: obj.winW ? parseInt(obj.winW) : 450,   
				height: obj.winH ? parseInt(obj.winH) : 310,   
				modal: false,
				collapsible: false,
				minimizable: false,
				maximizable: false,
				resizable: false,
				onClose: function(){
					$(div).remove();
				}
			});
			
		}else{//��������
			for(var i=0; i<jsons.length; i++){
				tabHtml += "<tr><td width='30%'>" + jsons[i].title + "</td><th width='70%'>" + jsons[i].text + "</th></tr>";
			}
			$('#singleRowTbl').html(tabHtml);
		}
	}
/*******************************�ڲ���������************************************/
	setLayoutParam(obj);
	var returnVal = getDataListItem(obj);
	//����װ�õ�json������󸳸�obj��jsons����
	obj.jsons = returnVal;
	setVerticalTbl(obj);
};

/*
	��һ��
	obj:�����Ͳ��������Բμ�getDataListItem����
*/
commonUtil.preRow = function(obj)
{
	var listObj = null;
	if(obj.listType == 1 || obj.listType == "htc"){
		listObj = (typeof(obj.listId)=="string") ? document.getElementById(obj.listId) : obj.listId;
		if(listObj){
			var items = listObj.items;//ѡԪ��
			var item = listObj.selectedItem;//ѡ����
			var rowId = "";//��0��ʼ
			
			for(var i=0; i<items.length; i++){//��ȡ�к�
				if(items[i].iEmployeeId == item.iEmployeeId){rowId = i;break;}
			}
			
			if(rowId == 0){return;}//simpleAlert("��Ϊ����");
			for(var i=0; i<items.length; i++){
				items[i].setChecked(false);
			}
			items[rowId-1].setChecked(true);
			items[rowId-1].setSelected();
			
			commonUtil.singleRowToView(obj);
		}
	}else if(obj.listType == 2 || obj.listType == "flex"){
		listObj = obj.listId;
		if(listObj){
			var sItem = listObj.getSelectedItem();//ѡ����
			var items = listObj.items();//�����ж���
			
			var rowId = sItem.rowId - 1;
			if(rowId == 0){return;}//simpleAlert("��Ϊ����");
			
			listObj.setSelected(items[rowId - 1]);
			
			commonUtil.singleRowToView(obj);
		}
	}else{
		listObj = (typeof(obj.listId)=="string") ? $("#"+obj.listId) : obj.listId;
		if(listObj){
			var row = listObj.datagrid("getSelected");
			if(row){
				//��ȡ�к�
				var index = listObj.datagrid("getRowIndex", row);
				if(index == 0){return;}//simpleAlert("��Ϊ����");
				//ȡ��ѡ��
				listObj.datagrid("unselectRow", index);
				//ѡ������
				listObj.datagrid("selectRow", index-1);
				
				commonUtil.singleRowToView(obj);
			}
		}
	}
};

commonUtil.getLayoutParam = function(listId)
{
	for (var i = 0;i < commonUtil.objParamArray.length;i++) {
		if (commonUtil.objParamArray[i].listId == listId) {
			return commonUtil.objParamArray[i];
		}
	}
	return null;
};

/*
	��һ��
	obj:�����Ͳ��������Բμ�getDataListItem����
*/
commonUtil.nextRow = function(obj)
{
	var listObj = null;
	if(obj.listType == 1 || obj.listType == "htc"){
		listObj = (typeof(obj.listId)=="string") ? document.getElementById(obj.listId) : obj.listId;
		if(listObj){
			var items = listObj.items;//ѡԪ��
			var item = listObj.selectedItem;//ѡ����
			
			var rowId = "";//��0��ʼ
			for(var i=0; i<items.length; i++){
				if(items[i].iEmployeeId == item.iEmployeeId){
					rowId = i;
					break;		
				}
			}
		
			if(rowId == listObj.items.length - 1){return;}//simpleAlert("��Ϊβ��");
			for(var i=0; i<items.length; i++){
				items[i].setChecked(false);
			}
			items[rowId+1].setChecked(true);
			items[rowId+1].setSelected();
			
			commonUtil.singleRowToView(obj);
		}
	}else if(obj.listType == 2 || obj.listType == "flex"){
		listObj = obj.listId;
		if(listObj){
			var sItem = listObj.getSelectedItem();//ѡ����
			var items = listObj.items();//�����ж���
			listObj.setUnChecked(items[sItem.rowId-1],"check");
			
			var rowId = sItem.rowId;//�к�
			if(rowId == items.length){return;}//simpleAlert("��Ϊβ��");
			var index = rowId;
			listObj.setSelected(items[index]);
			
			commonUtil.singleRowToView(obj);
		}
	}else{
		listObj = (typeof(obj.listId)=="string") ? $("#"+obj.listId) : obj.listId;
		if(listObj){
			var row = listObj.datagrid("getSelected");
			if(row){
				//��ȡ�к�
				var index = listObj.datagrid("getRowIndex", row);
				var rows = listObj.datagrid("getRows");
				if(index == rows.length-1){return;}//simpleAlert("��Ϊβ��");
				//ȡ��ѡ��
				listObj.datagrid("unselectRow", index);
				//ѡ������
				listObj.datagrid("selectRow", index+1);
				
				commonUtil.singleRowToView(obj);
			}
		}
	}
};
/***************************������ת������ʾ����*************************/

/***************************�������ʾ��Ϣ****************************/
commonUtil.showDefaultMsg = function(obj)
{
	//var hintOptions = [];//�����������ʾ��Ϣ������
	//var hintId = 1;
	//var hintColor = "#9c9a97";
	function initHint(input, _msg){
		if (input.type != "text" && input.type != "textarea") return;
		if (_msg != undefined && _msg != null) {
			input.setAttribute("showDefault", "true");
			input.setAttribute("msg", _msg);
			//input.attributes['showDefault'].nodeValue = "true";
			//input.attributes['msg'].nodeValue = _msg;
		}
		if (input.attributes['showDefault'] == undefined || input.attributes['showDefault'] == null || input.attributes['showDefault'].nodeValue != "true") return;
		var msg = input.attributes['msg'].nodeValue == undefined ? "" : input.attributes['msg'].nodeValue;
		//�Ǽ���ʾ��Ϣ��������Ϣ����
		var option = new Object();
		option.id = input.id;
		if (option.id == null || option.id == "") {
			option.id = "hint"+commonUtil.hintId.toString();commonUtil.hintId++;
		}
		input.hintId = option.id;
		option.oldColor = input.style.color;
		option.msg = msg;
		option.currentValue = input.value;
		commonUtil.hintOptions.push(option);
		//����ı�Ϊ��,����ʾֵ(ȡmsg���Ե�ֵ),�������ı������ʽΪ��ʾ��Ϣ��ʽ
		input.value = msg;
		input.style.color = commonUtil.hintColor;
		if (GetBrowserType().indexOf("IE") >= 0){
			input.attachEvent("onfocus", function(event){focus(event);});
			input.attachEvent("onblur", function(event){blur(event);});
		} else {
			input.addEventListener('focus', function(event){focus(event);}, false);
			input.addEventListener('blur', function(event){blur(event);}, false);
		}
	}
	function focus(event){//����ı���ʾ,�����ı�����ʽΪ������ʽ
		var input = event.srcElement ? event.srcElement : event.target;
		var option = getHintOption(input.hintId);
		if (input.value != option.msg) return;
		input.value = "";input.value = "";
		input.style.color = option.oldColor;
	}
	function blur(event){//����ı���ֵΪ��,����ʾ��ʾ��Ϣ
		var input = event.srcElement ? event.srcElement : event.target;
		showHint(input);
	}
	//���ı���Ϊ��ʱ,�ı�����ʽ����Ϊ��ʾ��Ϣ��ʽ,����ʾ��ʾ��Ϣ
	function showHint(input){
		if (input.value != "") return;
		var option = getHintOption(input.hintId);
		input.style.color = commonUtil.hintColor;
		input.value = option.msg;
	}
	function setHintOption(option){
		var isHave = false;
		for (var i = 0;i < commonUtil.hintOptions.length;i++) {
			if (commonUtil.hintOptions[i].id == option.id) {
				commonUtil.hintOptions[i] = option;
				isHave = true;
			}
		}
		if (!isHave) {
			commonUtil.hintOptions.push(option);
		}
	}
	function getHintOption(id){
		for (var i = 0;i < commonUtil.hintOptions.length;i++) {
			if (commonUtil.hintOptions[i].id == id) {
				return commonUtil.hintOptions[i];
			}
		}
		return null;
	}
	function initHints(obj){
		var eids = obj.eids.split("##");
		var msgs = obj.msgs.split("##");
		for (var i = 0;i < eids.length;i++) {
			initHint(document.getElementById(eids[i]), msgs[i]);
		}
	}
/*******************************�ڲ���������************************************/
	if (obj == undefined || obj == null) {//��һ��,����ҳ����showDefault=true���ı���
		var inputs = document.getElementsByTagName("input");
		var areas = document.getElementsByTagName("textarea");
		for (var i = 0;i < inputs.length;i++)
			initHint(inputs[i]);
		for (var i = 0;i < areas.length;i++){
			initHint(areas[i]);
		}
	} else if (obj.eids != undefined && obj.msgs != undefined) {//�ڶ���,��ָ��id���ı������
		initHints(obj);
	} else if (obj.url != undefined && obj.url != null) {//������,�Ӻ�̨�õ�ָ������
		AjaxRequest.doRequest(null,obj.url,obj.options,function(backData){
			var jsonData = eval('('+backData+')');
			if (obj.filterFn != undefined && typeof obj.filterFn == "function"){//����й��˷���
				jsonData = obj.filterFn.call(this, jsonData);
			}
			initHints(jsonData);
			if (obj.callBackFn != undefined && obj.callBackFn != null && typeof obj.callBackFn == "function"){
				obj.callBackFn.call(this, jsonData);
			}
		});
	}
};
/***************************�������ʾ��Ϣ����*************************/

/***************************���㿪�������߶ȿ�ʼ*************************/
//�����Ŀ�ߴ��ڿ��Ӵ���Ŀ�ߣ������ݿ��Ӵ���Ŀ��������
//������Ӧ��ͬ�ֱ���
commonUtil.computerWH = function(width, height,winObj)
{
	if(!winObj)winObj=window;
	var screenW=winObj.$("body").width();//document.body.clientWidth;
	var screenH=winObj.$("body").height();//document.body.clientHeight;
	
	if(!width)width=screenW;
	if(!height)height=screenH;
	
	if(width<=1)screenW=screenW*width;
	if(height<=1)screenH=screenH*height;
	
	if(width>1 && width<screenW)screenW=width;
	else if(width>1 && width>screenW)screenW=screenW-40;
	if(height>1 && height<screenH)screenH=height;
	else if(height>1 && height>screenH)screenH=screenH-20;

	return {w:screenW,h:screenH};
};
/***************************���㿪�������߶Ƚ���*************************/

/**
�Զ��岿������ʾ�����ط���
eid:����ID
boo:true��ʾ��false����
**/
commonUtil.setDisplay = function(eid,boo)
{
	var obj=(typeof(eid)=="string")?$("#"+eid):$(eid);
	obj.closest("span").css({"display":(boo?"inline":"none")});
};

/**
�Զ��岿���Ľ��õķ���
eid:����ID
flg:true���ò�����falseȡ������
boo:�д�ֵ����ƽ̨�Զ������
**/
commonUtil.setDisabled = function(eid,flg,boo)
{
	var obj=(typeof(eid)=="string")?$("#"+eid):$(eid);
	obj.attr("disabled",flg);
	if(flg){ //�����ý���״̬�������������
	  obj.css("background-color","#EEEEEE");
	  obj.css("border","#999 1px solid");
	}else{
	  obj.css("background-color","");
	}
};

/**
�Զ���ҳ�水ť��ؼ�
auth:tangyj
date:2013-01-23
eId:��ť���ǩid
bgColor:������ɫ,Ĭ��͸��
className:��ť��ʽ,����ΪϵͳĬ����ʽ
bgClass:������ʽ��(��ָ�����ť�ײ�Ҳ���ô˰�ť�飬����Ҫ������ʽ����)
btnAlign����ť���з�ʽ(����:center������:left������:right)
btnOptions:��ť���ԣ�btnId:��ťID��btnName:��ť���ơ�btnPicName:��ťͼƬ���ơ�btnFun:��ť�����¼�������btnIsHidden:�Ƿ����أ�
**/
commonUtil.initButtonDiv = function(option)
{
	var path=getFullPath();
	var oldDiv = document.getElementById(option.eId);
	if (IsEmpty(oldDiv)) return;	
	
	var btnAlign = IsEmpty(option.btnAlign) ? "center" : option.btnAlign ;
	var btnAreaWidth = IsEmpty(option.btnAreaWidth) ? "100%" : option.btnAreaWidth ;
	var bgColor = IsEmpty(option.bgColor) ? "" : "background-color:" + option.bgColor;
	var className = IsEmpty(option.className) ? "btn_hover" : option.className; //��ť��ʽ
    var bgClass = IsEmpty(option.bgClass) ? "btn_span" : option.bgClass;
	
	//var newDiv = oldDiv.cloneNode();
	var newDiv = document.createElement("span");
	newDiv.id=oldDiv.id;
	$(newDiv).attr("style", "text-align: "+btnAlign+";width:"+btnAreaWidth+";margin-top:0px;"+bgColor);
	$(newDiv).attr("class", bgClass); //���ɶ�������Ϊbtn-span��css��ʽ add by qiaoqide
	var table = document.createElement("table");
	//$(table).attr("class", "formbasic");
	$(table).attr("algin", btnAlign+";");
	var margin = 'margin-right:auto;margin-left:auto';//add by pjw text-align�����ڹȸ衢FF�в����ݣ���ͨ��margin���þ��еȶ����ֹ
	if(btnAlign == "right"){
		margin = 'margin-left:auto;';
	}
	if(btnAlign == "left"){
		margin = 'margin-right:auto;';		
	}
	$(table).attr("style", "background:none;border:0px;"+margin);
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	$(td).attr("style", "background:none;border:0px;height:20px;");
	var ul = document.createElement("ul");
	$(ul).attr("class", className);
	$(ul).css("width", "auto");
	
	var li,a,span,div1;
	for(var i = 0; i<option.btnOptions.length; i++){
		var btnOption = option.btnOptions[i];
		
		li = document.createElement("li");
		$(li).css("width","auto");
		li.id=btnOption.btnId;
		if(btnOption.btnFun){
			$(li).bind("click", btnOption.btnFun);
		}
		a = document.createElement("a");
		$(a).attr("href", "javascript:void(0);");
		$(a).attr("id", btnOption.btnId+"_a");
		
		//IE6�»�ִ��href�е����ݣ��򿪴��ڣ�URLΪjavascript:void(0); add by zhanweibin
		if(GetBrowserType().indexOf("IE 6") >= 0){
			$(a).bind("click", function(){event.returnValue = false;});
		}
		
		if(btnOption.btnIsHidden && 'none' == btnOption.btnIsHidden){
			$(a).css("display", 'none');
		}
		span = document.createElement("span");
		div1 = document.createElement("div");
		$(div1).attr("id", btnOption.btnId+"_div");
		
		if(btnOption.btnPicName.indexOf(".")>0){//�����.��˵��������ͼƬ����
			$(div1).attr("style", "background:url("+path+"/images/core/"+btnOption.btnPicName+") no-repeat 0 center;padding-left:20px");
		}else{//����˵����������ʽ����
			$(div1).addClass(btnOption.btnPicName);
		}
		$(ul).append($(li).append($(a).append($(span).append($(div1).append(btnOption.btnName)))));
	}
	$(newDiv).append($(table).append($(tr).append($(td).append($(ul)))));
	
	oldDiv.parentNode.replaceChild(newDiv,oldDiv);//��ԭ�Ŀ���ɵ�ǰ��span
};

/**
*�Զ���ҳ��ָ����ؼ�
*auth:qiaoqide
*date:2013-05-21
*�������壺
*eId:�����ָ�����ǩid
*title:�ָ�������
*shrinkPic:����ͼ�� �ɲ���
*expandPic:չ��ͼ�� �ɲ���
*hasScroll:�Ƿ���������ʾ������,Ĭ����
*items:��ť����(title:ÿһ����Ŀ���⡢id:װ�ص��ָ�������id/����jquery����ĺ����塢hidden:title/all(���طָ���)��
*             tools:[{btnId:'��ťid', btnPicName:'��ťͼ��', btnName:'��ť����',btnFun:'��ť��������'}])
**/
commonUtil.createSeparatorbar = function(obj)
{
      var jsonData = obj.items;
      if(!jsonData)return;
      var titleHeight = 0;
      var zkPic = ""; //չ��ͼ��
      var ssPic = ""; //����ͼ��
      var componentWidth = "100%"; //�ָ�����ռ��Ļ�ٷֱ�
      var hasScroll = true; //������
      if(obj.shrinkPic)ssPic = obj.shrinkPic;
      if(obj.expandPic)zkPic = obj.expandPic;
      if(obj.width)componentWidth = obj.width+"%";
      if(!IsEmpty(obj.hasScroll)){
         hasScroll = obj.hasScroll;
      }
      
      /***********�ָ������� start**********/
      var titleTable = document.createElement("table");
      $(titleTable).attr("style", "border: 0px;width:100%;padding:0px;");
      $(titleTable).attr("cellspacing","0");
      /***********�ָ������� end**********/
      
      var table = document.createElement("table");
      $(table).attr("id","tbl_"+obj.eId);
      $(table).attr("style", "border: 0px;width:100%;padding:0px;");
      $(table).attr("cellspacing","0");
      for(var i = 0;i<jsonData.length;i++){
         /**************�����ָ�����Ŀ start************/
         var tr = document.createElement("tr");
         $(tr).attr("id","titleTr_"+obj.eId+"_"+i);
         if(jsonData[i].hidden && (jsonData[i].hidden=='title'||jsonData[i].hidden=='all')){
            $(tr).attr("style", "display:none;"); //���ر���
         }
         var td = document.createElement("td"); //���ⵥԪ��
         $(td).attr("style", "height:30px;padding:0px;");
         
         var titleDiv = document.createElement("div");//����div
         $(titleDiv).attr("class", "panel-header");
         $(titleDiv).attr("style", "width:100%;height:100%;padding-top:0px;");
         
         var div = document.createElement("div");
         $(div).attr("style", "float:left;padding-left: 3px;");
         
         if(jsonData[i].title!=obj.title){
	         /*var img = document.createElement("img"); //����չ������ͼƬ����
	         $(img).attr("src", path+"/images/core/"+zkPic);
	         $(img).attr("trId",obj.eId+"_"+i);
	         $(img).attr("id","img_"+obj.eId+"_"+i);
	         $(img).bind("click", function(){changeTable(this);});*/
	         
	         var img = document.createElement("span"); //����չ������ͼƬ����
	         $(img).attr("class", "separatorbar-open");
	         if(zkPic){  //zkPic��Ϊ��˵�����û��Զ���ͼ��
	            $(img).attr("style","background:url("+path+"/images/core/"+zkPic+");cursor:pointer;");
	         }
	         $(img).attr("trId",obj.eId+"_"+i);
	         $(img).attr("id","img_"+obj.eId+"_"+i);
	         $(img).bind("click", function(){changeTable(this);});
	         $(img).html("&nbsp;&nbsp;&nbsp;&nbsp;");

	         var label = document.createElement("label"); //������Ŀ�������
             $(label).html("&nbsp;&nbsp;"+jsonData[i].title);
             $(label).attr("style", "cursor:pointer;");
             $(label).attr("trId",obj.eId+"_"+i);
	         $(label).bind("click", function(){changeTable(this);});
             
	         $(div).append($(img));
	         $(div).append($(label));
         }else{
             createTitle(div, jsonData[i].title);//�ָ�������
         }

         var buttonDiv = document.createElement("div"); //���������Ŀ��ť��div����
         $(buttonDiv).attr("id","buttonDiv_"+obj.eId+"_"+i);
         $(buttonDiv).attr("style","float:right;padding-right: 15px;");
         
         $(titleDiv).append($(div)).append($(buttonDiv));
         $(td).append($(titleDiv));
         
         if(jsonData[i].title==obj.title){ //�������Ŀ��Ϊ���������ö���Ϊ�ָ�������
            $(titleTable).append($(tr).append($(td)));
            titleHeight = 30; //������������ȥ�ָ�������ĸ߶�
            continue;
         }else{
            $(table).append($(tr).append($(td)));
         }
         /*************�����ָ�����Ŀ end*************/
         
         /***********�����ָ�������װ������ start*******/
         var contentTr = document.createElement("tr");
         if(jsonData[i].hidden && jsonData[i].hidden=='all'){
            $(contentTr).attr("style", "display:none;"); //�����Ƿ�������������
         }
         $(contentTr).attr("id","contentTr_"+obj.eId+"_"+i);
         var contentTd = document.createElement("td");
         $(contentTd).attr("style", "padding:0px;");
         var contentObj = null;//����������׷�Ӷ���
         if(jsonData[i].id && typeof(jsonData[i].id)=="string"){
             contentObj = $("#"+jsonData[i].id);//dom����
             $("#"+jsonData[i].id).css("display", "block"); //��dom��������Ϊ��ʾ���ں�̨����display:none�����Ż��˼���Ч�� add by zwb
         }else{
             contentObj = jsonData[i].id;//jquery����
         }
         $(contentTd).append(contentObj);
         $(contentTr).append($(contentTd));
         $(table).append($(contentTr));
         /***********�����ָ�������װ������ end*******/
      }
      var allDiv = document.createElement("div"); //���������ָ���������
      $(allDiv).attr("id","contentDiv_"+obj.eId);
      $(allDiv).append($(table));
      
      /******���������ָ���ռ��Ļ�İٷֱ�  start*******/
      var alignDiv = document.createElement("div");
      $(alignDiv).attr("style", "width:"+componentWidth+";");
      $(alignDiv).append($(titleTable));//��������
      $(alignDiv).append($(allDiv)); //�²�������
      /******���������ָ���ռ��Ļ�İٷֱ�  end ********/
      
      $("#"+obj.eId).append($(alignDiv));
      $("#"+obj.eId).attr("style","text-align:center;");
      
      if($("#"+obj.eId).height()>document.body.clientHeight && obj.hasScroll){
         $(allDiv).attr("style", "height:"+(document.body.clientHeight-titleHeight)+"px;width:100%;overflow-y:auto;overflow-x:hidden;");
      }else{
         $(allDiv).attr("style", "width:100%;overflow-x:hidden;");
      }
      createButtons(obj);
      
      //�����ָ����ϵİ�ť
	  function createButtons(objData){
	      var jsonData = objData.items;
	      for(var i = 0;i<jsonData.length;i++){
	          if(jsonData[i].tools){
			      var btnJson = {eId:"buttonDiv_"+objData.eId+"_"+i, btnAlign : "right", btnOptions : jsonData[i].tools, bgClass : "btn_sep"};
				  commonUtil.initButtonDiv(btnJson);
			  }
		  }
	  }
	  
	  //���ػ���ʾ�ָ�����������
	  function changeTable(id){
	       var index = "";
	       if(typeof(id)=="string"){
	          index = id;
	       }else{
	          index = $(id).attr("trId");
	       }
	       if($("#contentTr_"+index).is(":hidden")) {
	          $("#contentTr_"+index).show();
	          $("#img_"+index).attr("class", "separatorbar-open");
	          if(zkPic){  //zkPic��Ϊ��˵�����û��Զ���ͼ��
	             $("#img_"+index).attr("style","background:url("+path+"/images/core/"+zkPic+");cursor:pointer;");
	          }
	          //$("#img_"+index).attr("src",path+"/images/core/"+zkPic);
	       }else{
	          $("#contentTr_"+index).hide();
	          $("#img_"+index).attr("class", "separatorbar-close");
	          if(ssPic){  //ssPic��Ϊ��˵�����û��Զ���ͼ��
	             $("#img_"+index).attr("style","background:url("+path+"/images/core/"+ssPic+");cursor:pointer;");
	          }
	          //$("#img_"+index).attr("src",path+"/images/core/"+ssPic);
	       }
	       //resizeSeparatorbar();
	   }
	   
	   //���¼���table��ײ��и߶�,��������Ӧ
	   function resizeSeparatorbar(){
	      var table = document.getElementById("tbl_"+obj.eId);
	      var contentDiv = document.getElementById("contentDiv_"+obj.eId);
	      var h = document.body.clientHeight-$(table).height()-titleHeight;
	      for(var i=0; i<table.rows.length; i++){
	          var row = table.rows[i];
	          var td = row.cells[0];
	          $(td).attr("separatorbar_h",$(row).height()+h);
	          $(td).attr("separatorbar_w",contentDiv.clientWidth);
	      }
	      $(window).resize();
	   }
	   
	   //�ָ�������
	   function createTitle(objDiv, title){
	      var label = document.createElement("label"); 
          $(label).html("&nbsp;&nbsp;"+title);
          $(label).attr("style","line-height:25px;");
          $(objDiv).append($(label));
	   }
};

/**************************************************ҳ���ʼʱ�Զ���ʼcommonUtil�ؼ�**************************************************/
/*
author:tanjianwen
2011-12-29
���ı���ģ���������,���ڿ�����ʽͼƬ���������ݼ���
�˹���������jquery
*/
//window.document.onreadystatechange
if (typeof jQuery != "undefined")
{
$(window).bind("load",function(){
		var lis=$(".JQSelect").each(function(){
			if (this.tagName != 'TABLE') {
				var option = {eId : $(this).attr("id"),
										desc : (IsEmpty($(this).attr("desc")) && $(this).attr("desc")!='' )? "ȫ��" : $(this).attr("desc")};
				commonUtil.initJquerySelect(option); 
			}
		});
		
		var lis=$(".Digit").each(function(){
			if (this.tagName != 'TABLE') {
				var option = {eId : $(this).attr("id"),limit : $(this).attr("limit"),max : $(this).attr("max"),min : $(this).attr("min"),dataType : $(this).attr("dataType"),decimals : $(this).attr("decimals")};
				commonUtil.initDigit(option); 
			}
		});
		
		var lis=$(".PopupWindow").each(function(){
			if ((this.className.indexOf("PickList") >= 0 && this.tagName != 'SPAN') || 
				(this.className.indexOf("PopupWindow") >= 0 && this.tagName != 'TABLE')) {
				var evt=$(this).attr("onclick");//�ÿ�����ť����¼�
				var cleanEvt=$(this).attr("onclean");//����¼�
				if (!IsEmpty(cleanEvt)) {
					try{
						eval("cleanEvt = function(){ "+cleanEvt+"}");
					}catch(ee){
					}
				}
				$(this).attr("onclick","");
				//alert(1);
				var valIds=$(this).attr("valIds");
				commonUtil.initPopupWindow($(this).attr("id"), evt, valIds, cleanEvt);
			}
		});
		
		var lis=$(".TextAreaPopupWindow").each(function(){
			if ((this.className.indexOf("TextAreaPopupWindow") >= 0 && this.tagName != 'TABLE')) {
				var evt=$(this).attr("onclick");//�ÿ�����ť����¼�
				var cleanEvt=$(this).attr("onclean");//����¼�
				if (!IsEmpty(cleanEvt)) {
					try{
						eval("cleanEvt = function(){ "+cleanEvt+"}");
					}catch(ee){
					}
				}
				$(this).attr("onclick","");
				var valIds=$(this).attr("valIds");
				var openPic=$(this).attr("openPic");
				var cleanPic=$(this).attr("cleanPic");
				commonUtil.initTextareaWindow($(this).attr("id"), evt, valIds, cleanEvt, openPic, cleanPic);
			}
		});
		
		var lis=$(".fileUpload").each(function(){
			if ((this.className.indexOf("fileUpload") >= 0 && this.tagName != 'TABLE')) {
				var uploadEvt = $(this).attr("afterUpload");//�ϴ��ص�
				var cleanEvt = $(this).attr("afterClean");//��ջص�
				var afterUpload;
				if (!IsEmpty(uploadEvt)) {
					try{
						eval("afterUpload = "+uploadEvt);
					}catch(ee){
						alert(ee);
					}
				}
				var afterClean;
				if (!IsEmpty(cleanEvt)) {
					try{
						eval("afterClean = "+cleanEvt);
					}catch(ee){}
				}
				var iTableId = IsEmpty($(this).attr("iTableId"))?"":$(this).attr("iTableId");
				var iTableId1=iTableId1;
				if(!IsEmpty(iTableId)){
					iTableId = decode(iTableId);
				}
				var sTableName = IsEmpty($(this).attr("sTableName"))?"":$(this).attr("sTableName");
				var isToLocal = IsEmpty($(this).attr("isToLocal"))?"":$(this).attr("isToLocal");
				var isSaveToDB = IsEmpty($(this).attr("isSaveToDB"))?"1":$(this).attr("isSaveToDB");
				var directory = IsEmpty($(this).attr("directory"))?"":$(this).attr("directory");
				var limitReg = IsEmpty($(this).attr("limitReg"))?"":$(this).attr("limitReg");
				var displayName = IsEmpty($(this).attr("displayName"))?"serverFileDir":$(this).attr("displayName");
				var iDeptId = IsEmpty($(this).attr("iDeptId"))?"":$(this).attr("iDeptId");
				var maxUpSize = IsEmpty($(this).attr("maxUpSize"))?"":$(this).attr("maxUpSize");
				var option = {eId : $(this).attr("id") , iTableId : iTableId, iTableId1 : iTableId1, sTableName : sTableName,isToLocal : isToLocal,isSaveToDB : isSaveToDB,
										directory : directory,limitReg : limitReg, maxUpSize : maxUpSize, displayName : displayName,iDeptId : iDeptId,afterUpload : afterUpload,afterClean : afterClean};
					
				commonUtil.initFileUpload(option);
			}
		});
		
		var lis=$(".moreValue").each(function(){
			if ((this.className.indexOf("moreValue") >= 0 && this.tagName == 'TEXTAREA')) {
				var option  = {mId : this};
				commonUtil.changeMoreValue(option);
			}
		});
});

//tangyj 2013-04-27
//���ڵ�����Сʱ�����Զ���������Զ���Ӧ�ٷֱȿ��
$(window).bind("resize",function(){
	window.setTimeout(commonUtil.autoWidth,500);
  });
}
//���ø����Զ���������аٷֱȿ������Ӧ
commonUtil.autoWidth=function(){
	function changeObjWidth(obj,cssName,imgWidth){
		var $inputObj = $(obj);
		var reg = new RegExp("^\\s*(\\d+)(%)\\s*$");
		if(!$inputObj.attr("oldWidth")){
			return;
		}
		var r = $inputObj.attr("oldWidth").match(reg);
		if (r == null) {
			return;
		}
		//��ȡ�ؼ��ⲿ�����ĳ���
		var $outObj = $inputObj.parents("table."+cssName).parent().parent();
		if(cssName == "fileUpload"){
			$outObj = $outObj.parent();
		}
		while($outObj.length>0 
			//&& ($outObj.css("width") == "auto" 
			&& $outObj.attr("isBorderSpan") == "1"){//"isBorderSpan"�����ж��Ƿ�Ϊͨ�ñ�����������߿�span
			$outObj = $outObj.parent();
		}
		
		var outObj = $outObj.get(0);
		//��ȡ�߿�����Ҽ��
		var paddingLeftWidth = 0 ;
		var paddingRightWidth = 0;
		var regPaddingWidth = new RegExp("^\\s*(\\d+)(px)?\\s*$");
		if($(outObj).css("padding-left")){
			var r_paddingLeft = $(outObj).css("padding-left").match(regPaddingWidth);
			if(r_paddingLeft != null){
				paddingLeftWidth = parseInt(r_paddingLeft[1]);
			}
		}
		if($(outObj).css("padding-right")){
			var r_paddingRight = $(outObj).css("padding-right").match(regPaddingWidth);
			if(r_paddingRight != null){
				paddingRightWidth = parseInt(r_paddingRight[1]);
			}
		}
		//ȡ���ǰ������Ϊ0
		$inputObj.css("width",0);
		
		var outObjWidth = outObj.offsetWidth;
		//�����ⲿ�����Ŀ�ȺͿؼ���ȣ��ٷֱȣ����㳤��
		//var inputOjbWidth =((outObjWidth ) * r[1] / 100.00) - imgWidth - (paddingLeftWidth + paddingRightWidth);//edit by pjw
		var inputOjbWidth =((outObjWidth ) * r[1] / 100.00) - imgWidth;
		$inputObj.css("width",inputOjbWidth);
	}
	
	$("input.JQSelect").each(function(){
		changeObjWidth(this,"JQSelect",15);
	});

	$("input.Digit").each(function(){
		changeObjWidth(this,"Digit",16);
	});
	
	$("input.PopupWindow").each(function(){
		changeObjWidth(this,"PopupWindow",16);
	});
	
	$("table.fileUpload").each(function(){
		changeObjWidth($(this).find("tbody>tr>td>input")[0],"fileUpload",17);
	});
	
	$("textarea.moreVal").each(function(){
		changeObjWidth(this,"moreVal",15);
	});
		
}

/**
	����tabҳȨ����ʾtabҳ
	tabsArr:tabҳ��������
	tabs:tab����div����
	rightNo:��ǰtabҳ��rightNo
	tab:��ǰtabҳ����
*/
function tabsRights(tabsArr,tabs,rightNo,tab){
	function rightByUrl(){
		var fullPath = getFullPath()+"/";
		var path = getPathName();
		var param = {
			sUrl:window.location.href.replace(fullPath,"")
		};
		AjaxRequest.doRequest(null,path + "/safeMgr/login!getLimitTabByUrl.action",param,function(backData){
			tabsRights.urlList = decode(backData);
			//rList����Ϊ���е�ǰ�û�û��Ȩ�޵�tabҳ
		},false);
	}
	
	function rightByNo(){
		var fullPath = getFullPath()+"/";
		var path = getPathName();
		if(!IsEmpty(window.staffTabRightList)){
			tabsRights.rnList = decode(window.staffTabRightList);
		}else{
			AjaxRequest.doRequest(null,path + "/safeMgr/login!getTabRight.action",null,function(backData){
				window.staffTabRightList = backData;
				tabsRights.rnList = decode(backData);
			},false);
		}	
	}
	
	function checkUrl(tabsArr,tabs,tabTitle,tab){
		if(tabsRights.urlList[tabs[0].id+tabTitle] 
			||tabsRights.urlList[tabTitle]){
			closeTab(tabsArr,tab);
		}else if("undefined" == typeof tabsRights.firstPop){
			selTab(tabsArr);
		}
	}
	function checkNo(tabsArr,tabs,rightNo,tab){
		if(rightNo && !tabsRights.rnList[rightNo]){
			closeTab(tabsArr,tab);
		}else if("undefined" == typeof tabsRights.firstPop){
			selTab(tabsArr);
		}
	}
	function closeTab(tabsArr,tab){
		var wt = tabsArr[tabsArr.length-1].panel("options").tab;
		wt.hide();
		wt.show=function(){};
		wt.hide=function(){};
		$(tab).css("display","none");
		tabsArr[tabsArr.length-1].panel("options").closed = true;
	}
	function selTab(tabsArr){
		tabsArr[tabsArr.length-1].panel("options").closed = false;
		tabsRights.firstPop = tabsArr.length-1;
	}
	var tabTitle = tabsArr[tabsArr.length-1].panel("options").title;
	if("undefined" != typeof tabsRights.urlList){
		checkUrl(tabsArr,tabs,tabTitle,tab);
	}else if("undefined" != typeof tabsRights.rnList){
		checkNo(tabsArr,tabs,rightNo,tab);
	}else{
		var uURL;
		if("undefined" != typeof useURLRight){
			uURL = useURLRight;
		} 
		if("1"==getSystemParams("isCheckSession","1")){
			if(!uURL || uURL == 0){//Ȩ�޶�ȡ��ʽ1������rigthNoƥ��
				rightByNo();
				checkNo(tabsArr,tabs,rightNo,tab);
			}else{//Ȩ�޶�ȡ��ʽ2������URL��tab���⡢����tabs��id����ƥ��
				rightByUrl();
				checkUrl(tabsArr,tabs,tabTitle,tab);
			}
		}
	}
	
}

/** ��дeasyUI��tabҳ��ʼ�����淽��ΪtabsRights*/
$.fn.extend({pushTabs:function(tabsArr,tabs,rightNo,tab){
		tabsRights(tabsArr,tabs,rightNo,tab);
}});


/**�Ƿ��������*/
function isNotEncrypt(){
	return (getSystemParams("isEncryptParams","0") == '0');
}

/** 
���CONTEXT_PATH_NAME������˵��û������commonJs.jsp���������һ����.ftlģ�����ɵĽ��棬����ͨ�ñ�
��ô�Ͷ�̬����
key:config.properties���key
def:��ûȡ��ֵʱ������Ĭ��ֵ
noPath:���������������������ã���Ϊ�˳����Լ��ж��Ƿ��й�����
*/
var getSystemParamsNUM = 0;
function getSystemParams(key,def,noPath){
	if("undefined" == typeof(window[key])){
		try{
		   $.ajax({
				 type: 'POST',
				 url: (noPath ? "" : getPathName(window)) + '/systemParams.jsp',
				 data: {},
				 async: false,//�첽��falseΪ����
				 timeout:40000,//40���ʱ
				 dataType: 'json',
				 success: function(jsonData, textStatus) {
				    if(typeof(jsonData)=='object'){
				   		for(var i in jsonData){
				   			window[i]=jsonData[i];
				   		}
				    }
				    if("undefined" == typeof(window[key])){//���û�У�����Ĭ��ֵ
				    	window[key]=def;
				    }
			 	 },
			 	 error: function(XMLHttpRequest, textStatus, errorThrown) {
			 	 	getSystemParamsNUM++;
			 	 	if(getSystemParamsNUM<=1){//�����ǵݹ���ã�Ҫȷ����������ѭ��
			 	 	  	getSystemParams(key,def,true); 
			 	 	}else{
			 	 		window[key] = def;
			 	 	}
			 	 }
			 });
		}catch(e){window[key] = def;}
	}
	return window[key] ? window[key] : def;
}

/**
 * ������������
 * @param param ����
 * @param flg �Ƿ�url�ڵĲ���
 * @param force �Ƿ�ǿ�Ƽ��ܣ�ǿ�Ƽ��������ȫ������isEncryptParams
 * @return ���ܺ�Ĳ���
 */
function encryptParam(parm,flg,force) {
	if(force!=true && isNotEncrypt()){
	  	return parm;
	}else{
		var str_in;
		var num_out = "";
		var a = ["A", "B", "C", "D", "E", "F", "G", "H", 
			"I", "Z", "K", "L", "M", "N", "O", "P", "Q", 
			"R", "S", "T", "U", "V", "W", "X", "Y", "Z"];
		//�е�ģ����߽���������2��encodeURIComponentת�룬һ�����˴˼��ܷ���������˾��޷��Զ������һ�㣬�����������Ƚ��
		if(flg){//url��Ĳ��������Ƚ�һ��
			parm = decodeURIComponent(parm); 
		}
		parm = encodeURIComponent(parm);
		str_in = escape("${&`~m.';@#}"+parm);//���ϱ�ʶ��
		for(var i = 0; i < str_in.length; i++) {
			var random = parseInt(Math.random()*a.length);
			var str = str_in.charCodeAt(i) - 23;
			str = str.toString();
			if(random < 8){
				num_out += a[random];
			}
			num_out += str.substr(0,1);
			if(random > 18){
				num_out	+= a[random];
			}
			num_out += str.substr(1,1);
			if(random <= 18 && random >= 8){
				num_out +=  a[random];
			}
		}
		return num_out;
	}
}

/**
 * ������������
 * @param param ����
 * @param force �Ƿ�ǿ�ƽ��ܣ�ǿ�Ƽ��������ȫ������isEncryptParams
 * @return ���ܺ�Ĳ���
 */
function decryptParam(param,force) {
	if(force!=true && isNotEncrypt()){
		return param;
	}else{
		var str_out = ""; 
		var num_in;
		var num_out = param;
		for(i = 0; i < num_out.length; i += 3) {
			var s = num_out.substr(i,3);
			var n = "";
			for(j = 0; j < s.length; j++){
				if(!isNaN(s.substr(j, 1))){
					n += s.substr(j,1);
				}
			}
			num_in = parseInt(n) + 23;
			num_in = unescape('%' + num_in.toString(16));
			str_out += num_in;
		}
		var result = "";
		try{result = decodeURIComponent(unescape(str_out));}catch(e){result = param;} //��ͨ�ñ������"Ԥ��"�س���Ϊ����Ҫ�󲶻��쳣
		if(result.indexOf("${&`~m.';@#}") == 0){
			return result.replace("${&`~m.';@#}", "");
		}else {
			return param;
		}
	}
}

/** 
 * ��������-�����������
 * @param params ��������
 * @param force �Ƿ�ǿ�ƽ��ܣ�ǿ�Ƽ��������ȫ������isEncryptParams
 * @return �Բ������ֽ��ܺ�Ķ���
 */
function decryptParams(params,force) {
	var param = $.extend(true, {}, params);
	for(park in param){
		var parv = param[park];
		parv = decryptParam(parv,force);
		param[park] = parv;
	}
	return param;
}

/**
 * ��������-url��������
 * @param url urlȫ��
 * @param force �Ƿ�ǿ�ƽ��ܣ�ǿ�Ƽ��������ȫ������isEncryptParams
 * @return �Բ������ֽ��ܺ��url
 */
function decryptURL(url,force){
	var q = url.split("?");
	if(q.length != 1){
		var qStr = q[1];
		url = q[0] + "?";
		var qStrs = qStr.split("&");
		for(var i = 0; i < qStrs.length; i++){
			if(i != 0){
				url += "&";
			}
			var qv = qStrs[i].split("=");
			url += qv[0] + "=" + decryptParam(qv[1],force);
		}
	}
	return url;
}

/** 
 * ��������-�����������
 * @param params ��������
 * @param force �Ƿ�ǿ�Ƽ��ܣ�ǿ�Ƽ��������ȫ������isEncryptParams
 * @return �Բ������ּ��ܺ�Ķ���
 */
function encryptParams(params,force) {
	var param = $.extend(true, {}, params);
	for(park in param){
		var parv = param[park];
		parv = encryptParam(parv,false,force);
		param[park] = parv;
	}
	return param;
}

/**
 * ��������-url��������
 * @param url urlȫ��
 * @param force �Ƿ�ǿ�Ƽ��ܣ�ǿ�Ƽ��������ȫ������isEncryptParams
 * @return �Բ������ּ��ܺ��url
 */
function encryptURL(url,force){
	var index = url.indexOf("?"); //��indexOf������split��Ϊ�˱�������������ʺ�
	if(index>-1){
		var qStr = url.substring(index+1);//q[1];
		var ul = url.substring(0,index);
		url = ul + "?";
		var qStrs = qStr.split("&");
		for(var i = 0; i < qStrs.length; i++){
			if(i != 0){
				url += "&";
			}

			var qv = [];
			var ind= qStrs[i].indexOf("="); //��indexOf������split��Ϊ�˱�����������е��ں�
			qv[0] = qStrs[i].substring(0,ind);
			qv[1] = qStrs[i].substring(ind+1);
			url += qv[0] + "=" + encryptParam(qv[1],true,force);
		}
	}
	return url;
}

/**     
 * ��Date����չ���� Date ת��Ϊָ����ʽ��String     
 * ��(M)����(d)��12Сʱ(h)��24Сʱ(H)����(m)����(s)����(E)������(q) ������ 1-2 ��ռλ��     
 * ��(y)������ 1-4 ��ռλ��������(S)ֻ���� 1 ��ռλ��(�� 1-3 λ������)     
 * eg:     
 * (new Date()).pattern("yyyy-MM-dd hh:mm:ss.S") ==> 2013-07-02 08:09:04.423     
 * (new Date()).pattern("yyyy-MM-dd E HH:mm:ss") ==> 2013-03-10 �� 20:09:04     
 * (new Date()).pattern("yyyy-MM-dd EE hh:mm:ss") ==> 2013-03-10 �ܶ� 08:09:04     
 * (new Date()).pattern("yyyy-MM-dd EEE hh:mm:ss") ==> 2013-03-10 ���ڶ� 08:09:04     
 * (new Date()).pattern("yyyy-M-d h:m:s.S") ==> 2013-7-2 8:9:4.18     
 */     
Date.prototype.pattern=function(fmt) {        
    var o = {        
    "M+" : this.getMonth()+1, //�·�        
    "d+" : this.getDate(), //��        
    "h+" : this.getHours()%12 == 0 ? 12 : this.getHours()%12, //Сʱ        
    "H+" : this.getHours(), //Сʱ        
    "m+" : this.getMinutes(), //��        
    "s+" : this.getSeconds(), //��        
    "q+" : Math.floor((this.getMonth()+3)/3), //����        
    "S" : this.getMilliseconds() //����        
    };        
    var week = {        
    "0" : "\u65e5",        
    "1" : "\u4e00",        
    "2" : "\u4e8c",        
    "3" : "\u4e09",        
    "4" : "\u56db",        
    "5" : "\u4e94",        
    "6" : "\u516d"       
    };        
    if(/(y+)/.test(fmt)){        
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));        
    }        
    if(/(E+)/.test(fmt)){        
        fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "\u661f\u671f" : "\u5468") : "")+week[this.getDay()+""]);        
    }        
    for(var k in o){        
        if(new RegExp("("+ k +")").test(fmt)){        
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));        
        }        
    }        
    return fmt;        
}

/**
 * ��ʱ���ַ���ת��ʱ�����, ������java��simpleDateFormat
 * author:zhangzhiqiang
 * ʾ����
 * 	"2014-06-20 12:12:33".dateformat('yyyy-MM-dd HH:mm:ss')
 */
String.prototype.dateformat = function(pattern){
	var result = new Date();
    var source = this.toString();
    var startIndex = -1;
	
    startIndex = pattern.indexOf("yyyy");
    if (startIndex >= 0) {
        var year = source.substring(startIndex, startIndex + 4);
		result.setFullYear(year);
		if(result.getFullYear() != year){
			throw new Error("SimpleDateFormat parse " + source + " error!");
		}
		pattern.replace(/(yyyy)/g, year);
    }
	startIndex = pattern.indexOf("MM");
    if (startIndex >= 0) {
        var month = source.substring(startIndex, startIndex + 2) - 1;
		pattern.replace(/(MM)/g, month);
		result.setDate(1);//����ǰʱ��Ϊ31�ţ��ᵼ���·������Զ���1
		result.setMonth(month);
		if(result.getMonth() != month){
			throw new Error("SimpleDateFormat parse " + source + " error!");
		}
    }
	startIndex = pattern.indexOf("dd");
    if (startIndex >= 0) {
        var day = source.substring(startIndex, startIndex + 2);
		pattern.replace(/(dd)/g, day);
		result.setDate(day);
		if(result.getDate() != day){
			throw new Error("SimpleDateFormat parse " + source + " error!");
		}
    }
	startIndex = pattern.indexOf("HH");
    if (startIndex >= 0) {
        var hour = source.substring(startIndex, startIndex + 2);
		pattern.replace(/(HH)/g, hour);
		result.setHours(hour);
		if(result.getHours() != hour){
			throw new Error("SimpleDateFormat parse " + source + " error!");
		}
    }
	
	startIndex = pattern.indexOf("mm");
    if (startIndex >= 0) {
        var minute = source.substring(startIndex, startIndex + 2);
		pattern.replace(/(mm)/g, minute);
		result.setMinutes(minute);
		if(result.getMinutes() != minute){
			throw new Error("SimpleDateFormat parse " + source + " error!");
		}
    }
	
	startIndex = pattern.indexOf("ss");
    if (startIndex >= 0) {
        var second = source.substring(startIndex, startIndex + 2);
		pattern.replace(/(ss)/g, second);
		result.setSeconds(second);
		if(result.getSeconds() != second){
			throw new Error("SimpleDateFormat parse " + source + " error!");
		}
    }
	
	startIndex = pattern.indexOf("SSS");
    if (startIndex >= 0) {
        var millisecond = source.substring(startIndex, startIndex + 3);
		pattern.replace(/(SSS)/g, millisecond);
		result.setMilliseconds(millisecond);
		if(result.getMilliseconds() != millisecond){
			throw new Error("SimpleDateFormat parse " + source + " error!");
		}
    }
	return result;
};