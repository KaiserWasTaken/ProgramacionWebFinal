package com.bodriopopcorn.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.bodriopopcorn.entity.TitleType;

public record TitleResponse(
		Long id,
		TitleType type,
		String name,
		String synopsis,
		Integer releaseYear,
		Integer durationMinutes,
		Integer seasons,
		String posterUrl,
		BigDecimal avgRating,
		List<GenreResponse> genres,
		List<TitleCreditResponse> credits,
		LocalDateTime createdAt,
		String tmdbMediaType,
		Long tmdbId
) {
}
