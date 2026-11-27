CREATE TABLE roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_name (name),
    CONSTRAINT chk_roles_name CHECK (name IN ('ADMIN', 'MODERATOR', 'USER'))
) ENGINE=InnoDB;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    phone VARCHAR(30),
    avatar_url VARCHAR(500),
    role_id BIGINT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    KEY idx_users_role_id (role_id),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB;

CREATE TABLE titles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type VARCHAR(10) NOT NULL,
    name VARCHAR(180) NOT NULL,
    synopsis TEXT NOT NULL,
    release_year INT NOT NULL,
    duration_minutes INT,
    seasons INT,
    poster_url VARCHAR(500),
    avg_rating DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_titles_name (name),
    CONSTRAINT chk_titles_type CHECK (type IN ('MOVIE', 'SERIES')),
    CONSTRAINT chk_titles_avg_rating CHECK (avg_rating >= 0.00 AND avg_rating <= 5.00),
    CONSTRAINT chk_titles_release_year CHECK (release_year BETWEEN 1888 AND 2100)
) ENGINE=InnoDB;

CREATE TABLE genres (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(60) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_genres_name (name)
) ENGINE=InnoDB;

CREATE TABLE title_genres (
    title_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (title_id, genre_id),
    KEY idx_title_genres_genre_id (genre_id),
    CONSTRAINT fk_title_genres_title FOREIGN KEY (title_id) REFERENCES titles (id) ON DELETE CASCADE,
    CONSTRAINT fk_title_genres_genre FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE reviews (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment VARCHAR(2000) NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_reviews_user_title (user_id, title_id),
    KEY idx_reviews_title_status (title_id, status),
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_title FOREIGN KEY (title_id) REFERENCES titles (id) ON DELETE CASCADE,
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
) ENGINE=InnoDB;

CREATE TABLE watchlist_items (
    user_id BIGINT NOT NULL,
    title_id BIGINT NOT NULL,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, title_id),
    KEY idx_watchlist_title_id (title_id),
    CONSTRAINT fk_watchlist_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_watchlist_title FOREIGN KEY (title_id) REFERENCES titles (id) ON DELETE CASCADE
) ENGINE=InnoDB;
