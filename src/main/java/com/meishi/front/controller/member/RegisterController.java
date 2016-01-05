package com.meishi.front.controller.member;

import com.jfinal.log.Logger;
import com.meishi.front.common.AppContextData;
import com.meishi.front.common.rabbit.SendRabbitMQMsg;
import com.meishi.front.common.util.GenerateLinkUtils;
import com.meishi.front.controller.BaseController;
import com.meishi.model.Member;
import com.meishi.model.MemberLink;
import com.meishi.util.ToolUtil;
import com.meishi.util.beetl.render.CaptchaRender;
import com.meishi.util.msg.SendMsgTemplate;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class RegisterController extends BaseController {
	private static Logger log = Logger.getLogger(RegisterController.class);
	public void index() {
		setAttr("referee_no", getPara("refereeNo"));
		render("/user/regist.html");
	}

	public void registerdo() {
		JSONObject result = new JSONObject();
		if ("1".equals(getPara("registerType"))) {
			Member member = new Member().set("id", ToolUtil.getUuidByJdk(true))
					.set("tel", getPara("tel")).set("referee_no", getPara("referee_no"));
			String password = getPara("phonepassword");
			MemberLink memberLink = MemberLink.dao.findPhoneCodeMemberLink(1,
					getPara("phoneCode"), SendMsgTemplate.REGIST_CODE,getPara("tel"));
			if (!MemberLink.dao.checkMemberLink(memberLink)) {
				result.put("status", "n");
				result.put("info", "手机动态码不正确或者已过期!");
				renderJson(result.toString());
				return;
			}
			Member.dao.phoneregister(member, password, memberLink,
					getPara("registerType"));
		} else if ("2".equals(getPara("registerType"))) {
			Member member = new Member().set("id", ToolUtil.getUuidByJdk(true))
					.set("email", getPara("email"))
					.set("random_code", ToolUtil.getUuidByJdk(true))
					.set("username", getPara("username"))
					.set("referee_no", getPara("referee_no"));
			String password = getPara("password");
			if (!CaptchaRender.validate(getSessionAttr("register_code").toString()
					, getPara("email_code"))) {
				System.out.println("验证验证码");
				result.put("status", "n");
				result.put("info", "验证码输入错误");
				renderJson(result.toString());
				return;
			}
			member.put("operate", AppContextData.MEMBER_OPERATE_REGIST);
			Member.dao.phoneregister(member, password, null,
					getPara("registerType"));
			HashMap map = new HashMap();
			map.put("email", member.getStr("email"));
			map.put("id", member.getStr("id"));
			map.put("username", getPara("username"));
			map.put("operateCode", member.get("random_code"));
			map.put("operate", AppContextData.MEMBER_OPERATE_REGIST);
			if(!SendRabbitMQMsg.service.sendEmailMsg(map)){
				result.put("status", "n");
				result.put("info", "无法发送邮件");
				renderJson(result.toString());
				return;
			}
			setSessionAttr("email", "");
			getSession().setAttribute("email", member.getStr("email"));
			result.put("url",
					"register/success?email=" + member.getStr("email"));
		} else {
			result.put("status", "n");
			result.put("info", "无法解析你的注册请求");
			renderJson(result.toString());
			return;
		}
		result.put("status", "y");
		renderJson(result.toString());
	}

	
	// 注册成功UI
	public void success() {
		if (StringUtils.isEmpty(getPara("email"))) {
			render("404");
		}
		setAttr("email", getPara("email"));
		render("/oldfront/user/register_result.html");
	}

	// 弹框
	public void dialog() {
		setAttr("phone", getPara("phone"));
		render("/oldfront/user/_code.html");
	}

	public void checkDialgCode() {
		String code = getPara("email_code");// 验证码
		Object captchaCode = getSessionAttr("register_code");
		String md5RandomCode = null;
		if (captchaCode != null) {
			md5RandomCode = captchaCode.toString();
		}
		// String c = getSessionAttr("register_code");
		if (!CaptchaRender.validate(md5RandomCode, code)) {
			renderText("false");
			return;
		}
		renderText("true");
	}
	//验证手机号码是否存在
	public void ajaxPhone() {
		Member member = Member.dao.findByPhone(getPara("tel"));
		if (member == null ) {
			renderText("false");
			return;
		}else{
			renderText("true");
		}
	}
	//验证昵称是否存在
	public void ajaxName(){
		Member member=Member.dao.findByUsername(getPara("username"));
		if (member == null) {
			renderText("false");
			return;
		}
		renderText("true");
	}
	//验证邮箱是否存在
	public void ajaxEmail(){
		Member member=Member.dao.findByEmail(getPara("email"));
		if (member == null) {
			renderText("false");
			return;
		}
		renderText("true");
	}
	//邮箱注册号码激活
	public void validCode(){
		MemberLink memberLink = MemberLink.dao.findMemberLink(2,getPara(GenerateLinkUtils.CHECK_CODE),"1");
		if (!MemberLink.dao.checkMemberLink(memberLink)) {
			setAttr("message", "激活码不存在或已失效");
			render("/oldfront/user/active_result.html");
		}
		Member member=Member.dao.findById(memberLink.getStr("member_id"));
		member.set("status", memberLink.getInt("status"));
		Member.dao.activeMember(member, memberLink);
		setAttr("message", "激活成功！");
		redirect("/register/registersuccess");
	}
	//激活成功
	public void registersuccess(){
		render("/oldfront/user/registerResult.html");
	}
}
