$(document).ready(function(){
	LoginForm.initForm();
	if ($.cookie('loginCookie') != null) {
		$('#sAccount').val($.cookie('loginCookie'));
		if($.cookie('rolCookie') != null){
			AjaxRequest.doRequest(null,
				path+'/safeMgr/login!getRoleByAccount.action',
				{sStaffAccount:document.getElementById("sAccount").value},
				function(backData){
					var jsonData = decode(backData);
					if(jsonData.length == 0)return;
					for(var i =0;i<jsonData.length;i++){
						if(jsonData[i].iRoleId == $.cookie('rolCookie')){
							$('#sSelRole').val(jsonData[i].sRoleName);
							$('#iSelRoleId').val(jsonData[i].iRoleId);
							break;
						}
					}
			});
		}
	}
});

LoginForm = {
	aryLoginUrl : [
		{sUrl : path+'/safeMgr/login!login1.action', iLoginFailure : 0}
	]
};
$.extend(LoginForm, {
	initForm : function()
	{	
		$('#LoginButton').click(LoginForm.check);
		$('#sAccount').keyup(function(event){LoginForm.keyUp(event)});
		$('#sPasswd').keyup(function(event){LoginForm.keyUp(event)});
		if($('#sValidataCode').length != 0){
			$('#sValidataCode').keyup(function(event){LoginForm.keyUp(event)});
		}
		try
		{
			$('#sAccount').val = $.cookie('loginCookie') || '';
			$('#sAccount').focus();
		}catch(e){}
	},
	keyUp : function(event, obj, map)
	{
		hideMsg();
		if(event.keyCode == 13)
			LoginForm.check();
		return false;
	},
	check : function()
	{	
		document.body.focus();//让几个输入框失焦,否则如果是验证码输入错误，则会因为未失焦而不刷新验证码
		LoginForm.iCount = 0;
		LoginForm.iFailureCount = 0;
		LoginForm.iFailureTotal = 0;
		LoginForm.iTryFailureTotal = 3;
		
		var account = $('#sAccount').val();//账号
		var password = $('#sPasswd').val();//密码
		var validataCode = "";
		var iSelRoleId = "";
		var iSelDeptId = "";
		var sBrowserType = $('#sBrowserType').val();//浏览器类型
		if(account.trim().length == 0||account=="用户名")
		{
			showMsg('请输入用户名！',$(".input_bg")[0]);
			$('#sAccount').focus();
			//simpleAlert({type:1,msg:'请输入用户名！',fn:function(){setTimeout("$('#sAccount').focus()",200);}});
			return false;
		}
		if(password.trim().length == 0||password=="密码")
		{
			showMsg('请输入密码！',$(".pwinput_bg")[0]);
			$('#sPasswd').focus();
		    //simpleAlert({type:1,msg:'请输入密码！',fn:function(){setTimeout("$('#sPasswd').focus()",200);}});
			return false;
		}
		
		if(document.getElementById('sValidataCode')!=null){//如果有验证码对象
			validataCode = $('#sValidataCode').val();//验证码
			if(!validataCode||validataCode.trim().length == 0||validataCode=="验证码")
			{
				showMsg('请输入验证码！',$(".pwinput_bg2")[0]);
			   	//simpleAlert({type:1,msg:'请输入验证码！',fn:function(){setTimeout("$('#sValidataCode').focus()",200);}});
				$('#validataCode').focus();
				return false;
			}
		}
		
		if(document.getElementById("iSelRoleId") != null){
			iSelRoleId = $("#iSelRoleId").val();
		}
		if(document.getElementById("iSelDeptId")!=null){
		    iSelDeptId=$("#iSelDeptId").val();
		}
		LoginForm.hParams = {
			sAccount: account,
			sPasswd: password,
			sBrowserType:sBrowserType,
			//isMinLogin: IsEmpty(document.getElementById("isMinLogin")) ? 'no' : 'yes',
			sValidataCode:validataCode,
			iSelRoleId:iSelRoleId,
			iSelDeptId:iSelDeptId,
			isforceEncryptParams:true,//此请求为强制加密
			iMenuRightId:document.getElementById("iMenuRightId").value,
			lastUrl: IsEmpty(document.getElementById("lastUrl")) ? '' : 
				document.getElementById("lastUrl").value
		};
		if(typeof(window.top.RegistWindow)!='undefined'
			   &&window.top.RegistWindow!=null
			   &&window.top.RegistWindow.isForceLogin=="yes"){//由于session超时引起的强制登录
			LoginForm.hParams.lastUrl = window.top.RegistWindow.lastUrl;
		}
		for(var i = 0, iLen = LoginForm.aryLoginUrl.length; i < iLen; i++) 
		{
			LoginForm.aryLoginUrl[i].iLoginFailure = 0;
			LoginForm.submit(LoginForm.aryLoginUrl[i].sUrl);
		}
		load_open();
	},
	submit : function(sUrl)
	{  
		$.ajax({
			type: 'POST',
			url: encryptURL(sUrl),
			data: encryptParams(LoginForm.hParams,true),
			dataType: 'json',
			success: LoginForm.onSuccess,
			error: LoginForm.onFailure
		});
	},
	onSuccess : function(hJson, options)
	{  
		load_close();
		var flg="";
		var iTip = hJson.iTip || 2;
		if(iTip == 1){ 
			LoginForm.iCount ++;
			if(hJson.passTimeMsg!=null){
				simpleAlert({msg:hJson.passTimeMsg.trim(),type:2,icon:"question", fn:function(){
					var account = $('#sAccount').val();
					reloadCode();
					$.cookie('loginCookie', account, { expires : 30 });
					if(hJson.noq!=null){
						LoginForm.openReviseMQAA(account,'修改密码',1);
					}else {
						LoginForm.openReviseM(account,'修改密码',1);//jquery window开窗改密码
					}
				    $('#sPasswd').val("");
				},fnCancel:function(){
					LoginForm.loadMain();
				}});
				return;
			}
			LoginForm.loadMain();
		}else{
			LoginForm.iFailureCount ++;
			if(LoginForm.iFailureCount == 1)
			{
				if(hJson.iTip=="2"){//账号密码不对，提示完后要刷新页面 add by zwb
					showMsg(hJson.sMsg.trim() || '',$(".input_bg")[0]);
					reloadCode();
					//alert提示改为页面直接提示
					//simpleAlert({msg:hJson.sMsg.trim() || '', fn:function(){
						//window.location.href = path+"/login1.jsp";
					//}});
				}else if(hJson.iTip=="4" || hJson.iTip=="5" || hJson.iTip=="8"){
				    var account = $('#sAccount').val();
				    reloadCode();
				    //var child = SystemModalDialog(path + '/reviseM/revise_pword.jsp?login=1&account='+account,450,250,null);
				    if(hJson.noq!=null)LoginForm.openReviseMQAA(account,hJson.sMsg);
					else LoginForm.openReviseM(account,hJson.sMsg);//jquery window开窗改密码
				    $('#sPasswd').val("");
				}else if(hJson.iTip=="7"){
					simpleAlert({msg:'您好，当前账号正被他人登录使用中，登录IP是：'+hJson.sMsg+'。\n强行登录将迫使他人下线退出！\n\n强行登录请按“确定”，否则请按“取消”!',type:2,icon:"question", fn:function(){
						LoginForm.hParams.isForce='1';
						for(var i = 0, iLen = LoginForm.aryLoginUrl.length; i < iLen; i++) 
						{
							LoginForm.aryLoginUrl[i].iLoginFailure = 0;
							LoginForm.submit(LoginForm.aryLoginUrl[i].sUrl);
						}
					}
					});
				}else {
					//reloadCode();
				    showMsg(hJson.sMsg.trim() || '',$(".pwinput_bg2")[0]);
			    	if(hJson.iTip=="3"){
			    	    $("#sValidataCode").bind("focus",reloadCode1);
						//reloadCode();
				    }
				    if(hJson.iTip=="6" || hJson.iTip=="11" ){ //多次登录不成功把登录按钮也禁用, 6是存在的账号被锁，11是不存的账号被锁
				    	CheckRepeat.doDisabled();
				    }
				    //simpleAlert({msg:hJson.sMsg.trim() || '', fn:function(){
				    	//if(hJson.iTip=="3"){
				    		//$("#sValidataCode").val("验证码");
							//reloadCode();
					    //}
				    //}});
				}
				return false;
			}
		}
		//LoginForm.loadMain(flg);
	},
	onFailure : function(response, options)
	{
		LoginForm.iFailureTotal ++;
		if(LoginForm.iFailureTotal > LoginForm.aryLoginUrl.length * LoginForm.iTryFailureTotal)
		{
		    simpleAlert('请求失败！');
		    load_close();
			return;
		}
		for(var i = 0, iLen = LoginForm.aryLoginUrl.length; i < iLen; i++) 
		{
			if(LoginForm.aryLoginUrl[i].sUrl == options.url)
			{
				LoginForm.aryLoginUrl[i].iLoginFailure ++;
				if(LoginForm.aryLoginUrl[i].iLoginFailure <= LoginForm.iTryFailureTotal)
				{
					LoginForm.submit(LoginForm.aryLoginUrl[i].sUrl);
				}
				return;
			}
		}
		load_close();
	},
	loadMain : function(flg)
	{
		if (LoginForm.iCount == LoginForm.aryLoginUrl.length) 
		{
			$.cookie('loginCookie', null);
			$.cookie('rolCookie', null);
			$.cookie('loginCookie', $('#sAccount').val(), { expires : 30 });
			if($('#isSelRoleId').val() != ''){
				$.cookie('rolCookie', $('#iSelRoleId').val(),{expries : 30});
				$.cookie('rolNameCookie', $('#sSelRole').val(),{expries : 30});
			}
			if(typeof(window.top.RegistWindow)!='undefined'
			   &&window.top.RegistWindow!=null
			   &&window.top.RegistWindow.isForceLogin=="yes"){//由于session超时引起的强制登录
				window.top.RegistWindow.window.location.href=window.top.RegistWindow.lastUrl;
				try{
					window.top.RegistWindow=null;
					setTimeout(closeChild,1500);
				}catch(e){}
			}else if(document.getElementById("isMinLogin")&&document.getElementById("isMinLogin").value=="yes"){//迷你登录窗登录
				window.top.MinLoginSuc=true;//用于敏感菜单弹窗手动关闭与登录成功自动关闭区分
				window.top.$('#login_Win').window("close");
				window.top.MinLoginSuc=null;
			}else if(isFrameset&&isFrameset=="1"){//由ajax请求发现session超时，且原始页面是个frameset页
				window.history.back();
			}else{//正常转至首页首，或登录前访问页面 lastUrl=(path+"/?")的情况出现在 登录界面放在admin目录下的时候
				window.location=((lastUrl=="null"||lastUrl==(path+"/?"))?path+"/demo/index.jsp":lastUrl);
			}
			if(open=='1')
			{
				window.close();
			}
			document.getElementById('sPasswd').value="密码";//登录完清空密码
			$('#sValidataCode').val("验证码");
		}
	},
	openReviseM:function(account,sMsg,isSuccess){//修改密码窗
		simpleAlert({msg:sMsg,fn:function(){
			var div=document.getElementById("reviseM_Win");
			if(div!=null)$('#reviseM_Win').remove();
			if($('#reviseM_Win').length==0||(div==null||typeof(div)=='undefined')){
				div=document.createElement("DIV");
				var html='<iframe id="reviseM_Win_Frame" scrolling="no" src="'+path + '/admin/safeMgr/reviseM/revise_pword.jsp?login=2&isSuccess='+(isSuccess==1?1:0)+'&account='+account+'" frameborder="0" style="display:block;width:100%;height:100%;z-index:9000"></iframe>';
				with(div){id="reviseM_Win";innerHTML=html;}
				document.body.appendChild(div);
			}
			var options={
				title:sMsg,width:350,height:168,
				modal:true,resizable:false,collapsible:false,
		 		minimizable:false,maximizable:false,closable:false
			};
			$('#reviseM_Win').window(options);
		}});
	},
	openReviseMQAA:function(account,sMsg,isSuccess){//修改密码窗(含取回密码资料)
		simpleAlert({msg:sMsg+"\n取回密码问题答案也须补充完整！",fn:function(){
			var div=document.getElementById("reviseMQAA_Win");
			if(div!=null)$('#reviseMQAA_Win').remove();
			if($('#reviseMQAA_Win').length==0||(div==null||typeof(div)=='undefined')){
				div=document.createElement("DIV");
				var html='<iframe id="reviseM_Win_Frame" scrolling="no" src="'+path + '/admin/safeMgr/reviseM/revise_pword.jsp?noquestion=1&login=2&isSuccess='+(isSuccess==1?1:0)+'&account='+account+'" frameborder="0" style="display:block;width:100%;height:100%;z-index:9000"></iframe>';
				with(div){id="reviseMQAA_Win";innerHTML=html;}
				document.body.appendChild(div);
			}
			var options={
				title:sMsg+'(密码资料需补充)',width:350,height:228,
				modal:true,resizable:false,collapsible:false,
		 		minimizable:false,maximizable:false,closable:false
			};
			$('#reviseMQAA_Win').window(options);
		}});
	},
	openResetPwd:function(account){//取回密码窗
		var div=document.getElementById("resetPwd_Win");
		if(div!=null)$('#resetPwd_Win').remove();
		if($('#resetPwd_Win').length==0||(div==null||typeof(div)=='undefined')){
			div=document.createElement("DIV");
			var html='<iframe id="reviseM_Win_Frame" scrolling="no" src="'+path +'/admin/safeMgr/reviseM/revise_pword.jsp?account='+account+'&reset=1" frameborder="0" style="display:block;width:100%;height:100%;z-index:9000"></iframe>';
			with(div){id="resetPwd_Win";innerHTML=html;}
			document.body.appendChild(div);
		}
		var options={
			title:'取回密码',width:350,height:210,
			modal:true,resizable:false,collapsible:false,
	 		minimizable:false,maximizable:false,closable:false
		};
		$('#resetPwd_Win').window(options);
	}
});

function initJquerySelect(option){
		var text = document.getElementById(option.eId);
		if (IsEmpty(text)) return;
		
		var div = document.createElement("span");
		var table = document.createElement("table");
		$(table).css("top","-3px");
		$(table).addClass("JQSelect").attr("cellpadding", "0").attr("cellspacing", "0");
		table.width="195px";
		
		var tbody = document.createElement("tbody");
		var tr = document.createElement("tr");
		var td_1 = document.createElement("td");
		var obj = text.cloneNode(true);//重新生成一个文本框
		
		//获取文本框的样式中width属性，并设置到span，默认为100%
		$(div).css("width",IsEmpty($(text).css("width")) ? "100%" : $(text).css("width"));
		
		$(div).css({"display":$(obj).css("display")});
		if(option.clickFn){
			$(obj).bind("click", option.clickFn);
		}
		if (!IsEmpty(option.changeFn)){
			$(obj).bind("change", option.changeFn);
		}
		//将输入框本身长度设置成100%
		$(obj).addClass("txt").css("width", "100%").css("height","15px").css("border","0px");
		$(tr).append($(td_1).append(obj));
		
		var td_2=document.createElement("td");
		td_2.width="15px";
		
		var span = document.createElement("span");
		span.className = "btn2";
		$(span).css("height","18px").css("border","0px");
		$(span).html("&nbsp;&nbsp;&nbsp;&nbsp;");//对span绑定事件原input框的值
		if ($(obj).attr("disabled") == false) {
			if ($(obj).attr("disabled") == true) return;
			if(option.clickFn){
				$(span).bind("click", option.clickFn);
			}else{
				$(span).bind("click", function(){obj.click();});//点击按钮时相当于点击了文本框，用于自动初始化界面的弹出框
			}
			$(span).bind("click", function(){obj.focus()});
		}
		$(document.body).append($(div).append($(table).append($(tbody).append($(tr).append($(td_2).append(span))))));
		
		var desc = "全部";
		if (!IsEmpty(option.desc)) desc = option.desc;
		$(obj).attr("desc", desc);
		if ($(obj).val() == "") {
			$(obj).val(desc);
		}
		text.parentNode.replaceChild(div,text);//将原文框替成当前的span
	};
		
	function reflashRole(){
		if(document.getElementById("sSelRole") != null){
			document.getElementById("sSelRole").value = "登录角色";
			document.getElementById("iSelRoleId").value = "";
		}
	}


function closeChild(){
	window.top.$('#login_Win_http').window("close");
}

//密码重置，检查用户名是否输入，判断用户名是否存在，跳转密码重置页
//add by tanjianwen 2012-08-13
function checkAccount() {
	var sAccount = document.getElementById("sAccount").value.trim();
	if (sAccount == null || sAccount == '') {
		simpleAlert('请输入忘记密码的帐号!');
		return;
	}
	AjaxRequest.doRequest(null,path + '/safeMgr/login!checkAccount.action',{sAccount:sAccount}, function(backData){
		if (backData == '1' || backData == 1) {
			LoginForm.openResetPwd(sAccount);
		} else if (backData == '2' || backData == 2) {
			simpleAlert('帐号未设置好取回密码资料,不能重置密码!');
		} else {
			simpleAlert('不存在该帐号!');
		}
	});
}

//显示登录错误提示信息
function showMsg(msg,obj){
	$(".layer_form_tips").css("display","block");
	//有提示内容参数时显示提示，没有则调整显示效果
	if(msg){
		document.getElementById("failMsg").innerHTML = "&nbsp;"+msg;
	}else if(document.getElementById("failMsg").innerHTML == ""){
		return;
	}else{
		msg = document.getElementById("failMsg").innerHTML;
	}
	msgLocalOn(obj);
}
//根据当前提示内容获取，提示所占行数
function getMsgLine(){
	var line = 1;
	var msg = document.getElementById("failMsg").innerHTML;
	msg = msg.replace("&nbsp;"," ");
	var realLength = 0;
	var len = msg.length;
	var charCode;
	for (var i = 0; i < len; i++) {
        charCode = msg.charCodeAt(i);
        if (charCode >= 0 && charCode <= 128) realLength += 1;
        else realLength += 2;
    }
	if(realLength / 28 > 1){
		line = 1 + parseInt((realLength - 28) / 30);
		if(realLength - 28 != 0 && (realLength - 28) % 30 != 0){
			line++;
		}
	}
	return line;
}
//隐藏登录错误信息
function hideMsg(){
	$(".layer_form_tips").css("display","none");
	document.getElementById("failMsg").innerHTML = "";
}
var lastObj = null;
//根据对象绝对位置定位登录错误提示div
function msgLocalOn(obj){
	obj = obj?obj:lastObj;
	if(obj == null){
		obj = $(".input_bg")[0];
	}
	lastObj = obj;
	var line = getMsgLine();
	var left = obj.offsetLeft + 2;
	var top = obj.offsetTop-4-line*15;
	/**jzgj
	var tt = $(".login_main").css("top");
	if(tt && tt != "auto"){top += parseInt(tt.split("px")[0]);}
	*/
	top = top < 0?0:top;
	$(".layer_form_tips").css("left",left);
	$(".layer_form_tips").css("top",top);
}
//重置验证码
function reloadCode(flg){
       var imgId = document.getElementById("imgId");
       if(imgId == null) return ;
       if(flg!="0"){
       		$("#sValidataCode").val("验证码");
       }
       $("#sValidataCode").unbind("focus",reloadCode1);
       imgId.src=path+'/validate.jsp?rd='+new Date().getTime();
    }
function reloadCode1(){$("#sValidataCode").val("");reloadCode("0")}
var CheckRepeat = {
	doDisabled:function(){
		var btn = $("#LoginButton");
    	btn.attr("disabled","disabled");
    	$(btn).find("a").attr("disabled","disabled");
    	//btn.removeClass("LoginButton");
    	
    	$('#LoginButton').unbind("click");
        $('#sAccount').unbind("keyup");//.keyup(function(event){LoginForm.keyUp(event)});
        $('#sPasswd').unbind("keyup");//.keyup(function(event){LoginForm.keyUp(event)});
        if($('#sValidataCode').length != 0){
            $('#sValidataCode').unbind("keyup");//.keyup(function(event){LoginForm.keyUp(event)});
        }
	}
};