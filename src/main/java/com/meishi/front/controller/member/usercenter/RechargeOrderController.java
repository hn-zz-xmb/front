package com.meishi.front.controller.member.usercenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.common.AppContextData;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.Coin;
import com.meishi.model.Member;
import com.meishi.model.RechargeOrder;
import com.meishi.util.DateUtil;
import com.meishi.util.GenerateUtils;
import com.meishi.util.ToolUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

@Before(LoginInteceptor.class)
// @Controller(controllerKey = "/account/rechargeOrder")
public class RechargeOrderController extends BaseController {
	private static Logger log = Logger.getLogger(RechargeOrderController.class);

	// 跳转到充值界面
	public void index() {
		Member member = Member.dao.findById(getUserIds());
		Coin coin = Coin.dao.findById(member.getStr("coin_id"));
		setAttr("member", member);
		setAttr("coin", coin);
		render("/usercenter/chongzhi_center.html");
	}

	// 充值验证和充值跳转
	public void dorecharge() {
		JSONObject result = new JSONObject();
		// 1.验证充值方式
		// 如果为空,则默认为支付宝
		if (!"zfb".equals(getPara("choicepayway"))
				&& !AppContextData.Bank.checkValue(getPara("choicepayway"))) {
			result.put("info", "支付方式选择错误");
			result.put("status", "n");
			renderJson(result.toString());
			return;
		}
		// 2.验证金额
		if (StringUtils.isEmpty(getPara("money"))
				|| new BigDecimal(getPara("money"))
						.compareTo(new BigDecimal(0)) < 1) {
			result.put("info", "金额错误");
			result.put("status", "n");
			renderJson(result.toString());
			return;
		}
		RechargeOrder rechargeOrder = new RechargeOrder()
				.set("id", ToolUtil.getUuidByJdk(true))
				.set("name", "用户充值")
				.set("num", GenerateUtils.orderNo())
				.set("r_money", new BigDecimal(getPara("money")))
				.set("member_id", getUserIds())
				.set("status", AppContextData.OrderStatus.submitted.name())
				.set("create_date",
						DateUtil.format(new Date(), "yyyyMMddHHmmss"))
				.set("payway",
						"zfb".equals(getPara("choicepayway")) ? ""
								: getPara("choicepayway"));
		rechargeOrder.save();
		result.put("status", "y");
		result.put("url",
				"alipaybank/api?orderNo=" + rechargeOrder.getStr("num"));
		renderJson(result.toString());
	}
}
