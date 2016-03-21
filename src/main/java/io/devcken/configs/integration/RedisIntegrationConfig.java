package io.devcken.configs.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicLong;

/**
 * spring-integration-redis를 이용한 Redis 통합 설정으로 PUB-SUB 패러다임을 사용하도록 구성하였다.
 *
 * @author Leejun Choi
 */
@Configuration
public class RedisIntegrationConfig {
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory();
	}

	/**
	 * Redis Message publishing을 위한 template를 bean으로 등록한다.
	 *
	 * @param jedisConnectionFactory
	 * @return {@link RedisTemplate}
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Object.class));
		redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class));

		return redisTemplate;
	}

	@Bean
	public RedisPublisher redisPublisher(RedisTemplate redisTemplate,
	                                      ChannelTopic channelTopic) {
		return new RedisPublisher(redisTemplate, channelTopic);
	}

	/**
	 * Redis Message를 subscribe하기 위한 listener를 bean으로 등록한다.
	 *
	 * @return {@link MessageListenerAdapter}
	 */
	@Bean
	public MessageListenerAdapter messageListenerAdapter() {
		return new MessageListenerAdapter(new RedisMessageListener());
	}

	/**
	 * Redis 채널을 위한 {@link ChannelTopic} 인스턴스를 bean으로 등록한다.
	 *
	 * @return {@link ChannelTopic}
	 */
	@Bean
	public ChannelTopic channelTopic() {
		return new ChannelTopic("pubsub:queue");
	}

	/**
	 * Redis Message를 subscribe하는 container를 등록한다.
	 *
	 * @param jedisConnectionFactory
	 * @param messageListenerAdapter
	 * @param channelTopic
	 * @return {@link RedisMessageListenerContainer}
	 */
	@Bean
	public RedisMessageListenerContainer messageListenerContainer(JedisConnectionFactory jedisConnectionFactory,
	                                                              MessageListenerAdapter messageListenerAdapter,
	                                                              ChannelTopic channelTopic) {
		final RedisMessageListenerContainer messageListenerContainer = new RedisMessageListenerContainer();

		messageListenerContainer.setConnectionFactory(jedisConnectionFactory);
		messageListenerContainer.addMessageListener(messageListenerAdapter, channelTopic);

		return messageListenerContainer;
	}

	/**
	 * Redis Message를 subscribe하여 처리하게 될 listener 클래스를 구현한다.
	 */
	private class RedisMessageListener implements MessageListener {
		private Logger logger = LoggerFactory.getLogger(RedisMessageListener.class);

		@Override
		public void onMessage(Message message, byte[] pattern) {
			logger.debug(message.toString());
		}
	}

	/**
	 * Redis Message를 publshing하기 위한 publisher를 구현한다.
	 */
	private class RedisPublisher {
		private RedisTemplate redisTemplate;
		private ChannelTopic channelTopic;
		private final AtomicLong counter = new AtomicLong(0);

		public RedisPublisher(RedisTemplate redisTemplate,
		                      ChannelTopic channelTopic) {
			this.redisTemplate = redisTemplate;
			this.channelTopic = channelTopic;
		}

		@Scheduled(fixedDelay = 5000)
		public void publish() {
			redisTemplate.convertAndSend(this.channelTopic.getTopic(), "Message " + counter.incrementAndGet() + ", " + Thread.currentThread().getName());
		}
	}
}
