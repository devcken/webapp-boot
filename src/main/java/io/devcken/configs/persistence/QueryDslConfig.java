package io.devcken.configs.persistence;

import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * JPA 기반의 persistence 설정들을 구현한다.
 * <p>Datasource로는 {@link HikariDataSource}의 인스턴스를 사용한다.</p>
 * <p>JPA verdor는 Hibernate로 하므로 {@link HibernateJpaVendorAdapter}를 bean으로 등록하고,
 * 그 외에 Hibernate와 관련된 property들은 {@link Properties}의 인스턴스로 설정한다.</p>
 * <p>dataSource나 entityManagerFactory를 다른 context에서 공유해야 한다면 root context에 등록하고
 * 그렇지 않다면 servlet context에 등록하면 된다.</p>
 *
 * @author Leejun Choi
 */
@Configuration
@EnableJpaRepositories("io.devcken.boot")
@PropertySource("classpath:datasource.properties")
public class QueryDslConfig {
	@Inject
	private Environment environment;

	/**
	 * 데이터베이스 연결을 위한 {@link DataSource}의 구현 인스턴스를 생성한다.
	 * 여기서는 <a href="https://brettwooldridge.github.io/HikariCP/">HikariCP</a>의 {@link HikariDataSource}를
	 * 사용한다.
	 * <p>{@link HikariDataSource}가 {@link java.io.Closeable}를 구현하므로,
	 * bean이 파괴될 때 {@link HikariDataSource#close()}가 실행되도록 {@link Bean#destroyMethod()}에 설정한다.</p>
	 * <p>{@link HikariDataSource}와 관련된 설정들은 모두 property 설정에서 가져온다.</p>
	 *
	 * @return {@link DataSource}
	 */
	@Bean
	public DataSource dataSource() {
		HikariDataSource dataSource = new HikariDataSource();

		dataSource.setDriverClassName(environment.getProperty("database.driverClassName"));
		dataSource.setJdbcUrl(environment.getProperty("database.url"));
		dataSource.setUsername(environment.getProperty("database.username"));
		dataSource.setPassword(environment.getProperty("database.password"));

		return dataSource;
	}

	/**
	 * JPA vendor의 어댑터를 bean으로 생성한다.
	 * JPA vendor로 Hibernate를 사용한다.
	 * <p>데이터베이스 변경이 필요할 경우, {@link HibernateJpaVendorAdapter#setDatabase(Database)}와 {@link HibernateJpaVendorAdapter#setDatabasePlatform(String)}에
	 * 설정되는 값을 데이터베이스에 맞게 수정해야 한다.</p>
	 *
	 * @return {@link HibernateJpaVendorAdapter}
	 */
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();

		jpaVendorAdapter.setDatabase(Database.MYSQL);
		jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
		jpaVendorAdapter.setShowSql(true);
		jpaVendorAdapter.setGenerateDdl(true);

		return jpaVendorAdapter;
	}

	/**
	 * {@link LocalContainerEntityManagerFactoryBean} bean을 생성한다.
	 * <p>{@link LocalContainerEntityManagerFactoryBean#setPackagesToScan(String...)}에 설정해야 하는 값은 property 설정에서 가져온다.</p>
	 *
	 * @return {@link LocalContainerEntityManagerFactoryBean}
	 */
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

		entityManagerFactoryBean.setPackagesToScan(environment.getProperty("database.packages_to_scan"));
		entityManagerFactoryBean.setDataSource(dataSource);
		entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		entityManagerFactoryBean.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());

		return entityManagerFactoryBean;
	}

	/**
	 *
	 *
	 * @param dataSource {@link QueryDslConfig#dataSource()}
	 * @return {@link SQLQueryFactory}
	 */
	@Bean
	public SQLQueryFactory queryFactory(DataSource dataSource) {
		com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(new MySQLTemplates());

		return new SQLQueryFactory(configuration, dataSource);
	}
}
