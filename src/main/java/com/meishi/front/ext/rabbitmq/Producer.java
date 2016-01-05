package com.meishi.front.ext.rabbitmq;

import org.apache.commons.lang.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;

public class Producer extends EndPoint {

	public Producer(String endpointName) throws IOException {
		super(endpointName);
	}

	public void sendMessage(Serializable object) throws IOException {
		channel.basicPublish("", endPointName, null,
				SerializationUtils.serialize(object));
	}

}
