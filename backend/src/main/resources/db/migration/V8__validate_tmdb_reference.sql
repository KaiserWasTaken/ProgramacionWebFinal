ALTER TABLE titles
    DROP CHECK chk_titles_tmdb,
    ADD CONSTRAINT chk_titles_tmdb CHECK (
        (tmdb_id IS NULL AND tmdb_media_type IS NULL)
        OR (tmdb_id IS NOT NULL AND tmdb_media_type IS NOT NULL
            AND tmdb_id > 0 AND tmdb_media_type IN ('movie', 'tv'))
    );
