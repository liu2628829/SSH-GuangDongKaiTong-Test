var AjaxExprotFile=function(){
	return {
	    /**
	     С����������
	    el����id
	    url:��������ʱ���������
	    params:�������
	    intervalTime:��ʱ���Զ������ʱ��(����)
	    fn:������Ϻ�Ļص�����
	    */
	    exportLittleData:function(el,url,params,intervalTime,fn){
	    	if(typeof(WaitBar) != "undefined") WaitBar.show();//�ȴ���
			 
			 AjaxExprotFile.backFunction=fn;//ע��ûص�����
			 var t=1000;//������ֳ�ʱ�����Ĭ�ϸ�5���Զ��������󣬻�ȡ��Ҫ���ļ�
			 if(intervalTime&&intervalTime>0){t=intervalTime;}//�д���ʱ���Դ���ʱ��Ϊ׼
			 AjaxExprotFile.setBasePath();//��������·��
			 
			 //�ռ����
			 var param = new Object();
			 var obj = document.getElementById(el);
			 if(el == null || el == '' || obj == null) {//�ޱ�,��params��������Ϊ׼
			 	param = params;
			 }else {//�б����Ա�����Ϊ׼
			 	for(var i=0; i<obj.elements.length; i++) {
			 		with(obj.elements[i]){
			 			var cId = getAttribute("id");
			 			var elment = document.getElementById(cId);
			 			if( elment != null && typeof(elment) != "undefined") {
			 			    var reg = /'/gi;
			 			    value = value.replace(reg, "");
							value=value.replace(/(^\s*)|(\s*$)/,"");//���ͷβ���ַ�
			 				var reg = "param." + cId + " = '" + value + "'";
			 				eval(reg);
			 			}
			 		}
			 	}
			 }
			 //��ȡ�򴴽������ļ���Ҫ�õ���iframe
			 //AjaxExprotFile.iframe=AjaxExprotFile.createIframeOnNotExists();
			 //�ύ����
			 $.ajax({
				 type: 'POST',
				 url: encryptURL(url),
				 data: encryptParams(param),
				 async: true,//�첽��falseΪ����
				 timeout:40000,//40���ʱ
				 dataType: 'text',
				 success: function(data, textStatus) {
				 	//data��-1��ʾ����ǰ�����������࣬�������;1��ʾ���ļ���������
				 	if(data=="-1"){
				 		simpleAlert("��ǰͬ������������Ա������Ժ����ԣ�");
				 		if(typeof(WaitBar) != "undefined") WaitBar.hide();
				 		return;
				 	}
			 	 },
			 	 error: function(XMLHttpRequest, textStatus) {
			 	 	//AjaxExprotFile.interval=window.setInterval(AjaxExprotFile.reExport,t);//�Զ����ļ�
			 	 }
			 });
			 //���ܳɹ���������Զ����ļ�,����ʾ����
			 AjaxExprotFile.interval=window.setInterval(AjaxExprotFile.reExport,t);
	    }, 
	    /**
	     ������������
	    el����id
	    url:��������ʱ���������
	    params:�������
	    intervalTime:��ʱ���Զ������ʱ��(����)
	    fn:������Ϻ�Ļص�����
	    */
		exporLargeData: function(el,url,params,intervalTime,fn){//�������������������첽����
			//������������������Ҫ����ȷ��
			simpleAlert({msg:"ȷ��Ҫ���д��������ĵ���������", type:2, fn:function(){
					 AjaxExprotFile.exportLittleData(el,url,params,intervalTime,fn);
				}
			});
		},
		/**
		��ʱ�����Զ�����
		*/ 
		reExport: function(){//��ʱ������
			 $.ajax({
				 type: 'POST',
				 url: AjaxExprotFile.basePath+"/dataIO/exportFileLisener!fileLisener.action",
				 data: {},
				 async: true,//�첽��falseΪ����
				 timeout:20000,//20���ʱ
				 dataType: 'text',
				 success: function(data, textStatus) {
				 	if(data.indexOf("progress:")==0){
				 		var str=data.replace("progress:","");
				 		if(str*1>=100)str="99";
				 		if(typeof(WaitBar) != "undefined") WaitBar.setProgress(str,null);
				 	}else{//˵���ɹ��ҵ��ļ�
					 	window.clearInterval(AjaxExprotFile.interval);//�����ʱ����
					 	if(typeof(WaitBar) != "undefined") WaitBar.setProgress("100",data);
					 	if(AjaxExprotFile.backFunction){
					 		AjaxExprotFile.backFunction.call(this,data);//�����ص�
					 	}	
				 	}
			 	 },
			 	 error: function(XMLHttpRequest, textStatus) {}
			 });
		}, 
		/**
		��ȡ�򴴽�����ʱҪ�õ�iframe
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
		��������·��
		*/
		setBasePath:function(){
			var pathname=getPathName();
			var basepath=getFullName();
			AjaxExprotFile.basePath=basepath;
			return basepath;
		},
		iframe:null,//�����ļ�Ҫ�õ���iframe
		interval:null,//��ʱ��������
		backFunction:null,//�ص�����
		basePath:null//������·��
	}
}();