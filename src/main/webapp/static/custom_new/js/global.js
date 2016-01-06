// JavaScript Document
$(document).ready(function() {  
$('.foodnicelist ul li:nth-child(5n+1)').css({"border-left":"none"});
$('.foodnicelist ul li:nth-child(5n+5)').css({"border-right":"none"});
$('.foodnicelist ul li:nth-child(5n+1) a').css({"padding-left":"5px"});
$('.foodnicelist ul li:nth-child(n+6) a').css({"padding-top":"10px"}); 
$('.friends li:nth-child(10n+10)').css({"border-right":"none"});
$('.friends li:nth-child(10n+10)').css({"width":"117px"});
$('.recommend li:last').css({"border-right":"none"});
$('.recommend li:last').css({"width":"238px"});
$('.orderweb01 div i:last').css({"padding-right":"40px"});
$('.food li:nth-child(4n+4)').css({"border-right":"none"});
$('.food li:nth-child(-n+4)').css({"border-top":"none"}); 
$('.page a:first-child').css({"border-left":"1px #e6e9ed solid"});
$('.hotproductlist li:last').css({"border-bottom":"none"});
$('.shopmanagement th:last').css({"border-right":"none"});
$('.shopmanagement td:nth-child(7n+7)').css({"border-right":"none"}); 
$('.activelist ul li:last').css({"border-bottom":"none"}); 
$('.activelist ul li:odd').css({"background-color":"#fff"});
});


 

$(document).ready(function() {
$('.foodmain_Tab a').mouseover(function(){
$(this).addClass("current").siblings().removeClass();
$(".foodnicelist > ul").eq($(".foodmain_Tab a").index(this)).show().siblings().hide();
});

$('.login_tab a').mouseover(function(){
$(this).addClass("current").siblings().removeClass();
$(".login_info > ul").eq($(".login_tab a").index(this)).show().siblings().hide();
});

$('.payway_tab a').mouseover(function(){
$(this).addClass("current").siblings().removeClass();
$(".payway_type > li").eq($(".payway_tab a").index(this)).show().siblings().hide();
});

$('#payway_tab a').mouseover(function(){
$(this).addClass("current").siblings().removeClass();
$(".paycon > div").eq($("#payway_tab a").index(this)).show().siblings().hide();
});
 
 
});







$(document).ready(function() {
$(window).bind("load", function() {
    var footerHeight = 0;
    var footerTop = 0;
    positionFooter();
    function positionFooter() {
        footerHeight = $("#footer").height();
        footerTop = ($(window).scrollTop()+$(window).height()-footerHeight)+"px";
        //如果页面内容高度小于屏幕高度，div#footer将绝对定位到屏幕底部，否则div#footer保留它的正常静态定位
        if(($(document.body).height()) < $(window).height()){
            $("#footer").css({ position:"absolute",left:"0" }).stop().css({top:footerTop});
        }
		else{
			$("#footer").css({position:"static",left:"",top:""});
			}
    }
    $(window).scroll(positionFooter).resize(positionFooter);
});


});


 

//下拉
function searchtype(f) {
document.getElementById('searchlist').style.visibility= f ? 'visible' : 'hidden';
document.getElementById('searchtype').onclick = function () {searchtype(!f)};
}
function c_cleanLiA1(idx) {
var lis = document.getElementById('searchlist').getElementsByTagName('p');
for (var i = 0;i < lis.length;i++) {
lis[i].style.backgroundColor = i == idx ? '#fafafa' : '#fff'
}
}  
window.onload = function() { 
var lis =  document.getElementById('searchlist').getElementsByTagName('p');
for (var i = 0;i < lis.length;i++) {
lis[i].liIdx = i;
lis[i].onclick = function () {
document.getElementById('searchtype').value = this.getAttribute('pl');
document.getElementById('searchlist').style.visibility='hidden';
document.getElementById('searchtype').onclick = function () {searchtype(true)};	
}
lis[i].onmouseover = function () {
c_cleanLiA1(this.liIdx);
}
}    
}

 
	 