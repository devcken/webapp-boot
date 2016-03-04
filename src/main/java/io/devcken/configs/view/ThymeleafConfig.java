package io.devcken.configs.view;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@Configuration
@PropertySource("classpath:thymeleaf.properties")
public class ThymeleafConfig {
	@Inject
	Environment environment;

	/**
	 * {@link TemplateResolver}를 bean으로 등록한다.
	 * <p>View template을 classpath 내에서 검색하기 위해 {@link ClassLoaderTemplateResolver}의
	 * 인스턴스를 사용한다.</p>
	 *
	 * @return {@link TemplateResolver}
	 */
	@Bean
	public TemplateResolver templateResolver() {
		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

		templateResolver.setPrefix(environment.getProperty("thymeleaf.prefix"));
		templateResolver.setSuffix(environment.getProperty("thymeleaf.suffix"));
		// Thymeleaf 3.0부터는 TemplateMode라는 enum을 사용해야 한다.
		// templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setTemplateMode(environment.getProperty("thymeleaf.templateMode"));
		templateResolver.setCacheable(Boolean.parseBoolean(environment.getProperty("thymeleaf.cacheable")));
		templateResolver.setCharacterEncoding(environment.getProperty("thymeleaf.encoding"));

		return templateResolver;
	}

	/**
	 * {@link SpringTemplateEngine}을 bean으로 등록한다.
	 * Spring WebMVC와 연계하기 위해 {@link SpringTemplateEngine}을 구현한다.
	 * <p>{@link SpringTemplateEngine}이 사용할 dialect로 {@link LayoutDialect}를
	 * 추가하는데 이는 view의 layout을 위해서다.</p>
	 *
	 * @return {@link SpringTemplateEngine}
	 */
	@Bean
	public SpringTemplateEngine templateEngine(TemplateResolver templateResolver, MessageSource messageSource) {
		Set<IDialect> dialects = new HashSet<>();

		dialects.add(new LayoutDialect());

		SpringTemplateEngine templateEngine = new SpringTemplateEngine();

		templateEngine.setMessageSource(messageSource);
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.setAdditionalDialects(dialects);

		return templateEngine;
	}

	/**
	 * {@link ThymeleafViewResolver}를 bean으로 등록한다.
	 *
	 * @return {@link ThymeleafViewResolver}
	 */
	@Bean
	public ThymeleafViewResolver thymeleafViewResolver(SpringTemplateEngine templateEngine) {
		ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();

		thymeleafViewResolver.setTemplateEngine(templateEngine);
		thymeleafViewResolver.setCharacterEncoding("UTF-8");
		thymeleafViewResolver.setOrder(1);

		return thymeleafViewResolver;
	}
}
