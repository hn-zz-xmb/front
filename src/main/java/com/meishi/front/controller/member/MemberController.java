package com.meishi.front.controller.member;

import com.jfinal.log.Logger;
import com.meishi.front.common.AppContextData;
import com.meishi.front.common.rabbit.SendRabbitMQMsg;
import com.meishi.front.controller.BaseController;
import com.meishi.model.Member;
import com.meishi.model.MemberLink;
import com.meishi.util.GenerateUtils;
import com.meishi.util.ToolUtil;
import com.meishi.util.beetl.render.CaptchaRender;
import com.meishi.util.msg.SendMsgTemplate;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MemberController extends BaseController {
	private static Logger log = Logger.getLogger(MemberController.class);
	/**
	 * 账户激活
	 */
	public void activate() {
		MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(2,
				getPara("email"), AppContextData.MEMBER_OPERATE_REGIST,getPara("phone"));

		if (!MemberLink.dao.checkMemberLink(memberLink)) {
			setAttr("message", "激活码不存在或已失效");
			render("/oldfront/user/active_result.html");
		}

		// 得到用户
		Member member = Member.dao.findByEmail(getPara("email"));
		if (member == null) {
			setAttr("message", "用户不存在");
			render("/oldfront/user/active_result.html");
		}

		member.set("status", 1).set("pay_pwd", member.getStr("password"))
				.set("pay_salt", member.get("pay_salt"));
		Member.dao.activeMember(member, memberLink);
		setAttr("message", "激活成功！");
		setSessionAttr("user_", member);
		redirect("/member/activeSuccess");
	}

	// 激活成功跳转链接
	public void activeSuccess() {
		render("/oldfront/user/active_success.html");
	}

	// 忘记密码跳转链接
	public void forgetPwd() {
		render("/oldfront/user/reset_pwd/forget_pwd.html");
	}

	// 选择密码修改方式前进行验证
	public void checkInfo() {
		JSONObject result = new JSONObject();
		if (StringUtils.isEmpty(getPara("email"))) {
			result.put("status", "n");
			result.put("info", "请先确认帐号");
			renderJson(result.toString());
			return;
		}
		// 1.验证验证码是否正确
		if (!CaptchaRender.validate(getSessionAttr("register_code").toString(),
				getPara("captchaCode"))) {
			result.put("status", "n");
			result.put("info", "验证码输入错误");
			renderJson(result.toString());
			return;
		}
		// 2.验证用户名是否存在
		Member member = Member.dao.findByLogin(getPara("email"));

		if (member == null) {
			result.put("status", "n");
			result.put("info", "用户名不存在");
			renderJson(result.toString());
			return;
		}
		result.put("status", "y");
		result.put("url", "/member/chooseStyle");
		setSessionAttr("loginUser", member);// 点击处理忘记登录密码
		setSessionAttr("loginName", getPara("email"));// 点击处理忘记登录密码
		renderJson(result.toString());
	}

	// 选择修改方式
	public void chooseStyle() {
		String email = getSessionAttr("loginName");
		if (StringUtils.isEmpty(email)) {
			redirect("/member/forgetPwd");
		}
		render("/oldfront/user/reset_pwd/choose_style.html");
	}

	// 通过邮箱修改的发送激活链接
	public void sendForgetPwdEmail() {
		Member member = getSessionAttr("loginUser");
		if (member == null) {
			redirect("/member/forgetPwd");
		}
		if (member == null || StringUtils.isEmpty(member.getStr("email"))) {
			setAttr("error", "邮箱不存在");
			render("/oldfront/user/reset_pwd/forget_pwd.html");
		}
		member.put("operate", AppContextData.MEMBER_OPERATE_RESETPWD);
		HashMap map = new HashMap();
		map.put("email", member.getStr("email"));
		map.put("id", member.getStr("id"));
		map.put("randomCode", member.get("random_code"));
		map.put("operate", AppContextData.MEMBER_OPERATE_RESETPWD);
		SendRabbitMQMsg.service.sendEmailMsg(map);
		setSessionAttr("email", member.get("email"));
		redirect("/member/sendForgetPwdEmailsuccess");
	}

	// 发送成功
	public void sendForgetPwdEmailsuccess() {
		String email = getSessionAttr("email");
		if (StringUtils.isEmpty(email)) {
			redirect("/member/forgetPwd");
		}
		render("/oldfront/user/reset_pwd/findByEmail.html");
	}

	/*********************************** Email修改密码 begin *******************************************/
	// 重新发送激活链接
	public void sendAForgetPwdEmail() {
		JSONObject result = new JSONObject();
		String email = getSessionAttr("email");
		if (StringUtils.isEmpty(email)) {
			result.put("error", "错误的邮箱信息");
			result.put("isSend", false);
			renderJson(result.toString());
			return;
		}
		Member member = Member.dao.findByEmail(email);
		if (member == null) {
			result.put("error", "邮箱不存在");
			result.put("isSend", false);
			renderJson(result.toString());
			return;
		}
		member.set("random_code", ToolUtil.getUuidByJdk(true));
		Map map = new HashMap();
		map.put("email", member.getStr("email"));
		map.put("id", member.getStr("id"));
		map.put("randomCode", member.get("random_code"));
		map.put("operate", AppContextData.MEMBER_OPERATE_RESETPWD);
		result.put("isSend", true);
		renderJson(result.toString());
	}

	// 检验重置密码链接
	public void resetPwd() {
		MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(2,
				getPara("checkCode"), AppContextData.MEMBER_OPERATE_RESETPWD,getPara("phone"));

		if (memberLink == null || !MemberLink.dao.checkMemberLink(memberLink)) {
			setAttr("message", "激活码不存在或已失效");
			render("/oldfront/user/active_result.html");
		}
		Member member = Member.dao.findByEmail(memberLink.getStr("type_no"));
		if (member == null) {
			setAttr("message", "用户名不存在");
			render("/oldfront/user/resetPwd_result.html");
		}

		setSessionAttr("email", member.get("email"));
		render("/oldfront/user/reset_pwd/reset_pwd_email.html");
	}

	public void ajaxresetPwd() {
		JSONObject result = new JSONObject();
		String email = getSessionAttr("email");
		if (StringUtils.isEmpty(email)) {
			result.put("status", "n");
			result.put("isRe", true);
			result.put("re_url", "/member/forgetPwd");
			renderJson(result.toString());
		}

		MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(2,
				email, AppContextData.MEMBER_OPERATE_RESETPWD,getPara("phone"));
		if (memberLink == null || !MemberLink.dao.checkMemberLink(memberLink)) {
			result.put("status", "n");
			result.put("isRe", false);
			result.put("error", "请求出错,或者你的链接已过期");
			renderJson(result.toString());
		}
		Member member = Member.dao.findByEmail(email);
		Member.dao.updateloginpwd(member, memberLink, getPara("newPwd"));
		result.put("status", "y");
		result.put("url", "/member/resetPwdSuccess");
		renderJson(result.toString());
	}

	// 修改成功
	public void resetPwdSuccess() {
		render("/oldfront/user/reset_pwd/reset_succeed.html");
	}

	/******************************** Email修改密码 over **************************************************/
	/******************************** tel修改密码 begin **************************************************/
	public void sendForgetPwdMsg() {
		Member member = (Member) getSession().getAttribute("loginUser");
		if (member == null) {
			redirect("/member/forgetPwd");
		}
		if (member == null || StringUtils.isEmpty(member.getStr("tel"))) {
			setAttr("error", "手机号码不存在");
			render("/oldfront/user/reset_pwd/forget_pwd.html");
		}
		List<String> params = new LinkedList<String>();// 短信参数
		// 生成随机码
		String phone_code = GenerateUtils.generateIntRandom(6);
		params.add(phone_code);
		HashMap map = new HashMap();
		map.put("msgType", SendMsgTemplate.FORGET_PWD_CODE);
		map.put("phone", member.getStr("tel"));
		map.put("phone_code", phone_code);
		map.put("params", params);
		SendRabbitMQMsg.service.sendPhoneMsg(map);
		setSessionAttr("tel", member.getStr("tel"));
		redirect("/member/sendForgetPwdMsgSuccess");
	}

	// 短信发送成功
	public void sendForgetPwdMsgSuccess() {
		String tel = getSessionAttr("tel");
		if (StringUtils.isEmpty(tel)) {
			redirect("/member/forgetPwd");
		}
		setAttr("phone", tel);
		render("/oldfront/user/reset_pwd/findByPhone.html");
	}

	// 检验重置密码Code
	public void resetPwdCheck() {
		JSONObject result = new JSONObject();

		String tel = (String) getSession().getAttribute("tel");
		if (StringUtils.isEmpty(tel)) {
			result.put("status", "n");
			result.put("isRe", true);
			result.put("re_url", "/member/forgetPwd");
			renderJson(result.toString());
			return;
		}

		MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1,
				getPara("phoneCode"), SendMsgTemplate.FORGET_PWD_CODE,getPara("phone"));

		if (memberLink == null || !MemberLink.dao.checkMemberLink(memberLink)) {
			result.put("status", "n");
			result.put("isRe", false);
			result.put("info", "激活码不存在或已失效");
			renderJson(result.toString());
			return;
		}

		result.put("status", "y");
		result.put("url", "/member/toResetPwd");
		renderJson(result.toString());
	}

	// 跳转重置密码界面
	public void toResetPwd() {
		String tel = (String) getSession().getAttribute("tel");
		if (StringUtils.isEmpty(tel)) {
			redirect("/member/forgetPwd");
		}
		render("/oldfront/user/reset_pwd/reset_pwd_phone.html");
	}

	// 重置密码
	public void resetPwdByPhone() {
		String tel = getSessionAttr("tel");
		JSONObject result = new JSONObject();
		if (StringUtils.isEmpty(tel)) {
			result.put("status", "n");
			result.put("isRe", true);
			result.put("re_url", "/member/forgetPwd");
			renderJson(result.toString());
		}

		MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1, tel,
				SendMsgTemplate.FORGET_PWD_CODE,getPara("phone"));

		if (memberLink == null || !MemberLink.dao.checkMemberLink(memberLink)) {
			result.put("status", "n");
			result.put("isRe", false);
			result.put("error", "请求出错,或者你的链接已过期");
			renderJson(result.toString());
		}

		Member member = Member.dao.findByPhone(tel);
		Member.dao.updateloginpwd(member, memberLink, getPara("newPwd"));
		result.put("status", "y");
		result.put("url", "/member/resetPwdSuccess");
		renderJson(result.toString());
	}
	/******************************** tel修改密码 end **************************************************/
}
