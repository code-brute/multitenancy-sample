package com.github.pires.example.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.zama.examples.multitenant.context.MultiTenantListener;
import org.zama.examples.multitenant.context.event.MultiTenantDatasSourceInitializedEvent;
import org.zama.examples.multitenant.context.event.SimpleMultiTenantEventMulticaster;
import org.zama.examples.multitenant.master.entity.Tenant;
import org.zama.examples.multitenant.master.repository.TenantRepository;
import org.zama.examples.multitenant.util.MultiTenantUtils;

import com.github.pires.example.model.dto.UrlSecurity;
import com.github.pires.example.model.entity.Role;
import com.github.pires.example.model.entity.Url;
import com.github.pires.example.repository.UrlRepository;
import com.github.pires.example.service.ShiroFilerChainManager;
import com.github.pires.example.service.UrlSecurityService;

@Service(UrlSecurityService.BEAN_ID)
public class UrlSecurityServiceImpl implements UrlSecurityService, MultiTenantListener<MultiTenantDatasSourceInitializedEvent>, ApplicationContextAware {

	@Autowired
	private TenantRepository tenantRepository;

	@Autowired
	private UrlRepository urlRepository;

	@Inject
	private SimpleMultiTenantEventMulticaster simpleMultiTenantEventMulticaster;

	@Autowired
	private ShiroFilerChainManager shiroFilerChainManager;

	@Override
	public void initUrlSecurity(List<UrlSecurity> urlSecurities, String tenantPrefix) {
		shiroFilerChainManager.initFilterChains(urlSecurities, tenantPrefix);
	}

	@Override
	public void initUrlSecurity(List<UrlSecurity> urlSecurities) {
		initUrlSecurity(urlSecurities, null);
	}

	@Override
	public void initUrlSecurity() {
		initUrlSecurity(buildUrlSecurities());
	}

	@Override
	public void refreshUrlSecurity() {
		initUrlSecurity();
	}

	private List<UrlSecurity> buildUrlSecurities() {
		List<Url> urls = urlRepository.findValidUrl();
		if (urls != null) {
			List<UrlSecurity> urlSecurities = new ArrayList<UrlSecurity>(urls.size());
			urls.forEach(url -> {
				List<Role> roles = url.getRoles();
				if (roles != null) {
					List<String> roleNames = new ArrayList<String>(roles.size());
					roles.forEach(role -> {
						roleNames.add(role.getName());
					});
					urlSecurities.add(new UrlSecurity(url.getName(), MultiTenantUtils.getTenantPrefix() + url.getUrl(), String.join(",", roleNames)));
				}
			});
			return urlSecurities;
		}
		return Collections.emptyList();
	}

	@Override
	public void onMultiTenantEvent(MultiTenantDatasSourceInitializedEvent event) {
		List<Tenant> tenants = tenantRepository.findAll();
		if (tenants.isEmpty()) {
			return;
		}
		List<UrlSecurity> urlSecurities = new ArrayList<UrlSecurity>();
		tenants.forEach(tenant -> {
			try {
				String tenantId = tenant.getTenantId();
				MultiTenantUtils.setCurrentTenantId(tenantId);
				urlSecurities.addAll(buildUrlSecurities());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				MultiTenantUtils.clear();
			}
		});
		this.initUrlSecurity(urlSecurities);

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		simpleMultiTenantEventMulticaster.addMultiTenantListener(this);
	}
}
