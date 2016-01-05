
var getArgs=(function(){
	var sc=document.getElementsByTagName('script');
	if(sc[sc.length-1]!=null && sc[sc.length-1].src.split('?')[1] != undefined) {
		var paramsArr = sc[sc.length - 1].src.split('?')[1].split('&');
		var args = {}, argsStr = [], param, t, name, value;
		for (var ii = 0, len = paramsArr.length; ii < len; ii++) {
			param = paramsArr[ii].split('=');
			name = param[0], value = param[1];
			if (typeof args[name] == "undefined") { //参数尚不存在
				args[name] = value;
			} else if (typeof args[name] == "string") { //参数已经存在则保存为数组
				args[name] = [args[name]]
				args[name].push(value);
			} else {  //已经是数组的
				args[name].push(value);
			}
		}
	}
	/*在实际应用中下面的showArg和args.toString可以删掉，这里只是为了测试函数getArgs返回的内容*/
	//var showArg=function(x){   //转换不同数据的显示方式
	//	if(typeof(x)=="string" && !d+/.test(x)) return "'"+x+"'"; //字符串
	//		if(x instanceof Array) return "["+x+"]" //数组
	//	return x;   //数字
	//}
	//组装成json格式
	//args.toString=function(){
	//	for(var ii in args) argsStr.push(ii+':'+showArg(args[ii]));
	//	return '{'+argsStr.join(',')+'}';
	//}
	return function(){return args;} //以json格式返回获取的所有参数
})();

$(function(){

	// 通用
	var table = $("#tabList");
	var tLength = table.find("tr").length;
	if(length < 30){
		var l = 30 - tLength;
		var tr = table.find("tr")[0];
		var tdL = $(tr).find("th").length;
		var td="";
		for(var z = 0; z < tdL; z++){
			td += "<td></td>";
		}
		for (var i = 0; i <= l; i++) {
			table.append("<tr>" + td + "</tr>");
		};
	}
	// 小表格
	var t2 = $("#tabLittleList");
	var tl = t2.find("tr").length;
	if(length < 15){
		var l = 15 - tl;
		var tr = t2.find("tr")[0];
		var tdL = $(tr).find("th").length;
		var td="";
		for(var z = 0; z < tdL; z++){
			td += "<td></td>";
		}
		for (var i = 0; i <= l; i++) {
			t2.append("<tr>" + td + "</tr>");
		};
	}

	var ctx=getArgs()["ctx"];
	if(ctx==undefined) return;
	var selContent = '<option value="javascript:void(0);" selected="selected">报表中心</option>'
					  +'<optgroup label="营业报表">'
					  +'<option value="'+ctx+'/salerManage/yun/report/menu/simple_day">简要日报表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/menu/detail_day">详细日报表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/menu/menu_px">菜品排行报表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/menu/cancel">撤单报表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/menu/anti">反结账报表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/menu/order_yh">账单优惠报表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/menu/order_pay">付款统计报表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/menu/shift">交接班报表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/menu/return_food">退菜报表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/menu/menu_sales">销售统计报表</option>'
					  +'</optgroup>'
					  +'<optgroup label="原料报表">'
					  +'<option value="'+ctx+'/salerManage/yun/report/material/stock_info">库存一览表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/material/material_info">原料流水表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/material/instore">入库表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/material/return_">退货表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/material/loss">报损表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/material/inventory">盘点表</option>'
					  +'</optgroup>'
					  +'<optgroup label="会员报表">'
					  +'<option value="'+ctx+'/salerManage/yun/report/card/card_add">会员添加报表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/card/not_recharge">红冲报表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/card/card_recharge">充值报表</option>'
					  +'<option value="'+ctx+'/salerManage/yun/report/card/card_custom">会员消费报表</option>'
					  +'</optgroup>';
	$("#selReport").empty();
	$("#selReport").append(selContent);
});
