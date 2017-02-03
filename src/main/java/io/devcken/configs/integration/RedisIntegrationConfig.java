package io.devcken.configs.integration;

import io.devcken.boot.redis.RedisMessageListener;
import io.devcken.boot.redis.RedisPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.inject.Inject;

/**
 * spring-integration-redis를 이용한 Redis 통합 설정으로 PUB-SUB 패러다임을 사용하도록 구성하였다.
 *
 * @author Leejun Choi
 */
@Configuration
@PropertySource("classpath:redis.properties")
public class RedisIntegrationConfig {
	private final Environment environment;

	@Inject
	public RedisIntegrationConfig(Environment environment) {
		this.environment = environment;
	}

	/**
	 * Redis connection을 위한 connection factory bean을 등록한다.
	 *
	 * @return {@link JedisConnectionFactory}
	 */
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();

		jedisConnectionFactory.setHostName(environment.getProperty("redis.host"));
		jedisConnectionFactory.setPort(Integer.parseInt(environment.getProperty("redis.port")));
		jedisConnectionFactory.setPassword(environment.getProperty("redis.password"));

		return jedisConnectionFactory;
	}

	/**
	 * Redis Message publishing을 위한 template를 bean으로 등록한다.
	 *
	 * @param jedisConnectionFactory {@link JedisConnectionFactory}
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
	 * Redis Message를 subscribe하는 container를 등록한다.
	 *
	 * @param jedisConnectionFactory {@link JedisConnectionFactory}
	 * @return {@link RedisMessageListenerContainer}
	 */
	@Bean
	public RedisMessageListenerContainer messageListenerContainer(JedisConnectionFactory jedisConnectionFactory,
	                                                              RedisMessageListener messageListener) {
		final RedisMessageListenerContainer messageListenerContainer = new RedisMessageListenerContainer();

		messageListenerContainer.setConnectionFactory(jedisConnectionFactory);

		messageListenerContainer.addMessageListener(messageListener, new ChannelTopic(messageListener.getChannelName()));

		return messageListenerContainer;
	}

	/**
	 * {@link MessageListener}를 구현한 뒤 bean으로 등록하고 이를 {@link RedisMessageListenerContainer}에 설정한다.
	 * @return {@link RedisMessageListener}
	 */
	@Bean
	RedisMessageListener messageListener() {
		return new RedisMessageListener();
	}

	/**
	 * Redis로 메시지를 전송하기 위한 bean으로 실제로는 이렇게 하지 않아도 된다.
	 * @param redisTemplate {@link RedisTemplate}
	 * @return {@link RedisPublisher}
	 */
	@Bean
	public RedisPublisher redisPublisher(RedisTemplate redisTemplate) {
		return new RedisPublisher(redisTemplate, "pubsub:queue");
	}
}
