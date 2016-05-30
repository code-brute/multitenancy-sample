package com.github.pires.example.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.zama.examples.multitenant.model.BaseObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(value = { "version" })
public class Permission extends BaseObject<Permission> {

	private static final long serialVersionUID = 1L;
	@Column(name = "DESCRIPTION_", length = 128)
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
