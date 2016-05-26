package com.github.minly.multitenancy.demo.configuration;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * Created by Jannik on 02.07.15.
 */
@Configuration
@EnableJpaRepositories(basePackages = {"com.github.minly.multitenancy.demo.repository"}, entityManagerFactoryRef = "entityManagerFactory")
public class DatabaseConfiguration implements EnvironmentAware{

    private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);
    private RelaxedPropertyResolver jpaProps;



    @Bean(name = "metaDataSource")
    @ConfigurationProperties(prefix="spring.datasource.meta")
    public DataSource metaDataSource() {
    	return DataSourceBuilder.create()
         .url("jdbc:mysql://localhost:3306/meta_db?useUnicode=true&characterEncoding=utf8&useSSL=false")
         .username("root")
         .password("minly")
         .driverClassName("com.mysql.jdbc.Driver")
         .build();
    }

    //TODO: Replace schema-init with liquibase
//    @Bean(name = "tenantOneDataSource")
//    @ConfigurationProperties(prefix="spring.datasource.tenant1")
//    public DataSource tenantOneDataSource() {
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .setName("tenant_2_db")
//                .addScript("classpath:sql/tenant/schema.sql")
//                .build();
//    }

    //TODO: Replace schema-init with liquibase
//    @Bean(name = "tenantTwoDataSource")
//    @ConfigurationProperties(prefix="spring.datasource.tenant2")
//    public DataSource tenantTwoDataSource() {
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.H2)
//                .setName("tenant_1_db")
//                .addScript("classpath:sql/tenant/schema.sql")
//                .build();
//    }

    public void setEnvironment(Environment environment) {
        this.jpaProps = new RelaxedPropertyResolver(environment, "spring.jpa.");
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       DataSource dataSource) {
        log.info("Configuring EntityManagerFactory for Tenants");
        LocalContainerEntityManagerFactoryBean factory = builder.dataSource(dataSource)
                .persistenceUnit("tenantPU")
                .jta(true)
                .packages("com.github.minly.multitenancy.demo")
                .build();
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaDialect(new HibernateJpaDialect());

        return factory;
    }

}

