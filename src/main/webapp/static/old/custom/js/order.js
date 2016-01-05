$(function() {
	var receiverHtml = $("#receiver").html();
	$("#receiver").children().remove();
	var yudingHtml = $("#yuding").html();

	var fee = $("#takeOutFee").val() - 0;
	var totalPrice = $("#totalPrice").val() - 0;

	$("#order_amount").text("￥" + totalPrice.toFixed(2));

	$("#arrive_eat").click(function() {
		$("#yuding").html(yudingHtml);
		$("#receiver").children().remove();
		$("#fee").hide();
		$("#order_amount").text("￥" + totalPrice.toFixed(2));
	});

	$("#take_out").click(function() {
		$("#receiver").html(receiverHtml);
		$("#yuding").children().remove();
		$("#fee").show();
		$("#order_amount").text("￥" + (totalPrice + fee).toFixed(2));
		init();
	});
});
function showYuding() {
	$("#yuding").show();
	$("#receiver").hide();
}
function showReceiver() {
	$("#yuding").hide();
	$("#receiver").show();
}

/**
 * 提交订单
 */
function submit_Order(ctxPath) {
	var actionUrl = ctxPath + "/order/makeorder"
	var data = {};
	var consumeMethod = $("input[name='consumeMethod']:checked").val();
	if (!checkOrder(consumeMethod)) {
		return;
	}

	$("#submit_id").attr("disabled", true);
	data = orderParam(consumeMethod);
	var result = callJson(actionUrl, data, true);
	if (!result.isCsrf) {
		alert("非法请求");
		return;
	}
	if (!result.hasCart) {
		alert("确认你的购物车有信息");
		return;
	}
	if (result.success) {
		var successUrl = ctxPath + "/order/ok";
		window.location.href = successUrl + "?order_id=" + result.orderId
				+ "&rid=" + Math.random();
		return;
	}
}
// 验证订单信息
function checkOrder(consumeMethod) {
	if (isEmpty(consumeMethod)) {
		alert("选择消费方式");
		return false;
	}
	
	// 到店消费
	if (consumeMethod == 1) {
		var buyerName = $("#buyerName").val();
		var peopleNum = $("#peopleNum").val();
		var eatTime = $("#eatTime").val();
		var eatTime2 = $("#eatTime2").val();
		var phone = $("#phone").val();
		if (isEmpty(buyerName)) {
			alert("请输入姓名");
			return false;
		}
		if (isEmpty(peopleNum)) {
			alert("选择就餐人数");
			return false;
		}
		var patt1=new RegExp("^[0-9]*$");
		if(!patt1.test(peopleNum)){
			alert("请填写正确的就餐人数！");
			return false;
		}
		if (isEmpty(eatTime)||isEmpty(eatTime2)) {
			alert("选择消费时间");
			return false;
		}
		if (isEmpty(phone)) {
			alert("请填写联系电话");
			return false;
		}
		return true;
	}
	// 外卖
	else if (consumeMethod == 2) {
		var buyerName = $("#buyerName_").val();
		var buyerAddress = $("#buyerAddress").val();
		var zipcode = $("#zipcode").val();
		var code = /^[1-9][0-9]{5}$/;
		var phone_ = $("#phone_").val();
		var regPhone=/1[3-8]+\d{9}/;
		if (isEmpty(buyerName)) {
			alert("请输入姓名");
			return false;
		}
		if (isEmpty(buyerAddress)) {
			alert("请输入收货地址");
			return false;
		}
		if (trimTxt(zipcode) == "") {
			alert("请填写你的邮编号码!");
			return false;
		}else if(!code.test(zipcode)){
			alert("邮编格式不正确!");
			return false;
		}
		if (trimTxt(phone_) == "") {
			alert("请输入你的手机号码!");
			return false;
		}else if(!regPhone.test(phone_)){
			alert("手机格式不正确!");
			return false;
		}
		return true;
	}

}
/**
 * 处理参数
 * 
 * @param consumeMethod
 * @returns {___anonymous2492_2493}
 */
function orderParam(consumeMethod) {
	var data = {};
	// 到店消费
	if (consumeMethod == 1) {
		var storeId = $("#storeId").val();
		var consumeMethod = $("input[name='consumeMethod']:checked").val();
		var buyerName = $("#buyerName").val();
		var peopleNum = $("#peopleNum").val();
		var eatTime = $("#eatTime").val()+$("#eatTime2").val();
		var phone = $("#phone").val();
		var postscript = $("#postscript").val();
		// 获取是否使用优惠券
		var isCoupon = 0;
		var s = '';
		$('input[name="myCouponCheckBox"]:checked').each(function() {
			s += $(this).val() + ',';
		});

		if (s) {
			isCoupon = 1;
		}
		data = {
			"storeId" : storeId,
			"consumeMethod" : consumeMethod,
			"buyerName" : buyerName,
			"peopleNum" : peopleNum,
			"eatTime" : eatTime,
			"phone" : phone,
			"postscript" : postscript,
			"coupon" : isCoupon,
			"member_couponId" : s,
			"isAjax" : "1"
		};
	} else if (consumeMethod == 2) {
		var storeId=$("#storeId").val();
		var consumeMethod = $("input[name='consumeMethod']:checked").val();
		var buyerName = $("#buyerName_").val();
		var buyerAddress = $("#buyerAddress").val();
		var zipcode=$("#zipcode").val();
		var phone=$("#phone_").val();//"postscript" : postscript,
		var postscript = $("#postscript").val();
		var delive=$("#delive").val();
		data={
				"storeId":storeId,
				"consumeMethod":consumeMethod,
				"buyerName":buyerName,
				"buyerAddress":buyerAddress,
				"zipcode":zipcode,
				"phone":phone,
				"postscript" : postscript,
				"delive":delive
		}
		
	}
	return data;
}
/**
 * 加载我的优惠券
 */
var isRepeatFind = false;
function findMyCoupon() {
	if (isRepeatFind) {
		$("#myCouponTable tr").remove();
		$("#showMyCoupon").hide();
		isRepeatFind = false;
		return;
	}
	var myCouponUrl = OrderAppConfig.Domain + "/buyer/ajaxCoupon?rid="
			+ Math.random();
	$
			.ajax({
				url : myCouponUrl,
				type : "GET",
				datatype : "json",
				success : function(data) {
					data = eval("(" + data + ")");
					if (data.has) {// 返回数据处理
						var showMyCoupon = $("#showMyCoupon");
						$
								.each(
										data.result,
										function(i, item) {
											$(
													"<tr><td><input type='checkbox' id='checkall' name='myCouponCheckBox' onclick='checkAll(this)' value='"
															+ item.id
															+ "'/></td>"
															+ "<td>"
															+ item.coupon.name
															+ "</td><td>"
															+ item.coupon.couponMoney
															+ "</td></tr>")
													.insertAfter(
															$("#myCouponTable"));
										});
						showMyCoupon.show();
					} else {// 没有你要的优惠券信息
						alert("没有你要的优惠券信息");
					}
					isRepeatFind = true;
				}
			});
}
