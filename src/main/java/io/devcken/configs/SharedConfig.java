package io.devcken.configs;

import io.devcken.support.ProfileEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedConfig {
	@Bean
	public ProfileEnvironment profileEnvironment() {
		return new ProfileEnvironment();
	}
}
