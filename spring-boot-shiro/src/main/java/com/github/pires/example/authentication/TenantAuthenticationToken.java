package com.github.pires.example.authentication;

import org.apache.shiro.authc.UsernamePasswordToken;

public class TenantAuthenticationToken extends UsernamePasswordToken {
	private static final long serialVersionUID = 5382076682148900760L;

	private String tenantId = null;

	public TenantAuthenticationToken(final String username, final char[] password, String tenantId) {
		setUsername(username);
		setPassword(password);
		setTenantId(tenantId);
	}

	public TenantAuthenticationToken(final String username, final String password, String tenantId) {
		setUsername(username);
		setPassword(password != null ? password.toCharArray() : null);
		setTenantId(tenantId);
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
}
