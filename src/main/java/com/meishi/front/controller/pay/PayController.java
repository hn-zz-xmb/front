package com.meishi.front.controller.pay;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.meishi.front.common.AppContextData;
import com.meishi.front.common.rabbit.SendRabbitMQMsg;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.*;
import com.meishi.util.DateUtil;
import com.meishi.util.GenerateUtils;
import com.meishi.util.StringUtil;
import com.meishi.util.msg.SendMsgTemplate;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
@Before(LoginInteceptor.class)
public class PayController extends BaseController {
	private static Logger log = Logger.getLogger(PayController.class);
	@Before(Tx.class)
	public void normalPay() {
		JSONObject result = new JSONObject();
		String paypwd = getPara("paypwd");
		String orderNo = getPara("orderNo");
		String orderType = getPara("orderType");

		if (StringUtil.isBlank(paypwd)) {
			result.put("isPay", false);
			result.put("error", "请核实你的密码");
			renderJson(result.toString());
			return;
		}
		if (StringUtil.isBlank(orderNo)) {
			result.put("isPay", false);
			result.put("error", "请核实你的订单号");
			renderJson(result.toString());
			return;
		}
		Member member = Member.dao.findById(getUserIds());
		if(StringUtil.isBlank(member.getStr("pay_salt"))){
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
		//Orders orders = Orders.dao.findByNo(orderNo);
		SubscribeOrder order=SubscribeOrder.dao.findCommonByNo(orderNo);
		//读取信息
		if(!AppContextData.ARRIVE_EAT.equals(order.getStr("order_type")) && !AppContextData.TAKE_OUT.equals(order.getStr("order_type"))) {
			result.put("isPay", false);
			result.put("error", "你的订单信息不能存在!");
			return;
		}
		BigDecimal fact_money=order.getBigDecimal("fact_money");
		String storeId=order.getStr("store_id");
		String valid_no=GenerateUtils.consumCode();
		String contact=order.getStr("contacts_type");
		String user_id=order.getStr("user_id");

		Coin coin = Coin.dao.findById(member.get("coin_id"));
		if (coin.getBigDecimal("account_money").compareTo(
				fact_money) < 0) {
			result.put("isPay", false);
			result.put("error", "你的账户余额不足!");
			renderJson(result.toString());
			return;
		}

		if(AppContextData.ARRIVE_EAT.equals(order.getStr("order_type"))){
			SubscribeOrder new_order=SubscribeOrder.dao.findByNo(orderNo);
			new_order.set("valid_no", valid_no)
			.set("status", AppContextData.OrderStatus.payed.name()).
			set("pay_type", AppContextData.INTERNAL);
			new_order.set("update_time",
					DateUtil.format(new Date(), DateUtil.pattern_ymd_hms));
			SubscribeOrder.dao.payOrder(coin.getStr("id"),new_order);
		}else if(AppContextData.TAKE_OUT.equals(order.getStr("order_type"))){
			TakeoutOrder new_order=TakeoutOrder.dao.findByNo(orderNo);
			new_order.set("valid_no",valid_no)
					.set("status", AppContextData.OrderStatus.payed.name()).
					set("pay_type",AppContextData.INTERNAL);
			new_order.set("update_time",
					DateUtil.format(new Date(), DateUtil.pattern_ymd_hms));
			TakeoutOrder.dao.payOrder(coin.getStr("id"),new_order);
		}

		Store store = Store.dao.findById(storeId, "phone");

		if (StringUtil.isNotBlank(store.getStr("phone"))) {
			// 发送短信通知(发给用户)
			String customCode = valid_no;
			HashMap map = new HashMap();
			map.put("msgType", SendMsgTemplate.GOODS_CODE);
			map.put("phone", contact);
			map.put("phone_code", customCode);
			List<String> params_ = new LinkedList<String>();// 短信参数
			params_.add(Member.dao.findById(user_id,
					"username").getStr("username"));
			params_.add(customCode);
			map.put("params", params_);
			map.put("memberId", getUserIds());
			SendRabbitMQMsg.service.sendPhoneMsg(map);
			// 发短信通知店家
			HashMap map2 = new HashMap();
			map2.put("msgType", SendMsgTemplate.STOREORDER_CODE);
			map2.put("phone", store.get("phone"));
			List<String> params = new LinkedList<String>();// 短信参数
			params.add(store.getStr("name"));
			map2.put("params", params);
			SendRabbitMQMsg.service.sendPhoneMsg(map2);
			result.put("isPay", true);
			renderJson(result.toString());
		}
	}
}
