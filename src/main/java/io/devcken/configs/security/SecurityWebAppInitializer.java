package io.devcken.configs.security;

import io.devcken.configs.persistence.QueryDslConfig;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * Spring Security를 위한 Context를 등록한다.
 *
 * @author Leejun Choi
 */
public class SecurityWebAppInitializer extends AbstractSecurityWebApplicationInitializer {
	public SecurityWebAppInitializer() {
		super(SecurityConfig.class, QueryDslConfig.class);
	}
}
