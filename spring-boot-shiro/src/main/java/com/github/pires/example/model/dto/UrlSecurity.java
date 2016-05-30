package com.github.pires.example.model.dto;

public class UrlSecurity {
	private String name;
	private String url;
	private String roles;
	private String permissions;

	public UrlSecurity() {
		super();
	}

	public UrlSecurity(String name, String url, String roles) {
		this(name, url, roles, null);
	}

	public UrlSecurity(String name, String url, String roles, String permissions) {
		super();
		this.name = name;
		this.url = url;
		this.roles = roles;
		this.permissions = permissions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

}
