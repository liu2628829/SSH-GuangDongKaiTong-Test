AjaxRequest=function(){
    var timeout=100000;//��ʱʱ��
    function syn(async){ return (async!=false&&async!="false")?true:false;} 
    function showWaitBar(options){ if(typeof(WaitBar) != "undefined"&&options.showWaitBar) WaitBar.show();} 
    function hideWaitBar(options){ if(typeof(WaitBar) != "undefined"&&options.showWaitBar) WaitBar.hide();} 
    function setDefaultOptions(options){
    	return $.extend({
			showWaitBar:true,
			timeout:timeout
		},options);
    }
    //$ajax()��,timeout������ֻ����async=true(�첽)ʱ����Ч
	return{
	    /*
	     * ��ʼ�������б�
	     * id      �б��ID(����)
	     * url     ����·��(����)
	     * params  ���������磺{a: 'test', b: 2}
	     * async   trueΪ�첽��falseΪͬ����Ĭ���첽 
	     * options ajaxѡ�
	     * 		    	showWaitBar		�Ƿ���ʾWaitBar,Ĭ��true
	     * 				timeout			��ʱʱ�䣬Ĭ��100��
	     */
		initSelect: function(selId, url, params,async,options) {
			 options=setDefaultOptions(options);
		     var sel = document.getElementById(selId);
		     if(sel == null || typeof(sel) == "undefined") {
		         simpleAlert("�������б���󲻴��ڣ�");
		         return;
		     }
		     showWaitBar(options);
		     $.ajax({
				 type: 'POST',
				 url: encryptURL(url),
				 data: encryptParams(params),
				 async: syn(async),//�첽��falseΪ����
				 timeout:options.timeout,//40���ʱ
				 dataType: 'json',
				 success: function(jsonData, textStatus) {
					 hideWaitBar(options);
					//ϵͳ�쳣
					if(typeof jsonData == "object" && jsonData.SUCCESS == "false"){
						//simpleAlert(jsonData.MESSAGE);return;
						AjaxRequest.simpleAlert(jsonData.MESSAGE);return; //update by qiaoqide
					}
					if (jsonData == null) return;
					for (var i = 0; i < jsonData.length; i++) {
						var option = new Option();
						option.text = jsonData[i].text;
						option.value = jsonData[i].value;
						sel.add(option);
					}
			 	 },
			 	 error: function(XMLHttpRequest, textStatus, errorThrown) {
			 		AjaxRequest.dealFailue(XMLHttpRequest, textStatus, function(){AjaxRequest.initSelect(selId, url, params,async)}, options);
			 	 }
			 });
		},
		/*
	     * ��ʼ����
	     * formId      ��ID(����)
	     * url         ����·��(����)
	     * params      ���������磺{a: 'test', b: 2}
	     * flag        �Ƿ���ʾ���ؽ����� true��ʾ��falseΪ����ʾ
	     * fn		    ִ�гɹ���ص�����
	     * async       trueΪ�첽��falseΪͬ����Ĭ���첽 
	     * options ajaxѡ�
	     * 		    	showWaitBar		�Ƿ���ʾWaitBar,Ĭ��true
	     * 				timeout			��ʱʱ�䣬Ĭ��100��
	     * ע��    �Է���json���ݵļ�����ȫ���Դ�Сд�����Ԫ�ص�ID��name����ƥ�丳ֵ
	     */
		initForm: function(formId, url, params, flag, fn,async,options) {
			options=setDefaultOptions(options);
		     var form = document.getElementById(formId);
		     if(form == null || typeof(form) == "undefined") {
		         simpleAlert("�˱����󲻴��ڣ�");
		         return;
		     }
		     showWaitBar(options);
		     $.ajax({
				 type: 'POST',
				 url: url,//encryptURL(url),
				 data: params,//encryptParams(params),
				 async: syn(async),//�첽��falseΪ����
				 timeout:options.timeout,//40���ʱ
				 dataType: 'json',
				 success: function(jsonData, textStatus) {
		    	 	
					 hideWaitBar(options);
					//ϵͳ�쳣	
					if(typeof jsonData == "object" && jsonData.SUCCESS == "false"){
						//simpleAlert(jsonData.MESSAGE);return;
						AjaxRequest.simpleAlert(jsonData.MESSAGE);return; //update by qiaoqide
					}			
					/**�жϷ��ص��Ƕ��󣬻��Ƕ����б�*/
					if(jsonData[0]!= undefined){
						jsonData = jsonData[0];
					}
					if(jsonData == null) return;
					/*����һ��ȫСдΪ����ֵ*/
					for(var key in jsonData){
						var tempKey=key.toLowerCase();
						jsonData[tempKey]=((jsonData[key]&&!isNaN(jsonData[key])&&jsonData[key].toString().indexOf(".")==0)?(jsonData[key]*1)+"":jsonData[key]);//oracle��ʱ������С������û��С����ǰ��0,������һ��*1�Ĳ�����תΪ����
					}
					var count = form.elements.length;
					for(var i=0;i<count;i++){
					    with(form.elements[i]){
					        if(form.elements[i] == null || !getAttribute("id")) continue;
					        var lId = getAttribute("id").toLowerCase();
					        switch(type){
					            case "text":
					            case "textarea":
					            case "hidden":
					            case "password":
					                temp = jsonData[lId];
					                if(temp==null){value="";break;}
					                value = temp.toString().trim();
					                if(value.length>=21){//date ��Ҫ����
										if(/^\d{4}-(0?[1-9]|1[0-2])-(0?[1-9]|[1-2]\d|3[0-1]) ([0-1]\d|2[0-3]):[0-5]\d:[0-5]\d\.[0-9]{1,3}$/.test(value)){				
											value=value.substring(0,19);
						   				}
					   				}
					   				if(type=="textarea")//IE8�ϣ�textarea����������л��У�ֻ��ʾ�˵�һ�У���Ҫ�����ػ�,����ȫ����ʾ
					   				   form.elements[i].style.width=form.elements[i].clientWidth||form.elements[i].style.width;
					                break;
					            case "select-one":
					            	if(IsEmpty(jsonData[lId])){
					            		value = "";
					            	}else{
					            		value = jsonData[lId];
					            	}
					                break;
					            /*case "checkbox":
					                value = jsonData[lId] || 0;
					                if(value == 0)
					                	checked = false;
					                else
					                    checked = true;
					                break;*/
					           case "radio"://��ѡ
									var radioName = getAttribute("name").toLowerCase();
									if(jsonData[radioName] == value){
										checked = true;
									}else{
										checked = false;
									}
					                break;
					            case "checkbox": //��ѡ(���ֵ�ö��ŷָ�)
					            	var checkboxName = getAttribute("name").toLowerCase();
									var cValues = jsonData[checkboxName];
									if(cValues){
										var checkFlag = false;
										var valueArr = cValues.split(',');
										for(var k = 0; k < valueArr.length; k++){
											if (value == valueArr[k]) {
												checkFlag = true;
												break;
											}
										}
										if(checkFlag){
											checked = true;
										}else{
											checked = false;
										}
									}else{
										checked = false;
									}
									break;
					            default:
					                break;
					        }
					    }
					}
					if(typeof(fn)!="undefined"&&fn!=null){fn.call(this,jsonData);}
			 	 },
			 	 error: function(XMLHttpRequest, textStatus, errorThrown) {
			 		AjaxRequest.dealFailue(XMLHttpRequest, textStatus, function(){AjaxRequest.initForm(formId, url, params, flag, fn,async) }, options);
			 	 }
			 });
		},
		
		/* initFormFn�Ѿ���˷���һ�£����Բ�������ʹ�ô˷���,Ϊ�˲�Ӱ��������Ŀ���ݲ�ɾ����ע�͵��˷���
	     * ��ʼ����-���ӵ����� -- add by DongXiaoFeng
	     * formId      ��ID(����)
	     * url         ����·��(����)
	     * params      ���������磺{a: 'test', b: 2}
	     * flag        �Ƿ���ʾ���ؽ����� true��ʾ��falseΪ����ʾ3
	     * fn		   (��ѡ)�ص�����(���Ϊ��URL���ص�����)
	     * async       trueΪ�첽��falseΪͬ����Ĭ���첽 
	     * options ajaxѡ�
	     * 		    	showWaitBar		�Ƿ���ʾWaitBar,Ĭ��true
	     * 				timeout			��ʱʱ�䣬Ĭ��100��
	     * ע���Է���json���ݵļ�����ȫ���Դ�Сд�����Ԫ�ص�ID��name����ƥ�丳ֵ
	     */ 
		initFormFn: function(formId, url, params, flag ,fn,async,options) {
			options=setDefaultOptions(options);
		     var form = document.getElementById(formId);
		     if(form == null || typeof(form) == "undefined") {
		         simpleAlert("�˱����󲻴��ڣ�");
		         return;
		     }
		     showWaitBar(options);
		     $.ajax({
				 type: 'POST',
				 url: encryptURL(url),
				 data: encryptParams(params),
				 async: syn(async),//�첽��falseΪ����
				 timeout:options.timeout,//40���ʱ
				 dataType: 'json',
				 success: function(jsonData, textStatus) {
				 	//simpleAlert(Ext.util.JSON.encode(jsonData));
					 hideWaitBar(options);
					//ϵͳ�쳣	
					if(typeof jsonData == "object" && jsonData.SUCCESS == "false"){
						//simpleAlert(jsonData.MESSAGE);return;
						AjaxRequest.simpleAlert(jsonData.MESSAGE);return; //update by qiaoqide
					}		
					//�жϷ��ص��Ƕ��󣬻��Ƕ����б�
					if(jsonData[0]!= undefined){
						jsonData = jsonData[0];
					}
					if(jsonData == null) return;
					//����һ��ȫСдΪ����ֵ
					for(var key in jsonData){var tempKey=key.toLowerCase();jsonData[tempKey]=jsonData[key];}
					var count = form.elements.length;
					for(var i=0;i<count;i++){
					    with(form.elements[i]){
					        if(form.elements[i] == null || !getAttribute("id")) continue;
					        var lId = getAttribute("id").toLowerCase();
					        switch(type){
					            case "text":
					            case "textarea":
					            case "hidden":
					            case "password":
					                temp =jsonData[lId];
					                if(temp==null){value="";break;}
					                value = temp.toString().trim();
					                if(value.length>=21){//date
										if(/^\d{4}-(0?[1-9]|1[0-2])-(0?[1-9]|[1-2]\d|3[0-1]) ([0-1]\d|2[0-3]):[0-5]\d:[0-5]\d\.[0-9]{1,3}$/.test(value)){				
											value=value.substring(0,19);
						   				}
					   				}
					                break;
					            case "select-one":
					                value = jsonData[lId] || 0;
					                break;
								case "radio"://��ѡ
									var radioName = getAttribute("name").toLowerCase();
									if(jsonData[radioName] == value){
										checked = true;
									}else{
										checked = false;
									}
					                break;
					            case "checkbox": //��ѡ(���ֵ�ö��ŷָ�)
					            	var checkboxName = getAttribute("name").toLowerCase();
									var cValues = jsonData[checkboxName];
									if(cValues){
										var checkFlag = false;
										var valueArr = cValues.split(',');
										for(var k = 0; k < valueArr.length; k++){
											if (value == valueArr[k]) {
												checkFlag = true;
												break;
											}
										}
										if(checkFlag){
											checked = true;
										}else{
											checked = false;
										}
									}else{
										checked = false;
									}
									break;
					            default:
					                break;
					        }
					    }
					}
					if(fn){fn.call(this,jsonData);}
			 	 },
			 	 error: function(XMLHttpRequest, textStatus, errorThrown) {
			 		AjaxRequest.dealFailue(XMLHttpRequest, textStatus, function(){AjaxRequest.initFormFn(formId, url, params, flag ,fn,async)}, options);
			 	 }
			 });
		},
		/*
	     * ��ʼ�����
	     * tableId     ���ID(����)
	     * url         ����·��(����)
	     * params      ���������磺{a: 'test', b: 2}
	     * flag        �Ƿ���ʾ���ؽ����� true��ʾ��falseΪ����ʾ
	     * async       trueΪ�첽��falseΪͬ����Ĭ���첽 
	     * options ajaxѡ�
	     * 		    	showWaitBar		�Ƿ���ʾWaitBar,Ĭ��true
	     * 				timeout			��ʱʱ�䣬Ĭ��100��
	     * ע���Է���json���ݵļ�����ȫ���Դ�Сд����td��ID����ƥ�丳ֵ
	     */
		initTable: function(tableId, url, params, flag,async,options) {
			options=setDefaultOptions(options);
		     var form = document.getElementById(tableId);
		     if(form == null || typeof(form) == "undefined") {
		         simpleAlert("�˱����󲻴��ڣ�");
		         return;
		     }
		     showWaitBar(options);
		     $.ajax({
				 type: 'POST',
				 url: encryptURL(url),
				 data: encryptParams(params),
				 dataType: 'json',
				 async: syn(async),//�첽��falseΪ����
				 timeout:options.timeout,//40���ʱ
				 success: function(jsonData, textStatus) {
					 hideWaitBar(options);
					//ϵͳ�쳣
					if(typeof jsonData == "object" && jsonData.SUCCESS == "false"){
						//simpleAlert(jsonData.MESSAGE);return;
						AjaxRequest.simpleAlert(jsonData.MESSAGE);return; //update by qiaoqide
					}		
					/**�жϷ��ص��Ƕ��󣬻��Ƕ����б�*/
					if(jsonData[0]!= undefined){
						jsonData = jsonData[0];
					}
					if(jsonData == null) return;
					//����һ��ȫСдΪ����ֵ
					for(var key in jsonData){var tempKey=key.toLowerCase();jsonData[tempKey]=jsonData[key];}
					var tds = form.getElementsByTagName('td');
					if(tds == null) return;
					var count = tds.length;
					for(var i=0;i<count;i++){
					    with(tds[i]){
                         if(tds[i] == null || !getAttribute("id")) continue;
					     var cId = getAttribute("id").toLowerCase();
                         if(cId == null || cId == "") continue;
				         //var lId = cId.substring(0,1).toLowerCase() + cId.substring(1); //������ĸתΪСд
                         //var reg = "jsonData." + cId;var lreg = "jsonData." + lId;
                         var reg=jsonData[cId];
                         //innerHTML = (eval(reg) || eval(lreg) || '&nbsp;')+'&nbsp;';
                         innerHTML = (reg || '&nbsp;')+'&nbsp;';
					    }
					}
			 	 },
			 	 error: function(XMLHttpRequest, textStatus, errorThrown) {
			 		AjaxRequest.dealFailue(XMLHttpRequest, textStatus, function(){AjaxRequest.initTable(tableId, url, params, flag,async)} , options);
			 	 }
			 });
		},
		/*
	     * �첽����
	     * el      ��ID
	     * url     ����·��(����)
	     * params  ���������磺{a: 'test', b: 2}
	     * fn      �ص�����
	     * async   trueΪ�첽��falseΪͬ����Ĭ���첽 
	     * options ajaxѡ�
	     * 		    	showWaitBar		�Ƿ���ʾWaitBar,Ĭ��true
	     * 				timeout			��ʱʱ�䣬Ĭ��100��
	     */
		doRequest: function(el, url, params, fn, async,options) {
			 options=setDefaultOptions(options);
		     var param = params;
		     if(!param){param = new Object();}
		     var obj = document.getElementById(el);
		     /*var param = new Object();
			 if(el == null || el == '' || obj == null) {
			 	param =  params;
			 }else{*/
			 if(!(el == null || el == '' || obj == null)) {
			 	for(var i=0; i<obj.elements.length; i++) {
			 	    var inp = obj.elements[i];
			 	    if(inp.disabled)continue;//�����õĶ���ֵ���ύ
			 		with(inp){
			 			var cId = getAttribute("id");
			 			if(!cId){continue;} //��������Щ��ǩû�� id ��������js �����
			 			var elment = document.getElementById(cId);
			 			if(elment != null && typeof(elment) != "undefined"
			 					&& !(elment.getAttribute('showDefault') == "true" && elment.getAttribute('msg') == elment.value)) {
			 			    var reg = /'/gi;
			 			    var v = value.replace(reg, ""); //���� value = value.replace(reg, ""),�ڻ���ϲ�����
							v = v.replace(/(^\s*)|(\s*$)/,"");//���ͷβ���ַ�
			 				var reg = "param." + cId + " = '" + v.replace(/\\/g, "\\\\") + "'";//��"\"�滻Ϊ"\\"
			 				eval(reg);
			 			}
			 		}
			 	}
			 }
			 showWaitBar(options);
			 var time = (params?(params.timeout?(!isNaN(params.timeout)?params.timeout:timeout):timeout):timeout);
			 $.ajax({
				 type: 'POST',
				 url: encryptURL(url),
				 data: encryptParams(param),
				 dataType: 'text',
				 async: syn(async),//�첽��falseΪ����
				 timeout:options.timeout||time,//40���ʱ
				 success: function(data, textStatus) {
					 hideWaitBar(options);
					//ϵͳ�쳣
					var jsonData = decode(data);		
					if(jsonData && typeof jsonData == "object" && jsonData.SUCCESS == "false"){
						//simpleAlert(jsonData.MESSAGE);return;
						AjaxRequest.simpleAlert(jsonData.MESSAGE);return;
					}		
					fn.call(this, data);
			 	 },
			 	 error: function(XMLHttpRequest, textStatus) {
			 		AjaxRequest.dealFailue(XMLHttpRequest, textStatus, function(){AjaxRequest.doRequest(el, url, params, fn, async)}, options);
			 	 }
			 });
		},
	    /**
	    * ajax����ʧ��ͳһ����ʽ,�����ڴ�js�ڲ�����
	    * @param {} response(����)
	    * @param {
	    *   skipLevel : ���ü���	Ĭ��1,1ʱ�ٸ�Ĭ���ж�ǰִ�У�5Ϊ�ų���Ĭ���жϺ�ִ��
	    *   2~4 �ɸ���ָ���ж�����ִ�лص����������Ĭ��
	    *  } options(����)
	    */
		dealFailue:function(XMLHttpRequest, textStatus, fn, options) {
			options=setDefaultOptions(options);
			hideWaitBar(options);
			
			var param = {
				textStatus:textStatus,
				XMLHttpRequest:XMLHttpRequest
			};
			var skipLevel = 1;
			if(!IsEmpty(options.skipLevel)){
				skipLevel = options.skipLevel;
			}
			
			if(!IsEmpty(options.exFn) && skipLevel==1){
				options.exFn.call(this, param);
				return;
			}
			//�±����if�жϱ���д������if֮ǰ��������ʱʱ��XMLHttpRequest.status��IE�ϱ�js�����
			if(textStatus=="timeout"){
				if(!IsEmpty(options.exFn) && skipLevel==2){
					options.exFn.call(this, param);
					return;
				}
				simpleAlert({msg:"ϵͳ��æ������ʱ�����Ժ���!",opts:{timeout:5000,winId:"catt_err_msg"}});
				return;
			}			
			if (9999 == XMLHttpRequest.status) {//session��ʱ���������¼��
				if(!IsEmpty(options.exFn) && skipLevel==3){
					options.exFn.call(this, param);
					return;
				}
				openMinLoginWin(fn);//�˷������ദ���ã���common.js��ͳһ����
				return;
			}
			if (8888 == XMLHttpRequest.status) {//��������������ʱ��ʾ
				if(!IsEmpty(options.exFn) && skipLevel==4){
					options.exFn.call(this, param);
					return;
				}
				simpleAlert({msg:"��ǰ������������,�Ժ�����!",opts:{timeout:5000,winId:"catt_err_msg"}});
				return;
			}
			if(!IsEmpty(options.exFn)) {
				options.exFn.call(this, param);
				return;
			}
			simpleAlert({msg:"ϵͳ��æ�����Ժ���!",opts:{timeout:5000,winId:"catt_err_msg"}});
			return;
			
		},
		
	    /*��Ϣ��ʾ��
		 *@author:qiaoqide
		 *@since:2014-2-12
		 */
		simpleAlert:function(msg){
		    var m = msg.split("#%");
		    if(m && m.length > 1) { //˵���ǿ�������
		       AjaxRequest.customAlert(msg);
		    }else{
		       simpleAlert(msg); //��������
		    }
		},
		
		/*�Զ�����쳣��Ϣ����Ϣ��ʾ��
		 *@author:qiaoqide
		 *@since:2014-2-12
		 */
		customAlert:function(msg){
		    var width = 300;  height=0;
		    var m = msg.split("#%");
		    var div = document.createElement("div");
			div.style.overflow = "hidden";
			with(div){id = "simpleAlert_Div";}
			div.className = "easyui-layout";
			$(div).css("text-align","center");
			
			//��ʾͼ��
			var picDiv = document.createElement("div");
			$(picDiv).attr("class", "messager-icon messager-info");
			$(picDiv).attr("style", "margin-top:10px;margin-left:15px;");
			$(div).append($(picDiv));
			
			//��ʾ��Ϣ
			var msgParentDiv = document.createElement("div");
			$(msgParentDiv).attr("style","text-align: left;");
			var msgDiv = document.createElement("div");
			$(msgDiv).attr("style", "margin-top:15px;");
			$(msgDiv).html(m[0]);
			$(div).append($(msgParentDiv).append($(msgDiv)));
			
			//��ť
			var str = '<div style="margin-top:25px;margin-bottom:5px;" class="messager-button"><a id="ok" class="l-btn" style="margin-left: 10px;" href="javascript:void(0)">';
			str += '<span class="l-btn-left">';
			str += '<span class="l-btn-text">ȷ��</span></span></a>';
			
			str += '<a id="msgInfo" class="l-btn" style="margin-left: 10px;" href="javascript:void(0)">';
			str += '<span class="l-btn-left"><span class="l-btn-text">�쳣����</span></span></a></div>';
			$(div).append(str);
			
			//��ʾ������쳣��Ϣ e.getMessage();
			var exceptionDiv = document.createElement("div");
			//$(exceptionDiv).attr("style","display: none;width:435px;");
			$(exceptionDiv).attr("style","display: none;");
			$(exceptionDiv).attr("id","exceptionDiv");
			var textarea = "<textarea style='width:100%;' id='event_edit_area' readonly>";
			    textarea += "���쳣���ƣ�" + m[3]+"\n";
			    textarea += "���쳣������" + m[1]+"\n";
			    textarea += "�쳣����·����\n\t " + m[2].replace(/\|/g, '\n\t');
			    textarea += "</textarea>";
			$(exceptionDiv).html(textarea);
			$(div).append($(exceptionDiv));
			document.body.appendChild(div);
			
			//�󶨰�ť�¼�
			$("#ok").bind("click",function(){closeWin();});
			var flg = true;
			$("#msgInfo").bind("click", function(){
			   if(flg){
				   $("#exceptionDiv").show();
				   showWin();
				   $("#msgInfo .l-btn-text").html("�ر�����");
				   flg = false;
			   }else{
			       $("#exceptionDiv").hide();
				   $("#simpleAlert_Div").window({width:width,height:height});
				   $("#msgInfo .l-btn-text").html("�쳣����");
				   flg = true;
			   }
			   setCenter();
			});
			
			$("#simpleAlert_Div").window({
				title:"��ʾ",
				width:width,
				//height:130,
				modal:true,
				collapsible:false,
				minimizable:false,
				maximizable:false,
				resizable: false,
				closable: false,
				onClose:function(){
					$("#simpleAlert_Div").remove();
				}
			});
			
			height = $("#simpleAlert_Div").height()+35;
			
			//��������Ӧ
			$(window).bind("resize",function(){
			   if(!flg){
			     showWin();
			   }else{
			     $("#simpleAlert_Div").window({height:height, width:width});
			     setCenter();
			   }
			 });
			
			//����div�߿���λ��
			function showWin(){
			   var w = $(window).width()*0.6;
			   var h = $(window).height()*0.7;
			   $("#simpleAlert_Div").window({height:h, width:w});
			   $("#event_edit_area").height($("#simpleAlert_Div").height()-90);
			   setCenter();
			}
			
			//div����
			function setCenter(){
			   //���þ���
			   var w = $("#simpleAlert_Div").width();
			   var h = $("#simpleAlert_Div").height();
			   var div_left=($(window).width()-w)*0.5;
			   var div_top=($(window).height()-h)*0.5;
			   $("#simpleAlert_Div").window({top:div_top, left:div_left});
			}
			function closeWin(){
			   $("#simpleAlert_Div").window("close");
			   if($("#simpleAlert_Div").length > 0){
			      $("#simpleAlert_Div").remove();
			   }
			}
		}
		
	}
}();
