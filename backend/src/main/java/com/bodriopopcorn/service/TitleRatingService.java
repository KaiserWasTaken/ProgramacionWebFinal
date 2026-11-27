package com.bodriopopcorn.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodriopopcorn.entity.ReviewStatus;
import com.bodriopopcorn.entity.Title;
import com.bodriopopcorn.exception.ResourceNotFoundException;
import com.bodriopopcorn.repository.ReviewRepository;
import com.bodriopopcorn.repository.TitleRepository;

@Service
public class TitleRatingService {

	private final ReviewRepository reviewRepository;
	private final TitleRepository titleRepository;

	public TitleRatingService(ReviewRepository reviewRepository, TitleRepository titleRepository) {
		this.reviewRepository = reviewRepository;
		this.titleRepository = titleRepository;
	}

	@Transactional
	public void recalculate(Long titleId) {
		Double average = reviewRepository.findAverageRatingByTitleIdAndStatus(titleId, ReviewStatus.APPROVED);
		Title title = titleRepository.findById(titleId)
				.orElseThrow(() -> new ResourceNotFoundException("Title", titleId));
		title.setAvgRating(average == null
				? BigDecimal.ZERO.setScale(2)
				: BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP));
	}
}
