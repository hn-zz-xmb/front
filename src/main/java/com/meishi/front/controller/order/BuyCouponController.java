package com.meishi.front.controller.order;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.meishi.front.controller.BaseController;
import com.meishi.front.controller.goods.CartController;
import com.meishi.model.Coin;
import com.meishi.model.Coupon;
import com.meishi.model.CouponOrder;
import com.meishi.model.Member;
import com.meishi.util.DateUtil;
import com.meishi.util.GenerateUtils;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;

public class BuyCouponController extends BaseController {
	private static Logger log = Logger.getLogger(CartController.class);

	public void tobuy() {
		JSONObject result = new JSONObject();
		// 验证代金券是否存在
		Coupon coupon = Coupon.dao.findById(getPara("couponId"));
		if (coupon == null) {
			result.put("isCart", false);
			result.put("isLogin", false);
			result.put("error", "请先确定代金券是否存在");
			renderJson(result);
			return;
		}

		// 验证是否登陆
		if (StringUtil.isBlank(getUserIds())) {
			result.put("isCart", true);
			result.put("isLogin", false);
			result.put("error", "请先登录");
			renderJson(result);
			return;
		}
		result.put("isCart", true);
		result.put("isLogin", true);
		renderJson(result.toString());
	}

	public void couponpay() {
		Coupon coupon = Coupon.dao.findById(getPara("couponId"));
		setAttr("count", 1);
		setAttr("totalmoney", coupon.getBigDecimal("sale_price"));
		setAttr("coupon", coupon);
		render("/order/couponpay.html");
	}

	/**
	 * 改數量
	 */
	public void changeCount() {
		JSONObject result = new JSONObject();
		String couponId = getPara("couponId");
		Coupon coupon = Coupon.dao.findById(couponId);
		Integer count = getParaToInt("count");
		boolean isAdd = getParaToBoolean("isAdd");
		if (isAdd) {
			count = count + 1;
			result.put("isChange", true);
			result.put("totalmoney", coupon.getBigDecimal("sale_price")
					.multiply(new BigDecimal(count)));
			result.put("count", count);
		} else {
			count = count - 1;
			result.put("isChange", true);
			result.put("totalmoney", coupon.getBigDecimal("sale_price")
					.multiply(new BigDecimal(count)));
			result.put("count", count);
		}
		renderJson(result.toString());
	}

	@Before(Tx.class)
	public void normalPay() {
		JSONObject result = new JSONObject();
		String paypwd = getPara("paypwd");
		String couponId = getPara("couponId");
		Integer count = getParaToInt("count");
		if (StringUtil.isBlank(paypwd)) {
			result.put("isPay", false);
			result.put("error", "请核实你的密码");
			renderJson(result.toString());
			return;
		}
		if (StringUtil.isBlank(couponId)) {
			result.put("isPay", false);
			result.put("error", "代金券不存在");
			renderJson(result.toString());
			return;
		}
		Member member = Member.dao.findById(getUserIds());
		if (StringUtil.isBlank(member.getStr("pay_salt"))) {
			result.put("isPay", false);
			result.put("error", "您还未设置支付密码");
			renderJson(result.toString());
			return;
		}
		if (!Member.dao.checkPayPwd(paypwd, member)) {
			result.put("isPay", false);
			result.put("error", "请核实你的支付密码");
			renderJson(result.toString());
			return;
		}
		Coupon coupon = Coupon.dao.findById(couponId);
		Coin coin = Coin.dao.findById(member.get("coin_id"));
		if (coin.getBigDecimal("account_money").compareTo(
				coupon.getBigDecimal("sale_price").multiply(
						new BigDecimal(count))) < 0) {
			result.put("isPay", false);
			result.put("error", "你的账户余额不足!");
			renderJson(result.toString());
			return;
		}
		CouponOrder orders = new CouponOrder();
		orders.set("id", ToolUtil.getUuidByJdk(true))
				.set("order_no", GenerateUtils.orderNo())
				.set("coupon_id", couponId)
				.set("user_id", member.get("id"))
				.set("store_id", coupon.get("store_id"))
				.set("buy_time", DateUtil.format(new Date(), "yyyyMMddHHmmss"))
				.set("buy_count", count)
				.set("total_money",
						coupon.getBigDecimal("sale_price").multiply(
								new BigDecimal(count)))
				.set("order_status", "false").save();
		CouponOrder.dao.payCouponOrder(coin.getStr("id"), orders);
		result.put("isPay", true);
		renderJson(result.toString());
	}

	@Before(Tx.class)
	public void beforePay() {
		String couponId = getPara("couponId");
		Integer count = getParaToInt("count");
		Member member = Member.dao.findById(getUserIds());
		Coupon coupon = Coupon.dao.findById(couponId);
		String payWay = getPara("payWay");
		if (member != null && coupon != null) {
			CouponOrder orders = new CouponOrder();
			orders.set("id", ToolUtil.getUuidByJdk(true))
					.set("order_no", GenerateUtils.orderNo())
					.set("coupon_id", couponId)
					.set("user_id", member.get("id"))
					.set("store_id", coupon.get("store_id"))
					.set("buy_time",
							DateUtil.format(new Date(), "yyyyMMddHHmmss"))
					.set("buy_count", count)
					.set("total_money",
							coupon.getBigDecimal("sale_price").multiply(
									new BigDecimal(count)))
					.set("order_status", "false").save();
			if ("zfb".equals(payWay)) {
				redirect("/couponpay/api?orderNo=" + orders.getStr("order_no")
						+ "&payWay=");
			} else {
				redirect("/couponpay/api?orderNo=" + orders.getStr("order_no")
						+ "&payWay=" + payWay);
			}
		} else {
			renderError(404);
		}
	}

}
