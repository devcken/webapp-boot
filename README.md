# WebApp Boot

Java 기반의 웹 애플리케이션을 위한 boilerplate.

WebApp Boot(이하 Boot)에서 사용된 라이브러리들은 기본적인 문제가 없도록 테스트되었으나 어떠한 문제도 없다고 보장하지는 못한다. 보장할 수 있는 것은 Boot를
이용한 다른 프로젝트에서 발견된 문제들을 통해 계속해서 개선해 나갈 것이라는 것이다.

이 프로젝트는 템플릿을 통해 빠르게 (실제)프로젝트를 시작하는 데에도 그 의의가 있으나, 무엇보다도 프로젝트를 구성하게 될 의존성들간의 충돌, 버전에 따른 부작용
등을 계속해서 개선해내가는 데에 주요 목표가 있다. 또한, 프로젝트 진행 시에 필요로 할 수 있는 것들(예를 들어, 전역 예외 핸들러 같은)을 미리 구현해두어 프로젝트
진입 장벽을 낮추는데에도 도움을 줄 수 있다.

## Gradle

Boot에서는 dependency maangement와 build를 위해 **Gradle**을 사용한다. 빌드 설정은 *build.gradle*에 작성한다.

### Dependencies Version

의존성 버전을 효율적으로 관리하기 위해서 의존성 버전을 위한 변수를 선언해두었다. 전체 프로젝트와 서브 프로젝트 모두로부터 접근이 가능하도록 *ext* 내에 선언해두었다.

```
ext {
	{의존성 대명사}Version: "{의존성 버전}"
	...
}
```

위와 같이 *{의존성 대명사}Version*를 키로 하여 의존성 버전을 변수화시켰다.

### Plugins

적용된 Gradle plugin들은 다음과 같다.

- [java](https://docs.gradle.org/current/userguide/java_plugin.html)
- [war](https://docs.gradle.org/current/userguide/war_plugin.html)
- [groovy](https://docs.gradle.org/current/userguide/groovy_plugin.html)
- [idea](https://docs.gradle.org/current/userguide/idea_plugin.html)

## Spring Framework

현재 버전: *4.2.5.RELEASE*

서블릿 컨테이너 구성을 위해 Spring Framework를 사용한다.

### WebAppInitializer

`extends AbstractAnnotationConfigDispatcherServletInitializer`

*web.xml*을 대신하여 root servlet, application servlet 그리고 필요한 filter를 등록할 수 있다. `WebAppInitializer#encodingFilter()`를
통해 `org.springframework.web.filter.CharacterEncodingFilter`를 필터로 등록한다.

> Boot는 web.xml이나 servlet-config.xml과 같은 XML 타입의 파일을 설정으로 사용하지 않는다. 모든 설정은 java config를 기반으로 이루어지도록 구성하였다.

### WebServletConfig

'extends org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter'

application servlet을 위한 설정 클래스로 다음과 같은 메서드를 오버라이드한다.

* `WebMvcConfigurerAdapter#getValidator()`
* `WebMvcConfigurerAdapter#addResourceHandlers(ResourceHandlerRegistry registry)`

#### Validator

Boot는 validator를 위해 다음과 같은 Spring bean들을 context에 등록한다.

* `org.springframework.context.support.ReloadableResourceBundleMessageSource`
* `org.springframework.validation.beanvalidation.LocalValidatorFactoryBean`

`LocalValidatorFactoryBean`는 validator를 등록하기 위해서 사용되며, `ResourceMessageInterpolator`는 validator에 message resource를
공급하기 위해서 사용되는데 WAS 구동 시에도 message resource의 변경을 감지하여 *reload* 하도록 구현되었다.

> Validation을 위해 모든 message resource를 제공할 필요는 없다. Hibernate Validation은 제공되는 라이브러리 내에 기본적인 message resource를
제공하고 있으므로 문제될 것이 없다. 다만, customizing validation annotation에 대해서는 message resource를 새로 작성해야 한다.

Message resource는 `src/main/resources/messages` 경로 내에 `validation.messages`라는 prefix를 갖도록 구성되어 있다. 만약, prefix를
바꾸고자 한다면 `io.devcken.configs.WebServletConfig#messageSource()`에서 `ReloadableResourceBundleMessageSource.setBaseNames()`의
parameter를 변경하면 된다.

> Validation annotation에서 defaultMessage를 curly braces('{'와 '}')을 앞뒤로 감싸고 있는데 이는 내부적인 처리를 위해 사용할 뿐이다. properties
파일 내에 설정되는 message resource의 이름을 curly braces로 감싸서는 안된다.

#### Resource Handlers

Boot에서는 WEB-INF와 같은 [Servlet 스펙](http://download.oracle.com/otn-pub/jcp/servlet-2.4-fr-spec-oth-JSpec/servlet-2_4-fr-spec.pdf?AuthParam=1457030710_d57062dc0e8240973d68680268e3c9da)을
따르지 않고 있다. 모든 resource들을 `src/main/resources` 경로에 두고 있고 그 덕분에 resource 또한 classpath를 통해 접근해야 한다.

이럴 경우 내부적으로 접근해야 하는 resource들은 별 문제가 되지 않지만(실행 중인 context가 classpath에 접근이 가능하므로) 사용자(가 사용하는 브라우저)가
직접 URI를 통해 접근해야 하는 image, style sheet, script 등은 classpath에 접근할 수 있는 방법이 필요하다.

그래서 `org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter`는 `addResourceHandlers()`라는 메서드를 제공한다.
`WebServletConfig`는 이 메서드를 overriding하여 외부에 공개되어야 하는 resource들의 경로를 만들어준다.

```
registry.addResourceHandler("/scripts/**").addResourceLocations("classpath:scripts/");
```

script에 대한 경로를 외부로 열어주기 위해서는 위와 같이 설정한다. 여기서 `/scripts/**`는 외부로 공개되었을 때의 contextPath 이하의 경로를 나타내고
`classpath:script/`는 classpath 내의 경로를 뜻한다.

## JPA && QueryDSL

persistence API의 현재 버전: *1.0.2*
Spring Data JPA의 현재 버전: *1.10.0.M1*
QueryDSL의 현재 버전: *4.0.9*

Relational database에 대한 persistence와 query를 위해 JPA와 QueryDSL을 사용한다. Boot는 예제 등을 위해 *MySQL*을 사용하고 있으며 다른 database에도
충분히 대응할 수 있는 구조로 되어 있다.

### QueryDslConfig

#### Data source

Database에 접속하기 위해서는 javax.sql.DataSource의 구현이 필요한데, Boot는 [HikariCP](https://github.com/brettwooldridge/HikariCP)를 사용한다.

HikariCP의 현재 버전: *2.4.3*

`QueryDslConfig#dataSource()`를 통해 Spring bean으로 등록되며 database에 접속하기 위한 모든 정보는 `classpath:datasource.properties`로부터
가져오도록 되어 있다.

#### Jpa vendor

*Hibernate*을 JPA vendor로 사용한다.

MySQL이 아닌 다른 database를 사용해야 할 경우, 다음 설정을 사용하려는 database에 맞게 수정해야 한다

```
jpaVendorAdapter.setDatabase(Database.MYSQL);
jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
```

#### Entity manager

Data source를 이용해 database와 연결하고 entity들을 이용해 query하는 등의 역할을 하는 `EntityManager`를 만들기 위해
`org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean`을 bean으로 등록한다.

#### Query Factory

QueryDSL에는 크게 JPA로 query하는 방법과 SQL로 query하는 방법이 있는데 Boot에서는 SQL로 query하는 방법을 사용한다. 그래서 `com.querydsl.sql.SQLQueryFactory`를
bean으로 등록한다. `SQLQueryFactory`를 등록할 때에도 database에 따라 설정되는 template이 달라지므로 database 변경 시에 유의해야 한다.

```
com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(new MySQLTemplates());
```

> `com.querydsl.sql.SQLQueryFactory`을 사용해 쿼리하려면 entity class를 `com.querydsl.sql.RelationalPathBase` class로 확장한 class가 필요하다.
이런 class들은 database의 schema로부터 가져와 생성해야 하는데, 이를 위해 Gradle task를 제공하고 있으므로 [Generating query types for QueryDSL](#Generating query types for QueryDSL)을
참고하기 바란다.

### Transaction config

#### Transaction manager

JPA에서 transaction을 사용하려면 `org.springframework.orm.jpa.JpaTransactionManager`을 bean으로 등록해야 한다.

#### QueryDsl config와의 분리

Transaction 설정을 database source나 entity manager 등의 설정과 분리했는데 이는 transaction이 *thread-safe*한 환경에서만 실행 가능하기 때문이다.
사실 이것은 Spring Security를 적용하기 위함인데 Spring Security는 별도의 context에 설정이 가능하므로 QueryDsl 등의 persistence 설정을
servlet context와 공유할 수 있는 구조로 구성하는 것이 좋다. 하지만 그럴 경우 transaction이 thread-safe하지 못한 환경에 놓이게 되므로 사용이 불가하다.

이를 위해 분리한 것이며 QueryDsl의 설정은 Spring Security의 context 혹은 root context에 등록하고, transaction config는 각각의 context 별로
따로 등록하도록 했다.

### Generating query types for QueryDSL

QueryDSL 사용을 위해서는 QueryDSL을 위한 query type이 필요한데 이 때 사용하는 것이 `com.querydsl.sql.codegen.MetaDataExporter`이며 Boot에서는
이를 이용해 query type 생성을 위한 task를 마련하고 있다.

#### Gradle task `generateQueryDSL`

`com.querydsl.sql.codegen.MetaDataExporter`을 이용해 database 상의 schema를 query type으로 가져오는 gradle task다.

```
$ gradle generateQueryDSL
```

위와 같이 실행하고 정상적으로 완료되면,

```
:generateQueryDSL UP-TO-DATE

BUILD SUCCESSFUL
```

query type이 생성된다. `src/main/querydsl/io/devcken/boot/querydsl`의 경로에 생성되며 table 이름 앞에 영대문자 'Q'가 prefix된 이름으로 생성된다.

> MetaDataExporter가 query type을 만들 때, schema_version이라는 table을 사용하는데 공교롭게도 이 table에 대한 query type도 함께 만들어진다.
이 query type이 생성되지 않게 하는 방법은 아직 찾지 못했다.

### Examples for JPA and QueryDSL

JPA와 QueryDSL을 위한 예제는 `io.devcken.boot.employee`에 있다.

## Neo4J

Spring data Neo4J의 현재 버전: *4.0.0.RELEASE*

Boot는 [Neo4J](http://neo4j.com) 지원을 위해 Spring data Neo4J를 사용한다.

### Neo4J config

`Neo4jConfig`는 접속 정보를 위해 `classpath:neo4j.properties`를 참조하며 `org.springframework.data.neo4j.config.Neo4jConfiguration`을
확장하여 구현한다.

#### Neo4jServer

Neo4J 서버 정보는 `org.springframework.data.neo4j.server.Neo4jServer`의 bean으로 등록되어야 한다.

#### Session

Neo4J에 대한 접속은 session으로 이루어지는데 이를 위해 `org.neo4j.ogm.session.SessionFactory`이 필요하다. 이는 bean으로 등록되며
`Neo4jConfig#getSession()` method는 `org.springframework.data.neo4j.config.Neo4jConfiguration#getSession()` method를
overriding하여 필요할 때마다 `SessionFactory`로부터 session을 발급받게 된다.

### examples for Neo4J

Neo4J를 위한 예제는 `io.devcken.boot.student`를 참고하기 바란다.

> Neo4J를 위한 repository는 `org.springframework.data.neo4j.repository.GraphRepository<T>` interface를 확장하여 만든다.
이렇게 만들어진 repository를 service layer에 주입할 때에는 그냥 `@Autowired` annotation을 적용하게 되면 제대로 주입을 받지 못하게 된다.
실행 시점에 주입을 받게 되므로 반드시 `@Lazy` annotation을 함께 적용하도록 하자.

## Spring Security

현재 버전: *4.0.4.RELEASE*

### Security config

Spring Security를 설정하기 위해 `SecurityConfig`를 구현해야 한다. 해당 configuration class는 `@EnableWebSecurity` annotation을
설정해야 한다. `@EnableGlobalMethodSecurity`를 설정한 이유는 method level의 보안을 적용하기 위함이다. Method security에 대한 내용은
[Spring Security Java Config Preview: Method Security](https://spring.io/blog/2013/07/04/spring-security-java-config-preview-method-security/)를
참고하기 바란다.

#### WebSecurityConfigurerAdapter

`SecurityConfig` class 내에 보면 `WebSecurityConfigurerAdapter` class를 확장한 `ServiceSecurityConfig` class가 있다. Security를 위한
설정은 모두 이 class의 overriding method를 이용하게 된다.

다음과 같이 `org.springframework.security.authentication.AuthenticationManager`을 반드시 bean으로 등록해야 한다.

```
@Bean
public AuthenticationManager authenticationManagerBean() throws Exception {
	return super.authenticationManagerBean();
}
```

그리고 다음에 살펴볼 두 개의 method를 overriding하여 구현해야 한다.

##### `configure(HttpSecurity http)`

이 method는 `org.springframework.security.config.annotation.web.builders.HttpSecurity`의 instance를 parameter로 받게 되는데
해당 instance를 이용해 security에 필요한 정책 및 설정을 구성할 수 있다.

어떤 정책과 설정이 존재하는지는 [Spring Security, 3. Java Configuration#HttpSecurity](http://docs.spring.io/spring-security/site/docs/current/reference/html/jc.html#jc-httpsecurity)를
참고하기 바란다.

##### `configure(AuthenticationManagerBuilder auth)`

`configure(HttpSecurity http)` method로 정책과 설정을 구성했다면 `configure(AuthenticationManagerBuilder auth)`를 통해서는 어떤 방법으로
인증을 시도하고 처리할 것인지 정할 수 있다.

[`AuthenticationManagerBuilder`](http://docs.spring.io/autorepo/docs/spring-security/4.0.4.RELEASE/apidocs/org/springframework/security/config/annotation/authentication/builders/AuthenticationManagerBuilder.html) class는 다양한 방법의 인증 방법을 제공한다.

### `configureWeb(WebSecurity web)`

`org.springframework.security.config.annotation.web.builders.WebSecurity` class는 web 보안을 위한 filtering을 제공한다.
그래서 Boot에서는 이를 이용해 static resource들에 대해 security 정책 및 설정이 적용되지 않도록 하는데 사용하고 있다.

```
public static void configureWeb(WebSecurity web) {
	web.ignoring().
			antMatchers("/styles/**").
			antMatchers("/images/**").
			antMatchers("/scripts/**");
}
```

### PasswordEncoder

인증 시 사용자가 제공한 password를 encoding하기 위한 *encoder*가 필요하므로 bean으로 등록해 사용해야 한다.
Boot는 [BCrypt](https://en.wikipedia.org/wiki/Bcrypt)를 이용해 password를 encoding한다.

> 당연한 얘기지만, 인증에서 사용되는 *password encoder*를 사용자의 password를 database(혹은 그 외에 사용자 정보를 저장할 수 있는 무엇이든)에
저장할 경우에도 사용해야 한다.

### Handling authentication result

인증의 결과는 성공 혹은 실패인데 Spring Security는 이를 위한 처리자, `org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler`와
`org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler`를 제공한다. Boot는 이 두 handler를
확장, overriding하여 처리 결과를 RESTful하게 전달할 수 있는 class를 제공한다.

이 두 class의 overrding 메서드를 서비스에 맞게 구현하도록 한다.

#### RestAuthenticationSuccessHandler

인증 성공을 RESTful하게 처리하기 위한 handler로 `onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)` method와
`handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)` method를 overriding한다.

#### RestAuthenticationFailureHandler

인증 실패한 경우 결과를 RESTful하게 처리하기 위한 handler로 `onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)`
method를 overriding한다.

### SecurityWebAppInitializer

Spring Security를 적용하려면 security를 위한 context를 만들어야 하는데 이를 위해 `org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer`
class가 제공된다. Boot는 이 class를 확장한 `SecurityWebAppInitializer` class를 제공한다. 다음과 같이 `SecurityWebAppInitializer` class의 생성자에서
`SecurityConfig` class를 등록해야 한다.

```
public class SecurityWebAppInitializer extends AbstractSecurityWebApplicationInitializer {
	public SecurityWebAppInitializer() {
		super(SecurityConfig.class, QueryDslConfig.class);
	}
}
```

> `AbstractSecurityWebApplicationInitializer` class의 확장 class가 제공되면 security context가 root context로써 동작한다.
그러므로 `QueryDslConfig` class 또한 security context 영역에 등록해줘야 한다.

### Spring Security example

`SecurityConfig#ServiceSecurityConfig` class의 `configure(HttpSecurity http)` method에서는 몇 가지 정책이 구성되어 있는데
예제를 위해서(물론 운영에서도 사용 가능하다.) 다음과 같은 설정을 기본 제공하고 있다.

```
	...
		loginProcessingUrl("/id").
		usernameParameter("username").
		passwordParameter("password").
	...
```

`loginProcessingUrl()` method를 통해서 인증 URL을 정할 수 있다. 예를 들면, `http://localhost:8080/id` URL을 요청하면 인증이 시도된다.
`usernameParameter()`와 `passwordParameter()`는 각각 사용자의 인증 이름과 암호를 전달받기 위한 parameter의 이름을 설정할 수 있는 method다.

그러므로 Boot에서 제공 중인 설정으로는 인증을 테스트하려면,

```
[POST]
http://localhost:8080/id?username={username}&passsword={password}
```

와 같이 요청하면 된다. 인증이 성공하면 HTTP Code로 `200 OK`가 전달되어야 하며, 실패할 경우 '401 Unauthorized'가 반환되어야 한다.

## Spring Integration

현재 버전: *4.2.5.RELEASE*

다른 시스템과 커뮤니케이션이 필요한 경우가 종종 있는데, 시스템마다 전송 방식이 다르다. 그렇기 때문에 전송 방식에 따라 구현이 달라지며, 어떤 domain data를
다루는지에 따라서도 구현이 달라진다. 더구나 strong coupling으로 인해 business layer와의 구분이 어려워지게 된다.

이러한 한계를 극복하기 위해서 구현된 것이 바로 **Spring Integration**이다. 물론, transporting과 messaging을 위한 [*Apache Camel*](http://camel.apache.org)이라는
좋은 라이브러리가 있지만, Spring Framework을 container로 사용하므로 *Spring Integration*을 사용하기로 했다.

### Spring Integration WebSocket

*spring-integration-websocket* module은 전송 방식으로 [**WebSocket**](https://www.w3.org/TR/2011/WD-websockets-20110419/)을
지원하는 Spring Integration의 module이다.

예제는 [http://localhsot:8080/websocket](http://localhsot:8080/websocket)을 통해 확인할 수 있다.

#### WebSocketConfig

`org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer`을 확장하여 message broker와
STOMP endpoint를 등록한다.

`org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker` annotation을 적용하여 상위 레벨의 서브 프로토콜을 이용해
message broker가 지원되는 messaging을 지원하게 된다.

##### `MessageBrokerRegistry#enableSimpleBroker(...)`
broker의 대상 목적지에 대한 prefix를 설정한다. 예제에서 *send*이라는 이름으로 message broker의 prefix를 설정했는데, 이것은 쉽게 말해 server-side에서
client-side로 전송할 때 사용하는 경로의 prefix를 말한다.(편의 상, server를 주체로 용어를 결정하였다.)

##### `MessageBrokerRegistry#setApplicationDestinationPrefixes(...)`
Websocket을 이용한 application의 prefix를 설정한다. 예제에서는 *receive*이라는 이름으로 application prefix를 설정했는데, 이것은 쉽게 말해 client-side에서
server-side로 전송할 때 사용하는 경로의 prefix를 말한다.

##### `StompEndpointRegistry#addEndpoint(...)#withSockJS()`
STOMP을 통해 통신하는 endpoint를 등록하게 된다. 그리고 `withSockJS` 메서드로 SockJS에 대한 fallback을 허용하게 된다. 예제에서는 *withws*라는 이름의 endpoint를
등록했으므로 client-side에서 접속할 때 사용하게 되는 URL은 `http://localhost:8080/withws`가 된다.

예제 파일 중 websocket.html을 보게 되면,

```
	function connect(e) {
		var socket = new SockJS('/withws');

		stomp = Stomp.over(socket);

		stomp.connect({}, connected);
	}
```

와 같이 `/withws`라는 주소를 이용하고 있다.

#### WebSocketController

*spring-integration-websocket* module을 테스트하기 위한 controller로, 요청 관련 매핑이나 messaging을 위한 매핑 정보, 그리고 그에 대한 처리를
담당하고 있다. 앞서 살펴보았던 WebSocketConfig는 설정을 담당한다면, WebSocketController는 우리가 실제 구현해야 할 MVC pattern 중 controller에
해당한다.

##### @MessageMapping(...)
Client-side로부터 들어오는 요청을 받는 경로와 해당 요청을 처리하는 method를 mapping하는 역할을 한다. Parameter로 설정되는 string은 `WebSocketConfig`에서
설정한 `MessageBrokerRegistry#setApplicationDestinationPrefixes(...)`의 prefix와 결합되어 하나의 경로를 만든다.

예제에서 `WebSocketController#sum()` method는 *calc*라는 mapping 정보를 제공했으므로 `/receive/calc`라는 주소와 mapping된다.
그러므로 client-side에서는,

```
	stomp.send("/receive/calc", {},
		... // paramters
	);
```

와 같이, 전송하게 된다.

##### @SendTo(...)
`@MessageMapping`과 마찬가지로 `WebSocketController#sum()`에 적용된 annotation으로 websocket을 통해 수신된 요청에 대한 응답을 어떤 경로로
할지를 결정하게 된다.

예제에서는 `/send/result`를 경로를 지정했다. 이 경로는 `MessageBrokerRegistry#enableSimpleBroker("send")`을 통해 결정된다. 즉, 경로는 prefix와
나머지 경로로 이루어진다.

##### messaging template과 PUB-SUB
`WebSocketController`에는 `SimpMessagingTemplate`이라는 클래스의 인스턴스가 injection되는데, `AbstractMessageSendingTemplate<String>`의 확장이다.
예제에서는 client-side로 message를 전송하기 위해 사용되는데, 이 클래스의 인스턴스는 `AbstractWebSocketMessageBrokerConfigurer`을 확장함으로써
injection이 가능하다. 하지만, Java configuration을 이용해 설정될 때에는 반드시 `@Lazy` annotation을 함께 지정해주어야 한다.
`AbstractWebSocketMessageBrokerConfigurer`을 확장하게 되면 message context가 생기게 되는데, 이 message context가 나중에 생성되므로
servlet context 생성 시점에 injection이 불가능하다. 그러므로 실제 사용 시점에 이를 injection 받아야한다.

이 `SimpMessagingTemplate`의 인스턴스는 `WebSocketController#scheduledForSub()`라는 method 내에서 사용된다.

```
	...
	messagingTemplate.convertAndSend("/send/subscribable", result);
	...
```

위와 같이 `/send/subscriable` 이라는 경로로 message를 전달한다.
이를 client-side에서 전달받기 위해서 다음과 같은 구문을 실행하게 된다.

```
	...

	stomp.subscribe('/send/subscribable', function(result) {
		...
	});

	...

```

이렇게 하여 client는 server-side의 message를 subscribe하게 된다.

## Thymeleaf

현재 버전: *2.1.4.RELEASE*

HTML을 위한 view resolving 과 layout 구성에 사용된다.

### ThymeleafConfig

#### ClassLoaderTemplateResolver

Classpath를 통해 *Thymeleaf*의 template을 resolving하기 위해 `org.thymeleaf.templateresolver.ClassLoaderTemplateResolver`가
bean으로 등록된다. `ClassLoaderTemplateResolver`의 설정은 `src/main/resources/thymeleaf.properties` 내에 구성했다.

Boot 내에서는 template의 경로를 `src/main/resources/templates`로 하였다.

#### SpringTemplateEngine

`TemplateResolver`과 `MessageSource`를 통해 template을 처리할 template engine이다. Bean으로 등록된 `ClassLoaderTemplateResolver`가
주입되며 `WebServletConfig#messageSource()`를 통해 bean으로 등록된 `ReloadableResourceBundleMessageSource`의 bean이 주입된다.

> View resolving 시에 message resource를 이용해 static한 text나 localization을 처리할 수 있다. Boot에서는 `ReloadableResourceBundleMessageSource`를
bean으로 등록할 때, message resource의 경로 중 하나로 `classpath:messages/messages`를 추가했는데 이 경로가 바로 view resolving 시에 사용될
message resource의 경로가 된다.

#### ThymeleafViewResolver

`SpringTemplateEngine` bean을 이용해 view resolving을 담당한다.

## Logger

slf4j의 현재 버전: *1.7.18*
logback의 현재 버전: *1.1.6*

Boot는 logger interface로 [slf4j](http://www.slf4j.org)을 사용하고 implement로 [logback](http://logback.qos.ch)을 사용한다.

### logback 설정

logback은 설정을 위해 여러 가지 방법을 제공하는데, Boot는 그 중 *groovy script*를 사용하기로 했다. classpath의 root에 존재해야 하므로
`src/main/resources/logback.groovy`가 설정 파일의 위치가 된다.

> `logback.groovy` 내에 `scan(1000)`이라는 설정을 해두었는데 이는 logback 설정 파일의 변경을 감지하기 위한 script다. 개발 중에는 이 script가 문제될
것이 없으나 production level에서는 성능에 영향을 줄 수 있으므로 수치를 변경하거나 script를 제거해야 한다.

Custom한 log message을 사용하기 위해 `PatternLayoutEncoder`으로 message 형태를 재구성했다.

### slf4j 사용법

다음은 `io.devcken.boot.RootController`에 적용되어 있는 logger interface다.

```
private static Logger logger = LoggerFactory.getLogger(RootController.class);

...() {
	logger.debug(...);
	logger.info(...);
	logger.warn(...);
	logger.error(...);
	...
}

```

## Test

Spock 현재 버전: *1.0-groovy-2.4*

Boot는 test framework로 [Spock Framework](https://code.google.com/archive/p/spock/)를 사용한다.
Spock은 여러 가지 언어를 대응하지만, Boot에서는 *groovy*를 사용하기로 했다.

또, Spring framework를 위해 *spring test*를 함께 사용한다. 즉, Spring 기반으로 작성된 내역들은 spring test를 사용해야 한다.