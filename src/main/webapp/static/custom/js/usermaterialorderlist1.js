$(function(){
	$(".chooseList>a").click(function(){
		$(".chooseList ul").toggle();
	})
	$("#searchInput").keydown(function(event){
		if(event.which==13){
			var orderNo=this.value;
			window.location.href=MSConfig.BaseURL+"/buyerMaterOrder/findUserOrder?orderNo="+orderNo;
		}
	})
	$(".searchIf a").click(function(){
		$(this).parents(".searchIf").find("a").removeClass("on");
		$(this).addClass("on");
	})
})
function findOrder(){
	var url=MSConfig.BaseURL+"/buyerMaterOrder/findUserOrder";
	var orderStatus=$("#orderStatus").find("a.on").attr("data-value");
	var consumeMethod=$("#consumeMethod").find("a.on").attr("data-value");
	var appriseStatus=$("#appriseStatus").find("a.on").attr("data-value");
	var startTime=$("#startTime").val();
	var endTime=$("#endTime").val();
	window.location.href=url+"?status="+orderStatus+"&consume_method="+consumeMethod
	+"&apprise="+appriseStatus+"&startTime="+startTime+"&endTime="+endTime;
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