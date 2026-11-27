ALTER TABLE titles
    ADD COLUMN tmdb_id BIGINT NULL,
    ADD COLUMN tmdb_media_type VARCHAR(5) NULL,
    ADD CONSTRAINT uk_titles_tmdb UNIQUE (tmdb_media_type, tmdb_id),
    ADD CONSTRAINT chk_titles_tmdb CHECK (
        (tmdb_id IS NULL AND tmdb_media_type IS NULL)
        OR (tmdb_id > 0 AND tmdb_media_type IN ('movie', 'tv'))
    );
