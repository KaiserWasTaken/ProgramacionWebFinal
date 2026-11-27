package com.bodriopopcorn.dto.request;

import com.bodriopopcorn.entity.RoleName;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
		@NotBlank @Email @Size(max = 254) String email,
		@NotBlank @Size(max = 120) String fullName,
		@Size(max = 30) String phone,
		@Size(max = 500) String avatarUrl,
		@NotNull RoleName role,
		@NotNull Boolean enabled
) {
}
