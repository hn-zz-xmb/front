package com.meishi.front.controller.member.safecenter;

import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.controller.BaseController;
import com.meishi.front.interceptor.LoginInteceptor;
import com.meishi.model.Member;
import net.sf.json.JSONObject;

/**
 * Created by rsp on 14-11-22.
 */
@Before(LoginInteceptor.class)
public class PwdController extends BaseController {
	private static Logger log = Logger.getLogger(PwdController.class);

    /**
     * 修改密码页面
     */
	public void index() {
		render("/oldfront/account/security/password/modify.html");
	}

    /**
     *  修改登录密码
     */
	public void update() {
		JSONObject result = new JSONObject();
		Member member = Member.dao.findById(getUserIds());
		if (!Member.dao.checkPwd(getPara("oldPwd"), member)) {
			result.put("error", "旧密码错误");
			result.put("status", "n");
			renderJson(result.toString());
			return;
		}
		Member.dao.updateloginpwd(member, null, getPara("password"));
		result.put("status", "y");
		renderJson(result.toString());
	}	
	// 跳转到修改成功页面
	public void success() {
		removeSessionAttr("user_");
		render("/oldfront/account/security/password/success.html");
	}
	//验证当前登录账号的的当前密码是否正确
	public void ajaxOldpwd(){
		String password=getPara("oldPwd");
		Member member=Member.dao.findById(getUserIds(),"salt,password");
		if (!Member.dao.checkPwd(password, member)) {
			renderText("false");
			return;
		}
		renderText("true");
	}
}
