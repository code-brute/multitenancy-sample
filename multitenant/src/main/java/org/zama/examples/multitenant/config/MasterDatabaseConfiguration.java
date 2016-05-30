package org.zama.examples.multitenant.config;

import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.zama.examples.multitenant.annotation.MasterTransactional;
import org.zama.examples.multitenant.util.Constants;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * DatabaseConfiguration.
 *
 * @author Zakir Magdum
 */
@Configuration
@EnableConfigurationProperties(JpaProperties.class)
@EnableJpaRepositories(entityManagerFactoryRef = Constants.MASTER_ENTITY_MANAGER_NAME, 
transactionManagerRef = MasterTransactional.DEFAULT_NAME, 
basePackages = {"org.zama.examples.multitenant.master.repository" })
@EnableTransactionManagement
public class MasterDatabaseConfiguration {
	private final static Logger LOGGER = LoggerFactory.getLogger(MasterDatabaseConfiguration.class);

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.dataSourceClassName}")
	private String dataSourceClassName;

	@Value("${spring.datasource.username}")
	private String user;

	@Value("${spring.datasource.password}")
	private String password;

	@Inject
	private JpaProperties jpaProperties;

	// @Inject
	// private DataSource dataSource;

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		LOGGER.debug("Configuring datasource {} {} {}", dataSourceClassName, url, user);
		HikariConfig config = new HikariConfig();
		config.setDataSourceClassName(dataSourceClassName);
		config.addDataSourceProperty("url", url);
		config.addDataSourceProperty("user", user);
		config.addDataSourceProperty("password", password);
		return new HikariDataSource(config);
	}

	// @Bean
	// public SpringLiquibase liquibase(DataSource dataSource) {
	// SpringLiquibase sl = new SpringLiquibase();
	// sl.setDataSource(dataSource);
	// sl.setContexts(liquibaseContext);
	// sl.setChangeLog("classpath:dbchangelog.xml");
	// sl.setShouldRun(true);
	// return sl;
	// }

	@Bean(name = "masterEntityManager")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan(new String[] { "org.zama.examples.multitenant.master.entity" });
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalJpaProperties(dataSource));

		em.setPersistenceUnitName("master");

		return em;
	}

	private Properties additionalJpaProperties(DataSource dataSource) {
		Properties properties = new Properties();
		for (Map.Entry<String, String> entry : jpaProperties.getHibernateProperties(dataSource).entrySet()) {
			properties.setProperty(entry.getKey(), entry.getValue());
		}
		return properties;
	}

	@Bean(name = MasterTransactional.DEFAULT_NAME)
	public JpaTransactionManager transactionManager(EntityManagerFactory masterEntityManager) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(masterEntityManager);
		return transactionManager;
	}
}
