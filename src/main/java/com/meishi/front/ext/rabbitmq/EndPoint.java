package com.meishi.front.ext.rabbitmq;

import com.meishi.front.config.PropertyConfig;
import com.meishi.util.StringUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

public abstract class EndPoint {

	protected Channel channel;
	protected Connection connection;
	protected String endPointName;

	public EndPoint(String endpointName) throws IOException {
		this.endPointName = endpointName;

		// Create a connection factory
		ConnectionFactory factory = new ConnectionFactory();

		// hostname of your rabbitmq server
		String host= PropertyConfig.me().getProperty("rabbit.host");
		String username= PropertyConfig.me().getProperty("rabbit.username");
		String password= PropertyConfig.me().getProperty("rabbit.password");
		factory.setHost(host);

		if(StringUtil.isNotBlank(username) && StringUtil.isNotBlank(password)) {
			factory.setUsername(username);
			factory.setPassword(password);
		}

		// getting a connection
		connection = factory.newConnection();

		// creating a channel
		channel = connection.createChannel();

		// declaring a queue for this channel. If queue does not exist,
		// it will be created on the server.
		channel.queueDeclare(endpointName, false, false, false, null);
	}

	/**
	 * 关闭channel和connection。并非必须，因为隐含是自动调用的。
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		this.channel.close();
		this.connection.close();
	}
}
