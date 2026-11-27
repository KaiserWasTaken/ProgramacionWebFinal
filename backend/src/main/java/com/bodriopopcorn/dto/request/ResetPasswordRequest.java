package com.bodriopopcorn.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
		@NotBlank @Size(max = 100) String token,
		@NotBlank
		@Pattern(
				regexp = RegisterRequest.PASSWORD_PATTERN,
				message = "must contain at least 8 characters, one uppercase letter, one number, and one special character"
		)
		String password
) {
}
