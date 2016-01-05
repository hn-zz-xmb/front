package com.meishi.front.controller.member.usercenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;

//@Controller(controllerKey = "/account/checkemail")
@Before(LoginInteceptor.class)
public class AccountEmailController extends BaseController {
	private static Logger log = Logger.getLogger(AccountEmailController.class);
	// 用户激活
//	public void verifyEmail() {
//		if (StringUtils.isEmpty(getPara("code"))) {
//			setAttr("message", "激活码不存在或已失效");
//			render("/oldfront/user/active_result.html");
//		}
//
//		MemberLink memberLink = MemberLink.dao.findMemberLink(2,
//				getPara("code"), AppContextData.MEMBER_OPERATE_VERIFYEMAIL);
//		if (memberLink == null || !MemberLink.dao.checkMemberLink(memberLink)) {
//			setAttr("message", "激活码不存在或已失效");
//			render("/oldfront/user/active_result.html");
//		}
//		Member member = Member.dao.findById(memberLink.get("member_id"));
//		if (member == null) {
//			setAttr("message", "您激活的用户不存在");
//			render("/oldfront/user/active_result.html");
//		}
//		member.set("email", memberLink.get("type_no")).update();
//		memberLink.set("status", 0).update();
//		render("/oldfront/account/security/email/success.html");
//	}
//
//	// 邮箱链接验证
//	public void modifyModileVerify() {
//		if (StringUtils.isEmpty(getPara("code"))) {
//			setAttr("message", "激活码不存在或已失效");
//			render("");
//		}
//
//		MemberLink memberLink = MemberLink.dao.findMemberLink(2,
//				getPara("code"), AppContextData.MEMBER_OPERATE_MODIFYPHONE);
//		if (memberLink == null || !MemberLink.dao.checkMemberLink(memberLink)) {
//			setAttr("message", "激活码不存在或已失效");
//			render("");
//		}
//		Member member = Member.dao.findById(memberLink.get("member_id"));
//		if (member == null) {
//			setAttr("message", "您激活的用户不存在");
//			render("");
//		}
//		render("");
//	}

}
