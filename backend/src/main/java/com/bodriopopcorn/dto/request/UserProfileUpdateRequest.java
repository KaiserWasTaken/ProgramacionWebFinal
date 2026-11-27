package com.bodriopopcorn.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserProfileUpdateRequest(
		@NotBlank @Size(max = 120) String fullName,
		@Pattern(regexp = "^$|^\\+[1-9][0-9]{1,14}$",
				message = "Ingresa un teléfono internacional con + y código de país, por ejemplo +525512345678.") String phone,
		@Size(max = 500) String avatarUrl
) {
}
