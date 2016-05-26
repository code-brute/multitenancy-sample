package com.github.pires.example.shiro;

import java.io.Serializable;
import java.util.Map;

import org.apache.shiro.session.Session;

import com.hazelcast.query.Predicate;

/**
 * Hazelcast query predicate for Shiro session attributes.
 */
public class SessionAttributePredicate<T> implements Predicate<Serializable, Session> {

	private static final long serialVersionUID = 1L;
	private final String attributeName;
	private final T attributeValue;

	public SessionAttributePredicate(String attributeName, T attributeValue) {
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public T getAttributeValue() {
		return attributeValue;
	}

	@Override
	public boolean apply(Map.Entry<Serializable, Session> sessionEntry) {
		@SuppressWarnings("unchecked")
		final T attribute = (T) sessionEntry.getValue().getAttribute(attributeName);
		return attribute.equals(attributeValue);
	}

}
