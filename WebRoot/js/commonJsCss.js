Util = {
	//�����ƻ�ȡcookie
	getCookie : function(name){
   		var cookieValue = null;
		if (document.cookie && document.cookie != '') {
		    var cookies = document.cookie.split(';');
		    for (var i = 0; i < cookies.length; i++) {
			var cookie = this.trim(cookies[i]);
			if (cookie.substring(0, name.length + 1) == (name + '=')) {
			    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
			    break;
			}
		    }
		}
		return cookieValue;
	},
	//Trims any whitespace from either side of a string
	trim : function(value){
        return String(value).replace(/^\s+|\s+$/g, "");
    },
	//�����ƻ�ȡ������ֵ
	getParameter : function(name) {    
		var reg = new RegExp("(^|&)" + name + "=([^&]*?)(?:&|$)", "i");    
		var r = location.search.substr(1).match(reg);    
		if (r != null) 
			return decodeURIComponent(r[2]); 
		return null;    
	},
	//��ȡ���в����Ķ���
	getParameterObj : function(){ 
   		var url = location.search; 
  	 	var theRequest = new Object(); 
   		if (url.indexOf("?") != -1) { 
      		var str = url.substr(1); 
      		strs = str.split("&"); 
      		for(var i = 0; i < strs.length; i ++) { 
         		theRequest[strs[i].split("=")[0]]=decodeURIComponent(strs[i].split("=")[1]); 
      		} 
   		} 
  		return theRequest; 
	},
	//��������ĸ�����"/"��Ӧ�ã���ȡ��վ��Ŀ¼,���ؽ��Ϊ��http://localhost:8080/SSH3/
	getRootPath : function(){
    	var curWwwPath=window.document.location.href;
    	var pathName=window.document.location.pathname;
    	var pos=curWwwPath.indexOf(pathName);
    	var localhostPath=curWwwPath.substring(0,pos);
    	var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    	return(localhostPath+projectName);
	},
	//��������ĸ�����"/"��Ӧ�ã���ȡӦ��Ŀ¼
	getAppPath : function(){
		var pathName=window.document.location.pathname;
		return pathName.substring(0,pathName.substr(1).indexOf('/')+1);
	}	
};

//��֪����ʲô���ã���Ҫ���β鿴�Ƿ�����
var CONTEXT_PATH_NAME = Util.getAppPath();
try{
	if(window.opener && window.opener.MSH){
		window.MSH = window.opener.MSH;
	}else if(window.parent && window.parent.MSH){
		window.MSH= window.parent.MSH;
	}else if(window.dialogArguments && window.dialogArguments.MSH){
		window.MSH= window.dialogArguments.MSH;
	}else if(window.top && window.top.MSH){
		window.MSH= window.top.MSH;
	}
}catch(e){}
//ֻ�Ƿ���ʾ���ð�ˮӡ
var waterPic=Util.getParameter("waterPic");

//��ȡ��ʽ��cookieֵ
var style = Util.getCookie("style");
if(style == null)
	style = "orange";
(function( window, undefined ){
	//����meta ��ֹ����
	document.write('<meta http-equiv="content-type" content="text/html; charset=GBK"><meta http-equiv="pragma" content="no-cache"><meta http-equiv="cache-control" content="no-cache">');
	
	//���ع���CSS�ļ�
	var cssTags = '<link rel="stylesheet" type="text/css" href="'+CONTEXT_PATH_NAME+'/css/default.css"/>'+
		'<link rel="stylesheet" id="R_Toolbar" type="text/css" href="'+CONTEXT_PATH_NAME+'/skin/'+style+'/Toolbar.css"/>'+
		'<link rel="stylesheet" id="R_easyui" type="text/css" href="'+CONTEXT_PATH_NAME+'/skin/'+style+'/easyui.css">'+
		'<link rel="stylesheet" id="R_default" type="text/css" href="'+CONTEXT_PATH_NAME+'/skin/'+style+'/default.css">'+
		'<link rel="stylesheet" id="R_style" type="text/css" href="'+CONTEXT_PATH_NAME+'/skin/'+style+'/style.css">';
	document.write(cssTags);
	
	//���ع���JS�ļ�
	var jsFile = [
		CONTEXT_PATH_NAME+"/js/jquery-1.4.2.min.js",
		CONTEXT_PATH_NAME+"/js/jquery.easyui.min.js",
		CONTEXT_PATH_NAME+"/js/jquery.cookie.js",
		CONTEXT_PATH_NAME+"/js/Toolbar.js",
		CONTEXT_PATH_NAME+"/js/common.js",
		CONTEXT_PATH_NAME+"/js/ajax-request.js",
		CONTEXT_PATH_NAME+"/js/validator.js",
		CONTEXT_PATH_NAME+"/js/tooltip_split.js",
		CONTEXT_PATH_NAME+"/js/cattMsg.js",
		//CONTEXT_PATH_NAME+"/js/watermark.js",
		CONTEXT_PATH_NAME+"/html/js/session.jsp",
		CONTEXT_PATH_NAME+"/js/feedback.js"
	];
	var jsTags = "";
	for(var i in jsFile) {
		jsTags += '<script type="text/javascript" src="' + jsFile[i] + '"></script>';
	}
	document.write(jsTags);
})( window );