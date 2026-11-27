CREATE TABLE user_avatars (
    user_id BIGINT NOT NULL,
    content_type VARCHAR(20) NOT NULL,
    image MEDIUMBLOB NOT NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT fk_user_avatars_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;
