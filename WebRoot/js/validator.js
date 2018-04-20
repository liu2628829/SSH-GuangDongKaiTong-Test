//表单验证类
Validator = function(){
	//常用的正则表达式
    var trimRe = /^\s+|\s+$/g;
	var emptyRe = /^\s*$/;
	var errorTip = {
	    zh      : "请输入中文",
	    telephone:"请输入一个正确的电话号码",
	    mobilephone:"请输入一个正确的手机号码",
	    combinephone:"请输入一个正确的电话号码", //固话与手机的组合，不分前后，英文逗号分隔
		ip		: "请输入一个有效的IP",
		ipv6	: "请输入一个有效的IPv6",
		postcode: "请输入一个有效的邮编",
		require : "这是必填的",
		email	: "请输入一个有效的电子邮件",
		number	: "请输入一个有效的数字",
		digit	: "请输入一个有效的整数",
		alpha	: "只能是字母",
		alphanum: "只能是字母和数字",
		url		: "请输入一个有效网址",
		filter	: "不符合过滤规则",
		limit	: "内容超过了最大长度",
		limitZh : "内容超过了最大长度",
		repeat  : "两次的输入不一致",
		range   : "不在规定的范围内",
		custom  : "不符合自定义的规则",
		compare : "比较验证不成立",
		compare2: "比较验证不成立",
		email_limit: "请输入一个有效的电子邮件,并且内容超过了最大长度",
        number_limit: "请输入一个有效的数字,并且内容超过了最大长度",
        digit_limit: "请输入一个有效的整数,并且内容超过了最大长度",
        alpha_limit: "只能是字母,并且内容超过了最大长度",
        alphanum_limit: "只能是字母和数字,并且内容超过了最大长度",
        url_limit: "请输入一个有效网址,并且内容超过了最大长度",
        password: "密码最小长度是8位,最大长度是32位,必须同时包含数字、字母(区分大小写)和特殊符号(!#&$)",
        specialString : "内容不能存在特殊字符"
	};
	var specialStrings = ["'","\""," drop "," create "," exec "," insert "," select "," delete "," update "," or ",
						  " and "," truncate "," execute "," grant "," use "," group_concat "," union "];
	/*var setDisabled=function(){
			//设置遮罩
			 var div=document.getElementById("VALIDATOR_MODAL_DIV");
			 if(!div){
			 div=document.createElement("div");
			 div.id="VALIDATOR_MODAL_DIV";
			 document.body.appendChild(div);
			 	 div.className = "Mask";
				 with(div.style){
				 	width=document.body.clientWidth;
				    height=document.body.clientHeight;
				 }   
			}
			div.style.display="block";
		}
		
	var cancleDisabled=function (){
				/*lis=$(":input[value='保存']");
				lis.attr("disabled",false);
				lis=$(":input[value='确认']");
				lis.attr("disabled",false);
				//撤消遮罩
				document.getElementById("VALIDATOR_MODAL_DIV").style.display="none";
				$('#VALIDATOR_MODAL_DIV').css("z-index", "-1");
		}*/
	
	return{
	    zh:/^[\u4e00-\u9fa5],{0,}$/,
	    telephone:/^(\(\d{3,4}\)|\d{3,4}-)?\d{7,8}$/,
	    mobilephone:/^1[0-9][0-9]\d{8}$/, ///^1[3|4|5|8][0-9]\d{8}$/, 第2位不再限制
		combinephone:/^(\(\d{3,4}\)|\d{3,4}-)?\d{7,8}\s*[\,]\s*1[3|4|5|8][0-9]\d{8}|1[3|4|5|8][0-9]\d{8}\s*[\,]\s*(\(\d{3,4}\)|\d{3,4}-)?\d{7,8}|1[3|4|5|8][0-9]\d{8}|(\(\d{3,4}\)|\d{3,4}-)?\d{7,8}$/,
		ip:/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/,
		ipv6:/^([\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$|^::([\da-fA-F]{1,4}:){0,4}((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$|^([\da-fA-F]{1,4}:):([\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$|^([\da-fA-F]{1,4}:){2}:([\da-fA-F]{1,4}:){0,2}((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$|^([\da-fA-F]{1,4}:){3}:([\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$|^([\da-fA-F]{1,4}:){4}:((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$|^([\da-fA-F]{1,4}:){7}[\da-fA-F]{1,4}$|^:((:[\da-fA-F]{1,4}){1,6}|:)$|^[\da-fA-F]{1,4}:((:[\da-fA-F]{1,4}){1,5}|:)$|^([\da-fA-F]{1,4}:){2}((:[\da-fA-F]{1,4}){1,4}|:)$|^([\da-fA-F]{1,4}:){3}((:[\da-fA-F]{1,4}){1,3}|:)$|^([\da-fA-F]{1,4}:){4}((:[\da-fA-F]{1,4}){1,2}|:)$|^([\da-fA-F]{1,4}:){5}:([\da-fA-F]{1,4})?$|^([\da-fA-F]{1,4}:){6}:$/,
		postcode:/^(\d){6}$/,		
		require : /^\s+|\s+$/g,
		email : /^([\w]+)(.[\w]+)*@([\w-]+\.){1,5}([A-Za-z]){2,4}$/,
		number : /^[-+]?\d*\.?\d+$/,
		digit : /^[-+]?[0-9]+$/,
		alpha : /^[a-z ._-]+$/i,
		alphanum : /^[a-zA-Z0-9_]+$/,
		url :  /(((https?)|(ftp)):\/\/([\-\w]+\.)+\w{2,3}(\/[%\-\w]+(\.\w{2,})?)*(([\w\-\.\?\\\/+@&#;`~=%!]*)(\.\w{2,})?)*\/?)/i,
		filter : "Validator.doFilter(value, getAttribute('accept'))",
		limit : "Validator.doLimit(value.length,getAttribute('min'),getAttribute('max'))",
		limitZh : "Validator.doLimit(Validator.lenZh(value), getAttribute('min'),getAttribute('max'))",
		repeat : "!Validator.isEmpty(value,false) && value == document.getElementsByName(getAttribute('to'))[0].value",
		range : "getAttribute('min') < (value|0) && (value|0) < getAttribute('max')",
		custom : "Validator.doExec(value, getAttribute('regexp'))",
		compare : "Validator.doCompare(value,getAttribute('operator'),document.getElementsByName(getAttribute('to'))[0].value)",
		compare2 : "Validator.doCompare(value,getAttribute('operator'),getAttribute('to'))",
		compareNum: "Validator.doCompareNum(value,getAttribute('operator'),document.getElementsByName(getAttribute('to'))[0].value)",
		password: "Validator.doPassword(value)",
		errorItem : [document.forms[0]],
		errorMessage : ["以下原因导致提交失败：\t\t\t\t"],
		/*email_limit : true,
		number_limit : true,
		digit_limit : true,
		alpha_limit : true,
		alphanum_limit : true,
		url_limit : true,*/
		//表单初始化,用于表单项失焦时就触发验证
		init : function(theForm,mode)
		{
			var obj = theForm || document.forms[0];
			var count = obj.elements.length;
			var thisObj=this;
			for(var i=0;i<count;i++){
				with(obj.elements[i]){
					var _dataType = getAttribute("dataType");
					if(typeof(_dataType) == "object" || this.isEmpty(_dataType,false))  continue;// typeof(this[_dataType]) == "undefined"
					obj.elements[i].onblur=function(){
						thisObj.clearState(this);
						var _dataType= getAttribute("dataType");
						var _msg = getAttribute("msg");
						if(_msg == null)
							_msg= errorTip[_dataType];
						switch(_dataType){
							case "repeat" :
							case "range" :
							case "custom" :
							case "limit" :
							case "limitZh" :
							case "compare" :
							case "compare2" :
							case "compareNum":
							case "filter" :
							case "password" :
								if(!eval(thisObj[_dataType]))    
								{
									thisObj.doErrorTip(mode,_msg,this);
									return false;
								}
								break;
							case "require" :
								if(thisObj.isEmpty(value,false))
								{
									thisObj.doErrorTip(mode,_msg,this);
									return false;
								}
								break;
							default :
								var ar = _dataType.split("_");
								if(ar.length>1) {
									if(!thisObj[ar[0]].test(value) || !eval(thisObj[ar[1]]))
									{
										thisObj.addError(i, getAttribute("msg"),_dataType);
									}
								}else {
									if(thisObj[_dataType]&&!thisObj[_dataType].test(value))
									{
										thisObj.addError(i, getAttribute("msg"),_dataType);
									}
								}
								break;
						}
					}
				}
			}
		},
		/*
		初始化验证元素
		add by tanjianwen 2011-12-22
		传入参数option内容详情:
		[{
		    eId:元素ID,
		    datatype:[require|ip|email|number|digit|range|alpha|alphanum|url|
		    		  limit|limitZh|repeat|custom|compare|compare2|compareNum|
		    		  ip_require|digit_range|alpha_limit] 验证类型,
		    require: [true|false] 是否必填,
		    min: 数字 datatype是[range|limit|limitZh]时必填,
		    max: 数字 同上,
		    to: datatype是repeat时必填,
		    regexp: 当datatype设置为custom时,这里填上验证的正则表达式,
		    ...其他略
		    msg: 验证到错误时的错误提示说明(可选,建议填上)
		}{...}]
		*/
		initValidate : function(option){
			try {
				for (var i = 0;i < option.length; i++) {
					var e;//指定元素对象
					for (var attr in option[i]) {
						if (attr == 'eId') {
							e = document.getElementById(option[i][attr]);
							continue;
						}
						e.setAttribute(attr, option[i][attr]);//对元素添加验证设置			
					}
				}
			} catch (err) {
				simpleAlert('验证初始化失败,请检查配置是否正确!');
			}
		},
		/*
		表单验证
		update by tanjianwen 2012-5-10
		theForm : 如果类型是字符串,代表表单ID,否则是表单对象 (当form对象没有action属性值时，则不会提交，只做效验, 回调函数依然会调用)
		mode : 错误显示类型(默认是2,代表报错显示类型为弹出窗)
		fn : 回调函数
		msg : 外部错误信息数组(必须是数组)
		async : 是否同步(默认同步)
		opts : 传入js对象,对象中添加width属性,指定报错窗口的宽度,例子 {width:500}
		  |- width
		  |- win 调用消息框的窗口对象
		  |- special true|false 是否验证特殊字符 默认false
		*/
		validate : function(theForm,mode,fn,msg,async,opts){
			var obj=(typeof(theForm)=="string")?document.getElementById(theForm):theForm;
			if(obj==null||obj==undefined)obj=event.srcElement;
			if(obj==null||obj==undefined||obj.nodeName!="FORM"){simpleAlert("没有指定要提交的表单!");return;}
			
			//setDisabled();
			var count = obj.elements.length;
			this.errorMessage.length = 1;
			this.errorItem.length = 1;
			this.errorItem[0] = obj;
			
			var hintInputs = [];
			for(var i=0; i<obj.elements.length; i++) {
				var input = obj.elements[i];
				if (input.getAttribute('showDefault') == "true" && input.getAttribute('msg') == input.value) {
					input.value = "";
					hintInputs.push(input);
				}
			}
			
			for(var i=0;i<count;i++){
				with(obj.elements[i]){
					if((disabled==true||disabled=="disabled")&&getAttribute("submitValue")!="true"&&getAttribute("submitValue")!=true)continue;
					value=Validator.trim(value,"");//value.replace(/(^\s*)|(\s*$)/,"");//清空首尾空字符	
					if (opts && opts.special == true)this.validateSpecial(obj.elements[i], i);//特殊字符验证
					var _dataType = getAttribute("dataType");
					if(typeof(_dataType) == "object" ||this.isEmpty(_dataType,false))  continue;//|| typeof(this[_dataType]) == "undefined"
					this.clearState(obj.elements[i]);
					//if(_dataType != "require" && getAttribute("require") != "true" && value == "") continue;
					if(_dataType.indexOf("require")<0 && getAttribute("require") != "true" && value == "") continue;
					
					if(getAttribute("require") == "true"&&this.isEmpty(value,false)){
						this.addError(i, getAttribute("msg"),_dataType);continue;
					}
					switch(_dataType){
						case "repeat" :
						case "range" :
						case "custom" :
						case "limit" :
						case "limitZh" :
						case "compare" :
						case "compare2" :
						case "compareNum" :
						case "filter" :
						case "password" :
							if(!eval(this[_dataType]))    
							{
								this.addError(i, getAttribute("msg"),_dataType);
							}
							break;
						case "require" :
							if(this.isEmpty(value,false))
							{
								this.addError(i, getAttribute("msg"),_dataType);
							}
							break;
						default :
							var ar = _dataType.split("_");
							if(ar.length>1) {
								/*这种方式，要求第一个必须为正则效验，而第二个是可执行的字符串,就不能让现有属性自由组装
								if(!this[ar[0]].test(value) || !eval(this[ar[1]]))
								{
									this.addError(i, getAttribute("msg"),_dataType);
								}*/
								var boo1=true,boo2=true,boo3=true; 
								if(ar[0]=="require"||ar[1]=="require"){boo3=!(this.isEmpty(value,false));}
								if(ar[0]!="require"){
									if(typeof(this[ar[0]])=="object") boo1=this[ar[0]].test(value);
									else  boo1=eval(this[ar[0]]);
								}
								if(ar[1]!="require"){
									if(typeof(this[ar[1]])=="object") boo2=this[ar[1]].test(value);
									else  boo2=eval(this[ar[1]]);
								}
								if(!(boo1&&boo2&boo3)){	this.addError(i, getAttribute("msg"),_dataType);}
						    }else {
								if(this[_dataType]&&!this[_dataType].test(value))
								{
									this.addError(i, getAttribute("msg"),_dataType);
								}
							}
							break;
					}
				}
			}
			
			//加外部传入错误信息
			if(msg&&typeof(msg)=="object"){
				for(var j=0;j<msg.length;j++){
					this.errorMessage[this.errorMessage.length] = this.errorMessage.length + ":" + msg[j];
				}
			}
			if(this.errorMessage.length > 1){
				for (var i = 0;i < hintInputs.length;i++) {
					hintInputs[i].value = hintInputs[i].msg;
				}
				mode = mode || 2;
				var errCount = this.errorItem.length;
				var alertOpts = {type:1};
				alertOpts.opts = {width : (!opts || !opts.width ? 300 : opts.width)};
				switch(mode)
				{
					case 2 :
						for(var i=1;i<errCount;i++){
							$("#"+this.errorItem[i].id).css({color:"red",backgroundColor:"#FFF2F2"});
							if(this.errorItem[i].type=="hidden"&&document.getElementById(this.errorItem[i].id+"_text")){//针对弹窗选，下拉多选等控件，重置背景色
								$("#"+this.errorItem[i].id+"_text").css({color:"red",backgroundColor:"#FFF2F2"});
							}
						}
					case 1 :
						alertOpts.msg = this.errorMessage.join("\n");
						break;
					case 3 :
						for(var i=1;i<errCount;i++){
						try{
								var span = document.createElement("SPAN");
								span.id = "__ErrorMessagePanel";
								span.style.color = "red";
								this.errorItem[i].parentNode.appendChild(span);
								span.innerHTML = this.errorMessage[i].replace(/\d+:/,"*");
							}
							catch(e){
								alertOpts.msg = e.description;
							}
						}
						this.errorItem[1].focus();
						break;
					default :
						alertOpts.msg = this.errorMessage.join("\n");
						break;
				}
				var w = opts && opts.win ? opts.win : (getTopWin(window)?getTopWin(window):window);//默认在顶级窗体弹出
				w.simpleAlert(alertOpts);
				//cancleDisabled();
				return false;
			}
			//return true;
			//如果通过表单验证,则提交表单
			this.submitForm(obj,fn,async);
			for (var i = 0;i < hintInputs.length;i++) {
				hintInputs[i].value = hintInputs[i].msg;
			}
		},
		/*
		通用组件表单验证
		update by dengjianfei 2013-1-4
		element : 需要验证的对象数组 或 容器ID 或 校验规则
		mode : 错误显示类型(默认是2,代表报错显示类型为弹出窗)
		fn : 回调函数
		msg : 外部错误信息数组(必须是数组)
		async : 是否同步(默认同步)
		opts : 传入js对象,对象中添加width属性,指定报错窗口的宽度,例子 {width:500}
		  |- width
		  |- win 调用消息框的窗口对象
		  |- special true|false 是否验证特殊字符 默认false
		*/
		validate_dgt : function(element,mode,fn,msg,async,opts){
			var objs;
			var fb = true; //校验规则
			var filterObj = {}; //单选复选表单元素对象
			
			//处理容器
			if(typeof element=="string"){
				var idStr = ""; //id选择器字符串
				var eles = element.split(",");
				for(var i = 0; i < eles.length; i++){
					if(i > 0){idStr += ",";}
					idStr += "#" + eles[i];
				}
				
				objs = $("*[dataType]", idStr);//"#"+element
				fb = false;
			}else{
				objs = element;
				if(objs.length>0 && objs[0].getAttribute){fb = false;}
			}
			
			if(objs == null || objs.length == 0){return true;}
			
			this.errorMessage.length = 1;
			this.errorItem.length = 1;
			this.errorItem[0] = objs;
			
			var hintInputs = [];
			if(!fb){
				for(var i=0; i<objs.length; i++) {
					var input = objs[i];
					if ((input.getAttribute('showDefault') == "true" && input.getAttribute('msg') == input.value)
					     ||(input.getAttribute('desc') == input.value)) {
						input.value = "";
						hintInputs.push(input);
					}
				}
			}
			
			//过滤出radio,checkbox表单元素
			filterObj = this.filterElements(objs);
			
			//表单元素校验
			for(var i = 0; i < objs.length; i++){
				this.validateElement(i, objs[i], opts, fb);
			}
			
			//校验单选、复选按钮
			this.validRadioAndCheckbox(filterObj);
			
			//加外部传入错误信息
			if(msg && typeof(msg) == "object"){
				for(var j=0;j<msg.length;j++){
					this.errorMessage[this.errorMessage.length] = this.errorMessage.length + ":" + msg[j];
				}
			}else if(msg && typeof(msg) == "string"){
				this.errorMessage[this.errorMessage.length] = this.errorMessage.length + ":" + msg;
			}
			
			if(this.errorMessage.length > 1){
			
				if(fb){
					var rel = [];
					for(var i=1;i<this.errorMessage.length;i++){
						var eo = {id:this.errorItem[i].id, text:this.errorItem[i].text, msg:this.errorMessage[i]}
						rel.push(eo);
					}
					return rel;
				}
			
				for (var i = 0;i < hintInputs.length;i++) {
					if (input.getAttribute('showDefault') == "true" && input.getAttribute('msg') == input.value){
						hintInputs[i].value = hintInputs[i].getAttribute('msg');
					}else{
						hintInputs[i].value = hintInputs[i].getAttribute('desc');
					}
				}
				mode = mode || 2;
				var errCount = this.errorItem.length;
				var alertOpts = {type:1};
				alertOpts.opts = {width : (!opts || !opts.width ? 300 : opts.width)};
				switch(mode)
				{
					case 2 :
						for(var i=1;i<errCount;i++){
							$("#"+this.errorItem[i].id).css({color:"red",backgroundColor:"#FFF2F2"});
							if(this.errorItem[i].type=="hidden"&&document.getElementById(this.errorItem[i].id+"_text")){//针对弹窗选，下拉多选等控件，重置背景色
								$("#"+this.errorItem[i].id+"_text").css({color:"red",backgroundColor:"#FFF2F2"});
							}
						}
					case 1 :
						alertOpts.msg = this.errorMessage.join("\n");
						break;
					case 3 :
						for(var i=1;i<errCount;i++){
							try{
								var span = document.createElement("SPAN");
								span.id = "__ErrorMessagePanel";
								span.style.color = "red";
								this.errorItem[i].parentNode.appendChild(span);
								span.innerHTML = this.errorMessage[i].replace(/\d+:/,"*");
							}
							catch(e){
								alertOpts.msg = e.description;
							}
						}
						this.errorItem[1].focus();
						break;
					default :
						alertOpts.msg = this.errorMessage.join("\n");
						break;
				}
				var w = opts && opts.win ? opts.win : (getTopWin(window)?getTopWin(window):window);//默认在顶级窗体弹出
				w.simpleAlert(alertOpts);
				//cancleDisabled();
				return false;
			}
			if(fb)return [];
			return true;
			//如果通过表单验证,则提交表单
			//this.submitForm(obj,fn,async);
			for (var i = 0;i < hintInputs.length;i++) {
				hintInputs[i].value = hintInputs[i].msg;
			}
		},
		/**
		 *校验
		 */
		validateElement:function(i,obj,opts,fb){
			var disabled = fb?false:obj.getAttribute("disabled");
			var submitValue = fb?"":obj.getAttribute("submitValue");
			var _dataType = fb?obj.dataType:obj.getAttribute("dataType");
			var require = fb?obj.require:obj.getAttribute("require");
			var msg = fb?obj.msg:obj.getAttribute("msg");
			var value = obj.value;
			
			if((disabled==true||disabled=="disabled")&&submitValue!="true"&&submitValue!=true)return;
			if(isNaN(value))//如果是非数字类型则清空首尾空字符
			   Validator.trim(value,"");//value=value.replace(/(^\s*)|(\s*$)/,"");//清空首尾空字符		
			if (opts && opts.special == true)this.validateSpecial(objs[i], i,fb);//特殊字符验证
			
			if(require == "true"&&this.isEmpty(value,false)){
				this.addError(i, msg,"require",fb);return;
			}
			
			if(typeof(_dataType) == "object" ||this.isEmpty(_dataType,false))  return false;//|| typeof(this[_dataType]) == "undefined"
			if(!fb)this.clearState(obj);
			//if(_dataType != "require" && getAttribute("require") != "true" && value == "") continue;
			if(_dataType.indexOf("require")<0 && require != "true" && value == "") return;
			
			function getAttribute(attr){
				if(fb)return obj[attr];
				return obj.getAttribute(attr);
			}
			
			switch(_dataType){
				case "repeat" :
				case "range" :
				case "custom" :
				case "limit" :
				case "limitZh" :
				case "compare" :
				case "compare2" :
				case "compareNum" :
				case "filter" :
				case "password" :
					if(!eval(this[_dataType]))    
					{
						this.addError(i, msg,_dataType,fb);
					}
					break;
				case "require" :
					if(this.isEmpty(value,false))
					{
						this.addError(i, msg,_dataType,fb);
					}
					break;
				default :
					var ar = _dataType.split(",");
					if(ar.length > 1) {
						//配置的校验类型为 require,limitZh,mobilephone等，需要对每一项进行校验
						for(var index = 0; index < ar.length; index++){
							
							var boo = true;
							if(ar[index] == "require"){
								boo = !this.isEmpty(value, false);
							}else{
								if(typeof(this[ar[index]]) == "object"){
									boo = this[ar[index]].test(value);
								}else{
									boo = eval(this[ar[index]]);
								}
							}
							
							//i 为方法入参
							if(!boo){this.addError(i, msg,_dataType, fb); break;}
						}
						
				    }else {
						if(this[_dataType]&&!this[_dataType].test(value))
						{
							this.addError(i, msg,_dataType,fb);
						}
					}
					break;
			}
		},
		
		/**
		 * 过滤表单元素中的radio和checkbox
		 * objs 表单元素对象
		 * add by zhanweibin 2013-8-8
		 */
		filterElements: function(objs){
			var filterObj = {};
			
			for(var i = objs.length - 1; i >= 0; i--){
				var type = $(objs[i]).attr("type");
				var dataType = $(objs[i]).attr("dataType");
				var name = $(objs[i]).attr("name");
				
				//单选、复选按钮，并且是必填的，记录的临时变量中
				if((type == "radio" || type == "checkbox") && dataType == "require"){
					if(!filterObj[name]){
						filterObj[name] = [];
					}
					filterObj[name].push(objs[i]);
					//将元素移除，不走常规校验的逻辑
					objs.splice(i, 1);
				}
			}
			
			return filterObj;
		},
		
		/**
		 * 校验单选、复选按钮是否必填
		 * add by zhanweibin 2013-8-8
		 * obj 过滤出来的radio,checkbox元素，格式为：
		 * {
		 *   'name1': [ele1, ele2,...],
		 *   'name2': [ele1, ele2,...]...
		 * }
		 */
		validRadioAndCheckbox: function(obj){
			//按name分组来校验，eles为相同name的dom元素
			var validEach = function(eles){
				var isChecked = false;
				for(var i = 0; i < eles.length; i++){
					var check = $(eles[i]).attr("checked");
					if(check){
						isChecked = true;
						break;
					}
				}
				return isChecked;
			};
			
			for(var o in obj){
				var isChecked = validEach(obj[o]);
				if(!isChecked){
					this.errorMessage[this.errorMessage.length] = this.errorMessage.length + ":" 
									+ (IsEmpty($(obj[o][0]).attr("msg")) ? errorTip['require'] : $(obj[o][0]).attr("msg"));
				}
			}
			
		},
		
		/**
		 * 获取验证提示信息(通用表单)
		 * add by dengjianfei 2013-1-8
		 */
		getErrorTip:function(dataType){
			return errorTip[dataType];
		},
		/*特殊字符串验证
		  1.系统已经默认有以下特殊字符串：
			', drop , create , exec , insert , select , delete , update , or ,
			and , truncate , execute , grant , use , group_concat , union 
			当输入的信息包含以上特殊字符串时，验证将会报错,提示存在特殊字符串；
			
		  2.如果想要附加特殊字符串，在input标签里添加extraSpecial属性,
		     写上自指定要验证的特殊字符串,多个以逗号隔开,如 ：extraSpecial="test,test1,test2"
		     如果想要指定的特殊字符串包含逗号,请在逗号前添加转义符"\",如extraSpecial="\,,test"；
		     
		  3.如果要跳过某些特殊字符串不验证,在input标签里添加ignoreSpecial属性,
		    写上指定的特殊字符串,如果有多个以逗号隔开,如 ：ignoreSpecial="test,test1,test2"
		    如果想要指定的特殊字符串包含逗号,请在逗号前添加转义符"\",如ignoreSpecial="\,,test"；
		    
		  4.自定义的错误描述信息,在input标签里面添加specialMsg属性,例如：specialMsg="XXX不能包含特殊字符"
		*/
		validateSpecial : function(obj, index,fb) {
			var ignoreSpecial = fb?obj.ignoreSpecial:obj.getAttribute("ignoreSpecial");//忽略的特殊字符
			var extraSpecial = fb?obj.extraSpecial:obj.getAttribute("extraSpecial");//附加的特殊字符
			var specialMsg = fb?obj.specialMsg:obj.getAttribute("specialMsg");//附加的特殊字符
			var specials = [];
			for (var j = 0; j < specialStrings.length; j++) {specials.push(specialStrings[j]);}
			if (extraSpecial != null) {
				var extras = extraSpecial.replace("\\,", "##douhao##").split(",");
				for (var j = 0; j < extras.length; j++) {specials.push(extras[j].replace("##douhao##", ","));}
			}
			var ignoreSpecials = [];
			if (ignoreSpecial != null) {
				var ignores = ignoreSpecial.replace("\\,", "##douhao##").split(",");
				for (var j = 0; j < ignores.length; j++) {ignoreSpecials.push(ignores[j].replace("##douhao##", ","));}
			}
			for (var j = 0; j < specials.length; j++) {
				if (obj.value.indexOf(specials[j]) >= 0) {
					var flag = false;
					for (var m = 0; m < ignoreSpecials.length; m++) {
						if (specials[j] == ignoreSpecials[m]) {
							flag = true;
							break;
						}
					}
					if (flag) continue;
					else if (specialMsg == null) {
						this.addError(index, "内容不能存在特殊字符：\""+specials[j]+"\"", "specialString");
					} else {
						this.addError(index, specialMsg+"\""+specials[j]+"\"", "specialString");
					}
					break;
				}
			}
		},
		//错误提示
		doErrorTip:function(mode,msg,obj)
		{
			mode = mode || 2;
			switch(mode)
			{
				case 2:
				case 1:
					obj.style.color = "red";
					break;
				case 3:
					try{
							var span = document.createElement("SPAN");
							span.id = "__ErrorMessagePanel";
							span.style.color = "red";
							obj.parentNode.appendChild(span);
							span.innerHTML = msg;
						}
						catch(e){}
					break;
				default:
					obj.style.color = "red";
					break;
			}
		},
		//去掉字符串两端的空格
        trim : function(value){
            return String(value).replace(trimRe, "");
        },
		//是否为空
		isEmpty : function(v, allowBlank){
            return v === null || v === undefined || (!allowBlank ? emptyRe.test(v) : false);
        },
		//长度限制在一定范围
		doLimit : function(len,min, max)
		{
			min = min || 0;
			max = max || Number.MAX_VALUE;
			return min <= len && len <= max;
		},
		doCompare : function(op1,operator,op2){
			switch (operator) {
				case "NotEqual":
					return (op1 != op2);
				case "GreaterThan":
					return (op1 > op2);
				case "GreaterThanEqual":
					return (op1 >= op2);
				case "LessThan":
					return (op1 < op2);
				case "LessThanEqual":
					return (op1 <= op2);
				default:
					return (op1 == op2);           
			}
		},
		doCompareNum:function(op1,operator,op2){
			if(isNaN(op1*1)||isNaN(op2*1))return false;
			var o1=op1*1,o2=op2*1;
			switch (operator) {
				case "NotEqual":
					return (o1 != o2);
				case "GreaterThan":
					return (o1 > o2);
				case "GreaterThanEqual":
					return (o1 >= o2);
				case "LessThan":
					return (o1 < o2);
				case "LessThanEqual":
					return (o1 <= o2);
				default:
					return (o1 == o2);           
			}
		},
		//中文是两个字节
		lenZh : function(str){
			return str.replace(/[^\x00-\xff]/g,"**").length;
		},
		//清除状态
		clearState : function(elem){
			with(elem){
				if(style.color == "red"){
					style.color = "";
					style.backgroundColor = "";
				}
				if(type == "hidden"&&document.getElementById(id+"_text")){//针对弹窗选，下拉多选等控件，重置背景色
					$("#"+id+"_text").css({color:"",backgroundColor:""});
				}
				var lastNode = parentNode.childNodes[parentNode.childNodes.length-1];
				if(lastNode.id == "__ErrorMessagePanel")
					parentNode.removeChild(lastNode);
			}
		},
		//增加错误信息
		addError : function(index,str,dataType,fb){
			if(str == null)
				str= errorTip[dataType];
			/** edit by dengjianfei 2013-1-4 通用表单组件验证***/
			if(this.errorItem[0].elements!=undefined)this.errorItem[this.errorItem.length] = this.errorItem[0].elements[index];
			else this.errorItem[this.errorItem.length] = this.errorItem[0][index];
			/************************/
			if(fb)this.errorMessage[this.errorMessage.length] = str;
			else this.errorMessage[this.errorMessage.length] = this.errorMessage.length + ":" + str;
		},
		//测试字符串op是否符合正则对象reg所设定的规则
		doExec : function(op, reg){
			return new RegExp(reg,"g").test(op);
		},
		//对字符串进行过滤
		doFilter : function(input, filter){
			return new RegExp("^.+\.(?=EXT)(EXT)$".replace(/EXT/g, filter.split(/\s*,\s*/).join("|")), "gi").test(input);
		},
		//执行密码格式判断
		doPassword : function(value) {
			return ((value.length >= 8 && value.length <=32) && 
					new RegExp("^\\S*[0-9]+\\S*$").test(value) && 
					new RegExp("^\\S*[a-zA-Z]+\\S*$").test(value) &&
					new RegExp("^\\S*[\!|\#|\&|\$]+\\S*$").test(value) &&
					!new RegExp("^\\S*[^a-zA-Z0-9\!\#\&\$]+\\S*$").test(value)
					);
		},
		//提交表单函数
		submitForm:function(pForm,fn,async)
		{
			if(!pForm.action){//cancleDisabled();
			if(fn&&typeof(fn)=="function")fn.call(this);return;}//仅是数据效验，并不提交，常用批量操作时，单行界面端处理
			if(typeof(WaitBar) != "undefined")WaitBar.show(1);
			$(pForm).ajaxSubmit({
					type: 'POST',
					dataType: 'text',
					async: (async!=false&&async!="false")?true:false,//异步，false为阻塞
				 	timeout:40000,
					success: function(data, statusText) {
						if(typeof(WaitBar) != "undefined") 
							WaitBar.hide();
						//系统异常
						var jsonData;
						try{
							jsonData = decode(data);//当反回的是非数值字符串时，此句会报异常	
							if(typeof jsonData == "object" && jsonData.SUCCESS == "false"){
								simpleAlert(jsonData.MESSAGE);//cancleDisabled();
								return;
							}
						}catch(e){//cancleDisabled();
						}	
						fn.call(this, jsonData);
						//cancleDisabled();
					},
					error:function(response, options){
						Validator.dealFailue(response, options, function(){Validator.submitForm(pForm,fn,async)});
					}
			});
		},
		/**
	    * ajax请求失败统一处理方式,仅限于此js内部调用
	    * @param {} response(必须)
	    * @param {} options(必须)
	    */
		dealFailue:function(XMLHttpRequest, textStatus, fn) {
			if (typeof(WaitBar) != "undefined") {WaitBar.hide();}
			//下边这个if判断必须写在其它if之前，当请求超时时，XMLHttpRequest.status在IE上报js错误解
			if(textStatus=="timeout"){
				simpleAlert({msg:"系统繁忙，请求超时，请稍后处理!",opts:{timeout:5000,winId:"catt_err_msg"}});
				return;
			}			
			if (9999 == XMLHttpRequest.status) {//session超时，开迷你登录窗
				openMinLoginWin(fn);//此方法被多处重用，在common.js中统一管理
				return;
			}
			if (8888 == XMLHttpRequest.status) {//并发量超出限制时提示
				simpleAlert({msg:"当前访问人数过多,稍后再试!",opts:{timeout:5000,winId:"catt_err_msg"}});
				return;
			}
			simpleAlert({msg:"系统繁忙，请稍后处理!",opts:{timeout:5000,winId:"catt_err_msg"}});
			return;
		}
	}
}();


/******************************************************************************************************
 * 以下是jquery.form.js的内容,功能是可以直接异步提交form表单
 */
(function($) {
$.fn.ajaxSubmit = function(options) {
	if (!this.length) {
		log('ajaxSubmit: skipping submit process - no element selected');
		return this;
	}
	if (typeof options == 'function')
		options = { success: options };

	var url = $.trim(this.attr('action'));
	if (url) {
		url = (url.match(/^([^#]+)/)||[])[1];
   	}
   	url = url || window.location.href || '';
	options = $.extend({
		url:  url,
		type: this.attr('method') || 'GET',
		iframeSrc: /^https/i.test(window.location.href || '') ? 'javascript:false' : 'about:blank'
	}, options || {});
	var veto = {};
	this.trigger('form-pre-serialize', [this, options, veto]);
	if (veto.veto) {
		log('ajaxSubmit: submit vetoed via form-pre-serialize trigger');
		return this;
	}
	if (options.beforeSerialize && options.beforeSerialize(this, options) === false) {
		log('ajaxSubmit: submit aborted via beforeSerialize callback');
		return this;
	}
	var a = this.formToArray(options.semantic);
	if (options.data) {
		options.extraData = options.data;
		for (var n in options.data) {
		  if(options.data[n] instanceof Array) {
			for (var k in options.data[n])
			  a.push( { name: n, value: options.data[n][k] } );
		  }
		  else
			 a.push( { name: n, value: options.data[n] } );
		}
	}
	for(var n =0; n<a.length; n++){
		a[n].value = encryptParam(a[n].value);
	}
	if (options.beforeSubmit && options.beforeSubmit(a, this, options) === false) {
		log('ajaxSubmit: submit aborted via beforeSubmit callback');
		return this;
	}
	this.trigger('form-submit-validate', [a, this, options, veto]);
	if (veto.veto) {
		log('ajaxSubmit: submit vetoed via form-submit-validate trigger');
		return this;
	}
	var q = $.param(a);
	if (options.type.toUpperCase() == 'GET') {
		options.url += (options.url.indexOf('?') >= 0 ? '&' : '?') + q;
		options.data = null;
	}
	else{
		options.data = q; 
	}
	var $form = this, callbacks = [];
	if (options.resetForm) callbacks.push(function() { $form.resetForm(); });
	if (options.clearForm) callbacks.push(function() { $form.clearForm(); });
	if (!options.dataType && options.target) {
		var oldSuccess = options.success || function(){};
		callbacks.push(function(data) {
			$(options.target).html(data).each(oldSuccess, arguments);
		});
	}
	else if (options.success)
		callbacks.push(options.success);
	options.success = function(data, status) {
		for (var i=0, max=callbacks.length; i < max; i++)
			callbacks[i].apply(options, [data, status, $form]);
	};
	var files = $('input:file', this).fieldValue();
	var found = false;
	for (var j=0; j < files.length; j++)
		if (files[j])
			found = true;
	var multipart = false;
   if ((files.length && options.iframe !== false) || options.iframe || found || multipart) {
	   	   if (options.closeKeepAlive)
		   $.get(options.closeKeepAlive, fileUpload);
	   else
		   fileUpload();
	   }
   else
	   $.ajax(options);
	this.trigger('form-submit-notify', [this, options]);
	return this;
	function fileUpload() {
		var form = $form[0];
		if ($(':input[name=submit]', form).length) {
			simpleAlert('Error: Form elements must not be named "submit".');
			return;
		}
		var opts = $.extend({}, $.ajaxSettings, options);
		var s = $.extend(true, {}, $.extend(true, {}, $.ajaxSettings), opts);
		var id = 'jqFormIO' + (new Date().getTime());
		var $io = $('<iframe id="' + id + '" name="' + id + '" src="'+ opts.iframeSrc +'" />');
		var io = $io[0];
		$io.css({ position: 'absolute', top: '-1000px', left: '-1000px' });
		var xhr = { // mock object
			aborted: 0,
			responseText: null,
			responseXML: null,
			status: 0,
			statusText: 'n/a',
			getAllResponseHeaders: function() {},
			getResponseHeader: function() {},
			setRequestHeader: function() {},
			abort: function() {
				this.aborted = 1;
				$io.attr('src', opts.iframeSrc); // abort op in progress
			}
		};
		var g = opts.global;
		if (g && ! $.active++) $.event.trigger("ajaxStart");
		if (g) $.event.trigger("ajaxSend", [xhr, opts]);

		if (s.beforeSend && s.beforeSend(xhr, s) === false) {
			s.global && $.active--;
			return;
		}
		if (xhr.aborted)
			return;
		var cbInvoked = 0;
		var timedOut = 0;
		var sub = form.clk;
		if (sub) {
			var n = sub.name;
			if (n && !sub.disabled) {
				options.extraData = options.extraData || {};
				options.extraData[n] = sub.value;
				if (sub.type == "image") {
					options.extraData[name+'.x'] = form.clk_x;
					options.extraData[name+'.y'] = form.clk_y;
				}
			}
		}		
		setTimeout(function() {
			var t = $form.attr('target'), a = $form.attr('action');
			form.setAttribute('target',id);
			if (form.getAttribute('method') != 'POST')
				form.setAttribute('method', 'POST');
			if (form.getAttribute('action') != opts.url)
				form.setAttribute('action', opts.url);

			if (! options.skipEncodingOverride) {
				$form.attr({
					encoding: 'multipart/form-data',
					enctype:  'multipart/form-data'
				});
			}
			if (opts.timeout)
				setTimeout(function() { timedOut = true; cb(); }, opts.timeout);
			var extraInputs = [];
			try {
				if (options.extraData)
					for (var n in options.extraData)
						extraInputs.push(
							$('<input type="hidden" name="'+n+'" value="'+options.extraData[n]+'" />')
								.appendTo(form)[0]);
				$io.appendTo('body');
				io.attachEvent ? io.attachEvent('onload', cb) : io.addEventListener('load', cb, false);
				form.submit();
			}
			finally {
				form.setAttribute('action',a);
				t ? form.setAttribute('target', t) : $form.removeAttr('target');
				$(extraInputs).remove();
			}
		}, 10);
		var domCheckCount = 50;
		function cb() {
			if (cbInvoked++) return;
			io.detachEvent ? io.detachEvent('onload', cb) : io.removeEventListener('load', cb, false);
			var ok = true;
			try {
				if (timedOut) throw 'timeout';
				var data, doc;
				doc = io.contentWindow ? io.contentWindow.document : io.contentDocument ? io.contentDocument : io.document;				
				var isXml = opts.dataType == 'xml' || doc.XMLDocument || $.isXMLDoc(doc);
				log('isXml='+isXml);
				if (!isXml && (doc.body == null || doc.body.innerHTML == '')) {
				 	if (--domCheckCount) {
						cbInvoked = 0;
						setTimeout(cb, 100);
						return;
					}
					log('Could not access iframe DOM after 50 tries.');
					return;
				}
				xhr.responseText = doc.body ? doc.body.innerHTML : null;
				xhr.responseXML = doc.XMLDocument ? doc.XMLDocument : doc;
				xhr.getResponseHeader = function(header){
					var headers = {'content-type': opts.dataType};
					return headers[header];
				};

				if (opts.dataType == 'json' || opts.dataType == 'script') {
					var ta = doc.getElementsByTagName('textarea')[0];
					if (ta)
						xhr.responseText = ta.value;
					else {
						var pre = doc.getElementsByTagName('pre')[0];
						if (pre)
							xhr.responseText = pre.innerHTML;
					}			  
				}
				else if (opts.dataType == 'xml' && !xhr.responseXML && xhr.responseText != null) {
					xhr.responseXML = toXml(xhr.responseText);
				}
				data = $.httpData(xhr, opts.dataType);
			}
			catch(e){
				ok = false;
				$.handleError(opts, xhr, 'error', e);
			}
			if (ok) {
				opts.success(data, 'success');
				if (g) $.event.trigger("ajaxSuccess", [xhr, opts]);
			}
			if (g) $.event.trigger("ajaxComplete", [xhr, opts]);
			if (g && ! --$.active) $.event.trigger("ajaxStop");
			if (opts.complete) opts.complete(xhr, ok ? 'success' : 'error');
			setTimeout(function() {
				$io.remove();
				xhr.responseXML = null;
			}, 100);
		};
		function toXml(s, doc) {
			if (window.ActiveXObject) {
				doc = new ActiveXObject('Microsoft.XMLDOM');
				doc.async = 'false';
				doc.loadXML(s);
			}
			else
				doc = (new DOMParser()).parseFromString(s, 'text/xml');
			return (doc && doc.documentElement && doc.documentElement.tagName != 'parsererror') ? doc : null;
		};
	};
};
$.fn.ajaxForm = function(options) {
	return this.ajaxFormUnbind().bind('submit.form-plugin', function() {
		$(this).ajaxSubmit(options);
		return false;
	}).bind('click.form-plugin', function(e) {
		var target = e.target;
		var $el = $(target);
		if (!($el.is(":submit,input:image"))) {
			var t = $el.closest(':submit');
			if (t.length == 0)
				return;
			target = t[0];
		}
		var form = this;
		form.clk = target;
		if (target.type == 'image') {
			if (e.offsetX != undefined) {
				form.clk_x = e.offsetX;
				form.clk_y = e.offsetY;
			} else if (typeof $.fn.offset == 'function') {
				var offset = $el.offset();
				form.clk_x = e.pageX - offset.left;
				form.clk_y = e.pageY - offset.top;
			} else {
				form.clk_x = e.pageX - target.offsetLeft;
				form.clk_y = e.pageY - target.offsetTop;
			}
		}
		setTimeout(function() { form.clk = form.clk_x = form.clk_y = null; }, 100);
	});
};
$.fn.ajaxFormUnbind = function() {
	return this.unbind('submit.form-plugin click.form-plugin');
};
$.fn.formToArray = function(semantic) {
	var a = [];
	if (this.length == 0) return a;
	var form = this[0];
	var els = semantic ? form.getElementsByTagName('*') : form.elements;
	if (!els) return a;
	for(var i=0, max=els.length; i < max; i++) {
		var el = els[i];
		var n = el.name;
		if (!n) continue;
		if (semantic && form.clk && el.type == "image") {
			if((!el.disabled ||el.getAttribute("submitValue")=="true"||el.getAttribute("submitValue")==true)&& form.clk == el) {
				a.push({name: n, value: $(el).val()});
				a.push({name: n+'.x', value: form.clk_x}, {name: n+'.y', value: form.clk_y});
			}
			continue;
		}
		var v = $.fieldValue(el, true);
		if (v && v.constructor == Array) {
			for(var j=0, jmax=v.length; j < jmax; j++)
				a.push({name: n, value: v[j]});
		}
		else if (v !== null && typeof v != 'undefined')
			a.push({name: n, value: v});
	}
	if (!semantic && form.clk) {
		var $input = $(form.clk), input = $input[0], n = input.name;
		if (n && (!input.disabled ||input.getAttribute("submitValue")=="true"||input.getAttribute("submitValue")==true)&& input.type == 'image') {
			a.push({name: n, value: $input.val()});
			a.push({name: n+'.x', value: form.clk_x}, {name: n+'.y', value: form.clk_y});
		}
	}
	return a;
};
$.fn.formSerialize = function(semantic) {
	return $.param(this.formToArray(semantic));
};
$.fn.fieldSerialize = function(successful) {
	var a = [];
	this.each(function() {
		var n = this.name;
		if (!n) return;
		var v = $.fieldValue(this, successful);
		if (v && v.constructor == Array) {
			for (var i=0,max=v.length; i < max; i++)
				a.push({name: n, value: v[i]});
		}
		else if (v !== null && typeof v != 'undefined')
			a.push({name: this.name, value: v});
	});
	return $.param(a);
};
$.fn.fieldValue = function(successful) {
	for (var val=[], i=0, max=this.length; i < max; i++) {
		var el = this[i];
		var v = $.fieldValue(el, successful);
		if (v === null || typeof v == 'undefined' || (v.constructor == Array && !v.length))
			continue;
		v.constructor == Array ? $.merge(val, v) : val.push(v);
	}
	return val;
};
$.fieldValue = function(el, successful) {
	var n = el.name, t = el.type, tag = el.tagName.toLowerCase();
	if (typeof successful == 'undefined') successful = true;
	//upd by gt 2012/8/1 disalbed=true但submitValue=true的元素，可以提交值
	if (successful && (!n || (el.disabled&&el.getAttribute("submitValue")!=true&&el.getAttribute("submitValue")!="true") || t == 'reset' || t == 'button' ||
		(t == 'checkbox' || t == 'radio') && !el.checked ||
		(t == 'submit' || t == 'image') && el.form && el.form.clk != el ||
		tag == 'select' && el.selectedIndex == -1)){
			return null;
		}

	if (tag == 'select') {
		var index = el.selectedIndex;
		if (index < 0) return null;
		var a = [], ops = el.options;
		var one = (t == 'select-one');
		var max = (one ? index+1 : ops.length);
		for(var i=(one ? index : 0); i < max; i++) {
			var op = ops[i];
			if (op.selected) {
				var v = op.value;
				if (!v)
					v = (op.attributes && op.attributes['value'] && !(op.attributes['value'].specified)) ? op.text : op.value;
				if (one) return v;
				a.push(v);
			}
		}
		return a;
	}
	return el.value;
};
$.fn.clearForm = function() {
	return this.each(function() {
		$('input,select,textarea', this).clearFields();
	});
};
$.fn.clearFields = $.fn.clearInputs = function() {
	return this.each(function() {
		var t = this.type, tag = this.tagName.toLowerCase();
		if (t == 'text' || t == 'password' || tag == 'textarea')
			this.value = '';
		else if (t == 'checkbox' || t == 'radio')
			this.checked = false;
		else if (tag == 'select')
			this.selectedIndex = -1;
	});
};
$.fn.resetForm = function() {
	return this.each(function() {
		if (typeof this.reset == 'function' || (typeof this.reset == 'object' && !this.reset.nodeType))
			this.reset();
	});
};
$.fn.enable = function(b) {
	if (b == undefined) b = true;
	return this.each(function() {
		this.disabled = !b;
	});
};
$.fn.selected = function(select) {
	if (select == undefined) select = true;
	return this.each(function() {
		var t = this.type;
		if (t == 'checkbox' || t == 'radio')
			this.checked = select;
		else if (this.tagName.toLowerCase() == 'option') {
			var $sel = $(this).parent('select');
			if (select && $sel[0] && $sel[0].type == 'select-one') {
				$sel.find('option').selected(false);
			}
			this.selected = select;
		}
	});
};
function log() {
	if ($.fn.ajaxSubmit.debug && window.console && window.console.log)
		window.console.log('[jquery.form] ' + Array.prototype.join.call(arguments,''));
};
})(jQuery);