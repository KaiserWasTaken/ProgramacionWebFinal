package com.bodriopopcorn.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bodriopopcorn.entity.WatchlistItem;
import com.bodriopopcorn.entity.WatchlistItemId;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, WatchlistItemId> {

	List<WatchlistItem> findByUser_Id(Long userId);

	@EntityGraph(attributePaths = "title")
	Page<WatchlistItem> findByUser_Id(Long userId, Pageable pageable);

	void deleteByUser_Id(Long userId);
}
