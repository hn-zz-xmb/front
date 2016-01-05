package com.meishi.front.controller.pay.alipay;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.meishi.front.common.AppContextData;
import com.meishi.front.common.rabbit.SendRabbitMQMsg;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.*;
import com.meishi.util.GenerateUtils;
import com.meishi.util.StringUtil;
import com.meishi.util.msg.SendMsgTemplate;
import com.meishi.util.pay.util.AlipayNotify;

import java.io.IOException;
import java.util.*;

/**
 * 异步通知
 * 
 * @author zen
 *
 */
// @Controller(controllerKey = "/common/alipay/notify")
@Before(LoginInteceptor.class)
public class AlipayNotifyController extends BaseController {

	private static Logger log = Logger.getLogger(AlipayNotifyController.class);

	@Before(Tx.class)
	public void notifyPay() throws IOException {
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = getParaMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}

		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		// 商户订单号

		String out_trade_no = new String(getPara("out_trade_no").getBytes(
				"ISO-8859-1"), "UTF-8");
		// 支付宝交易号
		// String trade_no = new
		// String(request.getParameter("trade_no").getBytes(
		// "ISO-8859-1"), "UTF-8");
		// 交易状态
		String trade_status = new String(getPara("trade_status").getBytes(
				"ISO-8859-1"), "UTF-8");
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		if (AlipayNotify.verify(params)) {// 验证成功
			// ////////////////////////////////////////////////////////////////////////////////////////
			// 请在这里加上商户的业务逻辑程序代码
			// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			if (trade_status.equals("TRADE_FINISHED")) {
				// 判断该笔订单是否在商户网站中已经做过处理
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序
				SubscribeOrder orders=SubscribeOrder.dao.findCommonByNo(out_trade_no);	//方便执行查询

				if (AppContextData.OrderStatus.submitted.name().equals(
						orders.getStr("status"))) {
					String customCode = GenerateUtils.consumCode();// 生成购物消费吗

					if(!AppContextData.ARRIVE_EAT.equals(orders.getStr("order_type")) && !AppContextData.TAKE_OUT.equals(orders.getStr("order_type"))){
						return;
					}
					if(AppContextData.ARRIVE_EAT.equals(orders.getStr("order_type"))){
						SubscribeOrder new_order=SubscribeOrder.dao.findByNo(orders.getStr("no"));

						new_order.set("valid_no", customCode);
						SubscribeOrder.dao.payAndUpdateStatus(
								AppContextData.OrderStatus.payed.name(),
								AppContextData.EXTERNAL, new_order);
						SubscribeOrder.dao.rebate(new_order, AppContextData.PLATFORM_COIN_ID);
					}else if(AppContextData.TAKE_OUT.equals(orders.getStr("order_type"))) {
						TakeoutOrder new_order=TakeoutOrder.dao.findByNo(orders.getStr("no"));

						new_order.set("valid_no", customCode);
						TakeoutOrder.dao.payAndUpdateStatus(
								AppContextData.OrderStatus.payed.name(),
								AppContextData.EXTERNAL, new_order);
						TakeoutOrder.dao.rebate(new_order, AppContextData.PLATFORM_COIN_ID);
					}
					Store store = Store.dao.findById(orders.getStr("store_id"),
							"phone");
					if (StringUtil.isNotBlank(store.getStr("phone"))) {
						// 发送短信通知
						HashMap map = new HashMap();
						map.put("msgType", SendMsgTemplate.GOODS_CODE);
						map.put("phone", store.getStr("phone"));
						map.put("phone_code",
								GenerateUtils.generateIntRandom(6));
						List<String> params_ = new LinkedList<String>();// 短信参数
						params_.add(Member.dao.findById(
								orders.getStr("user_id"), "username").getStr(
								"username"));
						params_.add(customCode);
						map.put("params", params_);
						map.put("memberId", getUserIds());
						SendRabbitMQMsg.service.sendPhoneMsg(map);
					}
				}

				// 注意：
				// 该种交易状态只在两种情况下出现
				// 1、开通了普通即时到账，买家付款成功后。
				// 2、开通了高级即时到账，从该笔交易成功时间算起，过了签约时的可退款时限（如：三个月以内可退款、一年以内可退款等）后。
			} else if (trade_status.equals("TRADE_SUCCESS")) {
				// 判断该笔订单是否在商户网站中已经做过处理
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序
				SubscribeOrder orders=SubscribeOrder.dao.findCommonByNo(out_trade_no);	//方便执行查询

				if (AppContextData.OrderStatus.submitted.name().equals(
						orders.getStr("status"))) {
					String customCode = GenerateUtils.consumCode();// 生成购物消费吗

					if(!AppContextData.ARRIVE_EAT.equals(orders.getStr("order_type")) && !AppContextData.TAKE_OUT.equals(orders.getStr("order_type"))){
						return;
					}
					if(AppContextData.ARRIVE_EAT.equals(orders.getStr("order_type"))){
						SubscribeOrder new_order=SubscribeOrder.dao.findByCode(orders.getStr("no"));

						new_order.set("valid_no", customCode);
						SubscribeOrder.dao.payAndUpdateStatus(
								AppContextData.OrderStatus.payed.name(),
								AppContextData.EXTERNAL, new_order);
						SubscribeOrder.dao.rebate(new_order, AppContextData.PLATFORM_COIN_ID);
					}else if(AppContextData.TAKE_OUT.equals(orders.getStr("order_type"))) {
						TakeoutOrder new_order=TakeoutOrder.dao.findByNo(orders.getStr("no"));

						new_order.set("valid_no", customCode);
						TakeoutOrder.dao.payAndUpdateStatus(
								AppContextData.OrderStatus.payed.name(),
								AppContextData.EXTERNAL, new_order);
						TakeoutOrder.dao.rebate(new_order, AppContextData.PLATFORM_COIN_ID);
					}
					Store store = Store.dao.findById(orders.getStr("store_id"),
							"phone");
					if (StringUtil.isNotBlank(store.getStr("phone"))) {
						// 发送短信通知
						HashMap map = new HashMap();
						map.put("msgType", SendMsgTemplate.GOODS_CODE);
						map.put("phone", store.getStr("phone"));
						map.put("phone_code",
								GenerateUtils.generateIntRandom(6));
						List<String> params_ = new LinkedList<String>();// 短信参数
						params_.add(Member.dao.findById(
								orders.getStr("user_id"), "username").getStr(
								"username"));
						params_.add(customCode);
						map.put("params", params_);
						map.put("memberId", getUserIds());
						SendRabbitMQMsg.service.sendPhoneMsg(map);
					}
				}

				// 注意：
				// 该种交易状态只在一种情况下出现——开通了高级即时到账，买家付款成功后。
			}

			// ——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
			renderText("success");
			// ////////////////////////////////////////////////////////////////////////////////////////
		} else {// 验证失败
			renderText("fail");
		}
	}


	@SuppressWarnings("rawtypes")
	public void notifyRecharge() throws IOException {
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = getParaMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}

		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		// 商户订单号

		String out_trade_no = new String(getPara("out_trade_no").getBytes(
				"ISO-8859-1"), "UTF-8");
		// 支付宝交易号
		// String trade_no = new
		// String(request.getParameter("trade_no").getBytes(
		// "ISO-8859-1"), "UTF-8");
		// 交易状态
		String trade_status = new String(getPara("trade_status").getBytes(
				"ISO-8859-1"), "UTF-8");
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		if (AlipayNotify.verify(params)) {// 验证成功
			// ////////////////////////////////////////////////////////////////////////////////////////
			// 请在这里加上商户的业务逻辑程序代码
			// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			if (trade_status.equals("TRADE_FINISHED")) {
				// 判断该笔订单是否在商户网站中已经做过处理
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序
				RechargeOrder order = RechargeOrder.dao.findByNum(out_trade_no);
				if (order != null) {
					if (!AppContextData.OrderStatus.finished.name().equals(
							order.get("status"))) {
						RechargeOrder.dao.updateStatus(order);
					}
				}
				// 注意：
				// 该种交易状态只在两种情况下出现
				// 1、开通了普通即时到账，买家付款成功后。
				// 2、开通了高级即时到账，从该笔交易成功时间算起，过了签约时的可退款时限（如：三个月以内可退款、一年以内可退款等）后。
			} else if (trade_status.equals("TRADE_SUCCESS")) {
				// 判断该笔订单是否在商户网站中已经做过处理
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序
				RechargeOrder order = RechargeOrder.dao.findByNum(out_trade_no);
				if (order != null) {
					if (!AppContextData.OrderStatus.finished.name().equals(
							order.get("status"))) {
						RechargeOrder.dao.updateStatus(order);
					}
				}

				// 注意：
				// 该种交易状态只在一种情况下出现——开通了高级即时到账，买家付款成功后。
			}

			// ——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
			// return "success";
			renderText("success");
			// ////////////////////////////////////////////////////////////////////////////////////////
		} else {// 验证失败
			// return "fail";
			renderText("fail");
		}
	}
}
