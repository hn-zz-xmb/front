package com.meishi.front.common.rabbit;

import com.meishi.front.common.email.SendEmailHandler;
import com.meishi.front.common.message.SendPhoneMsgHandler;
import com.meishi.front.ext.rabbitmq.Producer;

import java.io.IOException;
import java.util.HashMap;

public class SendRabbitMQMsg {
	public static final SendRabbitMQMsg service = new SendRabbitMQMsg();

	/**
	 * 发送邮件
	 * 
	 * @param params
	 * @throws IOException
	 */
	public boolean sendEmailMsg(final HashMap<String,Object> params) {
		try {
			SendEmailHandler consumer = new SendEmailHandler("email");
			Thread consumerThread = new Thread(consumer);
			consumerThread.start();

			Producer producer = new Producer("email");
//			HashMap<String, Object> message = new HashMap<String, Object>();
//			message.put("email", map.get("email"));
//			message.put("id", map.get("id"));
//			message.put("randomCode", map.get("random_code"));
//			message.put("operate", map.get("operate"));
			producer.sendMessage(params);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 发送短信
	 * 
	 * @param message
	 * @throws IOException
	 */
	public boolean sendPhoneMsg(final HashMap<String, Object> message ) {
		try {
			SendPhoneMsgHandler
					consumer = new SendPhoneMsgHandler("msg");
			Thread consumerThread = new Thread(consumer);
			consumerThread.start();

			Producer producer = new Producer("msg");
//			HashMap<String, Object> message = new HashMap<String, Object>();
//			message.put("msgType", map.get("msgType"));
//			message.put("phone", map.get("phone"));
//			message.put("phone_code", map.get("phone_code"));
//			message.put("params", map.get("params"));
			producer.sendMessage(message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
}
