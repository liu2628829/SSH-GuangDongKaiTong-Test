/**新界面风格单选项控件
 * 1.初始化：
	 * 	a):单选按钮
		   $("#XX").radio({
				type : 1 // 1:平铺单选形态，2：平铺多选形态，3:开关，开关形态时，要求传入的选项数据datas的长度只能为2
				datas:[{id:"",text:"",color:""}], //选项数据,color表示背景颜色，默认blue，开关形态时才会
				id:datas数据中隐式值字段名, 默认取"id"字段 必须与datas保持一致
				text:datas数据中显示文本字段名,默认取"text"字段 必须与datas保持一致
				onChange:function(obj){}, //所选值发生变化事件,返回obj, 如: {id:"",text:"", currentId:"", currentText:"",currentChecked:""}, currentXX是针对多选操作，当前点击的复选框值，文本，选中状态
				addDefault: true; 是否添加默认选项（如单选时“请选择”，多选时“全部”）
				defaultItem: //自定义默认选项，默认“全部”,字段名必须与datas保持一致，如：{id:"",text:"全国"}
				defaultSelect:index, //初始化时选中第几个选项 ，默认0,
				defaultSelectTriggerChangeEvent:0,//初始化设置选项时是否触发onChange事件，1触发，0不触发，默认0
				width:200, //div的宽度，开关形态最大200px，默认未设置宽度。
				height:40, //div的高度，默认为24。   
				fontSize：24  //字体大小 默认24
				minItemWidth:80,  //每个选项的最小宽度，默认50（平铺）
				maxItemWidth : 140, 每个选项的最大宽度，默认150 （平铺）
				itemMargin:5  //选项间的间距，默认4 （平铺）
				formatter:function(item){return "xx";}  //自定义文本呈现
		  });
 * 2.方法调用：
 *   	$("#XX").radio("setValue", {id:"1"});  或{index:1}  //设置选中项: 按id值设置或按下标设置, 下标从0开始，设置多选如：{id:"1,2"} 
   		var v = $("#XX").radio("getValue"); //获取值: 为多选框时返回json数组[{id:"",text:"",rowData:{}},..]，单选时返回1个json对象  {id:"",text:"",rowData:{}}
   		var opts = $("#XX").radio("options"); //获取属性定义
   		$("#XX").radio("setItems",[{}]); //重置可选项，相关于重置datas属性
 */

(function($){
	$.fn.radio = function(opt, options){
		if(typeof opt != "object"){
			if(opt == "loadData"){
				var opts = $(this).data();
				opts.data = options.datas; //得到传入的数据data
				loadData(opts, this);
			}else if(opt == "setValue"){
				setValue(this, options); //设置选中项
			}else if(opt == "getValue"){
				return getValue(this);   //得到选中值
			}else if(opt == "options"){
				return this.data();			//得到入参时的options
			}else if(opt == "setItems"){ //设置选项
				var opts = $(this).data();
				opts.datas = options;  //得到传入的数据data
				setItems(this, opts);
			}
		}else{
			//初始化
			loadData(opt,this);
		}
	};
	
	/**初始化方法*/
	function loadData(opts, obj){
		var inp = $(obj);
		var id = inp.attr("id");
		inp.next().remove();
		//inp为下拉框时
		if(inp.is("select")){ 
			inp.after('<input type="hidden" id="'+id+'" value="" />'); 
			inp.remove();
			inp = $("#" + id);
		}
		//原始数据与默认数据整合
		opts = $.extend({},$.fn.radio.defaults,opts);
		
		//添加默认选项
		addDefaultItem(inp,opts);
		inp.data(opts);
		var type = opts.type;
		if(type == "3" && opts.datas.length == 2){  //开关形式
			setSwitch(inp,opts);
		}else {                      //平铺 形式
			var html = [];
			html.push("<div class='selectOrder'></div>");
			inp.hide();
			inp.after(html.join(''));
			setPItems(inp, opts);
		}
		//绑定点击事件
		if(type == "2"){
			multiClickEvent(inp);
		}else{
			radioClickEvent(inp);
		}
		//默认选项选中
		var index = opts.defaultSelect+"";
		setValue(inp, {"index" : index}, null, true);
		//设置div高宽
		setHeiWid(inp);
		//设置选项的长度及其间距
		setItemWidMargin(inp);
	}
	
	/**添加默认选项*/
	function addDefaultItem(inp,opts){
		if(opts.addDefault && opts.defaultItem && opts.type != '3'){  //用户自定义
			opts.datas.unshift(opts.defaultItem); 
		}else if(opts.addDefault && opts.type != '3'){ //默认选项
			var defaultItem = {};
			var id = opts.id;
			var text = opts.text; 
			defaultItem[id] = "";    
			defaultItem[text] = "全部";
			opts.datas.unshift(defaultItem);
		}
	}
	
	/**重置单选选项*/
	function setPItems(inp,opts){
		var id = opts.id;
		var text = opts.text;
		var div = inp.next();
		var type = opts.type;
		var datas = opts.datas;
		var html = [];
		for(var i = 0; i<datas.length; i++){
			var temp = datas[i];
			var lastShowText = opts.formatter ? opts.formatter(temp) : temp[text];
			
			html.push("<p style='text-align:left;overflow:hidden;text-overflow:ellipsis'  title='"+temp[text]+"' >");
			if(type == "1"){  //平铺单选
				html.push("<span class='jqTransformRadioWrapper'>");
				html.push("<a href='javascript:void(0)' class='notSwitch jqTransformRadio' index='"+i+"'></a>");
				html.push("<input type='radio' value='"+temp[id]+"' checked=false class='jqTransformHidden'/>");
			}else{  //平铺多选
				html.push("<span class='jqTransformCheckboxWrapper' style='display:block;float:left;margin:0px 4px;margin-top:2px;'>");
				html.push("<a href='javascript:void(0)' class='notSwitch jqTransformCheckbox' index='"+i+"'></a>");
				html.push("<input type='checkbox' name='chbox' value='"+temp[id]+"' checked=false class='jqTransformHidden'/>");
			}
			html.push("</span>");
			html.push(lastShowText);
			html.push("</p>");
		}
		div.html(html.join(''));
		//div.find(":radio").jqTransRadio(); //加载单选框样式
	}
	
	/**绑定点击事件*/
	function radioClickEvent(inp){
		var liEle= getLiObj(inp);
		var pEle = getPObj(inp);
		var element = pEle.length > 0 ? pEle : liEle; //区分开关形态
		element.unbind().bind("click", function(){
			var childEle = pEle.length > 0 ? $(this).find("a") : $(this);
			if(childEle.hasClass("jqTransformChecked") || childEle.hasClass("selected")) return;
			var id = pEle.length>0 ? childEle.next().val() : childEle.val();
			setValue(inp, {"id" : id});
		});
	}
	
	/**多选框绑定点击事件**/
	function multiClickEvent(inp){
		var jElement = getPObj(inp);
		jElement.unbind().bind("click",function(){
			var jaEle = $(this).find("a");
			var i = jaEle.attr("index");
			var checked = false;
			if(inp.data().addDefault && i==0){
				multiSelectAll(inp);
			}else{
				if(jaEle.hasClass("jqTransformChecked")){
					checked = false;
					jaEle.removeClass("jqTransformChecked");
					if(inp.data().addDefault){
						jElement.eq(0).find("a").removeClass("jqTransformChecked");
						jElement.eq(0).find("a").attr("checked", false);
					}
				}else{
					checked = true;
					jaEle.addClass("jqTransformChecked");
				}
				jaEle.attr("checked", checked);
				isSelectAll(inp, inp.data());
			}
			
			setValue(inp, null, jaEle);
		});
	}
	
	/**多选框全选事件**/
	function multiSelectAll(inp){
		var aEle = getPObj(inp).find("a");
		if(aEle.eq(0).hasClass("jqTransformChecked")){
			aEle.removeClass("jqTransformChecked");
			aEle.attr("checked", false);
		}else{
			aEle.addClass("jqTransformChecked");
			aEle.attr("checked", true);
		}
		inp.val(' ');
	}
	
	/**判断多选框是否要勾选全选**/
	function isSelectAll(inp,opts){
		if(!opts.addDefault) return;
		if(inp.next().find(".jqTransformChecked").length == opts.datas.length-1){
			var aEle = getPObj(inp).find("a");
			aEle.eq(0).addClass("jqTransformChecked");
			aEle.eq(0).attr("checked",true);
		}
	}
	
	/**开关形态*/
	function setSwitch(target,opts){
		target.next().remove();
		var idAttr = target.attr("id");
		var id = target.data().id;
		var text = target.data().text;
		var html = [];
		html.push('<ul class="yesNo" id="'+idAttr+'_on_off" >');
		var color1 = opts.datas[0].color || "#45b6d7";
		html.push('<li class="selected" value="'+opts.datas[0][id]+'"style="background:'+color1+';color:#fff">'+opts.datas[0][text]+'</li>');
		html.push('<li value="'+opts.datas[1][id]+'" class="">'+opts.datas[1][text]+'</li>');
		html.push('</ul>');
		target.after(html.join(""));
		target.hide();
	}
	
	/**设置选中项*/
	function setValue(inp, options, currentClickObj, isSetDefaultSelect){
		if(options){
			if(inp.data().type == "2"){  //多选框时
				setMultiVal(inp, options);
			}else{
				var aElement = getPObj(inp).find("a");
				var liElement = getLiObj(inp);
				var i = options.index;  //下标
				var id = options.id;  //id值
				if(i != null && aElement.length>0){  //选项下标
					setRadioClass(inp,i);
				}else if(i != null && liElement.length > 0){  //开关下标
					setLiStyle(inp,i);
				}else if(id != null && aElement.length>0){  //选项id
					aElement.each(function(i){
						if($(this).next().val() == id){
							setRadioClass(inp,i);
						} 
					});
				}else if(id != null && liElement.length>0){ //开关id
					liElement.each(function(i){
						if($(this).val() == id){
							setLiStyle(inp,i);
						} 
					});
				}
				//给隐藏域inp的value赋值
				var $next = inp.next();
				var value = aElement.length>0 ? $next.find(".jqTransformChecked").next().val() : $next.find(".selected").val();
				inp.val(value);
			}
		}
		
		if( !(isSetDefaultSelect && !inp.data().defaultSelectTriggerChangeEvent)){
			doOnChange(inp, currentClickObj);
		}
	}
	
	/**多选框设置value值**/
	function setMultiVal(inp, options){
		var obj = getPObj(inp).find("a");
		obj.removeClass("jqTransformChecked");
		obj.next().attr("checked",false);
		var ids = options.id;
		var indexs = options.index;
		if(ids != null){
			ids += "";  //id为整数时转成字符串类型
			var idArr = ids.split(",");
			for(var i=0; i<idArr.length; i++){
				inp.next().find(".jqTransformCheckbox").each(function(j){
					if(idArr[i] == $(this).next().val()){
						$(this).addClass("jqTransformChecked");
						$(this).attr("checked", true);
					}
				});
			}
			inp.val(ids);
		}else if(indexs != null){
			indexs += ""; //indexs为整数时转成字符串类型
			var indArr = indexs.split(",");
			var v = [];
			for(var i=0;i<indArr.length;i++){
				inp.next().find(".jqTransformCheckbox").each(function(j){
					if(indArr[i] == $(this).attr("index")){
						obj.eq(j).addClass("jqTransformChecked");
						obj.eq(j).next().attr("checked","checked");
						v.push($(this).next().val());
					}
				});
			}
			inp.val(v.join(","));
		}
	}
	
	/**设置单选框class、attr**/
	function setRadioClass(inp,index){
		var aElement = getPObj(inp).find("a");
		aElement.removeClass("jqTransformChecked");
		aElement.next().attr("checked", false);
		aElement.eq(index).addClass("jqTransformChecked");
		aElement.eq(index).next().attr("checked","checked");
	}
	
	/**li样式变化*/
	function setLiStyle(inp,index){
		var liElement = getLiObj(inp);
		liElement.removeClass("selected").css("background", "none").css("color","#b4b4b4");
		var color = inp.data().datas[index].color || "#45b6d7";
		liElement.eq(index).addClass("selected").css("background", color ).css("color","#fff");
	}
	
	/**得到选中值**/
	function getValue(inp){
		var v = null;
		var aEle = inp.next().find(".jqTransformChecked"); 
		var datas = inp.data().datas;
		if(inp.data().type == "2"){
			var valueArr = [];
			aEle.each(function(){
				valueArr.push({
					id : $(this).next().val(),
					text : $(this).parents("p").attr("title"),
					rowData : datas[ $(this).attr("index")] 
				});
			});
			
			if(inp.data().addDefault && datas.length==valueArr.length){ //如果已全选且有默认选项，去掉默认选项
				valueArr.splice(0,1);
			}
			return valueArr;
		}else{
			var ulEle = inp.next().find(".selected");
			var json = {};
			var text = aEle.length > 0 ? aEle.parents("p").attr("title") : ulEle.text();
			var id = aEle.length > 0 ? aEle.next().val() : ulEle.val();
			json = {id: id, text : text};
			if(aEle.length > 0 ){json.rowData=  inp.data().datas[aEle.attr("index")]; }
			return json;
		}
	}
	
	/***设置选项**/
	function setItems(inp, opts){
		var aEle = getPObj(inp).find("a");
		var liEle = getLiObj(inp);
		addDefaultItem(inp, inp.data()); //添加默认选项
		var datas = opts.datas;
		if(liEle.length>0){
			setSwitch(inp, opts);
		}else if(aEle.length>0){
			setPItems(inp, opts);
		}
		var index = inp.data().defaultSelect;
		index = index >datas.length ? 0 : index;
		if(inp.data().type == "2"){
			multiClickEvent(inp);
			setMultiVal(inp,{"index" : index});
		}else{
			radioClickEvent(inp);
			setValue(inp,{"index" : index});  
		}
		//设置div高宽
		setHeiWid(inp);
	}
	
	/**设置div高宽字体大小*/
	function setHeiWid(inp){
		var hei = inp.data().height;
		if(hei){
			inp.next().height(hei);
			inp.next().find("li").css("line-height", hei+"px");
		}
		var wid = inp.data().width;
		if(wid){
			inp.next().width(wid);
		}
		var fontSize = inp.data().fontSize + "px";
		if(fontSize){
			inp.next().css("font-size",fontSize );
		}
	}
	
	/**设置选项的宽度，间距**/
	function setItemWidMargin(inp){
		var jp = inp.next().find("p");
		var minItemWidth = inp.data().minItemWidth;
		if(minItemWidth){
			jp.css("min-width" , minItemWidth);
		}
		if(inp.data().width!='auto'){
			var maxItemWidth = inp.data().maxItemWidth;
			if(maxItemWidth){
				jp.css("max-width" , maxItemWidth);
			}
		}
		var itemMargin = inp.data().itemMargin;
		if(itemMargin){
			jp.css("margin-right", itemMargin+"px");
		}
	}
	
	/**获取单\多选框对象的父元素p**/
	function getPObj(inp){
		return inp.next().find("p");
	}	
	
	/**获取li对象*/
	function getLiObj(inp){
		return inp.next().find("li");
	}
	
	/**回调方法onChange*/
	function doOnChange(inp, currentClickObj){
		var params = inp.data();
		if(params.onChange){
			var returnVal;
			if(params.type=="2"){
				var arr =  getValue(inp);
				var idArr = [], textArr=[], rowDatas = [];
				for(var i=0; i<arr.length; i++){
					idArr.push(arr[i].id);
					textArr.push(arr[i].text);
					rowDatas.push(arr[i].rowData);
				}
				
				returnVal = {id:idArr.join(",") ,text:textArr.join(","), rowData:rowDatas};
				if(currentClickObj){
					returnVal.currentId = currentClickObj.next().val();
					returnVal.currentText = currentClickObj.parents("p").attr("title");
					returnVal.currentChecked = currentClickObj.attr("checked")=="checked";
				}
			}else{
				returnVal = getValue(inp);
			}
			params.onChange.call(this, returnVal);
		}
	}
	
	/***默认配置**/
	$.fn.radio.defaults = {
		type:"1",
		datas : [],
		id : "id",
		text:"text",
		addDefault:false,
		defaultSelect : 0,
		defaultSelectTriggerChangeEvent:0,
		itemMargin : 3,
		minItemWidth:50,
		maxItemWidth : 160,
		onChange:function(json){}
	};
})(jQuery);