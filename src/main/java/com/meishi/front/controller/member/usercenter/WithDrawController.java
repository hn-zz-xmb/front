package com.meishi.front.controller.member.usercenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Page;
import com.meishi.front.common.AppContextData;
import com.meishi.front.common.util.PwdUtils;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.*;
import com.meishi.util.DateUtil;
import com.meishi.util.GenerateUtils;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Before(LoginInteceptor.class)
// @Controller(controllerKey = "/account/withdraw")
public class WithDrawController extends BaseController {
	private static Logger log = Logger.getLogger(WithDrawController.class);

	// 跳转到提现页面
	public void index() {
		Member member = Member.dao.findById(getUserIds());
		List<Account> accounts = Account.dao
				.findByMemberId(member.getStr("id"));
		setAttr("account", accounts);
		setAttr("coin", Coin.dao.findById(member.getStr("coin_id")));
		render("/usercenter/tixian.html");
	}

	// 添加绑定银行卡
	public void toaddBankCard() {
		// 目前只支持建设银行提现，若存在其他此处为查询
		render("/usercenter/addBankDialog.html");
	}

	public void addBankCard() {
		JSONObject result = new JSONObject();
		Member member = Member.dao.findById(getUserIds());
		Account account = getModel(Account.class);
		account.set("id", ToolUtil.getUuidByJdk(true))
				.set("member_id", member.get("id")).set("is_default", 0)
				.set("add_time", DateUtil.format(new Date(), "yyyyMMddHHmmss"))
				.save();
		result.put("status", "y");
		renderJson(result.toString());
	}

	// 设置默认提现银行卡
	public void setdefault() {
		Account account = Account.dao.findById(getPara("accountId"));
		account.set("is_default", 1).update();
		redirect("/account/withdraw");
	}

	// 取消银行卡默认设置
	public void cancledefault() {
		Account account = Account.dao.findById(getPara("accountId"));
		account.set("is_default", 0).update();
		redirect("/account/withdraw");
	}

	// 删除绑定的银行卡
	public void deleteBankCard() {
		Account account = Account.dao.findById(getPara("accountId"));
		if (account == null) {
			renderError(404);
		} else {
			account.delete();
			redirect("/account/withdraw");
		}
	}

	// 提现(银行卡)
	public void withDraw() {
		JSONObject result = new JSONObject();
		Member member = Member.dao.findById(getUserIds());
		Coin coin = Coin.dao.findById(member.getStr("coin_id"));
		Withdraw withdraw = new Withdraw();
        //验证支付密码
        String payPwd=getPara("payPwd");
        if(StringUtil.isBlank(member.getStr("pay_salt"))){
            result.put("status", "n");
            result.put("error", "你的支付还没有设置,请先到安全中心设置!");
            renderJson(result.toString());
            return;
        }
        if(!PwdUtils.checkPayPwd(payPwd,member)){
            result.put("status", "n");
            result.put("error", "你的支付密码输入有误!");
            renderJson(result.toString());
            return;
        }
		// 验证用户是否有账户
		if (coin == null) {
			result.put("status", "n");
			result.put("error", "你的账户还没有激活!");
			renderJson(result.toString());
			return;
		}
		// 比较用户账户余额是否足够
		if (coin.getBigDecimal("account_money").compareTo(
				new BigDecimal(getPara("withdraw_money"))) < 0) {
			result.put("status", "n");
			result.put("error", "你的账户余额不足!");
			renderJson(result.toString());
			return;
		}
		// 设置提现信息
		withdraw.set("id", ToolUtil.getUuidByJdk(true))
				.set("withdraw_num", "TX" + GenerateUtils.orderNo())
				.set("withdraw_money",
						new BigDecimal(getPara("withdraw_money")))
				.set("account_id", getPara("account_id"))
				.set("audit_status", "0")
				.set("apply_time",
						DateUtil.format(new Date(), "yyyyMMddHHmmss"))
				.set("user_id", member.get("id")).save();
		coin.set(
				"account_money",
				coin.getBigDecimal("account_money").subtract(
						new BigDecimal(getPara("withdraw_money"))))
				.set("freeze_money", new BigDecimal(getPara("withdraw_money")))
				.update();
		result.put("status", "y");
		renderJson(result.toString());
	}

	// 前往提现页面（支付宝）
	public void towithdraw() {
		Member member = Member.dao.findById(getUserIds());
		setAttr("coin", Coin.dao.findById(member.getStr("coin_id")));
		render("/usercenter/tixian_ali.html");
	}

	// 支付宝提现（）

	// 跳转到提现记录页面
	public void findWithdrawRecord() {
		Member member = Member.dao.findById(getUserIds());
		Map<String, Object> params = new HashMap<String, Object>();
		if (member != null) {
			setAttr("dataDicList",
					Datadic.dao.findByGroup(AppContextData.ORDER_STATUS));
			params.put("startTime", getPara("startTime"));
			params.put("endTime", getPara("endTime"));
			params.put("userId", member.getStr("id"));
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
			Page<Withdraw> result = Withdraw.dao.findUserRecord(pageNum,
					pageSize, params);
			setAttr("page", result);
			setAttr("params", params);
			setAttr("page_params", paramsToStr(params));
			setAttr("coin", Coin.dao.findById(member.getStr("coin_id")));
			render("/usercenter/tixian_record.html");
		} else {
			render("/login");
		}
	}

}
