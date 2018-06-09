/*****************************************************************************************************
������������ش���
*/
window.onresize=setWH;
window.onload = onloadFn;

/** onload�¼����� */
function onloadFn() {
	createToolBar(); //����������
	createList(); //�ļ��б�
	$("#mainCenter").bind("resize",setWH); //layout ���ֿ��С�ı䣬�ڲ����Ҫ����Ӧ
	initLoadData(true); //��ʼ�ļ�����
	setWH(); //��ʼ����Ӧ
	commonUtil.setFastKey({eId:"form2", fn:function(){initLoadData(true)}}); //��Enter��ݼ�
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
								 addEditFiles(0);
							  }
						},'-',
						{type : 'button',text : '�޸�',
							  handler : function(){
								addEditFiles(1);
							  }
						},'-',
						{type : 'button',text : 'ɾ��',
							  handler : function(){
								deleteFiles();
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
	$("#filesList").datagrid("resize",{height:h-100,width:w});
}


//�����б�
function createList(){
	
	//������
	var cols = [
		[
		{field:'sFileName',title:'�ļ�����',width:100,displayTip:true,sortable:true},
		{field:'sFileType',title:'�ļ�����',width:100,align:'center'},
		{field:'sFilePath',title:'�ļ�·��',width:100},
		{field:'iFileSize',title:'�ļ���С��KB��',width:100},
		{field:'sUploadUser',title:'�ļ��ϴ���', align:'right',width:70},
		{field:'dUploadTime',title:'�ļ��ϴ�ʱ��',align:'center',width:150},
		{field:'sRemark',title:'�ļ����',width:230,displayTip:true}
		]

	];
	
	//��ʼ���б�
	// $('#filesList').datagrid({
	// 	idField:"iId",
	// 	frozenColumns:[[{field:'ck',checkbox:true}]],//�����У��Ҵ���Ϊ��ѡ��
	// 	columns:cols,//��ͨ������
	// 	pagination:true, //��ʾ��ҳ��
	// 	nowrap:true
	// });

    //����ʼ����components�ؼ���
    $('#filesList').datagrid({
        idField:"iId", //��ȷΨһ����
        frozenColumns:[[{field:'ck',checkbox:true}]],//��ѡ����
        columns:cols,//��ͨ��
        showHeader:true,//�Ƿ���ʾ��ͷ��Ĭ��true��ʾ
        nowrap:false, //false���Զ����У�Ĭ��true
        onDblClickRow:function(rowIndex, rowData){ alert("�����¼���"+rowIndex+"--"+rowData.name);}, //��˫���¼�
        onClickRow:function(rowIndex, rowData){ }, //�е����¼�
        width:1024, //��ʼ��
        height:100, //ʵʼ��
        pagination:true //��ʾ��ҳ��
    });
	
	//�Ȼ�ȡ�б��ڵķ�ҳ������  
	// var p = $('#filesList').datagrid('getPager');
	//
	//���÷�ҳ�ؼ�  
	// $(p).pagination({
	// 	 total:0,//�����ܼ�¼����Ĭ��0
	// 	 pageSize: 5,//ÿҳ��ʾ�ļ�¼������������Ĭ��Ϊ10
	// 	 pageNumber:0,//��ǰҳ��Ĭ��0
	// 	 showPageList:false,
	// 	 showRefresh:false,
	// 	 //����
	// 	 beforePageText: '��',//ҳ���ı���ǰ��ʾ�ĺ���
	// 	 afterPageText: 'ҳ    �� {pages} ҳ',
	// 	 displayMsg: '��ǰ��ʾ�� {from} - {to} ����¼   �� {total} ����¼',
	// 	 onSelectPage:function(pageNo,limit){//����ϡ���һҳʱ�������¼�
	// 	 	initLoadData();//alert("����ϡ���ҳ�¼���\n��һҳ��"+pageNo+"\nÿҳ����ʾ����:"+limit);
	// 	 }
	//  });

    //��ҳ����ʼ����components�ؼ���
    var p = $('#filesList').datagrid('getPager');//�Ȼ�ȡ�б����
    $(p).pagination({
        total:0,//�����ܼ�¼����Ĭ��1
        pageSize:5,//ÿҳ��ʾ�ļ�¼������������Ĭ��Ϊ10
        pageNumber:0,//��ǰҳ��Ĭ��1
        position:"center",//left,right,center
        onSelectPage:function(pageNo,limit){//����ϡ���һҳʱ�������¼�
            initLoadData();//alert("����ϡ���ҳ�¼���\n��һҳ��"+pageNo+"\nÿҳ����ʾ����:"+limit);
        }
    });
    //��ҳ�����ûص�1ҳ
    var options = $(p).pagination("options");
    options.total=1000; //��ǰ�ܼ�¼��
    options.pageNumber=1;
    $(p).pagination(options);
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
	
	var p = $('#filesList').datagrid('getPager');
	var options=$(p).pagination("options");
	var pageNo=flg?1:options.pageNumber
	var limit=options.pageSize;
	var url = path + "/demo/files!getFilesList.action";
    // AjaxRequest���������������
	AjaxRequest.doRequest("form2", url, {pageNo:pageNo,limit:limit}, function(backData){
		var jsonData = decode(backData);
		$('#filesList').datagrid('loadData',jsonData);
		if(pkId)$('#filesList').datagrid('selectRecord',pkId);//���֮ǰ��ѡ����,�����ѡ��,�����ڱ༭��ѡ�б༭��
		//��ѯ�������ܼ�¼���뵱ǰҳ��(������δ������������Ŀ����װһ��)
		if(jsonData[0] != null){
			$(p).pagination({total:jsonData[0].totalCount,pageNumber:pageNo});
		}else {
			$(p).pagination({total:0,pageNumber:1});//��ҳ
		}
	});
}

/** �������޸��ļ� */
function addEditFiles(flag) {
	//�����޸��ļ���������
	var addOrEditDiv = {
		title:"�����ļ�",
		id:"addOrEdit",
		url:path+"/demo/dbam/jquery/addOrEditFilesFormControl.jsp",
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
		addOrEditDiv.title = "�����ļ�";
	}else{
		//��Ҫ�޸ĵ���,getDelOrUpdItems()��js/common.js��
		var obj={listType:"jquery",optType:"update",listId:"filesList",keyCol:"iId"};
		var iId=getDelOrUpdItems(obj);
		if(iId=="notOnly"){alert("�޸Ĳ�����ֻ�ܹ�ѡ���У�");return;}
		if(!iId){alert("��ѡ��Ҫ�޸ĵ���!");return;}
	    
		addOrEditDiv.url += ("?flag=1&iId=" + iId);
		addOrEditDiv.title = "�޸��ļ�";
    }

	showJqueryWindow(addOrEditDiv);//showJqueryWindow����js/common.js��
}

/** ɾ���ļ� */
function deleteFiles() {
	//��Ҫɾ������,getDelOrUpdItems()��js/common.js��
	var obj={listType:"jquery",optType:"delete",listId:"filesList",keyCol:"iId"};
	var iId=getDelOrUpdItems(obj);
	if(!iId){alert("�빴ѡҪɾ������!");return}
	
    //����ȷ�Ϸ�Ҫɾ��
	var boo = confirm("ȷ��Ҫɾ����");
	if(boo){
		//ִ��ɾ������
	    AjaxRequest.doRequest(null, path+'/demo/files!deleteFiles.action', {iId: iId}, function(flag) {
	    	 if(flag == '0' || flag == 0) {
	    		 alert('����ʧ�ܣ�');
			 }else {
				 alert('������ɣ�');
				 initLoadData();
			 }
	    });
	}
}