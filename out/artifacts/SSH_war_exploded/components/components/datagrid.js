/**
*表格控件
*/
(function($) { 
	$.fn.datagrid= function(opt,options) {
		if(typeof opt != 'object'){
			if(opt == 'loadData'){
				var opts = $(this).data(); //得到原始定义
				opts.datas=options; //要呈现的数据
				loadData(opts,this);
			}else if(opt=='options'){
				 return $(this).data();
			}else if(opt=='getSelected'){
				return getSelected(this);				
			}else if(opt=='getSelections'){
				return getSelections($(this));				
			}else if(opt=='getRowIndex'){
				 return getRowIndex(this, options);
			}else if(opt=='getRowByIndex'){
				return getRowByIndex(this,options)
			}else if(opt=='getRowById'){
				return getRowById(this, options);
			}else if(opt=='getRows'){
				 return getRows(this);
			}else if(opt=='selectRecord'){
				 selectRecord(this, options);
			}else if(opt=='selectRow'){
				 selectRow(this, options);
			}else if(opt=='checkRecord'){
				 checkRecord(this, options);
			}else if(opt=='checkRow'){
				 checkRow(this, options);
			}else if(opt=='isChecked'){
				 return isChecked(this, options);
			}else if(opt=='reSize'||opt=='resize'){
				 reSize(options, this);		
			}else if(opt=='getPager'){
				 return getPager(this);
			}else if(opt=='insertRow'){
				var ids = getSelectIds(this);
				var opts = $(this).data(); //得到原始定义
				var datas = getRows(this);
				datas.splice(options.index,0,options.row); //要呈现的数据
				loadData(opts,this);
				checkRecord(this,{id:ids,check:true})
				selectRow(this,options.index);
			}else if(opt=='updateRow'){
				var ids = getSelectIds(this);
				var opts = $(this).data(); //得到原始定义
				var datas = getRows(this);
				$.extend(datas[options.index], options.row); 
				loadData(opts,this);
				checkRecord(this,{id:ids,check:true});
				selectRow(this,options.index);
			}else if(opt=='appendRow'){
				var ids = getSelectIds(this);
				var opts = $(this).data(); //得到原始定义
				var datas = getRows(this);
				//datas.push(options);
				//datas = $.merge(datas,options);
				datas = datas.concat(options);
				opts.datas=datas;
				loadData(opts,this);
				checkRecord(this,{id:ids,check:true});
				selectRow(this,datas.length-1);
			}else if(opt=='deleteRow'){
				var ids = getSelectIds(this);
				var opts = $(this).data(); //得到原始定义
				var datas = getRows(this);
				datas.splice(options,1); //要呈现的数据
				loadData(opts,this);
				checkRecord(this,{id:ids,check:true});
				//selectRow(this,options.index);
			}else if(opt=='getColumnFields'){
				
			}else if(opt=='hideColumn'){
				
			}else if(opt=='showColumn'){
				
			}else if(opt=='getColumnOption'){
				
			}else if(opt=='getHeadDefine'){
				return getHeadDefine(this);
			}
		}else{
			loadData(opt, this);
		}
	};
	

	function loadData(options, obj){
		var div = $(obj); 
		
	    div.data(options); //绑定原始定义数据
		var columns = options.columns; //列定义
		
		//加1空列, 用于列宽较少时，自动填充满表格, 不留白
		var cols = columns[columns.length-1];
		var f = cols[cols.length-1];
		if(f.field){
			cols.push({field:'',title:'',width:0});
		}
		
		var checkbox=isShowCheckBox(options); //是否允许多选
		var datas = options.datas; //数据
		
		var headData = getThead(columns);
		options.headData = headData;
		options.headDataLineArr = headData.lineArr;
		var levelDefine = getRowLevelDefineds(headData.lastLeaf); //跨行级别定义
		options.hasLevelDefine = levelDefine.length>0;
		
		var tby = div.find('tbody');
		var h = tby.get(0)? $(tby).height():(options.height||div.height());
		h= (h<=0 || options.height=="auto") ? "auto" : h+"px";
		var w = tby.get(0)? $(tby).width():div.width();
		
		//生成列表html代码
		var html = "";
		html += '<table class="attachTable'+ (headData.lineArr.length>1 || levelDefine.length>0 ?' attachTableMixHead' : '') +'">';
		html += createHead(options, headData.lineArr, checkbox); //拼接表头
		html += createTbody(h, w, datas, headData.lastLeaf, checkbox, options, levelDefine);//拼接表体
		html += '</table>';
		div.html(html);
		
		//鼠标hover行事件
		if(levelDefine.length==0){//没有跨行的情况
			div.find("tbody tr").hover(
				function(){$(this).addClass("trhover");},
				function(){$(this).removeClass("trhover");}
			);
		}
		//表头点击事件
		div.find("thead th div a").bind("click",function(){
			var lineArr = options.headDataLineArr;
			var div = $(this).parent();
			var r = div.attr("r");
			var c = div.attr("c");
			
			var field = lineArr[r][c];
			if(field.headClick){
				field.headClick(field, options);
			}
		});
		//表头排序
		div.find("thead th div").bind("click", function(){
			var lineArr = options.headDataLineArr;
			var cdiv = $(this);
			var p = cdiv.find(".sort");
			if(p.length==0){return;}
			
			var ps = div.find("thead th div .sort");
			$(ps).hide();
			p.show();
			
			var r = cdiv.attr("r");
			var c = cdiv.attr("c");
			var boo =  p.hasClass("sort_asc"); 
			var st = boo ? "desc" : "asc";
			p.removeClass(boo?"sort_asc":"sort_desc");
			p.addClass(boo?"sort_desc":"sort_asc");
			var field = lineArr[r][c];
			field.currentSortCase = st; //当前排序方式，记录下来，跨行分组列表时要用到。
			options.currentSortField = field; //记录当前排序的列
			
			if(options.remoteSort){//服务端排序
				options.remoteSort(field.field, st, field);
			}else{
				localSort(options, obj);
			}
		}); 
		//点击行
		div.find("tbody tr").bind("click",function(){
			select(div, this); //设置选中
			if(options.onClickRow){ //单击事件
				var index=$(this).attr("index")//div.find("."+selectCss).attr("index");
				index=parseInt(index);
				var data=getRowByIndex(div,index);	
				options.onClickRow.call(this,index,data);
			}
		});
		//双击行
		div.find("tbody tr").bind("dblclick",function(){
			if(options.onDblClickRow){
				//获取被选中的行
				var index=$(this).attr("index");//div.find("."+selectCss).attr("index");
				index=parseInt(index);
				var data=getRowByIndex(div,index);	
				options.onDblClickRow.call(this,index,data);
			}
		});
		//tbody 与 thead 滚动条绑定
		div.find('tbody').unbind('scroll').scroll(function(){
			div.find('thead').scrollLeft($(this).scrollLeft());
		});
		//重新加载复选框样式
		if(checkbox){ 
			div.find('.formStyle input').jqTransCheckBox(); 
		} 
		//点击全选框
		getCheckBox(div.find("thead")).click(function(){
			if(optCheckClass($(this), 3)){
				optCheckClass(getCheckBox(div), 1);
				if(options.onSelectAll){options.onSelectAll.call(this,datas);} //事件回调
			}else{
				optCheckClass(getCheckBox(div), 2);
				if(options.onUnselectAll){options.onUnselectAll.call(this,datas);} //事件回调
			}
		});
		//点击行选复选框
		getCheckBox(div.find("tbody")).click(function(){
			setTitleCheckSate(div);
			var index = $(this).closest("tr").attr("index");
			var row = datas[index];
			if(optCheckClass($(this), 3)){
				if(options.onSelect){options.onSelect.call(this, index, row);} //事件回调
			}else{
				if(options.onUnselect){options.onUnselect.call(this, index, row);} //事件回调
			}
		});
		//分页栏初始化
		if(options.pagination && !$(obj).next().hasClass('pagination')){
			div.after("<div id='pagination' class='pagination'><div>");
		}
		//自适应
		setLastColumnWidth(div);
		setTheadWidth(options, obj);
		tableResizeColumn($(div).find(".attachTable"));
	}
	
	/**表头运算，构造复合多级表头*/
	function getThead(cols){
        var allArr = []; //所有层级的字段定义平铺
     	var level = 0;  //平铺过程中的当前对象的层级
     	var maxLevel = 0; //最大层级
     	
     	var lastLeaf = []; //所有没有子节点的节点， 是后续<tbody>画数据要参考的字段与顺序
     	var lineArr = []; //二维数组，每一行该画哪些单元格

     	var tempArr = [];
     	for(var i=0; i<cols.length; i++){
     		for(var j=0; j<cols[i].length; j++){
     			tempArr.push(cols[i][j]);
     		}
     	}
     	
     	//2个内部方法执行
     	firstReBuildColumns(tempArr, 'f'); //第1次重构，得出allArr
     	secondReBuildColumns(); //第二次重构 得出lastLeaf,lineArr
	
     	/* 
     	第一次重构, 得allArr, maxLevel
     	path: 层级标识
     	hasLeaf: 当前节点是否还有此节点
     	level:当前节点属于第几层级
     	var allArr=[
	     	{path:'f_1', hasLeaf=1, level=1},
	     		{path:'f_1_1', hasLeaf=1, level=2},
	     			{path:'f_1_1_1', hasLeaf=0, level=3},
	     			{path:'f_1_1_2',  hasLeaf=0, level=3},
	     		{path:'f_1_2', hasLeaf=0, level=2},	     	
	     	{path:'f_2', hasLeaf=0, level=1},
     	];

     	参数：tempArr 原始定义。  path: 给个path前缀，任意非数值文本即可
     	*/
		function firstReBuildColumns(tempArr, path){
		    	level ++;
		    	for(var i=0; i< tempArr.length; i++){
		    		var field = tempArr[i];
		    		if(field.hidden && field.hidden.toString().toLowerCase()=="true"){continue;}  //隐藏字段不考虑
		    		
		    		field.level = level;
		    		field.path = path+"_"+i;
		     		allArr.push(field);
		     		if(field.children && field.children.length>0){
		     		    field.hasLeaf=1;
		     		    firstReBuildColumns(field.children, field.path);
		    	    }
		     	}
		     	maxLevel = level > maxLevel ? level : maxLevel;
		     	level--;
		}
	
	    /*第二次重构 得出lastLeaf,lineArr*/
		function secondReBuildColumns(){
		    	for(var i=0; i<allArr.length; i++){
		    		var field = allArr[i];
		    		var path = field.path;
		    		
		    		var colspan = 0;
		    		for(var j = i+1; j<allArr.length; j++){
		    			var nextField = allArr[j];
		    			var nextPath = nextField.path;
		    			//当子节点归属于当前父节点，且自己没有子节点，说明是当前父节点的 子列。所以colspan++
		    			
		    			if(nextPath.indexOf(path+"_")>=0 && !nextField.hasLeaf){
		    				colspan++;
		    			}
		    		}
		    		field.colspan=colspan; //得出当前节点该跨的列数
	
		    		if(!field.hasLeaf){ //当前节没有子节点，则以最大层级-当前节点层级 得出跨行数。
		    			field.rowspan = maxLevel-field.level+1;
		    			lastLeaf.push(field);
		    		}
	
		    		//当前节点属于第几层，则放入二维数组对应下标的子数组内。
		    		var index = field.level-1;
		    		lineArr[index] = lineArr[index] ?  lineArr[index] : [];
		    		lineArr[index].push(field);
		    	}
		}
		return {lastLeaf:lastLeaf,lineArr:lineArr};
	}
	
	/**生成表头*/
	function createHead(options, lineArr, checkbox){
		var html = [];
		
		//是否显示表头
		var isShowHeader = !(options.showHeader+"" == "0" || options.showHeader+"" == "false") ;
		html.push('<thead style="overflow:hidden;float:left;'+(isShowHeader?'':'display:none;')+'" >');
		for(var i=0; i<lineArr.length; i++ ){
			html.push('<tr>');
			
			if(i==0 && checkbox){//如果有复选框，加在第1行
				html.push('<th style="width:10px" rowspan='+lineArr.length+'><div class="formStyle"><input type="checkbox" name="chbox"></div></th>');
			}else if(i==0 && options.hasLevelDefine){ //i==0 && 
				html.push('<th style="width:2px" rowspan='+lineArr.length+'>&nbsp;</th>');
			}
			for(var j=0; j<lineArr[i].length; j++ ){
				var field = lineArr[i][j];
				var colspan= field.colspan ? ' colspan='+field.colspan  : ' ';
				var rowspan= field.rowspan ? ' rowspan='+field.rowspan  : ' ';
				
				var style = [];
				style.push('style="');
				
				if(!field.hasLeaf){
					var initialWidth =(field.width+"")?field.width:200; 
					var width= initialWidth+'px';
					style.push("width:" + width+";");
					style.push(field.width==0?"padding:0;":"");
				}
				style.push('"'); 
				
				var text = field.headClick ? '<a '+(field.headClickTitle?'title="'+field.headClickTitle+'"':"")+'>'+field.title+'</a>' : field.title;
				var currentSortCase = field.currentSortCase; //当前排序方式，记录下来，跨行分组列表时要用到。
				var currentSortField = options.currentSortField ; //记录当前排序的列
				
				var boo = field.field == (currentSortField ? currentSortField.field:"");
				var sortCss = currentSortCase ? "sort_"+currentSortCase : "sort_asc";
				var sort = field.sortable ? '<span class="sort '+sortCss+'" '+(boo?'style="display:inline;"':'')+'></span>':'';
				var cursor = field.sortable ? 'cursor: pointer;' : '';
				var eventAClass= (field.headClick||field.sortable)?'class="event_a"':'';
				html.push('<th '+ style.join('') + colspan + rowspan+'><div '+eventAClass+' r="'+i+'" c="'+j+'" style="width:'+(colspan.length>1 ? "100%" : width)+';'+cursor+'">'+text+sort+'</div></th>');
			}
			html.push('</tr>');
		}
		html.push('</thead>');
		
		return html.join('');
	}
	
	/**得到要跨行分组的字段*/
	function getRowLevelDefineds(columns){
		var levelDefine = [];
    	for(var i=0; i< columns.length; i++){
    		var field = columns[i];
    		if(field.rowLevel && !isNaN(field.rowLevel)){
    			levelDefine.push(field);
    		}
    	} 
    	levelDefine.sort(function(a,b){ return a.rowLevel - b.rowLevel;});
    	return levelDefine;
	}
	
	/**生成表体*/
	function createTbody(h, w, datas, columns, checkbox, options, levelDefine){
		
		var html=[];
		html.push('<tbody style="overflow:auto;float:left;height:'+h+';width:'+w+'px;">');
		//拼接数据行
		if(datas){
			datas = datasSort(datas, levelDefine, options);//全量数据排序
			var flgs =getRowspanFlg(datas, levelDefine, options);//算出某列在第几行要跨行，跨多少行
			options.datas=datas;//行的顺序可能发生了变化，要以新数据为准
			
			for(var i=0;i<datas.length;i++){
				
				html.push('<tr class="formStyle" index="'+i+'"  dataId="'+datas[i][options.idField]+'">');
				if(checkbox){
					html.push('<td width="10px"><div class="formStyle"><input index="'+i+'" type="checkbox" name="chbox"  '
							+ (datas[i].disabled?"disabled":"")  +'  ></div></td>');
				}else if(levelDefine.length>0){
					html.push('<td width="2px">&nbsp;</td>'); //用于标识行选
				}
				
				var tdDivClass = options.nowrap==false ? "div_show_more" : "div_show_less";
				for(var j=0;j<columns.length;j++){
					var col =columns[j];
					if(col.hidden && col.hidden.toString().toLowerCase()=="true"){continue;}
					
					var fName = col.field;
	    			var boo1 = flgs[fName]; //是跨行列
	    			var boo = boo1 && flgs[fName][i+'']; //是跨行行
	    			var rowspan = boo ? flgs[fName][i+''] : 0;
	    			if(!(!boo1 || rowspan>0)){continue;} //被跨行的列，不必绘制
					
					var initialWidth =(col.width+"")?col.width:200; 
					var width= initialWidth+'px';
					var padding = col.width==0 ?"padding:0;":"";
					
					html.push('<td '+ (rowspan>1 ? 'rowspan="'+rowspan+'"' : '') +'  style="'+padding+'width:'+width +' ');
					if(col.cls){ html.push('class="'+col.cls+'" '); }
					var align=col.align?col.align:"left";
					html.push(";text-align:"+align);
					html.push('">');
					
					html.push('<div class="'+tdDivClass+'" contenteditable="'+(col.contenteditable?col.contenteditable:false)+'"  onblur="resetEditValueToDatas(this)" field="'+col.field
							+'" style="width:'+width);
					if(align=='center'){
						html.push(';margin-left:auto;margin-right:auto;');
					}else if(align=='right'){
						html.push(';margin-left:auto;');
					}
					
					html.push('"');
					var dataValue = datas[i][col.field]?datas[i][col.field]:'';
					if(col.formatter){dataValue=col.formatter(dataValue,datas[i],i);}
					
					if(col.displayTip != false && !col.formatter){
						html.push('title="'+dataValue+'" ');
					}
					html.push('>');
					html.push(dataValue);
					html.push('</div>');
					html.push('</td>');
        			
				}
				html.push('</tr>');
			}
    	}
		html.push('</tbody>');
		return html.join('');
		
		
		//全量数据排序  --内部方法第1层
		function datasSort(datas, levelDefine, options){
			/* 要确定针对第几层排序。
			 [
				 { groupName:"a",children:[
									{groupName:"a1",children:[0,1]},
									{groupName:"a2",children:[2,3]};
									{groupName:"a3",children:[4]} ]
				 },...
			 ] 
			 * */
			
			/*分组, 将数据转换成上方格式的--内部方法第2层
			 * dts 原数据
			 * 当前处理的分组级别
			 * 分组级别定义
			 * */
			var doGroup =function(dts, level, levelDefine){
				if(level>=levelDefine.length){return dts;}
				
				var fName = levelDefine[level].field; //分组字段
				var index = 0; //下标位置
				var v_i = {}; //组名与下标映射
				var i_v = {}; //下标与组名映射
				var groupArr = [];//分组二维数组，上边两个变量说的下标即是此数组第1维的下标
				var tree = []; //最终构造后的结果
				
				for(var j=0; j<dts.length; j++){
					var line = dts[j];
					var groupName = line[fName];
					var arr = (v_i[groupName]!=null &&   v_i[groupName]!=undefined) ?  groupArr[v_i[groupName]] : null;
					
					if(!arr){
						arr = []; 
						v_i[groupName]=index;
						i_v[index]=groupName;
						groupArr.push(arr);
						index++;
					}
					arr.push(line);
				}
				
				for(var i=0 ; i<groupArr.length; i++){
					var groupName = i_v[i];
					++level;
					if(level<=levelDefine.length){
						var children = doGroup(groupArr[i], level, levelDefine);  //递归进行下层分组
						tree.push({groupName:groupName, children:children}); //组装成设计的数据格式
					}else{
						tree.push(groupArr[i]);
					}
					--level;
				}
				
				return tree;
			};
			
			/*排序 --内部方法第2层
			 * tree 已分组的树型数据
			 * flg true 表示不需按分组排序，直接全量排序，tree里的数据也是全量平铺的数据
			 * */
			var sort = function(tree, flg){
				
				/* 找到对应层次的数据进行排序 --内部方法第3层
				 * currentLevl 当前层级
				 * toLevel 应该排序的层级
				 * tempTree 要排序的数组
				 * noLeveField 要排序的字段，此参数在排序最子级时才用得上
				 * --*/
				var getLevelData = function (currentLevl, toLevel, tempTree, noLeveField){
					if(currentLevl == toLevel){ //到达对应层级才真正做排序
						tempTree.sort(function (o1, o2) {
				    		var x= noLeveField ? o1[noLeveField.field] : o1.groupName;
				    		x =  fieldType ?  (isNaN(x) ? x : x*1)  :  x;
				    		var y= noLeveField ? o2[noLeveField.field] : o2.groupName;
				    		y =  fieldType ?  (isNaN(y) ? y : y*1)  :  y;
				    		
				    	    if (x > y) {
				    	        return sortCase ? 1 : -1;
				    	    } else if (x < y) {
				    	        return sortCase ? -1 : 1;
				    	    } else {
				    	        return 0;
				    	    }
				    	});
					}else{ //去到对应层级
						currentLevl++;
						for(var i=0;i<tempTree.length; i++){
							getLevelData(currentLevl, toLevel, tempTree[i].children, noLeveField); //递归
						}
						currentLevl--;
					}
				};
				
				/*执行*/
				var field = options.currentSortField //当前排序字段
				//!field  说明没有排序字段，不需排序
				if(field){
					//当前排序方式
					var sortCase = field.currentSortCase == "asc"; //true 升序， false降序
					var fieldType = field.fieldType == "1"; //1表示按数值运算排序
					
					if(flg){//直接全量排序
						tree.sort(function (o1, o2) {
				    		var x= o1[field.field]; x =  fieldType ?  (isNaN(x) ? x : x*1)  :  x;
				    		var y= o2[field.field]; y =  fieldType ?  (isNaN(y) ? y : y*1)  :  y;
				    	    if (x > y) {
				    	        return sortCase ? 1 : -1;
				    	    } else if (x < y) {
				    	        return sortCase ? -1 : 1;
				    	    } else {
				    	        return 0;
				    	    }
				    	});
					}else{ //对分组后的数据进行排序
						var toLevel = -1;  //对应的排序层级
						for(var i=0; i<levelDefine.length; i++){
							if(field.field == levelDefine[i].field){
								toLevel = i;
							}
						}
						//排序
						var currentLevl = 0;
						var boo = toLevel == -1; //-1表示不是针对某个组名字段排序，而是针对最子级数据排序
						toLevel = boo ? levelDefine.length : toLevel; //如果是最子级，去定义到最子级
						var noLeveField = boo ? field : null; //最子级，得知道当前排序的是哪个字段，后边要用到字段名
						getLevelData(currentLevl, toLevel, tree, noLeveField);
					}
				}
				
				return tree;
			}
			
			//将排好序的数据取出平铺 -- 内部方法第2层
			var getLastDatas = function(tr){
				//取叶节点  -- 内部方法第3层
				var  getLeaf= function(tr){
					for(var i=0; i<tr.length; i++ ){
						var t = tr[i];
						if( t.children){
							getLeaf(t.children);  //递归
						}else{
							allArr.push(t); //取出
						}				
					}
				};
				//执行
				var allArr=[];
				getLeaf(tr);
				return allArr;
			};
			
			
			/*执行*/
			/*
		 	排序case1: 优先分组，且分组以组名出现的先后顺序为准， 然后组内再按指定的某字段排序
		 	排序case2：优先按指定的某字段全量排序，然后再分组，组名以出现的先后顺序呈现*/
			var lastDatas;
			if(levelDefine.length>0){ //有分组定义
				if(options.sortCase=="2"){
					var tree = sort(datas,true);
					tree = doGroup(datas, 0, levelDefine);
					lastDatas = getLastDatas(tree);
				}else{
					var tree = doGroup(datas, 0, levelDefine);
					tree = sort(tree);
					lastDatas = getLastDatas(tree);
				}
			}else{
				lastDatas = sort(datas, true); //sort方法内部会判断是否真的需要排
			}
			return lastDatas;
		}
		
		//算出某列在第几行要跨行，跨多少行getRowspanFlg
		function getRowspanFlg(datas, levelDefine ){
	    	var flgs = {};
	    	var len = datas.length;
	    	for(var k=0; k<levelDefine.length; k++){
				
	    		var fName = levelDefine[k].field;
	    		var temp = {};
	    		flgs[fName]= temp;
	    		
	    		var rowIndex = 0;
	    		for(var i=0; i<len; i++){
	    			var nextIndex = i+1;
					var row1 = datas[i];
					var row2 = nextIndex == len ? []:datas[nextIndex];
					
	    			var v1 = row1[fName];
	    			var v2 = row2[fName];
	    			
	    			if(v1!=v2){
	    				var rowspan = nextIndex - rowIndex;
	    				temp[rowIndex+""]=rowspan;
	    				rowIndex = nextIndex; 
	    			}
	    		}
	    	}
	    	return flgs;
		}
	}
	
	/**得到分页栏*/
	function getPager(obj){
		return $(obj).next();
	}
	
	/**重置宽高*/
	function reSize(options, obj){
		var wh ={
				width:options.width ? options.width : $(obj).parent().width() || '100%',
				height:options.height ? ( $(obj).data().pagination ? options.height -30 : options.height) : $(obj).parent().height() || '100%'  //有分页栏要再减去分页栏的高度
		};
		$(obj).css(wh);
		
		//重置表格适应,实现表头固定
		$(obj).find('tbody').height(wh.height-$(obj).find('thead').height());
		$(obj).find('tbody').width(wh.width);
		
		setLastColumnWidth(obj);
		setTheadWidth(options, obj);
	}
	
	/**设置表头宽度*/
	function setTheadWidth(options, obj){
		var tby = $(obj).find('tbody');
		var w = tby.width();
		var scorllWidth = getScrollWidth(tby);
	    $(obj).find('thead').width(w-scorllWidth);
	}
	
	/**判断是否为空*/
	function IsEmpty(obj){
		return !obj ? true : false;		
	}
	
	/**得行选行*/
	function getSelected(div){
		var selectCss = getSelectedCss(div);
		var index=$(div).find("."+selectCss).attr("index");
		index=parseInt(index);
		return getRowByIndex(div,index);
	}
	
	/**得勾选行*/
	function getSelections(div){
		var arr=[];
		$(div).find("tbody .jqTransformChecked").each(function(){
			var index=$(this).next().attr("index");
			index=parseInt(index);
			arr.push(getRowByIndex(div,index));
		});
		return arr;
	}
	
	/**得到所有勾选行的id*/
	function getSelectIds(div){
		var ids = "";
		var idField = $(div).data().idField;
		$(div).find("tbody .jqTransformChecked").each(function(){
			var index=$(this).next().attr("index");
			index=parseInt(index);
			var id= getRowByIndex(div,index)[idField];
			ids+=(id+",");
		});
		if(ids){ids=ids.substring(0,ids.length-1);}
		return ids;
	}
	
	/**根据行号得行数据*/
	function getRowByIndex(div,index){
		return $(div).data().datas[index];
	}
	
	/**根据行号设置1行选中*/
	function selectRow(div, index){
		var tr = $(div).find("tbody tr[index='"+index+"']");
		select(div, tr);
	}
	
	/**根据id值设置1行选中*/
	function selectRecord(div, idValue){
		var tr = $(div).find("tbody tr[dataId='"+idValue+"']");
		select(div, tr);
	}
	
	/**设置1行选中*/
	function select(div, tr){
		
		var options = div.data();
		var selectCss = getSelectedCss(div);
		if(options.hasLevelDefine){
			div.find("tbody tr ."+selectCss+" td").eq(0).removeClass("td");
		}
		div.find("tbody tr").removeClass(selectCss);
		$(tr).addClass(selectCss);
		if(options.hasLevelDefine){
			$(tr).find("td").eq(0).addClass("td");
		}
	}
	
	function getSelectedCss(div){
		var options = $(div).data();
		var selectCss = options.hasLevelDefine ? "trSelect_":"trSelect";
		return selectCss;
	}
	
	/**根据id值设置1行勾选与否*/
	function checkRecord(div, options){
		if(!options.id){return;}
		var ids = options.id.split(",");
		for(var i=0; i<ids.length; i++){
			var tr = $(div).find("tbody tr[dataId='"+ids[i]+"']");
			check(tr, options.check);
		}
	}
	
	/**根据行号设置1行选中与否*/
	function checkRow(div, options){
		if(!options.index){return;}
		
		var indexs = (""+options.index).split(",");
		for(var i=0; i<indexs.length; i++){
			var tr = $(div).find("tbody tr[index='"+indexs[i]+"']");
			check(tr, options.check);
		}
	}
	
	/**设置勾选*/
	function check(obj, isCheck){
		if(obj.length==0){return;}
		var optType = isCheck ? 1 : 2;
		optCheckClass( getCheckBox(obj), optType);
		
		setTitleCheckSate($(obj).closest("div"));
	}
	
	/**判断一行是否为勾选状态*/
	function isChecked(div, options){ // {index:indexValue}|{id:idValue} 
		var tr = options.id==undefined ? 
					 	  $(div).find("tbody tr[index='"+options.index+"']")
						: $(div).find("tbody tr[dataId='"+options.id+"']");
						
		return optCheckClass( getCheckBox(tr), 3);
	}
	
	/**根据数据ID得行数据*/
	function getRowById(div, id){
		div = $(div);
		var tr = div.find("tr[dataId='"+id+"']");
		var index = tr.attr("index");
		return div.data().datas[index];
	}
	
	/**根据行数据得ID*/
	function getRowIndex(div, options){
		var id = typeof(options)=='object' ?  options[$(div).data().idField] : options;
		
		var index = $(div).find("tbody tr[dataId='"+id+"']").attr("index");
		
		return index ? index*1 : -1; 
	}
	
	/**判断是否需要复选框*/
	function isShowCheckBox(options){
	   var checkbox=options.checkbox;
	   var cols = options.frozenColumns;
	   var boo =false;
	   if(cols && cols[0]){
		   for(var i=0; i<cols[0].length; i++){
				if(cols[0][i].checkbox){
					boo = true; break;
				}
		   }
	   }
	   return checkbox=="1" || boo;
	}
	
	/**得到所有数据行*/
	function getRows(div){
		var datas = $(div).data().datas;
		if(!datas){datas=[];  $(div).data().datas = datas;} 
		return datas;
	}
	
	/**得到某对象内的样式复选框*/
    function getCheckBox(obj){
    	return obj.find(".jqTransformCheckbox");
    }
    
    /**对某对象内的样式复选框进行操作, 1,勾选，2，取消勾选，3，是否有*/
    function optCheckClass(obj, optType){
    	if(optType==1){
    		obj.addClass("jqTransformChecked");
    		$(obj).each(function(){$(this).next().attr("checked",true);});
    	}else if(optType==2){
    		obj.removeClass("jqTransformChecked");
    		$(obj).each(function(){$(this).next().attr("checked",false);});
    	}else if(optType==3){
    		return obj.hasClass("jqTransformChecked");
    	}
    }
	
	/**根据所有行的选中状态设置全选框的状态*/
	function setTitleCheckSate(obj){
			var div = $(obj);
			var datas = div.data().datas;
			
			var size=div.find("tbody .jqTransformChecked").length;
			var titleCheck = getCheckBox(div.find("thead"));
			
			optCheckClass(titleCheck, size==datas.length?1:2);
	}
	
	/**将可编辑单元格的数据设置到数据对象内去*/
	window.resetEditValueToDatas=function(obj){
		var cDiv = $(obj);
		var contenteditable = cDiv.attr("contenteditable");
		if(contenteditable!="true"){return;} //IE必须有这句
		
		var text = cDiv.text();
		var field = cDiv.attr("field");
		var tr = cDiv.closest("tr");
		var index = $(tr).attr("index");
		
		var div = $(tr).closest("div");
		var datas = $(div).data().datas;
		datas[index][field]=text;
		$(div).data().datas = datas;
	};
	
	/**拖动重置列宽*/
	function tableResizeColumn(table){
		//复合表头暂不做列拖拽
		if($(table).find("thead tr").length>1){return;}
		
		var cols = $(table).find("thead tr th");
		var tTD; //用来存储当前更改宽度的Table Cell,避免快速移动鼠标的问题
		cols.bind("mousedown",function () {
				tTD = this;//记录单元格
				window.datagrid_tTD=tTD;
				if (event.offsetX > tTD.offsetWidth - 10) {
					tTD.mouseDown = true;
					tTD.oldX = event.x;
					tTD.oldWidth = tTD.offsetWidth;
				}
			}
		);	
		cols.bind("mouseup", function () {
				//结束宽度调整
				if (tTD == undefined){ tTD = this;}
				tTD.mouseDown = false;
				tTD.style.cursor = 'default';
			}
		 );
		$(window).bind("mouseup", function () {
				//结束宽度调整
				if (window.datagrid_tTD){ 
					var tTD = window.datagrid_tTD;
					tTD.mouseDown = false;
					tTD.style.cursor = 'default';
				}
			}
		 );
		cols.bind("mousemove", function () {
				//取出暂存的Table Cell
				if (tTD == undefined) {tTD = this;}
				if(tTD.cellIndex==0 && $(tTD).find(".jqTransformCheckbox").length>0){return;}
				
				//更改鼠标样式
				var cs =  (event.offsetX > this.offsetWidth - 10) ? 'col-resize' : 'default';
				this.style.cursor = cs;
				
				//调整宽度
				if (tTD.mouseDown != null && tTD.mouseDown == true) {
					//调整列宽
					tTD.style.cursor = 'col-resize';
					if (tTD.oldWidth + (event.x - tTD.oldX)>0){
						var w =  tTD.oldWidth + (event.x - tTD.oldX)-20;
						$(tTD).width(w);
						$(tTD).find("div").eq(0).width(w);
					}
					
					//调整该列中的每个Cell
					var rows = $(table).find("tbody tr");
					for (j = 0; j < rows.length; j++) {
						var td = $($(rows[j]).find("td")[tTD.cellIndex]);
						var w = $(tTD).width();
						td.width(w);
						td.find("div").eq(0).width(w);
					}
				}
			}
		);
	}
	
	/**自适应最后一列宽度：如果所有列宽比不过容器宽度，则将最后一列宽度补足，做到界面满铺*/
	function setLastColumnWidth(div){
		//容器宽度
		var content_w = $(div).width(); 
		//列表宽度
		var tby = $(div).find('tbody');
		var trs = $(tby).find("tr");
		var table_w = tby.get(0) && trs.length>0 ? $(trs[0]).width():div.width();
		
		if(content_w>table_w){
			//最后一列宽度
			var tds = $(trs).eq(0).find("td");
			var lastCol_w = $(tds[tds.length-1]).width();
			lastCol_w = content_w-(table_w - lastCol_w);
		    var scorllWidth = getScrollWidth(tby);
			var css = {"width":lastCol_w+"px", "text-align":"center"};
			//最后一列被重置，则让此列表头与数据都居中，避免宽度过宽，数据不对齐会比较难看
			var divCss = {"width":(lastCol_w-10-scorllWidth)+"px", "text-align":"center"};
			//表头设置,适应复合表头
			var trs = $(div).find("table thead tr");
			for(var i=0;i<trs.length;i++){
				var th =  $(trs[i]).children("th:last");
				var colspan =th.attr("colspan");
				
				if(!colspan || colspan == 1){//没有跨列，说明已经是整个表头的最后一列
					th.css(css);
					th.find("div").css(divCss); 
					break;
				}
			}
			
			css["width"]=(lastCol_w-scorllWidth)+"px";
			//表体设置
			$(div).find("table tbody tr").each(function(){  
				$(this).children("td:last").css(css);
				$(this).children("td:last").find("div").css(divCss);
			});
		}else if(content_w==table_w && trs.length==0){ //还没数据的情况下
			var trs = $(div).find("table thead tr");
			var tds = $(trs[trs.length-1]).find("th");
			
		    var w = 0;
		    for(var i=0;i<tds.length-1;i++){
		    	w += $(tds[i]).width();
		    }
			var css = {"width":(content_w-w)+"px", "text-align":"center"};
			var divcss = {"width":(content_w-w-10)+"px", "text-align":"center"};
		    
			var td = $(tds[tds.length-1]);
			td.css(css);
			td.find("div").css(divcss); 
		}
	}
	
	//流动条宽度
	function getScrollWidth(tby){
		var boo = $(tby).height() < $(tby).get(0).scrollHeight; //说明没有滚动条
		var scorllWidth = ($.browser.msie || $.browser.mozilla) && boo ? 18 :10 ; //IE与chrome竖向流动条宽度不一样
		return scorllWidth;
	}
	
	//本地排序
	function localSort(options, obj){
		loadData(options, obj);
	}
	
	/**获得表头定义，返回适合通用导出的表头定义*/
	function getHeadDefine(obj){
		var options = $(obj).data();
		var headData= options.headData;
		var lineArr =  headData.lineArr;
		var lastLeaf = headData.lastLeaf;
		var heads = [];
		var cols = [];
		
		for(var i=0; i<lastLeaf.length; i++){
			var c = lastLeaf[i];
			heads.push(c.title);
			cols.push(c.field);
		}
		
		if(lineArr.length>1){
			var line = lineArr[0];
			var c = 0;
			setColumnIndex(line, c);
		}
		
		//算出列在行中的下标
		function  setColumnIndex (line, c){
			for(var i=0; i<line.length; i++){
				var f = line[i];				
				f.c = c;	
				if(f.children && f.children.length>0){
					setColumnIndex(f.children, c);
				}
				//下一个单元格的列下标
				c = c + (f.colspan == 0  ? 1 : f.colspan );
			}
		}
		
		var EXP_heads = lineArr.length<=1 ? heads.join(",") : JSON.stringify(lineArr);
		EXP_heads = encodeURIComponent(encodeURIComponent(EXP_heads));
		var EXP_cols = cols.join(",");
		
		return {EXP_heads:EXP_heads,EXP_cols:EXP_cols};
	}
	
})(jQuery);
