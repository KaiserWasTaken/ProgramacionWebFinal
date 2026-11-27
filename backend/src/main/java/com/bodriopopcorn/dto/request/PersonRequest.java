package com.bodriopopcorn.dto.request;

import com.bodriopopcorn.entity.PersonRoleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PersonRequest(
		@NotBlank @Size(max = 120) String name,
		@NotNull PersonRoleType roleType
) {
}
