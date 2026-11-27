package com.bodriopopcorn.dto.request;

import java.util.List;

import com.bodriopopcorn.entity.TitleType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TitleRequest(
		@NotNull TitleType type,
		@NotBlank @Size(max = 180) String name,
		@NotBlank @Size(max = 5000) String synopsis,
		@NotNull @Min(1888) @Max(2100) Integer releaseYear,
		@Positive Integer durationMinutes,
		@Positive Integer seasons,
		@Size(max = 500) String posterUrl,
		@NotEmpty List<@NotNull @Positive Long> genreIds,
		@Valid List<@Valid TitleCreditRequest> credits
) {
	@AssertTrue(message = "MOVIE requires durationMinutes only; SERIES requires seasons only")
	public boolean isFormatValid() {
		if (type == null) {
			return true;
		}
		return switch (type) {
			case MOVIE -> durationMinutes != null && seasons == null;
			case SERIES -> seasons != null && durationMinutes == null;
		};
	}
}
