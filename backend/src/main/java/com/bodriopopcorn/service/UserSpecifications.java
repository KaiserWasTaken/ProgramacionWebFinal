package com.bodriopopcorn.service;

import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.bodriopopcorn.entity.User;

final class UserSpecifications {

	private UserSpecifications() {
	}

	static Specification<User> withSearch(String search) {
		return (root, query, builder) -> {
			if (search == null || search.isBlank()) {
				return builder.conjunction();
			}
			String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
			return builder.or(
					builder.like(builder.lower(root.get("email")), pattern),
					builder.like(builder.lower(root.get("fullName")), pattern)
			);
		};
	}
}
