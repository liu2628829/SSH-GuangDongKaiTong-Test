/**
 通用导出JS
 author:gaotao
 2012/06/25
*/
//var Catt_count=0;//标识当前页面第几次点“导出”按钮,仅用于测试时使用。
var Catt_AjaxExprotFile=function(){
	//获取鼠标的坐标
	function getMousePoint(ev) {
		// 定义鼠标在视窗中的位置
		var point = { x:0, y:0 };
        if(ev!=null){
            // 如果浏览器支持 pageYOffset, 通过 pageXOffset 和 pageYOffset 获取页面和视窗之间的距离
            if(typeof window.pageYOffset != 'undefined') {
                point.x = window.pageXOffset;
                point.y = window.pageYOffset;
            }
            // 如果浏览器支持 compatMode, 并且指定了 DOCTYPE, 通过 documentElement 获取滚动距离作为页面和视窗间的距离
            // IE 中, 当页面指定 DOCTYPE, compatMode 的值是 CSS1Compat, 否则 compatMode 的值是 BackCompat
            else if(typeof document.compatMode != 'undefined' && document.compatMode != 'BackCompat') {
                point.x = document.documentElement.scrollLeft;
                point.y = document.documentElement.scrollTop;
            }
            // 如果浏览器支持 document.body, 可以通过 document.body 来获取滚动高度
            else if(typeof document.body != 'undefined') {
                point.x = document.body.scrollLeft;
                point.y = document.body.scrollTop;
            }
            // 加上鼠标在视窗中的位置
            point.x += ev.clientX;
            point.y += ev.clientY;
        }
		// 返回鼠标在视窗中的位置
		return point;
	}

	//使用列表字段直接导出数据
	function Catt_Export_Direct(option){
	    var heads="",cols1="";
	    var cols=option.cols;
	    var idFieldCname = "";
		if(!IsEmpty(option.filterCon)){
			var filterCons = option.filterCon.split(",");
			for(var i=0; i<cols[0].length; i++){
				var isHas = false;
				var fd = cols[0][i]; 
				if(!IsEmpty(fd.hidden)){ fd.hidden = (fd.hidden+"").toLowerCase(); }
				if(!IsEmpty(fd.isExport)){ fd.isExport = (fd.isExport+"").toLowerCase(); }
				var boo = (fd.field==option.idField);
				if(boo){idFieldCname = fd.title;}//主键中文字段名
				if(fd.hidden!="true" ||(option.keepPkFiled==1 && boo) || fd.isExport=="true"){//排除隐藏域，如果导出后将用于导入，要把主键字段放出来
					for(var j=0; j<filterCons.length; j++){//排除要过滤的字段
						if(fd.field==filterCons[j] || fd.field.indexOf(filterCons[j])>=0){
							isHas = true;
							continue;
						}
					}
					if(!isHas){
						if(heads.length>0){
							heads+=",";cols1+=",";
						}
						heads+=(fd.title);
						cols1+=(fd.field);
					}
				}
			}
		}else{
			for(var i=0; i<cols[0].length; i++){
				var fd = cols[0][i]; 
				if(!IsEmpty(fd.hidden)){ fd.hidden = (fd.hidden+"").toLowerCase();}
				if(!IsEmpty(fd.isExport)){ fd.isExport = (fd.isExport+"").toLowerCase(); }
				var boo = (fd.field==option.idField);
				if(boo){idFieldCname = cols[0][i].title;}//主键中文字段名
				if(fd.hidden!="true" ||(option.keepPkFiled==1 && boo) || fd.isExport=="true"){//排除隐藏域，如果导出后将用于导入，要把主键字段放出来
					if(heads.length>0){
						heads+=",";cols1+=",";
					}
					heads+=(fd.title);
					cols1+=(fd.field);
				}
			}
		}
		if(option.keepPkFiled==1){
		    simpleAlert({
		       msg:"注意：\n1.导出数据文件后，一定不要对【"+idFieldCname+"】字段进行修改或清空，否则会影响后续导入数据的准确性。\n2.请针对自己关注字段进行修改，不关注的字段不要修改，不可整列删除任何字段列，否则会影响数据的正常导入。",
		       ok:'知道了',
		       fn:function(){Catt_AjaxExprotFile.Catt_Export_Data(option, heads, cols1,3);}
		    });
		}else{
			Catt_AjaxExprotFile.Catt_Export_Data(option, heads, cols1,3);
		}
	    
	}
	
	
	//生成弹出菜单
	function Catt_Export_CreateMenu(option){
		var div = document.createElement("div");
		with(div){id="optMenu_"+option.formId;}
		with(div.style){width="140px";overflow:"hidden"}
		
		var items = [
			{type:"button",text:"列表全部字段",bodyStyle:"chargeback",useable:"T",
				handler:function(){option.keepPkFiled=0;Catt_Export_Direct(option);}
			}/*, 
			{type:"button",text:"二次筛选字段",bodyStyle:"gongdang",useable:"T",
				handler:function(){Catt_Export_FilterColums(option);}
			}*/,
            {type:"button",text:"导出并用于导入",bodyStyle:"chargeback",useable:"T",
				handler:function(){Catt_Export_InOut_Colums(option);}
			}
		];
		
		for(var i=0;i<items.length;i++){
			var tempMenu=document.createElement("div");
			$(tempMenu).text(items[i].text);//tempMenu.innerText=items[i].text;
			tempMenu.iconCls=items[i].bodyStyle;
			$(tempMenu).bind("click",items[i].handler);
			div.appendChild(tempMenu);
		}
		document.body.appendChild(div);
		
		var ev = option.ev;
        if(ev==null){ 
           try{ev = event;}catch(e){}
        }
        var point = getMousePoint(ev);
        //弹出菜单定位问题
        $("#optMenu_"+option.formId).menu({});
        $("#optMenu_"+option.formId).menu("show", { left:point.x,  top:point.y  });
        
	}
	
        //创建导出导入字段div入口
    var fastDevelopPageDiv = {
        id: "fastDevelopPageDiv",
        width: 750,
        height: 500,
        zIndex: 9000,
        draggable: true, //拖动
        resizable: true, //改变大小
        modal: true, //后台页面可编辑
        closed: false, //是否关闭
        minimizable: false,//最小化按钮
        maximizable: true,//最大化按钮
        closable: true, //关闭按钮
        collapsible: true //收缩按钮 
    };
    
    //此项仅被应用于综合网管版通用表单
	function Catt_Export_InOut_Colums(option){
	    //update by gt 2013/05/29
	    //从表单属性字段定义中拿到所有必填的字段
	    var formId = option.formId.replace("form_","");
	    AjaxRequest.doRequest(null, path+"/commonModule/fastDevelop/fastDevelopApp!getFastDevelopFormAttrList.action", 
	   			{iFormId: formId}, function(backData){
			var jsonData = decode(backData);	    
	     	//与当前界面的字段合并(只合并必填的字段)
	     	if(jsonData && jsonData.length>0){
	     	   var cols = option.cols[0];
	     	   for(var i=0; i<jsonData.length; i++){
	     	   	 var f1 = jsonData[i];
	     	   	 if(f1.iRequired!="1"){continue;} //非必填的字段不处理
	     	   	 var boo = true;
	     	   	 for(var j=0; j<cols.length; j++){
	     	   	 	if(cols[j].field == f1["sFieldName"]){boo=false;break;} //界面上已经有此字段，也不必处理了
	     	   	 }
	     	   	 if(boo){
	     	   	 	var json = {field:f1["sFieldName"],title:f1["sPropertyName"]};
	     	   	 	cols.push(json);
	     	   	 }
	     	   }
	     	}
	     	
	    	option.Catt_export_from = 1; //加一个标识参数，标识导出并用于导入 buildSqlData.java里拼查询SQL时会用到
	   		option.keepPkFiled = 1; //主键字段一定导入
	    	Catt_Export_Direct(option); //然后当作普通导出执行
	    });
	   
        /*fastDevelopPageDiv.title = "导入模板定义";
        var sTableName =option.url.substring( option.url.indexOf('Fd_sTableName=')+14);
		fastDevelopPageDiv.url = path+"/admin/commonModule/importMgr/templatesAddEditByFd.jsp?Fd_iFormId="+
            option.formId.substring(option.formId.indexOf('_')+1)+"&sTableName="+sTableName+"&sTemplateName="+option.fileName;
        showJqueryWindow(fastDevelopPageDiv);
        Catt_AjaxExprotFile.tempOption = option;*/
        }
        
	//创建筛选字段div入口
	function Catt_Export_FilterColums(option){
		//创建div和table
		if(IsEmpty(option.layoutId)){
			var div = Catt_Export_CreateDiv(option);
			document.body.appendChild(div);
		}else{//存在则删除
			var layoutObj = $("#"+option.layoutId);
			if(layoutObj){
				layoutObj.remove();
				var div = Catt_Export_CreateDiv(option);
				document.body.appendChild(div);
			}
		}
		
		//增加工具栏
		Catt_Export_CreateToolbar(option);
		//创建datagrid
		Catt_Export_CreateDatagrid(option);
		//加载数据
		Catt_Export_LoadData(option);
		//自适应宽高
		
		$("#"+option.layoutId).window({
			title:"筛选导出字段", 
			width:300,   
			height:450,   
			modal:true,
			collapsible:false,
			minimizable:false,
			maximizable:false,
			//resizable:false,
			onClose:function(){
				$("#"+option.layoutId).remove();
			},
			onResize:function(width, height){
				//修改内部panel的宽高
				$("#"+option.panelId).panel("resize", {width:width-10, height:height-75});
				commonUtil.setWH({eId:option.tableId,eType:"jquery",rId:"Export_PanelDiv_"+option.formId,width:-2,height:-1});
			}
		});
	}
	
	//创建筛选字段div
	function Catt_Export_CreateDiv(option){
		
		var layoutDiv = document.createElement("div");
		layoutDiv.style.overflow="hidden";
		with(layoutDiv){id="Export_Div_"+option.formId;}
		layoutDiv.className = "easyui-layout";
		option.layoutId = layoutDiv.id;

		var centerDiv = document.createElement("div");
		with(centerDiv){id="Export_CenterDiv_"+option.formId; region="center"}
		with(centerDiv.style){border="0px"}
		option.centerId = centerDiv.id;
		
		var panelDiv = document.createElement("div");
		with(panelDiv){id="Export_PanelDiv_"+option.formId}
		option.panelId = panelDiv.id
		
		var table = Catt_Export_CreateTable(option);	
		
		var southHtml = "<table width='100%' height='100%' class='formbasic'><tr>";
	    southHtml += "<td style='text-align:center;border:0px;'><ul class='btn_hover'>";
	    southHtml += "<li onClick='Catt_AjaxExprotFile.getFilterColumns(\""+option.tableId+"\",\""+option.fileName+"\",\""+option.formId+"\",\""+option.url+"\",\""+option.layoutId+"\")'><a href='#'><span>";
	    southHtml += "<div class='ok'>导出</div>";
	    southHtml += "</span></a></li>";
	    southHtml += "<li onClick='Catt_AjaxExprotFile.closeWin(\""+option.layoutId+"\")'><a href='#'><span>";
	    southHtml += "<div class='no'>取消</div>";
	    southHtml += "</span></a></li>";
	    southHtml += "</ul></td></tr></table>";
		
		var southDiv = document.createElement("div");
		with(southDiv){id="Export_SouthDiv_"+option.formId; region="south"}
		with(southDiv.style){height="40px";};
		southDiv.innerHTML = southHtml;
		option.southId = southDiv.id;
		
		panelDiv.appendChild(table);
		centerDiv.appendChild(panelDiv);
		layoutDiv.appendChild(centerDiv);
		layoutDiv.appendChild(southDiv);
		
		return layoutDiv;
	}
	
	/*创建在线与离线导出div
	* author:qiaoqide
	*/
	function Catt_Export_OffLine(option){
		//创建div和table
		if(IsEmpty(option.layoutId)){
			var div = CreateOffLineExport_Div(option);
			document.body.appendChild(div);
		}else{//存在则删除
			var layoutObj = $("#"+option.layoutId);
			if(layoutObj){
				layoutObj.remove();
				var div = CreateOffLineExport_Div(option);
				document.body.appendChild(div);
			}
		}
		initExportButton(option);
		
		$("#"+option.layoutId).window({
			title:"导出数据", 
			width:380,   
			height:130,   
			modal:true,
			collapsible:false,
			minimizable:false,
			maximizable:false,
			resizable: false,
			onClose:function(){
				$("#"+option.layoutId).remove();
			}
		});
	}
	
	//初始化按钮
	function initExportButton(option){
	   var buttons=[
				{
					btnId:'btnOfflineExport', btnPicName:'ok.gif', btnName:'导出',
					btnFun: function(){
					   Catt_AjaxExprotFile.offLineExportData(option, option.layoutId);
					   return false;
					}
				},
				{
					btnId:'btnCancel', btnPicName:'no.gif', btnName:'取消',
					btnFun: function(){Catt_AjaxExprotFile.closeWin(option.layoutId);}
				}
				];
		var btnJson = {eId:"btnDiv",btnAlign : "center", btnOptions : buttons};
		commonUtil.initButtonDiv(btnJson);
	}
	
	/*创建离线导出div
	*author:qiaoqide
	*/
	function CreateOffLineExport_Div(option){
		
		var layoutDiv = document.createElement("div");
		layoutDiv.style.overflow = "hidden";
		with(layoutDiv){id = "OffLine_Export_Div_"+option.formId;}
		layoutDiv.className = "easyui-layout";
		option.layoutId = layoutDiv.id;
		
		var centerHtml = "<table width='100%' height='60' cellpadding='0' cellspacing='0' border='0' class='formbasic'>";
		centerHtml += "<tr><th style='text-align:center;'>";
		centerHtml += "<input type='radio' id='online' name='export' value='0' checked='checked' />";
		centerHtml += "<label for='online'>导出所有数据</label>";
		centerHtml += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		centerHtml += "<input type='radio' id='offline' name='export' value='1'>";
		centerHtml += "<label for='offline'>导出所有数据(离线)</label>";
		centerHtml += "</th></tr></table>";
		
		var centerDiv = document.createElement("div");
		with(centerDiv){id="OffLine_Export_CenterDiv_"+option.formId; region="center"}
		with(centerDiv.style){border="0px"}
		option.centerId = centerDiv.id;
		centerDiv.innerHTML = centerHtml;
		
		var colsStr = $.toJSON(option.cols);
		var southHtml = "<table width='100%' height='100%' class='formbasic'><tr>";
	    southHtml += "<th style='text-align:center;border:0px;'><div id='btnDiv'></div>";
	    southHtml += "</th></tr></table>";
		
		var southDiv = document.createElement("div");
		with(southDiv){id="OffLine_Export_SouthDiv_"+option.formId; region="south"}
		with(southDiv.style){height="40px";};
		southDiv.innerHTML = southHtml;
		option.southId = southDiv.id;

		layoutDiv.appendChild(centerDiv);
		layoutDiv.appendChild(southDiv);
		
		return layoutDiv;
	}
	
	
	//创建筛选字段table
	function Catt_Export_CreateTable(option){
		var table = document.createElement("table");
		with(table){id="Export_Table_"+option.formId}
		option.tableId = table.id;
		return table;
	}
	
	function Catt_Export_CreateToolbar(option){
		$("#"+option.panelId).panel({
			title:"&nbsp;",
			width:286,
		  	height:375,
		  	tools: [{   
		  		title: "上移",
	     		iconCls:'icon-up',   
	     		handler:function(){
	     			//有数据，并且不是第一行
	     			var rows = $("#"+option.tableId).datagrid("getRows");
	     			if(rows && rows.length > 0){
	     				var selectRow = $("#"+option.tableId).datagrid("getSelected");
	     				//for(var i in selectRow){alert(i+"=="+selectRow[i]);	}
	     				var rowIndex = $("#"+option.tableId).datagrid("getRowIndex", selectRow);
	     				if(rowIndex != 0){
	     					var isChecked = $("#"+option.tableId).datagrid("isChecked", {index:rowIndex});
	     					$("#"+option.tableId).datagrid("deleteRow", rowIndex);//先删除要移动的行
	     					rowIndex -= 1;
	     					$("#"+option.tableId).datagrid("insertRow", {index:rowIndex, row:selectRow});//再插入原来的行
	     					$("#"+option.tableId).datagrid("selectRow", rowIndex);//并且选中移动的行
	     					$("#"+option.tableId).datagrid("checkRow", {index:rowIndex, check:isChecked});
	     				}
	     			}	
	     		}   
	   		},{   
	   			title: "下移",
	    		iconCls:'icon-down',
	     		handler:function(){
	     			//有数据，并且不是最后一行
	     			var rows = $("#"+option.tableId).datagrid("getRows");
	     			if(rows && rows.length > 0){
	     				var selectRow = $("#"+option.tableId).datagrid("getSelected");
	     				var rowIndex = $("#"+option.tableId).datagrid("getRowIndex", selectRow);
	     				if(rowIndex != rows.length - 1){
	     					var isChecked = $("#"+option.tableId).datagrid("isChecked", {index:rowIndex});
	     					$("#"+option.tableId).datagrid("deleteRow", rowIndex);//先删除要移动的行
	     					rowIndex += 1;
	     					$("#"+option.tableId).datagrid("insertRow", {index:rowIndex, row:selectRow});//再插入原来的行
	     					$("#"+option.tableId).datagrid("selectRow", rowIndex);//并且选中移动的行
	     					$("#"+option.tableId).datagrid("checkRow", {index:rowIndex, check:isChecked});
	     				}
	     			}
	     		}   
	   		}]   
		});
	}
	
	//创建筛选字段datagrid
	function Catt_Export_CreateDatagrid(option){
		var chkCol = [{field:"ck",checkbox:true}];
		var cols = [[
			{field:"fieldName", title:"field字段",width:50, hidden:"true"},
			{field:"fieldDisplay", title:"字段名称",width:50}
		]];
		$("#"+option.tableId).datagrid({
		    idField:"fieldName",
			columns:cols,
			frozenColumns:[chkCol],//冻结列，且此列为复选框
			nowrap:true,
			fitColumns:true,
			rownumbers:false,
			width:$("#"+option.panelId).panel("options").width-2,
			height:$("#"+option.panelId).panel("options").height-28
		});
	}
	
	//加载datagrid数据
	function Catt_Export_LoadData(option){
		
		//加载的数据为columns中的field和title属性
		var cols = option.cols;
		var data = [];
		if(!IsEmpty(option.filterCon)){
			var filterCons = option.filterCon.split(",");
			for(var i=0; i<cols[0].length; i++){
				var isHas = false;
				if(!IsEmpty(cols[0][i].hidden))
					cols[0][i].hidden = (cols[0][i].hidden+"").toLowerCase();
				if(cols[0][i].hidden!="true" || cols[0][i].field == option.idField){//排除隐藏域，放开主键字段
					for(var j=0; j<filterCons.length; j++){//排除要过滤的字段
						if(cols[0][i].field==filterCons[j] || cols[0][i].field.indexOf(filterCons[j])>=0){
							isHas = true;
							continue;
						}
					}
					if(!isHas){
						data.push({fieldName:option.cols[0][i].field, fieldDisplay:option.cols[0][i].title});
					}
				}
			}
		}else{
			for(var i=0; i<cols[0].length; i++){
				if(!IsEmpty(cols[0][i].hidden))
					cols[0][i].hidden = (cols[0][i].hidden+"").toLowerCase();
				if(cols[0][i].hidden!="true"){
					data.push({fieldName:option.cols[0][i].field, fieldDisplay:option.cols[0][i].title});
				}
			}
		}
		$("#"+option.tableId).datagrid("loadData", data);
		//设置勾选
		var rows = $("#"+option.tableId).datagrid("getRows");
		$("#"+option.panelId).find(" .datagrid-header-check").find(":checkbox").attr("checked", "true");//勾选头部复选框
		for(var i=0; i<rows.length; i++){
			$("#"+option.tableId).datagrid("checkRow", {index:i,check:true});
		}
	}
	
	return {
	    /**
	     数据导出
	    el：表单id
	    url:导出数据时发起的请求
	    params:对象入参
	    intervalTime:超时后，自动监测间隔时长(毫秒)
	    fn:导出完毕后的回调函数(暂无用)
	    */
		exportData: function(el,url,params,intervalTime,fn){//大数据量导出，发起异步请求
			 Catt_AjaxExprotFile.backFunction=fn;//注册好回调函数
			 var t=1000;//如果出现超时情况，默认隔1秒自动发起请求，获取需要的文件
			 if(intervalTime&&intervalTime>0){t=intervalTime;}//有传入时间以传入时间为准
			 Catt_AjaxExprotFile.setBasePath();//得上下文路径
			 Catt_AjaxExprotFile.el=el;
			 Catt_AjaxExprotFile.url=url;
			 if(Catt_AjaxExprotFile.interval){
			    window.clearInterval(Catt_AjaxExprotFile.interval);
			    Catt_AjaxExprotFile.interval=null;
			  }
			 
			 //收集入参
			 var param = new Object();
			 var obj = document.getElementById(el);
			 if(el == null || el == '' || obj == null) {//无表单,以params对象数据为准
			 	param = params;
			 }else {//有表单，先拿到对象参数，然后再拿表单数据
			    if(params&&typeof(params)=="object"){param = params;}
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
			 //param.Catt_count=++Catt_count;
			 param.EXP_fileName=encodeURIComponent(param.EXP_fileName); //避免中文乱码问题，进行转码，服务端会解码
			 Catt_AjaxExprotFile.params=param;
			 Catt_AjaxExprotFile.params.Catt_export_random=parseInt(Math.random()*1000);//页面唯一标识
			 Catt_AjaxExprotFile.params.Catt_export_confirm_flag="first";//第一次发起请求
			 Catt_AjaxExprotFile.params.Catt_export_isOver=false;
			 Catt_AjaxExprotFile.params.Catt_export_limitBreak=false;
			 Catt_AjaxExprotFile.params.Catt_export_overData=null;
			 Catt_AjaxExprotFile.params.Catt_export_interupt=0;
			 Catt_AjaxExprotFile.params.Catt_export_finish_flag="0";
			 Catt_AjaxExprotFile.params.Catt_export_isBlock=true;//阻塞中
			 Catt_AjaxExprotFile.params.Catt_export_flag=1;
			 
			 /*构建用到导出的form对象
			 if(!Catt_AjaxExprotFile.exportForm){
			 	var exportForm=document.createElement("form");
			 	exportForm.method="post";
			 	exportForm.style.display="none";
			 	document.body.appendChild(exportForm);
			 	Catt_AjaxExprotFile.exportForm=exportForm;
			 }*/
			 
			 //检查三项必须的参数
			 //var msg1=Catt_AjaxExprotFile.checkRequire();
			 //if(msg1.length>0){var str=msg1.join("\n");alert(str);simpleAlert(msg1.join("\n"));return false;}	
			 if(typeof(WaitBar) != "undefined") WaitBar.show(2);//等待条
			 //提交请求
			 $.ajax({
				 type: 'POST',
				 url: encryptURL(url),
				 data: encryptParams(param),
				 async: true,//异步，false为阻塞
				 timeout:20000,//20秒后超时
				 dataType: 'text',
				 success: function(data, textStatus) {
				    Catt_AjaxExprotFile.params.Catt_export_isBlock=false;
				 	Catt_AjaxExprotFile.createInfo(data,function(boo){
				 	   Catt_AjaxExprotFile.reExport(el,url,intervalTime,fn);
				 	   if(boo){
				 	     Catt_AjaxExprotFile.setInterval(function(){Catt_AjaxExprotFile.reExport(el,url,intervalTime,fn)},t);
				 	   }
				 	});
			 	 },
			 	 error: function(XMLHttpRequest, textStatus) {//请求超时或其它错误
			 	 	Catt_AjaxExprotFile.whenBlock(el,url,intervalTime,fn);
			 	 }
			 });
			 
	    },
	    /**
	      检查三个必须的入参
	    */
	    checkRequire:function(){
	    	var EXP_fileName=Catt_AjaxExprotFile.params.EXP_fileName;
	    	var EXP_heads=Catt_AjaxExprotFile.params.EXP_heads;
	    	var EXP_cols=Catt_AjaxExprotFile.params.EXP_cols;
	        var msg=[];
	        if(!EXP_fileName||EXP_fileName.replace(/ /g,"")=="")
	        	msg.push("文件名称不能为空");
	        if(!EXP_heads||EXP_heads.replace(/ /g,"")=="")
	        	msg.push("表头名称不能为空");
	        if(!EXP_cols||EXP_cols.replace(/ /g,"")=="")
	        	msg.push("字段名称不能为空");
	        if(msg.length==0&&(EXP_heads.split(",").length!=EXP_cols.split(",").length))
	        	msg.push("表头名称与字段名称数量不一致");
	        return msg;
	    }, 
	    /**
	      定时请求
	    */
	    setInterval:function(fn,t){
	    	window.clearInterval(Catt_AjaxExprotFile.interval);
	    	Catt_AjaxExprotFile.interval=window.setInterval(fn,t);
	    },
		/**
		超时后发起自动重连
		*/
		reExport: function(el,url,intervalTime,fn){//超时后重连
			 if(Catt_AjaxExprotFile.params.Catt_export_break_flag=="1"){ return; }
			 if(Catt_AjaxExprotFile.params.Catt_export_confirm_flag!="confirm"&&Catt_AjaxExprotFile.params.Catt_export_confirm_flag!="cancle")
			 Catt_AjaxExprotFile.params.Catt_export_confirm_flag="nature";//非第一次发起请求
		 	 if(typeof(WaitBar) != "undefined") WaitBar.show(2);//等待条
			 //提交请求
			 $.ajax({
				 type: 'POST',
				 url: encryptURL(url),
				 data: encryptParams(Catt_AjaxExprotFile.params),
				 async: true,//异步，false为阻塞
				 timeout:20000,//20秒后超时
				 dataType: 'text',
				 success: function(data, textStatus) {
			 		if(Catt_AjaxExprotFile.params.Catt_export_confirm_flag=="cancle"||!Catt_AjaxExprotFile.params.Catt_export_isOver){
				 		Catt_AjaxExprotFile.params.Catt_export_isBlock=false;
						Catt_AjaxExprotFile.createInfo(data,function(boo){Catt_AjaxExprotFile.reExport(el,url,intervalTime,fn)});
				 	}else{
						Catt_AjaxExprotFile.createInfo(Catt_AjaxExprotFile.params.Catt_export_overData,function(boo){Catt_AjaxExprotFile.reExport(el,url,intervalTime,fn)});
				 	}
				 },
			 	 error: function(XMLHttpRequest, textStatus) {
			 	 	if(Catt_AjaxExprotFile.params.Catt_export_break_flag!="1"){
			 	 	 Catt_AjaxExprotFile.whenBlock(el,url,intervalTime,fn);
			 	 	}
			 	 }
			  });
		}, 
		/**
		中断导出
		*/
		breakExport: function(finish, url){
			 if(Catt_AjaxExprotFile.params.Catt_export_confirm_flag!="confirm"){
			 	Catt_AjaxExprotFile.params.Catt_export_confirm_flag="nature";//非第一次发起请求
			 }
		 	 window.clearInterval(Catt_AjaxExprotFile.interval);//清除定时重连
		 	 //alert(1);
		 	 if(finish==1){
			 	 Catt_AjaxExprotFile.params.Catt_export_finish_flag="1";//标识下载开始，删除线程
		 	 }else{
			 	 if(typeof(WaitBar) != "undefined"){ WaitBar.setMsg("<font style='font-weight: bold;color:red'>&nbsp;&nbsp;&nbsp;&nbsp;取消中....</font>");}
		 	 }
		 	 Catt_AjaxExprotFile.params.Catt_export_break_flag="1";//标识打断
			 //提交请求
			 $.ajax({
				 type: 'POST',
				 url: encryptURL(Catt_AjaxExprotFile.url),
				 data: encryptParams(Catt_AjaxExprotFile.params),
				 async: true,//异步，false为阻塞
				 timeout:20000,//20秒后超时
				 dataType: 'text',
				 success: function(data, textStatus) {
				  			 var msg=null;
		   					 eval("msg="+data);
		   					 if(msg.iState=="6"){//成功
		   					 	setTimeout(function(){Catt_AjaxExprotFile.setWaitBar();},1000);
		   					 	Catt_AjaxExprotFile.params.Catt_export_isBlock=false;
		 					 }else if(msg.iState=="5"){
		 					 	//线程删除成功
		 					 	Catt_AjaxExprotFile.params.Catt_export_finish_flag="0";
		 					 }else{//否则继续打断
		 					 	Catt_AjaxExprotFile.breakExport();
		 					 }
		 				   },
			 	 error: function(XMLHttpRequest, textStatus) {Catt_AjaxExprotFile.breakExport();}
			 });
		},
		/**
		得上下文路径
		*/
		setBasePath:function(){
			var pathname=getPathName();
			var basepath=getFullPath();
			Catt_AjaxExprotFile.basePath=basepath;
			return basepath;
		},
		/*
		当程序被阻塞时(这种情况极少，一般就是因为底层SQL查的结果集超大时，SQL响应速度超慢而导致的程序阻塞)
		*/
		whenBlock:function(el,url,intervalTime,fn){
		    var m="数据加载缓慢，系统阻塞中<br>请重置过滤条件，减少数据量<br>如果重置，<a href='javascript:void(0)' onclick='Catt_AjaxExprotFile.breakExport();'><font style='font-weight: bold;color:red'>请先点击这里取消本次导出</font></a><br>";
			if(typeof(WaitBar) != "undefined"){WaitBar.setMsg(m);}
			Catt_AjaxExprotFile.params.Catt_export_isBlock=true;
			window.setTimeout(function(){Catt_AjaxExprotFile.reExport(el,url,intervalTime,fn)},2000);
		},
		/**
		处理Waitbar
		*/
		setWaitBar:function(){
			if(typeof(WaitBar) != "undefined") {
			    WaitBar.hideProgress();
			    WaitBar.setMsg("");
			    WaitBar.hide(2);
	 		}
		},
		/*
		构建提示信息
			-1,超出最大导出线程数
			0,导出过程中
			1,导出成功
			2,无数据
			3,数据量超出警示值,提示用户
			4,二进制导出方式，总记录数越限
			5,用户二次确认不导出
			6,成功打断导出线程
		*/
		createInfo:function(data,fun){
			if(Catt_AjaxExprotFile.params.Catt_export_break_flag=="1"){return;}//如果已经"取消"导出，后边就不需要再有任何提示
		    
		    var msg=null;
		    /*if(typeof(IS_CAS)!="undefined"&&IS_CAS!=null&&IS_CAS=="1"&&data==""){
				window.location.href = path+"/login.jsp";
				return;
		    }*/
		    eval("msg="+data);
		    if(msg.iState=="1"&&msg.url==""){//偶尔会出现这种情况,还未找到原因
		    	window.clearInterval(Catt_AjaxExprotFile.interval);//清除定时重连
		    	Catt_AjaxExprotFile.params.Catt_export_isOver=true;
				Catt_AjaxExprotFile.setWaitBar();
		 		simpleAlert("导出失败，请重试！");
		    	return;
		    }
		    if(msg.iState!="0"){
		    	window.clearInterval(Catt_AjaxExprotFile.interval);//清除定时重连
		    	Catt_AjaxExprotFile.interval=null;
				Catt_AjaxExprotFile.setWaitBar();
		    }
			switch(msg.iState){
				case "-1":
				  simpleAlert("系统最多允许同时存在"+msg.MAX_EXPORTER+"个导出进程，目前"+msg.MAX_EXPORTER+"个进程，正被占用中，请稍后操作！");
				  Catt_AjaxExprotFile.params.Catt_export_isOver=true;
				  break;
				case "0":
				  var m="当前导出总记录数为<font style='font-weight: bold;color:red'>"+msg.totalCount+"</font>行<br>";
				  //当前导出总记录数为X行，文件生成进度 100%，文件压缩打包中，请稍后!
				  var progress=msg.currentCount/msg.totalCount*100;
				  progress=Math.round(progress);
				  if(progress<100){m+="文件生成进度<font style='font-weight: bold;color:red'>"+progress+"%</font><br><a href='javascript:void(0)' onclick='Catt_AjaxExprotFile.breakExport();'><font style='font-weight: bold;color:red'>点击这里可取消导出</font></a><br>";
				  }else{m+="文件生成进度<font style='font-weight: bold;color:green'>100%</font><br>文件压缩打包中，请稍等!";}
				  if(typeof(WaitBar) != "undefined"){WaitBar.setMsg(m);}
				  if(!Catt_AjaxExprotFile.interval){
				  	fun.call(this,true);
				  }
				  break;
				case "1":
				  //当前导出总记录数为X行，文件生成进度 100%，文件压缩打包完毕，点击这里 下载文件!
				  /*var m="当前导出总记录数为<font style='font-weight: bold;color:red'>"+msg.totalCount+"</font>行<br>文件生成进度 <font style='font-weight: bold;color:green'>100%</font><br>文件压缩打包完毕<br>";
				  m+="请<a href='"+msg.url+"' onclick='WaitBar.hideProgress();WaitBar.hide(2);Catt_AjaxExprotFile.breakExport(1);'><font style='font-weight: bold;color:red'>点击这里下载文件</font></a>";//onclick中不能加：WaitBar.setMsg(\"\")，否则链接就先被清空了
			      WaitBar.setMsg(m);*/
			      /*Catt_AjaxExprotFile.params.Catt_export_isOver=true;
			      Catt_AjaxExprotFile.exportForm.action=Catt_AjaxExprotFile.basePath+"/admin/common/commonExport.jsp?fileName=人员&filePath="+msg.url;
			      Catt_AjaxExprotFile.exportForm.submit();*/
			      if(typeof(WaitBar) != "undefined"){
			      	WaitBar.hideProgress();WaitBar.hide(2);
			      }
			      Catt_AjaxExprotFile.breakExport(1, msg.url);
			      if(Catt_AjaxExprotFile.params.isOffLine != "1"){ //在线下载 add by qiaoqide 2013-10-29
			      	if($.browser.msie && ($.browser.version*1)<9){//低版本IE:IE8及以下
			          simpleAlert({msg:'数据文件生成完毕，点"确定"可下载!',type:"confirm", fn:function(){Catt_AjaxExprotFile.doLinkFile(msg.url,'');}});
			        }else{ //否则直接下载
			          Catt_AjaxExprotFile.doLinkFile(msg.url,'');
			        }
			      }
				  break;
				case "2":
				  simpleAlert("没有符合导出条件的数据可供导出！");
				  Catt_AjaxExprotFile.params.Catt_export_isOver=true;
				  break;
				case "3":
				  simpleAlert({msg:"当前导出结果将超过"+msg.EXP_CONFIRM_CONUT+"行，是否确定导出？", type:2, 
				    fn:function(){Catt_AjaxExprotFile.params.Catt_export_confirm_flag="confirm";fun.call(this,true);},
				    fnCancel:function(){Catt_AjaxExprotFile.params.Catt_export_confirm_flag="cancle";Catt_AjaxExprotFile.params.Catt_export_isOver=true;fun();}
				  });
				  break;
				case "4":
				  simpleAlert("此功能限制最大导出"+msg.EXP_BINARY_TYPE_LIMIT+"条数据<br>当前数据量已达到"+msg.totalCount+"条<br>超出"+(msg.totalCount-msg.EXP_BINARY_TYPE_LIMIT)+"条！");
				  Catt_AjaxExprotFile.params.Catt_export_isOver=true;
				  Catt_AjaxExprotFile.params.Catt_export_limitBreak=true;
				  break;
				case "5":break;
				case "6":
					if(Catt_AjaxExprotFile.params.Catt_export_interupt>3){
					  simpleAlert("操作进程被中断！");
					  Catt_AjaxExprotFile.params.Catt_export_isOver=true;
					}else{
						Catt_AjaxExprotFile.params.Catt_export_interupt = Catt_AjaxExprotFile.params.Catt_export_interupt+1;
						if(!Catt_AjaxExprotFile.params.Catt_export_isOver&&!Catt_AjaxExprotFile.interval){
				  			fun.call(this,true);
				 		}
					}
				    break;
			}
			if(Catt_AjaxExprotFile.params.Catt_export_isOver){Catt_AjaxExprotFile.params.Catt_export_overData=data;}
		},
		/**
	      通过iframe下载文件
	    */
	    doLinkFile:function(url,fileName){
	    	var fr=document.getElementById("Catt_importFile_frame");
	    	if(!fr){
	    	   $("body").append("<iframe id='Catt_importFile_frame' src='' style='display:none;'></iframe>")
	    	}
	    	var url = Catt_AjaxExprotFile.basePath+
	    		"/commonModule/importMgr/importTemplate!getTemplate.action?path="+
	    		encodeURIComponent(encodeURIComponent(url))+"&fileName="+encodeURIComponent(encodeURIComponent(fileName));
	    	document.getElementById("Catt_importFile_frame").src = encryptURL(url);
	    	//if(Catt_AjaxExprotFile.backFunction){Catt_AjaxExprotFile.backFunction(1)};	
	    },
		/*
		  筛选导出字段
		  author:zhanweibin 2012-09-20
		  @parma option 此对象可以包含以下通用导入识别的参数，可以任意再加其它任何参数，最终都会被提交到服务端，用被用于自己的查询业务逻辑
		  formId:表单ID
		  optType:操作类型。1或者"direct"为直接导出；2或者"filter"为筛选字段；3或者"other"为弹出选择框菜单
		  cols:columns数组
		  url:导出数据时发起的请求
		  expType:导出操作类型
		  filterCon:过滤条件。过滤不需要导出的字段
		  fileName:导出文件名，可选，默认为file
		*/
		filterAndExportData:function(option){
			if(option.optType == "1" || option.optType == "direct"){//直接导出
				Catt_Export_Direct(option);
			}else if(option.optType == "2" || option.optType == "filter"){//过滤字段导出
				Catt_Export_FilterColums(option);
			}else if(option.expType == "3" || option.optType == "other"){//弹出选择框
				Catt_Export_CreateMenu(option);//创建菜单div
			}else if(option.optType == "4" || option.optType == "offLine"){ //离线导出 for LTE
			    Catt_Export_OffLine(option);
			}
		},
		
		/*
		 在线离线，导出数据
		 author:qiaoqide
		*/
		offLineExportData:function(option, divId){
		    var val = $("input[name='export']:checked").val();
		    option.isOffLine = val; //是否离线导出
		    Catt_Export_Direct(option);
		    Catt_AjaxExprotFile.closeWin(divId);
		},
		
		/*
		 过滤字段操作点击确定后，导出数据
		*/
		getFilterColumns:function(tableId, fileName, formId, url, layoutId){
			var option = {tableId:tableId, fileName:fileName, formId:formId, url:url};
			var heads="",cols1=""
			//拼接列表中的字段
			var rows = $("#"+tableId).datagrid("getSelections");
			if(rows && rows.length > 0){
                if("3"==document.body.getAttribute("exploreType")){
                    for(var i=0;i<tempData.length;i++){
                        var val = tempData[i];
                        if(val.iRequired=='N'){
                            var isFind = false;
                            for(var j=0;j<rows.length;j++){
                                if(val.sFieldName ==rows[j].fieldName || rows[j].fieldName ==(val.sFieldName+'_text')){
                                    isFind = true; break;
                                }
                            }
                            if(!isFind){
                                $.messager.alert('Warning',"缺乏“"+ val.sFieldName+" ”必填字段!\n<br/>\t请选上。",'error'); 
                                return;
                            }
                        }
                    }
                }
				for(var i=0; i<rows.length; i++){
					if(i>0){
						heads+=",";cols1+=",";
					}
					heads+=rows[i].fieldDisplay;
					cols1+=rows[i].fieldName;
				}
				Catt_AjaxExprotFile.Catt_Export_Data(option, heads, cols1,2);
				Catt_AjaxExprotFile.closeWin(layoutId);
			}else{
				simpleAlert({msg:"请勾选需要导出的字段！", opts:{width:"240px"}});
			}
		},
		closeWin:function(layoutId){
			$("#"+layoutId).window("close");
			$("#"+layoutId).remove();
		},
		exportForm:null,
		interval:null,//定时重连对象
		backFunction:null,//回调函数
		basePath:null,//上下文路径
		params:null, //请求参数
		url:null//导出数据时发起的请求
	}
}();
//导出数据
Catt_AjaxExprotFile.Catt_Export_Data = function(option, heads, cols1, eType){
    eType = eType == null ? 3 : eType;
    var exportType = (option.exportType ? option.exportType : 1); //add by qiaoqide
    var params={
       EXP_heads:heads,
       EXP_cols:cols1,
       EXP_fileName:IsEmpty(option.fileName)?option.formId:option.fileName,
       Catt_export_type:exportType, Catt_export_from:eType};
    for(var i in option){
    	if(i != "formId" && i != "url" && i!="ev" && i!="cols"){
    		params[i]=option[i];
    	}
    }
    Catt_AjaxExprotFile.exportData(option.formId, option.url, params, 2000, function(backdata){});
};
    
/**
窗体关闭事件注册打断导出方法
先判断当前是否在导出，如果在导出，才执行打断方法。
waitbar内有导出进度提示内容，且进度提示内容不包含100%，则说明正在进行导出操作；或者当前导出请求一直堵塞中(一般是由于第一次发起的请求，取最大数据量时，SQL查询太慢而导致组塞)
*/
$(window).bind("beforeunload",function(){
    if(Catt_AjaxExprotFile.params && Catt_AjaxExprotFile.params.isOffLine == "1"){return;} //如果是离线导出则关闭窗口不会打断后台导出 add by  qiaoqide
    if(Catt_AjaxExprotFile.params&&Catt_AjaxExprotFile.params.Catt_export_isOver){ if(typeof(WaitBar) != "undefined"){WaitBar.hideProgress();WaitBar.hide(2);}Catt_AjaxExprotFile.breakExport(1);}
    if( (typeof(WaitBar) != "undefined"&&WaitBar.isExporting())||(Catt_AjaxExprotFile.params&&Catt_AjaxExprotFile.params.Catt_export_isBlock)){
    	if(Catt_AjaxExprotFile.params&&!Catt_AjaxExprotFile.params.Catt_export_limitBreak){Catt_AjaxExprotFile.breakExport();}
	}
});


var tempData = null;