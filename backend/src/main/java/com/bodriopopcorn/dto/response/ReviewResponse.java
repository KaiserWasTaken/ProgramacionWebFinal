package com.bodriopopcorn.dto.response;

import java.time.LocalDateTime;

import com.bodriopopcorn.entity.ReviewStatus;

public record ReviewResponse(
		Long id,
		Long titleId,
		String titleName,
		String userEmail,
		Integer rating,
		String comment,
		ReviewStatus status,
		LocalDateTime createdAt
) {
}
