  //窗体加载完毕的时候执行
 $(window).ready(function(){
 
(function(){
	//获取顶级窗口
	var topWin = getTopWin(window);
	
	//反馈模块的对象构建函数
	function Feedback(){
		this.panlHeight = 35;//面板高度
		this.panlExpandWidth =88;//面板展开的宽度
		this.panlCloseWidth = 18;//面板收缩的宽度
		//this.currentModuleURL = window.document.location.pathname;
		this.currentWindow = window;
		this.bindWindow = window;
		this.addFeedbackWinId = "ssh3_addFeedback";
		this.myFeedbackWinId = "ssh3_myFeedback";
		
		//新增修改知识窗口配置
		this.dialogOpt = {
			baseWin:this.bindWindow,
			zIndex: 9000,
			draggable:true, //拖动
			resizable:false, //改变大小
			modal: true, //后台页面可编辑
			closed: false, //是否关闭？
			minimizable: false,//最小化按钮
			maximizable: false,//最大化按钮
			closable: true, //关闭按钮
			collapsible: false //收缩按钮 
		};
	}
	//初始化反馈对象
	 var  _instance = new Feedback();
	 //初始化加载反馈div
     _initFeedbackPanl.call(_instance);
     //将反馈对象绑定到当前窗口
     _instance.bindWindow.SSH3_Feedback_Instance = _instance;
     
	 //展开或关闭
	_instance.expandOrClose = function(){
		var panlLeft;
		if(this.bindWindow.$("#ssh3_feedback_detail_div").css("display") == 'none'){
			panlLeft = _getPanlPositionLeft.call(this,this.panlExpandWidth);
			this.bindWindow.$("#ssh3_feedback_div").animate({left:panlLeft});
			//$("#ssh3_feedback_detail_div").show();
			this.bindWindow.$("#ssh3_feedback_detail_div").fadeIn(1000);
			this.bindWindow.$("#ssh3_feedback_div").css("width",this.panlExpandWidth);
		}else{
			panlLeft = _getPanlPositionLeft.call(this,this.panlCloseWidth);
			this.bindWindow.$("#ssh3_feedback_div").animate({left:panlLeft});
			//$("#ssh3_feedback_detail_div").fadeOut(1000);
			this.bindWindow.$("#ssh3_feedback_detail_div").hide();
			this.bindWindow.$("#ssh3_feedback_div").css("width",this.panlCloseWidth);
		}
	}
	
	//打开反馈窗口
	_instance.openDialog = function(flag){
		if(!flag) return;
		
		var windowPathName=this.currentWindow.document.location.pathname;
		var feedbackPath = windowPathName.substring(0,windowPathName.substr(1).indexOf('/')+1);
		//反馈链接
		var feedbackURL = windowPathName.substring(windowPathName.substr(1).indexOf('/')+2);
		var newDialogOpt = null;
		//添加反馈
		if('addFeedback' == flag){
			newDialogOpt = $.extend({}, this.dialogOpt);
			newDialogOpt.id = this.addFeedbackWinId;
			newDialogOpt.title = "新增反馈";
			newDialogOpt.url = feedbackPath + "/admin/commonModule/feedbackMgr/addOrEditFeedback.html?feedbackURL="+feedbackURL;
			newDialogOpt.width = 760;
			newDialogOpt.height = 405;
		}
		//我的反馈
		if('myFeedback'==flag){
			newDialogOpt = $.extend({}, this.dialogOpt);
			newDialogOpt.id = this.myFeedbackWinId;
			newDialogOpt.title = "我的反馈";
			newDialogOpt.url = feedbackPath+'/admin/commonModule/feedbackMgr/myFeedback.html';
			newDialogOpt.width = 800;
			newDialogOpt.height = 500;
		}
		showJqueryWindow(newDialogOpt);//showJqueryWindow方在js/common.js中
	}
	
	//重新定位
	_instance.rePositionFeedbackPanl = function(){
		//获取反馈面板
		var feedbackPanl = this.bindWindow.$("#ssh3_feedback_div");
		if(feedbackPanl.length>0){
			 var width;
			 if(this.bindWindow.$("#ssh3_feedback_detail_div").css("display") == 'none'){
			 	width=_getPanlPositionLeft.call(this,this.panlCloseWidth);
			 }else{
			 	width=_getPanlPositionLeft.call(this,this.panlExpandWidth);
			 }
			 var height=_getPanlPositionTop.call(this,this.panlHeight);
			 feedbackPanl.css("top",height);
			 feedbackPanl.css("left",width); 
		}
	}
	
	//关闭弹窗
	_instance.closeAddFeedbackWin = function(){
		//获取反馈面板
		var $addFeedbackPanl = this.bindWindow.parent.$("#"+this.addFeedbackWinId);
		$addFeedbackPanl.window("close");
	}
	
	//初始化反馈面板
	function  _initFeedbackPanl(){
		//取当前链接的参数进行判断
		var isOpenFeedBack = _isDisplayFeedBackDiv.call(this,"openFeedback");
		//获取反馈面板
		var feedbackPanl = this.bindWindow.$("#ssh3_feedback_div");
		if(isOpenFeedBack){
			//有则显示
			if(feedbackPanl.length>0){
				 feedbackPanl.show();
			}else{//否则就需要创建
				 var width=_getPanlPositionLeft.call(this,this.panlCloseWidth);
			     var height=_getPanlPositionTop.call(this,this.panlHeight);
			     var _html = "<div id='ssh3_feedback_div' style='Z-INDEX:999999;text-align:center;position:absolute;width:"+this.panlCloseWidth+"px;height:"+this.panlHeight+"px;"+
				 "top:"+height+";left:"+(width)+";background:none;opacity:1;filter:alpha(opacity=100);'>"+
				 "<div  style='float:left;width:16;'><a href='javascript:SSH3_Feedback_Instance.expandOrClose();' >反馈</a></div><div id ='ssh3_feedback_detail_div' style='display:none;float:left;width:64;'><a href='javascript:SSH3_Feedback_Instance.openDialog(\"addFeedback\");'>我要反馈</a><br><a href='javascript:SSH3_Feedback_Instance.openDialog(\"myFeedback\");'>我的反馈</a></div></div>";
				 $(_html).appendTo($(this.bindWindow.document.body));
			}
		}else{
			if(feedbackPanl.length>0){
				 feedbackPanl.hide();
			}
		}
	}
	
	/**
	*获取反馈面板左偏移宽度
	*/
	function _getPanlPositionLeft(width){
		return this.bindWindow.document.body.clientWidth-width;
	}
	
	/**
	*获取反馈面板偏移的高度
	*/
	function _getPanlPositionTop(height){
		return ((this.bindWindow.document.body.clientHeight)-height)/2;
	}
	
	/**
	*判断是否需要显示反馈面板
	*/
	function _isDisplayFeedBackDiv(name){
		var reg = new RegExp("(^|&)" + name + "=([^&]*?)(?:&|$)", "i");
		var r = this.currentWindow.location.search.substr(1).match(reg); 
		if(r != null){
			return $.trim(decodeURIComponent(r[2])) == '1' ? true : false;
		}else{
			return false;
		}
	}
	
})();

});

 //窗体大小改变的时候执行
$(window).resize(function(){
      SSH3_Feedback_Instance.rePositionFeedbackPanl();
});

