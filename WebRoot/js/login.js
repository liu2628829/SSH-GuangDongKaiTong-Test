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
		document.body.focus();//�ü��������ʧ��,�����������֤��������������Ϊδʧ������ˢ����֤��
		LoginForm.iCount = 0;
		LoginForm.iFailureCount = 0;
		LoginForm.iFailureTotal = 0;
		LoginForm.iTryFailureTotal = 3;
		
		var account = $('#sAccount').val();//�˺�
		var password = $('#sPasswd').val();//����
		var validataCode = "";
		var iSelRoleId = "";
		var iSelDeptId = "";
		var sBrowserType = $('#sBrowserType').val();//���������
		if(account.trim().length == 0||account=="�û���")
		{
			showMsg('�������û�����',$(".input_bg")[0]);
			$('#sAccount').focus();
			//simpleAlert({type:1,msg:'�������û�����',fn:function(){setTimeout("$('#sAccount').focus()",200);}});
			return false;
		}
		if(password.trim().length == 0||password=="����")
		{
			showMsg('���������룡',$(".pwinput_bg")[0]);
			$('#sPasswd').focus();
		    //simpleAlert({type:1,msg:'���������룡',fn:function(){setTimeout("$('#sPasswd').focus()",200);}});
			return false;
		}
		
		if(document.getElementById('sValidataCode')!=null){//�������֤�����
			validataCode = $('#sValidataCode').val();//��֤��
			if(!validataCode||validataCode.trim().length == 0||validataCode=="��֤��")
			{
				showMsg('��������֤�룡',$(".pwinput_bg2")[0]);
			   	//simpleAlert({type:1,msg:'��������֤�룡',fn:function(){setTimeout("$('#sValidataCode').focus()",200);}});
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
			isforceEncryptParams:true,//������Ϊǿ�Ƽ���
			iMenuRightId:document.getElementById("iMenuRightId").value,
			lastUrl: IsEmpty(document.getElementById("lastUrl")) ? '' : 
				document.getElementById("lastUrl").value
		};
		if(typeof(window.top.RegistWindow)!='undefined'
			   &&window.top.RegistWindow!=null
			   &&window.top.RegistWindow.isForceLogin=="yes"){//����session��ʱ�����ǿ�Ƶ�¼
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
						LoginForm.openReviseMQAA(account,'�޸�����',1);
					}else {
						LoginForm.openReviseM(account,'�޸�����',1);//jquery window����������
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
				if(hJson.iTip=="2"){//�˺����벻�ԣ���ʾ���Ҫˢ��ҳ�� add by zwb
					showMsg(hJson.sMsg.trim() || '',$(".input_bg")[0]);
					reloadCode();
					//alert��ʾ��Ϊҳ��ֱ����ʾ
					//simpleAlert({msg:hJson.sMsg.trim() || '', fn:function(){
						//window.location.href = path+"/login1.jsp";
					//}});
				}else if(hJson.iTip=="4" || hJson.iTip=="5" || hJson.iTip=="8"){
				    var account = $('#sAccount').val();
				    reloadCode();
				    //var child = SystemModalDialog(path + '/reviseM/revise_pword.jsp?login=1&account='+account,450,250,null);
				    if(hJson.noq!=null)LoginForm.openReviseMQAA(account,hJson.sMsg);
					else LoginForm.openReviseM(account,hJson.sMsg);//jquery window����������
				    $('#sPasswd').val("");
				}else if(hJson.iTip=="7"){
					simpleAlert({msg:'���ã���ǰ�˺��������˵�¼ʹ���У���¼IP�ǣ�'+hJson.sMsg+'��\nǿ�е�¼����ʹ���������˳���\n\nǿ�е�¼�밴��ȷ�����������밴��ȡ����!',type:2,icon:"question", fn:function(){
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
				    if(hJson.iTip=="6" || hJson.iTip=="11" ){ //��ε�¼���ɹ��ѵ�¼��ťҲ����, 6�Ǵ��ڵ��˺ű�����11�ǲ�����˺ű���
				    	CheckRepeat.doDisabled();
				    }
				    //simpleAlert({msg:hJson.sMsg.trim() || '', fn:function(){
				    	//if(hJson.iTip=="3"){
				    		//$("#sValidataCode").val("��֤��");
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
		    simpleAlert('����ʧ�ܣ�');
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
			   &&window.top.RegistWindow.isForceLogin=="yes"){//����session��ʱ�����ǿ�Ƶ�¼
				window.top.RegistWindow.window.location.href=window.top.RegistWindow.lastUrl;
				try{
					window.top.RegistWindow=null;
					setTimeout(closeChild,1500);
				}catch(e){}
			}else if(document.getElementById("isMinLogin")&&document.getElementById("isMinLogin").value=="yes"){//�����¼����¼
				window.top.MinLoginSuc=true;//�������в˵������ֶ��ر����¼�ɹ��Զ��ر�����
				window.top.$('#login_Win').window("close");
				window.top.MinLoginSuc=null;
			}else if(isFrameset&&isFrameset=="1"){//��ajax������session��ʱ����ԭʼҳ���Ǹ�framesetҳ
				window.history.back();
			}else{//����ת����ҳ�ף����¼ǰ����ҳ�� lastUrl=(path+"/?")����������� ��¼�������adminĿ¼�µ�ʱ��
				window.location=((lastUrl=="null"||lastUrl==(path+"/?"))?path+"/demo/index.jsp":lastUrl);
			}
			if(open=='1')
			{
				window.close();
			}
			document.getElementById('sPasswd').value="����";//��¼���������
			$('#sValidataCode').val("��֤��");
		}
	},
	openReviseM:function(account,sMsg,isSuccess){//�޸����봰
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
	openReviseMQAA:function(account,sMsg,isSuccess){//�޸����봰(��ȡ����������)
		simpleAlert({msg:sMsg+"\nȡ�����������Ҳ�벹��������",fn:function(){
			var div=document.getElementById("reviseMQAA_Win");
			if(div!=null)$('#reviseMQAA_Win').remove();
			if($('#reviseMQAA_Win').length==0||(div==null||typeof(div)=='undefined')){
				div=document.createElement("DIV");
				var html='<iframe id="reviseM_Win_Frame" scrolling="no" src="'+path + '/admin/safeMgr/reviseM/revise_pword.jsp?noquestion=1&login=2&isSuccess='+(isSuccess==1?1:0)+'&account='+account+'" frameborder="0" style="display:block;width:100%;height:100%;z-index:9000"></iframe>';
				with(div){id="reviseMQAA_Win";innerHTML=html;}
				document.body.appendChild(div);
			}
			var options={
				title:sMsg+'(���������貹��)',width:350,height:228,
				modal:true,resizable:false,collapsible:false,
		 		minimizable:false,maximizable:false,closable:false
			};
			$('#reviseMQAA_Win').window(options);
		}});
	},
	openResetPwd:function(account){//ȡ�����봰
		var div=document.getElementById("resetPwd_Win");
		if(div!=null)$('#resetPwd_Win').remove();
		if($('#resetPwd_Win').length==0||(div==null||typeof(div)=='undefined')){
			div=document.createElement("DIV");
			var html='<iframe id="reviseM_Win_Frame" scrolling="no" src="'+path +'/admin/safeMgr/reviseM/revise_pword.jsp?account='+account+'&reset=1" frameborder="0" style="display:block;width:100%;height:100%;z-index:9000"></iframe>';
			with(div){id="resetPwd_Win";innerHTML=html;}
			document.body.appendChild(div);
		}
		var options={
			title:'ȡ������',width:350,height:210,
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
		var obj = text.cloneNode(true);//��������һ���ı���
		
		//��ȡ�ı������ʽ��width���ԣ������õ�span��Ĭ��Ϊ100%
		$(div).css("width",IsEmpty($(text).css("width")) ? "100%" : $(text).css("width"));
		
		$(div).css({"display":$(obj).css("display")});
		if(option.clickFn){
			$(obj).bind("click", option.clickFn);
		}
		if (!IsEmpty(option.changeFn)){
			$(obj).bind("change", option.changeFn);
		}
		//��������������ó�100%
		$(obj).addClass("txt").css("width", "100%").css("height","15px").css("border","0px");
		$(tr).append($(td_1).append(obj));
		
		var td_2=document.createElement("td");
		td_2.width="15px";
		
		var span = document.createElement("span");
		span.className = "btn2";
		$(span).css("height","18px").css("border","0px");
		$(span).html("&nbsp;&nbsp;&nbsp;&nbsp;");//��span���¼�ԭinput���ֵ
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
		if (!IsEmpty(option.desc)) desc = option.desc;
		$(obj).attr("desc", desc);
		if ($(obj).val() == "") {
			$(obj).val(desc);
		}
		text.parentNode.replaceChild(div,text);//��ԭ�Ŀ���ɵ�ǰ��span
	};
		
	function reflashRole(){
		if(document.getElementById("sSelRole") != null){
			document.getElementById("sSelRole").value = "��¼��ɫ";
			document.getElementById("iSelRoleId").value = "";
		}
	}


function closeChild(){
	window.top.$('#login_Win_http').window("close");
}

//�������ã�����û����Ƿ����룬�ж��û����Ƿ���ڣ���ת��������ҳ
//add by tanjianwen 2012-08-13
function checkAccount() {
	var sAccount = document.getElementById("sAccount").value.trim();
	if (sAccount == null || sAccount == '') {
		simpleAlert('����������������ʺ�!');
		return;
	}
	AjaxRequest.doRequest(null,path + '/safeMgr/login!checkAccount.action',{sAccount:sAccount}, function(backData){
		if (backData == '1' || backData == 1) {
			LoginForm.openResetPwd(sAccount);
		} else if (backData == '2' || backData == 2) {
			simpleAlert('�ʺ�δ���ú�ȡ����������,������������!');
		} else {
			simpleAlert('�����ڸ��ʺ�!');
		}
	});
}

//��ʾ��¼������ʾ��Ϣ
function showMsg(msg,obj){
	$(".layer_form_tips").css("display","block");
	//����ʾ���ݲ���ʱ��ʾ��ʾ��û���������ʾЧ��
	if(msg){
		document.getElementById("failMsg").innerHTML = "&nbsp;"+msg;
	}else if(document.getElementById("failMsg").innerHTML == ""){
		return;
	}else{
		msg = document.getElementById("failMsg").innerHTML;
	}
	msgLocalOn(obj);
}
//���ݵ�ǰ��ʾ���ݻ�ȡ����ʾ��ռ����
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
//���ص�¼������Ϣ
function hideMsg(){
	$(".layer_form_tips").css("display","none");
	document.getElementById("failMsg").innerHTML = "";
}
var lastObj = null;
//���ݶ������λ�ö�λ��¼������ʾdiv
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
//������֤��
function reloadCode(flg){
       var imgId = document.getElementById("imgId");
       if(imgId == null) return ;
       if(flg!="0"){
       		$("#sValidataCode").val("��֤��");
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