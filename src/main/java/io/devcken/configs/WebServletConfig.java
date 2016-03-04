package io.devcken.configs;

import io.devcken.configs.view.ThymeleafConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@EnableAsync
@ComponentScan(
		basePackages = { "io.devcken.boot", "io.devcken.exception" }
)
@Import({ ThymeleafConfig.class })
public class WebServletConfig extends WebMvcConfigurerAdapter {
	@Autowired
	ApplicationContext context;

	/**
	 * {@link #getValidator()}를 통해 설정되는 {@link Validator}가 사용하게 될
	 * {@link ReloadableResourceBundleMessageSource}를 생성한다.
	 *
	 * <p>이 메시지 소스는 서버 운영 중에 reload가 가능하며, i18n을 지원하여 메시지를 지역화할 수 있다.
	 *
	 * @return {@link ReloadableResourceBundleMessageSource}
	 */
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

		messageSource.setBasenames(
				"classpath:messages/messages",                // view resolving 메시지
				"classpath:messages/validation.messages"      // 유효성 검증 관련 메시지
		);
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds(1);

		return messageSource;
	}

	/**
	 * {@link LocalValidatorFactoryBean} bean을 생성한다.
	 *
	 * @return {@link LocalValidatorFactoryBean}
	 */
	@Bean
	public LocalValidatorFactoryBean validatorFactoryBean() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();

		localValidatorFactoryBean.setValidationMessageSource(messageSource());

		return localValidatorFactoryBean;
	}

	/**
	 * {@inheritDoc}
	 *
	 * {@link ReloadableResourceBundleMessageSource}을 사용하기 위해 {@link LocalValidatorFactoryBean}를 반환한다.
	 * null을 반환할 경우 default {@link Validator}가 사용된다.
	 *
	 * @return
	 */
	@Override
	public Validator getValidator() {
		return validatorFactoryBean();
	}

	/**
	 * {@inheritDoc}
	 *
	 * 리소스 파일의 실제 경로는 classpath를 root로 한다.
	 * Project structure 상에서는 src/resources의 하위 경로들이 된다.
	 * 새로운 리소스 유형이 추가될 경우, 경로를 만들고 여기에 추가해줘야 Spring Context가 인식할 수 있다.
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/**").addResourceLocations("classpath:images/");
		registry.addResourceHandler("/styles/**").addResourceLocations("classpath:styles/");
		registry.addResourceHandler("/scripts/**").addResourceLocations("classpath:scripts/");

		super.addResourceHandlers(registry);
	}
}
