/**
 ͨ�õ���JS
 author:gaotao
 2012/06/25
*/
//var Catt_count=0;//��ʶ��ǰҳ��ڼ��ε㡰��������ť,�����ڲ���ʱʹ�á�
var Catt_AjaxExprotFile=function(){
	//��ȡ��������
	function getMousePoint(ev) {
		// ����������Ӵ��е�λ��
		var point = { x:0, y:0 };
        if(ev!=null){
            // ��������֧�� pageYOffset, ͨ�� pageXOffset �� pageYOffset ��ȡҳ����Ӵ�֮��ľ���
            if(typeof window.pageYOffset != 'undefined') {
                point.x = window.pageXOffset;
                point.y = window.pageYOffset;
            }
            // ��������֧�� compatMode, ����ָ���� DOCTYPE, ͨ�� documentElement ��ȡ����������Ϊҳ����Ӵ���ľ���
            // IE ��, ��ҳ��ָ�� DOCTYPE, compatMode ��ֵ�� CSS1Compat, ���� compatMode ��ֵ�� BackCompat
            else if(typeof document.compatMode != 'undefined' && document.compatMode != 'BackCompat') {
                point.x = document.documentElement.scrollLeft;
                point.y = document.documentElement.scrollTop;
            }
            // ��������֧�� document.body, ����ͨ�� document.body ����ȡ�����߶�
            else if(typeof document.body != 'undefined') {
                point.x = document.body.scrollLeft;
                point.y = document.body.scrollTop;
            }
            // ����������Ӵ��е�λ��
            point.x += ev.clientX;
            point.y += ev.clientY;
        }
		// ����������Ӵ��е�λ��
		return point;
	}

	//ʹ���б��ֶ�ֱ�ӵ�������
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
				if(boo){idFieldCname = fd.title;}//���������ֶ���
				if(fd.hidden!="true" ||(option.keepPkFiled==1 && boo) || fd.isExport=="true"){//�ų�������������������ڵ��룬Ҫ�������ֶηų���
					for(var j=0; j<filterCons.length; j++){//�ų�Ҫ���˵��ֶ�
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
				if(boo){idFieldCname = cols[0][i].title;}//���������ֶ���
				if(fd.hidden!="true" ||(option.keepPkFiled==1 && boo) || fd.isExport=="true"){//�ų�������������������ڵ��룬Ҫ�������ֶηų���
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
		       msg:"ע�⣺\n1.���������ļ���һ����Ҫ�ԡ�"+idFieldCname+"���ֶν����޸Ļ���գ������Ӱ������������ݵ�׼ȷ�ԡ�\n2.������Լ���ע�ֶν����޸ģ�����ע���ֶβ�Ҫ�޸ģ���������ɾ���κ��ֶ��У������Ӱ�����ݵ��������롣",
		       ok:'֪����',
		       fn:function(){Catt_AjaxExprotFile.Catt_Export_Data(option, heads, cols1,3);}
		    });
		}else{
			Catt_AjaxExprotFile.Catt_Export_Data(option, heads, cols1,3);
		}
	    
	}
	
	
	//���ɵ����˵�
	function Catt_Export_CreateMenu(option){
		var div = document.createElement("div");
		with(div){id="optMenu_"+option.formId;}
		with(div.style){width="140px";overflow:"hidden"}
		
		var items = [
			{type:"button",text:"�б�ȫ���ֶ�",bodyStyle:"chargeback",useable:"T",
				handler:function(){option.keepPkFiled=0;Catt_Export_Direct(option);}
			}/*, 
			{type:"button",text:"����ɸѡ�ֶ�",bodyStyle:"gongdang",useable:"T",
				handler:function(){Catt_Export_FilterColums(option);}
			}*/,
            {type:"button",text:"���������ڵ���",bodyStyle:"chargeback",useable:"T",
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
        //�����˵���λ����
        $("#optMenu_"+option.formId).menu({});
        $("#optMenu_"+option.formId).menu("show", { left:point.x,  top:point.y  });
        
	}
	
        //�������������ֶ�div���
    var fastDevelopPageDiv = {
        id: "fastDevelopPageDiv",
        width: 750,
        height: 500,
        zIndex: 9000,
        draggable: true, //�϶�
        resizable: true, //�ı��С
        modal: true, //��̨ҳ��ɱ༭
        closed: false, //�Ƿ�ر�
        minimizable: false,//��С����ť
        maximizable: true,//��󻯰�ť
        closable: true, //�رհ�ť
        collapsible: true //������ť 
    };
    
    //�������Ӧ�����ۺ����ܰ�ͨ�ñ�
	function Catt_Export_InOut_Colums(option){
	    //update by gt 2013/05/29
	    //�ӱ������ֶζ������õ����б�����ֶ�
	    var formId = option.formId.replace("form_","");
	    AjaxRequest.doRequest(null, path+"/commonModule/fastDevelop/fastDevelopApp!getFastDevelopFormAttrList.action", 
	   			{iFormId: formId}, function(backData){
			var jsonData = decode(backData);	    
	     	//�뵱ǰ������ֶκϲ�(ֻ�ϲ�������ֶ�)
	     	if(jsonData && jsonData.length>0){
	     	   var cols = option.cols[0];
	     	   for(var i=0; i<jsonData.length; i++){
	     	   	 var f1 = jsonData[i];
	     	   	 if(f1.iRequired!="1"){continue;} //�Ǳ�����ֶβ�����
	     	   	 var boo = true;
	     	   	 for(var j=0; j<cols.length; j++){
	     	   	 	if(cols[j].field == f1["sFieldName"]){boo=false;break;} //�������Ѿ��д��ֶΣ�Ҳ���ش�����
	     	   	 }
	     	   	 if(boo){
	     	   	 	var json = {field:f1["sFieldName"],title:f1["sPropertyName"]};
	     	   	 	cols.push(json);
	     	   	 }
	     	   }
	     	}
	     	
	    	option.Catt_export_from = 1; //��һ����ʶ��������ʶ���������ڵ��� buildSqlData.java��ƴ��ѯSQLʱ���õ�
	   		option.keepPkFiled = 1; //�����ֶ�һ������
	    	Catt_Export_Direct(option); //Ȼ������ͨ����ִ��
	    });
	   
        /*fastDevelopPageDiv.title = "����ģ�嶨��";
        var sTableName =option.url.substring( option.url.indexOf('Fd_sTableName=')+14);
		fastDevelopPageDiv.url = path+"/admin/commonModule/importMgr/templatesAddEditByFd.jsp?Fd_iFormId="+
            option.formId.substring(option.formId.indexOf('_')+1)+"&sTableName="+sTableName+"&sTemplateName="+option.fileName;
        showJqueryWindow(fastDevelopPageDiv);
        Catt_AjaxExprotFile.tempOption = option;*/
        }
        
	//����ɸѡ�ֶ�div���
	function Catt_Export_FilterColums(option){
		//����div��table
		if(IsEmpty(option.layoutId)){
			var div = Catt_Export_CreateDiv(option);
			document.body.appendChild(div);
		}else{//������ɾ��
			var layoutObj = $("#"+option.layoutId);
			if(layoutObj){
				layoutObj.remove();
				var div = Catt_Export_CreateDiv(option);
				document.body.appendChild(div);
			}
		}
		
		//���ӹ�����
		Catt_Export_CreateToolbar(option);
		//����datagrid
		Catt_Export_CreateDatagrid(option);
		//��������
		Catt_Export_LoadData(option);
		//����Ӧ���
		
		$("#"+option.layoutId).window({
			title:"ɸѡ�����ֶ�", 
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
				//�޸��ڲ�panel�Ŀ��
				$("#"+option.panelId).panel("resize", {width:width-10, height:height-75});
				commonUtil.setWH({eId:option.tableId,eType:"jquery",rId:"Export_PanelDiv_"+option.formId,width:-2,height:-1});
			}
		});
	}
	
	//����ɸѡ�ֶ�div
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
	    southHtml += "<div class='ok'>����</div>";
	    southHtml += "</span></a></li>";
	    southHtml += "<li onClick='Catt_AjaxExprotFile.closeWin(\""+option.layoutId+"\")'><a href='#'><span>";
	    southHtml += "<div class='no'>ȡ��</div>";
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
	
	/*�������������ߵ���div
	* author:qiaoqide
	*/
	function Catt_Export_OffLine(option){
		//����div��table
		if(IsEmpty(option.layoutId)){
			var div = CreateOffLineExport_Div(option);
			document.body.appendChild(div);
		}else{//������ɾ��
			var layoutObj = $("#"+option.layoutId);
			if(layoutObj){
				layoutObj.remove();
				var div = CreateOffLineExport_Div(option);
				document.body.appendChild(div);
			}
		}
		initExportButton(option);
		
		$("#"+option.layoutId).window({
			title:"��������", 
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
	
	//��ʼ����ť
	function initExportButton(option){
	   var buttons=[
				{
					btnId:'btnOfflineExport', btnPicName:'ok.gif', btnName:'����',
					btnFun: function(){
					   Catt_AjaxExprotFile.offLineExportData(option, option.layoutId);
					   return false;
					}
				},
				{
					btnId:'btnCancel', btnPicName:'no.gif', btnName:'ȡ��',
					btnFun: function(){Catt_AjaxExprotFile.closeWin(option.layoutId);}
				}
				];
		var btnJson = {eId:"btnDiv",btnAlign : "center", btnOptions : buttons};
		commonUtil.initButtonDiv(btnJson);
	}
	
	/*�������ߵ���div
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
		centerHtml += "<label for='online'>������������</label>";
		centerHtml += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		centerHtml += "<input type='radio' id='offline' name='export' value='1'>";
		centerHtml += "<label for='offline'>������������(����)</label>";
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
	
	
	//����ɸѡ�ֶ�table
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
		  		title: "����",
	     		iconCls:'icon-up',   
	     		handler:function(){
	     			//�����ݣ����Ҳ��ǵ�һ��
	     			var rows = $("#"+option.tableId).datagrid("getRows");
	     			if(rows && rows.length > 0){
	     				var selectRow = $("#"+option.tableId).datagrid("getSelected");
	     				//for(var i in selectRow){alert(i+"=="+selectRow[i]);	}
	     				var rowIndex = $("#"+option.tableId).datagrid("getRowIndex", selectRow);
	     				if(rowIndex != 0){
	     					var isChecked = $("#"+option.tableId).datagrid("isChecked", {index:rowIndex});
	     					$("#"+option.tableId).datagrid("deleteRow", rowIndex);//��ɾ��Ҫ�ƶ�����
	     					rowIndex -= 1;
	     					$("#"+option.tableId).datagrid("insertRow", {index:rowIndex, row:selectRow});//�ٲ���ԭ������
	     					$("#"+option.tableId).datagrid("selectRow", rowIndex);//����ѡ���ƶ�����
	     					$("#"+option.tableId).datagrid("checkRow", {index:rowIndex, check:isChecked});
	     				}
	     			}	
	     		}   
	   		},{   
	   			title: "����",
	    		iconCls:'icon-down',
	     		handler:function(){
	     			//�����ݣ����Ҳ������һ��
	     			var rows = $("#"+option.tableId).datagrid("getRows");
	     			if(rows && rows.length > 0){
	     				var selectRow = $("#"+option.tableId).datagrid("getSelected");
	     				var rowIndex = $("#"+option.tableId).datagrid("getRowIndex", selectRow);
	     				if(rowIndex != rows.length - 1){
	     					var isChecked = $("#"+option.tableId).datagrid("isChecked", {index:rowIndex});
	     					$("#"+option.tableId).datagrid("deleteRow", rowIndex);//��ɾ��Ҫ�ƶ�����
	     					rowIndex += 1;
	     					$("#"+option.tableId).datagrid("insertRow", {index:rowIndex, row:selectRow});//�ٲ���ԭ������
	     					$("#"+option.tableId).datagrid("selectRow", rowIndex);//����ѡ���ƶ�����
	     					$("#"+option.tableId).datagrid("checkRow", {index:rowIndex, check:isChecked});
	     				}
	     			}
	     		}   
	   		}]   
		});
	}
	
	//����ɸѡ�ֶ�datagrid
	function Catt_Export_CreateDatagrid(option){
		var chkCol = [{field:"ck",checkbox:true}];
		var cols = [[
			{field:"fieldName", title:"field�ֶ�",width:50, hidden:"true"},
			{field:"fieldDisplay", title:"�ֶ�����",width:50}
		]];
		$("#"+option.tableId).datagrid({
		    idField:"fieldName",
			columns:cols,
			frozenColumns:[chkCol],//�����У��Ҵ���Ϊ��ѡ��
			nowrap:true,
			fitColumns:true,
			rownumbers:false,
			width:$("#"+option.panelId).panel("options").width-2,
			height:$("#"+option.panelId).panel("options").height-28
		});
	}
	
	//����datagrid����
	function Catt_Export_LoadData(option){
		
		//���ص�����Ϊcolumns�е�field��title����
		var cols = option.cols;
		var data = [];
		if(!IsEmpty(option.filterCon)){
			var filterCons = option.filterCon.split(",");
			for(var i=0; i<cols[0].length; i++){
				var isHas = false;
				if(!IsEmpty(cols[0][i].hidden))
					cols[0][i].hidden = (cols[0][i].hidden+"").toLowerCase();
				if(cols[0][i].hidden!="true" || cols[0][i].field == option.idField){//�ų������򣬷ſ������ֶ�
					for(var j=0; j<filterCons.length; j++){//�ų�Ҫ���˵��ֶ�
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
		//���ù�ѡ
		var rows = $("#"+option.tableId).datagrid("getRows");
		$("#"+option.panelId).find(" .datagrid-header-check").find(":checkbox").attr("checked", "true");//��ѡͷ����ѡ��
		for(var i=0; i<rows.length; i++){
			$("#"+option.tableId).datagrid("checkRow", {index:i,check:true});
		}
	}
	
	return {
	    /**
	     ���ݵ���
	    el����id
	    url:��������ʱ���������
	    params:�������
	    intervalTime:��ʱ���Զ������ʱ��(����)
	    fn:������Ϻ�Ļص�����(������)
	    */
		exportData: function(el,url,params,intervalTime,fn){//�������������������첽����
			 Catt_AjaxExprotFile.backFunction=fn;//ע��ûص�����
			 var t=1000;//������ֳ�ʱ�����Ĭ�ϸ�1���Զ��������󣬻�ȡ��Ҫ���ļ�
			 if(intervalTime&&intervalTime>0){t=intervalTime;}//�д���ʱ���Դ���ʱ��Ϊ׼
			 Catt_AjaxExprotFile.setBasePath();//��������·��
			 Catt_AjaxExprotFile.el=el;
			 Catt_AjaxExprotFile.url=url;
			 if(Catt_AjaxExprotFile.interval){
			    window.clearInterval(Catt_AjaxExprotFile.interval);
			    Catt_AjaxExprotFile.interval=null;
			  }
			 
			 //�ռ����
			 var param = new Object();
			 var obj = document.getElementById(el);
			 if(el == null || el == '' || obj == null) {//�ޱ�,��params��������Ϊ׼
			 	param = params;
			 }else {//�б������õ����������Ȼ�����ñ�����
			    if(params&&typeof(params)=="object"){param = params;}
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
			 //param.Catt_count=++Catt_count;
			 param.EXP_fileName=encodeURIComponent(param.EXP_fileName); //���������������⣬����ת�룬����˻����
			 Catt_AjaxExprotFile.params=param;
			 Catt_AjaxExprotFile.params.Catt_export_random=parseInt(Math.random()*1000);//ҳ��Ψһ��ʶ
			 Catt_AjaxExprotFile.params.Catt_export_confirm_flag="first";//��һ�η�������
			 Catt_AjaxExprotFile.params.Catt_export_isOver=false;
			 Catt_AjaxExprotFile.params.Catt_export_limitBreak=false;
			 Catt_AjaxExprotFile.params.Catt_export_overData=null;
			 Catt_AjaxExprotFile.params.Catt_export_interupt=0;
			 Catt_AjaxExprotFile.params.Catt_export_finish_flag="0";
			 Catt_AjaxExprotFile.params.Catt_export_isBlock=true;//������
			 Catt_AjaxExprotFile.params.Catt_export_flag=1;
			 
			 /*�����õ�������form����
			 if(!Catt_AjaxExprotFile.exportForm){
			 	var exportForm=document.createElement("form");
			 	exportForm.method="post";
			 	exportForm.style.display="none";
			 	document.body.appendChild(exportForm);
			 	Catt_AjaxExprotFile.exportForm=exportForm;
			 }*/
			 
			 //����������Ĳ���
			 //var msg1=Catt_AjaxExprotFile.checkRequire();
			 //if(msg1.length>0){var str=msg1.join("\n");alert(str);simpleAlert(msg1.join("\n"));return false;}	
			 if(typeof(WaitBar) != "undefined") WaitBar.show(2);//�ȴ���
			 //�ύ����
			 $.ajax({
				 type: 'POST',
				 url: encryptURL(url),
				 data: encryptParams(param),
				 async: true,//�첽��falseΪ����
				 timeout:20000,//20���ʱ
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
			 	 error: function(XMLHttpRequest, textStatus) {//����ʱ����������
			 	 	Catt_AjaxExprotFile.whenBlock(el,url,intervalTime,fn);
			 	 }
			 });
			 
	    },
	    /**
	      ���������������
	    */
	    checkRequire:function(){
	    	var EXP_fileName=Catt_AjaxExprotFile.params.EXP_fileName;
	    	var EXP_heads=Catt_AjaxExprotFile.params.EXP_heads;
	    	var EXP_cols=Catt_AjaxExprotFile.params.EXP_cols;
	        var msg=[];
	        if(!EXP_fileName||EXP_fileName.replace(/ /g,"")=="")
	        	msg.push("�ļ����Ʋ���Ϊ��");
	        if(!EXP_heads||EXP_heads.replace(/ /g,"")=="")
	        	msg.push("��ͷ���Ʋ���Ϊ��");
	        if(!EXP_cols||EXP_cols.replace(/ /g,"")=="")
	        	msg.push("�ֶ����Ʋ���Ϊ��");
	        if(msg.length==0&&(EXP_heads.split(",").length!=EXP_cols.split(",").length))
	        	msg.push("��ͷ�������ֶ�����������һ��");
	        return msg;
	    }, 
	    /**
	      ��ʱ����
	    */
	    setInterval:function(fn,t){
	    	window.clearInterval(Catt_AjaxExprotFile.interval);
	    	Catt_AjaxExprotFile.interval=window.setInterval(fn,t);
	    },
		/**
		��ʱ�����Զ�����
		*/
		reExport: function(el,url,intervalTime,fn){//��ʱ������
			 if(Catt_AjaxExprotFile.params.Catt_export_break_flag=="1"){ return; }
			 if(Catt_AjaxExprotFile.params.Catt_export_confirm_flag!="confirm"&&Catt_AjaxExprotFile.params.Catt_export_confirm_flag!="cancle")
			 Catt_AjaxExprotFile.params.Catt_export_confirm_flag="nature";//�ǵ�һ�η�������
		 	 if(typeof(WaitBar) != "undefined") WaitBar.show(2);//�ȴ���
			 //�ύ����
			 $.ajax({
				 type: 'POST',
				 url: encryptURL(url),
				 data: encryptParams(Catt_AjaxExprotFile.params),
				 async: true,//�첽��falseΪ����
				 timeout:20000,//20���ʱ
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
		�жϵ���
		*/
		breakExport: function(finish, url){
			 if(Catt_AjaxExprotFile.params.Catt_export_confirm_flag!="confirm"){
			 	Catt_AjaxExprotFile.params.Catt_export_confirm_flag="nature";//�ǵ�һ�η�������
			 }
		 	 window.clearInterval(Catt_AjaxExprotFile.interval);//�����ʱ����
		 	 //alert(1);
		 	 if(finish==1){
			 	 Catt_AjaxExprotFile.params.Catt_export_finish_flag="1";//��ʶ���ؿ�ʼ��ɾ���߳�
		 	 }else{
			 	 if(typeof(WaitBar) != "undefined"){ WaitBar.setMsg("<font style='font-weight: bold;color:red'>&nbsp;&nbsp;&nbsp;&nbsp;ȡ����....</font>");}
		 	 }
		 	 Catt_AjaxExprotFile.params.Catt_export_break_flag="1";//��ʶ���
			 //�ύ����
			 $.ajax({
				 type: 'POST',
				 url: encryptURL(Catt_AjaxExprotFile.url),
				 data: encryptParams(Catt_AjaxExprotFile.params),
				 async: true,//�첽��falseΪ����
				 timeout:20000,//20���ʱ
				 dataType: 'text',
				 success: function(data, textStatus) {
				  			 var msg=null;
		   					 eval("msg="+data);
		   					 if(msg.iState=="6"){//�ɹ�
		   					 	setTimeout(function(){Catt_AjaxExprotFile.setWaitBar();},1000);
		   					 	Catt_AjaxExprotFile.params.Catt_export_isBlock=false;
		 					 }else if(msg.iState=="5"){
		 					 	//�߳�ɾ���ɹ�
		 					 	Catt_AjaxExprotFile.params.Catt_export_finish_flag="0";
		 					 }else{//����������
		 					 	Catt_AjaxExprotFile.breakExport();
		 					 }
		 				   },
			 	 error: function(XMLHttpRequest, textStatus) {Catt_AjaxExprotFile.breakExport();}
			 });
		},
		/**
		��������·��
		*/
		setBasePath:function(){
			var pathname=getPathName();
			var basepath=getFullPath();
			Catt_AjaxExprotFile.basePath=basepath;
			return basepath;
		},
		/*
		����������ʱ(����������٣�һ�������Ϊ�ײ�SQL��Ľ��������ʱ��SQL��Ӧ�ٶȳ��������µĳ�������)
		*/
		whenBlock:function(el,url,intervalTime,fn){
		    var m="���ݼ��ػ�����ϵͳ������<br>�����ù�������������������<br>������ã�<a href='javascript:void(0)' onclick='Catt_AjaxExprotFile.breakExport();'><font style='font-weight: bold;color:red'>���ȵ������ȡ�����ε���</font></a><br>";
			if(typeof(WaitBar) != "undefined"){WaitBar.setMsg(m);}
			Catt_AjaxExprotFile.params.Catt_export_isBlock=true;
			window.setTimeout(function(){Catt_AjaxExprotFile.reExport(el,url,intervalTime,fn)},2000);
		},
		/**
		����Waitbar
		*/
		setWaitBar:function(){
			if(typeof(WaitBar) != "undefined") {
			    WaitBar.hideProgress();
			    WaitBar.setMsg("");
			    WaitBar.hide(2);
	 		}
		},
		/*
		������ʾ��Ϣ
			-1,������󵼳��߳���
			0,����������
			1,�����ɹ�
			2,������
			3,������������ʾֵ,��ʾ�û�
			4,�����Ƶ�����ʽ���ܼ�¼��Խ��
			5,�û�����ȷ�ϲ�����
			6,�ɹ���ϵ����߳�
		*/
		createInfo:function(data,fun){
			if(Catt_AjaxExprotFile.params.Catt_export_break_flag=="1"){return;}//����Ѿ�"ȡ��"��������߾Ͳ���Ҫ�����κ���ʾ
		    
		    var msg=null;
		    /*if(typeof(IS_CAS)!="undefined"&&IS_CAS!=null&&IS_CAS=="1"&&data==""){
				window.location.href = path+"/login.jsp";
				return;
		    }*/
		    eval("msg="+data);
		    if(msg.iState=="1"&&msg.url==""){//ż��������������,��δ�ҵ�ԭ��
		    	window.clearInterval(Catt_AjaxExprotFile.interval);//�����ʱ����
		    	Catt_AjaxExprotFile.params.Catt_export_isOver=true;
				Catt_AjaxExprotFile.setWaitBar();
		 		simpleAlert("����ʧ�ܣ������ԣ�");
		    	return;
		    }
		    if(msg.iState!="0"){
		    	window.clearInterval(Catt_AjaxExprotFile.interval);//�����ʱ����
		    	Catt_AjaxExprotFile.interval=null;
				Catt_AjaxExprotFile.setWaitBar();
		    }
			switch(msg.iState){
				case "-1":
				  simpleAlert("ϵͳ�������ͬʱ����"+msg.MAX_EXPORTER+"���������̣�Ŀǰ"+msg.MAX_EXPORTER+"�����̣�����ռ���У����Ժ������");
				  Catt_AjaxExprotFile.params.Catt_export_isOver=true;
				  break;
				case "0":
				  var m="��ǰ�����ܼ�¼��Ϊ<font style='font-weight: bold;color:red'>"+msg.totalCount+"</font>��<br>";
				  //��ǰ�����ܼ�¼��ΪX�У��ļ����ɽ��� 100%���ļ�ѹ������У����Ժ�!
				  var progress=msg.currentCount/msg.totalCount*100;
				  progress=Math.round(progress);
				  if(progress<100){m+="�ļ����ɽ���<font style='font-weight: bold;color:red'>"+progress+"%</font><br><a href='javascript:void(0)' onclick='Catt_AjaxExprotFile.breakExport();'><font style='font-weight: bold;color:red'>��������ȡ������</font></a><br>";
				  }else{m+="�ļ����ɽ���<font style='font-weight: bold;color:green'>100%</font><br>�ļ�ѹ������У����Ե�!";}
				  if(typeof(WaitBar) != "undefined"){WaitBar.setMsg(m);}
				  if(!Catt_AjaxExprotFile.interval){
				  	fun.call(this,true);
				  }
				  break;
				case "1":
				  //��ǰ�����ܼ�¼��ΪX�У��ļ����ɽ��� 100%���ļ�ѹ�������ϣ�������� �����ļ�!
				  /*var m="��ǰ�����ܼ�¼��Ϊ<font style='font-weight: bold;color:red'>"+msg.totalCount+"</font>��<br>�ļ����ɽ��� <font style='font-weight: bold;color:green'>100%</font><br>�ļ�ѹ��������<br>";
				  m+="��<a href='"+msg.url+"' onclick='WaitBar.hideProgress();WaitBar.hide(2);Catt_AjaxExprotFile.breakExport(1);'><font style='font-weight: bold;color:red'>������������ļ�</font></a>";//onclick�в��ܼӣ�WaitBar.setMsg(\"\")���������Ӿ��ȱ������
			      WaitBar.setMsg(m);*/
			      /*Catt_AjaxExprotFile.params.Catt_export_isOver=true;
			      Catt_AjaxExprotFile.exportForm.action=Catt_AjaxExprotFile.basePath+"/admin/common/commonExport.jsp?fileName=��Ա&filePath="+msg.url;
			      Catt_AjaxExprotFile.exportForm.submit();*/
			      if(typeof(WaitBar) != "undefined"){
			      	WaitBar.hideProgress();WaitBar.hide(2);
			      }
			      Catt_AjaxExprotFile.breakExport(1, msg.url);
			      if(Catt_AjaxExprotFile.params.isOffLine != "1"){ //�������� add by qiaoqide 2013-10-29
			      	if($.browser.msie && ($.browser.version*1)<9){//�Ͱ汾IE:IE8������
			          simpleAlert({msg:'�����ļ�������ϣ���"ȷ��"������!',type:"confirm", fn:function(){Catt_AjaxExprotFile.doLinkFile(msg.url,'');}});
			        }else{ //����ֱ������
			          Catt_AjaxExprotFile.doLinkFile(msg.url,'');
			        }
			      }
				  break;
				case "2":
				  simpleAlert("û�з��ϵ������������ݿɹ�������");
				  Catt_AjaxExprotFile.params.Catt_export_isOver=true;
				  break;
				case "3":
				  simpleAlert({msg:"��ǰ�������������"+msg.EXP_CONFIRM_CONUT+"�У��Ƿ�ȷ��������", type:2, 
				    fn:function(){Catt_AjaxExprotFile.params.Catt_export_confirm_flag="confirm";fun.call(this,true);},
				    fnCancel:function(){Catt_AjaxExprotFile.params.Catt_export_confirm_flag="cancle";Catt_AjaxExprotFile.params.Catt_export_isOver=true;fun();}
				  });
				  break;
				case "4":
				  simpleAlert("�˹���������󵼳�"+msg.EXP_BINARY_TYPE_LIMIT+"������<br>��ǰ�������Ѵﵽ"+msg.totalCount+"��<br>����"+(msg.totalCount-msg.EXP_BINARY_TYPE_LIMIT)+"����");
				  Catt_AjaxExprotFile.params.Catt_export_isOver=true;
				  Catt_AjaxExprotFile.params.Catt_export_limitBreak=true;
				  break;
				case "5":break;
				case "6":
					if(Catt_AjaxExprotFile.params.Catt_export_interupt>3){
					  simpleAlert("�������̱��жϣ�");
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
	      ͨ��iframe�����ļ�
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
		  ɸѡ�����ֶ�
		  author:zhanweibin 2012-09-20
		  @parma option �˶�����԰�������ͨ�õ���ʶ��Ĳ��������������ټ������κβ��������ն��ᱻ�ύ������ˣ��ñ������Լ��Ĳ�ѯҵ���߼�
		  formId:��ID
		  optType:�������͡�1����"direct"Ϊֱ�ӵ�����2����"filter"Ϊɸѡ�ֶΣ�3����"other"Ϊ����ѡ���˵�
		  cols:columns����
		  url:��������ʱ���������
		  expType:������������
		  filterCon:�������������˲���Ҫ�������ֶ�
		  fileName:�����ļ�������ѡ��Ĭ��Ϊfile
		*/
		filterAndExportData:function(option){
			if(option.optType == "1" || option.optType == "direct"){//ֱ�ӵ���
				Catt_Export_Direct(option);
			}else if(option.optType == "2" || option.optType == "filter"){//�����ֶε���
				Catt_Export_FilterColums(option);
			}else if(option.expType == "3" || option.optType == "other"){//����ѡ���
				Catt_Export_CreateMenu(option);//�����˵�div
			}else if(option.optType == "4" || option.optType == "offLine"){ //���ߵ��� for LTE
			    Catt_Export_OffLine(option);
			}
		},
		
		/*
		 �������ߣ���������
		 author:qiaoqide
		*/
		offLineExportData:function(option, divId){
		    var val = $("input[name='export']:checked").val();
		    option.isOffLine = val; //�Ƿ����ߵ���
		    Catt_Export_Direct(option);
		    Catt_AjaxExprotFile.closeWin(divId);
		},
		
		/*
		 �����ֶβ������ȷ���󣬵�������
		*/
		getFilterColumns:function(tableId, fileName, formId, url, layoutId){
			var option = {tableId:tableId, fileName:fileName, formId:formId, url:url};
			var heads="",cols1=""
			//ƴ���б��е��ֶ�
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
                                $.messager.alert('Warning',"ȱ����"+ val.sFieldName+" �������ֶ�!\n<br/>\t��ѡ�ϡ�",'error'); 
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
				simpleAlert({msg:"�빴ѡ��Ҫ�������ֶΣ�", opts:{width:"240px"}});
			}
		},
		closeWin:function(layoutId){
			$("#"+layoutId).window("close");
			$("#"+layoutId).remove();
		},
		exportForm:null,
		interval:null,//��ʱ��������
		backFunction:null,//�ص�����
		basePath:null,//������·��
		params:null, //�������
		url:null//��������ʱ���������
	}
}();
//��������
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
����ر��¼�ע���ϵ�������
���жϵ�ǰ�Ƿ��ڵ���������ڵ�������ִ�д�Ϸ�����
waitbar���е���������ʾ���ݣ��ҽ�����ʾ���ݲ�����100%����˵�����ڽ��е������������ߵ�ǰ��������һֱ������(һ�������ڵ�һ�η��������ȡ���������ʱ��SQL��ѯ̫������������)
*/
$(window).bind("beforeunload",function(){
    if(Catt_AjaxExprotFile.params && Catt_AjaxExprotFile.params.isOffLine == "1"){return;} //��������ߵ�����رմ��ڲ����Ϻ�̨���� add by  qiaoqide
    if(Catt_AjaxExprotFile.params&&Catt_AjaxExprotFile.params.Catt_export_isOver){ if(typeof(WaitBar) != "undefined"){WaitBar.hideProgress();WaitBar.hide(2);}Catt_AjaxExprotFile.breakExport(1);}
    if( (typeof(WaitBar) != "undefined"&&WaitBar.isExporting())||(Catt_AjaxExprotFile.params&&Catt_AjaxExprotFile.params.Catt_export_isBlock)){
    	if(Catt_AjaxExprotFile.params&&!Catt_AjaxExprotFile.params.Catt_export_limitBreak){Catt_AjaxExprotFile.breakExport();}
	}
});


var tempData = null;