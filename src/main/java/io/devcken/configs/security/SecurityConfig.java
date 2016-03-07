package io.devcken.configs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan("io.devcken.boot")
public class SecurityConfig {
	/**
	 * {@link WebSecurityConfigurerAdapter}를 확장하여 Spring Security에 적용할 설정들을 구성한다.
	 * <p>만약, URL에 따라 보안 설정을 다르게 해야 할 경우 이와 같은 클래스({@link WebSecurityConfigurerAdapter}를 확장한)를
	 * 구현하여 설정을 달리 적용하면 된다. 인증 성공/실패에 대한 handler까지 달리해야 한다면 구현하게 될 클래스의 inner 클래스로
	 * handler를 구현해야 한다.</p>
	 *
	 * @author Leejun Choi
	 */
	@Configuration
	static class ServiceSecurityConfig extends WebSecurityConfigurerAdapter {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void configure(WebSecurity web) throws Exception {
			configureWeb(web);
		}

		/**
		 * {@inheritDoc}
		 *
		 * <p>보안과 관련된 세부 설정을 여기에 하면 된다.</p>
		 */
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/**").
					csrf().
					disable().
					exceptionHandling().
					and().
					formLogin().
					loginPage("/signin").
					loginProcessingUrl("/id").
					usernameParameter("username").
					passwordParameter("password").
					successHandler(authenticationSuccessHandler()).
					failureHandler(authenticationFailureHandler()).
					permitAll().
					and().
					logout().
					logoutUrl("/signout").
					logoutSuccessUrl("/").
					invalidateHttpSession(true).
					and().
					authorizeRequests().
					antMatchers("/**").
					permitAll();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			// 인증 처리와 관련하여 @Service 클래스, JDBC 혹은 LDAP 등을 이용할 수 있다.
			// 사용자 정보와 관련하여 어떤 형태로 관리하고 있는지에 따라 다르게 처리해야 한다.
			// 예를 들어, RDBMS에 사용자의 정보를 저장하고 있고 그와 관련된 @Service 기반 클래스가 있다면
			// 다음과 같은 처리가 가능하다.
			// auth.userDetailsService(...).passwordEncoder(...);
			// 만약 RDBMS에 사용자의 정보를 저장하고 있지만 관련된 @Service 기반 클래스가 없다면 JDBC를 이용해야 한다.
			// auth.jdbcAuthentication().usersByUsernameQuery(...).authoritiesByUsernameQuery(...);
			// LDAP을 사용하는 경우에는 다음과 같이 설정할 수 있다.
			// auth.ldapAuthentication().passwordEncoder(passwordEncoder()).passwordCompare()...

//          RDBMS를 이용할 경우의 예제
//			auth.userDetailsService(userService).passwordEncoder(passwordEncoder());

//          LDAP을 이용할 경우의 예제
//			auth.ldapAuthentication()
//					.userDnPatterns("uid={0},ou=Employee")
//					.groupSearchBase("ou=Person")
//					.passwordCompare()
//					.passwordEncoder(new PlaintextPasswordEncoder())
//					.passwordAttribute("password");

			// 예제
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

			auth.inMemoryAuthentication()
					.passwordEncoder(passwordEncoder())
					.withUser("john")
					.password(passwordEncoder.encode("1234"))
					.roles("ADMIN", "USER");
		}

		/**
		 * {@link AuthenticationManager}를 bean으로 등록한다.
		 * <p>{@link AuthenticationManager}의 구현체는 상위 클래스인 {@link WebSecurityConfigurerAdapter} 내부에서
		 * 정의된 AuthenticationManagerDelegator 클래스를 사용한다.</p>
		 *
		 * @return {@link AuthenticationManager}
		 * @throws Exception
		 */
		@Bean
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}
	}

	/**
	 * {@link WebSecurity}를 통해 Spring Security와 관련된 설정들을 처리한다. 여기서 처리되는 설정들은 다음과 같다.
	 * <ul><li>{@link WebSecurity#ignoring()}을 이용해 static 리소스 요청에 대해서 보안을 해제한다.</li></ul>
	 *
	 * @param web {@link org.springframework.security.config.annotation.web.builders.WebSecurity}
	 */
	public static void configureWeb(WebSecurity web) {
		web.ignoring().
				antMatchers("/styles/**").
				antMatchers("/images/**").
				antMatchers("/scripts/**");
	}

	/**
	 * 암호화를 위한 {@link PasswordEncoder}를 bean으로 등록한다.
	 * <p>{@link PasswordEncoder}의 구현으로 {@link BCryptPasswordEncoder}를 사용한다.
	 * BCrypt에 대한 정보는 <a href="http://d2.naver.com/helloworld/318732">안전한 패스워드 저장</a>을 참고하자.</p>
	 * <p>RDBMS나 LDAP에서 사용 중인 비밀번호 암호화 기법을 따라야 한다는 것을 알아두자.
	 * 암호화 기법을 바꿔야 할 경우 적합한 {@link PasswordEncoder} 구현을 bean으로 등록하거나 구현해야 한다.</p>
	 *
	 * @return {@link BCryptPasswordEncoder}
	 */
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * RESTful 기반의 인증 실패 핸들러인 {@link RestAuthenticationSuccessHandler}를 bean으로 등록한다.
	 *
	 * @return {@link RestAuthenticationSuccessHandler RestAuthenticationSuccessHandler}
	 */
	@Bean
	public static RestAuthenticationSuccessHandler authenticationSuccessHandler() {
		return new RestAuthenticationSuccessHandler();
	}

	/**
	 * RESTful 기반의 인증 실패 핸들러인 {@link RestAuthenticationFailureHandler}를 bean으로 등록한다.
	 *
	 * @return {@link RestAuthenticationFailureHandler RestAuthenticationFailureHandler}
	 */
	@Bean
	public static RestAuthenticationFailureHandler authenticationFailureHandler() {
		Map<String, String> exceptionMappings = new HashMap<>();

		// TODO AccountExpired, AccountLocked, CredentialExpired와 관련된 URL 매핑 전략 필요
		//exceptionMappings.put(DisabledException.class.getCanonicalName(), "/disabled");

		RestAuthenticationFailureHandler authenticationFailureHandler = new RestAuthenticationFailureHandler();

		authenticationFailureHandler.setExceptionMappings(exceptionMappings);

		return authenticationFailureHandler;
	}

	/**
	 * 인증이 성공한 경우 그 결과를 RESTful 형식으로 처리하기 위한 handler를 구현한다.
	 *
	 * @author Leejun Choi
	 */
	protected static class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
		RequestCache requestCache = new HttpSessionRequestCache();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
			this.handle(request, response, authentication);

			request.setAttribute("authorities", authentication.getAuthorities());

			clearAuthenticationAttributes(request);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
			if (requestCache.getRequest(request, response) == null) {
				clearAuthenticationAttributes(request);

				return;
			}

			String targetUrlParam = getTargetUrlParameter();

			if (isAlwaysUseDefaultTargetUrl() ||
					(targetUrlParam != null && StringUtils.hasText(request.getParameter(targetUrlParam)))) {
				requestCache.removeRequest(request, response);

				clearAuthenticationAttributes(request);

				return;
			}

			clearAuthenticationAttributes(request);
		}
	}

	/**
	 * 인증이 실패한 경우 그 결과를 RESTful 형식으로 처리하기 위한 handler를 구현한다.
	 *
	 * @author Leejun Choi
	 */
	protected static class RestAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {
		// implements here!
	}
}
