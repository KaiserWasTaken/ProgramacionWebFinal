package com.bodriopopcorn.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class WatchlistItemId implements Serializable {

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "title_id")
	private Long titleId;

	protected WatchlistItemId() {
	}

	public WatchlistItemId(Long userId, Long titleId) {
		this.userId = userId;
		this.titleId = titleId;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getTitleId() {
		return titleId;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof WatchlistItemId that)) {
			return false;
		}
		return Objects.equals(userId, that.userId) && Objects.equals(titleId, that.titleId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, titleId);
	}
}
