package com.bodriopopcorn.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TitleCreditRequest(
		@NotNull @Positive Long personId,
		@Size(max = 120) String characterName
) {
}
