package com.meishi.front.common.controller;

import com.meishi.front.common.rabbit.SendRabbitMQMsg;
import com.meishi.front.controller.BaseController;

import java.util.HashMap;

public class SendEmailController extends BaseController {

	public void send() {
		// 1.首先得到用户的输入类型

		HashMap map = new HashMap();
		map.put("email", "niao.shuai123@163.com");
		map.put("id", "123231223");
		map.put("randomCode", "324234");
		map.put("operate", "123123");
		SendRabbitMQMsg.service.sendEmailMsg(map);
	}
}
