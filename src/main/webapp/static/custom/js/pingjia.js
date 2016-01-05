$(function(){
	$("#pingjia_tab").on("click", "a",function(){
		$("#pingjia_tab li a").removeClass("active");
		$(this).addClass("active");
	});
});

function showTab(i){
	$(".tab").hide();
	$("#tab"+i).show();
}