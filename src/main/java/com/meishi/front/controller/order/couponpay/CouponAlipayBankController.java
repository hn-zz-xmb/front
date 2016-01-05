package com.meishi.front.controller.order.couponpay;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.common.AppContextData;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.RechargeOrder;
import com.meishi.util.pay.config.AlipayConfig;
import com.meishi.util.pay.util.AlipayNotify;
import com.meishi.util.pay.util.AlipaySubmit;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 充值
 */
// @Controller(controllerKey = "/pay/alipayBank/")
@Before(LoginInteceptor.class)
public class CouponAlipayBankController extends BaseController {

	private static Logger log = Logger.getLogger(CouponAlipayBankController.class);

	public void api() {
		RechargeOrder order = RechargeOrder.dao.findByNum(getPara("orderNo"));
		String payment_type = "1";
		String notify_url = AppContextData.DOMAIN+"/alipayNotify/notifyPay/notifyRecharge";
		String return_url = AppContextData.DOMAIN+"/alipaybank/return_url";
		String seller_email = new String("18039226858@163.com");
		String out_trade_no = new String(order.getStr("num"));
		String subject = new String(order.getStr("name"));
		String total_fee = new String(order.getBigDecimal("r_money").toString());
		String paymethod = "bankPay";
		String defaultbank = new String(order.getStr("payway"));
		String show_url = new String("");
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
		sParaTemp.put("body", "");

		if (StringUtils.isNotBlank(defaultbank)) {
			sParaTemp.put("paymethod", paymethod);
			sParaTemp.put("defaultbank", defaultbank);
		}
		sParaTemp.put("show_url", show_url);
		sParaTemp.put("anti_phishing_key", anti_phishing_key);
		sParaTemp.put("exter_invoke_ip", exter_invoke_ip);

		// 建立请求
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
		setAttr("sHtmlText", sHtmlText);
		render("/oldfront/pay/alipay/alipayBankAPI.html");
	}

	@SuppressWarnings("rawtypes")
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
		// String trade_no = new
		// String(request.getParameter("trade_no").getBytes(
		// "ISO-8859-1"), "UTF-8");
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
				// Orders order = orderService.findByOrderNo(out_trade_no);
				// // 判断该笔订单是否在商户网站中已经做过处理
				RechargeOrder order = RechargeOrder.dao.findByNum(out_trade_no);
				if (order != null) {
					if (!AppContextData.OrderStatus.finished.name().equals(
							order.get("status"))) {
						RechargeOrder.dao.updateStatus(order);
					}
				}
				// 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				// 如果有做过处理，不执行商户的业务程序
			}

			// 该页面可做页面美工编辑
			// ——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

			// ////////////////////////////////////////////////////////////////////////////////////////
			redirect("/account/accountcenter");
		} else {
			// 该页面可做页面美工编辑
			renderText("验证失败");
		}
	}
}
