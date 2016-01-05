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
import com.meishi.util.pay.config.AlipayConfig;
import com.meishi.util.pay.util.AlipayNotify;
import com.meishi.util.pay.util.AlipaySubmit;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 支付
 * 
 * @author zen
 *
 */
// @Controller(controllerKey = "/pay/alipay")
@Before(LoginInteceptor.class)
public class AlipayController extends BaseController {

	private static Logger log = Logger.getLogger(AlipayController.class);

	public void api() {
		//Orders orders = Orders.dao.findByNo(getPara("orderNo"));\
		String orderType=getPara("orderType");
		//定义order_no
		//定义fact_money
		String order_no=getPara("orderNo");

		SubscribeOrder orders=SubscribeOrder.dao.findCommonByNo(order_no);	//方便执行查询
		if(!AppContextData.ARRIVE_EAT.equals(orders.getStr("order_type")) && !AppContextData.TAKE_OUT.equals(orders.getStr("order_type"))){
			return;
		}

		String payment_type = "1";

		String notify_url = AppContextData.DOMAIN + "/alipayNotify/notifyPay";
		String return_url = AppContextData.DOMAIN + "/alipay/return_url";

		String seller_email = new String("18039226858@163.com");

		String out_trade_no = new String(order_no);
		String subject = new String(order_no);
		String total_fee = new String(orders.getBigDecimal("fact_money")
				.toString());
		String body = new String();
		String show_url = new String();
		// 若要使用请调用类文件submit中的query_timestamp函数
		String anti_phishing_key = "";
		String exter_invoke_ip = "";

		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "create_direct_pay_by_user");
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("payment_type", payment_type);
		sParaTemp.put("notify_url", notify_url);
		sParaTemp.put("return_url", return_url);
		sParaTemp.put("seller_email", seller_email);
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("subject", subject);
		sParaTemp.put("total_fee", total_fee);
		// 若为网银
		if (StringUtils.isNotBlank(getPara("payWay"))) {
			sParaTemp.put("paymethod", "bankPay");
			sParaTemp.put("defaultbank", getPara("payWay"));
		}
		sParaTemp.put("body", body);
		sParaTemp.put("show_url", show_url);
		sParaTemp.put("anti_phishing_key", anti_phishing_key);
		sParaTemp.put("exter_invoke_ip", exter_invoke_ip);

		// 建立请求
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
		setAttr("sHtmlText", sHtmlText);
		// return "pay/alipay/alipayAPI.html";
		render("/order/alipay/alipayAPI.html");
	}

	@Before(Tx.class)
	public void return_url() throws IOException {
		// 获取支付宝GET过来反馈信息
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
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}

		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		// 商户订单号
		String out_trade_no = new String(getPara("out_trade_no").getBytes(
				"ISO-8859-1"), "UTF-8");
		// 支付宝交易号
		String trade_no = new String(
				getPara("trade_no").getBytes("ISO-8859-1"), "UTF-8");
		// 交易状态
		String trade_status = new String(getPara("trade_status").getBytes(
				"ISO-8859-1"), "UTF-8");
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

		// 计算得出通知验证结果
		boolean verify_result = AlipayNotify.verify(params);

		if (verify_result) {// 验证成功
			// ////////////////////////////////////////////////////////////////////////////////////////
			// 请在这里加上商户的业务逻辑程序代码

			// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			if (trade_status.equals("TRADE_FINISHED")
					|| trade_status.equals("TRADE_SUCCESS")) {
				//Orders orders = Orders.dao.findByNo(out_trade_no);
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
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序
			}

			// 该页面可做页面美工编辑
			// ——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

			// ////////////////////////////////////////////////////////////////////////////////////////

			redirect("/buyerOrder/findUserOrder");
		} else {
			// 该页面可做页面美工编辑
			// response.getWriter().println("验证失败");
			renderText("验证失败");
		}
	}


}
