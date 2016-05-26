package org.zama.examples.multitenant.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.zama.examples.multitenant.util.LocalDateTimePersistenceConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * BaseObject.
 * 
 * @author Zakir Magdum
 */
@SuppressWarnings({ "serial", "rawtypes" })
@MappedSuperclass
public class BaseObject<T extends BaseObject> implements Serializable {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	@Column(name = "ID_")
	private String id;

	@Column(name = "NAME_", unique = true, nullable = false, length = 128)
	private String name;

	@Version
	@JsonIgnore
	@Column(name = "VERSION_")
	private Long version;

	@Column(name = "CREATED_")
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime created;
	@Column(name = "CREATED_BY_", length = 50)
	private String createdBy;

	@Column(name = "UPDATED_BY_", length = 50)
	private String updatedBy;

	@Column(name = "UPDATED_")
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime updated;

	// @Column
	// @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	// private LocalDateTime deleted;

	@SuppressWarnings("unchecked")
	public T merge(T other) {
		// do not copy id so as not to confuse hibernate
		this.name = other.getName();
		this.created = other.getCreated();
		this.updated = other.getUpdated();

		return (T) this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getUpdated() {
		return updated;
	}

	public void setUpdated(LocalDateTime updated) {
		this.updated = updated;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		BaseObject<?> that = (BaseObject<?>) o;

		return !(name != null ? !name.equals(that.name) : that.name != null);

	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}
