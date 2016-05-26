package com.github.minly.multitenancy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Jannik on 15.02.15.
 */
@Entity
@Table(name = "T_TENANT_DATA_SOURCE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantDataSourceConfigEntity {

	@Id
	@Column(name = "tenant_id_")
	private String tenantId;
	@Column(name = "db_name_")
	private String dbName;
	@Column(name = "username_")
	private String username;
	@Column(name = "password_")
	private String password;
	@Column(name = "driver_class_name_")
	private String driverClassName;
	@Column(name = "server_name_")
	private String serverName;
	@Column(name = "port_")
	private String port;
	@Column(name = "schema_")
	private String schema;
	@Column(name = "url_")
	private String url;

	public TenantDataSourceConfigEntity() {
		super();
	}

	public TenantDataSourceConfigEntity(String dbName, String driverClassName, String tenantId, String username, String password, String serverName, String port, String schema, String url) {
		this.dbName = dbName;
		this.driverClassName = driverClassName;
		this.tenantId = tenantId;
		this.username = username;
		this.password = password;
		this.serverName = serverName;
		this.port = port;
		this.schema = schema;
		this.url = url;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getUsername() {
		return username;
	}

	public String getDbName() {
		return dbName;
	}

	public String getPassword() {
		return password;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getServerName() {
		return serverName;
	}

	public String getPort() {
		return port;
	}

	public String getSchema() {
		return schema;
	}

	public String getUrl() {
		return url;
	}

}
