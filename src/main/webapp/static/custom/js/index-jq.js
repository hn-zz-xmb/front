//焦点图
$(function(){
	var int = setInterval("move()",3000);
	$(".carousel").on("mouseover",".carousel_item",function(){
		clearInterval(int);
		$(".carousel_item").removeClass("active");
		$(this).addClass("active");
		var imgUrl = $(this).find(".carousel_item_img img").attr("src");
		var urlLink = $(this).find(".url").val();
		$("#target_id").attr("href",urlLink);
		$("#carousel_img_big").attr("src",imgUrl);
		$("#carousel_img_big").attr("tag","1");
	});
	$(".carousel").on("mouseover",".carousel_item",function(){
		int = setInterval("move()",3000);
	});
	
	$("#carousel_img_big").click(function(){
		var tag=$(this).attr("tag");
		if(tag!=undefined && tag=="2"){
			clickToShow();
		}
	})
});

function clickToShow(){
	var i = $.layer({
	    type : 1,
	    title : false,
	    fix : false,
	    offset:['' , ''],
	    area : ['650px','300px'],
	    page : {dom : '#tong'}
	});
}
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
	$("#carousel_img_big").attr("tag",$next.attr("tag"));
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
	$(".right").click(function(){
		$(".pic").animate({left:-1440},180,function(){
			$(".pic").css({left:0});
			$(".pic li:first").insertAfter($(".pic li:last"));
		});
		})
	$(".left").click(function(){
		$(".pic").animate({left:1440},180,function(){
			$(".pic").css({left:0});
			$(".pic li:last").insertBefore($(".pic li:first"));
		});
	})
});
$(function(){
	$("body").on("mouseover","#scroll",function(){
	$("#left,#right").show();
});
$("body").on("mouseleave","#scroll",function(){
	$("#left,#right").hide();
});
	});
	
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
