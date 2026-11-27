package com.bodriopopcorn.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodriopopcorn.dto.request.ModerationRequest;
import com.bodriopopcorn.dto.request.ReviewRequest;
import com.bodriopopcorn.dto.request.ReviewUpdateRequest;
import com.bodriopopcorn.dto.response.ReviewResponse;
import com.bodriopopcorn.entity.Review;
import com.bodriopopcorn.entity.ReviewStatus;
import com.bodriopopcorn.entity.Title;
import com.bodriopopcorn.entity.User;
import com.bodriopopcorn.exception.ResourceNotFoundException;
import com.bodriopopcorn.repository.ReviewRepository;
import com.bodriopopcorn.repository.TitleRepository;
import com.bodriopopcorn.repository.UserRepository;

@Service
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final TitleRepository titleRepository;
	private final UserRepository userRepository;
	private final TitleRatingService titleRatingService;
	private final WhatsAppService whatsAppService;
	private final NotificationScheduler notificationScheduler;

	public ReviewService(
			ReviewRepository reviewRepository,
			TitleRepository titleRepository,
			UserRepository userRepository,
			TitleRatingService titleRatingService,
			WhatsAppService whatsAppService,
			NotificationScheduler notificationScheduler
	) {
		this.reviewRepository = reviewRepository;
		this.titleRepository = titleRepository;
		this.userRepository = userRepository;
		this.titleRatingService = titleRatingService;
		this.whatsAppService = whatsAppService;
		this.notificationScheduler = notificationScheduler;
	}

	@Transactional(readOnly = true)
	public Page<ReviewResponse> listForModeration(ReviewStatus status, String search, Pageable pageable) {
		PageableSupport.validate(pageable, "id", "rating", "status", "createdAt");
		return reviewRepository.findAll(ReviewSpecifications.withFilters(status, search), pageable)
				.map(this::toResponse);
	}

	@Transactional(readOnly = true)
	public Page<ReviewResponse> listMine(String email, Pageable pageable) {
		PageableSupport.validate(pageable, "id", "rating", "status", "createdAt");
		User user = findUser(email);
		return reviewRepository.findByUser_Id(user.getId(), pageable).map(this::toResponse);
	}

	@Transactional(readOnly = true)
	public ReviewResponse get(Long id, String email, boolean privileged) {
		Review review = findReview(id);
		if (!privileged && !review.getUser().getEmail().equals(email)) {
			throw new AccessDeniedException("You can only view your own reviews");
		}
		return toResponse(review);
	}

	@Transactional(readOnly = true)
	public Page<ReviewResponse> listApprovedForTitle(Long titleId, Pageable pageable) {
		PageableSupport.validate(pageable, "id", "rating", "createdAt");
		if (!titleRepository.existsById(titleId)) {
			throw new ResourceNotFoundException("Title", titleId);
		}
		return reviewRepository.findByTitle_IdAndStatus(titleId, ReviewStatus.APPROVED, pageable)
				.map(this::toResponse);
	}

	@Transactional
	public ReviewResponse create(String email, ReviewRequest request) {
		User user = findUser(email);
		Title title = titleRepository.findById(request.titleId())
				.orElseThrow(() -> new ResourceNotFoundException("Title", request.titleId()));
		if (reviewRepository.existsByUser_IdAndTitle_Id(user.getId(), title.getId())) {
			throw new IllegalArgumentException("A review already exists for this title");
		}
		return toResponse(reviewRepository.save(new Review(
				user, title, request.rating(), request.comment().trim())));
	}

	@Transactional
	public ReviewResponse update(Long id, String email, boolean administrator, ReviewUpdateRequest request) {
		Review review = findReview(id);
		if (!administrator && !review.getUser().getEmail().equals(email)) {
			throw new AccessDeniedException("You can only edit your own reviews");
		}
		review.setRating(request.rating());
		review.setComment(request.comment().trim());
		review.setStatus(ReviewStatus.PENDING);
		reviewRepository.saveAndFlush(review);
		titleRatingService.recalculate(review.getTitle().getId());
		return toResponse(review);
	}

	@Transactional
	public ReviewResponse moderate(Long id, ModerationRequest request) {
		Review review = findReview(id);
		ReviewStatus previousStatus = review.getStatus();
		review.setStatus(request.status() == ModerationRequest.ModerationDecision.APPROVED
				? ReviewStatus.APPROVED
				: ReviewStatus.REJECTED);
		reviewRepository.saveAndFlush(review);
		titleRatingService.recalculate(review.getTitle().getId());
		if (previousStatus != review.getStatus()) {
			notificationScheduler.afterCommit(() -> whatsAppService.sendReviewStatus(
					review.getUser().getPhone(), review.getTitle().getName(), review.getStatus()));
		}
		return toResponse(review);
	}

	@Transactional
	public void delete(Long id, String email, boolean administrator) {
		Review review = findReview(id);
		if (!administrator && !review.getUser().getEmail().equals(email)) {
			throw new AccessDeniedException("You can only delete your own reviews");
		}
		Long titleId = review.getTitle().getId();
		reviewRepository.delete(review);
		reviewRepository.flush();
		titleRatingService.recalculate(titleId);
	}

	private User findUser(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User", email));
	}

	private Review findReview(Long id) {
		return reviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review", id));
	}

	private ReviewResponse toResponse(Review review) {
		return new ReviewResponse(
				review.getId(),
				review.getTitle().getId(),
				review.getTitle().getName(),
				review.getUser().getEmail(),
				review.getRating(),
				review.getComment(),
				review.getStatus(),
				review.getCreatedAt()
		);
	}
}
