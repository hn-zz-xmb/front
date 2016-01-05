$(document).ready(function(){
	//新浪，微信
  $('li.mainlevel').mousemove(function(){
  $(this).find('ul').slideDown("1000");//you can give it a speed
  });
  $('li.mainlevel').mouseleave(function(){
  $(this).find('ul').slideUp("fast");
  });
  
  //排序
  $("#sort").on("click","a",function(){
		$("#sort a").removeClass("active01");
		$(this).addClass("active01");
	});
  
  //导航
  menuFixed("navBox");
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
			obj.style.position = 'relative';
		} else {
			obj.style.position = 'fixed';
			obj.style.top=0;
		}
	}

