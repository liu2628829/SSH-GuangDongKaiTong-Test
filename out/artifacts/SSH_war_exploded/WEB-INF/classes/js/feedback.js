  //���������ϵ�ʱ��ִ��
 $(window).ready(function(){
 
(function(){
	//��ȡ��������
	var topWin = getTopWin(window);
	
	//����ģ��Ķ��󹹽�����
	function Feedback(){
		this.panlHeight = 35;//���߶�
		this.panlExpandWidth =88;//���չ���Ŀ��
		this.panlCloseWidth = 18;//��������Ŀ��
		//this.currentModuleURL = window.document.location.pathname;
		this.currentWindow = window;
		this.bindWindow = window;
		this.addFeedbackWinId = "ssh3_addFeedback";
		this.myFeedbackWinId = "ssh3_myFeedback";
		
		//�����޸�֪ʶ��������
		this.dialogOpt = {
			baseWin:this.bindWindow,
			zIndex: 9000,
			draggable:true, //�϶�
			resizable:false, //�ı��С
			modal: true, //��̨ҳ��ɱ༭
			closed: false, //�Ƿ�رգ�
			minimizable: false,//��С����ť
			maximizable: false,//��󻯰�ť
			closable: true, //�رհ�ť
			collapsible: false //������ť 
		};
	}
	//��ʼ����������
	 var  _instance = new Feedback();
	 //��ʼ�����ط���div
     _initFeedbackPanl.call(_instance);
     //����������󶨵���ǰ����
     _instance.bindWindow.SSH3_Feedback_Instance = _instance;
     
	 //չ����ر�
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
	
	//�򿪷�������
	_instance.openDialog = function(flag){
		if(!flag) return;
		
		var windowPathName=this.currentWindow.document.location.pathname;
		var feedbackPath = windowPathName.substring(0,windowPathName.substr(1).indexOf('/')+1);
		//��������
		var feedbackURL = windowPathName.substring(windowPathName.substr(1).indexOf('/')+2);
		var newDialogOpt = null;
		//��ӷ���
		if('addFeedback' == flag){
			newDialogOpt = $.extend({}, this.dialogOpt);
			newDialogOpt.id = this.addFeedbackWinId;
			newDialogOpt.title = "��������";
			newDialogOpt.url = feedbackPath + "/admin/commonModule/feedbackMgr/addOrEditFeedback.html?feedbackURL="+feedbackURL;
			newDialogOpt.width = 760;
			newDialogOpt.height = 405;
		}
		//�ҵķ���
		if('myFeedback'==flag){
			newDialogOpt = $.extend({}, this.dialogOpt);
			newDialogOpt.id = this.myFeedbackWinId;
			newDialogOpt.title = "�ҵķ���";
			newDialogOpt.url = feedbackPath+'/admin/commonModule/feedbackMgr/myFeedback.html';
			newDialogOpt.width = 800;
			newDialogOpt.height = 500;
		}
		showJqueryWindow(newDialogOpt);//showJqueryWindow����js/common.js��
	}
	
	//���¶�λ
	_instance.rePositionFeedbackPanl = function(){
		//��ȡ�������
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
	
	//�رյ���
	_instance.closeAddFeedbackWin = function(){
		//��ȡ�������
		var $addFeedbackPanl = this.bindWindow.parent.$("#"+this.addFeedbackWinId);
		$addFeedbackPanl.window("close");
	}
	
	//��ʼ���������
	function  _initFeedbackPanl(){
		//ȡ��ǰ���ӵĲ��������ж�
		var isOpenFeedBack = _isDisplayFeedBackDiv.call(this,"openFeedback");
		//��ȡ�������
		var feedbackPanl = this.bindWindow.$("#ssh3_feedback_div");
		if(isOpenFeedBack){
			//������ʾ
			if(feedbackPanl.length>0){
				 feedbackPanl.show();
			}else{//�������Ҫ����
				 var width=_getPanlPositionLeft.call(this,this.panlCloseWidth);
			     var height=_getPanlPositionTop.call(this,this.panlHeight);
			     var _html = "<div id='ssh3_feedback_div' style='Z-INDEX:999999;text-align:center;position:absolute;width:"+this.panlCloseWidth+"px;height:"+this.panlHeight+"px;"+
				 "top:"+height+";left:"+(width)+";background:none;opacity:1;filter:alpha(opacity=100);'>"+
				 "<div  style='float:left;width:16;'><a href='javascript:SSH3_Feedback_Instance.expandOrClose();' >����</a></div><div id ='ssh3_feedback_detail_div' style='display:none;float:left;width:64;'><a href='javascript:SSH3_Feedback_Instance.openDialog(\"addFeedback\");'>��Ҫ����</a><br><a href='javascript:SSH3_Feedback_Instance.openDialog(\"myFeedback\");'>�ҵķ���</a></div></div>";
				 $(_html).appendTo($(this.bindWindow.document.body));
			}
		}else{
			if(feedbackPanl.length>0){
				 feedbackPanl.hide();
			}
		}
	}
	
	/**
	*��ȡ���������ƫ�ƿ��
	*/
	function _getPanlPositionLeft(width){
		return this.bindWindow.document.body.clientWidth-width;
	}
	
	/**
	*��ȡ�������ƫ�Ƶĸ߶�
	*/
	function _getPanlPositionTop(height){
		return ((this.bindWindow.document.body.clientHeight)-height)/2;
	}
	
	/**
	*�ж��Ƿ���Ҫ��ʾ�������
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

 //�����С�ı��ʱ��ִ��
$(window).resize(function(){
      SSH3_Feedback_Instance.rePositionFeedbackPanl();
});

