package com.meishi.front.controller.member.usercenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.OrderAppraise;
import com.meishi.model.Orders;
import com.meishi.model.StoreCredit;
import com.meishi.util.DateUtil;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;
import net.sf.json.JSONObject;

import java.util.Date;

//@Controller(controllerKey = "/account/buyerorder")
@Before(LoginInteceptor.class)
public class OrderAppraiseController extends BaseController {
	private static Logger log = Logger.getLogger(OrderAppraiseController.class);

	// 跳转评价订单页面
	public void index() {
		Orders orders = Orders.dao.findOrderNO(getPara("id"));
		setAttr("order", orders);
		render("/usercenter/appraise.html");
	}

	// 提交评价
	public void saveappraise() {
		JSONObject result = new JSONObject();
		OrderAppraise orderAppraise = getModel(OrderAppraise.class);
		String orderid = getPara("orderid");
		if (StringUtil.isBlank(orderid)) {
			result.put("isLogin", false);
		} else {
			Orders order = Orders.dao.findById(orderid);
			orderAppraise
					.set("id", ToolUtil.getUuidByJdk(true))
					.set("member_id", getUserIds())
					.set("order_no", order.getStr("order_no"))
					.set("store_id", order.getStr("store_id"))
					.set("create_time",
							DateUtil.format(new Date(),
									DateUtil.pattern_ymd_hms))
					.set("is_change", -1);
			// 添加评价
			StoreCredit storeCredit = StoreCredit.dao.findByStore(orderAppraise
					.getStr("store_id"));
			if (storeCredit == null) {
				storeCredit = new StoreCredit();
				storeCredit.set("id", ToolUtil.getUuidByJdk(true))
						.set("store_id", orderAppraise.getStr("store_id"))
						.set("credit_points", 0).set("good_points", 0)
						.set("total_appriase", 0).save();
			}
			int count = Orders.dao.findcount(orderAppraise.getStr("store_id"));
			int env_points = orderAppraise.getInt("env_points");
			int server_points = orderAppraise.getInt("server_points");
			int taste_points = orderAppraise.getInt("taste_points");
			int points = env_points + server_points + taste_points;
			if (points >= 0 && points <= 5) {
				int credit = storeCredit.getInt("credit_points") - 2;
				if (credit > count) {
					storeCredit.set("credit_points", credit);
					orderAppraise.set("store_credit", -2);
				} else {
					orderAppraise.set("store_credit", 0);
				}

			} else if (points >= 6 && points <= 10) {
				storeCredit.set("credit_points",
						storeCredit.getInt("credit_points") + 1);
				orderAppraise.set("store_credit", 1);
			} else if (points >= 11 && points <= 15) {
				storeCredit.set("credit_points",
						storeCredit.getInt("credit_points") + 3).set(
						"good_points",
						storeCredit.getInt("good_points") + points);
				orderAppraise.set("store_credit", 3);
			}
			orderAppraise.save();
			storeCredit.set("total_appriase",
					storeCredit.getInt("total_appriase") + points).update();
			result.put("isLogin", true);
		}
		renderJson(result.toString());
	}

	// 跳转到修改评价页面
	public void upappraise() {
		Orders orders = Orders.dao.findOrderNO(getPara("id"));
		setAttr("order", orders);
		OrderAppraise orderAppraise = OrderAppraise.dao
				.findappraise(orders.getStr("order_no"));
		if (orderAppraise == null) {
			renderError(500);
		} else {
			if (orderAppraise.getInt("is_change") == 1) {
				renderError(500);
			} else {
				setAttr("orderAppraise", orderAppraise);
				render("/usercenter/updateappraise.html");
			}
		}
	}

	// 修改评价
	public void updateappraise() {
		JSONObject result = new JSONObject();
		OrderAppraise orderAppraise = getModel(OrderAppraise.class);// 修改后的评价
		if (StringUtil.isBlank(orderAppraise.getStr("id"))) {
			result.put("isLogin", false);
		} else {
			OrderAppraise appraise = OrderAppraise.dao.findById(orderAppraise
					.getStr("id"));// 修改前的评价
			if (appraise.getInt("is_change") == -1) {
				int count = Orders.dao.findcount(appraise.getStr("store_id"));// 店铺订单数
				int env_points = orderAppraise.getInt("env_points");
				int server_points = orderAppraise.getInt("server_points");
				int taste_points = orderAppraise.getInt("taste_points");
				int points = env_points + server_points + taste_points;
				int env = appraise.getInt("env_points");
				int server = appraise.getInt("server_points");
				int taste = appraise.getInt("taste_points");
				int point = env + server + taste;
				int credit = appraise.getInt("store_credit");
				StoreCredit storeCredit = StoreCredit.dao.findByStore(appraise
						.getStr("store_id"));// 店铺信誉
				if (points >= 0 && points <= 5) {
					if (point >= 6 && point <= 10) {// 中评改差评
						int credits = storeCredit.getInt("credit_points") - 3;
						if (credits > count) {
							storeCredit.set("credit_points", credit);
							orderAppraise.set("store_credit", -2);
						} else {
							storeCredit.set("credit_points",
									storeCredit.getInt("credit_points") - 1);
							orderAppraise.set("store_credit", 0);
						}
					} else if (point >= 11 && point <= 15) {// 好评改差评
						int credits = storeCredit.getInt("credit_points") - 5;
						if (credits > count) {
							storeCredit.set("credit_points", credit).set(
									"good_points",
									storeCredit.getInt("good_points") - point);
							orderAppraise.set("store_credit", -2);
						} else {
							storeCredit.set("credit_points",
									storeCredit.getInt("credit_points") - 3)
									.set("good_points",
											storeCredit.getInt("good_points")
													- point);
							orderAppraise.set("store_credit", 0);
						}
					}

				} else if (points >= 6 && points <= 10) {
					if (point >= 0 && point <= 5) {// 差评改中评
						if (credit == -2) {// 原来评价差并扣过信誉度
							storeCredit.set("credit_points",
									storeCredit.getInt("credit_points") + 3);
						} else if (credit == 0) {// 原来评价差没有扣过信誉度
							storeCredit.set("credit_points",
									storeCredit.getInt("credit_points") + 1);
						}
						orderAppraise.set("store_credit", 1);
					} else if (point >= 11 && point <= 15) {// 好评改中评
						storeCredit.set("credit_points",
								storeCredit.getInt("credit_points") - 2).set(
								"good_points",
								storeCredit.getInt("good_points") - point);
						orderAppraise.set("store_credit", 1);
					}
				} else if (points >= 11 && points <= 15) {
					if (point >= 0 && point <= 5) {// 差评改好评
						if (credit == -2) {// 原来评价差并扣过信誉度
							storeCredit.set("credit_points",
									storeCredit.getInt("credit_points") + 5)
									.set("good_points",
											storeCredit.getInt("good_points")
													+ points);
						} else if (credit == 0) {// 原来评价差没有扣过信誉度
							storeCredit.set("credit_points",
									storeCredit.getInt("credit_points") + 3)
									.set("good_points",
											storeCredit.getInt("good_points")
													+ points);
						}
						orderAppraise.set("store_credit", 3);
					} else if (point >= 6 && point <= 10) {// 中评改好评
						storeCredit.set("credit_points",
								storeCredit.getInt("credit_points") + 2).set(
								"good_points",
								storeCredit.getInt("good_points") + points);
						orderAppraise.set("store_credit", 3);
					} else if (point >= 11 && point <= 15) {// 好评改好评
						storeCredit.set("good_points",
								storeCredit.getInt("good_points") + points
										- point);
					}
				}
				orderAppraise
						.set("is_change", 1)
						.set("change_time",
								DateUtil.format(new Date(),
										DateUtil.pattern_ymd_hms)).update();
				storeCredit.set("total_appriase",
						storeCredit.getInt("total_appriase") + points - point)
						.update();
				result.put("isLogin", true);
			} else {
				result.put("isLogin", false);
			}
		}
		renderJson(result.toString());
	}
}
