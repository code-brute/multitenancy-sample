package org.zama.examples.multitenant.master.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;
import org.zama.examples.multitenant.model.BaseObject;

/**
 * Tenant
 * 
 * @author Minly Wang
 * @since 2016年5月24日
 *
 */
@Entity
public class Tenant extends BaseObject<Tenant> {

	private static final long serialVersionUID = 1L;
	@Column(name = "TENANT_ID_", length = 64, unique = true, nullable = false)
	private String tenantId;
	@Column(name = "DESCRIPTION_", length = 255)
	private String description;
	@Column(name = "ADDRESS_", length = 255)
	private String address;
	@Column(name = "ENABLED_", length = 2)
	@Type(type = "yes_no")
	private boolean enabled;
	@OneToOne(optional = false)
	@JoinColumn(name = "TENANT_DATA_SOURCE_", unique = true)
	private TenantDataSource tenantDataSource;

	public Tenant merge(Tenant tenant) {
		super.merge(tenant);
		this.tenantId = tenant.tenantId;
		this.description = tenant.description;
		this.address = tenant.address;
		this.enabled = tenant.enabled;
		return this;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public TenantDataSource getTenantDataSource() {
		return tenantDataSource;
	}

	public void setTenantDataSource(TenantDataSource tenantDataSource) {
		this.tenantDataSource = tenantDataSource;
	}

}
