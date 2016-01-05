package com.meishi.front.controller.member.safecenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.Member;

//@Controller(controllerKey = "/account/security")
@Before(LoginInteceptor.class)
public class SecurityController extends BaseController {
    private static Logger log = Logger.getLogger(SecurityController.class);
//	/**
//	 * 安全中心
//	 */
//	public void index() {
//		setAttr("user", Member.dao.findById(getUserIds()));
//		render("/oldfront/account/security/securitycenter.html");
//	}
//
//
//
//	// 邮箱绑定
//	// 跳转到绑定邮箱页面
//	public void toSetEmail() {
//		render("/oldfront/account/security/email/setEmail.html");
//	}
//
//	// 绑定邮箱
//	public void setEmail() {
//		JSONObject result = new JSONObject();
//		if (!CaptchaRender.validate(getSessionAttr("register_code").toString(),
//				getPara("pcode"))) {
//			result.put("status", "n");
//			result.put("error", "验证码不正确");
//			renderJson(result.toString());
//			return;
//		}
//		Member member = Member.dao.findByEmail(getPara("email"));
//		if (member != null) {
//			result.put("status", "n");
//			result.put("error", "你的邮箱已被绑定,请重新输入");
//			renderJson(result.toString());
//			return;
//		}
//		// 发送邮件
//		Member member2 = Member.dao.findById(getUserIds());
//		Map map = new HashMap();
//		map.put("email", getPara("email"));
//		map.put("id", member2.getStr("id"));
//		map.put("randomCode", ToolUtil.getUuidByJdk(true));
//		map.put("operate", AppContextData.MEMBER_OPERATE_VERIFYEMAIL);
//		SendRabbitMQMsg.service.sendEmailMsg(map);
//		result.put("status", "y");
//		renderJson(result.toString());
//	}
//
//	// 绑定手机号码
//	// 前往绑定页面
//	public void toSetMobile() {
//		render("");
//	}
//
//	// 绑定手机号码并验证
//	public void verifyByMobile() {
//		JSONObject result = new JSONObject();
//		// 1.判断手机号码是否存在
//		if (StringUtils.isEmpty(getPara("phone"))) {
//			result.put("status", "n");
//			result.put("error", "请填写你的手机号码");
//			renderJson(result.toString());
//		}
//		if (StringUtils.isEmpty(getPara("phoneCode"))) {
//			result.put("status", "n");
//			result.put("error", "请填写你的手机验证码");
//			renderJson(result.toString());
//		}
//		Member phonemember = Member.dao.findByPhone(getPara("phone"));
//		if (phonemember != null) {
//			result.put("status", "n");
//			result.put("error", "你的手机号码已被绑定,请检查你的手机号码");
//			renderJson(result.toString());
//		}
//		MemberLink memberLink = MemberLink.dao.findPhoneMemberLink(1,
//				getPara("phone'"), SendMsgTemplate.USE_CODE);
//		if (!MemberLink.dao.checkMemberLink(memberLink)) {
//			result.put("status", "n");
//			result.put("error", "激活码不存在或已失效");
//			renderJson(result.toString());
//		}
//
//		Member member = Member.dao.findById(getUserIds());
//		member.set("tel", getPara("phone")).update();
//		memberLink.set("status", 0).update();
//		result.put("status", "y");
//		renderJson(result.toString());
//	}
//
//	// 修改手机号码成功
//	public void verifyMobileSuccess() {
//		render("");
//	}
//
//	// 前往选择修改手机号码方式的页面
//	public void goModifyPhonePage() {
//		setAttr("member", Member.dao.findById(getUserIds()));
//		render("");
//	}
//
//	// 通过邮箱修改手机号码
//	public void modifyPhoneByEmail() throws IOException {
//		Member member = Member.dao.findById(getUserIds());
//		if (member != null) {
//			// 发邮件
//			HashMap<String, Object> message = new HashMap<String, Object>();
//			message.put("email", member.getStr("email"));
//			message.put("id", member.getStr("id"));
//			message.put("random_code", member.getStr("random_code"));
//			message.put("operate", AppContextData.MEMBER_OPERATE_MODIFYPHONE);
//			SendRabbitMQMsg.service.sendEmailMsg(message);
//		}
//		render("");
//	}
//
//	// 通过邮箱更换手机号(执行修改并验证)
//	public void modifyMoileByEmail() {
//		JSONObject result = new JSONObject();
//		// 1.判断手机号码是否存在
//		if (StringUtils.isEmpty(getPara("newPhone"))) {
//			result.put("status", "n");
//			result.put("error", "请填写你的手机号码");
//			renderJson(result.toString());
//		}
//
//		if (StringUtils.isEmpty(getPara("phoneCode"))) {
//			result.put("status", "n");
//			result.put("error", "请填写你的手机验证码");
//			renderJson(result.toString());
//		}
//		MemberLink memberLink = MemberLink.dao.findPhoneMemberLink(1,
//				getPara("newPhone"), SendMsgTemplate.USE_CODE);
//		if (!MemberLink.dao.checkMemberLink(memberLink)) {
//			result.put("status", "n");
//			result.put("error", "激活码不存在或已失效");
//			renderJson(result.toString());
//		}
//		Member member = Member.dao.findById(getUserIds());
//		member.set("tel", getPara("newPhone")).update();
//		memberLink.set("status", 0).update();
//		result.put("status", "y");
//		renderJson(result.toString());
//	}
//
//	// 更换成功
//	public void updateByEmailSuccess() {
//		render("");
//	}
//
//	// 通过旧的手机号码验证
//	public void sendModifyPhoneMsg() {
//		// Member member = Member.dao.findById(getUserIds());
//		List<String> params = new LinkedList<String>();// 短信参数
//		// 生成随机码
//		String phone_code = GenerateUtils.generateIntRandom(6);
//		params.add(phone_code);
//		// 发短信 未完成
//
//	}
//
//	// 验证短信验证码
//	public void checkPhoneCode() {
//		JSONObject result = new JSONObject();
//		MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1,
//				getPara("phonecode"), SendMsgTemplate.USE_CODE);
//		if (!MemberLink.dao.checkMemberLink(memberLink)) {
//			result.put("status", "n");
//			result.put("error", "短信验证码不正确或者已过期");
//			renderJson(result.toString());
//		}
//		result.put("status", "y");
//		renderJson(result.toString());
//	}
//
//	// 新手机号码直接验证
//	public void toVerifyMobile() {
//		render("");
//	}
//
//	public void verifyMobile() {
//		JSONObject result = new JSONObject();
//		Member member = Member.dao.findById(getUserIds());
//		if (StringUtils.isEmpty(getPara("phone"))) {
//			result.put("status", "n");
//			result.put("error", "手机号码不能为空");
//			renderJson(result.toString());
//		}
//		if (!getPara("phone").equals(member.get("tel"))) {
//			result.put("status", "n");
//			result.put("error", "手机号码输入错误");
//			renderJson(result.toString());
//		}
//		result.put("status", "y");
//		renderJson(result.toString());
//	}
//
//	// 修改支付密码
//	// 前往修改支付密码页面
//	public void goSetPayPwd() {
//		setAttr("member", Member.dao.findById(getUserIds()));
//		render("");
//	}
//
//	// 修改支付密码时候需要发短信此处发短信功能
//	// 未完成
//
//	// 修改支付密码
//	public void setPayPwd() {
//		JSONObject result = new JSONObject();
//		MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1,
//				getPara("phonecode"), SendMsgTemplate.PAY_CODE);
//		if (!MemberLink.dao.checkMemberLink(memberLink)) {
//			result.put("status", "n");
//			result.put("error", "短信验证码不正确");
//			renderJson(result.toString());
//		}
//		Member member = Member.dao.findById(getUserIds());
//		if (!Member.dao.checkPayPwd(getPara("currentPwd"), member)) {
//			result.put("status", "n");
//			result.put("error", "当前支付密码输入错误");
//			renderJson(result.toString());
//		}
//		Member.dao.updatepaypwd(member, memberLink, getPara("repeatPwd"));
//		result.put("status", "y");
//		renderJson(result.toString());
//	}
//
//	// 修改成功
//	public void setPayPwdSuccess() {
//		render("");
//	}

    /**
     * 跳转到安全中心
     */
    public void index(){
        Member member=Member.dao.findById(getUserIds(),"tel,email,salt,pay_salt");
        setAttr("member",member);
        render("/safecenter/securitycenter.html");
    }

}
