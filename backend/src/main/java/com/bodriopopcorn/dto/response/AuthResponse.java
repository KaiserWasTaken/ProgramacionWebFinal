package com.bodriopopcorn.dto.response;

import java.time.Instant;

public record AuthResponse(String accessToken, String tokenType, Instant expiresAt, String email, String role) {
}
