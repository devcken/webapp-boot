package io.devcken.boot.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * Redis Message를 subscribe하여 처리하게 될 listener 클래스를 구현한다.
 */
public class RedisMessageListener implements MessageListener {
	private final Logger logger = LoggerFactory.getLogger(RedisMessageListener.class);
	private final String channelName = "pubsub:queue";

	@Override
	public void onMessage(Message message, byte[] pattern) {
		logger.debug(message.toString());
	}

	public String getChannelName() {
		return channelName;
	}
}
