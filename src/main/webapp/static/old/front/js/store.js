$(function(){
	//商品分类
	$("#category").click(function(){
		$("#category_box").show();
	});
	$("body").on("mouseleave","#category_box",function(){
		$("#category_box").hide();
	});
	
	//我的菜单
	$("#my_menu_btn").click(function(){
		$("#my_menu_box").show();
	});
	$("body").on("mouseleave","#my_menu_box",function(){
		$("#my_menu_box").hide();
	});
	
	//导航
	menuFixed("navigation");
	
	//二维码
	 $('#ervm').mousemove(function(){
		  $(this).find('ul').slideDown("1000");//you can give it a speed
		  });
		  $('#ervm').mouseleave(function(){
		  $(this).find('ul').slideUp("fast");
		  });
});

function menuFixed(id) {
	var obj = document.getElementById(id);
	var _getHeight = obj.offsetTop;

	$(window).scroll(function() {
		changePos(id, _getHeight);
	});
}
function changePos(id, height) {
	var obj = document.getElementById(id);
	var scrollTop = document.documentElement.scrollTop
			|| document.body.scrollTop;
	if (scrollTop < height) {
		obj.className="navigation_w";
	} else {
		obj.className="navigation_w_fix";
	}
}