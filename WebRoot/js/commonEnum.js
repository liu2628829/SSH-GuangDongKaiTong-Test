/*
���¼�¼:
2012/3/30 gaotao ��2012/4/1�տ�ʼ��Ϊ��һ���淶ö��������ö��key������ʽΪ:����_�ֶ���

*/
//ö����������
var ENUMCONFIG=[
   //��һ��,��ȷ����ֵ����ʱ������db,sql
   {key:"yn",val:[{id:-1,text:"ȫ��"},{id:0,text:"��"},{id:1,text:"��"}],
  	 db:"",sql:""},
   //�ڶ���,ȥ���ݿ��,��applicationContext.xml���õ�����Դ����ʱ������db,val
   {key:"hibernate",sql:"select iDomainId as \"id\",sDomainName as \"text\" "+
   	"from tbOsDomain",db:"",val:null},
   //������,ȥ���ݿ��,��bonecp.xml���õ�����Դ����ʱ������val
   {key:"bonecp",db:"",sql:"select iDomainId as \"id\",sDomainName"+
   	" as \"text\" from tbOsDomain",val:null},
   
   //ȥ���ݿ�鲿����Ϣ,��applicationContext.xml���õ�����Դ��
   {key:"dept",sql:"select iDeptId as \"id\",cDeptName as \"text\""+
   	" from tbDept"},
   //ȥ���ݿ��ְλ��Ϣ,��applicationContext.xml���õ�����Դ��
   {key:"duty",sql:"select iDutyId as \"id\", cDutyName as \"text\","+
   	"iDeptId as \"iDeptId\" from tbDuty"},
   //ȥ���ݿ��ȫ����Ӫָ����Ϣ,��proxool.xml���õ�����Դ��
   //{key:"itmes",sql:"select iItemID as \"id\",sItemName as \"text\" 
   //from tbItems where iItemNature=1",db:"YYBZ"},
   //ָ������
   {key:"sMpType",val:[{id:1440,text:"1440"},{id:60,text:"60"},
   	{id:30,text:"30"},{id:15,text:"15"},{id:5,text:"5"}],db:"",sql:""},
  	/*ģ���ѯ*/
   {key:"Total_Module",val:[{id:1,text:"����"},{id:2,text:"����"}],db:"",sql:""},
   
   /**������ͨ�ð�ȫ������Ҫ�õ���ö��****************************/
   //ѧ��ö��
   {key:"edu",val:[{id:1,text:"��ר"},{id:2,text:"��ר"},{id:3,text:"����"},
   {id:4,text:"˶ʿ"},{id:5,text:"��ʿ"},{id:6,text:"����"}]},
   //�Ƿ�
   {key:"yesno",val:[{id:1,text:"��"},{id:2,text:"��"}]},   
   //��������
   {key:"deptType",val:[{id:1,text:"���沿��"},{id:2,text:"��ά����"},
  	 {id:3,text:"�Ŷ�"}]},
   //���ż���
   {key:"deptLevel",val:[{id:1,text:"����"},{id:2,text:"����"},{id:3,text:"����"}]},
   //���б��
   {key:"sCityCode",sql:"select sCityCode as \"id\", DataDomain_Name as \"text\""+
   	" from tDataDomain where sCityCode is not null"},
    //��������
   {key:"domainType",val:[{id:1,text:"ʡ"},{id:2,text:"������"},{id:3,text:"����"},
  	 {id:4,text:"����"},{id:5,text:"�Զ���"},{id:20,text:"����"},{id:21,text:"EMS"}]},
   //Ȩ��Ӧ������
   {key:"rightAppFor",val:[{id:1,text:"ȫ��"},{id:2,text:"������ά"},
   	{id:3,text:"����ϵͳ"},{id:4,text:"����ϵͳ"},{id:6,text:"��ͻ�ϵͳ"}]},
   //Ȩ��Ӧ������2
   {key:"rightAppFor2",val:[{id:1,text:"ȫ����"},{id:2,text:"������"}]},
   //Ȩ������
   {key:"rightType",val:[{id:1,text:"�˵�"},{id:2,text:"���ܰ�ť"},{id:3,text:"C/SȨ��"},{id:5,text:"tabҳ"},{id:4,text:"����"}]},      
   //��������
   {key:"domain",sql:"select iDomainId as \"id\",sDomainName as \"text\" from tbOsDomain",val:null},
   //Ȩ��״̬�Ƿ񼤻�
   {key:"rightStatus",val:[{id:1,text:"��"},{id:0,text:"��"}]}, 
   //�����һ�����
   {key:"Retrieve_Password_Question",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'Question' and sEnumTblName = 'Retrieve_Password'"},
    //��־����ģ��
   {key:"logType",val:[{id:0,text:"SSH3ʾ��",children:[{id:11,text:"�����ϴ�������"}]},{id:1,text:"��ȫ����"}],db:"",sql:""},
   {key:"domain",sql:"select iDomainId as \"id\",sDomainName as \"text\" from tbOsDomain"},
   {key:"iOptObjType",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iOptObjType' and sEnumTblName = 'tbOsOperateLog'"},
   {key:"iOptLogType",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iOptLogType' and sEnumTblName = 'tbOsOperateLog'"},
   //IP�ڰ�����
   {key:"iObjType",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iObjType' and sEnumTblName = 'tbOsIpRight'"},
   {key:"iListType",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iListType' and sEnumTblName = 'tbOsIpRight'"},
   
   /*�������*/
   //����״̬
   {key:"iBulletinStatus",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'I_BULLETIN_STATUS' and sEnumTblName = 'T_BULLETIN_TICKET_INFO'"},
   //��������
   {key:"iKind",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'I_KIND' and sEnumTblName = 'T_BULLETIN_TICKET_INFO'"},
   //���漶��
   {key:"iLevel",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'I_LEVEL' and sEnumTblName = 'T_BULLETIN_TICKET_INFO'"},
   //���淶Χ
   {key:"iRange",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'I_RANGE' and sEnumTblName = 'T_BULLETIN_TICKET_INFO'"},
   //������

   {key:"iIsMind",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'I_IS_MIND' and sEnumTblName = 'T_PRO_BULLETIN_AUDIT'"},

   {key:"iIsMind",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'I_IS_MIND' and sEnumTblName = 'T_PRO_BULLETIN_AUDIT'"},

   /*���ٿ���ƽ̨*/
   //����ֶ���������
   {key:"fieldType",val:[{id:1,text:"����"},{id:2,text:"С��"},{id:3,text:"�ַ���"},{id:4,text:"����ʱ��"},{id:5,text:"����"},{id:6,text:"ʱ��"}]},
   //���Ի���У������
   {key:"tbFdFormProperty_iValidType",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iValidType' and sEnumTblName = 'tbFdFormProperty' order by iEnumValue"},
   //���ԱȽ�����
   {key:"tbFdFormProperty_iCompareType",val:[{id:0,text:">"},{id:1,text:"<"},{id:2,text:"="},{id:3,text:">="},{id:4,text:"<="}]},
   //���Բο�����
   {key:"tbFdFormProperty_iRefType",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iRefType' and sEnumTblName = 'tbFdFormProperty'"},
   //����������ֿؼ�
   {key:"tbFdFormProperty_iElement",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iElement' and sEnumTblName = 'tbFdFormProperty'"},
   //����Դ
   {key:"tbImportTemplates_sDataSource",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'sDataSource' and sEnumTblName = 'tbImportTemplates'"},
   //�Ժ��¼�ö�٣��涨ö��key������ʽΪ:����_�ֶ���

    /*��������*/
   //�����嵥-���������
   {key:"tbTechnologyList_sBrowser",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'sBrowser' and sEnumTblName = 'tbTechnologyList'"},
   //�����嵥-�������ݿ�
   {key:"tbTechnologyList_sDatabase",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'sDatabase' and sEnumTblName = 'tbTechnologyList'"},
   //�����嵥-���ݲ���ϵͳ
   {key:"tbTechnologyList_sOS",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'sOS' and sEnumTblName = 'tbTechnologyList'"},
   //ʹ�÷���-ʹ��״̬
   {key:"tbTechnologyUserFeedBack_iState",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iState' and sEnumTblName = 'tbTechnologyUserFeedBack'"},
   //ʹ�÷���-��������
   {key:"tbTechnologyUserFeedBack_iGrade",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iGrade' and sEnumTblName = 'tbTechnologyUserFeedBack'"},
   //����ظ�-���״̬
   {key:"tbTechnologyAnswerQuestion_iState",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iState' and sEnumTblName = 'tbTechnologyAnswerQuestion'"},
   
    /*������*/
   //����ֶ���������
   {key:"DGT_DBTM_FIELD_S_FIELDTYPE",val:[{id:1,text:"С��"},{id:2,text:"����"}, {id:3,text:"�������ַ���(varchar)"},{id:6,text:"�����ַ���(char)"},{id:4,text:"��-��-��"},{id:7,text:"��-��-�� ʱ-��-��"},{id:5,text:"���ı�"}],db:"",sql:""},
   //�ֶ�ȡֵ����
   {key:"DGT_DBTM_FIELD_I_VALUETYPE",val:[{id:1,text:"��ָ��"},{id:2,text:"����"},{id:3,text:"ö��"},{id:4,text:"����"},{id:5,text:"����ö��"}],db:"",sql:""}
   
];

/**********************************************************************************************************/
EnumRequest=function(){
	var timeout=40000;//��ʱʱ��
    function syn(async){ return (async!=false&&async!="false")?true:false;}
	return{
		/*
	     * ��ȡö�� 
	   	 * async trueΪ�첽��falseΪͬ����Ĭ���첽                    
	     */
		getEnum: function(key,fn,async){	
		    var enumVal;
			var param=null;
			for(var i=0;i<ENUMCONFIG.length;i++){
				if(ENUMCONFIG[i].key==key){
					var tempval=ENUMCONFIG[i].val;
					if(typeof(tempval)!="undefined"&&tempval!=null){
						enumVal=tempval;
					}else{
					    param=ENUMCONFIG[i];
					}
					break;
				}
			}
			if(typeof(enumVal)!="undefined"){fn.call(this,enumVal);return;}//�����ȷ��ֵ��ֱ�ӷ���
			if(param==null){simpleAlert({msg:"û�л��"+key+"ö�����ã�",opts:{timeout:5000,winId:"catt_err_msg"}});fn.call(this,[]);return;}	
			
			var path=getFullPath();
			if(typeof(WaitBar) != "undefined")WaitBar.show();
			$.ajax({
				 type: 'POST',
				 url: "commonEnum!getEnumMeaning.action",
				 data: encryptParams(param),
				 dataType: 'json',
				 async: syn(async),
				 timeout:timeout,
				 success: function(json, textStatus) {
				 	if(typeof(WaitBar) != "undefined")WaitBar.hide();	
					fn.call(this, json);
			 	 },
			 	 error: function(XMLHttpRequest, textStatus, errorThrown) {
			 		if(typeof(WaitBar) != "undefined") WaitBar.hide();
					simpleAlert({msg:"����ö������ʧ��!",opts:{timeout:5000,winId:"catt_err_msg"}});
					fn.call(this,[]);
			 	 }
			 });
		},
		/*
	     * ��ȡö��  ��T_ENUMERATE���ȡö��ֵ,����һ������
	     * ö��json��ʽ��{"3":"����","7":"����","2":"�����л�","4":"������","1":"����"}
	     * �򵥶���json��ʽ��[{"value":"3","text":"����"},{"value":"7","text":"����"},{"value":"2","text":"�����л�"},{"value":"4","text":"������"},{"value":"1","text":"����"}]                 
         * async �Ƿ�ͬ����Ĭ���첽
	     */
		getEnumMeaning: function(key,sql,reload,fn,async){	
			var param = {"key":key,"sql":sql,"reload":reload};
			var path=getFullPath();
			if(typeof(WaitBar) != "undefined")WaitBar.show();
			$.ajax({
				 type: 'POST',
				 url: "commonEnum!getEnumMeaning.action",
				 data: encryptParams(param),
				 dataType: 'text',
				 async: syn(async),
				 timeout:timeout,
				 success: function(data, textStatus) {
				 	if(typeof(WaitBar) != "undefined")WaitBar.hide();
				    var valTxt=data||'{}';
					var json=decode(valTxt);	
					fn.call(this, json);
			 	 },
			 	 error: function(XMLHttpRequest, textStatus, errorThrown) {
			 		if(typeof(WaitBar) != "undefined") WaitBar.hide();
					simpleAlert({msg:"����ö������ʧ��!",opts:{timeout:5000,winId:"catt_err_msg"}});
					fn.call(this,[]);
			 	 }
			 });			
		},
		/*
	     * ��������ö�� 
	     * async �Ƿ�ͬ����Ĭ���첽                   
	     */
		cleanEnum:function(key,fn,async){
			 var path=getFullPath();
			 if(typeof(WaitBar) != "undefined")WaitBar.show();
			 $.ajax({
				 type: 'POST',
				 url: "commonEnum!cleanEnum.action",
				 data: encryptParams({key:key}),
				 dataType: 'text',
				 async: syn(async),
				 timeout:timeout,
				 success: function(data, textStatus) {
				 	if(typeof(WaitBar) != "undefined")WaitBar.hide();
					fn.call(this, data);
			 	 },
			 	 error: function(XMLHttpRequest, textStatus, errorThrown) {
			 		if(typeof(WaitBar) != "undefined") WaitBar.hide();
					simpleAlert({msg:"����ö������ʧ��!",opts:{timeout:5000,winId:"catt_err_msg"}});
					fn.call(this,"0");
			 	 }
			 });		
		},	
		/*
	     * ��ʼ�������б�                    --add by �����
	     * key      ö��KEY
	     * selId    �����б�ID
	     * initSelId    ��ʼ��ѡ����������б�ID,��ѡ����� --add by ������
	     * flg �Ƿ��Ĭ���� Ĭ�ϲ���
	     * desc ����ֵ��Ĭ��"��ѡ��"
	     * async �Ƿ�ͬ����Ĭ���첽
	     */
		initSelect:function(key,selId,initSelId,flg,desc,async){
			EnumRequest.getEnum(key,function(backdata){
				sel = document.getElementById(selId);
				if(sel == null || typeof(sel) == "undefined") {
		             simpleAlert({msg:"IDΪ"+selId+"�������б���󲻴��ڣ�",opts:{timeout:5000,winId:"catt_err_msg"}});
		             return;
		    	}
		    	sel.options.length = 0;
		    	if(flg){
		    		var option = new Option();
				     option.text =desc?desc:"��ѡ��";
				     option.value ="";
				     sel.add(option);
		    	}
				for(var i = 0;i<backdata.length;i++){
					 var option = new Option();
				     option.text = backdata[i].text;
				     option.value = backdata[i].id;
				     sel.add(option);
				}
				if(initSelId){
					sel.value = initSelId;				
				}
   			},async);
		},
		/*
	     * ��ʼ�������б�ö�ٱ����ݣ�
	     * key        ö��KEY  ����_�ֶ���   �磺tbOsStaff_iSpec
	     * selId      �����б�ID
	     * initSelId ����ѡ����ʼ��ѡ����������б�ID,��ѡ����� 
	     * fn        ����ѡ�� ��ʼ�������б���ɺ�Ļص�����
	     * sql        �򵥶���Ҫִ�е�sql���
	     * reload     �Ƿ�����ִ��sql��䣬����ȡ���� true�� false��
	     * async �Ƿ�ͬ����Ĭ���첽
	     */
		initSelectEnum:function(key,selId,initSelId,fn,sql,reload,async){
			EnumRequest.getEnumMeaning(key,sql,reload,function(backdata){
				sel = document.getElementById(selId);
				if(sel == null || typeof(sel) == "undefined") {
		             simpleAlert({msg:"IDΪ"+selId+"�������б���󲻴��ڣ�",opts:{timeout:5000,winId:"catt_err_msg"}});
		             return;
		    	}
		    	if(reload){sel.options.length = 0;}
			    for(var i = 0;i<backdata.length;i++){
				   var option = new Option();
			       option.text = backdata[i].text;
			       option.value = backdata[i].value;
			       sel.add(option);
				}
				if(initSelId){
					sel.value = initSelId;				
				}
				if(fn){fn.call(this,backdata);}
   			},async);
		}
	}	
}();