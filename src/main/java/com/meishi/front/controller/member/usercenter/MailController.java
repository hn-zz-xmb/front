package com.meishi.front.controller.member.usercenter;


import com.jfinal.aop.Before;
import com.jfinal.log.Logger;
import com.meishi.front.interceptor.LoginInteceptor;
@Before(LoginInteceptor.class)
//@Controller(controllerKey = "/account/webmessage")
public class MailController {
	private static Logger log = Logger.getLogger(MailController.class);
	//获取用户未读消息
	public void getWeiDu(){
		
		
	}
	
	
	
}
