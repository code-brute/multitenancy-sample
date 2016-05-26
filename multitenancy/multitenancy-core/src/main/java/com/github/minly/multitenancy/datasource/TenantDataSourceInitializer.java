package com.github.minly.multitenancy.datasource;

import javax.sql.DataSource;

import com.github.minly.multitenancy.model.TenantDataSourceConfigEntity;

/**
 * Created by Jannik on 22.03.15.
 */
public interface TenantDataSourceInitializer {
    DataSource initializeDataSource(TenantDataSourceConfigEntity dataSourceConfig);
}
