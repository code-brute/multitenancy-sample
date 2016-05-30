package org.zama.examples.multitenant.master.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zama.examples.multitenant.master.entity.Tenant;

/**
 * TenantRepository
 * 
 * @author Minly Wang
 * @since 2016年5月24日
 *
 */
public interface TenantRepository extends JpaRepository<Tenant, String> {
	Optional<Tenant> findOneByName(String name);

	Optional<Tenant> findOneByTenantId(String tenantId);
}
