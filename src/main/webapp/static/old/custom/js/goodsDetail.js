/**
 * 
 */

$(function() {
	init();

	$('#show_s li').each(function() {
		$(this).mouseover(function() {
			show(this.value);
		});
		$(this).mouseover(function() {
			showLi(this.value);
		});

	});
});
// 初始化
function init() {
	show(1);
	showLi(1);	
	
	setImg_Pre();
}

function setView(id) {
	document.getElementById("a1").className = "";
	document.getElementById("a2").className = "";
	document.getElementById("a3").className = "";
	document.getElementById("a4").className = "";
	document.getElementById("a" + id).className = "nav_on";
}

//显示切换效果1
function show(index) {
	for (var i = 1; i <= 4; i++) {
		if (i == index) {
			$("#show_" + i).show();
			continue;
		}
		$("#show_" + i).hide();
	}
}

//显示切换效果2
function showLi(index) {
	for (var i = 1; i <= 4; i++) {
		if (i == index) {
			$("#liInfo_" + i).addClass("this");
			continue;
		}
		$("#liInfo_" + i).removeClass("this");
	}
}

var options={
		 zoomType: 'standard',
         lens:true,
         preloadImages: false,
         alwaysOn:false 
	};

//显示图片预览效果
function setImg_Pre(){
	 $('.jqzoom').jqzoom(options); 
}

