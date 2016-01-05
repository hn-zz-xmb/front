// JavaScript Document
$(function(){
	$("#goodsxq_tab").on("click", "a",function(){
		$("#goodsxq_tab li a").removeClass("active");
		$(this).addClass("active");
	});
});

function showTab(i){
	$(".tab").hide();
	$("#tab"+i).show();
}