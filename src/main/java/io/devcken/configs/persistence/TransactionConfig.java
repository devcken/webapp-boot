package io.devcken.configs.persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * <p>단일 transaction 자원을 가정하여 {@link JpaTransactionManager}를 사용한다. 만약, 다중 transaction 자원이 필요하다면
 * {@link org.springframework.transaction.jta.JtaTransactionManager}가 필요하다.</p>
 * <p>Transaction 설정을 따로 분리한 이유는 dataSource나 entityManagerFactory 등을 서로 다른 context에서 공유해야 할 경우 transaction는
 * 공유할 수가 없기 때문이다.</p>
 *
 * @author Leejun Choi
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {
	/**
	 * JPA 사용 시 transaction을 지원하기 위해 {@link JpaTransactionManager}를 bean으로 등록한다.
	 *
	 * @param entityManagerFactory {@link javax.persistence.EntityManagerFactory}
	 * @param dataSource {@link DataSource}
	 * @return {@link org.springframework.orm.jpa.JpaTransactionManager}
	 */
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory, DataSource dataSource) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();

		transactionManager.setEntityManagerFactory(entityManagerFactory);
		transactionManager.setDataSource(dataSource);

		return transactionManager;
	}
}
