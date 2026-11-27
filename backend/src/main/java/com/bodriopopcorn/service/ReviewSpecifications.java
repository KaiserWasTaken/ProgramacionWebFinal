package com.bodriopopcorn.service;

import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.bodriopopcorn.entity.Review;
import com.bodriopopcorn.entity.ReviewStatus;

final class ReviewSpecifications {

	private ReviewSpecifications() {
	}

	static Specification<Review> withFilters(ReviewStatus status, String search) {
		return (root, query, builder) -> {
			var predicates = builder.conjunction();
			if (status != null) {
				predicates = builder.and(predicates, builder.equal(root.get("status"), status));
			}
			if (search != null && !search.isBlank()) {
				String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
				predicates = builder.and(predicates, builder.or(
						builder.like(builder.lower(root.join("title").get("name")), pattern),
						builder.like(builder.lower(root.join("user").get("email")), pattern)
				));
			}
			return predicates;
		};
	}
}
