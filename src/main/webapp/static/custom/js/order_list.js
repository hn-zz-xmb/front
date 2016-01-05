$(function(){
	$("#searchInput").keydown(function(event){
		if(event.which==13){
			var orderNo=this.value;
			window.location.href=MSConfig.BaseURL+"/salerManage/ordermanage?orderNo="+orderNo;
		}
	})
	$(".searchIf a").click(function(){
		$(this).parents(".searchIf").find("a").removeClass("on");
		$(this).addClass("on");
	})
})
function searchAll(){
	var url = MSConfig.BaseURL+"/salerManage/ordermanage";
	var orderStatus = $("#orderStatus").find("a.on").attr("data-value");
	var type_search = $("#consumeMethod").find("a.on").attr("data-value");
	var fromTime = $("#fromTime").val();
	var toTime = $("#toTime").val();

	orderStatus = orderStatus == undefined ? "" : orderStatus;
	type_search = type_search == undefined ? "" : type_search;
	fromTime = fromTime == undefined ? "" : fromTime;
	toTime = toTime == undefined ? "" : toTime;
	window.location.href = url + "?status=" + orderStatus + "&consume_method="
			+ type_search + "&fromTime=" + fromTime + "&toTime=" + toTime;
}