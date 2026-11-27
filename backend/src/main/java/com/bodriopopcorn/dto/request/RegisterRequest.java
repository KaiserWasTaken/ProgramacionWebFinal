package com.bodriopopcorn.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Email @Size(max = 254) String email,
		@NotBlank
		@Pattern(
				regexp = PASSWORD_PATTERN,
				message = "must contain at least 8 characters, one uppercase letter, one number, and one special character"
		)
		String password,
		@NotBlank @Size(max = 120) String fullName
) {
	public static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,72}$";
}
