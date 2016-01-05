//焦点图
$(function(){
	var int = setInterval("move()",3000);
	$(".carousel").on("mouseover",".carousel_item",function(){
		clearInterval(int);
		$(".carousel_item").removeClass("active");
		$(this).addClass("active");
		var imgUrl = $(this).find(".carousel_item_img img").attr("src");
		$("#carousel_img_big").attr("src",imgUrl);
	});
	$(".carousel").on("mouseover",".carousel_item",function(){
		int = setInterval("move()",3000);
	});
});
function move(){
	var $active=$(".carousel_item.active");
	var $next=$active.next(".carousel_item");
	if($next.length==0){
		$next=$($(".carousel_item")[0]);
	}
	$active.removeClass("active");
	$next.addClass("active");
	var imgUrl = $next.find(".carousel_item_img img").attr("src");
	$("#carousel_img_big").attr("src",imgUrl);
}
//商品筛选
function showup(){
var buyBR02Hide=document.getElementById("buyBR02Hide");
  if(buyBR02Hide.style.display=='block'){
     buyBR02Hide.style.display='none';
	 buy_down.style.background='url(images/selection_down.png)';
        }else{
    buyBR02Hide.style.display='block';
	buy_down.style.background='url(images/selection_up.png)';
     }
}
//推荐商品
$(function(){
	var n=0;
	$(".left").click(function(){
		n++;
		if(n>4) n=4;
		$(".pic").animate({left:-166*n},180);
		})
	$(".right").click(function(){
		n--;
		if(n<0) n=0;
		$(".pic").animate({left:-166*n},180);
		})                           
	})
	
//人气美食
function switchTab(ProTag, ProBox) {
            for (i = 1; i < 5; i++) {
                if ("tab" + i == ProTag) {
                    document.getElementById(ProTag).getElementsByTagName("a")[0].className = "on";
                } else {
                    document.getElementById("tab" + i).getElementsByTagName("a")[0].className = "";
                }
                if ("con" + i == ProBox) {
                    document.getElementById(ProBox).style.display = "";
                } else {
                    document.getElementById("con" + i).style.display = "none";
                }
            }
        }
function switchTab(ProTag, ProBox) {
            for (i = 1; i < 4; i++) {
                if ("tab" + i == ProTag) {
                    document.getElementById(ProTag).getElementsByTagName("a")[0].className = "on";
                } else {
                    document.getElementById("tab" + i).getElementsByTagName("a")[0].className = "";
                }
                if ("con" + i == ProBox) {
                    document.getElementById(ProBox).style.display = "";
                } else {
                    document.getElementById("con" + i).style.display = "none";
                }
            }
        }

$(function(){
	$("body").on("mouseover","#scroll",function(){
	$("#left,#right").show();
});
$("body").on("mouseleave","#scroll",function(){
	$("#left,#right").hide();
});
	});
