package io.devcken.support.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.messaging.simp.SimpMessageType.*;

/**
 * Stomp client의 concurrent connection/subcription count를 관리하고 보관한다.
 *
 * @author Leejun Choi
 */
public class StompClientCountingInterceptor extends ChannelInterceptorAdapter {
	private final static Logger logger = LoggerFactory.getLogger(StompClientCountingInterceptor.class);

	private List<Object> connectedSessions;

	private Map<Object, List<Object>> subscribedSessions;

	public StompClientCountingInterceptor() {
		this.connectedSessions = new ArrayList<>();
		this.subscribedSessions = new HashMap<>();
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Message의 header 내의 simpMessageType에 따라 connected, disconnected, subscribed, unsubscribed 상태를 구분하여 세션 목록을 관리한다.</p>
	 *
	 * @param message {@inheritDoc}
	 * @param channel {@inheritDoc}
	 * @param sent {@inheritDoc}
	 * @param ex {@inheritDoc}
	 */
	@Override
	public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
		super.afterSendCompletion(message, channel, sent, ex);

		MessageHeaders headers = message.getHeaders();

		if (headers.containsKey("simpMessageType")) {
			Object messageType = headers.get("simpMessageType");
			Object sessionId = headers.get("simpSessionId");
			Object destination = headers.get("simpDestination");

			if (messageType.equals(CONNECT)) {
				connectedSessions.add(sessionId);
			} else if (messageType.equals(DISCONNECT)) {
				connectedSessions.remove(sessionId);

				// subscription 중인 세션이 끊겼을 경우를 대비해, 모든 destination의 subscription 목록을 조회해서 목록에서 제거한다.
				for (Object o : subscribedSessions.keySet()) {
					List<Object> subscribed = subscribedSessions.get(o);

					if (subscribed.contains(sessionId)) {
						subscribed.remove(sessionId);
					}
				}
			} else if (messageType.equals(SUBSCRIBE) || messageType.equals(UNSUBSCRIBE)) {
				if (!subscribedSessions.containsKey(destination)) {
					subscribedSessions.put(destination, new ArrayList<>());
				}

				List<Object> subscribed = subscribedSessions.get(destination);

				if (messageType.equals(SUBSCRIBE)) {
					if (subscribed.contains(sessionId)) return;

					subscribed.add(sessionId);
				} else if (messageType.equals(UNSUBSCRIBE)) {
					if (!subscribed.contains(sessionId)) return;

					subscribed.remove(sessionId);
				}
			}
		}
	}

	/**
	 * 현재 접속 중인 session 개수를 반환한다.
	 *
	 * @return connected session count
	 */
	public int getConnectedCount() {
		return connectedSessions.size();
	}

	/**
	 * <p>전체 subcription count를 반환한다.</p>
	 * <p>주의할 점은 subscription count는 session count와 다를 수 있다는 점이다. 하나의 session이 여러 개의 subscription을 subscribe하고
	 * 있을 수도 있다.</p>
	 *
	 * @return all subscription count
	 */
	public int getSubscribedCount() {
		int count = 0;

		for (Object o : subscribedSessions.keySet()) {
			List<Object> subscribed = subscribedSessions.get(o);

			count += subscribed.size();
		}

		return count;
	}

	/**
	 * 특정 destination에 대한 subscription count를 반환한다.
	 *
	 * @param destination specific destination
	 * @return subscription count for specific destination
	 */
	public int getSubscribedCountForDest(String destination) {
		if (!subscribedSessions.containsKey(destination)) return 0;

		return subscribedSessions.get(destination).size();
	}
}
