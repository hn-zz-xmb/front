package com.meishi.front.controller.member.safecenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.Member;
import com.meishi.model.MemberLink;
import com.meishi.util.msg.SendMsgTemplate;
import net.sf.json.JSONObject;

/**
 * Created by rsp on 14-11-22.
 */
@Before(LoginInteceptor.class)
public class PayPwdController extends BaseController {
	private static Logger log = Logger.getLogger(PayPwdController.class);

	// 跳转到支付密码修改页面
	public void index() {
		Member member = Member.dao.findById(getUserIds(), "id,tel,pay_salt");
		if (member.getStr("tel") != null
				&& !"".equals(member.getStr("tel"))) {//  判断是否绑定手机
			if (member.getStr("pay_salt") != null
					&& !"".equals(member.getStr("pay_salt"))) {//判断是否存在支付密码
				setAttr("user", member);
				render("/oldfront/account/security/paypwd/modify.html");
			} else {
				redirect("/safecenter/paypwd/setPayPwd");
			}
		} else {
			redirect("/safecenter/phone/setUI");
		}
	}

	// 验证当前支付密码
	public void ajaxPaypwd() {
		String password = getPara("currentPwd");
		Member member = Member.dao.findById(getUserIds(), "pay_salt,pay_pwd");
		if (!Member.dao.checkPayPwd(password, member)) {
			renderText("false");
			return;
		}
		renderText("true");
	}

	// 修改支付密码
	public void update() {
		JSONObject result = new JSONObject();
		Member member = Member.dao.findById(getUserIds());
		if (!Member.dao.checkPayPwd(getPara("currentPwd"), member)) {
			result.put("error", "旧密码错误");
			result.put("status", "n");
			renderJson(result.toString());
			return;
		}
		MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1,
				getPara("phoneCode"), SendMsgTemplate.PAY_CODE,
				getPara("phone"));
		if (!MemberLink.dao.checkMemberLink(memberLink)) {
			result.put("status", "n");
			result.put("info", "手机动态码不正确或者已过期!");
			renderJson(result.toString());
			return;
		}
		Member.dao.updatepaypwd(member, null, getPara("password"));
		result.put("status", "y");
		renderJson(result.toString());
	}

	// 修改成功
	public void success() {
		render("/oldfront/account/security/paypwd/success.html");
	}

	// 跳转到支付密码设置页面
	public void setPayPwd() {
		Member member = Member.dao.findById(getUserIds(),"id,tel");
		if (member.getStr("tel")!= null && !"".equals(member.getStr("tel"))) {//  判断是否绑定手机
			setAttr("user", member);
			render("/oldfront/account/security/paypwd/setPayPwd.html");
		}else{
			redirect("/safecenter/phone/setUI");
		}
	}

	// 设置支付密码
	public void savePayPwd() {
		JSONObject result = new JSONObject();
		MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1,
				getPara("phoneCode"), SendMsgTemplate.PAY_CODE,
				getPara("phone"));
		if (memberLink != null && MemberLink.dao.checkMemberLink(memberLink)) {
			Member member = Member.dao.findByPhone(getPara("phone"));
			Member.dao.updatepaypwd(member, null, getPara("password"));
			result.put("status", "y");
		} else {
			result.put("status", "n");
			result.put("info", "手机动态码不正确或者已过期!");
		}
		renderJson(result.toString());
	}

}
