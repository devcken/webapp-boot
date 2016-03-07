package io.devcken.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

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
				WebServletConfig.class
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
