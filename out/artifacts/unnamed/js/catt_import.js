/**
 ͨ�õ���JS
 author:gaotao
 2012/09/10
*/
var Catt_AjaxImprotFile=function(){
    return {
        /**
	       �������ڷ�������������Ŵ˷���
	      params�Ǹ���������Σ�����������:
	       Fd_formId:��ID(���ͨ�ñ�)
	       sDataSource:"Oracle",//����Դ
		   sTableName:"TBFDTEST",���ݱ�����
		   Fd_iFormId:"34090",//��ID
		   Catt_import_type: //1.��ģ�����õı��ֶζ���Ϊ׼; 2��3.ͨ�ñ�1.0���ֶζ���Ϊ׼; 5.ͨ�ñ�2.0�ı��ֶζ���Ϊ׼
		   Catt_import_define:"com.catt.XXX",��util.commonModule.importMgr.IImportDefine.java ��ʵ����
		   fn ������Ϻ�Ļص�������һ�㵼����Ϻ󣬶���Ҫ������ˢ�µĶ���
	    */
        importData:function(params,fn){
            Catt_AjaxImprotFile.params=params;
            Catt_AjaxImprotFile.params.Catt_export_random=parseInt(Math.random()*1000);//ҳ��Ψһ��ʶ
            Catt_AjaxImprotFile.setBasePath();
            Catt_AjaxImprotFile.url=Catt_AjaxImprotFile.basePath+"/commonModule/importMgr/importTemplate!importData.action";
            Catt_AjaxImprotFile.createFileUploadpage();
            Catt_AjaxImprotFile.backFun=fn;
        },
        /**
	      ������Ĳ������Ƿ���ȫ
	    */
        checkRequire:function(){
	    
        },
        /*����ģ�壬��ͨ���첽����ʱʵ����ģ�壬Ȼ���ٵ���ģ��*/
        doFileDownLoad:function(){
            if(typeof(WaitBar) != "undefined"){WaitBar.show(2);}
            /**
	    	1��Ҫ֪�����뷽ʽ���Ӷ�֪����ô����ģ��
	    	2��Ҫ֪����ID
	    	3������	
	    	*/
            $.ajax({
                type: 'POST',
                url: Catt_AjaxImprotFile.basePath+"/commonModule/importMgr/importTemplate!createTemplate.action",
				data: encryptParams(Catt_AjaxImprotFile.params),
                async: false,//�첽��falseΪ����
                timeout:40000,//40���ʱ
                dataType: 'text',
                success: function(data, textStatus) {//��һ�η�������ɹ�
                    //����ģ��url������ģ���ļ�
                    if(typeof(WaitBar) != "undefined"){WaitBar.hide(2);}
                    //simpleAlert({msg:'����ȷ����ʼ���أ�',type:2,OK:"����",fn:function(){
                    if(data.indexOf("\"SUCCESS\":\"false\"")>0){
                    	simpleAlert("ģ������ʧ�ܣ������ԣ�");
                    	return;
                    }
                    Catt_AjaxImprotFile.doLinkFile(data,'');
                    //}});
                },
                error: function(XMLHttpRequest, textStatus) {//ģ������ʧ��
                    simpleAlert("ģ������ʧ�ܣ������ԣ�");
                }
            });
        },
        /**
	      ͨ��iframe�����ļ�
	    */
        doLinkFile:function(url,fileName){
            var fr=document.getElementById("Catt_importFile_frame");
            if(!fr){
                $("body").append("<iframe id='Catt_importFile_frame' src='' style='display:none;'></iframe>");
            }
            //�������α����������url�д��Ĳ���,��get�ύ��java��̨Ĭ�Ͻ�����һ�Σ������ͱ���EncodeFilter������Ū��������
            //���첽�ύ�Ĳ�����ֻ��Ҫ����һ��
            var src=Catt_AjaxImprotFile.basePath+"/commonModule/importMgr/importTemplate!getTemplate.action?path="
            +encodeURIComponent(encodeURIComponent(url))+"&fileName="
            +encodeURIComponent(encodeURIComponent(fileName));
            document.getElementById("Catt_importFile_frame").src = encryptURL(src);
        },
              
        //�����ļ������뵼�붯���������ʵʱ�������
        doImport:function(){
            //Catt_AjaxImprotFile.params.Catt_import_dataFile="E:\\workspace\\SSH3\\WebRoot\\admin\\commonModule\\fastdfs\\upload\\zzz\\import_Temp\\1.xls";//c7f0a147-b980-4570-b5bb-27877cb112fc
            if(!Catt_AjaxImprotFile.params.Catt_import_dataFile){//�ж��ļ��Ƿ����ϴ��ļ�
                simpleAlert("�����ϴ�Ҫ����������ļ���");
                return;
            }
            Catt_AjaxImprotFile.doCloseWin();//�ر��ļ��ϴ���
            if(typeof(WaitBar) != "undefined") {WaitBar.show(2);}//�ȴ���
            //alert(Catt_AjaxImprotFile.params.Catt_import_dataFile);
            //�ύ����
            $.ajax({
                type: 'POST',
			    url: encryptURL(Catt_AjaxImprotFile.url),
				data: encryptParams(Catt_AjaxImprotFile.params),
                async: true,//�첽��falseΪ����
                timeout:40000,//20���ʱ
                dataType: 'text',
                success: function(data, textStatus) {//��һ�η�������ɹ�
                	//if(typeof(WaitBar) != "undefined")WaitBar.hide(2);
                    Catt_AjaxImprotFile.createInfo(data,function(boo){
	                    Catt_AjaxImprotFile.params.Catt_import_query_=false;
	                    // Catt_AjaxImprotFile.reImport();
                        if(boo){
                            Catt_AjaxImprotFile.queryTimeoutIvl = window.setTimeout("Catt_AjaxImprotFile.reImport()",1000);
                        }
                    });
                },
                error: function(XMLHttpRequest, textStatus) {//����ʱ����������
                    simpleAlert("������!\n0:\t"+textStatus);
                    if(typeof(WaitBar) != "undefined"){WaitBar.hide(2);}
                }
            });
        },
        /**
		��ʱ�����Զ�����
		*/
        reImport:function(){
        	if(Catt_AjaxImprotFile.params.Catt_import_break_flag=="1"){return;}
            Catt_AjaxImprotFile.params.Catt_import_query_ = parseInt(Math.random()*1000);
            $.ajax({
                type: 'POST',
			    url: encryptURL(Catt_AjaxImprotFile.url),
				data: encryptParams(Catt_AjaxImprotFile.params),
                async: true,//�첽��falseΪ����
                timeout:5000,//20���ʱ
                dataType: 'text',
                success: function(data, textStatus) {//��һ�η�������ɹ�
                    Catt_AjaxImprotFile.createInfo(data,function(boo){
                    	window.clearTimeout(Catt_AjaxImprotFile.queryTimeoutIvl);
                        if(boo){
                            Catt_AjaxImprotFile.queryTimeoutIvl = window.setTimeout("Catt_AjaxImprotFile.reImport()",2000);
                        }
                    });
                },
                error: function(XMLHttpRequest, textStatus) {//����ʱ����������
                	if(Catt_AjaxImprotFile.tryTime==null){
	                	Catt_AjaxImprotFile.tryTime = 0;
                	}
               		if(Catt_AjaxImprotFile.tryTime++ > 4){
                    	simpleAlert("������!\n1:\t"+textStatus);
                    	if(typeof(WaitBar) != "undefined"){WaitBar.hide(2);}
                    }else{
                    	Catt_AjaxImprotFile.reImport();
                    }
                }
            });
        },
        /**
		��ϵ���
		*/
        breakImport: function(finish){
            Catt_AjaxImprotFile.params.Catt_import_break_flag="1";
            if(finish==1){
                Catt_AjaxImprotFile.params.Catt_export_finish_flag="1";//��ʶ���ؿ�ʼ��ɾ���߳�
            }else{
                if(typeof(WaitBar) != "undefined") {WaitBar.setMsg("<font style='font-weight: bold;color:red'>&nbsp;&nbsp;&nbsp;&nbsp;ȡ����....</font>");}
            }
            Catt_AjaxImprotFile.params.Catt_import_break_flag="1";//��ʶ���
            //�ύ����
            $.ajax({
                type: 'POST',
			 	url: encryptURL(Catt_AjaxImprotFile.url),
				data: encryptParams(Catt_AjaxImprotFile.params),
                async: true,//�첽��falseΪ����
                timeout:20000,//20���ʱ
                dataType: 'text',
                success: function(data, textStatus) {
                    var msg=null;
                    eval("msg="+data);
                    if(msg.iState=="4"){//��ʱ������ʽ��Ǩ�ƹ�����,����ȡ��
                        Catt_AjaxImprotFile.createInfo(data,Catt_AjaxImprotFile.reImport);
                    }else if(msg.iState=="5"){//ȡ���ɹ�
                        Catt_AjaxImprotFile.createInfo(data,null);
                    }else if(msg.iState=="6"){//��ȡ��ʱ����̨�Ѿ��������
                        Catt_AjaxImprotFile.createInfo(data,null);
                    }
                },
                error: function(XMLHttpRequest, textStatus){
                    //Catt_AjaxImprotFile.breakImport();
                    simpleAlert("������!\n 2");
                }
            });
        },
		
        /**
		������ʾ��Ϣ
		*/
        createInfo:function(data,fun){
            var opts = {width:450};
            var msg = null;
            eval("msg="+data);
            msg.iState = "" +msg.iState;
            var errCnts = msg.update_insert_err_counts;
            var cts = errCnts ? errCnts.split(",") : [];
            //��ͬ��״ֵ̬����ͬ����ʾ
            switch(msg.iState){
                case "-1"://��ǰ�����̳߳���
                    Catt_AjaxImprotFile.setWaitBar();
                	if(msg.SAME_IMPORT!=null){
                		simpleAlert("��ǰ�������û��ڶԡ�"+msg.sTemplateName+"����������ݵ��룬���Ժ������");
                	}else{
                    	simpleAlert("ϵͳ�������ͬʱ����"+msg.MAX_IMPORT+"��������̣�Ŀǰ"+msg.MAX_IMPORT+"�����̣�����ռ���У����Ժ������");
                    }
                    break;
                case "0" ://������
                    if(!msg.totalCount){
                        var m="���ݽ����У����Ե�!";
                    }else{
                        var m="��ǰ�ļ��ܼ�¼��Ϊ"+msg.totalCount+"��<br>";
                        var progress=msg.currentCount/msg.totalCount*100;
                        progress=Math.round(progress);
                        m+="�ļ���������<font style='font-weight: bold;color:red'>"+progress+"%</font><br>";
                        if(msg.errCount){m+="�ѷ����в��ϸ�����"+msg.errCount+"��<br>";}
                        if(progress<100){
                            m+="<a href='javascript:void(0)' onclick='Catt_AjaxImprotFile.breakImport()'><font style='font-weight: bold;color:red'>��������ȡ������</font></a><br>";
                        }else{
                            m+="��������У����Ե�!";
                        }
                    }
                    if(typeof(WaitBar) != "undefined"){
                    	WaitBar.show(2);
                        WaitBar.setMsg(m);
                    }
                    fun.call(this,true);
                    break;
                case "1"://�������
                    var desc=[];
                    if(cts[0]*1>0)desc.push("�޸�"+cts[0]+"��");
                    if(cts[1]*1>0)desc.push("����"+cts[1]+"��");
                    if(cts[2]*1>0)desc.push("����"+cts[2]+"��");
	                if(cts[3]*1>0){desc.push("��Ч����"+cts[3]+"�У���Ч���ݿ��ܴ�������ԭ��\n 1.��ȫ�հ׵��� \n 2.�ϴ��������ļ��ڲ������ظ��� \n ���������������ļ��鿴");}
                    var m="!";
                    if(Catt_AjaxImprotFile.params.Catt_import_break_flag=="1"){//���õ�ȡ��ʱ��Ҳ�������õ������
                        m="���Ѿ�����ȡ����";
                    }
                    if((cts[2]*1+cts[3]*1)>0){//����д�����
                        var ms="�������"+m+"\n�ܼ�["+msg.totalCount+"��]��"+desc.join("��")+"��\n\n�Ѿ����ԭ�����ļ��в��ϸ�����ݵ�Ԫ������˴�����Ϣ��ע�����ش����ļ��밴\"ȷ��\"������\"ȡ��\"��";
                        simpleAlert({
                            msg:ms, 
                            type:2, 
                            icon:"question", 
                            opts:opts,
                            fn:function(){//���ش����ļ�
                                if(Catt_AjaxImprotFile.backFun && typeof(Catt_AjaxImprotFile.backFun)=='function'){
                                    Catt_AjaxImprotFile.backFun();
                                }
                                 Catt_AjaxImprotFile.downErrorFile();
                                //Catt_AjaxImprotFile.doLinkFile(Catt_AjaxImprotFile.params.Catt_import_dataFile,Catt_AjaxImprotFile.params.Catt_import_oldFileName);
                            },
                            fnCancel:function(){
                                if(Catt_AjaxImprotFile.backFun&&typeof(Catt_AjaxImprotFile.backFun)=='function'){
                                    Catt_AjaxImprotFile.backFun();
                                }
                            }
                        });
                    }else{
                        simpleAlert({
                            msg:"�������"+m+"\n�ܼƣ�"+desc.join("��")+"��\n",
                            opts:opts,
                            fn:function(){
                                if(Catt_AjaxImprotFile.backFun&&typeof(Catt_AjaxImprotFile.backFun)=='function'){
                                    Catt_AjaxImprotFile.backFun();
                                }
                            }
                        });
                    }
				Catt_AjaxImprotFile.setWaitBar();//�ص�waitbar  
                break;
            case "2"://�����ļ���û����
                Catt_AjaxImprotFile.setWaitBar();
                simpleAlert("�ύ�������ļ��������ݣ�");
                break;
            case "11": //�Զ����߼��жϳ������ظ�����
            	var des = "������"+(msg.repeatCountInfile*1+msg.repeatCountInDB*1)+"�������������ظ����Ƿ�ֱ�Ӹ����������ݣ�\nֱ�Ӹ����밴\"ȷ��\"��ֻ����δ�ظ������ݣ��밴\"ȡ��\"�� \n\nע��������Ϻ�������ļ��鿴��ϸ��";
                simpleAlert({
                    msg:des,
                    type:2, 
                    opts:opts,
                    icon:"question", 
                    fn:function(){//����
                        if(typeof(WaitBar) != "undefined"){WaitBar.show(2);}
                        Catt_AjaxImprotFile.params.Catt_import_repeatConfirm_flag="continue";
                        fun.call(this,true);
                    },
                    fnCancel:function(){//ȡ��
                        if(typeof(WaitBar) != "undefined"){WaitBar.show(2);}
                        Catt_AjaxImprotFile.params.Catt_import_repeatConfirm_flag="break";
                        fun.call(this,true);
                    }
                });
                Catt_AjaxImprotFile.setWaitBar();
                break;
            case "3"://�����в��ϸ������,���û�ȷ���Ƿ����
                if(msg.totalCount == cts[2]){ //���ϸ��¼�����ܼ�¼����һ���ֱ࣬����ʾ���ش����ļ�
                	Catt_AjaxImprotFile.breakImport();
                	simpleAlert({
                            msg:"�������ݶ����ϸ��Ƿ����ش����ļ���\n�����밴\"ȷ��\"������\"ȡ��\"��", 
                            type:2, 
                            icon:"question", 
                            fn:function(){//���ش����ļ�
                            	
                                Catt_AjaxImprotFile.downErrorFile();
                            }
	                 });
                }else{
	                simpleAlert({
	                    msg:"�ܼ�"+msg.totalCount+"�������У�����"+cts[2]+"�в��ϸ�ȷ������������", 
	                    type:2, 
	                    icon:"question", 
	                    fn:function(){//����
	                        if(typeof(WaitBar) != "undefined"){WaitBar.show(2);}
	                        Catt_AjaxImprotFile.params.Catt_import_errConfirm_flag="continue";
	                        fun.call(this,true);
	                    },
	                    fnCancel:function(){//ȡ��
	                        Catt_AjaxImprotFile.breakImport();
	                        simpleAlert({
	                            msg:"�Ƿ����ش����ļ���\n�����밴\"ȷ��\"������\"ȡ��\"��", 
	                            type:2, 
	                            icon:"question", 
	                            fn:function(){//���ش����ļ�
	                                Catt_AjaxImprotFile.downErrorFile();
	                            }
	                        });
	                    }
	                });
                }
                Catt_AjaxImprotFile.setWaitBar();
                break;
            case "4"://�Ѿ�����ʱ������ʽ������ݵĹ����У�������ȡ��
                if(typeof(WaitBar) != "undefined"){
                    WaitBar.show(2);
                    WaitBar.setMsg("��������У����Եȣ�");
                }
                fun.call(this,true);
           		break;
	        case "5"://ȡ�����
	            Catt_AjaxImprotFile.setWaitBar();
	            break;
	        case "6"://���ݵ�����ϣ��޷�ȡ��
	            var desc=[];
	            if(cts[0]*1>0){desc.push("�޸�"+cts[0]+"��");}
	            if(cts[1]*1>0){desc.push("����"+cts[1]+"��");}
	            if(cts[2]*1>0){desc.push("����"+cts[2]+"��");}
	            if(cts[3]*1>0){desc.push("��Ч����"+cts[3]+"�У���Ч���ݿ��ܴ�������ԭ��\n 1.��ȫ�հ׵��� \n 2.�ϴ��������ļ��ڲ������ظ��� \n ���������������ļ��鿴");}
	            if((cts[2]*1+cts[3]*1)>0){
	                var ms="�������"+m+"\n�ܼ�["+msg.totalCount+"��]��"+desc.join("��")+"��\n\n�Ѿ����ԭ�����ļ��в��ϸ�����ݵ�Ԫ������˴�����Ϣ��ע�����ش����ļ��밴\"ȷ��\"������\"ȡ��\"��";
	                simpleAlert({
	                    msg:ms, 
	                    type:2, 
	                    icon:"question", 
	                    opts:opts,
	                    fn:function(){//���ش����ļ�
	                        if(Catt_AjaxImprotFile.backFun&&typeof(Catt_AjaxImprotFile.backFun)=='function'){
	                            Catt_AjaxImprotFile.backFun();
	                        }
	                        Catt_AjaxImprotFile.downErrorFile();
	                    },
	                    fnCancel:function(){
	                        if(Catt_AjaxImprotFile.backFun&&typeof(Catt_AjaxImprotFile.backFun)=='function'){
	                            Catt_AjaxImprotFile.backFun();
	                        }
	                    }
	                });
	            }else{
	                simpleAlert({
	                    msg:"������ϣ��Ѿ�����ȡ����\n�ܼ�["+msg.totalCount+"��]��"+desc.join(",")+"��\n",
	                    opts:opts,
	                    fn:function(){
	                        if(Catt_AjaxImprotFile.backFun&&typeof(Catt_AjaxImprotFile.backFun)=='function'){
	                            Catt_AjaxImprotFile.backFun();
	                        }
	                    }
	                });
		        }
		        Catt_AjaxImprotFile.setWaitBar();//�ص�waitbar
		        break;
		    case "7":
		        Catt_AjaxImprotFile.setWaitBar();//�ص�waitbar
		        simpleAlert("���ݵ���ʧ�ܣ���������У�ϵͳ����δ֪�쳣������ϵϵͳ����Э���Ų飡");
		        break;
		    case "8":
		        Catt_AjaxImprotFile.setWaitBar();//�ص�waitbar
		        if(msg.IMP_MAX_FILESIZE!=null){
		            simpleAlert("���ϴ��������ļ�̫�󣬲�Ӧ������"+msg.IMP_MAX_FILESIZE+" KB");
		        }else{
		            simpleAlert("�����ļ����Ϲ淶: "+msg.msg);
		        }
		        break;
		    case "9":
		        Catt_AjaxImprotFile.setWaitBar();//�������󣬹ص�waitbar
		        break;
		    case "10":
		    	var ms="�ܼ�"+msg.totalCount+"�������У�����"+cts[2]+"�в��ϸ���������ȡ�����롣\n\n�Ѿ����ԭ�����ļ��в��ϸ�����ݵ�Ԫ������˴�����Ϣ��ע�����ش����ļ��밴\"ȷ��\"������\"ȡ��\"��";
	            simpleAlert({
	                    msg:ms, 
	                    type:2, 
	                    icon:"question", 
	                    opts:opts,
	                    fn:function(){//���ش����ļ�
	                        if(Catt_AjaxImprotFile.backFun&&typeof(Catt_AjaxImprotFile.backFun)=='function'){
	                            Catt_AjaxImprotFile.backFun();
	                        }
	                        Catt_AjaxImprotFile.downErrorFile();
	                    }
	             });
	                    
	            Catt_AjaxImprotFile.setWaitBar();
		    	break;
		    case "12":
		    	if(msg.IMP_MAX_LINES != null){ //CURRENT_MAX_LINES
			    	//simpleAlert("�ļ�����������������" + msg.IMP_MAX_LINES + "��");
			    	var l = msg.CURRENT_MAX_LINES - msg.IMP_MAX_LINES ;
			    	simpleAlert("�ļ���������Խ�ޣ��������"+ msg.IMP_MAX_LINES +"�У���ǰʵ��"+msg.CURRENT_MAX_LINES+"�У�����"+l+"��");
			    	Catt_AjaxImprotFile.setWaitBar();
		    	}
		    	break;
		    case "13":
		    	Catt_AjaxImprotFile.setWaitBar();
		    	if(msg.isFileClosed == "1"){
		    		Catt_AjaxImprotFile.downErrorFile();
		    	}else{
			    	simpleAlert({
			    		msg:"����Ԫ����࣬Ŀǰ�Ѿ�����"+msg.errCommentCount+"������������������н����п��ܵ��´�����ע��Ϣ�޷���д���ļ������������ڴ������ϵͳ�����ķ��գ�\n�������������ļ������ļ��ڵĴ�����ע��Ϣ������ѷ������Ĵ�����ٽ��е��룬лл��",
			    		opts:{width:500},
			    		fn:function(){
			    			if(typeof(WaitBar) != "undefined"){
			                	WaitBar.show(2);
			                    WaitBar.setMsg("���ڶԷ������Ĵ�����ע��Ϣ��д���ļ������Եȣ�");
			                }
			                fun.call(this,true);
			    		}		    		
			    	});
		    	}
		    	break;
		    case "14":
		    	if(typeof(WaitBar) != "undefined"){
                	WaitBar.show(2);
                    WaitBar.setMsg("���ڶԷ������Ĵ�����ע��Ϣ��д���ļ��������ĵȺ�");
                }
                fun.call(this,true);
		    	break;
		    case "15":
		    	if(typeof(WaitBar) != "undefined"){
                	WaitBar.show(2);
                    WaitBar.setMsg("���������ļ�ǰ����۹����У������ĵȺ�");
                }
                fun.call(this,true);
		    	break;
		    default:
		        Catt_AjaxImprotFile.setWaitBar();//�ص�waitbar
		        simpleAlert("����������ţ�"+msg.iState);
		        break;
}
},

//���ش����ļ�
downErrorFile:function(){
	Catt_AjaxImprotFile.doLinkFile(Catt_AjaxImprotFile.params.Catt_import_dataFile,Catt_AjaxImprotFile.params.Catt_import_oldFileName);
},
		
//��������С����
createFileUploadpage:function(){
    var div=document.getElementById("Catt_importFile_div");
    if(!div){
        div="<div id='Catt_importFile_div' style='overflow:hidden;' class='defaultColor'>"+
        "<form method='post'>"+
        "<table width='100%' height='50px' class='formbasic'><tr><td width='25%'>ѡ�������ļ�</td>"+
        "<th><input type='text' id='Catt_importFile_input' name='Catt_importFile_input' style='width:218px'><font style='color:red;padding-left:5px;'>*</font></th></tr><table>"+
        "</form>"+
        "<table width='100%' height='50px' style='border:0;margin:0;padding:0;border:0;' class='formbasic'><tr><td style='border-top:0' style='text-align:center;'>"+ 
        "<ul class='btn_hover' style='width:360px;margin-left:35px;'>"+
        "<li onClick='Catt_AjaxImprotFile.doFileDownLoad()' style='width:90px'><a href='javascript:void(0)'><span><div>����ģ��</div></span></a></li>"+
        "<li onClick='Catt_AjaxImprotFile.doImport();'><a href='javascript:void(0)'><span><div>ȷ��</div></span></a></li>"+
        "<li onClick='Catt_AjaxImprotFile.doCloseWin()'><a href='javascript:void(0)'><span><div>ȡ��</div></span></a></li>"+
        "</ul></td></tr></table>"+
        "</div>";
        $("body").append(div);
    }else{
        //�����ϴ��ؼ�
        Catt_AjaxImprotFile.params.Catt_import_dataFile="";
        Catt_AjaxImprotFile.params.Catt_import_oldFileName="";
    }
    $("#Catt_importFile_div").window({
        title:"�ϴ������ļ�",
        closable:true,
        collapsible:false,
        minimizable:false,
        maximizable:false,
        resizable:false,
        modal:true,
        width:390,
        height:128
    });
    //���ɵ��ļ��ϴ��ؼ�
    commonUtil.initFileUpload(
    {
        eId:'Catt_importFile_input',
        sTableName:'importTemplate',
        isToLocal:1,//���ϴ�������Ӧ�÷�����
        isSaveToDB:0,//�������ļ���Ϣ��������
        limitReg:"*.xls;*.xlsx;",//����ֻ���ϴ�xls
        maxUpSize:(getSystemParams("IMP_MAX_FILESIZE",10240))/1024,//�ļ���С����,Ĭ��10M
        //directory:'import_Temp',
        displayName:"localFileName",//�ı�������ʾ�ͻ����ļ�·��
        afterUpload:function(obj){
            Catt_AjaxImprotFile.params.Catt_import_dataFile=obj.fileId;//�ļ��ϴ���ϣ��õ��ļ����ڷ���˵�URL
            Catt_AjaxImprotFile.params.Catt_import_oldFileName=obj.oldFileName;
        },
        afterClean:function(obj){
            Catt_AjaxImprotFile.params.Catt_import_dataFile="";
            Catt_AjaxImprotFile.params.Catt_import_dataFile="";
        }
    }
    );
},
//ȡ���ر�С����
doCloseWin:function(){
    $("#Catt_importFile_div").window("close");
    //�����ϴ��ؼ���ֵ
    $("#Catt_importFile_input").val("");
},
/**
		��������·��
		*/
setBasePath:function(){
    var pathname=getPathName();
    var basepath=getFullPath();
    Catt_AjaxImprotFile.basePath=basepath;
    return basepath;
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
queryTimeoutIvl:null,
params:null,//�������г�ʼ���
basePath:null//������·�� �� /SSH3
};
}();

