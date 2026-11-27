package com.bodriopopcorn.service;

import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.bodriopopcorn.entity.Person;
import com.bodriopopcorn.entity.PersonRoleType;

final class PersonSpecifications {

	private PersonSpecifications() {
	}

	static Specification<Person> withFilters(String search, PersonRoleType roleType) {
		return (root, query, builder) -> {
			var predicates = builder.conjunction();
			if (search != null && !search.isBlank()) {
				predicates = builder.and(predicates, builder.like(
						builder.lower(root.get("name")),
						"%" + search.trim().toLowerCase(Locale.ROOT) + "%"));
			}
			if (roleType != null) {
				predicates = builder.and(predicates, builder.equal(root.get("roleType"), roleType));
			}
			return predicates;
		};
	}
}
