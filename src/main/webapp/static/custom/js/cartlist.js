/**
 * Created by rsp on 14-12-9.
 */
$(function(){
    $(".goods_info").on("click",".arrow_down",function(){
        $(this).parents(".goods_info").find(".goods_info_content").slideDown();
        $(this).text("点击关闭").removeClass("arrow_down").addClass("arrow_up");
        $(this).parents(".goods_info").find(".goods_info_head_r").hide();
    });
    $(".goods_info").on("click",".arrow_up",function(){
        $(this).parents(".goods_info").find(".goods_info_content").slideUp();
        $(this).text("点击查看").removeClass("arrow_up").addClass("arrow_down");
        $(this).parents(".goods_info").find(".goods_info_head_r").show();
    });

    $("a[name='deleteByStore_a']").each(function(){
        $(this).click(function(){
            var storeId=$(this).attr("id");
            deleteByStore(storeId);
        })
    })

    $("a[name='deleteCartItem']").each(function(){
        $(this).click(function(){
            var storeId=$(this).attr("storeId");
            var cartItemId=$(this).attr("cartItemId");
            deleteByCartItem(cartItemId,storeId);
        })
    })


    $("a[name='addCartItemNum']").each(function(){
        $(this).click(function(){
            var storeId=$(this).attr("storeId");
            var cartItemId=$(this).attr("cartItemId");
            changeNum(cartItemId,storeId,true);
        })
    })

    $("a[name='reduceCartItemNum']").each(function(){
        $(this).click(function(){
            var storeId=$(this).attr("storeId");
            var cartItemId=$(this).attr("cartItemId");
            changeNum(cartItemId,storeId,false);
        })
    })

});

/**
 * 删除店铺整个购物车
 */
function deleteByStore(storeId){
    var url=MSConfig.BaseURL+"/cart/deleteByStore";
    var params={"store_id":storeId};
    var result=callJson(url,params,false);
    if(result.isDelete){
        //删除操作
        $("#store_"+storeId).hide();
    }
}

/**
 * 删除购物车里面的某个商品
 */
function deleteByCartItem(cartItemId,storeId){
    var params={"cartItemId":cartItemId,  "storeId":storeId}
    var result=callJson(MSConfig.BaseURL+"/cart/remove",params);
    if(result.isDelete){
        var cartItemTr=$("#cartItem_"+cartItemId);
        cartItemTr.remove();
        $("#totalMoney_"+storeId).text(result.total+"元");
    }
}

/**
 * 更改数量
 */
function changeNum(cartItemId,storeId,isAdd){
    if(!isAdd){
        var cartItemCount=$("#count_"+cartItemId);
        if(cartItemCount.text()<1){
            return ;
        }
    }
    var params={"cartItemId":cartItemId,
        "storeId":storeId,
        "isAdd":isAdd}
    var result=callJson(MSConfig.BaseURL+"/cart/changeCount",params);
    if(result.isChange){
        if(result.count==0){
            var cartItemTr=$("#cartItem_"+cartItemId);
            cartItemTr.remove();
        }
        var cartItemCount=$("#count_"+cartItemId);
        cartItemCount.text(result.count);

        var itemPrice=$("#itemPriceTd_"+cartItemId);
        $("#totalMoney_"+storeId).text("￥"+result.total);
        $("#totalMoney_top_"+storeId).text("￥"+result.total);
        
        var itemTotalMoney=(Number(cartItemCount.text()*itemPrice.text())).toFixed(2);
        $("#totalMoneyTd_"+cartItemId).text(itemTotalMoney);
    }
}

