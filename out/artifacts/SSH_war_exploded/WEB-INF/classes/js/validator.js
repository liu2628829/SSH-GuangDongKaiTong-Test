//����֤��
Validator = function(){
	//���õ�������ʽ
    var trimRe = /^\s+|\s+$/g;
	var emptyRe = /^\s*$/;
	var errorTip = {
	    zh      : "����������",
	    telephone:"������һ����ȷ�ĵ绰����",
	    mobilephone:"������һ����ȷ���ֻ�����",
	    combinephone:"������һ����ȷ�ĵ绰����", //�̻����ֻ�����ϣ�����ǰ��Ӣ�Ķ��ŷָ�
		ip		: "������һ����Ч��IP",
		ipv6	: "������һ����Ч��IPv6",
		postcode: "������һ����Ч���ʱ�",
		require : "���Ǳ����",
		email	: "������һ����Ч�ĵ����ʼ�",
		number	: "������һ����Ч������",
		digit	: "������һ����Ч������",
		alpha	: "ֻ������ĸ",
		alphanum: "ֻ������ĸ������",
		url		: "������һ����Ч��ַ",
		filter	: "�����Ϲ��˹���",
		limit	: "���ݳ�������󳤶�",
		limitZh : "���ݳ�������󳤶�",
		repeat  : "���ε����벻һ��",
		range   : "���ڹ涨�ķ�Χ��",
		custom  : "�������Զ���Ĺ���",
		compare : "�Ƚ���֤������",
		compare2: "�Ƚ���֤������",
		email_limit: "������һ����Ч�ĵ����ʼ�,�������ݳ�������󳤶�",
        number_limit: "������һ����Ч������,�������ݳ�������󳤶�",
        digit_limit: "������һ����Ч������,�������ݳ�������󳤶�",
        alpha_limit: "ֻ������ĸ,�������ݳ�������󳤶�",
        alphanum_limit: "ֻ������ĸ������,�������ݳ�������󳤶�",
        url_limit: "������һ����Ч��ַ,�������ݳ�������󳤶�",
        password: "������С������8λ,��󳤶���32λ,����ͬʱ�������֡���ĸ(���ִ�Сд)���������(!#&$)",
        specialString : "���ݲ��ܴ��������ַ�"
	};
	var specialStrings = ["'","\""," drop "," create "," exec "," insert "," select "," delete "," update "," or ",
						  " and "," truncate "," execute "," grant "," use "," group_concat "," union "];
	/*var setDisabled=function(){
			//��������
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
				/*lis=$(":input[value='����']");
				lis.attr("disabled",false);
				lis=$(":input[value='ȷ��']");
				lis.attr("disabled",false);
				//��������
				document.getElementById("VALIDATOR_MODAL_DIV").style.display="none";
				$('#VALIDATOR_MODAL_DIV').css("z-index", "-1");
		}*/
	
	return{
	    zh:/^[\u4e00-\u9fa5],{0,}$/,
	    telephone:/^(\(\d{3,4}\)|\d{3,4}-)?\d{7,8}$/,
	    mobilephone:/^1[0-9][0-9]\d{8}$/, ///^1[3|4|5|8][0-9]\d{8}$/, ��2λ��������
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
		errorMessage : ["����ԭ�����ύʧ�ܣ�\t\t\t\t"],
		/*email_limit : true,
		number_limit : true,
		digit_limit : true,
		alpha_limit : true,
		alphanum_limit : true,
		url_limit : true,*/
		//����ʼ��,���ڱ���ʧ��ʱ�ʹ�����֤
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
		��ʼ����֤Ԫ��
		add by tanjianwen 2011-12-22
		�������option��������:
		[{
		    eId:Ԫ��ID,
		    datatype:[require|ip|email|number|digit|range|alpha|alphanum|url|
		    		  limit|limitZh|repeat|custom|compare|compare2|compareNum|
		    		  ip_require|digit_range|alpha_limit] ��֤����,
		    require: [true|false] �Ƿ����,
		    min: ���� datatype��[range|limit|limitZh]ʱ����,
		    max: ���� ͬ��,
		    to: datatype��repeatʱ����,
		    regexp: ��datatype����Ϊcustomʱ,����������֤��������ʽ,
		    ...������
		    msg: ��֤������ʱ�Ĵ�����ʾ˵��(��ѡ,��������)
		}{...}]
		*/
		initValidate : function(option){
			try {
				for (var i = 0;i < option.length; i++) {
					var e;//ָ��Ԫ�ض���
					for (var attr in option[i]) {
						if (attr == 'eId') {
							e = document.getElementById(option[i][attr]);
							continue;
						}
						e.setAttribute(attr, option[i][attr]);//��Ԫ�������֤����			
					}
				}
			} catch (err) {
				simpleAlert('��֤��ʼ��ʧ��,���������Ƿ���ȷ!');
			}
		},
		/*
		����֤
		update by tanjianwen 2012-5-10
		theForm : ����������ַ���,�����ID,�����Ǳ����� (��form����û��action����ֵʱ���򲻻��ύ��ֻ��Ч��, �ص�������Ȼ�����)
		mode : ������ʾ����(Ĭ����2,��������ʾ����Ϊ������)
		fn : �ص�����
		msg : �ⲿ������Ϣ����(����������)
		async : �Ƿ�ͬ��(Ĭ��ͬ��)
		opts : ����js����,���������width����,ָ�������ڵĿ��,���� {width:500}
		  |- width
		  |- win ������Ϣ��Ĵ��ڶ���
		  |- special true|false �Ƿ���֤�����ַ� Ĭ��false
		*/
		validate : function(theForm,mode,fn,msg,async,opts){
			var obj=(typeof(theForm)=="string")?document.getElementById(theForm):theForm;
			if(obj==null||obj==undefined)obj=event.srcElement;
			if(obj==null||obj==undefined||obj.nodeName!="FORM"){simpleAlert("û��ָ��Ҫ�ύ�ı�!");return;}
			
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
					value=Validator.trim(value,"");//value.replace(/(^\s*)|(\s*$)/,"");//�����β���ַ�	
					if (opts && opts.special == true)this.validateSpecial(obj.elements[i], i);//�����ַ���֤
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
								/*���ַ�ʽ��Ҫ���һ������Ϊ����Ч�飬���ڶ����ǿ�ִ�е��ַ���,�Ͳ�������������������װ
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
			
			//���ⲿ���������Ϣ
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
							if(this.errorItem[i].type=="hidden"&&document.getElementById(this.errorItem[i].id+"_text")){//��Ե���ѡ��������ѡ�ȿؼ������ñ���ɫ
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
				var w = opts && opts.win ? opts.win : (getTopWin(window)?getTopWin(window):window);//Ĭ���ڶ������嵯��
				w.simpleAlert(alertOpts);
				//cancleDisabled();
				return false;
			}
			//return true;
			//���ͨ������֤,���ύ��
			this.submitForm(obj,fn,async);
			for (var i = 0;i < hintInputs.length;i++) {
				hintInputs[i].value = hintInputs[i].msg;
			}
		},
		/*
		ͨ���������֤
		update by dengjianfei 2013-1-4
		element : ��Ҫ��֤�Ķ������� �� ����ID �� У�����
		mode : ������ʾ����(Ĭ����2,��������ʾ����Ϊ������)
		fn : �ص�����
		msg : �ⲿ������Ϣ����(����������)
		async : �Ƿ�ͬ��(Ĭ��ͬ��)
		opts : ����js����,���������width����,ָ�������ڵĿ��,���� {width:500}
		  |- width
		  |- win ������Ϣ��Ĵ��ڶ���
		  |- special true|false �Ƿ���֤�����ַ� Ĭ��false
		*/
		validate_dgt : function(element,mode,fn,msg,async,opts){
			var objs;
			var fb = true; //У�����
			var filterObj = {}; //��ѡ��ѡ��Ԫ�ض���
			
			//��������
			if(typeof element=="string"){
				var idStr = ""; //idѡ�����ַ���
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
			
			//���˳�radio,checkbox��Ԫ��
			filterObj = this.filterElements(objs);
			
			//��Ԫ��У��
			for(var i = 0; i < objs.length; i++){
				this.validateElement(i, objs[i], opts, fb);
			}
			
			//У�鵥ѡ����ѡ��ť
			this.validRadioAndCheckbox(filterObj);
			
			//���ⲿ���������Ϣ
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
							if(this.errorItem[i].type=="hidden"&&document.getElementById(this.errorItem[i].id+"_text")){//��Ե���ѡ��������ѡ�ȿؼ������ñ���ɫ
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
				var w = opts && opts.win ? opts.win : (getTopWin(window)?getTopWin(window):window);//Ĭ���ڶ������嵯��
				w.simpleAlert(alertOpts);
				//cancleDisabled();
				return false;
			}
			if(fb)return [];
			return true;
			//���ͨ������֤,���ύ��
			//this.submitForm(obj,fn,async);
			for (var i = 0;i < hintInputs.length;i++) {
				hintInputs[i].value = hintInputs[i].msg;
			}
		},
		/**
		 *У��
		 */
		validateElement:function(i,obj,opts,fb){
			var disabled = fb?false:obj.getAttribute("disabled");
			var submitValue = fb?"":obj.getAttribute("submitValue");
			var _dataType = fb?obj.dataType:obj.getAttribute("dataType");
			var require = fb?obj.require:obj.getAttribute("require");
			var msg = fb?obj.msg:obj.getAttribute("msg");
			var value = obj.value;
			
			if((disabled==true||disabled=="disabled")&&submitValue!="true"&&submitValue!=true)return;
			if(isNaN(value))//����Ƿ����������������β���ַ�
			   Validator.trim(value,"");//value=value.replace(/(^\s*)|(\s*$)/,"");//�����β���ַ�		
			if (opts && opts.special == true)this.validateSpecial(objs[i], i,fb);//�����ַ���֤
			
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
						//���õ�У������Ϊ require,limitZh,mobilephone�ȣ���Ҫ��ÿһ�����У��
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
							
							//i Ϊ�������
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
		 * ���˱�Ԫ���е�radio��checkbox
		 * objs ��Ԫ�ض���
		 * add by zhanweibin 2013-8-8
		 */
		filterElements: function(objs){
			var filterObj = {};
			
			for(var i = objs.length - 1; i >= 0; i--){
				var type = $(objs[i]).attr("type");
				var dataType = $(objs[i]).attr("dataType");
				var name = $(objs[i]).attr("name");
				
				//��ѡ����ѡ��ť�������Ǳ���ģ���¼����ʱ������
				if((type == "radio" || type == "checkbox") && dataType == "require"){
					if(!filterObj[name]){
						filterObj[name] = [];
					}
					filterObj[name].push(objs[i]);
					//��Ԫ���Ƴ������߳���У����߼�
					objs.splice(i, 1);
				}
			}
			
			return filterObj;
		},
		
		/**
		 * У�鵥ѡ����ѡ��ť�Ƿ����
		 * add by zhanweibin 2013-8-8
		 * obj ���˳�����radio,checkboxԪ�أ���ʽΪ��
		 * {
		 *   'name1': [ele1, ele2,...],
		 *   'name2': [ele1, ele2,...]...
		 * }
		 */
		validRadioAndCheckbox: function(obj){
			//��name������У�飬elesΪ��ͬname��domԪ��
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
		 * ��ȡ��֤��ʾ��Ϣ(ͨ�ñ�)
		 * add by dengjianfei 2013-1-8
		 */
		getErrorTip:function(dataType){
			return errorTip[dataType];
		},
		/*�����ַ�����֤
		  1.ϵͳ�Ѿ�Ĭ�������������ַ�����
			', drop , create , exec , insert , select , delete , update , or ,
			and , truncate , execute , grant , use , group_concat , union 
			���������Ϣ�������������ַ���ʱ����֤���ᱨ��,��ʾ���������ַ�����
			
		  2.�����Ҫ���������ַ�������input��ǩ�����extraSpecial����,
		     д����ָ��Ҫ��֤�������ַ���,����Զ��Ÿ���,�� ��extraSpecial="test,test1,test2"
		     �����Ҫָ���������ַ�����������,���ڶ���ǰ���ת���"\",��extraSpecial="\,,test"��
		     
		  3.���Ҫ����ĳЩ�����ַ�������֤,��input��ǩ�����ignoreSpecial����,
		    д��ָ���������ַ���,����ж���Զ��Ÿ���,�� ��ignoreSpecial="test,test1,test2"
		    �����Ҫָ���������ַ�����������,���ڶ���ǰ���ת���"\",��ignoreSpecial="\,,test"��
		    
		  4.�Զ���Ĵ���������Ϣ,��input��ǩ�������specialMsg����,���磺specialMsg="XXX���ܰ��������ַ�"
		*/
		validateSpecial : function(obj, index,fb) {
			var ignoreSpecial = fb?obj.ignoreSpecial:obj.getAttribute("ignoreSpecial");//���Ե������ַ�
			var extraSpecial = fb?obj.extraSpecial:obj.getAttribute("extraSpecial");//���ӵ������ַ�
			var specialMsg = fb?obj.specialMsg:obj.getAttribute("specialMsg");//���ӵ������ַ�
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
						this.addError(index, "���ݲ��ܴ��������ַ���\""+specials[j]+"\"", "specialString");
					} else {
						this.addError(index, specialMsg+"\""+specials[j]+"\"", "specialString");
					}
					break;
				}
			}
		},
		//������ʾ
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
		//ȥ���ַ������˵Ŀո�
        trim : function(value){
            return String(value).replace(trimRe, "");
        },
		//�Ƿ�Ϊ��
		isEmpty : function(v, allowBlank){
            return v === null || v === undefined || (!allowBlank ? emptyRe.test(v) : false);
        },
		//����������һ����Χ
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
		//�����������ֽ�
		lenZh : function(str){
			return str.replace(/[^\x00-\xff]/g,"**").length;
		},
		//���״̬
		clearState : function(elem){
			with(elem){
				if(style.color == "red"){
					style.color = "";
					style.backgroundColor = "";
				}
				if(type == "hidden"&&document.getElementById(id+"_text")){//��Ե���ѡ��������ѡ�ȿؼ������ñ���ɫ
					$("#"+id+"_text").css({color:"",backgroundColor:""});
				}
				var lastNode = parentNode.childNodes[parentNode.childNodes.length-1];
				if(lastNode.id == "__ErrorMessagePanel")
					parentNode.removeChild(lastNode);
			}
		},
		//���Ӵ�����Ϣ
		addError : function(index,str,dataType,fb){
			if(str == null)
				str= errorTip[dataType];
			/** edit by dengjianfei 2013-1-4 ͨ�ñ������֤***/
			if(this.errorItem[0].elements!=undefined)this.errorItem[this.errorItem.length] = this.errorItem[0].elements[index];
			else this.errorItem[this.errorItem.length] = this.errorItem[0][index];
			/************************/
			if(fb)this.errorMessage[this.errorMessage.length] = str;
			else this.errorMessage[this.errorMessage.length] = this.errorMessage.length + ":" + str;
		},
		//�����ַ���op�Ƿ�����������reg���趨�Ĺ���
		doExec : function(op, reg){
			return new RegExp(reg,"g").test(op);
		},
		//���ַ������й���
		doFilter : function(input, filter){
			return new RegExp("^.+\.(?=EXT)(EXT)$".replace(/EXT/g, filter.split(/\s*,\s*/).join("|")), "gi").test(input);
		},
		//ִ�������ʽ�ж�
		doPassword : function(value) {
			return ((value.length >= 8 && value.length <=32) && 
					new RegExp("^\\S*[0-9]+\\S*$").test(value) && 
					new RegExp("^\\S*[a-zA-Z]+\\S*$").test(value) &&
					new RegExp("^\\S*[\!|\#|\&|\$]+\\S*$").test(value) &&
					!new RegExp("^\\S*[^a-zA-Z0-9\!\#\&\$]+\\S*$").test(value)
					);
		},
		//�ύ������
		submitForm:function(pForm,fn,async)
		{
			if(!pForm.action){//cancleDisabled();
			if(fn&&typeof(fn)=="function")fn.call(this);return;}//��������Ч�飬�����ύ��������������ʱ�����н���˴���
			if(typeof(WaitBar) != "undefined")WaitBar.show(1);
			$(pForm).ajaxSubmit({
					type: 'POST',
					dataType: 'text',
					async: (async!=false&&async!="false")?true:false,//�첽��falseΪ����
				 	timeout:40000,
					success: function(data, statusText) {
						if(typeof(WaitBar) != "undefined") 
							WaitBar.hide();
						//ϵͳ�쳣
						var jsonData;
						try{
							jsonData = decode(data);//�����ص��Ƿ���ֵ�ַ���ʱ���˾�ᱨ�쳣	
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
	    * ajax����ʧ��ͳһ����ʽ,�����ڴ�js�ڲ�����
	    * @param {} response(����)
	    * @param {} options(����)
	    */
		dealFailue:function(XMLHttpRequest, textStatus, fn) {
			if (typeof(WaitBar) != "undefined") {WaitBar.hide();}
			//�±����if�жϱ���д������if֮ǰ��������ʱʱ��XMLHttpRequest.status��IE�ϱ�js�����
			if(textStatus=="timeout"){
				simpleAlert({msg:"ϵͳ��æ������ʱ�����Ժ���!",opts:{timeout:5000,winId:"catt_err_msg"}});
				return;
			}			
			if (9999 == XMLHttpRequest.status) {//session��ʱ���������¼��
				openMinLoginWin(fn);//�˷������ദ���ã���common.js��ͳһ����
				return;
			}
			if (8888 == XMLHttpRequest.status) {//��������������ʱ��ʾ
				simpleAlert({msg:"��ǰ������������,�Ժ�����!",opts:{timeout:5000,winId:"catt_err_msg"}});
				return;
			}
			simpleAlert({msg:"ϵͳ��æ�����Ժ���!",opts:{timeout:5000,winId:"catt_err_msg"}});
			return;
		}
	}
}();


/******************************************************************************************************
 * ������jquery.form.js������,�����ǿ���ֱ���첽�ύform��
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
	//upd by gt 2012/8/1 disalbed=true��submitValue=true��Ԫ�أ������ύֵ
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