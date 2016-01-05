/**
 * Created by rsp on 14-11-14.
 */


/**
 * 添加购物车
 */
function addCart(storeId,itemId,isSetMeal,obj,dynamicObj){
    var params={"storeId":storeId,"itemId":itemId,"isSetMeal":isSetMeal};
    var result=callJson(MSConfig.BaseURL+"/cart/add",params,false);
    if(result.isAdd){
        $("#my_menu_btn").text(result.totalCount);
        var divTop = $(obj).offset().top;
        var divLeft = $(obj).offset().left;
        dynamicObj.css({ "position": "absolute", "z-index": "501", "left": divLeft + "px", "top": divTop + "px","display":"block" });
        dynamicObj.animate({ "left": ($("#my_menu_btn").offset().left - $("#my_menu_btn").width()) + "px", "top": ($("#my_menu_btn").offset().top + 30) + "px", "width": "50px", "height": "50px" }, 500, function () {
            dynamicObj.animate({ "left": $("#my_menu_btn").offset().left + "px", "top": $("#my_menu_btn").offset().top + "px" }, 500).hide(0);
        });
    }
}
