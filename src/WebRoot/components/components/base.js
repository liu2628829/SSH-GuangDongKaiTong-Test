/**
 * 系统底层JS封装
 * 依赖jquery,layer
 * @author xzl
 */

(function (window) {
	/*===========================原型扩展 start==============================*/
    /**
     * 字符串替换全部
     */
    String.prototype.replaceAll = function (s1, s2) {
        return this.replace(new RegExp(s1, "gm"), s2);
    };
	//字符串替换
	String.prototype.replaceJson = function (json) {
		var str = this.toString();
		for(var key in json){
			str = str.replace(new RegExp("{" + key+ "}", "gm"), json[key]);
		}
	    return str;
	};
	/*===========================end==============================*/
	var Base = {};
    /**
     * 基于jQuery Ajax二次封装，统一处理失败，会话失效问题
     * @param options 主要参数如下：
     * type 请求类型【POST,GET】，默认POST
     * dataType 请求数据类型，默认json，如请求文本，xml格式数据需要调整
     * success 成功回调函数，参数（1、返回数据，2、请求返回状态，3、请求参数）
     * showException 是否提示后台成功返回的异常信息，默认true
     */
    Base.ajax = function (options) {
        //成功回调加入后台异常信息提示
        options.callback = options.success;
        delete options.success;
        
        var isShowWaitbar = typeof(options.isShowWaitbar)== 'undefined' ? true : options.isShowWaitbar;
        
        if (isShowWaitbar && typeof(Wait)!= 'undefined') {
        	Wait.show({});
        }
        
        var defaultOptions = {
            type: 'POST',
            dataType: 'json',
            timeout: 1000000,//1000秒后超时
            success: function (data, textStatus) {
            	if (isShowWaitbar && Wait) {
		        	Wait.hide();
		        }
                //统一处理后台返回的异常信息
                if (options.showException != false && typeof(data) == "object" && data && data.SUCCESS == "false") {
                    Base.alert(data["MESSAGE"], {icon:2});
                    return;
                }
                if ($.isFunction(options.callback)) {
                    options.callback(data, textStatus, options.data);
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            	if (isShowWaitbar && typeof(Wait)!= 'undefined') {
		        	Wait.hide();
		        }
                if (textStatus == 'timeout') {
                    Base.alert("请求超时，请稍候重试！", {icon: 2});
                    return;
                }
                //session超时，开迷你登录窗
                if (typeof(XMLHttpRequest.status) == 'number' && 9999 == XMLHttpRequest.status) {
                    //开窗登录
                    Base.openMinLoginWin(function () {
                        Base.ajax(options);
                    });
                    return;
                }
                Base.alert("服务器繁忙，请稍候重试！", {icon: 2});
            }
        };
        $.ajax($.extend({}, defaultOptions, options));
    }
    //动态引入JS
    Base.loadScript = function(url,callFn){
		var script = document.createElement("script");
		script.type = "text/javascript";
		script.src = url;
		document.body.appendChild(script);
		script.onload = function(e){  
			callFn(e);  
        } 
	}
    /**
     * 检查SESSION是否有效
     */
    Base.checkSession = function () {
        var result = false;
        $.ajax({
            url: Base.getContextPath() + "/login!checkSession.action",
            type: 'POST',
            async: false,
            success: function (data, textStatus) {
                result = data;
            }
        });
        return result;
    }


    /**
     * 会话失败时打开迷你登陆窗
     */
    Base.openMinLoginWin = function (fn) {

        //在TOP页面上处理
        var TopWindow = Base.getTopWindow();

        //绑定登陆后回调事件
        if (!$.isFunction(TopWindow.afterMinLogin)) {

            /**
             * 成功登录后执行
             */
            TopWindow.afterMinLogin = function () {
                //隐藏窗口
                TopWindow.LOGIN_WIN.hide(function () {
                    TopWindow.LOGIN_WIN = null;
                    //执行过期前请求
                    for (var i = 0; i < TopWindow.FN_ARRAY.length; i++) {
                        TopWindow.FN_ARRAY[i].call(this);
                    }
                    TopWindow.FN_ARRAY = [];
                });
            }
        }
        //保存登陆后执行的方法
        if ($.isFunction(fn)) {
            if (!TopWindow.FN_ARRAY) {
                TopWindow.FN_ARRAY = [];
            }
            TopWindow.FN_ARRAY.push(fn);
        }
        //已经打开登陆窗，防止重复
        if (TopWindow.LOGIN_WIN != null) {
            return;
        }
        var url = Base.getContextPath() + '/minlogin1.jsp';
        TopWindow.LOGIN_WIN = Base.dialog({
            title: "会话过期，请重新登录",
            checkSession: false,//不检查SESSION
            width: 300,
            height: 245,
            url: url,
            modal: true,
            scrollbar: true,
            closeButtonEnable: false,
            confirmButtonEnable: false,
            closeable: false,
            draggable: true
        });
    };

    /**
     * 获取顶层窗体对象
     */
    Base.getTopWindow = function () {
        return window.top.Base ? window.top : window;
    };

    /**
     * 获取应用上下文路径
     */
    Base.getContextPath = function () {
        var ctx = window.ctx || window.path;
        if (typeof(ctx) == 'undefined') {
            ctx = window.document.location.pathname;
            ctx = ctx.substring(0, ctx.substr(1).indexOf('/') + 1);
        }
        return ctx;
    };
    
    /**
     * 获取页面所在应用名称
    *win:window对象
    *不传参时自动取当前window对象
    */
    Base.getPathName = function (win){
    	if(win==null)win=window;
    	//commonJS.jsp中定义了全局变量
    	if(typeof(win.CONTEXT_PATH_NAME)!="undefined" && win.CONTEXT_PATH_NAME!=null){
    	   return win.CONTEXT_PATH_NAME;
    	}else {//非本项目
    	    try{
    			var contextPath = win.document.location.pathname; 
    			//alert("==="+contextPath);
    		  	var index =contextPath.substr(1).indexOf("/"); 
    		  	contextPath = contextPath.substr(0,index+1); 
    		  	//被模态开窗的页面，IE下没有前"/"，比如open开窗页面里得到/SSH3，而模态开窗只能得到SSH3
    		  	if(contextPath && contextPath.indexOf("/")!=0){contextPath=("/"+contextPath);} 
    		  	delete index; 
    		  	return contextPath.replace("/admin","");
    	  	}catch(e){
    	  		return CONTEXT_PATH_NAME.replace("/","");
    	  	}
    	}
    }
    

/**获取页面所在应用的根路径
*win:window对象
*不传参时自动取当前window对象
*/
    Base.getFullPath = function(win){
			if(win==null)win=window;
			var pathName = Base.getPathName(win);
			var fullPath = win.location.protocol+"//"+win.location.host+"/"+pathName.replace("/","");
			return fullPath;
    }
    
    /** 
    *获取当前元素的宽度，如果元素的父节点是隐藏的则先显示，获取到宽度后再重新隐藏父节点。
    *tangyj 2013-3-23
    */
    Base.getElementWidth = function(element){
    	var hiddenNodes = [];//定义数组，保存隐藏的节点。用来获取到元素宽度后，重新隐藏
    	
    	/**
    	*递归父节点，从根节点往下逐层显示节点
    	*/
    	function  dispalyElement(element){
    		var $ele = $(element);
    		if( !element || $ele.is("body") ){//判断当前节点是否可见或是body节点直接返回
    			return;
    		}else{//遍历到body节点，并找出所有display为none的节点，然后从根节点逐个设置
    			dispalyElement($ele[0].parentNode);
    			//判断当前节点是否设置成display为none,如果设置了则显示
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
    	var elementWidth =element.offsetWidth;//获取元素宽度
    	//获取元素宽度之后，需要还原节点隐藏属性
    	for(var i =0;i<hiddenNodes.length;i++){
    		$(hiddenNodes[i]).hide();
    	}
    	return elementWidth;
    }
    
    /**
     * 原生window弹窗
     */
    Base.open = function (options) {
    	var screenHeight = window.screen.availHeight;
		var screenWidth = window.screen.availWidth;
    	var height = options.height||screenHeight;
    	var width = options.width||screenWidth;
    	var left = options.left;
    	var top = options.top;
    	if(!left) {
			left = (screenWidth - width)/2;
    	}
    	if(!top) {
			top = (screenHeight - height)/2;
    	}
    	var mesg = "height="+height+",width="+width+",top=" + top + ",left=" + left;
    	if(options.other) {
			mesg+=options.other;
    	}
        options = options || {};
        var tmpUrl = options.url;
        var mywin = window.open(tmpUrl,"_blank", mesg);
        //全屏显示，窗口最大化
        if(mesg.indexOf("fullscreen=yes")>-1|| mesg.indexOf("fullscreen=1")>-1){
        	mywin.moveTo(0, 0);
        	mywin.resizeTo(screen.availWidth,screen.availHeight);
        }
        return mywin;
    };
    
	
	 /**
     * 关闭dialog弹窗
     * @param index 
     * index = Base.dialog(dialog) 
     * 传入-1时默认关闭当前最新弹窗
     */
    Base.closeDialog = function (index) {
    	if (typeof(layer) == 'undefined' || !layer.open) {	//兼容原来的旧弹窗
			//TODO
   		} else {
    		return index ? (index == -1 ? layer.close(layer.index): layer.close(index)) 
    					: layer.closeAll();
   		}
    };

    /**
     * 弹窗组件
     * @param options 
     * {id:'主键', width: 120（宽）,height: 高, url: '页面地址', content: '内容（可以支持html）',
         confirmButtonText: '确定',confirmButtonEnable: true（是否显示确定按钮）,confirm:function(index){} 确定按钮是回调方法 这3个选项要引入layer.js 才能使用
     }
     */
    Base.dialog = function (options) {
    	if (typeof(layer) == 'undefined' || !layer.open) {	//兼容原来的旧弹窗
	        options = options || {};
	        var html = options.html || options.content;	//layer与 boxy的差异
	        if (options.url) {
	            var wid = options.width ? options.width + 'px' : 'auto';
	            var height = options.height ? options.height + 'px' : 'auto';
	            var id = options.id ? ' id="' + options.id + '" ' : "";
	            html = '<div ' + id + ' style="height: ' + height + ';width: ' + wid + ';overflow: auto;">' +
	                '<iframe style="height: 100%;width: 100%;overflow: auto;z-index:90;" frameborder="0"';
	            html += ' src="' + options.url + '"></iframe></div>';
	        }
	        return new Boxy(html, options);
      	}
      	options.content = options.content || options.html;	//layer与 boxy的差异
        
        var defaults = {
            id: null,
            title: '提示',
            content: '<p>内容</p>',
            url: null,
            urlType: 'iframe', //load,iframe
            width: 0,
            height: 0,
            padding: true,//内容边距
            closeable: true,
            modal: true,
            closeButtonText: '关闭',
            closeButtonEnable: true,
            confirmButtonText: '确定',
            confirmButtonEnable: true,
            modalOption: {},
            unloadOnHide: true,//隐藏时销毁
            footerEnable: true,//底部栏
            confirm: function () {
                return true;
            },
            show: function () {
            },
            shown: function () {
            },
            hide: function () {
            },
            hidden: function () {
            }
        };
        options = $.extend(defaults, options);

        var area;
        if (options.width > 0 && options.height == 0) {
            area = options.width + 'px';
        } else if (options.width > 0 && options.height > 0) {
            area = [options.width + 'px', options.height + 'px'];
        }
        
        //1页面、2iframe
        var type = 1;
        var index;
        if (options.urlType == 'iframe' && options.url) {
            options.content = options.url;
            type = 2;
            openMethod();
        } else if (options.urlType == 'load' && options.url) {
            $.get(options.url, function (str) {
                options.content = str;
                openMethod();
            });
        } else {
            openMethod();
        }
        function openMethod() {
        	var params = {
                type: type,
//                title: options.title,
                area: area
//                ,content: options.content
            };
            params = $.extend({},params ,options);
            
            var btns = [];
            if(options.confirmButtonEnable){
            	btns.push(options.confirmButtonText);
            	params = $.extend({},params,{yes:options.confirm});
            }
            if(options.closeButtonEnable){
            	btns.push(options.closeButtonText);
            	params = $.extend({},params,{no: function(index){
									 layer.close(index); //一般设定no回调，必须进行手工关闭
							    }});
            }
            if(btns.length>0){
            	params = $.extend({},params,{btn:btns});
            }
             index = layer.open(params);
        }
        
        return index;
    }
    
    
   /**
   *多按钮询问框
   */
    Base.openDialog = function(options){
    	layer.open(options);
    }


    /**
     * 提示层
     * @param message
     * @param options {time : 3000, icon : 1} （time 默认是3000毫秒，icon 图标 1~20）
     * 	
     * 
     * @param callback 回调函数
     */
     Base.msg = function (message, options, callback) {
     	var p = {};
 		if (options) {
 			if ($.isFunction(options)) {
     			callback = options;
     		} else {
     			p = options;
     		}
 		}
     	if (typeof(layer) != 'undefined' && layer.msg) {
     		layer.msg(message, p , callback);
     	} else {
     		alert(message);
     		if ($.isFunction(callback)) {
     			callback();
     		}
     	}
     };

     
    /**
     * 提示框
     * @param message 内容
     * @param options {{time : 3000, icon : 1} （time 默认是3000毫秒，icon 图标 1~20）}
     * icon=1： 绿色的勾号图标； 2： 叉叉  3： 黄色问号  4: 锁 0：感叹号
     * @param callback 确认回调函数
     * @returns {*}
     */
    Base.alert = function (message, options, callback) {
    	var json = {time: 2000, icon:1};
    	options = $.extend({}, json, options);
    	if (typeof(layer) != 'undefined' && layer.alert) {
	        layer.alert(message, options,function (index) {
	            var flag = true;
	            if ($.isFunction(callback)) {
	                flag = callback();
	            }
	            if (flag != false) {
	                layer.close(index);
	            }
	        });
	        
	        return;
    	}
    	
    	options = $.extend({title:'信息'}, options);
		return Boxy.ask(message, ['确认'], callback, options);
    }

    /**
     * 确认框
     * @param message 内容
     * @param after 确认回调函数
     * @param options
     * @returns {*}
     */
    Base.confirm = function (message, ok, cancel, options) {
    	options = $.extend({title:'确认'}, options);
    	
    	if (typeof(layer) != 'undefined' && layer.alert) {
	        layer.confirm(message, function (index) {
	            if ($.isFunction(ok)) {
	                ok();
	            }
	            layer.close(index);
	        }, function (index) {
	            if ($.isFunction(cancel)) {
	                cancel();
	            }
	            layer.close(index);
	        }, function (index) {
	            if ($.isFunction(cancel)) {
	                cancel();
	            }
	            layer.close(index);
	        });
	        
	        return;
    	}
    	//兼容以前的boxy
    	return Boxy.ask(message, ['确认', '取消'], function (response) {
            if (response == '确认') ok();
            if (response == '取消') cancel();
        }, options);
    }
    
    /**
	 * 渲染模板中的占位符
	 * @param template
	 * @param json
	 */
	Base.renderTemplate = function(template, json, handleFn) {
	    
		return template.replace(/([{]{1}\w+[}]{1})/g, function(tmpWord){
			var tmpKey = tmpWord.substring(1,tmpWord.length -1);
			var tmpVal = json[tmpKey];
			if (Base.isFunction(handleFn)) {
				tmpVal = handleFn.call(this, tmpVal, tmpKey, json);
			}
			if (typeof(tmpVal) == "string" || typeof(tmpVal) == "number") {
				return tmpVal;
	        }
	        
			return '';
		});
	};
	
	/*
	**如果对象未定义，或者为空，或者是空字符串，返回真，否则返回假
	*/
	Base.IsEmpty = window.IsEmpty = window.IsEmpty || function(obj){
		if(typeof(obj)=="undefined"||obj==null||(typeof(obj)!="object"&&(obj+"").replace(/ /g,"")=="")){//||obj.length==0
			return true;
		}
		return false;
	};
	
	/**
	 * 判断是否是function
	 */
	Base.isFunction = $.isFunction;
	
	/**
	 * 字符串转date
	 * 将 2014-10-1 12:30:15 字符串转成Date类型 TODO 待完善
	 * 兼容  2014/10/1 12:30:15 
	 */
	Base.strToDate = function (str) {// 
		if (str && str.indexOf('-') >= 0) {
			str = str.replace(/-/g,"/");
			return new Date(str);
		}
		return null;
	};
	
	/**
	 * 去（前后）空格
	 */
	Base.trim = function (str) {
		return $.trim(str);
	};
	
	/**
	 * 计算文字真实显示宽度
	 */
	Base.getTextRealWidth = function (str) {
	    var textCount = 0;
	    var enCount = 0;
	    var sCOunt = 0;
	    for (var i = 0; i < str.length; i++) {
	        var chartCode = str.charCodeAt(i);
	        if (chartCode > 255) {//汉字
	            textCount++;
	        }
	        else if (chartCode < 65) {//标点符号
	            sCOunt++;
	        }
	        else if (chartCode >= 65 && chartCode <= 122) {//英文
	            enCount++;
	        }
	    }
	    var width = textCount * 16 + enCount * 10 + sCOunt * 7;
	    return width;
	};
	
	
	 var quoteString=function(string){
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
	
	/**
	 * 渲染模板中的占位符
	 * @param template
	 * @param json
	 */
	Base.JSON = {
		decode: function(str) {
			var jsonStr = '';
			if (typeof(str)!="string" || Base.IsEmpty(str)) {
				return str;
			}
			if (typeof(JSON)!='undefined' && JSON.parse) {
				try{
					jsonStr = JSON.parse(str);
				}catch(e){
					jsonStr = str.trim();
				}
			} else {
				try{
					jsonStr = eval('('+str+')');
				}catch(e){
					jsonStr = str.trim();
				}
			}
			return jsonStr;
		},
		toJSON: function(o) {
			if(typeof(JSON)=='object'&&JSON.stringify)return JSON.stringify(o);
	        var type=typeof(o);
	        if(o===null)return"null";
	        if(type=="undefined")return undefined;
	        if(type=="number"||type=="boolean")return o+"";
	        if(type=="string")return quoteString(o);
	        if(type=='object'){
	            if(typeof o.toJSON=="function")return this.toJSON(o.toJSON());
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
	                    ret.push(this.toJSON(o[i])||"null");
	                return ("["+ret.join(",")+"]").replaceAll(":undefined",":null");
	            }
	            var pairs=[];
	            for(var k in o){
	                var name;
	                var type=typeof k;
	                if(type=="number")name='"'+k+'"';
	                else if(type=="string")name=quoteString(k);
	                else continue;
	                if(typeof o[k]=="function")continue;
	                var val=this.toJSON(o[k]);
	                pairs.push(name+":"+val);
	            }
	            return ("{"+pairs.join(", ")+"}").replaceAll(":undefined",":null");
	        }
		}
	};
	
	/**
     * 为空时返回默认值。不传默认值返回 &nbsp;
     * @param obj
     * @param defaultValue
     * @returns
     */
	Base.formatValue = function(obj, defaultValue) {
    	if (!defaultValue) {
    		defaultValue = "&nbsp;";
    	}
		if (Base.IsEmpty(obj)) {
			return defaultValue;
		}
		if (typeof (obj) == "string") {
			return obj.replace(/\s+/g, defaultValue);
		}
		return obj;
	};
	 
	/**
	 * 单个参数解密
	 * @param param 参数
	 * @param force 是否强制解密
	 * @return 解密后的参数
	 */
	Base.decryptParam = function(param,force) {
		if(!force){
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
			try{result = decodeURIComponent(unescape(str_out));}catch(e){result = param;} //以通用表单设计器"预览"地场景为例，要求捕获异常
			if(result.indexOf("${&`~m.';@#}") == 0){
				return result.replace("${&`~m.';@#}", "");
			}else {
				return param;
			}
		}
	}
	
	/**
	 * 批量加密-url参数加密
	 * @param url url全文
	 * @param force 是否强制加密，强制加密则忽略全局配置isEncryptParams
	 * @return 对参数部分加密后的url
	 */
	Base.encryptURL = function encryptURL(url,force){
		var index = url.indexOf("?"); //用indexOf而不用split是为了避免请求参数有问号
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
				var ind= qStrs[i].indexOf("="); //用indexOf而不用split是为了避免请求参数有等于号
				qv[0] = qStrs[i].substring(0,ind);
				qv[1] = qStrs[i].substring(ind+1);
				url += qv[0] + "=" + this.encryptParam(qv[1],true,force);
			}
		}
		return url;
	}
	Base.encryptParam = function encryptParam(parm,flg,force) {
		if(force!=true){
		  	return parm;
		}else{
			var str_in;
			var num_out = "";
			var a = ["A", "B", "C", "D", "E", "F", "G", "H", 
				"I", "Z", "K", "L", "M", "N", "O", "P", "Q", 
				"R", "S", "T", "U", "V", "W", "X", "Y", "Z"];
			//有的模块里边进行了连续2次encodeURIComponent转码，一旦用了此加密方法，服务端就无法自动解除第一层，所以在这里先解掉
			if(flg){//url里的参数，才先解一次
				parm = decodeURIComponent(parm); 
			}
			parm = encodeURIComponent(parm);
			str_in = escape("\$\{&`~m.';@#}"+parm);//加上标识符
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
	 * form表单序列化后转json
	 * 注：表单元素请添加name属性，与id保持一致
	 */
	Base.serializeJson = function($form){  
        var serializeObj = {};  
        var eleARR = $form.serializeArray();  
       // var str = $form.serialize();  
        $(eleARR).each(function(){  	
        	var key = $(this)[0].name;
        	var value = $(this)[0].value;
        	if(!Base.IsEmpty(value)){
        		if(serializeObj[key]){  
                    if($.isArray(serializeObj[key])){  
                        serializeObj[key].push(value);  
                    }else{  
                        serializeObj[key] = [serializeObj[key],value];  
                    }  
                }else{  
                    serializeObj[key] = value;   
                }  
        	}
        });  
        return serializeObj;  
    } 
	
	/**
	 * 验证表单内容格式
	 * formId ： 表单id
	 * 调用前请在要验证的表单元素内添加
	 * 	1.验证类型属性dataType： mobilephone 手机号码； email: 邮箱； require 必填项
	 * 2. 提示信息属性msg(非必填)
	 * 
	 * 返回值： 有错误返回false, 正确：true
	 * 
	 * 注：表单元素请添加name属性，与id保持一致
	 */
	Base.validateFormat = function(formId){
		$form = $("#"+formId);
		if(Base.IsEmpty(formId) || typeof(formId) != "string"){
			$form = $("form").eq(0);
		}
		var sMsg = "";
		var eleARR = $form.serializeArray();  
		var index = 1;
		$(eleARR).each(function(){
			var key = $(this)[0].name;
        	var value = $(this)[0].value;
        	var $obj = $("#"+key);
        	var dataType = $obj.attr("dataType");
        	var msg = $obj.attr("msg") || Base.errorTip[dataType];
        	var regExp = Base.RegExpJson[dataType];
        	
        	if(!Base.IsEmpty(dataType) && $obj.css("display") != "none" 
        			&& $obj.parents('tr').css("display") != "none"){   //隐藏域不检查
        		
        		//若含一个文本多个验证信息，如：192.168.104.16， 192.168.104.17
        		var arr = [];
        		if(!Base.IsEmpty(value)){
        			if(value.indexOf(",") > -1){
        				arr = value.split(",");
        			}else if(value.indexOf(";") > -1){
        				arr = value.split(";");
        			}else{
        				arr.push(value);
        			}
        		}
        		
        		//验证 
        		var len = arr.length;
        		var flag = false;
        		for(var i=0; i<len; i++){
        			if(regExp.test(arr[i])){
        				flag = true;
        				$obj.css("border","1px solid #cacaca");
        			}else{
        				flag = false;
        				break;
        			}
        		}
				//验证不通过处理
        		if(!flag){
        			//错误标红
        			$obj.css("border","1px solid red");
        			//添加提示
        			if(sMsg.indexOf(msg) == -1){
        				sMsg += (index+"."+msg+"</br>");
        				index++;
        			}
        		}
        	}
        	
		});
		//提示框
		if(sMsg.length > 0){
    		Base.alert("内容有如下错误，请重新填写：</br>" +sMsg, {icon:2, time:3000});
    		return false;
    	}else{
    		return true;
    	}
		
	}
	
	Base.errorTip = {
			 mobilephone: "请输入一个正确的手机号码！",
				email	: "请输入一个有效的电子邮件地址！",
				ip: "IP地址输入格式不对!",
				require : "这是必填的！"
	}
	
	Base.RegExpJson = {
			mobilephone: /^1(3|4|5|7|8)\d{9}$/,
			email: /^([\w]+)(.[\w]+)*@([\w-]+\.){1,5}([A-Za-z]){2,4}$/,
			ip: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/,
			require: /\S/
	}
	
	
	/**是否参数加密*/
	function isNotEncrypt(){
		return (getSystemParams("isEncryptParams","0") == '0');
	}
	
	window.Base = Base;
})(window);

