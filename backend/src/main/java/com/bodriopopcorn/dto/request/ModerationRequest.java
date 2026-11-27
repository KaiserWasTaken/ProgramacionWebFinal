package com.bodriopopcorn.dto.request;

import jakarta.validation.constraints.NotNull;

public record ModerationRequest(@NotNull ModerationDecision status) {
	public enum ModerationDecision {
		APPROVED,
		REJECTED
	}
}
