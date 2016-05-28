package com.github.pires.example.web;

import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalMap;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.zama.examples.multitenant.util.MultiTenantUtils;

public class MultiTenantCookieRememberMeManager extends CookieRememberMeManager {

	protected void rememberIdentity(Subject subject, PrincipalCollection accountPrincipals) {
		String multiPrincipal = (String) accountPrincipals.getPrimaryPrincipal();
		if (multiPrincipal != null) {
			multiPrincipal = MultiTenantUtils.getCurrentTenantId() + ":" + multiPrincipal;
		}
		if (accountPrincipals != null && accountPrincipals instanceof SimplePrincipalCollection) {
			SimplePrincipalCollection newSimplePrincipalCollection = new SimplePrincipalCollection(multiPrincipal,accountPrincipals.getRealmNames().iterator().next());
			super.rememberIdentity(subject, newSimplePrincipalCollection);
		} else if (accountPrincipals != null && accountPrincipals instanceof SimplePrincipalMap) {
			super.rememberIdentity(subject, accountPrincipals);
		}
	}

	public PrincipalCollection getRememberedPrincipals(SubjectContext subjectContext) {
		PrincipalCollection accountPrincipals = super.getRememberedPrincipals(subjectContext);
		if (accountPrincipals != null && accountPrincipals instanceof SimplePrincipalCollection) {
			String multiPrincipal = (String) accountPrincipals.getPrimaryPrincipal();
			if (multiPrincipal != null && multiPrincipal.indexOf(":") > 0) {
				String[] info = multiPrincipal.split(":");
				MultiTenantUtils.setCurrentTenantId(info[0]);
				String principal = info[1];
				accountPrincipals = new SimplePrincipalCollection(principal,accountPrincipals.getRealmNames().iterator().next());
			}
		}
		return accountPrincipals;
	}
}
