/**
 * 带分页的jquery列表DataGrid封装使用
 * 罗德成 2011-12-14
 * @param {} config
 */
function jqueryDataGrid(config){
	this.tableId ='#'+ config.tableId;//div容器id
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
				singleSelect:false,//复选框永远只会选中一行	
				frozenColumns:[[{field:'ck',checkbox:true}]],//冻结列，且此列为复选框
				columns:this.cols,//普通列配置
				pagination:true,//是否需要分页
				nowrap:true,
				fitColumns: true,//如果为true,列表属性采用百分比
				rownumbers:false,//自动序列号
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
			//先获取列表对象  
			var p = $(this.tableId).datagrid('getPager');      
			//设置分页控件  
			$(p).pagination({
				 pageSize: 30,//每页显示的记录条数，不设置默认为10 
				 pageNumber:1,//当前页，默认1
				 showPageList:false,//是否显示选择记录数下拉框 ,默认true
				 onChangePageSize:function(limit){ }, 
				 beforePageText: '第',//页数文本框前显示的汉字           
				 afterPageText: '页    共 {pages} 页',           
				 displayMsg: '当前显示第 {from} - {to} 条记录   共 {total} 条记录', 
				 onSelectPage:function(pageNo,limit){//点击上、下一页时，触发事件
			 	   if(null!=config.loadDataEvent&&''!=config.loadDataEvent&&'undefined'!=config.loadDataEvent){
				 	 	config.loadDataEvent.call(this,'');
				 	   }
				 },
				 showRefresh:false//不显示刷新按钮，默认true         
			 }); 
	}
	this.init();
}

/**
 * 简单不带分页的jquery列表DataGrid封装使用
 * 罗德成 2011-12-20
 * @param {} config
 */
function simpleDataGrid(config){
	this.pkId = config.pkId;
	this.tableId ='#'+ config.tableId;//div容器id
	this.cols = config.cols;
	this.onClickRow = config.onClickRow;
	this.onSelect = config.onSelect;
	this.loadDataEvent = config.loadDataEvent;
	this.detailEvent = config.detailEvent;
	this.init = function(){
		$(this.tableId).datagrid({
			    idField:this.pkId,
				singleSelect:false,//复选框永远只会选中一行	
				frozenColumns:[[{field:'ck',checkbox:true}]],//冻结列，且此列为复选框
				columns:this.cols,//普通列配置
				pagination:false,//是否需要分页
				nowrap:true,
				fitColumns: true,//如果为true,列表属性采用百分比
				rownumbers:false,//自动序列号
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
 * jquery树列表treeGrid封装使用
 * 罗德成 2011-01-12
 * @param {} config
 */
function jqueryTreeGrid(config){
	this.tableId ='#'+ config.tableId;//div容器id
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
			    idField:this.pkId,//此属性必须有，可以加快树的加载速度
			    treeField:this.treeField,//此属性必须有，树节点，才能显示成树状
			    singleSelect:false,//复选框永远只会选中一行	
				fitColumns: true,//百分比
				rownumbers: false,//序列号
				animate:true,//动画
				collapsible:false,
				showFooter:false,
				checkChildren:true,
				checkParent:false,
				loadMsg:'正在加载数据，请稍等...',
				columns:this.cols,
				frozenColumns:[[{field:'ck',checkbox:true}]],
				pagination:this.pagination,//是否需要分页
				
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
		 //如果需要分页
		  if(this.pagination){
				//先获取列表对象  
				var p = $(this.tableId).treegrid('getPager');      
				//设置分页控件  
				$(p).pagination({
					 pageSize: 30,//每页显示的记录条数，不设置默认为10 
					 pageNumber:1,//当前页，默认1
					 showPageList:false,//是否显示选择记录数下拉框 ,默认true
					 onChangePageSize:function(limit){ }, 
					 beforePageText: '第',//页数文本框前显示的汉字           
					 afterPageText: '页    共 {pages} 页',           
					 displayMsg: '当前显示第 {from} - {to} 条记录   共 {total} 条记录', 
					 onSelectPage:function(pageNo,limit){//点击上、下一页时，触发事件
				 	   if(null!=config.loadDataEvent&&''!=config.loadDataEvent&&'undefined'!=config.loadDataEvent){
					 	 	config.loadDataEvent.call(this,'');
					 	   }
					 },
					 showRefresh:false//不显示刷新按钮，默认true         
				 }); 
			 }
	}
	this.init();
}

/**
 * 作者：罗德成
 * 时间：2012-1-13
 * jquery树形列表的工具类
 */
var treeGridUtil = {
	/**
	 * 获取指定节点下面的所有子节点
	 * option.tableId   树列表的ID
	 * option.id    需要返回的字段,注此id暂只支持返回设置的idField
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
	//获取选中行
	 getSelected:	function (option){
			return $(option.tableId).treegrid('getSelected');
		},
	//获取所有选中行
	 getSelections: function (option){
			return $(option.tableId).treegrid('getSelections');
		}
}
