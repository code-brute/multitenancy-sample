package com.github.minly.multitenancy.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.minly.multitenancy.model.TenantDataSourceConfigEntity;

/**
 * Spring Data JPA repository for the TenantDataSourceEntity.
 * Created by Jannik on 09.03.15.
 */
@Repository
public interface TenantDataSourceConfigRepository extends JpaRepository<TenantDataSourceConfigEntity, String> {
}
