package com.bodriopopcorn.dto.response;

import java.time.LocalDateTime;

public record WatchlistResponse(TitleSummaryResponse title, LocalDateTime addedAt) {
}
