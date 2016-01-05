package com.meishi.front.controller.store.admin;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.meishi.front.common.AppContextData;
import com.meishi.front.common.rabbit.SendRabbitMQMsg;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.StoreInteceptor;
import com.meishi.model.*;
import com.meishi.util.DateUtil;
import com.meishi.util.GenerateUtils;
import com.meishi.util.ToolUtil;
import com.meishi.util.msg.SendMsgTemplate;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

//@Controller(controllerKey = "/store/admin/verifyConsumeCode")
@Before(StoreInteceptor.class)
public class VerifyConsumeCodeController extends BaseController {
	private static Logger log = Logger
			.getLogger(VerifyConsumeCodeController.class);

	// 跳转到消费码验证订单页面
	public void makesure() {
		render("/store/admin/order/consumeCode.html");
	}

	// 消费码订单详情
	public void consumeorderUI() {
		SubscribeOrder orders = SubscribeOrder.dao
				.findByCode(getPara("consumeCode"));
		setAttr("orders", orders);
		setAttr("dataDics", Datadic.dao.findByGroup("order_status"));// 订单状态
		render("/store/admin/order/consumeload.html");
	}

	// 确认消费

	@Before(Tx.class)
	public void makesureOrder() {
		JSONObject result = new JSONObject();
		if (getPara("order_type").equals("arrive_eat")) {
			SubscribeOrder orders = SubscribeOrder.dao.findById(getPara("orderId"));
			if (orders == null) {
				result.put("isSure", false);
				result.put("error", "订单不存在");
			} else {
				// orders.set("status", AppContextData.FINISHED).update();
				List<SubscribeOrderItem> orderDetails = SubscribeOrderItem.dao
						.findByOrderId(orders.getStr("id"));
				// 确认消费后商品销售量增加更新商品统计
				for (int i = 0; i < orderDetails.size(); i++) {
					SubscribeOrderItem orderDetail = orderDetails.get(i);
					//if (orderDetail.getInt("item_type") == 0) {
						GoodsStatistics goodsStatistics = GoodsStatistics.dao
								.findByGoodsId(orderDetail.getStr("food_id"));
						if(goodsStatistics!=null){
							goodsStatistics.set(
									"sale_num",
									goodsStatistics.getInt("sale_num")
											+ orderDetail.getInt("count"))
									.update();
						}
						
//					} else {
//						SetMeal setMeal = SetMeal.dao.findById(orderDetail
//								.get("item_id"));
//						setMeal.set(
//								"sales_volume",
//								setMeal.getInt("sales_volume")
//										+ orderDetail.getInt("counts"))
//								.update();
//					}
				}
				orders.set("status", AppContextData.FINISHED).set("update_time",DateUtil.format(new Date(), DateUtil.pattern_ymd_hms))
						.update();

				// 确认消费后，打钱给卖家
				SubscribeOrder.dao.rebate(orders, AppContextData.PLATFORM_COIN_ID);// 返利
				Store store = Store.dao.findById(orders.get("store_id"));
				Member saler = Member.dao.findById(store.get("user_id"));
				Member buyer = Member.dao.findById(orders.get("user_id"));

				List<String> params = new LinkedList<String>();// 短信参数
				params.add(buyer.getStr("username"));
				params.add(orders.getStr("no"));
				//
				HashMap map = new HashMap();
				map.put("msgType", SendMsgTemplate.ORDERUSE_CODE);
				map.put("phone", orders.get("contacts_type"));
				map.put("phone_code", "");
				map.put("params", params);
				map.put("memberId", getUserIds());
				SendRabbitMQMsg.service.sendPhoneMsg(map);

				if ("I".equals(orders.get("pay_type"))) {
					// 1.划去买家的冻结金额
					Coin coin = Coin.dao.findById(buyer.get("coin_id"));
					coin.set(
							"freeze_money",
							coin.getBigDecimal("freeze_money").subtract(
									orders.getBigDecimal("fact_money")))
							.update();
					CoinTradeRecord coinTradeRecord = new CoinTradeRecord();
					coinTradeRecord
							.set("id", ToolUtil.getUuidByJdk(true))
							.set("trade_num", "JY" + GenerateUtils.orderNo())
							.set("trade_name", "订单付款(扣除买家金额)")
							.set("trade_type", 0)
							.set("trade_money",
									orders.getBigDecimal("total_money")
											.negate())
							.set("trade_time",
									DateUtil.format(new java.util.Date(),
											"yyyyMMddHHmmss"))
							.set("target_name", buyer.get("username")).save();
					// 2.卖家增加金额
					Coin coin1 = Coin.dao.findById(buyer.get("coin_id"));
					coin1.set(
							"account_money",
							coin1.getBigDecimal("account_money")
									.add(orders
											.getBigDecimal("fact_money")
											.subtract(
													orders.getBigDecimal(
															"fact_money")
															.multiply(
																	new BigDecimal(
																			0.05)))))
							.update();
					CoinTradeRecord coinTradeRecord1 = new CoinTradeRecord();
					coinTradeRecord1
							.set("id", ToolUtil.getUuidByJdk(true))
							.set("trade_num", "JY" + GenerateUtils.orderNo())
							.set("trade_name", "订单付款(增加卖家金额)")
							.set("trade_type", 0)
							.set("trade_money",
									orders.getBigDecimal("fact_money").negate())
							.set("trade_time",
									DateUtil.format(new Date(),
											"yyyyMMddHHmmss"))
							.set("target_name", saler.get("username")).save();
					// 3.平台增加金额（佣金）
					// 在返利方法中，已经计算过佣金
					/*
					 * PlatformCoin platformCoin = PlatformCoin.dao
					 * .findById(AppContextData.PLATFORM_COIN_ID);
					 * platformCoin.set( "coin_money",
					 * platformCoin.getBigDecimal("coin_money").add(
					 * orders.getBigDecimal("commission"))).update();
					 * 
					 * PlatformTradeRecord record = new PlatformTradeRecord();
					 * record.set("id", ToolUtil.getUuidByJdk(true))
					 * .set("target_id", saler.get("id")) .set("trade_money",
					 * orders.getBigDecimal("commission")) .set("trade_num",
					 * orders.get("order_no")) .set("trade_status", 1)
					 * .set("trade_time", DateUtil.format(new Date(),
					 * "yyyyMMddHHmmss")) .set("trade_type", 1)
					 * .set("trade_name", orders.get("order_no") + "订单付款（佣金）")
					 * .save();
					 */
				} else if ("E".equals(orders.get("pay_type"))) {
					Coin coin1 = Coin.dao.findById(buyer.get("coin_id"));
					coin1.set(
							"account_money",
							coin1.getBigDecimal("account_money").add(
									orders.getBigDecimal("total_money")))
							.update();
					CoinTradeRecord coinTradeRecord = new CoinTradeRecord();
					coinTradeRecord
							.set("id", ToolUtil.getUuidByJdk(true))
							.set("trade_num", "JY" + GenerateUtils.orderNo())
							.set("trade_name", "订单付款(支付宝支付)")
							.set("trade_type", 0)
							.set("trade_money",
									orders.getBigDecimal("total_money")
											.negate())
							.set("trade_time",
									DateUtil.format(new Date(),
											"yyyyMMddHHmmss"))
							.set("target_name", saler.get("username")).save();
				}
				result.put("isSure", true);
			}
		} else if (getPara("order_type").equals("take_out")) {
			TakeoutOrder orders = TakeoutOrder.dao.findById(getPara("orderId"));
			if (orders == null) {
				result.put("isSure", false);
				result.put("error", "订单不存在");
			} else {
				// orders.set("status", AppContextData.FINISHED).update();
				List<TakeoutOrderItem> orderDetails = TakeoutOrderItem.dao
						.findByOrderNo(orders.getStr("no"));
				// 确认消费后商品销售量增加更新商品统计
				for (int i = 0; i < orderDetails.size(); i++) {
					TakeoutOrderItem orderDetail = orderDetails.get(i);
//					if (orderDetail.getInt("item_type") == 0) {
						GoodsStatistics goodsStatistics = GoodsStatistics.dao
								.findByGoodsId(orderDetail.getStr("food_id"));
						goodsStatistics.set(
								"sale_num",
								goodsStatistics.getInt("sale_num")
										+ orderDetail.getInt("count"))
								.update();
//					} else {
//						SetMeal setMeal = SetMeal.dao.findById(orderDetail
//								.get("item_id"));
//						setMeal.set(
//								"sales_volume",
//								setMeal.getInt("sales_volume")
//										+ orderDetail.getInt("counts"))
//								.update();
//					}
				}
				orders.set("status", AppContextData.FINISHED)
						.set("is_return_coupon", 0).update();

				// 确认消费后，打钱给卖家
				TakeoutOrder.dao.rebate(orders, AppContextData.PLATFORM_COIN_ID);// 返利
				Store store = Store.dao.findById(orders.get("store_id"));
				Member saler = Member.dao.findById(store.get("user_id"));
				Member buyer = Member.dao.findById(orders.get("user_id"));

				List<String> params = new LinkedList<String>();// 短信参数
				params.add(buyer.getStr("username"));
				params.add(orders.getStr("no"));
				//
				HashMap map = new HashMap();
				map.put("msgType", SendMsgTemplate.ORDERUSE_CODE);
				map.put("phone", orders.get("contacts_type"));
				map.put("phone_code", "");
				map.put("params", params);
				map.put("memberId", getUserIds());
				SendRabbitMQMsg.service.sendPhoneMsg(map);

				if ("I".equals(orders.get("pay_type"))) {
					// 1.划去买家的冻结金额
					Coin coin = Coin.dao.findById(buyer.get("coin_id"));
					coin.set(
							"freeze_money",
							coin.getBigDecimal("freeze_money").subtract(
									orders.getBigDecimal("fact_money")))
							.update();
					CoinTradeRecord coinTradeRecord = new CoinTradeRecord();
					coinTradeRecord
							.set("id", ToolUtil.getUuidByJdk(true))
							.set("trade_num", "JY" + GenerateUtils.orderNo())
							.set("trade_name", "订单付款(扣除买家金额)")
							.set("trade_type", 0)
							.set("trade_money",
									orders.getBigDecimal("total_money")
											.negate())
							.set("trade_time",
									DateUtil.format(new java.util.Date(),
											"yyyyMMddHHmmss"))
							.set("target_name", buyer.get("username")).save();
					// 2.卖家增加金额
					Coin coin1 = Coin.dao.findById(buyer.get("coin_id"));
					coin1.set(
							"account_money",
							coin1.getBigDecimal("account_money")
									.add(orders
											.getBigDecimal("fact_money")
											.subtract(
													orders.getBigDecimal(
															"fact_money")
															.multiply(
																	new BigDecimal(
																			0.05)))))
							.update();
					CoinTradeRecord coinTradeRecord1 = new CoinTradeRecord();
					coinTradeRecord1
							.set("id", ToolUtil.getUuidByJdk(true))
							.set("trade_num", "JY" + GenerateUtils.orderNo())
							.set("trade_name", "订单付款(增加卖家金额)")
							.set("trade_type", 0)
							.set("trade_money",
									orders.getBigDecimal("fact_money").negate())
							.set("trade_time",
									DateUtil.format(new Date(),
											"yyyyMMddHHmmss"))
							.set("target_name", saler.get("username")).save();
					// 3.平台增加金额（佣金）
					// 在返利方法中，已经计算过佣金
					/*
					 * PlatformCoin platformCoin = PlatformCoin.dao
					 * .findById(AppContextData.PLATFORM_COIN_ID);
					 * platformCoin.set( "coin_money",
					 * platformCoin.getBigDecimal("coin_money").add(
					 * orders.getBigDecimal("commission"))).update();
					 * 
					 * PlatformTradeRecord record = new PlatformTradeRecord();
					 * record.set("id", ToolUtil.getUuidByJdk(true))
					 * .set("target_id", saler.get("id")) .set("trade_money",
					 * orders.getBigDecimal("commission")) .set("trade_num",
					 * orders.get("order_no")) .set("trade_status", 1)
					 * .set("trade_time", DateUtil.format(new Date(),
					 * "yyyyMMddHHmmss")) .set("trade_type", 1)
					 * .set("trade_name", orders.get("order_no") + "订单付款（佣金）")
					 * .save();
					 */
				} else if ("E".equals(orders.get("pay_type"))) {
					Coin coin1 = Coin.dao.findById(buyer.get("coin_id"));
					coin1.set(
							"account_money",
							coin1.getBigDecimal("account_money").add(
									orders.getBigDecimal("total_money")))
							.update();
					CoinTradeRecord coinTradeRecord = new CoinTradeRecord();
					coinTradeRecord
							.set("id", ToolUtil.getUuidByJdk(true))
							.set("trade_num", "JY" + GenerateUtils.orderNo())
							.set("trade_name", "订单付款(支付宝支付)")
							.set("trade_type", 0)
							.set("trade_money",
									orders.getBigDecimal("total_money")
											.negate())
							.set("trade_time",
									DateUtil.format(new Date(),
											"yyyyMMddHHmmss"))
							.set("target_name", saler.get("username")).save();
				}
				result.put("isSure", true);
			}
		}
		renderJson(result.toString());
	}

	// 检测验证消费码
	public void checkMakeSureCode() {
		String consumeCode = getPara("consumeCode");
		Store store = getSessionAttr("store");
		SubscribeOrder orders = SubscribeOrder.dao.findByCode(consumeCode);
		JSONObject result = new JSONObject();
		if (orders == null
				|| !orders.getStr("store_id").equals(store.getStr("id"))) {
			result.put("error", "你输入的消费码有误");
			result.put("isSure", false);
			renderJson(result.toString());
			return;
		}
		result.put("isSure", true);
		renderJson(result.toString());
	}

}
