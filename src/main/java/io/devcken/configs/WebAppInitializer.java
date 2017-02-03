package io.devcken.configs;

import com.zaxxer.hikari.HikariDataSource;
import io.devcken.configs.integration.RedisIntegrationConfig;
import io.devcken.configs.integration.WebSocketIntegrationConfig;
import io.devcken.configs.persistence.Neo4jConfig;
import io.devcken.configs.persistence.TransactionConfig;
import io.devcken.configs.view.ThymeleafConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * {@link Configuration}과 같은 클래스로 annotated된 설정들로
 * {@link org.springframework.web.servlet.DispatcherServlet}를 등록한다.
 * 그리고 서블릿 매핑과 서블릿 필터를 위해 각각 {@link #getServletMappings()}과 {@link #getServletFilters()}을 구현한다.
 *
 * @author Leejun Choi
 */
@Configuration
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	/**
	 * {@inheritDoc}
	 *
	 * @return 등록할 최상위 설정 클래스 배열을 반환한다.
	 */
	@Override
	protected java.lang.Class<?>[] getRootConfigClasses() {
		return new java.lang.Class<?>[] {};
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return 등록할 서블릿 설정 클래스 배열을 반환한다.
	 */
	@Override
	protected java.lang.Class<?>[] getServletConfigClasses() {
		return new java.lang.Class<?>[] {
				SharedConfig.class,
				WebServletConfig.class,
				ThymeleafConfig.class,
				TransactionConfig.class,
				Neo4jConfig.class,
				WebSocketIntegrationConfig.class,
				RedisIntegrationConfig.class
		};
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return 등록할 서블릿 매핑 문자열 배열을 반환한다.
	 */
	@Override
	protected java.lang.String[] getServletMappings() {
		return new java.lang.String[] {
				"/",
				"/**"
		};
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return 등록할 서블릿 필터 클래스 배열을 반환한다.
	 */
	@Override
	protected Filter[] getServletFilters() {
		return new Filter[] {
				encodingFilter()
		};
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);

		// 세션 관련 리스너를 등록하여, 생성 또는 파괴 시 이벤트를 실행한다.
		servletContext.addListener(new HttpSessionListener() {
			@Override
			public void sessionCreated(HttpSessionEvent se) {

			}

			@Override
			public void sessionDestroyed(HttpSessionEvent se) {

			}
		});

		// 서블릿 컨텍스트 리스너를 등록하여 서블릿 초기화 혹은 폐기 시 이벤트를 실행한다.
		servletContext.addListener(new ServletContextListener() {
			@Override
			public void contextInitialized(ServletContextEvent sce) {

			}

			@Override
			public void contextDestroyed(ServletContextEvent sce) {
				// 아래 두 줄은 JDNI 사용 시 DataSource가 제대로 해재되지 않는 현상을 보완하기 위한 코드다.
				// 즉, 컨텍스트 파괴 시 남아있는 DataSource를 찾아 close() 메서드를 실행한다.
				//WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());

				//wac.getBeansOfType(HikariDataSource.class).values().forEach(HikariDataSource::close);
			}
		});
	}

	/**
	 * 서블릿에 등록할 {@link CharacterEncodingFilter}를 생성한다.
	 *
	 * @return {@link CharacterEncodingFilter}
	 */
	private CharacterEncodingFilter encodingFilter() {
		CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();

		encodingFilter.setBeanName("encodingFilter");
		encodingFilter.setEncoding("UTF-8"); // 서블릿에 사용할 인코딩은 UTF-8이다.
		encodingFilter.setForceEncoding(true);

		return encodingFilter;
	}
}
