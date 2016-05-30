package com.github.pires.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.github.pires.example.model.entity.Url;

/**
 * DAO for {@link Url}.
 */
public interface UrlRepository extends JpaRepository<Url, String> {
	@Query(value = "select u from Url u where u.url is not null")
	List<Url> findValidUrl();
}
