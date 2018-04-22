var cattHuaChiHelp = function(eleContainer, callback ) {
	//保存选中的文字
	var selectedStr ="";
	eleContainer = eleContainer || document;
	var eleShare = null;	
	
	//创建浮动图标
	var funCreateImg = function() {
			var img = document.createElement("img");
			img.id = "catt_flotHelpPic";
			img.style.display = "none";
			img.style.position = "absolute";
			img.style.cursor = "pointer";
			
			img.setAttribute("src",CONTEXT_PATH_NAME+"/images/core/browse.gif");
			document.body.appendChild(img);
			
			//添加事件
			img.onclick = function(e) {
				e = e || window.event;
				if (selectedStr) {
					if(callback){
						var param ={};
						param.href=window.location.href;
						param.selectedStr = selectedStr;
						callback(param);
					}
				}
			};
		return img;	
	}
	
	if(document.getElementById("catt_flotHelpPic")){
		eleShare = document.getElementById("catt_flotHelpPic");
	}else{
		if(document.body){
			eleShare = funCreateImg();
		}
	}	
	
	//获取选中的文本
	var funGetSelectTxt = function() {
		var txt = "";
		if(document.selection) {
			txt = document.selection.createRange().text;// IE
		} else {
			txt = document.getSelection();
		}
		return txt.toString();
	};
	
	$(eleContainer).bind("mouseup ", function(e) {
		e = e || window.event;
		var target = e.srcElement ? e.srcElement : e.target;
		
		if(!eleShare){
			return;	
		}
		
		if(target.id == eleShare.id){
			eleShare.style.display = "none";
			return;
		}
				
		var txt = funGetSelectTxt(), sh = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0;
		var left = e.clientX , top = (e.clientY - 30 < 0) ? e.clientY + sh + 15 : e.clientY + sh - 30;
		if (txt ) {//&& selectedStr != txt
			eleShare.style.display = "inline";
			eleShare.style.left = left + "px";
			eleShare.style.top = top + "px";
			//设置值
			selectedStr = txt;
		} else {
			eleShare.style.display = "none";
			selectedStr = "";
		}
	});
	
};

$(window).load(function(){
	cattHuaChiHelp(null,function(para){
		alert('链接：'+para.href+'\n选择的文字：'+para.selectedStr);	
	});
 });