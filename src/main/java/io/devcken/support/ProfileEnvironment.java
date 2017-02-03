package io.devcken.support;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;

import javax.inject.Inject;

/**
 * <p>Spring Profile은 환경에 따라 bean 사용을 허용하거나 제한하는 좋은 기술이나 JavaConfiguration에서는 다소 문제가 있다.
 * Profile에 대한 property(properties 파일에 로드되는)를 구분하여 사용하는 것은 불가능하다. 그래서, profile을 prefix로 하는
 * property를 이용할 수 있는 {@link Environment}(정확히 말하자면, {@link org.springframework.web.context.support.StandardServletEnvironment})의
 * wrapper class를 만들었다.</p>
 *
 * @author Leejun Choi
 */
public class ProfileEnvironment {
	@Inject
	private Environment environment;

	/**
	 * Profile에 해당하는 property를 가져온다. Profile이 설정되지 않는다면, 기본 property를 대신 가져온다.
	 *
	 * @param key property에 대한 key 문자열
	 * @return Profile에 대한 property 값
	 */
	public String getProfileProperty(String key) {
		String profile = this.environment.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);

		// 지정된 profile이 없다면 raw key를 그대로 사용하고, 있다면 profile을 property key의 prefix로 활용한다.
		profile = profile == null ? "" : profile.concat(".");

		String property = this.environment.getProperty(profile.concat(key));

		// profile이 prefix된 key에 해당하는 property가 없다면, raw key에 해당하는 property value를 반환한다.
		return property == null ? this.environment.getProperty(key) : property;
	}
}

