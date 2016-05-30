package com.github.pires.example.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.NamedFilterList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pires.example.model.dto.UrlSecurity;

@Service(ShiroFilerChainManager.BEAN_ID)
public class ShiroFilerChainManager {
	public final static String BEAN_ID = "shiroFilerChainManager";
	@Autowired
	private DefaultFilterChainManager filterChainManager;
	private Map<String, NamedFilterList> defaultFilterChains;

	@PostConstruct
	public void init() {
		defaultFilterChains = new HashMap<String, NamedFilterList>(filterChainManager.getFilterChains());
	}

	public void initFilterChains(List<UrlSecurity> urlSecurities) {
		this.initFilterChains(urlSecurities, null);
	}

	public void initFilterChains(List<UrlSecurity> urlSecurities, String tenantPrefix) {
		// 1、首先删除以前老的filter chain并注册默认的
		Map<String, NamedFilterList> originalChains = filterChainManager.getFilterChains();
		if(StringUtils.isEmpty(tenantPrefix)){
			filterChainManager.getFilterChains().clear();
		}else{
			Set<String> keySet = new HashSet<String>(originalChains.keySet());
			keySet.forEach(key -> {
				if (key.startsWith(tenantPrefix)) {
					originalChains.remove(key);
				}
			});
		}
		if (defaultFilterChains != null) {
			filterChainManager.getFilterChains().putAll(defaultFilterChains);
		}
		// 2、循环URL Filter 注册filter chain
		for (UrlSecurity urlSecurity : urlSecurities) {
			String url = urlSecurity.getUrl();
			// 注册roles filter
			if (!StringUtils.isEmpty(urlSecurity.getRoles())) {
				filterChainManager.addToChain(url, "roles", urlSecurity.getRoles());
			}
			// 注册perms filter
			if (!StringUtils.isEmpty(urlSecurity.getPermissions())) {
				filterChainManager.addToChain(url, "perms", urlSecurity.getPermissions());
			}
		}
	}
}
