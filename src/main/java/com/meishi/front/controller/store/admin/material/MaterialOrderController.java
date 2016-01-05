package com.meishi.front.controller.store.admin.material;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.*;
import com.meishi.util.GenerateUtils;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//@Controller(controllerKey = "/salermanage/materialmanage")
@Before(StoreInteceptor.class)
public class MaterialOrderController extends BaseController {
	private static Logger log = Logger.getLogger(MaterialOrderController.class);


	// 查询原料订单(店家中心)
	public void index() {
		Store store = getSessionAttr("store");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", getPara("status"));// 订单状态
		params.put("orderNo", getPara("orderNo"));// 订单编号
		params.put("fromTime", getPara("fromTime"));// 订单开始时间
		params.put("toTime", getPara("toTime"));// 订单结束时间
		params.put("buyername",getPara("buyername"));//买家昵称
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
		Page<MaterialOrder> page = MaterialOrder.dao.findMaterialOrder(pageNum,
				pageSize, params);
		setAttr("page", page);
		setAttr("params", params);
		setAttr("page_params", paramsToStr(params));
		setAttr("dataDics", Datadic.dao.findByGroup("order_status"));// 订单状态
		render("/store/admin/material/order/order_list.html");
	}

	// 查询订单详情（店家中心）
	public void findOrder() {
		MaterialOrder materialOrder = MaterialOrder.dao.findMaterial(getPara("id"));// 订单
		List<MaterialOrderDetail> orderDetail = MaterialOrderDetail.dao
				.getMaterialOrderDetail(materialOrder.getStr("order_no"));// 子订单
		setAttr("materialOrder", materialOrder);
		setAttr("orderDetails", orderDetail);
		render("/store/admin/material/order/orderDetail.html");
	}
	
	// 返代金券弹出框
		public void returncoupon() {
			Store store = getSessionAttr("store");
			MaterialOrder orders = MaterialOrder.dao.findById(getPara("orderId"));
			if (orders == null
					|| !orders.getStr("store_id").equals(store.get("id"))) {
				renderError(404);
			}
			List<Coupon> coupons = Coupon.dao.findByStoreId(orders
					.getStr("store_id"));
			setAttr("coupons", coupons);
			setAttr("userId", orders.get("member_id"));
			render("/store/admin/material/order/couponDialog.html");
		}
		// 发放代金券
		public void sendcoupon() {
			JSONObject result = new JSONObject();
			String userId = getPara("userId");
			String couponId = getPara("couponId");
			if (StringUtil.isBlank(userId) || StringUtil.isBlank(couponId)) {
				result.put("status", false);
				result.put("error", "操作有误");
				renderJson(result.toString());
				return;
			}
			MemberCoupon memberCoupon = new MemberCoupon();
			memberCoupon.set("id", ToolUtil.getUuidByJdk(true))
					.set("coupon_id", couponId).set("user_id", userId)
					.set("is_used", 0)
					.set("code", GenerateUtils.generateRandom(13)).save();
			result.put("status", true);
			renderJson(result.toString());
		}
		
	//确认发货
		public void shipping(){
			MaterialOrder orders = MaterialOrder.dao.findById(getPara("id"));
			if(orders!=null){
				if("payed".equals(orders.getStr("order_status")) && "invite".equals(orders.getStr("delivery_method"))){
					orders.set("order_status", "shipped").update();
					redirect("/salerManage/materialManage/ordermanage");
				}else{
					renderError(500);
				}
			}else{
				renderError(404);
			}
		}
		//确认退款
		public void confirmRefund() {
			MaterialOrder orders = MaterialOrder.dao.findById(getPara("id"));
			if(orders!=null){
				if("closed".equals(orders.getStr("order_status")) && "applied".equals(orders.getStr("reason"))
						&& "going".equals(orders.getStr("reason_result"))){
					MaterialOrder.dao.suredrawback(orders);
					redirect("/salerManage/materialManage/ordermanage");
				}else{
					renderError(500);
				}
			}else{
				renderError(404);
			}
		}
		//确认退货
		public void confirmReturn() {
			MaterialOrder orders = MaterialOrder.dao.findById(getPara("id"));
			if(orders!=null){
				if("closed".equals(orders.getStr("order_status")) && "applyreturn".equals(orders.getStr("reason"))
						&& "going".equals(orders.getStr("reason_result"))){
					orders.set("reason_result", "success").update();
					MaterialOrder.dao.suredrawback(orders);
					redirect("/salerManage/materialManage/ordermanage");
				}else{
					renderError(500);
				}
			}else{
				renderError(404);
			}
		}
		
		//确认完单
		public void completeOrder() {
			MaterialOrder orders = MaterialOrder.dao.findById(getPara("id"));
			if(orders!=null){
				if("payed".equals(orders.getStr("order_status")) && "distribution".equals(orders.getStr("delivery_method"))){
					MaterialOrder.dao.suregetgoods(orders);
					redirect("/salerManage/materialManage/ordermanage");
				}else{
					renderError(500);
				}
			}else{
				renderError(404);
			}
		}
}
