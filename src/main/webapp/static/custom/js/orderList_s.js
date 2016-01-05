$(function(){
	$("#more").click(function(){
		if($(this).hasClass("down")){
			$(this).removeClass("down").addClass("up");
			$("#filter_more").slideDown();
		}else{
			$(this).removeClass("up").addClass("down");
			$("#filter_more").slideUp();
		}
	});
	
	$("#dropdown1").on("mouseover","#dropdown1_link",function(){
		$("#dropdown1").find("ul").show();
	});
	$("#dropdown1").on("mouseleave","ul",function(){
		$("#dropdown1").find("ul").hide();
	});
});