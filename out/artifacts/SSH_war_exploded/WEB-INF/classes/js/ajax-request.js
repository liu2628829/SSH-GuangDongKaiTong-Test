AjaxRequest=function(){
    var timeout=100000;//超时时长
    function syn(async){ return (async!=false&&async!="false")?true:false;} 
    function showWaitBar(options){ if(typeof(WaitBar) != "undefined"&&options.showWaitBar) WaitBar.show();} 
    function hideWaitBar(options){ if(typeof(WaitBar) != "undefined"&&options.showWaitBar) WaitBar.hide();} 
    function setDefaultOptions(options){
    	return $.extend({
			showWaitBar:true,
			timeout:timeout
		},options);
    }
    //$ajax()中,timeout参数，只有在async=true(异步)时才生效
	return{
	    /*
	     * 初始化下拉列表
	     * id      列表框ID(必须)
	     * url     请求路径(必须)
	     * params  参数对象，如：{a: 'test', b: 2}
	     * async   true为异步，false为同步，默认异步 
	     * options ajax选项集
	     * 		    	showWaitBar		是否显示WaitBar,默认true
	     * 				timeout			超时时间，默认100秒
	     */
		initSelect: function(selId, url, params,async,options) {
			 options=setDefaultOptions(options);
		     var sel = document.getElementById(selId);
		     if(sel == null || typeof(sel) == "undefined") {
		         simpleAlert("此下拉列表对象不存在！");
		         return;
		     }
		     showWaitBar(options);
		     $.ajax({
				 type: 'POST',
				 url: encryptURL(url),
				 data: encryptParams(params),
				 async: syn(async),//异步，false为阻塞
				 timeout:options.timeout,//40秒后超时
				 dataType: 'json',
				 success: function(jsonData, textStatus) {
					 hideWaitBar(options);
					//系统异常
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
	     * 初始化表单
	     * formId      表单ID(必须)
	     * url         请求路径(必须)
	     * params      参数对象，如：{a: 'test', b: 2}
	     * flag        是否显示加载进度条 true显示，false为不显示
	     * fn		    执行成功后回调函数
	     * async       true为异步，false为同步，默认异步 
	     * options ajax选项集
	     * 		    	showWaitBar		是否显示WaitBar,默认true
	     * 				timeout			超时时间，默认100秒
	     * 注：    对返回json数据的键，完全忽略大小写，与表单元素的ID或name进行匹配赋值
	     */
		initForm: function(formId, url, params, flag, fn,async,options) {
			options=setDefaultOptions(options);
		     var form = document.getElementById(formId);
		     if(form == null || typeof(form) == "undefined") {
		         simpleAlert("此表单对象不存在！");
		         return;
		     }
		     showWaitBar(options);
		     $.ajax({
				 type: 'POST',
				 url: url,//encryptURL(url),
				 data: params,//encryptParams(params),
				 async: syn(async),//异步，false为阻塞
				 timeout:options.timeout,//40秒后超时
				 dataType: 'json',
				 success: function(jsonData, textStatus) {
		    	 	
					 hideWaitBar(options);
					//系统异常	
					if(typeof jsonData == "object" && jsonData.SUCCESS == "false"){
						//simpleAlert(jsonData.MESSAGE);return;
						AjaxRequest.simpleAlert(jsonData.MESSAGE);return; //update by qiaoqide
					}			
					/**判断返回的是对象，还是对象列表*/
					if(jsonData[0]!= undefined){
						jsonData = jsonData[0];
					}
					if(jsonData == null) return;
					/*复制一份全小写为键的值*/
					for(var key in jsonData){
						var tempKey=key.toLowerCase();
						jsonData[tempKey]=((jsonData[key]&&!isNaN(jsonData[key])&&jsonData[key].toString().indexOf(".")==0)?(jsonData[key]*1)+"":jsonData[key]);//oracle的时候，碰到小数，会没有小数点前的0,所以做一个*1的操作，转为数字
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
					                if(value.length>=21){//date 不要毫秒
										if(/^\d{4}-(0?[1-9]|1[0-2])-(0?[1-9]|[1-2]\d|3[0-1]) ([0-1]\d|2[0-3]):[0-5]\d:[0-5]\d\.[0-9]{1,3}$/.test(value)){				
											value=value.substring(0,19);
						   				}
					   				}
					   				if(type=="textarea")//IE8上，textarea里内容如果有换行，只显示了第一行，需要做此重绘,才能全部显示
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
					           case "radio"://单选
									var radioName = getAttribute("name").toLowerCase();
									if(jsonData[radioName] == value){
										checked = true;
									}else{
										checked = false;
									}
					                break;
					            case "checkbox": //多选(多个值用逗号分隔)
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
		
		/* initFormFn已经与此方法一致，所以不建议再使用此方法,为了不影响已有项目，暂不删除或注释掉此方法
	     * 初始化表单-带加调函数 -- add by DongXiaoFeng
	     * formId      表单ID(必须)
	     * url         请求路径(必须)
	     * params      参数对象，如：{a: 'test', b: 2}
	     * flag        是否显示加载进度条 true显示，false为不显示3
	     * fn		   (可选)回调函数(入参为从URL返回的数据)
	     * async       true为异步，false为同步，默认异步 
	     * options ajax选项集
	     * 		    	showWaitBar		是否显示WaitBar,默认true
	     * 				timeout			超时时间，默认100秒
	     * 注：对返回json数据的键，完全忽略大小写，与表单元素的ID或name进行匹配赋值
	     */ 
		initFormFn: function(formId, url, params, flag ,fn,async,options) {
			options=setDefaultOptions(options);
		     var form = document.getElementById(formId);
		     if(form == null || typeof(form) == "undefined") {
		         simpleAlert("此表单对象不存在！");
		         return;
		     }
		     showWaitBar(options);
		     $.ajax({
				 type: 'POST',
				 url: encryptURL(url),
				 data: encryptParams(params),
				 async: syn(async),//异步，false为阻塞
				 timeout:options.timeout,//40秒后超时
				 dataType: 'json',
				 success: function(jsonData, textStatus) {
				 	//simpleAlert(Ext.util.JSON.encode(jsonData));
					 hideWaitBar(options);
					//系统异常	
					if(typeof jsonData == "object" && jsonData.SUCCESS == "false"){
						//simpleAlert(jsonData.MESSAGE);return;
						AjaxRequest.simpleAlert(jsonData.MESSAGE);return; //update by qiaoqide
					}		
					//判断返回的是对象，还是对象列表
					if(jsonData[0]!= undefined){
						jsonData = jsonData[0];
					}
					if(jsonData == null) return;
					//复制一份全小写为键的值
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
								case "radio"://单选
									var radioName = getAttribute("name").toLowerCase();
									if(jsonData[radioName] == value){
										checked = true;
									}else{
										checked = false;
									}
					                break;
					            case "checkbox": //多选(多个值用逗号分隔)
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
	     * 初始化表格
	     * tableId     表格ID(必须)
	     * url         请求路径(必须)
	     * params      参数对象，如：{a: 'test', b: 2}
	     * flag        是否显示加载进度条 true显示，false为不显示
	     * async       true为异步，false为同步，默认异步 
	     * options ajax选项集
	     * 		    	showWaitBar		是否显示WaitBar,默认true
	     * 				timeout			超时时间，默认100秒
	     * 注：对返回json数据的键，完全忽略大小写，与td的ID进行匹配赋值
	     */
		initTable: function(tableId, url, params, flag,async,options) {
			options=setDefaultOptions(options);
		     var form = document.getElementById(tableId);
		     if(form == null || typeof(form) == "undefined") {
		         simpleAlert("此表格对象不存在！");
		         return;
		     }
		     showWaitBar(options);
		     $.ajax({
				 type: 'POST',
				 url: encryptURL(url),
				 data: encryptParams(params),
				 dataType: 'json',
				 async: syn(async),//异步，false为阻塞
				 timeout:options.timeout,//40秒后超时
				 success: function(jsonData, textStatus) {
					 hideWaitBar(options);
					//系统异常
					if(typeof jsonData == "object" && jsonData.SUCCESS == "false"){
						//simpleAlert(jsonData.MESSAGE);return;
						AjaxRequest.simpleAlert(jsonData.MESSAGE);return; //update by qiaoqide
					}		
					/**判断返回的是对象，还是对象列表*/
					if(jsonData[0]!= undefined){
						jsonData = jsonData[0];
					}
					if(jsonData == null) return;
					//复制一份全小写为键的值
					for(var key in jsonData){var tempKey=key.toLowerCase();jsonData[tempKey]=jsonData[key];}
					var tds = form.getElementsByTagName('td');
					if(tds == null) return;
					var count = tds.length;
					for(var i=0;i<count;i++){
					    with(tds[i]){
                         if(tds[i] == null || !getAttribute("id")) continue;
					     var cId = getAttribute("id").toLowerCase();
                         if(cId == null || cId == "") continue;
				         //var lId = cId.substring(0,1).toLowerCase() + cId.substring(1); //将首字母转为小写
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
	     * 异步请求
	     * el      表单ID
	     * url     请求路径(必须)
	     * params  参数对象，如：{a: 'test', b: 2}
	     * fn      回调函数
	     * async   true为异步，false为同步，默认异步 
	     * options ajax选项集
	     * 		    	showWaitBar		是否显示WaitBar,默认true
	     * 				timeout			超时时间，默认100秒
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
			 	    if(inp.disabled)continue;//被禁用的对像，值不提交
			 		with(inp){
			 			var cId = getAttribute("id");
			 			if(!cId){continue;} //表单里面有些标签没有 id 导致这里js 报错的
			 			var elment = document.getElementById(cId);
			 			if(elment != null && typeof(elment) != "undefined"
			 					&& !(elment.getAttribute('showDefault') == "true" && elment.getAttribute('msg') == elment.value)) {
			 			    var reg = /'/gi;
			 			    var v = value.replace(reg, ""); //不能 value = value.replace(reg, ""),在火狐上不兼容
							v = v.replace(/(^\s*)|(\s*$)/,"");//清空头尾空字符
			 				var reg = "param." + cId + " = '" + v.replace(/\\/g, "\\\\") + "'";//将"\"替换为"\\"
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
				 async: syn(async),//异步，false为阻塞
				 timeout:options.timeout||time,//40秒后超时
				 success: function(data, textStatus) {
					 hideWaitBar(options);
					//系统异常
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
	    * ajax请求失败统一处理方式,仅限于此js内部调用
	    * @param {} response(必须)
	    * @param {
	    *   skipLevel : 调用级别	默认1,1时再各默认判断前执行，5为排除各默认判断后执行
	    *   2~4 可根据指定判断类型执行回调，其他情况默认
	    *  } options(必须)
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
			//下边这个if判断必须写在其它if之前，当请求超时时，XMLHttpRequest.status在IE上报js错误解
			if(textStatus=="timeout"){
				if(!IsEmpty(options.exFn) && skipLevel==2){
					options.exFn.call(this, param);
					return;
				}
				simpleAlert({msg:"系统繁忙，请求超时，请稍后处理!",opts:{timeout:5000,winId:"catt_err_msg"}});
				return;
			}			
			if (9999 == XMLHttpRequest.status) {//session超时，开迷你登录窗
				if(!IsEmpty(options.exFn) && skipLevel==3){
					options.exFn.call(this, param);
					return;
				}
				openMinLoginWin(fn);//此方法被多处重用，在common.js中统一管理
				return;
			}
			if (8888 == XMLHttpRequest.status) {//并发量超出限制时提示
				if(!IsEmpty(options.exFn) && skipLevel==4){
					options.exFn.call(this, param);
					return;
				}
				simpleAlert({msg:"当前访问人数过多,稍后再试!",opts:{timeout:5000,winId:"catt_err_msg"}});
				return;
			}
			if(!IsEmpty(options.exFn)) {
				options.exFn.call(this, param);
				return;
			}
			simpleAlert({msg:"系统繁忙，请稍后处理!",opts:{timeout:5000,winId:"catt_err_msg"}});
			return;
			
		},
		
	    /*消息提示框
		 *@author:qiaoqide
		 *@since:2014-2-12
		 */
		simpleAlert:function(msg){
		    var m = msg.split("#%");
		    if(m && m.length > 1) { //说明是开发环境
		       AjaxRequest.customAlert(msg);
		    }else{
		       simpleAlert(msg); //生产环境
		    }
		},
		
		/*自定义带异常信息的消息提示框
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
			
			//提示图标
			var picDiv = document.createElement("div");
			$(picDiv).attr("class", "messager-icon messager-info");
			$(picDiv).attr("style", "margin-top:10px;margin-left:15px;");
			$(div).append($(picDiv));
			
			//显示消息
			var msgParentDiv = document.createElement("div");
			$(msgParentDiv).attr("style","text-align: left;");
			var msgDiv = document.createElement("div");
			$(msgDiv).attr("style", "margin-top:15px;");
			$(msgDiv).html(m[0]);
			$(div).append($(msgParentDiv).append($(msgDiv)));
			
			//按钮
			var str = '<div style="margin-top:25px;margin-bottom:5px;" class="messager-button"><a id="ok" class="l-btn" style="margin-left: 10px;" href="javascript:void(0)">';
			str += '<span class="l-btn-left">';
			str += '<span class="l-btn-text">确定</span></span></a>';
			
			str += '<a id="msgInfo" class="l-btn" style="margin-left: 10px;" href="javascript:void(0)">';
			str += '<span class="l-btn-left"><span class="l-btn-text">异常详情</span></span></a></div>';
			$(div).append(str);
			
			//显示具体的异常信息 e.getMessage();
			var exceptionDiv = document.createElement("div");
			//$(exceptionDiv).attr("style","display: none;width:435px;");
			$(exceptionDiv).attr("style","display: none;");
			$(exceptionDiv).attr("id","exceptionDiv");
			var textarea = "<textarea style='width:100%;' id='event_edit_area' readonly>";
			    textarea += "根异常名称：" + m[3]+"\n";
			    textarea += "根异常描述：" + m[1]+"\n";
			    textarea += "异常程序路径：\n\t " + m[2].replace(/\|/g, '\n\t');
			    textarea += "</textarea>";
			$(exceptionDiv).html(textarea);
			$(div).append($(exceptionDiv));
			document.body.appendChild(div);
			
			//绑定按钮事件
			$("#ok").bind("click",function(){closeWin();});
			var flg = true;
			$("#msgInfo").bind("click", function(){
			   if(flg){
				   $("#exceptionDiv").show();
				   showWin();
				   $("#msgInfo .l-btn-text").html("关闭详情");
				   flg = false;
			   }else{
			       $("#exceptionDiv").hide();
				   $("#simpleAlert_Div").window({width:width,height:height});
				   $("#msgInfo .l-btn-text").html("异常详情");
				   flg = true;
			   }
			   setCenter();
			});
			
			$("#simpleAlert_Div").window({
				title:"提示",
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
			
			//窗体自适应
			$(window).bind("resize",function(){
			   if(!flg){
			     showWin();
			   }else{
			     $("#simpleAlert_Div").window({height:height, width:width});
			     setCenter();
			   }
			 });
			
			//重置div高宽与位置
			function showWin(){
			   var w = $(window).width()*0.6;
			   var h = $(window).height()*0.7;
			   $("#simpleAlert_Div").window({height:h, width:w});
			   $("#event_edit_area").height($("#simpleAlert_Div").height()-90);
			   setCenter();
			}
			
			//div居中
			function setCenter(){
			   //设置居中
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
