package com.bodriopopcorn.dto.response;

import java.math.BigDecimal;

import com.bodriopopcorn.entity.TitleType;

public record TitleSummaryResponse(
		Long id,
		String name,
		TitleType type,
		BigDecimal avgRating,
		String posterUrl
) {
}
