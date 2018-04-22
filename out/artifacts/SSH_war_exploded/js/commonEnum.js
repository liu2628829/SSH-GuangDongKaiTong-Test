/*
更新记录:
2012/3/30 gaotao 从2012/4/1日开始，为进一步规范枚举命名，枚举key命名格式为:表名_字段名

*/
//枚举配置数组
var ENUMCONFIG=[
   //第一种,有确定的值。此时不配置db,sql
   {key:"yn",val:[{id:-1,text:"全部"},{id:0,text:"否"},{id:1,text:"是"}],
  	 db:"",sql:""},
   //第二种,去数据库查,用applicationContext.xml配置的数据源。此时不配置db,val
   {key:"hibernate",sql:"select iDomainId as \"id\",sDomainName as \"text\" "+
   	"from tbOsDomain",db:"",val:null},
   //第三种,去数据库查,用bonecp.xml配置的数据源。此时不配置val
   {key:"bonecp",db:"",sql:"select iDomainId as \"id\",sDomainName"+
   	" as \"text\" from tbOsDomain",val:null},
   
   //去数据库查部门信息,用applicationContext.xml配置的数据源。
   {key:"dept",sql:"select iDeptId as \"id\",cDeptName as \"text\""+
   	" from tbDept"},
   //去数据库查职位信息,用applicationContext.xml配置的数据源。
   {key:"duty",sql:"select iDutyId as \"id\", cDutyName as \"text\","+
   	"iDeptId as \"iDeptId\" from tbDuty"},
   //去数据库查全网运营指标信息,用proxool.xml配置的数据源。
   //{key:"itmes",sql:"select iItemID as \"id\",sItemName as \"text\" 
   //from tbItems where iItemNature=1",db:"YYBZ"},
   //指标周期
   {key:"sMpType",val:[{id:1440,text:"1440"},{id:60,text:"60"},
   	{id:30,text:"30"},{id:15,text:"15"},{id:5,text:"5"}],db:"",sql:""},
  	/*模块查询*/
   {key:"Total_Module",val:[{id:1,text:"部门"},{id:2,text:"区域"}],db:"",sql:""},
   
   /**以下是通用安全管理需要用到的枚举****************************/
   //学历枚举
   {key:"edu",val:[{id:1,text:"中专"},{id:2,text:"大专"},{id:3,text:"本科"},
   {id:4,text:"硕士"},{id:5,text:"博士"},{id:6,text:"其它"}]},
   //是否
   {key:"yesno",val:[{id:1,text:"是"},{id:2,text:"否"}]},   
   //部门类型
   {key:"deptType",val:[{id:1,text:"常规部门"},{id:2,text:"代维部门"},
  	 {id:3,text:"团队"}]},
   //部门级别
   {key:"deptLevel",val:[{id:1,text:"中心"},{id:2,text:"科室"},{id:3,text:"班组"}]},
   //地市编号
   {key:"sCityCode",sql:"select sCityCode as \"id\", DataDomain_Name as \"text\""+
   	" from tDataDomain where sCityCode is not null"},
    //区域类型
   {key:"domainType",val:[{id:1,text:"省"},{id:2,text:"本地网"},{id:3,text:"县市"},
  	 {id:4,text:"扇区"},{id:5,text:"自定义"},{id:20,text:"子网"},{id:21,text:"EMS"}]},
   //权限应用类型
   {key:"rightAppFor",val:[{id:1,text:"全局"},{id:2,text:"电子运维"},
   	{id:3,text:"长线系统"},{id:4,text:"无线系统"},{id:6,text:"大客户系统"}]},
   //权限应用类型2
   {key:"rightAppFor2",val:[{id:1,text:"全局域"},{id:2,text:"个性域"}]},
   //权限类型
   {key:"rightType",val:[{id:1,text:"菜单"},{id:2,text:"功能按钮"},{id:3,text:"C/S权限"},{id:5,text:"tab页"},{id:4,text:"其它"}]},      
   //所属区域
   {key:"domain",sql:"select iDomainId as \"id\",sDomainName as \"text\" from tbOsDomain",val:null},
   //权限状态是否激活
   {key:"rightStatus",val:[{id:1,text:"是"},{id:0,text:"否"}]}, 
   //密码找回问题
   {key:"Retrieve_Password_Question",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'Question' and sEnumTblName = 'Retrieve_Password'"},
    //日志操作模块
   {key:"logType",val:[{id:0,text:"SSH3示例",children:[{id:11,text:"附件上传于下载"}]},{id:1,text:"安全管理"}],db:"",sql:""},
   {key:"domain",sql:"select iDomainId as \"id\",sDomainName as \"text\" from tbOsDomain"},
   {key:"iOptObjType",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iOptObjType' and sEnumTblName = 'tbOsOperateLog'"},
   {key:"iOptLogType",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iOptLogType' and sEnumTblName = 'tbOsOperateLog'"},
   //IP黑白名单
   {key:"iObjType",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iObjType' and sEnumTblName = 'tbOsIpRight'"},
   {key:"iListType",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iListType' and sEnumTblName = 'tbOsIpRight'"},
   
   /*公告管理*/
   //公告状态
   {key:"iBulletinStatus",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'I_BULLETIN_STATUS' and sEnumTblName = 'T_BULLETIN_TICKET_INFO'"},
   //公告类型
   {key:"iKind",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'I_KIND' and sEnumTblName = 'T_BULLETIN_TICKET_INFO'"},
   //公告级别
   {key:"iLevel",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'I_LEVEL' and sEnumTblName = 'T_BULLETIN_TICKET_INFO'"},
   //公告范围
   {key:"iRange",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'I_RANGE' and sEnumTblName = 'T_BULLETIN_TICKET_INFO'"},
   //审核意见

   {key:"iIsMind",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'I_IS_MIND' and sEnumTblName = 'T_PRO_BULLETIN_AUDIT'"},

   {key:"iIsMind",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'I_IS_MIND' and sEnumTblName = 'T_PRO_BULLETIN_AUDIT'"},

   /*快速开发平台*/
   //表格字段数据类型
   {key:"fieldType",val:[{id:1,text:"整数"},{id:2,text:"小数"},{id:3,text:"字符串"},{id:4,text:"日期时间"},{id:5,text:"日期"},{id:6,text:"时间"}]},
   //属性基本校验类型
   {key:"tbFdFormProperty_iValidType",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iValidType' and sEnumTblName = 'tbFdFormProperty' order by iEnumValue"},
   //属性比较类型
   {key:"tbFdFormProperty_iCompareType",val:[{id:0,text:">"},{id:1,text:"<"},{id:2,text:"="},{id:3,text:">="},{id:4,text:"<="}]},
   //属性参考类型
   {key:"tbFdFormProperty_iRefType",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iRefType' and sEnumTblName = 'tbFdFormProperty'"},
   //界面基本呈现控件
   {key:"tbFdFormProperty_iElement",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iElement' and sEnumTblName = 'tbFdFormProperty'"},
   //数据源
   {key:"tbImportTemplates_sDataSource",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'sDataSource' and sEnumTblName = 'tbImportTemplates'"},
   //以后新加枚举，规定枚举key命名格式为:表名_字段名

    /*技术管理*/
   //技术清单-兼容浏览器
   {key:"tbTechnologyList_sBrowser",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'sBrowser' and sEnumTblName = 'tbTechnologyList'"},
   //技术清单-兼容数据库
   {key:"tbTechnologyList_sDatabase",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'sDatabase' and sEnumTblName = 'tbTechnologyList'"},
   //技术清单-兼容操作系统
   {key:"tbTechnologyList_sOS",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'sOS' and sEnumTblName = 'tbTechnologyList'"},
   //使用反馈-使用状态
   {key:"tbTechnologyUserFeedBack_iState",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iState' and sEnumTblName = 'tbTechnologyUserFeedBack'"},
   //使用反馈-反馈评级
   {key:"tbTechnologyUserFeedBack_iGrade",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iGrade' and sEnumTblName = 'tbTechnologyUserFeedBack'"},
   //问题回复-解决状态
   {key:"tbTechnologyAnswerQuestion_iState",sql:"select iEnumValue as \"id\", sEnumName as \"text\" from tbCtEnumTbl2 where sEnumColName = 'iState' and sEnumTblName = 'tbTechnologyAnswerQuestion'"},
   
    /*库表管理*/
   //表格字段数据类型
   {key:"DGT_DBTM_FIELD_S_FIELDTYPE",val:[{id:1,text:"小数"},{id:2,text:"整数"}, {id:3,text:"不定长字符串(varchar)"},{id:6,text:"定长字符串(char)"},{id:4,text:"年-月-日"},{id:7,text:"年-月-日 时-分-秒"},{id:5,text:"长文本"}],db:"",sql:""},
   //字段取值类型
   {key:"DGT_DBTM_FIELD_I_VALUETYPE",val:[{id:1,text:"不指定"},{id:2,text:"区间"},{id:3,text:"枚举"},{id:4,text:"级联"},{id:5,text:"公共枚举"}],db:"",sql:""}
   
];

/**********************************************************************************************************/
EnumRequest=function(){
	var timeout=40000;//超时时长
    function syn(async){ return (async!=false&&async!="false")?true:false;}
	return{
		/*
	     * 获取枚举 
	   	 * async true为异步，false为同步，默认异步                    
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
			if(typeof(enumVal)!="undefined"){fn.call(this,enumVal);return;}//如果有确定值，直接返回
			if(param==null){simpleAlert({msg:"没有获得"+key+"枚举配置！",opts:{timeout:5000,winId:"catt_err_msg"}});fn.call(this,[]);return;}	
			
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
					simpleAlert({msg:"请求枚举数据失败!",opts:{timeout:5000,winId:"catt_err_msg"}});
					fn.call(this,[]);
			 	 }
			 });
		},
		/*
	     * 获取枚举  从T_ENUMERATE表获取枚举值,返回一个对象
	     * 枚举json格式：{"3":"传输","7":"接入","2":"无线市话","4":"数据网","1":"交换"}
	     * 简单对象json格式：[{"value":"3","text":"传输"},{"value":"7","text":"接入"},{"value":"2","text":"无线市话"},{"value":"4","text":"数据网"},{"value":"1","text":"交换"}]                 
         * async 是否同步，默认异步
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
					simpleAlert({msg:"请求枚举数据失败!",opts:{timeout:5000,winId:"catt_err_msg"}});
					fn.call(this,[]);
			 	 }
			 });			
		},
		/*
	     * 清除缓存的枚举 
	     * async 是否同步，默认异步                   
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
					simpleAlert({msg:"请求枚举数据失败!",opts:{timeout:5000,winId:"catt_err_msg"}});
					fn.call(this,"0");
			 	 }
			 });		
		},	
		/*
	     * 初始化下拉列表                    --add by 林秀刚
	     * key      枚举KEY
	     * selId    下拉列表ID
	     * initSelId    初始化选择项的下拉列表ID,可选的入参 --add by 董晓锋
	     * flg 是否加默认项 默认不加
	     * desc 描述值，默认"请选择"
	     * async 是否同步，默认异步
	     */
		initSelect:function(key,selId,initSelId,flg,desc,async){
			EnumRequest.getEnum(key,function(backdata){
				sel = document.getElementById(selId);
				if(sel == null || typeof(sel) == "undefined") {
		             simpleAlert({msg:"ID为"+selId+"的下拉列表对象不存在！",opts:{timeout:5000,winId:"catt_err_msg"}});
		             return;
		    	}
		    	sel.options.length = 0;
		    	if(flg){
		    		var option = new Option();
				     option.text =desc?desc:"请选择";
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
	     * 初始化下拉列表（枚举表数据）
	     * key        枚举KEY  表名_字段名   如：tbOsStaff_iSpec
	     * selId      下拉列表ID
	     * initSelId （可选）初始化选择项的下拉列表ID,可选的入参 
	     * fn        （可选） 初始化下拉列表完成后的回调函数
	     * sql        简单对象要执行的sql语句
	     * reload     是否重新执行sql语句，而不取缓存 true是 false否
	     * async 是否同步，默认异步
	     */
		initSelectEnum:function(key,selId,initSelId,fn,sql,reload,async){
			EnumRequest.getEnumMeaning(key,sql,reload,function(backdata){
				sel = document.getElementById(selId);
				if(sel == null || typeof(sel) == "undefined") {
		             simpleAlert({msg:"ID为"+selId+"的下拉列表对象不存在！",opts:{timeout:5000,winId:"catt_err_msg"}});
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