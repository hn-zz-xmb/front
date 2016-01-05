// JavaScript Document
// JavaScript Document
$(document).ready(function(){
    var stepW = 24;
    var description = new Array("1分非常差，回去再练练","2分真的是差，都不忍心说你了","3分一般，还过得去吧","4分很好，是我想要的东西","5分太完美了，此得几回闻!");
    var stars = $("#star1 > li");
    var descriptionTemp;
    $("#showb1").css("width",0);
    stars.each(function(i){
        $(stars[i]).click(function(e){
            var n = i+1;
            $("#env_points").val(n);
            $("#showb1").css({"width":stepW*n});
            descriptionTemp = description[i];
            $(this).find('a').blur();
            return stopDefault(e);
            return descriptionTemp;
        });
    });
    stars.each(function(i){
        $(stars[i]).hover(
            function(){
                $(".description1").text(description[i]);
            },
            function(){
                if(descriptionTemp != null){
                    $(".description1").text("当前您的评价为："+descriptionTemp);
                }else{ 
                    $(".description1").text(" ");
                }
            }
        );
    });
});
$(document).ready(function(){
    var stepW = 24;
    var description = new Array("1分非常差，回去再练练","2分真的是差，都不忍心说你了","3分一般，还过得去吧","4分很好，是我想要的东西","5分太完美了，此得几回闻!");
    var stars = $("#star2 > li");
    var descriptionTemp;
    $("#showb2").css("width",0);
    stars.each(function(i){
        $(stars[i]).click(function(e){
            var n = i+1;
            $("#server_points").val(n);
            $("#showb2").css({"width":stepW*n});
            descriptionTemp = description[i];
            $(this).find('a').blur();
            return stopDefault(e);
            return descriptionTemp;
        });
    });
    stars.each(function(i){
        $(stars[i]).hover(
            function(){
                $(".description2").text(description[i]);
            },
            function(){
                if(descriptionTemp != null)
                    $(".description2").text("当前您的评价为："+descriptionTemp);
                else 
                    $(".description2").text(" ");
            }
        );
    });
});
$(document).ready(function(){
    var stepW = 24;
    var description = new Array("1分非常差，回去再练练","2分真的是差，都不忍心说你了","3分一般，还过得去吧","4分很好，是我想要的东西","5分太完美了，此得几回闻!");
    var stars = $("#star3 > li");
    var descriptionTemp;
    $("#showb3").css("width",0);
    stars.each(function(i){
        $(stars[i]).click(function(e){
            var n = i+1;
            $("#taste_points").val(n);
            $("#showb3").css({"width":stepW*n});
            descriptionTemp = description[i];
            $(this).find('a').blur();
            return stopDefault(e);
            return descriptionTemp;
        });
    });
    stars.each(function(i){
        $(stars[i]).hover(
            function(){
                $(".description3").text(description[i]);
            },
            function(){
                if(descriptionTemp != null)
                    $(".description3").text("当前您的评价为："+descriptionTemp);
                else 
                    $(".description3").text(" ");
            }
        );
    });
});
function stopDefault(e){
    if(e && e.preventDefault)
           e.preventDefault();
    else
           window.event.returnValue = false;
    return false;
};



