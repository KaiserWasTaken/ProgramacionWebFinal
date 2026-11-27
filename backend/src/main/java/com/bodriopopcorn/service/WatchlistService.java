package com.bodriopopcorn.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodriopopcorn.dto.response.TitleSummaryResponse;
import com.bodriopopcorn.dto.response.WatchlistResponse;
import com.bodriopopcorn.entity.Title;
import com.bodriopopcorn.entity.User;
import com.bodriopopcorn.entity.WatchlistItem;
import com.bodriopopcorn.entity.WatchlistItemId;
import com.bodriopopcorn.exception.ResourceNotFoundException;
import com.bodriopopcorn.repository.TitleRepository;
import com.bodriopopcorn.repository.UserRepository;
import com.bodriopopcorn.repository.WatchlistItemRepository;

@Service
public class WatchlistService {

	private final WatchlistItemRepository watchlistRepository;
	private final UserRepository userRepository;
	private final TitleRepository titleRepository;

	public WatchlistService(
			WatchlistItemRepository watchlistRepository,
			UserRepository userRepository,
			TitleRepository titleRepository
	) {
		this.watchlistRepository = watchlistRepository;
		this.userRepository = userRepository;
		this.titleRepository = titleRepository;
	}

	@Transactional(readOnly = true)
	public Page<WatchlistResponse> list(String email, Pageable pageable) {
		PageableSupport.validate(pageable, "addedAt");
		User user = findUser(email);
		return watchlistRepository.findByUser_Id(user.getId(), pageable).map(this::toResponse);
	}

	@Transactional
	public WatchlistResponse add(String email, Long titleId) {
		User user = findUser(email);
		Title title = titleRepository.findById(titleId)
				.orElseThrow(() -> new ResourceNotFoundException("Title", titleId));
		WatchlistItemId id = new WatchlistItemId(user.getId(), titleId);
		if (watchlistRepository.existsById(id)) {
			throw new IllegalArgumentException("This title is already in your watchlist");
		}
		return toResponse(watchlistRepository.save(new WatchlistItem(user, title)));
	}

	@Transactional
	public void remove(String email, Long titleId) {
		User user = findUser(email);
		WatchlistItemId id = new WatchlistItemId(user.getId(), titleId);
		if (!watchlistRepository.existsById(id)) {
			throw new ResourceNotFoundException("Watchlist item", titleId);
		}
		watchlistRepository.deleteById(id);
	}

	private User findUser(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", email));
	}

	private WatchlistResponse toResponse(WatchlistItem item) {
		Title title = item.getTitle();
		return new WatchlistResponse(
				new TitleSummaryResponse(
						title.getId(),
						title.getName(),
						title.getType(),
						title.getAvgRating(),
						title.getPosterUrl()),
				item.getAddedAt());
	}
}
