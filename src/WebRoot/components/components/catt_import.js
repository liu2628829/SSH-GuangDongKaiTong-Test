/**
 通用导入JS
 author:gaotao
 2012/09/10
*/
$.Catt_AjaxImprotFile=function(){
    return {
        /**
	       导入的入口方法，对外仅开放此方法
	      params是个对象形入参，包含属性有:
	       Fd_formId:表单ID(针对通用表单)
	       sDataSource:"Oracle",//数据源
		   sTableName:"TBFDTEST",数据表名称
		   Fd_iFormId:"34090",//表单ID
		   Catt_import_type: //1.以模板配置的表字段定义为准; 2或3.通用表单1.0表字段定义为准; 5.通用表单2.0的表字段定义为准
		   Catt_import_define:"com.catt.XXX",是util.commonModule.importMgr.IImportDefine.java 的实现类
		   fn 导入完毕后的回调函数，一般导入完毕后，都需要做数据刷新的动作
	    */
        importData:function(params,fn){
            $.Catt_AjaxImprotFile.params=params;
            $.Catt_AjaxImprotFile.params.Catt_export_random=parseInt(Math.random()*1000);//页面唯一标识
            $.Catt_AjaxImprotFile.setBasePath();
            $.Catt_AjaxImprotFile.url=$.Catt_AjaxImprotFile.basePath+"/commonModule/importMgr/importTemplate!importData.action";
            $.Catt_AjaxImprotFile.createFileUploadpage();
            $.Catt_AjaxImprotFile.backFun=fn;
        },
        /**
	      检查必须的参数，是否齐全
	    */
        checkRequire:function(){
	    
        },
        /*下载模板，先通过异步请求，时实生成模板，然后再弹出模板*/
        doFileDownLoad:function(){
            if(typeof(WaitBar) != "undefined"){WaitBar.show(2);}
            /**
	    	1，要知道导入方式，从而知道怎么生成模板
	    	2，要知道表单ID
	    	3，表名	
	    	*/
            $.ajax({
                type: 'POST',
                url: $.Catt_AjaxImprotFile.basePath+"/commonModule/importMgr/importTemplate!createTemplate.action",
				data: Base.encryptParam($.Catt_AjaxImprotFile.params),
                async: false,//异步，false为阻塞
                timeout:40000,//40秒后超时
                dataType: 'text',
                success: function(data, textStatus) {//第一次发起请求成功
                    //返回模板url后，下载模板文件
                    if(typeof(WaitBar) != "undefined"){WaitBar.hide(2);}
                    //Base.alert({msg:'请点击确定开始下载！',type:2,OK:"下载",fn:function(){
                    if(data.indexOf("\"SUCCESS\":\"false\"")>0){
                    	Base.alert("模板生成失败，请重试！", {icon: 1});
                    	return;
                    }
                    $.Catt_AjaxImprotFile.doLinkFile(data,'');
                    //}});
                },
                error: function(XMLHttpRequest, textStatus) {//模板生成失败
                    Base.alert("模板生成失败，请重试！");
                }
            });
        },
        /**
	      通过iframe下载文件
	    */
        doLinkFile:function(url,fileName){
            var fr=document.getElementById("Catt_importFile_frame");
            if(!fr){
                $("body").append("<iframe id='Catt_importFile_frame' src='' style='display:none;'></iframe>");
            }
            //进行两次编码操作，因url中传的参数,是get提交，java后台默认将解码一次，解完后就被过EncodeFilter滤器给弄成乱码了
            //但异步提交的参数，只需要编码一次
            var src=$.Catt_AjaxImprotFile.basePath+"/commonModule/importMgr/importTemplate!getTemplate.action?path="
            +encodeURIComponent(encodeURIComponent(url))+"&fileName="
            +encodeURIComponent(encodeURIComponent(fileName));
            document.getElementById("Catt_importFile_frame").src = Base.encryptURL(src);
        },
              
        //发起文件解析与导入动作，并监控实时导入进度
        doImport:function(){
            //$.Catt_AjaxImprotFile.params.Catt_import_dataFile="E:\\workspace\\SSH3\\WebRoot\\admin\\commonModule\\fastdfs\\upload\\zzz\\import_Temp\\1.xls";//c7f0a147-b980-4570-b5bb-27877cb112fc
            if(!$.Catt_AjaxImprotFile.params.Catt_import_dataFile){//判断文件是否有上传文件
                Base.alert("请先上传要导入的数据文件！");
                return;
            }
            $.Catt_AjaxImprotFile.doCloseWin();//关闭文件上传窗
            if(typeof(WaitBar) != "undefined") {WaitBar.show(2);}//等待条
            //alert($.Catt_AjaxImprotFile.params.Catt_import_dataFile);
            //提交请求
            $.ajax({
                type: 'POST',
			    url: Base.encryptURL($.Catt_AjaxImprotFile.url),
				data: Base.encryptParam($.Catt_AjaxImprotFile.params),
                async: true,//异步，false为阻塞
                timeout:60000,//20秒后超时
                dataType: 'text',
                success: function(data, textStatus) {//第一次发起请求成功
                	//if(typeof(WaitBar) != "undefined")WaitBar.hide(2);
                    $.Catt_AjaxImprotFile.createInfo(data,function(boo){
	                    $.Catt_AjaxImprotFile.params.Catt_import_query_=false;
	                    // $.Catt_AjaxImprotFile.reImport();
                        if(boo){
                            $.Catt_AjaxImprotFile.queryTimeoutIvl = window.setTimeout("$.Catt_AjaxImprotFile.reImport()",1000);
                        }
                    });
                },
                error: function(XMLHttpRequest, textStatus) {//请求超时或其它错误
                    Base.alert("出错了!\n0:\t"+textStatus, {icon:2});
                    if(typeof(WaitBar) != "undefined"){WaitBar.hide(2);}
                }
            });
        },
        /**
		超时后发起自动重连
		*/
        reImport:function(){
        	if($.Catt_AjaxImprotFile.params.Catt_import_break_flag=="1"){return;}
            $.Catt_AjaxImprotFile.params.Catt_import_query_ = parseInt(Math.random()*1000);
            $.ajax({
                type: 'POST',
			    url: Base.encryptURL($.Catt_AjaxImprotFile.url),
				data: Base.encryptParam($.Catt_AjaxImprotFile.params),
                async: true,//异步，false为阻塞
                timeout:5000,//20秒后超时
                dataType: 'text',
                success: function(data, textStatus) {//第一次发起请求成功
                    $.Catt_AjaxImprotFile.createInfo(data,function(boo){
                    	window.clearTimeout($.Catt_AjaxImprotFile.queryTimeoutIvl);
                        if(boo){
                            $.Catt_AjaxImprotFile.queryTimeoutIvl = window.setTimeout("$.Catt_AjaxImprotFile.reImport()",2000);
                        }
                    });
                },
                error: function(XMLHttpRequest, textStatus) {//请求超时或其它错误
                	if($.Catt_AjaxImprotFile.tryTime==null){
	                	$.Catt_AjaxImprotFile.tryTime = 0;
                	}
               		if($.Catt_AjaxImprotFile.tryTime++ > 4){
                    	Base.alert("出错了!\n1:\t"+textStatus, {icon:2});
                    	if(typeof(WaitBar) != "undefined"){WaitBar.hide(2);}
                    }else{
                    	$.Catt_AjaxImprotFile.reImport();
                    }
                }
            });
        },
        /**
		打断导入
		*/
        breakImport: function(finish){
            $.Catt_AjaxImprotFile.params.Catt_import_break_flag="1";
            if(finish==1){
                $.Catt_AjaxImprotFile.params.Catt_export_finish_flag="1";//标识下载开始，删除线程
            }else{
                if(typeof(WaitBar) != "undefined") {WaitBar.setMsg("<font style='font-weight: bold;color:red'>&nbsp;&nbsp;&nbsp;&nbsp;取消中....</font>");}
            }
            $.Catt_AjaxImprotFile.params.Catt_import_break_flag="1";//标识打断
            //提交请求
            $.ajax({
                type: 'POST',
			 	url: Base.encryptURL($.Catt_AjaxImprotFile.url),
				data: Base.encryptParam($.Catt_AjaxImprotFile.params),
                async: true,//异步，false为阻塞
                timeout:20000,//20秒后超时
                dataType: 'text',
                success: function(data, textStatus) {
                    var msg=null;
                    eval("msg="+data);
                    if(msg.iState=="4"){//临时表往正式表迁移过程中,不能取消
                        $.Catt_AjaxImprotFile.createInfo(data,$.Catt_AjaxImprotFile.reImport);
                    }else if(msg.iState=="5"){//取消成功
                        $.Catt_AjaxImprotFile.createInfo(data,null);
                    }else if(msg.iState=="6"){//点取消时，后台已经导入完毕
                        $.Catt_AjaxImprotFile.createInfo(data,null);
                    }
                },
                error: function(XMLHttpRequest, textStatus){
                    //$.Catt_AjaxImprotFile.breakImport();
                    Base.alert("出错了!\n 2",{icon:2});
                }
            });
        },
		
        /**
		进度提示信息
		*/
        createInfo:function(data,fun){
            var opts = {width:450};
            var msg = null;
            eval("msg="+data);
            msg.iState = "" +msg.iState;
            var errCnts = msg.update_insert_err_counts;
            var cts = errCnts ? errCnts.split(",") : [];
            //不同的状态值做不同的提示
            switch(msg.iState){
                case "-1"://当前导入线程超标
                    $.Catt_AjaxImprotFile.setWaitBar();
                	if(msg.SAME_IMPORT!=null){
                		Base.alert("当前有其他用户在对【"+msg.sTemplateName+"】表进行数据导入，请稍后操作！",{icon:0});
                	}else{
                    	Base.alert("系统最多允许同时存在"+msg.MAX_IMPORT+"个导入进程，目前"+msg.MAX_IMPORT+"个进程，正被占用中，请稍后操作！",{icon:0});
                    }
                    break;
                case "0" ://进行中
                    if(!msg.totalCount){
                        var m="数据解析中，请稍等!";
                    }else{
                        var m="当前文件总记录数为"+msg.totalCount+"行<br>";
                        var progress=msg.currentCount/msg.totalCount*100;
                        progress=Math.round(progress);
                        m+="文件解析进度<font style='font-weight: bold;color:red'>"+progress+"%</font><br>";
                        if(msg.errCount){m+="已发现有不合格数据"+msg.errCount+"行<br>";}
                        if(progress<100){
                            m+="<a href='javascript:void(0)' onclick='$.Catt_AjaxImprotFile.breakImport()'><font style='font-weight: bold;color:red'>点击这里可取消导入</font></a><br>";
                        }else{
                            m+="数据入库中，请稍等!";
                        }
                    }
                    if(typeof(WaitBar) != "undefined"){
                    	WaitBar.show(2);
                        WaitBar.setMsg(m);
                    }
                    fun.call(this,true);
                    break;
                case "1"://导入完毕
                    var desc=[];
                    if(cts[0]*1>0)desc.push("修改"+cts[0]+"行");
                    if(cts[1]*1>0)desc.push("新增"+cts[1]+"行");
                    if(cts[2]*1>0)desc.push("错误"+cts[2]+"行");
	                if(cts[3]*1>0){desc.push("无效数据"+cts[3]+"行，无效数据可能存在以下原因：\n 1.完全空白的行 \n 2.上传的数据文件内部存在重复行 \n 具体请下载数据文件查看");}
                    var m="!";
                    if($.Catt_AjaxImprotFile.params.Catt_import_break_flag=="1"){//正好点取消时，也可能正好导入完毕
                        m="，已经不能取消！";
                    }
                    if((cts[2]*1+cts[3]*1)>0){//如果有错误行
                        var ms="导入完毕"+m+"\n总计["+msg.totalCount+"行]："+desc.join("，")+"。\n\n已经针对原数据文件中不合格的数据单元格加上了错误信息批注，下载错误文件请按\"确认\"，否则按\"取消\"！";
                        Base.confirm({ms, function(){//下载错误文件
                                if($.Catt_AjaxImprotFile.backFun && typeof($.Catt_AjaxImprotFile.backFun)=='function'){
                                    $.Catt_AjaxImprotFile.backFun();
                                }
                                 $.Catt_AjaxImprotFile.downErrorFile();
                                //$.Catt_AjaxImprotFile.doLinkFile($.Catt_AjaxImprotFile.params.Catt_import_dataFile,$.Catt_AjaxImprotFile.params.Catt_import_oldFileName);
                            }, function(){
                                if($.Catt_AjaxImprotFile.backFun&&typeof($.Catt_AjaxImprotFile.backFun)=='function'){
                                    $.Catt_AjaxImprotFile.backFun();
                                }
                            }
                        });
                    }else{
                    	var str= "导入完毕"+m+"\n总计："+desc.join("，")+"。\n";
                        Base.alert(str, {icon:0,time:5000} ,function(){
                                if($.Catt_AjaxImprotFile.backFun&&typeof($.Catt_AjaxImprotFile.backFun)=='function'){
                                    $.Catt_AjaxImprotFile.backFun();
                                }
                            }
                        );
                    }
				$.Catt_AjaxImprotFile.setWaitBar();//关掉waitbar  
                break;
            case "2"://数据文件中没数据
                $.Catt_AjaxImprotFile.setWaitBar();
                Base.alert("提交的数据文件中无数据！",{icon:0});
                break;
            case "11": //自定义逻辑判断出了有重复数据
            	var des = "发现有"+(msg.repeatCountInfile*1+msg.repeatCountInDB*1)+"行与已有数据重复，是否直接覆盖已有数据？\n直接覆盖请按\"确定\"；只导入未重复的数据，请按\"取消\"！ \n\n注：操作完毕后可下载文件查看明细。";
                Base.confirm(des, function(){//继续
                        if(typeof(WaitBar) != "undefined"){WaitBar.show(2);}
                        $.Catt_AjaxImprotFile.params.Catt_import_repeatConfirm_flag="continue";
                        fun.call(this,true);
                    }, function(){//取消
                        if(typeof(WaitBar) != "undefined"){WaitBar.show(2);}
                        $.Catt_AjaxImprotFile.params.Catt_import_repeatConfirm_flag="break";
                        fun.call(this,true);
                    }
                );
                $.Catt_AjaxImprotFile.setWaitBar();
                break;
            case "3"://发现有不合格的数据,由用户确认是否继续
                if(msg.totalCount == cts[2]){ //不合格记录数与总记录行数一样多，直接提示下载错误文件
                	$.Catt_AjaxImprotFile.breakImport();
                	Base.confirm("所有数据都不合格，是否下载错误文件？\n下载请按\"确定\"，否则按\"取消\"！", 
                            function(){//下载错误文件
                            	   $.Catt_AjaxImprotFile.downErrorFile();
                            }
	                 );
                }else{
	                Base.confirm("总计"+msg.totalCount+"行数据中，发现"+cts[2]+"行不合格，确定继续导入吗？", 
	                	function(){//继续
	                        if(typeof(WaitBar) != "undefined"){WaitBar.show(2);}
	                        $.Catt_AjaxImprotFile.params.Catt_import_errConfirm_flag="continue";
	                        fun.call(this,true);
	                    },function(){//取消
	                        $.Catt_AjaxImprotFile.breakImport();
	                        Base.confirm("是否下载错误文件？\n下载请按\"确定\"，否则按\"取消\"！",function(){//下载错误文件
	                                $.Catt_AjaxImprotFile.downErrorFile();
	                            }
	                        );
	                    }
	                );
                }
                $.Catt_AjaxImprotFile.setWaitBar();
                break;
            case "4"://已经是临时表往正式表搬数据的过程中，不可再取消
                if(typeof(WaitBar) != "undefined"){
                    WaitBar.show(2);
                    WaitBar.setMsg("数据入库中，请稍等！");
                }
                fun.call(this,true);
           		break;
	        case "5"://取消完毕
	            $.Catt_AjaxImprotFile.setWaitBar();
	            break;
	        case "6"://数据导入完毕，无法取消
	            var desc=[];
	            if(cts[0]*1>0){desc.push("修改"+cts[0]+"行");}
	            if(cts[1]*1>0){desc.push("新增"+cts[1]+"行");}
	            if(cts[2]*1>0){desc.push("错误"+cts[2]+"行");}
	            if(cts[3]*1>0){desc.push("无效数据"+cts[3]+"行，无效数据可能存在以下原因：\n 1.完全空白的行 \n 2.上传的数据文件内部存在重复行 \n 具体请下载数据文件查看");}
	            if((cts[2]*1+cts[3]*1)>0){
	                var ms="导入完毕"+m+"\n总计["+msg.totalCount+"行]："+desc.join("，")+"。\n\n已经针对原数据文件中不合格的数据单元格加上了错误信息批注，下载错误文件请按\"确定\"，否则按\"取消\"！";
	                Base.comfirm({ms, function(){//下载错误文件
	                        if($.Catt_AjaxImprotFile.backFun&&typeof($.Catt_AjaxImprotFile.backFun)=='function'){
	                            $.Catt_AjaxImprotFile.backFun();
	                        }
	                        $.Catt_AjaxImprotFile.downErrorFile();
	                    }, function(){
	                        if($.Catt_AjaxImprotFile.backFun&&typeof($.Catt_AjaxImprotFile.backFun)=='function'){
	                            $.Catt_AjaxImprotFile.backFun();
	                        }
	                    }
	                });
	            }else{
	                Base.alert("导入完毕，已经不能取消！\n总计["+msg.totalCount+"行]："+desc.join(",")+"。\n",
	                		{icon:0,time:5000}, function(){
	                        if($.Catt_AjaxImprotFile.backFun&&typeof($.Catt_AjaxImprotFile.backFun)=='function'){
	                            $.Catt_AjaxImprotFile.backFun();
	                        }
	                    }
	                );
		        }
		        $.Catt_AjaxImprotFile.setWaitBar();//关掉waitbar
		        break;
		    case "7":
		        $.Catt_AjaxImprotFile.setWaitBar();//关掉waitbar
		        Base.alert("数据导入失败，导入过程中，系统发生未知异常！请联系系统厂家协助排查！", {icon:0});
		        break;
		    case "8":
		        $.Catt_AjaxImprotFile.setWaitBar();//关掉waitbar
		        if(msg.IMP_MAX_FILESIZE!=null){
		            Base.alert("所上传的数据文件太大，不应超过："+msg.IMP_MAX_FILESIZE+" KB", {icon:0});
		        }else{
		            Base.alert("所传文件不合规范: "+msg.msg, {icon:0});
		        }
		        break;
		    case "9":
		        $.Catt_AjaxImprotFile.setWaitBar();//过期请求，关掉waitbar
		        break;
		    case "10":
		    	var ms="总计"+msg.totalCount+"行数据中，发现"+cts[2]+"行不合格，所有数据取消导入。\n\n已经针对原数据文件中不合格的数据单元格加上了错误信息批注，下载错误文件请按\"确定\"，否则按\"取消\"！";
	            Base.confirm(ms, function(){//下载错误文件
	                        if($.Catt_AjaxImprotFile.backFun&&typeof($.Catt_AjaxImprotFile.backFun)=='function'){
	                            $.Catt_AjaxImprotFile.backFun();
	                        }
	                        $.Catt_AjaxImprotFile.downErrorFile();
	                    }
	             );
	                    
	            $.Catt_AjaxImprotFile.setWaitBar();
		    	break;
		    case "12":
		    	if(msg.IMP_MAX_LINES != null){ //CURRENT_MAX_LINES
			    	//Base.alert("文件数据条数不允许超过" + msg.IMP_MAX_LINES + "条");
			    	var l = msg.CURRENT_MAX_LINES - msg.IMP_MAX_LINES ;
			    	Base.alert("文件数据行数越限，最大允许"+ msg.IMP_MAX_LINES +"行，当前实际"+msg.CURRENT_MAX_LINES+"行，超出"+l+"行", {icon:0});
			    	$.Catt_AjaxImprotFile.setWaitBar();
		    	}
		    	break;
		    case "13":
		    	$.Catt_AjaxImprotFile.setWaitBar();
		    	if(msg.isFileClosed == "1"){
		    		$.Catt_AjaxImprotFile.downErrorFile();
		    	}else{
			    	Base.alert(
			    		"错误单元格过多，目前已经发现"+msg.errCommentCount+"个。程序如果继续运行将极有可能导致错误批注信息无法回写至文件，甚至导致内存溢出、系统崩溃的风险！\n所以请先下载文件，按文件内的错误批注信息解决好已分析出的错误后再进行导入，谢谢！",
			    		{icon:0},
			    		function(){
			    			if(typeof(WaitBar) != "undefined"){
			                	WaitBar.show(2);
			                    WaitBar.setMsg("正在对分析出的错误批注信息回写至文件，请稍等！");
			                }
			                fun.call(this,true);
			    		}		    		
			    	);
		    	}
		    	break;
		    case "14":
		    	if(typeof(WaitBar) != "undefined"){
                	WaitBar.show(2);
                    WaitBar.setMsg("正在对分析出的错误批注信息回写至文件，请耐心等候！");
                }
                fun.call(this,true);
		    	break;
		    case "15":
		    	if(typeof(WaitBar) != "undefined"){
                	WaitBar.show(2);
                    WaitBar.setMsg("错误行向文件前方汇聚过程中，请耐心等候！");
                }
                fun.call(this,true);
		    	break;
		    default:
		        $.Catt_AjaxImprotFile.setWaitBar();//关掉waitbar
		        Base.alert("不明错误代号："+msg.iState, {icon:0});
		        break;
}
},

//下载错误文件
downErrorFile:function(){
	$.Catt_AjaxImprotFile.doLinkFile($.Catt_AjaxImprotFile.params.Catt_import_dataFile,$.Catt_AjaxImprotFile.params.Catt_import_oldFileName);
},
		
//构建导入小界面
createFileUploadpage:function(){
    var div=document.getElementById("Catt_importFile_div");
    if(!div){
        div="<div id='Catt_importFile_div' style='overflow:hidden;' class='defaultColor'>"+
        "<form method='post'>"+
        "<table width='100%' height='50px' class='formbasic'><tr>" +
        "<td style='width:100px;'><font style='color:red;padding-left:5px;float:none;'>*</font>选择数据文件</td>"+
        "<th><input type='text' id='Catt_importFile_input' name='Catt_importFile_input' style='width:218px'></th></tr><table>"+
        "</form>"+
        "<table width='100%' height='50px' style='border:0;margin:0;padding:0;border:0;' class='formbasic'><tr><td style='border-top:0' style='text-align:center;'>"+ 
        "<ul class='btn_hover' style='width:360px;margin-left:35px;'>"+
        "<li onClick='$.Catt_AjaxImprotFile.doFileDownLoad()' style='width:90px'><a href='javascript:void(0)'><span><div>下载模板</div></span></a></li>"+
        "<li onClick='$.Catt_AjaxImprotFile.doImport();'><a href='javascript:void(0)'><span><div>确定</div></span></a></li>"+
        "<li onClick='$.Catt_AjaxImprotFile.doCloseWin()'><a href='javascript:void(0)'><span><div>取消</div></span></a></li>"+
        "</ul></td></tr></table>"+
        "</div>";
        $("body").append(div);
    }else{
        //重置上传控件
        $.Catt_AjaxImprotFile.params.Catt_import_dataFile="";
        $.Catt_AjaxImprotFile.params.Catt_import_oldFileName="";
    }
    $("#Catt_importFile_div").window({
        title:"上传数据文件",
        closable:true,
        collapsible:false,
        minimizable:false,
        maximizable:false,
        resizable:false,
        modal:true,
        width:400,
        height:128
    });
    //生成单文件上传控件
    $.commonUtil.initFileUpload(
    {
        eId:'Catt_importFile_input',
        sTableName:'importTemplate',
        isToLocal:1,//仅上传到本地应用服务器
        isSaveToDB:0,//不保存文件信息到附件表
        limitReg:"*.xls;*.xlsx;",//限制只能上传xls
   //     maxUpSize:(getSystemParams("IMP_MAX_FILESIZE",10240))/1024,//文件大小限制,默认10M
        //directory:'import_Temp',
        displayName:"localFileName",//文本框中显示客户端文件路径
        afterUpload:function(obj){
            $.Catt_AjaxImprotFile.params.Catt_import_dataFile=obj.fileId;//文件上传完毕，得到文件的在服务端的URL
            $.Catt_AjaxImprotFile.params.Catt_import_oldFileName=obj.oldFileName;
        },
        afterClean:function(obj){
            $.Catt_AjaxImprotFile.params.Catt_import_dataFile="";
            $.Catt_AjaxImprotFile.params.Catt_import_dataFile="";
        }
    }
    );
},
//取消关闭小窗口
doCloseWin:function(){
    $("#Catt_importFile_div").window("close");
    //清理上传控件的值
    $("#Catt_importFile_input").val("");
},
/**
		得上下文路径
		*/
setBasePath:function(){
    var pathname=Base.getPathName();
    var basepath=Base.getFullPath();
    $.Catt_AjaxImprotFile.basePath=basepath;
    return basepath;
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
queryTimeoutIvl:null,
params:null,//保存所有初始入参
basePath:null//上下文路径 如 /SSH3
};

}();

