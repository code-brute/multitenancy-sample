package com.github.pires.example.service;

import java.util.List;

import com.github.pires.example.model.dto.UrlSecurity;

public interface UrlSecurityService {
	String BEAN_ID = "urlSecurityService";

	void initUrlSecurity();

	void refreshUrlSecurity();

	void initUrlSecurity(List<UrlSecurity> urlSecurities);

	void initUrlSecurity(List<UrlSecurity> urlSecurities, String tenantPrefix);
}
