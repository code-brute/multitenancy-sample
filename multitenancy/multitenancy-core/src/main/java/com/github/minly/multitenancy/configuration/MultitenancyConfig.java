package com.github.minly.multitenancy.configuration;


import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.github.minly.multitenancy.datasource.TenantRoutingDataSource;
import com.github.minly.multitenancy.identifier.TenantIdentifierStorage;
import com.github.minly.multitenancy.scope.TenantScope;

/**
 * Created by Jannik on 27.02.15.
 */
@Configuration
@ComponentScan("com.github.minly.multitenancy")
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = "com.github.minly.multitenancy.repository", entityManagerFactoryRef = "metaEntityManager")
@EnableAutoConfiguration
public class MultitenancyConfig implements EnvironmentAware {

    private RelaxedPropertyResolver dsProps;
    private RelaxedPropertyResolver jpaProps;

    @Override
    public void setEnvironment(Environment environment) {
        this.dsProps = new RelaxedPropertyResolver(environment, "spring.datasource.");
        this.jpaProps = new RelaxedPropertyResolver(environment, "spring.jpa.");
    }

    @Bean
    public static CustomScopeConfigurer configureTenantScope() {
        CustomScopeConfigurer customScopeConfigurer = new CustomScopeConfigurer();
        customScopeConfigurer.addScope(TenantScope.SCOPE_NAME, new TenantScope());
        return customScopeConfigurer;
    }

    @Bean
    @Primary
    public TenantRoutingDataSource tenantDataSource(@Qualifier("metaDataSource") DataSource metaDataSource) {
        return new TenantRoutingDataSource(metaDataSource);
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean metaEntityManager(EntityManagerFactoryBuilder builder,
                                                                    @Qualifier("metaDataSource") DataSource dataSource) {
        Map<String, Object> props = new HashMap<>();
        props.putAll(jpaProps.getSubProperties("properties."));
//        props.put("hibernate.hbm2ddl.auto", "create");

        LocalContainerEntityManagerFactoryBean factory = builder.dataSource(dataSource)
            .jta(true)
            .persistenceUnit("metaPU")
            .packages("com.github.minly.multitenancy.model")
            .properties(props)
            .build();
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaDialect(new HibernateJpaDialect());
        return factory;
    }

    @Bean
    public TenantIdentifierStorage tenantIdentifierStorage(){
        return new TenantIdentifierStorage();
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }
}
