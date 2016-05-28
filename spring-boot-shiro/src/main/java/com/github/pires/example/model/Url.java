package com.github.pires.example.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.zama.examples.multitenant.model.BaseObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(value = { "version" })
public class Url extends BaseObject<Url> {

	private static final long serialVersionUID = 1L;
	@Column(name = "URL_", length = 500, nullable = false)
	private String url;
	@Column(name = "DESCRIPTION_", length = 128)
	private String description;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "ROLE_URL", joinColumns = @JoinColumn(name = "URL_ID_"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID_"))
	private List<Role> roles;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Role> getRoles() {
		if (roles == null) {
			this.roles = new ArrayList<>();
		}
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

}
