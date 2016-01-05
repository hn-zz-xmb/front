package com.meishi.front.common.controller;

import com.meishi.front.common.rabbit.SendRabbitMQMsg;
import com.meishi.front.controller.BaseController;
import com.meishi.util.GenerateUtils;
import com.meishi.util.msg.SendMsgTemplate;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SendMsgController extends BaseController {

	public void send() {
		JSONObject result = new JSONObject();
		// 1.首先得到用户的输入类型
		String msgType = getPara("msgType");// 短信类型
		String phone = getPara("phone");

		List<String> params = new LinkedList<String>();// 短信参数
		// 生成随机码
		String phone_code = GenerateUtils.generateIntRandom(6);
		switch (msgType) {
		case SendMsgTemplate.REGIST_CODE:
			params.add(phone_code);
			break;
		case SendMsgTemplate.LOGIN_CODE:
			params.add(phone_code);
			break;
		case SendMsgTemplate.STORE_CODE:
			params.add(getPara("user_name"));
			params.add(getPara("store_name"));
			break;
		case SendMsgTemplate.PWD_CODE:
			params.add(phone_code);
			break;
		case SendMsgTemplate.GOODS_CODE:
			params.add(getPara("user_name"));
			params.add(phone_code);
			break;
		case SendMsgTemplate.PAY_CODE:
			params.add(phone_code);
			break;
		case SendMsgTemplate.USE_CODE:
			params.add(phone_code);
			break;
		case SendMsgTemplate.REGIST_STORE_CODE:
			params.add(phone_code);
			break;
		default:
			params.add(phone_code);
			break;
		}
		// 异步发送手机验证码
		// return sendMsgService.sendMsg(msgType, phone, phone_code, params,
		// getSessionUser() == null ? "" : getSessionUser().getId())
		// .toString();
		// return null;
		HashMap map = new HashMap();
		map.put("msgType", msgType);
		map.put("phone", phone);
		map.put("phone_code", phone_code);
		map.put("params", params);
		map.put("memberId",getUserIds());
		SendRabbitMQMsg.service.sendPhoneMsg(map);
		result.put("isMsg", true);
		renderJson(result.toString());
	}
}
