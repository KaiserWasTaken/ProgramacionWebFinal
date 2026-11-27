package com.bodriopopcorn.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.bodriopopcorn.service.AvatarService;
import com.bodriopopcorn.dto.request.UserUpdateRequest;
import com.bodriopopcorn.dto.request.UserProfileUpdateRequest;
import com.bodriopopcorn.dto.response.UserResponse;
import com.bodriopopcorn.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final AvatarService avatarService;

	public UserController(UserService userService, AvatarService avatarService) {
		this.userService = userService;
		this.avatarService = avatarService;
	}

	@GetMapping("/me/avatar")
	public ResponseEntity<byte[]> getAvatar(Authentication authentication) {
		var avatar = avatarService.get(authentication.getName());
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(avatar.contentType())).body(avatar.image());
	}

	@PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Void> uploadAvatar(Authentication authentication, @RequestPart("image") MultipartFile image)
			throws java.io.IOException {
		avatarService.upload(authentication.getName(), image);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public Page<UserResponse> list(
			@RequestParam(required = false) String search,
			@PageableDefault(size = 20, sort = "email", direction = Sort.Direction.ASC) Pageable pageable
	) {
		return userService.list(search, pageable);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public UserResponse get(@PathVariable Long id) {
		return userService.get(id);
	}

	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
	public UserResponse getCurrent(Authentication authentication) {
		return userService.getCurrent(authentication.getName());
	}

	@PutMapping("/me")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
	public UserResponse updateCurrent(
			Authentication authentication,
			@Valid @RequestBody UserProfileUpdateRequest request
	) {
		return userService.updateCurrent(authentication.getName(), request);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public UserResponse update(
			@PathVariable Long id,
			@Valid @RequestBody UserUpdateRequest request
	) {
		return userService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id, Authentication authentication) {
		userService.delete(id, authentication.getName());
	}
}
