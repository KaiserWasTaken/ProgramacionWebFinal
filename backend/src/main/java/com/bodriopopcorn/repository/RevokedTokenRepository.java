package com.bodriopopcorn.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodriopopcorn.entity.RevokedToken;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {

	int deleteByExpiresAtBefore(Instant instant);
}
