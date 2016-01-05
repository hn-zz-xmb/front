/**
 * 更改数量
 */
function changeCouponCount(couponId,isAdd){
	var cartItemCount=$("#couponcount");
	if(!isAdd){
        if(cartItemCount.text()<=1){
            return ;
        }
    }
    var params={"couponId":couponId,
        "count":cartItemCount.text(),"isAdd":isAdd}
    var result=callJson(MSConfig.BaseURL+"/buycoupon/changeCount",params);
    if(result.isChange){
        cartItemCount.text(result.count);
        $("#totalmoney").text(result.totalmoney+"元");
    }
}


/**
 * 跳转到填写订单页面
 */
function buy(couponId){
    //处理
    //验证是否登陆
	var params={
		"couponId":couponId	
	}
    var result=callJson(MSConfig.BaseURL+"/buycoupon/tobuy",params,false);
    if(!result.isCart){
        layer.msg('请先确定商品存在', 2, 3);
        return;
    }
    if(result.isLogin){
        window.location.href=MSConfig.BaseURL+"/buycoupon/couponpay?couponId="+couponId;
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



