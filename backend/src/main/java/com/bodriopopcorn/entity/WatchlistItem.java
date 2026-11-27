package com.bodriopopcorn.entity;

import java.time.LocalDateTime;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "watchlist_items")
public class WatchlistItem {

	@EmbeddedId
	private WatchlistItemId id;

	@MapsId("userId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@MapsId("titleId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "title_id", nullable = false)
	private Title title;

	@CreationTimestamp
	@jakarta.persistence.Column(name = "added_at", nullable = false, updatable = false)
	private LocalDateTime addedAt;

	protected WatchlistItem() {
	}

	public WatchlistItem(User user, Title title) {
		this.user = user;
		this.title = title;
		this.id = new WatchlistItemId(user.getId(), title.getId());
	}

	public WatchlistItemId getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public Title getTitle() {
		return title;
	}

	public LocalDateTime getAddedAt() {
		return addedAt;
	}
}
