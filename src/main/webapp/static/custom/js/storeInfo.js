$(function(){
	$("body").on("mouseover","#dropdown_link",function(){
		$(this).parent().next().show();
	});
	$("body").on("mouseleave","#dropdown ul",function(){
		$(this).hide();
	});
	$("#waimai_y").click(function(){
		$("#waimai_box").slideDown();
	});
	$("#waimai_n").click(function(){
		$("#waimai_box").slideUp();
	});
});