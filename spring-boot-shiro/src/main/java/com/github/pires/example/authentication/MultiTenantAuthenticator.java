package com.github.pires.example.authentication;

import java.util.Collection;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

public class MultiTenantAuthenticator extends ModularRealmAuthenticator {

	@Override
	protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
		assertRealmsConfigured();
		TenantAuthenticationToken tat = null;
		Realm tenantRealm = null;

		if (!(authenticationToken instanceof TenantAuthenticationToken)) {
			throw new AuthenticationException("Unrecognized token , not a typeof TenantAuthenticationToken ");
		} else {
			tat = (TenantAuthenticationToken) authenticationToken;
			tenantRealm = lookupRealm(tat.getTenantId());
		}

		return doSingleRealmAuthentication(tenantRealm, tat);

	}

	protected Realm lookupRealm(String clientId) throws AuthenticationException {
		Collection<Realm> realms = getRealms();
		for (Realm realm : realms) {
			if (realm.getName().equalsIgnoreCase(clientId)) {
				return realm;
			}
		}
		throw new AuthenticationException("No realm configured for Client " + clientId);
	}
}
