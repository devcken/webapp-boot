package io.devcken.integration;

import org.springframework.http.ResponseEntity;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

@MessagingGateway(defaultRequestChannel = "requestChannel", defaultReplyChannel = "replyChannel", defaultReplyTimeout = "10000")
public interface HttpMessagingService {
	<T> T send(Message<?> message);

	@Gateway(requestChannel = "requestChannelForNoBody")
	ResponseEntity<?> sendForNoBody(Message<?> message);
}
