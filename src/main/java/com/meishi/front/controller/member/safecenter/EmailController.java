package com.meishi.front.controller.member.safecenter;

import com.jfinal.aop.Before;
import com.jfinal.aop.ClearInterceptor;
import com.jfinal.log.Logger;
import com.meishi.front.common.AppContextData;
import com.meishi.front.common.rabbit.SendRabbitMQMsg;
import com.meishi.front.common.util.GenerateLinkUtils;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.Member;
import com.meishi.model.MemberLink;
import com.meishi.util.StringUtil;
import com.meishi.util.ToolUtil;
import com.meishi.util.beetl.render.CaptchaRender;
import net.sf.json.JSONObject;

import java.util.HashMap;

/**
 * Created by rsp on 14-11-22.
 */
@Before(LoginInteceptor.class)
public class EmailController extends BaseController {
	private static Logger log = Logger.getLogger(EmailController.class);

    /**
     * 绑定邮箱页面
     */
    public void index(){
        Member member=Member.dao.findById(getUserIds(),"username,email");
		if(StringUtil.isNotBlank(member.getStr("email"))){
			redirect("/safecenter");
			return;
		}
		setAttr("member",member);
		render("/oldfront/account/security/email/setEmail.html");
    }

    /**
     * 更新邮箱
     */
    public void check(){
        JSONObject result = new JSONObject();
		if (!CaptchaRender.validate(getSessionAttr("register_code").toString(),
				getPara("pcode"))) {
			result.put("status", "n");
			result.put("error", "验证码不正确");
			renderJson(result.toString());
			return;
		}
		Member member = Member.dao.findByEmail(getPara("email"));
		if (member != null) {
			result.put("status", "n");
			result.put("error", "你的邮箱已被绑定,请重新输入");
			renderJson(result.toString());
			return;
		}
		// 发送邮件
		HashMap map = new HashMap();
		map.put("email", getPara("email"));
		map.put("id", getUserIds());
		map.put("randomCode", ToolUtil.getUuidByJdk(true));
		map.put("operate", AppContextData.MEMBER_OPERATE_VERIFYEMAIL);
		boolean flag=SendRabbitMQMsg.service.sendEmailMsg(map);
		if(flag) {
			result.put("status", "y");
		}else{
			result.put("status", "n");
		}
		renderJson(result.toString());
    }

	/**
	 * 验证激活链接
	 */
	@ClearInterceptor
	public void validCode(){
		MemberLink memberLink = MemberLink.dao.findMemberLink(2,getPara(GenerateLinkUtils.CHECK_CODE),"1");

		if (!MemberLink.dao.checkMemberLink(memberLink)) {
			setAttr("message", "激活码不存在或已失效");
			render("/oldfront/user/active_result.html");
			return;
		}

		Member member=Member.dao.findById(memberLink.getStr("member_id"));
		member.set("email", memberLink.getStr("type_no"));
		Member.dao.updateEmail(member, memberLink);
		setAttr("message", "激活成功！");
		redirect("/safecenter/email/success");
	}

    /**
     * 绑定成功
     */
	@ClearInterceptor
    public void success(){
        render("/oldfront/account/security/email/success.html");
    }
}
