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