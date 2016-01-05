$(function(){
	
	$(".searchIf a").click(function(){
		$(this).parents(".searchIf").find("a").removeClass("on");
		$(this).addClass("on");
	})
})
function findOrder(){
	var url=MSConfig.BaseURL+"/buyer/coupon";
	var couponStatus=$("#couponStatus").find("a.on").attr("data-value");
	var startTime=$("#startTime").val();
	var endTime=$("#endTime").val();
	window.location.href=url+"?issure="+couponStatus+"&startTime="+startTime+"&endTime="+endTime;
}
