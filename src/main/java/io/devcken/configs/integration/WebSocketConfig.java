package io.devcken.configs.integration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * {@link AbstractWebSocketMessageBrokerConfigurer}를 확장함으로써 {@link StompEndpointRegistry}를 이용해 STOMP endpoint를 등록한다.
 *
 * @author Leejun Choi
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	/**
	 * {@inheritDoc}
	 *
	 * @param registry
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/send");
		registry.setApplicationDestinationPrefixes("/receive");

		super.configureMessageBroker(registry);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param registry
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/withws").withSockJS();
	}
}
