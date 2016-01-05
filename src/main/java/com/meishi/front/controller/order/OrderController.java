package com.meishi.front.controller.order;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.meishi.front.common.AppContextData;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.*;
import com.meishi.util.DateUtil;
import com.meishi.util.GenerateUtils;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

//@Controller(controllerKey = "/order")
@Before(LoginInteceptor.class)
public class OrderController extends BaseController {
	private static Logger log = Logger.getLogger(OrderController.class);

	/**
	 * 填写订单页面
	 */
	public void index() {
		Store store = Store.dao.findStoreById(getPara("storeId"));
		Member member = Member.dao.findById(getUserIds(), "realname,tel");
		if (store == null || member == null) {
			renderError(404);
		}
		String region_id = store.getStr("region_id");
		String area_id = store.getStr("area_id");
		Area area = new Area();
		City city = new City();
		Province province = new Province();
		if (StringUtil.isBlank(region_id)) {
			area = Area.dao.findById(area_id);
			city = City.dao.findById(area.get("city_id"));
			province = Province.dao.findById(city.get("proId"));
		} else {
			Street street = Street.dao.findById(region_id);
			area = Area.dao.findById(street.get("area_id"));
			city = City.dao.findById(street.get("city_id"));
			province = Province.dao.findById(street.get("pro_id"));
		}
		String addre = province.getStr("name") + city.getStr("name")
				+ area.getStr("name");
		List<CartItem> cartItems = CartItem.dao.findByMemberAndStore(
				getUserIds(), getPara("storeId"));
		BigDecimal totalmoney = new BigDecimal(0);
		for (CartItem cartItem : cartItems) {
			totalmoney = totalmoney.add(cartItem.getBigDecimal("itemprice")
					.multiply(new BigDecimal(cartItem.getInt("item_num"))));
		}
		String date = DateUtil.format(new Date(), "yyyyMMddHHmmss");
		List<MemberCoupon> coupons = MemberCoupon.dao.findCoupon(totalmoney,
				date, store.getStr("id"), getUserIds());
		List<ReceiveAddress> addressList = ReceiveAddress.dao
				.findByMember(getUserIds());
		if (StringUtil.isNotBlank(store.getStr("delivery_scope"))) {
			String[] deliveryScope = store.getStr("delivery_scope").split("\n");
			setAttr("deliveryScope", deliveryScope);
		}
		setAttr("coupons", coupons);
		setAttr("addre", addre);
		setAttr("order_cart", cartItems);// 购物车
		setAttr("store", store);// 店铺信息
		setAttr("addressList", addressList);
		setAttr("member", member);
		render("/order/write.html");
	}

	/**
	 * 生成订单方法
	 */
	@Before(Tx.class)
	public void save() {
		Orders order = getModel(Orders.class);
		ReceiveAddress receiveAddress = getModel(ReceiveAddress.class);
		String couponid = getPara("couponid");
		// 查询购物车里面有数据
		String consume_method = getPara("consume_method");
		if (AppContextData.TAKE_OUT.equals(consume_method)) {
			if (StringUtil.isBlank(order.getStr("delivery"))) {
				if ("1".equals(getPara("autoSave"))) {
					receiveAddress.set("id", ToolUtil.getUuidByJdk(true))
							.set("user_id", getUserIds()).save();
				}
			}

			TakeoutOrder takeout_order = new TakeoutOrder();

			BigDecimal totalmoney = CartItem.dao.getTotalMoney(getUserIds(),
					getPara("storeId"));// 总金额
			MemberCoupon memberCoupon = MemberCoupon.dao
					.findByMemberCouponid(couponid);
			takeout_order.set("id", ToolUtil.getUuidByJdk(true))
					.set("no", GenerateUtils.orderNo())
					.set("user_id", getUserIds())
					.set("type", "net")
					.set("status", AppContextData.OrderStatus.submitted.name())
					.set("create_time",
							DateUtil.format(new Date(), DateUtil.pattern_ymd_hms))
					.set("store_id", getPara("storeId"))
					.set("contacts", receiveAddress.getStr("buyer_name"))
					.set("contacts_type", receiveAddress.getStr("tel"))
					.set("send_address",getPara("addre"))
					.set("client_message", order.getStr("postscript"));
			List<CartItem> cartItemList = CartItem.dao.findByMemberAndStore(
					getUserIds(), getPara("storeId"));
			if (StringUtil.isNotBlank(couponid)) {
				takeout_order.set("total_money", totalmoney)
						.set("fact_money",
								totalmoney.subtract(memberCoupon
										.getBigDecimal("couponmoney")))
						.set("member_coupon_id", couponid).set("use_coupon", 1);
			} else {
				takeout_order.set("total_money", totalmoney).set("fact_money", totalmoney);
			}
			//TakeoutOrder.dao.save(takeout_order, cartItemList, couponid);

			if (StringUtil.isNotBlank(couponid)) {
				MemberCoupon coupon = MemberCoupon.dao.findById(couponid);
				coupon.set("is_used", 1).update();
			}
			takeout_order.save();

			for(CartItem cartItem:cartItemList){
				TakeoutOrderItem tmp=new TakeoutOrderItem();
				tmp.set("id", ToolUtil.getUuidByJdk(true))
						.set("takeout_order_id", takeout_order.getStr("id"))
						.set("item_id",cartItem.getStr("item_id"))
						.set("item_price",cartItem.getBigDecimal("item_price"))
						.set("item_name", cartItem.getStr("item_name"))
						.set("is_discount",cartItem.getStr("n"))
						.set("count",cartItem.getInt("item_num"))
						.set("create_time", DateUtil.format(new Date(), DateUtil.pattern_ymd_hms))
						.set("store_id",takeout_order.getStr("store_id"))
						.set("item_type",cartItem.getInt("is_set_meal") == 1 ? "package":"food")
						.save();
				//清空购物车信息
				cartItem.delete();
			}

			redirect("/order/payUI?orderNo=" + takeout_order.getStr("no")+"&orderType="+AppContextData.ARRIVE_EAT);
			return;
		} else if(AppContextData.ARRIVE_EAT.equals(consume_method)){
//			order.set("eat_time", order.getStr("eat_time").replace("-", "")
//					.replace(" ", "").replace(":", ""));
			// 预定 -- 到店消费
			SubscribeOrder sub_order = new SubscribeOrder();
			sub_order.set("eat_time", order.getStr("eat_time").replace("-", "")
					.replace(" ", "").replace(":", ""));

			BigDecimal totalmoney = CartItem.dao.getTotalMoney(getUserIds(),
					getPara("storeId"));// 总金额
			MemberCoupon memberCoupon = MemberCoupon.dao
					.findByMemberCouponid(couponid);
			sub_order.set("id", ToolUtil.getUuidByJdk(true))
					.set("no", GenerateUtils.orderNo())
					.set("user_id", getUserIds())
					.set("type", "net")	//网络订单
					.set("status", AppContextData.OrderStatus.submitted.name())
					.set("create_time",
							DateUtil.format(new Date(), DateUtil.pattern_ymd_hms))
					.set("store_id", getPara("storeId"))
					.set("person_count", order.getInt("people_num"))
					.set("contacts", order.getStr("buyer_name"))
					.set("contacts_type", order.getStr("phone"))
					.set("client_message", order.getStr("postscript"));
			List<CartItem> cartItemList = CartItem.dao.findByMemberAndStore(
					getUserIds(), getPara("storeId"));
			if (StringUtil.isNotBlank(couponid)) {
				sub_order.set("total_money", totalmoney)
						.set("fact_money",
								totalmoney.subtract(memberCoupon
										.getBigDecimal("couponmoney")))
						.set("coupon_id",couponid);
						//.set("member_coupon_id", couponid).set("use_coupon", 1);
			} else {
				sub_order.set("total_money", totalmoney).set("fact_money", totalmoney);
			}
			//SubscribeOrder.dao.save(sub_order, cartItemList, couponid);

			if (StringUtil.isNotBlank(couponid)) {
				MemberCoupon coupon = MemberCoupon.dao.findById(couponid);
				coupon.set("is_used", 1).update();
			}
			sub_order.save();

			for(CartItem cartItem:cartItemList){
				SubscribeOrderItem tmp=new SubscribeOrderItem();
				tmp.set("id", ToolUtil.getUuidByJdk(true))
						.set("subscribe_order_id", sub_order.getStr("id"))
						.set("item_id",cartItem.getStr("item_id"))
						.set("item_price",cartItem.getBigDecimal("item_price"))
						.set("item_name", cartItem.getStr("item_name"))
						.set("is_discount",cartItem.getStr("n"))
						.set("count",cartItem.getInt("item_num"))
						.set("create_time",DateUtil.format(new Date(), DateUtil.pattern_ymd_hms))
						.set("store_id",sub_order.getStr("store_id"))
						.set("item_type",cartItem.getInt("is_set_meal") == 1 ? "package":"food")
						.save();
				//清空购物车信息
				cartItem.delete();
			}
			redirect("/order/payUI?orderNo=" + sub_order.getStr("no")+"&orderType="+AppContextData.ARRIVE_EAT);
			return;

		}else {
			//订单类型不符合要求
			redirect("");
			return;
		}
	}

	/**
	 * 跳转到支付页面
	 */
	public void payUI() {
		String orderType = getPara("orderType");
		String orderNo = getPara("orderNo");
		SubscribeOrder orders=SubscribeOrder.dao.findCommonByNo(orderNo);	//方便执行查询

		if (!AppContextData.OrderStatus.submitted.name().equals(
				orders.getStr("status"))) {
			return;
		}

		if(!AppContextData.ARRIVE_EAT.equals(orders.getStr("order_type")) && !AppContextData.TAKE_OUT.equals(orders.getStr("order_type"))){
			return;
		}

		setAttr("order", orders);
		setAttr("orderType", orderType);
		render("/order/pay.html");
	}
}
