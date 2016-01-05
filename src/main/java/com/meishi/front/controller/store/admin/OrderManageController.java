package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.common.AppContextData;
import com.meishi.front.common.rabbit.SendRabbitMQMsg;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.*;
import com.meishi.util.DateUtil;
import com.meishi.util.GenerateUtils;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;
import com.meishi.util.msg.SendMsgTemplate;

import net.sf.json.JSONObject;

import java.util.*;

//@Controller(controllerKey = "/store/admin/order")
@Before(StoreInteceptor.class)
public class OrderManageController extends BaseController {
	private static Logger log = Logger.getLogger(OrderManageController.class);

	// 查询当前店铺中所有的订单

	public void index() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("consume_method", getPara("consume_method"));// 订单类型(外卖、到店消费)
		params.put("status", getPara("status"));// 订单状态
		params.put("orderNo", getPara("orderNo"));// 订单编号
		params.put("fromTime", getPara("fromTime"));// 订单开始时间
		params.put("toTime", getPara("toTime"));// 订单结束时间
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
		Store store = getSessionAttr("store");// 获取店铺
		params.put("storeId", store.getStr("id"));
		Page<SubscribeOrder> list = SubscribeOrder.dao.findStoreOrders(pageNum,
				pageSize, params);// 店铺订单分页
		setAttr("page", list);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		setAttr("dataDics", Datadic.dao.findByGroup("order_status"));// 订单状态
		render("/store/admin/order/order_list1.html");
	}

	// 订单详情
	public void showorder() {
		if (getPara("order_type").equals("arrive_eat")) {
			SubscribeOrder orders = SubscribeOrder.dao
					.orderDetails(getPara("orderId"));
			List<SubscribeOrderItem> orderDetails = SubscribeOrderItem.dao
					.findByOrderId(getPara("orderId"));
			setAttr("order", orders);
			setAttr("orderDetail", orderDetails);
			setAttr("orderType","arrive_eat");
		} else if (getPara("order_type").equals("take_out")) {
			TakeoutOrder orders = TakeoutOrder.dao
					.orderDetails(getPara("orderId"));
			List<TakeoutOrderItem> orderDetails = TakeoutOrderItem.dao
					.findByOrderNo(getPara("orderId"));
			setAttr("order", orders);
			setAttr("orderDetail", orderDetails);
			setAttr("orderType","take_out");
		}
		render("/store/admin/order/orderDetail.html");
	}

	// 发放代金券时弹出弹框
	public void getDialog() {
		Store store = getSessionAttr("store");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", store.getStr("id"));

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
		Page<Coupon> result = Coupon.dao.findCouponByStoreId(pageNum, pageSize,
				params);
		setAttr("page", result);
		setAttr("params", paramsToStr(params));
		render("");
	}

	// 返代金券弹出框
	public void returncoupon() {
		Store store = getSessionAttr("store");
		String storeId = "";
		if (getPara("order_type").equals("subscribe")) {
			SubscribeOrder orders = SubscribeOrder.dao
					.findById(getPara("orderId"));

			if (orders == null
					|| !orders.getStr("store_id").equals(store.get("id"))) {
				renderError(404);
			}
			setAttr("orders", orders);
		} else if (getPara("order_type").equals("takeout")) {
			TakeoutOrder orders = TakeoutOrder.dao.findById(getPara("orderId"));

			if (orders == null
					|| !orders.getStr("store_id").equals(store.get("id"))) {
				renderError(404);
			}
			setAttr("orders", orders);
		}
		List<Coupon> coupons = Coupon.dao.findByStoreId(storeId);
		setAttr("coupons", coupons);

		render("/store/admin/order/couponDialog.html");
	}

	// 发放代金券
	public void sendcoupon() {
		JSONObject result = new JSONObject();
		String userId = getPara("userId");
		String couponId = getPara("couponId");
		String orderId = getPara("orderId");
		Orders orders = Orders.dao.findById(orderId, "id,is_return_coupon");
		if (StringUtil.isBlank(userId) || StringUtil.isBlank(couponId)) {
			result.put("status", false);
			result.put("error", "操作有误");
			renderJson(result.toString());
			return;
		}
		if (orders.getInt("is_return_coupon") == 1) {
			result.put("status", false);
			result.put("error", "该订单已经返过代金券");
			renderJson(result.toString());
			return;
		}
		MemberCoupon memberCoupon = new MemberCoupon();
		memberCoupon
				.set("id", ToolUtil.getUuidByJdk(true))
				.set("coupon_id", couponId)
				.set("user_id", userId)
				.set("is_used", 0)
				.set("source", "coupon_return")
				.set("code", GenerateUtils.generateRandom(13))
				.set("send_time", DateUtil.format(new Date(), "yyyyMMddHHmmss"))
				.set("source", AppContextData.coupon_source_return).save();
		orders.set("is_return_coupon", 1).update();
		result.put("status", true);
		renderJson(result.toString());
	}

	/**
	 *  确认退单
	 */
	public void confirmRefund() {
		String orderId = getPara("orderId");
		String orderType = getPara("orderType");
		if(AppContextData.ARRIVE_EAT.equals(orderType)){
			SubscribeOrder.dao.refund(orderId);
		}else{
			TakeoutOrder.dao.refund(orderId);
		}
		redirect("/salerManage/ordermanage");
	}

	/**
	 * 确认配送
	 */
	public void confirmDelivery() {
		String orderId = getPara("orderId");
		TakeoutOrder order = TakeoutOrder.dao.findById(orderId);
		Store store = Store.dao.findById(order.get("store_id"));
		Member member = Member.dao.findById(order.get("user_id"));
		if (order.getStr("status").equals(AppContextData.SHIPPED)) {
			renderText("fail");
		} else {
			order.set("status", AppContextData.SHIPPED).set("update_time",DateUtil.format(new Date(), DateUtil.pattern_ymd_hms)).update();
			// 发短信通知买家
			HashMap map2 = new HashMap();
			map2.put("msgType", SendMsgTemplate.SETMEALSURE_CODE);
			map2.put("phone", order.get("contacts_type"));
			List<String> params = new LinkedList<String>();// 短信参数
			params.add(member.getStr("username"));
			params.add(store.getStr("name"));
			map2.put("params", params);
			SendRabbitMQMsg.service.sendPhoneMsg(map2);
			renderText("success");
		}
	}

}
