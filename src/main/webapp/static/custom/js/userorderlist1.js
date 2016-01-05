$(function(){
	$(".chooseList>a").click(function(){
		$(".chooseList ul").toggle();
	})
	$("#searchInput").keydown(function(event){
		if(event.which==13){
			var orderNo=this.value;
			window.location.href=MSConfig.BaseURL+"/buyerOrder/findUserOrder?orderNo="+orderNo;
		}
	})
	$(".searchIf a").click(function(){
		$(this).parents(".searchIf").find("a").removeClass("on");
		$(this).addClass("on");
	})
})
function findOrder(){
	var url=MSConfig.BaseURL+"/buyerOrder/findUserOrder";
	var orderStatus=$("#orderStatus").find("a.on").attr("data-value");
	var consumeMethod=$("#consumeMethod").find("a.on").attr("data-value");
	var appriseStatus=$("#appriseStatus").find("a.on").attr("data-value");
	var startTime=$("#startTime").val();
	var endTime=$("#endTime").val();
	window.location.href=url+"?status="+orderStatus+"&consume_method="+consumeMethod
	+"&apprise="+appriseStatus+"&startTime="+startTime+"&endTime="+endTime;
}

//取消订单
function cancelOrder(orderId,orderType) {
	if (!confirm("确定要取消该订单么？"))
		return;
	var url = MSConfig.BaseURL+"/buyerOrder/cancleOrder";
	var param = {
		"orderId" : orderId,
		"orderType":orderType
	}
	var result = callText(url, param, false);
	if (result == "success") {
		window.location.href = MSConfig.BaseURL+"/buyerOrder/findUserOrder";
	} else {
		alert("该订单已经取消");
	}
}
function suregetgoods(orderId) {
	if (!confirm("您要确认收货么？同意后将打款给卖家."))
		return;

	var url = MSConfig.BaseURL+"/buyerOrder/suregetgoods";
	var param = {
		"orderId" : orderId
	}
	var result = callText(url, param, false);
	if (result == "success") {
		window.location.href = MSConfig.BaseURL+"/buyerOrder/findUserOrder";
	} else {
		alert("该订单已经完成");
	}
}
function drawback(orderId) {
	if (!confirm("确定要申请退款么？"))
		return;

	var url = MSConfig.BaseURL+"/buyerOrder/applyRefund";
	var param = {
		"orderId" : orderId
	}
	var result = callText(url, param, false);
	if (result == "success") {
		window.location.href = MSConfig.BaseURL+"/buyerOrder/findUserOrder";
	} else {
		alert("出错，请刷新页面");
	}
}
function apprise(id) {
	$.layer({
		type : 2,
		shadeClose : true,
		title : '',
		closeBtn : [ 0, true ],
		shade : [ 0.5, '#000' ],
		border : [ 10, 0.3, '#000' ],
		iframe : {
			src : MSConfig.BaseURL + '/appraise?id=' + id
		},
		area : [ '900px', '800px' ],
	});
}
function updateapprise(id) {
	$.layer({
		type : 2,
		shadeClose : true,
		title : '',
		closeBtn : [ 0, true ],
		shade : [ 0.5, '#000' ],
		border : [ 10, 0.3, '#000' ],
		iframe : {
			src : MSConfig.BaseURL + '/appraise/upappraise?id=' + id
		},
		area : [ '900px', '800px' ],
	});
}
function sendUI(id) {
	$.layer({
		type : 2,
		shadeClose : true,
		title : '',
		closeBtn : [ 0, true ],
		shade : [ 0.5, '#000' ],
		border : [ 10, 0.3, '#000' ],
		iframe : {
			src : MSConfig.BaseURL + '/account/message/sendUI?storeId=' + id
		},
		area : [ '700px', '500px' ],
	});
}