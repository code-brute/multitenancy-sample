package com.github.pires.example.model.entity;

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
public class Role extends BaseObject<Role> {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "DESCRIPTION_", length = 128)
	private String description;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "ROLE_PERMISSION", joinColumns = @JoinColumn(name = "ROLE_ID_"), inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID_"))
	private List<Permission> permissions;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Permission> getPermissions() {
		if (permissions == null)
			this.permissions = new ArrayList<>();
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

}
