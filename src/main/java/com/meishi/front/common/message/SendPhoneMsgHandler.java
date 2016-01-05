package com.meishi.front.common.message;

import com.meishi.front.ext.rabbitmq.EndPoint;
import com.meishi.model.MemberLink;
import com.meishi.util.DateUtil;
import com.meishi.util.ToolUtil;
import com.meishi.util.msg.SendMsgTemplate;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.time.DateUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendPhoneMsgHandler extends EndPoint implements Runnable, Consumer {

	public SendPhoneMsgHandler(String endpointName) throws IOException {
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

			sendPhoneMsg(map);
			// 打印消息详情
			// logger.info("Email:{}, ObjectType:{}",
			// mapMessage.getString("email"),
			// mapMessage.getStringProperty("objectType"));
		} catch (Exception e) {
			// logger.error("处理消息时发生异常.", e);
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

	public void sendPhoneMsg(Map map) {
		SendMsgTemplate.sendMsg((String) map.get("msgType"), (String) map.get("phone"),
				(List<String>) map.get("params"));

		new MemberLink()
				.set("id", ToolUtil.getUuidByJdk(true))
				.set("type", 1)
				.set("status", 1)
				.set("lose_time",
						DateUtil.format(DateUtils.addMinutes(new Date(), 15),
								"yyyyMMddHHmmss"))
				.set("type_no", map.get("phone"))
				.set("code", map.get("phone_code"))
				.set("operate", map.get("msgType"))
				.set("member_id", map.get("memberId")).save();
	}
}
