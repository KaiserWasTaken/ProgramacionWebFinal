package com.bodriopopcorn.service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

public final class PageableSupport {

	private static final int MAX_PAGE_SIZE = 100;

	private PageableSupport() {
	}

	public static Pageable validate(Pageable pageable, String... allowedSortFields) {
		if (pageable.getPageSize() > MAX_PAGE_SIZE) {
			throw new IllegalArgumentException("Page size cannot exceed " + MAX_PAGE_SIZE);
		}
		Set<String> allowedFields = Arrays.stream(allowedSortFields).collect(Collectors.toSet());
		pageable.getSort().forEach(order -> {
			if (!allowedFields.contains(order.getProperty())) {
				throw new IllegalArgumentException("Sorting by " + order.getProperty() + " is not supported");
			}
		});
		return pageable;
	}
}
