/*****************************************************************************************************
������������ش���
*/
window.onresize=setWH;
window.onload = onloadFn;

/** onload�¼����� */
function onloadFn() {
	createToolBar(); //����������
	createList(); //��Ա�б�
	$("#mainCenter").bind("resize",setWH); //layout ���ֿ��С�ı䣬�ڲ����Ҫ����Ӧ
	initLoadData(true); //��ʼ��Ա����
	setWH(); //��ʼ����Ӧ
	commonUtil.setFastKey({eId:"form1", fn:function(){initLoadData(true)}}); //��Enter��ݼ�
}

//����������
function createToolBar(){
	//����������
	var toolJson = {
			renderTo : 'toolbar',
			align : 'left',
			items :
					[
						 { type : 'button',text : '��ѯ',
							handler : function(){ 
								initLoadData(true);
							}
						},'-',
						{type : 'button',text : '�½�',
							  handler : function(){
								 addEditEmployee(0);
							  }
						},'-',
						{type : 'button',text : '�޸�',
							  handler : function(){
								addEditEmployee(1);
							  }
						},'-',
						{type : 'button',text : 'ɾ��',
							  handler : function(){
								deleteEmployee();
							  }
						}
					]
	};
	//������
	var toolbar = new Toolbar(toolJson);
	toolbar.render();
}

//�����б�߶�
function setWH(){	
	var h = $(window).height();
	var w = $(window).width();
	$("#employeeList").datagrid("resize",{height:h-100,width:w});
}


//�����б�
function createList(){
	
	//������
	var cols = [
		[
		{field:'cEmployeeName',title:'��Ա����',width:100,displayTip:true,sortable:true},
		{field:'sSex',title:'��Ա�Ա�',width:100,align:'center'},
		{field:'cDeptName',title:'��������',width:100},
		{field:'cDutyName',title:'����ְ��',width:100},
		{field:'iLengthOfService',title:'��Ա����', align:'right',width:70},
		{field:'dEmployDate',title:'��ְ����',align:'center',width:150},
		{field:'cTel',title:'��ϵ�绰',width:150},
		{field:'remark',title:'��Ա��ע',width:230,displayTip:true}	 
		]
	];
	
	//��ʼ���б�
	$('#employeeList').datagrid({
		idField:"iEmployeeId",	
		frozenColumns:[[{field:'ck',checkbox:true}]],//�����У��Ҵ���Ϊ��ѡ��
		columns:cols,//��ͨ������
		pagination:true, //��ʾ��ҳ��
		nowrap:true
	});
	
	//�Ȼ�ȡ�б��ڵķ�ҳ������  
	var p = $('#employeeList').datagrid('getPager'); 
	
	//���÷�ҳ�ؼ�  
	$(p).pagination({
		 total:0,//�����ܼ�¼����Ĭ��0
		 pageSize: 5,//ÿҳ��ʾ�ļ�¼������������Ĭ��Ϊ10 
		 pageNumber:0,//��ǰҳ��Ĭ��0
		 showPageList:false,
		 showRefresh:false,
		 //����        
		 beforePageText: '��',//ҳ���ı���ǰ��ʾ�ĺ���           
		 afterPageText: 'ҳ    �� {pages} ҳ',           
		 displayMsg: '��ǰ��ʾ�� {from} - {to} ����¼   �� {total} ����¼', 
		 onSelectPage:function(pageNo,limit){//����ϡ���һҳʱ�������¼�
		 	initLoadData();//alert("����ϡ���ҳ�¼���\n��һҳ��"+pageNo+"\nÿҳ����ʾ����:"+limit);
		 }
	 }); 
}

/*****************************************************************************************************
�����ǹ�����ش���
*/

/**��������*/
function initLoadData(flg,pkId) {
	if(document.getElementById("endTime").value != null && document.getElementById("endTime").value != ""){
		if(document.getElementById("creatTime").value > document.getElementById("endTime").value){
			alert("��ʼʱ�䲻�ܴ��ڽ���ʱ��!");
			return false;
		}
	}
	
	var p = $('#employeeList').datagrid('getPager');      
	var options=$(p).pagination("options");
	var pageNo=flg?1:options.pageNumber
	var limit=options.pageSize;
	var url = path + "/demo/demo!getEmployeeList.action";
    // AjaxRequest���������������
	AjaxRequest.doRequest("form1", url, {pageNo:pageNo,limit:limit}, function(backData){
		var jsonData = decode(backData);
		$('#employeeList').datagrid('loadData',jsonData);
		if(pkId)$('#employeeList').datagrid('selectRecord',pkId);//���֮ǰ��ѡ����,�����ѡ��,�����ڱ༭��ѡ�б༭��
		//��ѯ�������ܼ�¼���뵱ǰҳ��(������δ������������Ŀ����װһ��)
		if(jsonData[0] != null){
			$(p).pagination({total:jsonData[0].totalCount,pageNumber:pageNo});
		}else {
			$(p).pagination({total:0,pageNumber:1});//��ҳ
		}
	});
}

/** �������޸���Ա */
function addEditEmployee(flag) {
	//�����޸���Ա��������
	var addOrEditDiv = {
		title:"������Ա",
		id:"addOrEdit",
		url:path+"/demo/dbam/jquery/addOrEdit.jsp",
		width:500,
		height:275,
		zIndex: 9000,
		draggable:true, //�϶�
		resizable:false, //�ı��С
		modal: true, //��̨ҳ��ɱ༭
		closed: false, //�Ƿ�رգ�
		minimizable: false,//��С����ť
		maximizable: false,//��󻯰�ť
		closable: true, //�رհ�ť
		collapsible: true, //������ť 
		refresh:true//��δ��Ƿ�ˢ��ҳ��
	}
	if(flag == 0){
		//�޸ĵ������������ļ��ı���
		addOrEditDiv.url+="?flag=0";
		addOrEditDiv.title = "������Ա";
	}else{
		//��Ҫ�޸ĵ���,getDelOrUpdItems()��js/common.js��
		var obj={listType:"jquery",optType:"update",listId:"employeeList",keyCol:"iEmployeeId"};
		var iEmployeeId=getDelOrUpdItems(obj);
		if(iEmployeeId=="notOnly"){alert("�޸Ĳ�����ֻ�ܹ�ѡ���У�");return;}
		if(!iEmployeeId){alert("��ѡ��Ҫ�޸ĵ���!");return;}
	    
		addOrEditDiv.url += ("?flag=1&iEmployeeId=" + iEmployeeId);
		addOrEditDiv.title = "�޸���Ա";
    }

	showJqueryWindow(addOrEditDiv);//showJqueryWindow����js/common.js��
}

/** ɾ����Ա */
function deleteEmployee() {
	//��Ҫɾ������,getDelOrUpdItems()��js/common.js��
	var obj={listType:"jquery",optType:"delete",listId:"employeeList",keyCol:"iEmployeeId"};
	var iEmployeeId=getDelOrUpdItems(obj);
	if(!iEmployeeId){alert("�빴ѡҪɾ������!");return}
	
    //����ȷ�Ϸ�Ҫɾ��
	var boo = confirm("ȷ��Ҫɾ����");
	if(boo){
		//ִ��ɾ������
	    AjaxRequest.doRequest(null, path+'/demo/demo!deleteEmployee.action', {iEmployeeId: iEmployeeId}, function(flag) {
	    	 if(flag == '0' || flag == 0) {
	    		 alert('����ʧ�ܣ�');
			 }else {
				 alert('������ɣ�');
				 initLoadData();
			 }
	    });
	}
}