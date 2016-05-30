package com.github.pires.example.model.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.zama.examples.multitenant.model.BaseObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(value = { "version" })
public class User extends BaseObject<User> {

	private static final long serialVersionUID = -1183403993173325535L;
	@Column(name = "EMAIL_", length = 128)
	private String email;
	@Column(name = "USERNAME_", length = 128, nullable = false)
	private String username;
	// @Column(name = "PASSWORD_HINT_", length = 50)
	// private String passwordHint;
	@Column(name = "PHONE_NUMBER_", length = 50)
	private String phoneNumber;
	@Column(name = "ACTIVE_")
	@Type(type = "yes_no")
	private Boolean active;
	@Column(name = "PASSWORD_", length = 128)
	private String password;
	@Transient
	private String tenantId;
	@Transient
	private boolean rememberMe;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID_"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID_"))
	private List<Role> roles;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
}
