package io.devcken.configs.persistence;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.data.neo4j.server.RemoteServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableNeo4jRepositories({"io.devcken.boot"})
@EnableTransactionManagement
@PropertySource("classpath:neo4j.properties")
public class Neo4jConfig extends Neo4jConfiguration {
	@Autowired
	private Environment environment;

	@Bean
	public Neo4jServer neo4jServer() {
		return new RemoteServer(environment.getProperty("neo4j.url"));
	}

	@Override
	public SessionFactory getSessionFactory() {
		return new SessionFactory(environment.getProperty("neo4j.domainPath"));
	}

	@Bean
	@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	@Override
	public Session getSession() throws Exception {
		return super.getSession();
	}
}
