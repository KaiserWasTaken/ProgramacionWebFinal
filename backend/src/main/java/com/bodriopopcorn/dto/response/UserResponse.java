package com.bodriopopcorn.dto.response;

import java.time.LocalDateTime;

import com.bodriopopcorn.entity.RoleName;

public record UserResponse(
		Long id,
		String email,
		String fullName,
		String phone,
		String avatarUrl,
		RoleName role,
		boolean enabled,
		LocalDateTime createdAt
) {
}
