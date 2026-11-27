CREATE TABLE revoked_tokens (
    jti VARCHAR(36) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    PRIMARY KEY (jti),
    KEY idx_revoked_tokens_expires_at (expires_at)
) ENGINE=InnoDB;
