package com.bodriopopcorn.service;

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.bodriopopcorn.entity.Title;
import com.bodriopopcorn.entity.TitleType;

import jakarta.persistence.criteria.JoinType;

final class TitleSpecifications {

	private TitleSpecifications() {
	}

	static Specification<Title> withFilters(
			String search,
			Long genreId,
			TitleType type,
			Integer year,
			BigDecimal minRating
	) {
		return (root, query, builder) -> {
			var predicates = builder.conjunction();
			if (search != null && !search.isBlank()) {
				String pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
				predicates = builder.and(predicates, builder.or(
						builder.like(builder.lower(root.get("name")), pattern),
						builder.like(builder.lower(root.get("synopsis")), pattern)
				));
			}
			if (genreId != null) {
				var genres = root.join("genres", JoinType.INNER);
				predicates = builder.and(predicates, builder.equal(genres.get("id"), genreId));
				query.distinct(true);
			}
			if (type != null) {
				predicates = builder.and(predicates, builder.equal(root.get("type"), type));
			}
			if (year != null) {
				predicates = builder.and(predicates, builder.equal(root.get("releaseYear"), year));
			}
			if (minRating != null) {
				predicates = builder.and(predicates, builder.greaterThanOrEqualTo(root.get("avgRating"), minRating));
			}
			return predicates;
		};
	}
}
