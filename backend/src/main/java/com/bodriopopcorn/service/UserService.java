package com.bodriopopcorn.service;

import java.util.Locale;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodriopopcorn.dto.request.UserUpdateRequest;
import com.bodriopopcorn.dto.request.UserProfileUpdateRequest;
import com.bodriopopcorn.dto.response.UserResponse;
import com.bodriopopcorn.entity.RoleName;
import com.bodriopopcorn.entity.ReviewStatus;
import com.bodriopopcorn.entity.User;
import com.bodriopopcorn.exception.ResourceNotFoundException;
import com.bodriopopcorn.repository.RoleRepository;
import com.bodriopopcorn.repository.ReviewRepository;
import com.bodriopopcorn.repository.UserRepository;
import com.bodriopopcorn.repository.WatchlistItemRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final ReviewRepository reviewRepository;
	private final WatchlistItemRepository watchlistRepository;
	private final TitleRatingService titleRatingService;

	public UserService(
			UserRepository userRepository,
			RoleRepository roleRepository,
			ReviewRepository reviewRepository,
			WatchlistItemRepository watchlistRepository,
			TitleRatingService titleRatingService
	) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.reviewRepository = reviewRepository;
		this.watchlistRepository = watchlistRepository;
		this.titleRatingService = titleRatingService;
	}

	@Transactional(readOnly = true)
	public Page<UserResponse> list(String search, Pageable pageable) {
		PageableSupport.validate(pageable, "id", "email", "fullName", "createdAt", "enabled");
		return userRepository.findAll(UserSpecifications.withSearch(search), pageable).map(this::toResponse);
	}

	@Transactional(readOnly = true)
	public UserResponse get(Long id) {
		return toResponse(find(id));
	}

	@Transactional(readOnly = true)
	public UserResponse getCurrent(String email) {
		return toResponse(findByEmail(email));
	}

	@Transactional
	public UserResponse updateCurrent(String email, UserProfileUpdateRequest request) {
		User user = findByEmail(email);
		user.setFullName(request.fullName().trim());
		user.setPhone(request.phone() == null ? null : request.phone().trim());
		user.setAvatarUrl(request.avatarUrl() == null ? null : request.avatarUrl().trim());
		return toResponse(user);
	}

	@Transactional
	public UserResponse update(Long id, UserUpdateRequest request) {
		User user = find(id);
		String email = request.email().trim().toLowerCase(Locale.ROOT);
		if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("A user with this email already exists");
		}
		if (user.getRole().getName() == RoleName.ADMIN && user.isEnabled()
				&& (request.role() != RoleName.ADMIN || !request.enabled())
				&& userRepository.countByRole_NameAndEnabledTrue(RoleName.ADMIN) <= 1) {
			throw new IllegalArgumentException("The last enabled administrator cannot be demoted or disabled");
		}
		user.setEmail(email);
		user.setFullName(request.fullName().trim());
		user.setPhone(request.phone() == null ? null : request.phone().trim());
		user.setAvatarUrl(request.avatarUrl() == null ? null : request.avatarUrl().trim());
		user.setRole(roleRepository.findByName(request.role())
				.orElseThrow(() -> new IllegalStateException("Configured role was not found: " + request.role())));
		user.setEnabled(request.enabled());
		return toResponse(user);
	}

	@Transactional
	public void delete(Long id, String administratorEmail) {
		User user = find(id);
		if (Objects.equals(user.getEmail(), administratorEmail)) {
			throw new AccessDeniedException("Administrators cannot delete their own account");
		}
		if (user.getRole().getName() == RoleName.ADMIN && user.isEnabled()
				&& userRepository.countByRole_NameAndEnabledTrue(RoleName.ADMIN) <= 1) {
			throw new IllegalArgumentException("The last enabled administrator cannot be deleted");
		}
		var affectedTitles = reviewRepository.findDistinctTitleIdsByUserIdAndStatus(
				user.getId(), ReviewStatus.APPROVED);
		reviewRepository.deleteByUser_Id(user.getId());
		watchlistRepository.deleteByUser_Id(user.getId());
		userRepository.delete(user);
		userRepository.flush();
		affectedTitles.forEach(titleRatingService::recalculate);
	}

	private User find(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
	}

	private User findByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", email));
	}

	private UserResponse toResponse(User user) {
		return new UserResponse(
				user.getId(),
				user.getEmail(),
				user.getFullName(),
				user.getPhone(),
				user.getAvatarUrl(),
				user.getRole().getName(),
				user.isEnabled(),
				user.getCreatedAt()
		);
	}
}
