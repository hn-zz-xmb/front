package com.meishi.front.controller.member.usercenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.common.AppContextData;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.*;
import com.meishi.util.DateUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller(controllerKey = "/account/buyerorder")
@Before(LoginInteceptor.class)
public class BuyerOrderController extends BaseController {
	private static Logger log = Logger.getLogger(BuyerOrderController.class);

	// 订单列表
	public void findUserOrder() {
		Member member = Member.dao.findById(getUserIds());
		Map<String, Object> params = new HashMap<String, Object>();
		if (member != null) {
			setAttr("dataDicList",
					Datadic.dao.findByGroup(AppContextData.ORDER_STATUS));
			params.put("consume_method", getPara("consume_method"));// 订单类型(外卖、到店消费)
			params.put("orderNo", getPara("orderNo"));
			params.put("orderTime", getPara("orderTime"));
			params.put("startTime", getPara("startTime"));
			params.put("endTime", getPara("endTime"));
			params.put("status", getPara("status"));
			params.put("apprise", getPara("apprise"));
			params.put("userId", member.getStr("id"));
			Integer pageNum = getParaToInt("pageNum");
			Integer pageSize = getParaToInt("pageSize");
			if (pageNum == null || pageNum.intValue() < 1) {
				pageNum = 1;// 默认第一页
			}
			if (pageSize == null || pageSize.intValue() < 0) {
				pageSize = 10;// 默认每页显示10条
			} else if (pageSize.intValue() > 100) {
				pageSize = 10;// 最大100条
			}
			Page<SubscribeOrder> result = SubscribeOrder.dao.findUserOrders(pageNum, pageSize,
					params);
			setAttr("page", result);
			setAttr("params", params);
			setAttr("page_params", paramsToStr(params));
			render("/usercenter/userorderlist1.html");
		} else {
			render("/login");
		}
	}

	// 订单详情
	public void orderDetail() {
		Orders orders = Orders.dao.orderDetails(getPara("orderId"));
		List<OrderDetail> orderDetails = OrderDetail.dao.findByOrderNo(orders
				.getStr("order_no"));
		setAttr("order", orders);
		setAttr("orderDetail", orderDetails);
		render("/usercenter/orderDetail.html");
	}

	// 取消订单
	public void cancleOrder() {
		String orderType=getPara("orderType");
		String orderId=getPara("orderId");
		if(AppContextData.ARRIVE_EAT.equals(orderType)){
			SubscribeOrder subscribeOrder=SubscribeOrder.dao.findById(orderId);
			if("submitted".equals(subscribeOrder.getStr("status"))){
				subscribeOrder.set("status", "canceled")
				.set("update_time", DateUtil.format(new Date(), "yyyyMMddHHmmss"))
				.update();
			}
		}else{
			TakeoutOrder takeoutOrder = TakeoutOrder.dao.findById(orderId);
			if("submitted".equals(takeoutOrder.getStr("status"))){
				takeoutOrder.set("status", "canceled")
				.set("update_time", DateUtil.format(new Date(), "yyyyMMddHHmmss"))
				.update();
			}
		}
		renderText("success");
	}

	/**
	 * 申请退单
	 */
	public void applyRefund() {
		String orderId = getPara("orderId");
		String orderType = getPara("orderType");
		if(AppContextData.ARRIVE_EAT.equals(orderType)){
			SubscribeOrder subscribeOrder=SubscribeOrder.dao.findById(orderId);
			if("payed".equals(subscribeOrder.getStr("status"))){
				subscribeOrder.set("status", "refunding")
				.set("update_time", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms))
				.update();
			}
		}else{
			TakeoutOrder takeoutOrder = TakeoutOrder.dao.findById(orderId);
			if("payed".equals(takeoutOrder.getStr("status"))){
				takeoutOrder.set("status", "refunding")
				.set("update_time", DateUtil.format(new Date(), "yyyyMMddHHmmss"))
				.update();
			}
		}
		renderText("success");
	}

	/**
	 * 确认收货
	 */
	public void suregetgoods() {
		String orderId = getPara("orderId");
		TakeoutOrder.dao.sureGetGoods(orderId);
		renderText("success");
	}
	
}
