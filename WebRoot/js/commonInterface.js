/**
此文件用于做一些自定义的公共方法扩展，具体见每个方法的说明。
@author gaotao
@date 2013/9/1
*/
var commonInterface = function(){
	return {
	    /**
	     当ajax请求发现session超时，SSH3会默认弹出SSH3实现的迷你登录窗(common.js里的openMinLoginWin方法)
	     实现此方法，可以作自定义的操作，而不走SSH3默认的迷你录窗
	     @param fn 此参数是发现session超时正在执行的方法
	     @param opts 未来扩展参数，暂无实际值 是一个{}类型
	    */
		/*
		whenSessionTimeout : function(fn, opts){
		       //....  //这里写好自定义的处理逻辑
		       //fn.call(this); //就可以发现session超时正执行的方法以，继续执行
		       alert("...whenSessionTimeout finish!");
		},
		*/
		/**
	     当开新界面前，发现将被访问的界面是一个敏感菜单界面，SSH3会默认打开一个敏感菜单登录验证界面(common.js里的openMinLoginWin方法)
	     实现此方法，可以作自定义的操作，而不走SSH3默认的敏感菜单验证登录界面
	     @param fn 此参数是发现session超时正在执行的方法
	     @param msg 如果是因敏感菜单而导致进行入此方法，那msg才有值，格式如: "2:菜单ID"
	     @param opts 未来扩展参数，暂无实际值 是一个{}类型
	    */
		/*
		whenIsSensit : function(fn, msg, opts){
			 //....  //这里写好自定义的处理逻辑
		     //fn.call(this); //就可以发现session超时正执行的方法以，继续执行
		     //alert("...whenIsSensit finish!");
		}
		*/
		//,....
	};
}();