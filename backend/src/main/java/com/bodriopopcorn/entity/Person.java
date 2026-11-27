package com.bodriopopcorn.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "people")
@BatchSize(size = 50)
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "role_type", nullable = false, length = 10)
	private PersonRoleType roleType;

	@Column(name = "tmdb_id")
	private Long tmdbId;

	protected Person() {
	}

	public Person(String name, PersonRoleType roleType) {
		this.name = name;
		this.roleType = roleType;
	}

	public Person(String name, PersonRoleType roleType, Long tmdbId) {
		this(name, roleType);
		this.tmdbId = tmdbId;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PersonRoleType getRoleType() {
		return roleType;
	}

	public void setRoleType(PersonRoleType roleType) {
		this.roleType = roleType;
	}
}
