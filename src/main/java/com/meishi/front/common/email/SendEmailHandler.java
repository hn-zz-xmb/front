package com.meishi.front.common.email;

import com.meishi.front.common.AppContextData;
import com.meishi.front.common.util.GenerateLinkUtils;
import com.meishi.front.ext.rabbitmq.EndPoint;
import com.meishi.model.MemberLink;
import com.meishi.util.DateUtil;
import com.meishi.util.ToolUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.time.DateUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SendEmailHandler extends EndPoint implements Runnable, Consumer {

	public SendEmailHandler(String endpointName) throws IOException {
		super(endpointName);
	}

	@Override
	public void handleConsumeOk(String consumerTag) {
	}

	@Override
	public void handleCancelOk(String consumerTag) {

	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handleDelivery(String arg0, Envelope arg1,
			BasicProperties arg2, byte[] body) throws IOException {
		Map map = (HashMap) SerializationUtils.deserialize(body);

		try {

			sendEmail(map);
			// 打印消息详情
			// logger.info("Email:{}, ObjectType:{}",
			// mapMessage.getString("email"),
			// mapMessage.getStringProperty("objectType"));
		} catch (Exception e) {
			// logger.error("处理消息时发生异常.", e);
			e.printStackTrace();
		}
	}

	@Override
	public void handleShutdownSignal(String consumerTag,
			ShutdownSignalException sig) {

	}

	@Override
	public void handleRecoverOk(String consumerTag) {

	}

	@Override
	public void run() {
		try {
			channel.basicConsume(endPointName, true, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendEmail(Map map) {
		String operate = (String) map.get("operate");
		String code = GenerateLinkUtils.generateCheckcode(map);
		map.put("operateCode", code);
		new MemberLink()
				.set("id", ToolUtil.getUuidByJdk(true))
				.set("type", 2)
				.set("type_no", map.get("email"))
				.set("code", map.get("operateCode"))
				.set("lose_time",
						DateUtil.format(DateUtils.addDays(new Date(), 1),
								"yyyyMMddHHmmss"))
				.set("operate", map.get("operate"))
				.set("status","1")
				.set("member_id", map.get("id")).save();

//		if (operate.equals(AppContextData.MEMBER_OPERATE_REGIST)) {
//			AccountEmailUtil.service.sendAccountActivateEmail(map);
//		} else if (operate.equals(AppContextData.MEMBER_OPERATE_RESETPWD)) {
//			AccountEmailUtil.service.resetPwdEmail(map);
//		} else if (operate.equals(AppContextData.MEMBER_OPERATE_MODIFYPWD)) {
//			AccountEmailUtil.service.modifyPwdEmail(map);
//		} else if (operate.equals(AppContextData.MEMBER_OPERATE_VERIFYEMAIL)) {
//			// yes
//			AccountEmailUtil.service.verifyEmail(map);
//		} else if (operate.equals(AppContextData.MEMBER_OPERATE_MODIFYPAYPWD)) {
//			AccountEmailUtil.service.modifyPayPwdEmail(map);
//		} else if (operate.equals(AppContextData.MEMBER_OPERATE_MODIFYPHONE)) {
//			AccountEmailUtil.service.modifyPhone(map);
//		} else {
//			AccountEmailUtil.service.test(map);
//		}

		switch (operate){
			case AppContextData.MEMBER_OPERATE_REGIST:
				AccountEmailUtil.service.sendAccountActivateEmail(map);
				break;
			case AppContextData.MEMBER_OPERATE_VERIFYEMAIL:
				AccountEmailUtil.service.verifyEmail(map);
				break;
			case AppContextData.MEMBER_OPERATE_MODIFYPHONE:
				AccountEmailUtil.service.modifyPhone(map);
				break;
			case AppContextData.MEMBER_OPERATE_RESETPWD:
//				AccountEmailUtil.service.resetPwdEmail(map);
				break;
			case AppContextData.MEMBER_OPERATE_MODIFYPWD:
//				AccountEmailUtil.service.modifyPwdEmail(map);
				break;
			case AppContextData.MEMBER_OPERATE_MODIFYPAYPWD:
//				AccountEmailUtil.service.modifyPayPwdEmail(map);
				break;
		}
	}
}
