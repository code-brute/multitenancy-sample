package org.zama.examples.multitenant.master.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.zama.examples.multitenant.model.BaseObject;

/**
 * 
 * @author Minly Wang
 * @since 2016年5月24日
 *
 */
@Entity
@Table(name = "TENANT_DATA_SOURCE")
public class TenantDataSource extends BaseObject<Tenant> {

	private static final long serialVersionUID = 1L;

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

	public TenantDataSource() {
		super();
	}

	public TenantDataSource(String dbName, String driverClassName, String username, String password, String serverName, String port, String schema, String url) {
		this.dbName = dbName;
		this.driverClassName = driverClassName;
		this.username = username;
		this.password = password;
		this.serverName = serverName;
		this.port = port;
		this.schema = schema;
		this.url = url;
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
