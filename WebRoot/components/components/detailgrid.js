/**
* 详情信息呈现-表格
* 
* $("#table").detailgrid({
*		columns:[{field:'cName',title:'姓名',colspan:0,formatter:function}],
*		data:{},
*		showborder:0,
*		showcols:2
*	});
*
*	columns:字段定义
*	 [{
*	 	field:数据库里面的英文名
*		title:显示在界面的字段
*	 	colspan:1占整行，其它，仅占1列
*		formatter:自定义呈现函数，注意函数必须有返回值，如：function(val,data){return "<a>"+val+"</a>";}，val是当前字段值，data是整行数据
*	 }]
*	)
*	data：数据 {key:value,...},这里的key与cols里的field对应
*	showcols:1行呈现几个字段(1个字段占2单元格)，默认为1
*   showborder:是否显示边框  0.不显示  1.显示, 默认为0
* */
(function($) {
	
	/**控件定义*/
	$.fn.detailgrid=function(opt,options){
		if(typeof opt!='object'){
			if(opt=='loadData'){
				var opts=$(this).data();
				opts.data=options;
				loadData(opts, this);
			}else if(opt=='getData'){
				return  $(this).data().data;
			}else if(opt=='options'){
				return $(this).data();
			}
		}else{
			loadData(opt, this);
		}
	};

	/**默认参数定义*/
	var defauts={
		columns:[{field:'',title:'',colspan:0}],
		data:{},
		showborder:0,
		showcols:1
	};

	/**绘制*/
	function loadData(opt, obj){
		var id = $(obj).attr("id");
		var opts = $.extend({},defauts,opt);
		if(opts.columns && opts.data){

			//将定义数据按显示行分组重构
			var arrs = [],tempArr=[],index =0;
			for(var i=0;i<opts.columns.length;i++){
				var tempCol = opts.columns[i];
				if(index==0 || (tempCol.colspan+'')=='1' || (arrs[arrs.length-1][0].colspan+'')=='1' || index+1> opts.showcols){
					index=0;
					tempArr = [];
					arrs.push(tempArr);
				}
				index++;
				tempArr.push(tempCol);
			}

			//组装表格数据
			var arr =['<table id="'+id+'" class="detailLaout'+ ((opts.showborder+''=='1')? ' detailLaout_border':'') + '">'];
			for(var m=0; m<arrs.length;m++){
				var tempCol = arrs[m];
				arr.push("<tr>");
				for(var i=0; i<opts.showcols; i++){
					var col = i<tempCol.length ? tempCol[i] : {};
					var colspan = (col.colspan+'')=='1' ?  (opts.showcols*2-1) : 0;
					var title = col.title ? col.title+'：' :'';
					var val = opts.data[col.field];
					val = col.formatter ? col.formatter(val,opts.data): val;
					val = (val==null||val==undefined) ? '' : val;

					arr.push('<th>'+title+'</th>');
					arr.push('<td '+ ( colspan ?  'colspan="'+colspan+'"':'')  +'>' +  val + '</td>');
					if(colspan){break;}
				}
				arr.push("</tr>");
			}
			arr.push("</table>");
			
			//绘制
			var tb = $(obj);
			tb.after(arr.join(''));
			tb.remove();
			
			//参数绑定
			$("#"+id).data(opts);
		}
	}
})(jQuery);