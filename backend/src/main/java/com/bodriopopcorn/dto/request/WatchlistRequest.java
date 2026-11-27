package com.bodriopopcorn.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WatchlistRequest(@NotNull @Positive Long titleId) {
}
