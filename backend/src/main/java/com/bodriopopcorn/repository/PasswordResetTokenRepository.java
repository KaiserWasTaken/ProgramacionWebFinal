package com.bodriopopcorn.repository;

import java.time.Instant;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.bodriopopcorn.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<PasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);

	void deleteByUser_IdAndUsedFalse(Long userId);

	void deleteByExpiresAtBefore(Instant instant);
}
