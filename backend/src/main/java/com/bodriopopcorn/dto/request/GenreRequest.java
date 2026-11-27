package com.bodriopopcorn.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GenreRequest(
		@NotBlank @Size(max = 60) String name
) {
}
