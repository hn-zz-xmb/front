/**
 * Created by rsp on 14-11-14.
 */z

/**
 * 删除购物车
 */
function removeCart(cartItemId,storeId){
    var params={"cartItemId":cartItemId,  "storeId":storeId}
    var result=callJson(MSConfig.BaseURL+"/cart/remove",params);
    if(result.isDelete){
        var cartItemTr=$("#cartItem_"+cartItemId);
        cartItemTr.remove();
        $("#totalMoney").text(result.total+"元");
        $("#my_menu_btn").text(result.totalCount);
    }
}

/**
 * 更改数量
 */
function changeCount(cartItemId,storeId,isAdd){
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
        $("#totalMoney").text(result.total+"元");
        $("#my_menu_btn").text(result.totalCount);
    }
}

/**
 * 清空
 */
function clearCart(storeId){
    var params={"storeId":storeId}
    var result=callJson(MSConfig.BaseURL+"/cart/clear",params);
    if(result.isClear){
        var menu=$("#menu_div");
        menu.html("暂无商品");
        $("#totalMoney").text("0.00元");
        $("#my_menu_btn").text(0);
    }
}

/**
 * 跳转到填写订单页面
 */
function toWriteOrder(storeId){
    //处理
    //验证是否登陆
    var result=callJson(MSConfig.BaseURL+"/cart/toWriteOrder",null,false);
    if(!result.isCart){
        layer.msg('请先确定购物车有物品', 2, 3);
        return;
    }
    if(result.isLogin){
        window.location.href=MSConfig.BaseURL+"/order?storeId="+storeId;
    }else{
        $.layer({
            type: 2,
            shade: [0],
            fix: false,
            title: '登录',
            maxmin: true,
            iframe: {src: MSConfig.BaseURL+'/login/ajaxUI'},
            area: ['464px', '543px'],
            close: function (index) {
                //layer.getChildFrame('#name', index).val()
                //document.location.reload();
            }
        });
    }
}



