package io.devcken.support;

import org.springframework.http.HttpMethod;
import org.springframework.integration.mapping.support.JsonHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

/**
 * HTTP/HTTPS를 통한 RESTful API와의 통신 시 {@link Message} 객체를 만들기 위한 {@link MessageBuilder}의 wrapper를 구현한다.
 *
 * @author Leejun Choi
 */
public class RestfulMessageBuilder {
	public static <T> Message<T> build(T payload, String host, int port, String username, String password, String service, Class jsonType, Class jsonContentType) {
		return build(payload, host, port, username, password, service, jsonType, jsonContentType, HttpMethod.GET);
	}

	public static Message build(String host, int port, String username, String password, String service, Class jsonType, Class jsonContentType, HttpMethod httpMethod) {
		return build("", host, port, username, password, service, jsonType, jsonContentType, httpMethod);
	}

	/**
	 * URI path와 HTTP method를 {@link Message}의 header에 설정한 {@link Message} instance를 만든다.
	 *
	 * @param payload 전송할 객체
	 * @param host 대상 호스트
	 * @param port 호스트에 대한 포트
	 * @param service 요청할 API의 서비스 이름(URI 전체에서 스키마, 호스트, 포트를 제외한 나머지 부분)
	 * @param httpMethod 전송시 사용할 {@link HttpMethod}
	 * @return {@link Message}
	 */
	public static <T> Message<T> build(T payload, String host, int port, String username, String password, String service, Class jsonType, Class jsonContentType, HttpMethod httpMethod) {
		return MessageBuilder.withPayload(payload)
				.setHeader("Content-Type", "application/json; charset=utf-8")
				.setHeader("host", host)
				.setHeader("port", port)
				.setHeader("username", username)
				.setHeader("password", password)
				.setHeader("service", service)
				.setHeader("method", httpMethod)
				.setHeader(JsonHeaders.TYPE_ID, jsonType)
				.setHeader(JsonHeaders.CONTENT_TYPE_ID, jsonContentType)
				.build();
	}
}
