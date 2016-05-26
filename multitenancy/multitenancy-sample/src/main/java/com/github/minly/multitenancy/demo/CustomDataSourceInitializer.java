package com.github.minly.multitenancy.demo;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import com.github.minly.multitenancy.datasource.TenantDataSourceInitializer;
import com.github.minly.multitenancy.model.TenantDataSourceConfigEntity;

/**
 * Created by Jannik on 02.07.15.
 */
@Component
public class CustomDataSourceInitializer implements TenantDataSourceInitializer {

    public DataSource initializeDataSource(TenantDataSourceConfigEntity tenantDataSourceConfigEntity) {
        return
                DataSourceBuilder.create()
                .url(tenantDataSourceConfigEntity.getUrl())
                .username(tenantDataSourceConfigEntity.getUsername())
                .password(tenantDataSourceConfigEntity.getPassword())
                .build();
    }
}
