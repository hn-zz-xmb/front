package com.meishi.front.controller.login;

import com.jfinal.log.Logger;
import com.meishi.front.controller.BaseController;
import com.meishi.model.Member;

public class LogoutController extends BaseController {
	private static Logger log = Logger.getLogger(LogoutController.class);
	public void index() {
		Member member = getSessionAttr("user_");
		if(member!=null){
			Member m = Member.dao.findById(member.getStr("id"));
			m.set("ticket", "").update();
		}
		removeSessionAttr("user_");
		removeSessionAttr("store");
		removeSessionAttr("cartItems_");
		redirect("/");
	}
}
