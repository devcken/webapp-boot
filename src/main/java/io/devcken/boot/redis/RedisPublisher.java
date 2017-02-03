package io.devcken.boot.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Redis Message를 publshing하기 위한 publisher를 구현한다.
 */
public class RedisPublisher {
	private RedisTemplate redisTemplate;
	private String channelName;
	private final AtomicLong counter = new AtomicLong(0);

	public RedisPublisher(RedisTemplate redisTemplate,
	                      String channelName) {
		this.redisTemplate = redisTemplate;
		this.channelName = channelName;
	}

	@Scheduled(fixedDelay = 5000)
	public void publish() {
		redisTemplate.convertAndSend(this.channelName, "Message " + counter.incrementAndGet() + ", " + Thread.currentThread().getName());
	}
}