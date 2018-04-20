var AjaxExprotFile=function(){
	return {
	    /**
	     小数据量导出
	    el：表单id
	    url:导出数据时发起的请求
	    params:对象入参
	    intervalTime:超时后，自动监测间隔时长(毫秒)
	    fn:导出完毕后的回调函数
	    */
	    exportLittleData:function(el,url,params,intervalTime,fn){
	    	if(typeof(WaitBar) != "undefined") WaitBar.show();//等待条
			 
			 AjaxExprotFile.backFunction=fn;//注册好回调函数
			 var t=1000;//如果出现超时情况，默认隔5秒自动发起请求，获取需要的文件
			 if(intervalTime&&intervalTime>0){t=intervalTime;}//有传入时间以传入时间为准
			 AjaxExprotFile.setBasePath();//得上下文路径
			 
			 //收集入参
			 var param = new Object();
			 var obj = document.getElementById(el);
			 if(el == null || el == '' || obj == null) {//无表单,以params对象数据为准
			 	param = params;
			 }else {//有表单，以表单数据为准
			 	for(var i=0; i<obj.elements.length; i++) {
			 		with(obj.elements[i]){
			 			var cId = getAttribute("id");
			 			var elment = document.getElementById(cId);
			 			if( elment != null && typeof(elment) != "undefined") {
			 			    var reg = /'/gi;
			 			    value = value.replace(reg, "");
							value=value.replace(/(^\s*)|(\s*$)/,"");//清空头尾空字符
			 				var reg = "param." + cId + " = '" + value + "'";
			 				eval(reg);
			 			}
			 		}
			 	}
			 }
			 //获取或创建导出文件是要用到的iframe
			 //AjaxExprotFile.iframe=AjaxExprotFile.createIframeOnNotExists();
			 //提交请求
			 $.ajax({
				 type: 'POST',
				 url: encryptURL(url),
				 data: encryptParams(param),
				 async: true,//异步，false为阻塞
				 timeout:40000,//40秒后超时
				 dataType: 'text',
				 success: function(data, textStatus) {
				 	//data：-1表示，当前导出操作过多，名额过多;1表示，文件正常生成
				 	if(data=="-1"){
				 		simpleAlert("当前同步导出操作人员超额，请稍后再试！");
				 		if(typeof(WaitBar) != "undefined") WaitBar.hide();
				 		return;
				 	}
			 	 },
			 	 error: function(XMLHttpRequest, textStatus) {
			 	 	//AjaxExprotFile.interval=window.setInterval(AjaxExprotFile.reExport,t);//自动找文件
			 	 }
			 });
			 //不管成功与否，马上自动找文件,并显示进度
			 AjaxExprotFile.interval=window.setInterval(AjaxExprotFile.reExport,t);
	    }, 
	    /**
	     大数据量导出
	    el：表单id
	    url:导出数据时发起的请求
	    params:对象入参
	    intervalTime:超时后，自动监测间隔时长(毫秒)
	    fn:导出完毕后的回调函数
	    */
		exporLargeData: function(el,url,params,intervalTime,fn){//大数据量导出，发起异步请求
			//大数据量导出操作，要二次确认
			simpleAlert({msg:"确定要进行大数据量的导出操作？", type:2, fn:function(){
					 AjaxExprotFile.exportLittleData(el,url,params,intervalTime,fn);
				}
			});
		},
		/**
		超时后发起自动重连
		*/ 
		reExport: function(){//超时后重连
			 $.ajax({
				 type: 'POST',
				 url: AjaxExprotFile.basePath+"/dataIO/exportFileLisener!fileLisener.action",
				 data: {},
				 async: true,//异步，false为阻塞
				 timeout:20000,//20秒后超时
				 dataType: 'text',
				 success: function(data, textStatus) {
				 	if(data.indexOf("progress:")==0){
				 		var str=data.replace("progress:","");
				 		if(str*1>=100)str="99";
				 		if(typeof(WaitBar) != "undefined") WaitBar.setProgress(str,null);
				 	}else{//说明成功找到文件
					 	window.clearInterval(AjaxExprotFile.interval);//清除定时重连
					 	if(typeof(WaitBar) != "undefined") WaitBar.setProgress("100",data);
					 	if(AjaxExprotFile.backFunction){
					 		AjaxExprotFile.backFunction.call(this,data);//函数回调
					 	}	
				 	}
			 	 },
			 	 error: function(XMLHttpRequest, textStatus) {}
			 });
		}, 
		/**
		获取或创建导出时要用的iframe
		*/
		createIframeOnNotExists: function(){
			var obj=document.getElementById("ExportFile_Iframe");
			if(!obj){
				obj=document.createElement("<iframe>");
				with(obj){
					id="ExportFile_Iframe";
					name="ExportFile_Iframe";
				}
				obj.style.display="none";
				document.body.appendChild(obj);
			}
			return obj;
		},
		/**
		得上下文路径
		*/
		setBasePath:function(){
			var pathname=getPathName();
			var basepath=getFullName();
			AjaxExprotFile.basePath=basepath;
			return basepath;
		},
		iframe:null,//导出文件要用到的iframe
		interval:null,//定时重连对象
		backFunction:null,//回调函数
		basePath:null//上下文路径
	}
}();