/**
 * ����ҳ��jquery�б�DataGrid��װʹ��
 * �޵³� 2011-12-14
 * @param {} config
 */
function jqueryDataGrid(config){
	this.tableId ='#'+ config.tableId;//div����id
	this.cols = config.cols;
	this.loadDataEvent = config.loadDataEvent;
	this.detailEvent = config.detailEvent;
	this.onClickRow = config.onClickRow;
	this.onSelect = config.onSelect;
	this.onUnselect = config.onUnselect;
	this.pkId = config.pkId;
	
	this.init = function(){
		$(this.tableId).datagrid({
			    idField:this.pkId,
				singleSelect:false,//��ѡ����Զֻ��ѡ��һ��	
				frozenColumns:[[{field:'ck',checkbox:true}]],//�����У��Ҵ���Ϊ��ѡ��
				columns:this.cols,//��ͨ������
				pagination:true,//�Ƿ���Ҫ��ҳ
				nowrap:true,
				fitColumns: true,//���Ϊtrue,�б����Բ��ðٷֱ�
				rownumbers:false,//�Զ����к�
				rowStyler:function(index,row){
			            return 'background-color:none';
			    },

				onClickRow:function(rowIndex, rowData){
					if(null!=config.onClickRow&&''!=config.onClickRow&&'undefined'!=config.onClickRow){
				    	 config.onClickRow.call(this,rowIndex, rowData);
					}
				},
				onSelect:function(rowIndex, rowData){
					if(null!=config.onSelect&&''!=config.onSelect&&'undefined'!=config.onSelect){
				    	 config.onSelect.call(this,rowIndex, rowData);
					}
				},	
				onUnselect:function(rowIndex, rowData){
					if(null!=config.onUnselect&&''!=config.onUnselect&&'undefined'!=config.onUnselect){
				    	 config.onUnselect.call(this,rowIndex, rowData);
					}
				},	
				onDblClickRow:function(){
					if(null!=config.detailEvent&&''!=config.detailEvent&&'undefined'!=config.detailEvent){
				    	 config.detailEvent.call(this,'');
					}
				},
				onLoadSuccess:function(){
					if($(this).datagrid('getRows').length>0){
						$(this).datagrid('unselectAll');
						$(this).datagrid('clearSelections');
						$(this).datagrid('selectRow',0);
					}
				}
			});
			//�Ȼ�ȡ�б����  
			var p = $(this.tableId).datagrid('getPager');      
			//���÷�ҳ�ؼ�  
			$(p).pagination({
				 pageSize: 30,//ÿҳ��ʾ�ļ�¼������������Ĭ��Ϊ10 
				 pageNumber:1,//��ǰҳ��Ĭ��1
				 showPageList:false,//�Ƿ���ʾѡ���¼�������� ,Ĭ��true
				 onChangePageSize:function(limit){ }, 
				 beforePageText: '��',//ҳ���ı���ǰ��ʾ�ĺ���           
				 afterPageText: 'ҳ    �� {pages} ҳ',           
				 displayMsg: '��ǰ��ʾ�� {from} - {to} ����¼   �� {total} ����¼', 
				 onSelectPage:function(pageNo,limit){//����ϡ���һҳʱ�������¼�
			 	   if(null!=config.loadDataEvent&&''!=config.loadDataEvent&&'undefined'!=config.loadDataEvent){
				 	 	config.loadDataEvent.call(this,'');
				 	   }
				 },
				 showRefresh:false//����ʾˢ�°�ť��Ĭ��true         
			 }); 
	}
	this.init();
}

/**
 * �򵥲�����ҳ��jquery�б�DataGrid��װʹ��
 * �޵³� 2011-12-20
 * @param {} config
 */
function simpleDataGrid(config){
	this.pkId = config.pkId;
	this.tableId ='#'+ config.tableId;//div����id
	this.cols = config.cols;
	this.onClickRow = config.onClickRow;
	this.onSelect = config.onSelect;
	this.loadDataEvent = config.loadDataEvent;
	this.detailEvent = config.detailEvent;
	this.init = function(){
		$(this.tableId).datagrid({
			    idField:this.pkId,
				singleSelect:false,//��ѡ����Զֻ��ѡ��һ��	
				frozenColumns:[[{field:'ck',checkbox:true}]],//�����У��Ҵ���Ϊ��ѡ��
				columns:this.cols,//��ͨ������
				pagination:false,//�Ƿ���Ҫ��ҳ
				nowrap:true,
				fitColumns: true,//���Ϊtrue,�б����Բ��ðٷֱ�
				rownumbers:false,//�Զ����к�
				onClickRow:function(rowIndex, rowData){
					if(null!=config.onClickRow&&''!=config.onClickRow&&'undefined'!=config.onClickRow){
				    	 config.onClickRow.call(this,rowIndex, rowData);
					}
				},
				onSelect:function(rowIndex, rowData){
					if(null!=config.onSelect&&''!=config.onSelect&&'undefined'!=config.onSelect){
				    	 config.onSelect.call(this,rowIndex, rowData);
					}
				},	
				onDblClickRow:function(){
					if(null!=config.detailEvent&&''!=config.detailEvent&&'undefined'!=config.detailEvent){
				    	 config.detailEvent.call(this,'');
					}
				},
				onLoadSuccess:function(){
					if($(this).datagrid('getRows').length>0){
						$(this).datagrid('unselectAll');
						$(this).datagrid('clearSelections');
						$(this).datagrid('selectRow',0);
					}
				}
			});
	}
	this.init();
}

/**
 * jquery���б�treeGrid��װʹ��
 * �޵³� 2011-01-12
 * @param {} config
 */
function jqueryTreeGrid(config){
	this.tableId ='#'+ config.tableId;//div����id
	this.cols = config.cols;
	this.loadDataEvent = config.loadDataEvent;
	this.detailEvent = config.detailEvent;
	this.onClickRow = config.onClickRow;
	this.onSelect = config.onSelect;
	this.pkId = config.pkId;
	this.treeField = config.treeField;
	this.pagination = config.pagination;
	
	this.init = function(){
		$(this.tableId).treegrid({
			    idField:this.pkId,//�����Ա����У����Լӿ����ļ����ٶ�
			    treeField:this.treeField,//�����Ա����У����ڵ㣬������ʾ����״
			    singleSelect:false,//��ѡ����Զֻ��ѡ��һ��	
				fitColumns: true,//�ٷֱ�
				rownumbers: false,//���к�
				animate:true,//����
				collapsible:false,
				showFooter:false,
				checkChildren:true,
				checkParent:false,
				loadMsg:'���ڼ������ݣ����Ե�...',
				columns:this.cols,
				frozenColumns:[[{field:'ck',checkbox:true}]],
				pagination:this.pagination,//�Ƿ���Ҫ��ҳ
				
				onClickRow:function(rowIndex, rowData){
					if(null!=config.onClickRow&&''!=config.onClickRow&&'undefined'!=config.onClickRow){
				    	 config.onClickRow.call(this,rowIndex, rowData);
					}
				},
				onSelect:function(rowIndex, rowData){
					if(null!=config.onSelect&&''!=config.onSelect&&'undefined'!=config.onSelect){
				    	 config.onSelect.call(this,rowIndex, rowData);
					}
				},				
				onDblClickRow:function(){
					if(null!=config.detailEvent&&''!=config.detailEvent&&'undefined'!=config.detailEvent){
				    	 config.detailEvent.call(this,'');
					}
				},
				onLoadSuccess:function(){
				}
		});
		 //�����Ҫ��ҳ
		  if(this.pagination){
				//�Ȼ�ȡ�б����  
				var p = $(this.tableId).treegrid('getPager');      
				//���÷�ҳ�ؼ�  
				$(p).pagination({
					 pageSize: 30,//ÿҳ��ʾ�ļ�¼������������Ĭ��Ϊ10 
					 pageNumber:1,//��ǰҳ��Ĭ��1
					 showPageList:false,//�Ƿ���ʾѡ���¼�������� ,Ĭ��true
					 onChangePageSize:function(limit){ }, 
					 beforePageText: '��',//ҳ���ı���ǰ��ʾ�ĺ���           
					 afterPageText: 'ҳ    �� {pages} ҳ',           
					 displayMsg: '��ǰ��ʾ�� {from} - {to} ����¼   �� {total} ����¼', 
					 onSelectPage:function(pageNo,limit){//����ϡ���һҳʱ�������¼�
				 	   if(null!=config.loadDataEvent&&''!=config.loadDataEvent&&'undefined'!=config.loadDataEvent){
					 	 	config.loadDataEvent.call(this,'');
					 	   }
					 },
					 showRefresh:false//����ʾˢ�°�ť��Ĭ��true         
				 }); 
			 }
	}
	this.init();
}

/**
 * ���ߣ��޵³�
 * ʱ�䣺2012-1-13
 * jquery�����б�Ĺ�����
 */
var treeGridUtil = {
	/**
	 * ��ȡָ���ڵ�����������ӽڵ�
	 * option.tableId   ���б��ID
	 * option.id    ��Ҫ���ص��ֶ�,ע��id��ֻ֧�ַ������õ�idField
	 */
	getChildren : function(option){
		var returnName= option.id;
		var returnValue ='';
		var node = $(option.tableId).treegrid('getSelected');
					if (node){
						var nodes = $(option.tableId).treegrid('getChildren', node[returnName]);
					} else {
						var nodes = $(option.tableId).treegrid('getChildren');
					}
					for(var i=0; i<nodes.length; i++){
						returnValue += node[returnName] + ',' ;
					}
					return returnValue.substring(0,returnValue.lastIndexOf(','));
		},
	getParent : function(option){
		var returnName= option.id;
		var returnValue ='';
		var node = $(option.tableId).treegrid('getSelected');
					if (node){
						var nodes = $(option.tableId).treegrid('getParent', node[returnName]);
					} else {
						var nodes = $(option.tableId).treegrid('getParent');
					}
					for(var i=0; i<nodes.length; i++){
						returnValue += node[returnName] + ',' ;
					}
					return returnValue.substring(0,returnValue.lastIndexOf(','));
		},
	//��ȡѡ����
	 getSelected:	function (option){
			return $(option.tableId).treegrid('getSelected');
		},
	//��ȡ����ѡ����
	 getSelections: function (option){
			return $(option.tableId).treegrid('getSelections');
		}
}
