package io.devcken.configs.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.devcken.support.ProfileEnvironment;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.http.outbound.HttpRequestExecutingMessageHandler;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.messaging.MessageChannel;

import javax.inject.Inject;
import javax.net.ssl.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * mServer의 RESTful API와 통신하기 위해 spring-integration-http 관련 사항을 설정한다.
 *
 * @author Leejun Choi
 */
@Configuration
@EnableIntegration
@PropertySource({ "classpath:http.properties" })
@IntegrationComponentScan("io.devcken.boot")
public class HttpIntegrationConfig {
	private final ProfileEnvironment profileEnvironment;

	@Inject
	public HttpIntegrationConfig(ProfileEnvironment profileEnvironment) {
		this.profileEnvironment = profileEnvironment;
	}

	/**
	 * Message를 전송하기 위한 channel을 등록한다.
	 *
	 * @return {@link DirectChannel}
	 */
	@Bean
	@Qualifier("requestChannel")
	public DirectChannel requestChannel() {
		return new DirectChannel();
	}

	@Bean
	@Qualifier("requestChannelForNoBody")
	public DirectChannel requestChannelForNoBody() {
		return new DirectChannel();
	}

	/**
	 * Message 전송 이후 전달되는 response를 받기 위한 channel을 등록한다.
	 *
	 * @return {@link DirectChannel}
	 */
	@Bean
	@Qualifier("responseChannel")
	public DirectChannel responseChannel() {
		return new DirectChannel();
	}

	/**
	 * {@link #responseChannel()}로 전달되는 HTTP response의 Message가 {@link #jsonTransformer()}로 전달되어 처리되면 전송되는
	 * {@link MessageChannel}을 bean으로 등록한다.
	 *
	 * @return {@link QueueChannel}
	 */
	@Bean
	@Qualifier("replyChannel")
	public QueueChannel replyChannel() {
		return new QueueChannel();
	}

	/**
	 * <p>HTTP request를 위한 {@link org.springframework.messaging.MessageHandler}를 bean으로 등록한다.</p>
	 * <p>RESTful API에 대한 다양한 URI를 제공하기 위해서 uri variable과 http method에 대한 expressoin을 사용한다.</p>
	 *
	 * @param httpClient {@link HttpIntegrationConfig#httpClient(Registry)}
	 * @param responseChannel {@link HttpIntegrationConfig#responseChannel()}
	 * @return {@link HttpRequestExecutingMessageHandler}
	 */
	@Bean
	@ServiceActivator(inputChannel = "requestChannel")
	@Qualifier("messageHandler")
	public HttpRequestExecutingMessageHandler messageHandler(
			HttpClient httpClient,
			@Qualifier("responseChannel") MessageChannel responseChannel) {
		return this.getMessageHandler(httpClient, responseChannel);
	}

	@Bean
	@ServiceActivator(inputChannel = "requestChannelForNoBody")
	@Qualifier("messageHandlerForNoBody")
	public HttpRequestExecutingMessageHandler messageHandlerForNoBody(
			HttpClient httpClient,
			@Qualifier("replyChannel") MessageChannel replyChannel) {
		return this.getMessageHandler(httpClient, replyChannel);
	}

	private HttpRequestExecutingMessageHandler getMessageHandler(
			HttpClient httpClient,
			MessageChannel messageChannel) {
		HttpRequestExecutingMessageHandler messageHandler = new HttpRequestExecutingMessageHandler(profileEnvironment.getProfileProperty("http.url"));

		Map<String, Expression> uriVariables = new HashMap<>();

		SpelExpressionParser expressionParser = new SpelExpressionParser();

		uriVariables.put(profileEnvironment.getProfileProperty("http.hostPathKey"), expressionParser.parseExpression(profileEnvironment.getProfileProperty("http.hostPathKeyExpression")));
		uriVariables.put(profileEnvironment.getProfileProperty("http.portPathKey"), expressionParser.parseExpression(profileEnvironment.getProfileProperty("http.portPathKeyExpression")));
		uriVariables.put(profileEnvironment.getProfileProperty("http.usernamePathKey"), expressionParser.parseExpression(profileEnvironment.getProfileProperty("http.usernamePathKeyExpression")));
		uriVariables.put(profileEnvironment.getProfileProperty("http.passwordPathKey"), expressionParser.parseExpression(profileEnvironment.getProfileProperty("http.passwordPathKeyExpression")));
		uriVariables.put(profileEnvironment.getProfileProperty("http.servicePathKey"), expressionParser.parseExpression(profileEnvironment.getProfileProperty("http.servicePathKeyExpression")));

		messageHandler.setRequestFactory(requestFactory(httpClient));
		messageHandler.setOutputChannel(messageChannel);
		messageHandler.setExpectedResponseType(String.class);
		messageHandler.setCharset("UTF-8");
		messageHandler.setUriVariableExpressions(uriVariables);
		messageHandler.setHttpMethodExpression(expressionParser.parseExpression("headers.get(\"method\")"));

		return messageHandler;
	}

	/**
	 * {@link ClientHttpRequestFactory}를 bean으로 등록한다.
	 *
	 * @param httpClient {@link HttpIntegrationConfig#httpClient(Registry)}
	 * @return {@link ClientHttpRequestFactory}
	 */
//	* @param httpClient {@link HttpIntegrationConfig#httpClient(CredentialsProvider, Registry)}
	@Bean
	public ClientHttpRequestFactory requestFactory(HttpClient httpClient) {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);

		return requestFactory;
	}

	/**
	 * SSL 지원을 위해 {@link SSLConnectionSocketFactory}를 bean으로 등록한다.
	 *
	 * @return {@link ConnectionSocketFactory}
	 * @throws NoSuchAlgorithmException
	 */
	@Bean
	public ConnectionSocketFactory connectionSocketFactory() throws NoSuchAlgorithmException {
		return new SSLConnectionSocketFactory(
				createUnsecureSSLContext(),
				getPassiveX509HostnameVerifier());
	}

	private SSLContext createUnsecureSSLContext() {
		SSLContext sc = null;
		try {
			TrustManager[] trustAllCerts = new TrustManager[]{getPassiveTrustManager()};
			sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
		} catch (Exception e) {
			throw new RuntimeException("error while creating unsecure SSLContext", e);
		}
		return sc;
	}

	private X509TrustManager getPassiveTrustManager() {
		return new X509TrustManager() {

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}

			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}
		};
	}

	private X509HostnameVerifier getPassiveX509HostnameVerifier() {
		return new X509HostnameVerifier() {
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}

			public void verify(String host, SSLSocket ssl)
					throws IOException {
			}

			public void verify(String host, X509Certificate cert)
					throws SSLException {
			}

			public void verify(String host, String[] cns,
			                   String[] subjectAlts) throws SSLException {
			}
		};
	}

	/**
	 * HTTP와 HTTPS 지원을 위한 {@link ConnectionSocketFactory}를 등록한 {@link Registry}를 bean으로 등록한다.
	 *
	 * @param sslConnectionSocketFactory {@link HttpIntegrationConfig#connectionSocketFactory()}
	 * @return {@link Registry<ConnectionSocketFactory>}
	 */
	@Bean
	public Registry<ConnectionSocketFactory> socketFactoryRegistry(ConnectionSocketFactory sslConnectionSocketFactory) {
		return RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", sslConnectionSocketFactory)
				.build();
	}

	/**
	 * Bean으로 등록된 {@link CredentialsProvider}과 {@link Registry}를 이용해 {@link HttpClient}를 생성하여 bean으로 등록한다.
	 *
	 * @param registry {@link HttpIntegrationConfig#socketFactoryRegistry(ConnectionSocketFactory)}
	 * @return {@link HttpClient}
	 * @throws NoSuchAlgorithmException
	 */
	@Bean
	public HttpClient httpClient(Registry<ConnectionSocketFactory> registry) throws NoSuchAlgorithmException {
		return HttpClientBuilder.create()
				.setConnectionManager(new BasicHttpClientConnectionManager(registry))
				.build();
	}

	/**
	 * Reply된 Message를 transform하기 위한 {@link org.springframework.integration.transformer.Transformer}를 bean으로 등록한다.
	 *
	 * @return {@link JsonToObjectTransformer}
	 */
	@Bean
	@Transformer(inputChannel = "responseChannel", outputChannel = "replyChannel")
	public JsonToObjectTransformer jsonTransformer() {
		ObjectMapper mapper = new ObjectMapper();

		mapper.registerModule(new JavaTimeModule());

		return new JsonToObjectTransformer(new Jackson2JsonObjectMapper(mapper));
	}
}
