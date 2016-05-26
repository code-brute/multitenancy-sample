package com.github.pires.example.shiro;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.jdbc.JdbcRealm;

import com.github.pires.example.authentication.TenantAuthenticationToken;

public class TenantSaltedJdbcRealm extends JdbcRealm {

	public TenantSaltedJdbcRealm() {
		// Cant seem to set this via beanutils/shiro.ini
		this.saltStyle = SaltStyle.COLUMN;
	}

	@Override
	public boolean supports(AuthenticationToken token) {
		return super.supports(token) && (token instanceof TenantAuthenticationToken);
	}
}
