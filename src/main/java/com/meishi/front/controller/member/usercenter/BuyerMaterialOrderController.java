package com.meishi.front.controller.member.usercenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.common.AppContextData;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.*;
import com.meishi.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Controller(controllerKey = "/account/buyerorder")
@Before(LoginInteceptor.class)
public class BuyerMaterialOrderController extends BaseController {
	private static Logger log = Logger.getLogger(BuyerMaterialOrderController.class);

	// 订单列表
	public void findUserOrder() {
		Member member = Member.dao.findById(getUserIds());
		Map<String, Object> params = new HashMap<String, Object>();
		if (member != null) {
			setAttr("dataDicList",
					Datadic.dao.findByGroup(AppContextData.ORDER_STATUS));
			params.put("consume_method", getPara("consume_method"));// 订单类型(外卖、到店消费)
			params.put("orderNo", getPara("orderNo"));//订单编号
			params.put("orderTime", getPara("orderTime"));//下单时间
			params.put("startTime", getPara("startTime"));
			params.put("endTime", getPara("endTime"));
			params.put("status", getPara("status"));//订单状态
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
			Page<MaterialOrder> page = MaterialOrder.dao.findPage(pageNum,
					pageSize, params);
			setAttr("page", page);
			setAttr("params", params);
			setAttr("page_params", paramsToStr(params));
			render("/usercenter/usermaterialorderlist1.html");
		} else {
			render("/login");
		}
	}

	// 订单详情
		public void orderDetail() {
			MaterialOrder orders = MaterialOrder.dao
					.findMaterial(getPara("orderId"));
			if(orders!=null){
				List<MaterialOrderDetail> orderDetails = MaterialOrderDetail.dao
					.getMaterialOrderDetail(orders.getStr("order_no"));
				setAttr("order", orders);
				setAttr("orderDetail", orderDetails);
				render("/order/orderDetail.html");
			}else{
				renderError(500);
			}
		}

		// 删除我的订单
			public void deleteOrder() {
				MaterialOrder orders = MaterialOrder.dao.findById(getPara("orderId"));
				if(orders!=null){
					MaterialOrder.dao.deleteById(getPara("orderId"));
					redirect("/buyerMaterOrder/findUserOrder");
				}else{
					renderError(500);
				}
			}

			// 申请退款交易关闭
			public void applyRefund() {
				MaterialOrder orders = MaterialOrder.dao.findById(getPara("orderId"));
				if(orders!=null){
					if("payed".equals(orders.getStr("order_status"))){
						orders.set("order_status", AppContextData.OrderStatus.refunding.name())
						.set("reason", "")
						.set("reason_result", "").update();
						redirect("/buyerMaterOrder/findUserOrder");
					}else{
						renderError(500);
					}
				}else{
					renderError(404);
				}
			}
			
			//取消订单交易关闭
			public void cancelOrder(){
				MaterialOrder order= MaterialOrder.dao.findById(getPara("orderId"));
				if(order!=null){
					if( "submitted".equals(order.getStr("order_status"))){
						order.set("order_status", AppContextData.OrderStatus.cancelled.name())
						.set("reason",AppContextData.OrderStatus.cancelled.name())
						.set("reason_result", AppContextData.OrderStatus.finished.name()).update();
						if(StringUtil.isNotBlank(order.getStr("member_coupon_id"))){
							MemberCoupon cou=MemberCoupon.dao.findById(order.getStr("member_coupon_id"));
							if(cou!=null){
								cou.set("is_used", 0).update();
							}
						}
						redirect("/buyerMaterOrder/findUserOrder");
					}else{
						renderError(500);
					}
				}else{
					renderError(404);
				}
			}
			
			// 申请退货交易关闭
			public void applyReturn() {
				MaterialOrder orders = MaterialOrder.dao.findById(getPara("orderId"));
				if(orders!=null){
					if("shipped".equals(orders.getStr("order_status"))){
						orders.set("order_status", AppContextData.OrderStatus.refunded.name())
						.set("reason",AppContextData.OrderStatus.refunded.name())
						.set("reason_result", "").update();
						redirect("/buyerMaterOrder/findUserOrder");
					}else{
						renderError(500);
					}
				}else{
					renderError(404);
				}
			}
			// 确认收货交易成功
			public void Receipt() {
				MaterialOrder orders = MaterialOrder.dao.findById(getPara("orderId"));
				if(orders!=null){
					if("shipped".equals(orders.getStr("order_status"))){
						MaterialOrder.dao.suregetgoods(orders);
						redirect("/buyerMaterOrder/findUserOrder");
					}else{
						renderError(500);
					}
				}else{
					renderError(404);
				}
			}
}
