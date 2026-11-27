INSERT INTO roles (id, name) VALUES
(1, 'ADMIN'),
(2, 'MODERATOR'),
(3, 'USER');

INSERT INTO genres (id, name) VALUES
(1, 'Action'),
(2, 'Adventure'),
(3, 'Animation'),
(4, 'Comedy'),
(5, 'Crime'),
(6, 'Documentary'),
(7, 'Drama'),
(8, 'Family'),
(9, 'Fantasy'),
(10, 'Horror'),
(11, 'Mystery'),
(12, 'Musical'),
(13, 'Romance'),
(14, 'Science Fiction'),
(15, 'Thriller');

INSERT INTO users (id, email, password_hash, full_name, phone, avatar_url, role_id, enabled) VALUES
(1, 'admin@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'BodrioPopcorn Admin', NULL, NULL, 1, TRUE),
(2, 'moderator@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'BodrioPopcorn Moderator', NULL, NULL, 2, TRUE),
(3, 'user01@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'Alex Rivera', NULL, NULL, 3, TRUE),
(4, 'user02@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'Sam Torres', NULL, NULL, 3, TRUE),
(5, 'user03@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'Jamie Cruz', NULL, NULL, 3, TRUE),
(6, 'user04@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'Taylor Flores', NULL, NULL, 3, TRUE),
(7, 'user05@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'Morgan Reyes', NULL, NULL, 3, TRUE),
(8, 'user06@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'Casey Vega', NULL, NULL, 3, TRUE),
(9, 'user07@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'Jordan Luna', NULL, NULL, 3, TRUE),
(10, 'user08@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'Riley Soto', NULL, NULL, 3, TRUE),
(11, 'user09@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'Avery Castillo', NULL, NULL, 3, TRUE),
(12, 'user10@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'Quinn Moreno', NULL, NULL, 3, TRUE),
(13, 'user11@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'Drew Navarro', NULL, NULL, 3, TRUE),
(14, 'user12@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'Skyler Ortiz', NULL, NULL, 3, TRUE),
(15, 'user13@bodriopopcorn.test', '$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK', 'Robin Medina', NULL, NULL, 3, TRUE);

INSERT INTO titles (id, type, name, synopsis, release_year, duration_minutes, seasons, poster_url, avg_rating) VALUES
(1, 'MOVIE', 'The Last Lantern', 'A lighthouse keeper follows a signal that appears only during storms.', 2024, 112, NULL, NULL, 0.00),
(2, 'SERIES', 'Orbit Station', 'A small crew keeps a remote research station alive at the edge of known space.', 2023, NULL, 2, NULL, 0.00),
(3, 'MOVIE', 'Paper Kingdom', 'Two siblings discover a hidden city inside their grandfather''s unfinished map.', 2022, 98, NULL, NULL, 0.00),
(4, 'SERIES', 'Northbound', 'A missing-persons investigator returns to the town where her family disappeared.', 2021, NULL, 3, NULL, 0.00),
(5, 'MOVIE', 'Midnight Recipe', 'A night-shift cook enters a neighborhood competition to save a family diner.', 2020, 105, NULL, NULL, 0.00),
(6, 'SERIES', 'Signal Lost', 'Radio operators investigate strange transmissions from an abandoned valley.', 2024, NULL, 1, NULL, 0.00),
(7, 'MOVIE', 'Quiet Harbor', 'A retired musician reconnects with her community while restoring a seaside theater.', 2019, 121, NULL, NULL, 0.00),
(8, 'SERIES', 'The Glass Archive', 'Archivists uncover a conspiracy hidden in records of a fictional city.', 2022, NULL, 2, NULL, 0.00),
(9, 'MOVIE', 'Wild Circuit', 'Young mechanics build an electric race car to keep their workshop open.', 2023, 109, NULL, NULL, 0.00),
(10, 'SERIES', 'Garden of Echoes', 'A botanist studies plants that react to memories in a remote greenhouse.', 2020, NULL, 2, NULL, 0.00),
(11, 'MOVIE', 'Sunday at Nine', 'Three neighbors turn a weekly breakfast into an unexpected friendship.', 2018, 94, NULL, NULL, 0.00),
(12, 'SERIES', 'Red Meridian', 'A disaster-response team coordinates rescues across a mountain region.', 2021, NULL, 4, NULL, 0.00),
(13, 'MOVIE', 'Borrowed Time', 'A watchmaker finds a pocket watch that lets its owner revisit one ordinary day.', 2025, 116, NULL, NULL, 0.00),
(14, 'SERIES', 'Blue Note Avenue', 'Musicians at a community venue work together to keep it independent.', 2019, NULL, 3, NULL, 0.00),
(15, 'MOVIE', 'A Map of Small Things', 'A teacher and her students document the overlooked stories of their district.', 2022, 101, NULL, NULL, 0.00);

INSERT INTO title_genres (title_id, genre_id) VALUES
(1, 1), (1, 15),
(2, 14), (2, 7),
(3, 2), (3, 9),
(4, 11), (4, 15),
(5, 4), (5, 7),
(6, 10), (6, 11),
(7, 7), (7, 12),
(8, 5), (8, 11),
(9, 1), (9, 7),
(10, 9), (10, 11),
(11, 4), (11, 13),
(12, 1), (12, 7),
(13, 14), (13, 13),
(14, 7), (14, 12),
(15, 6), (15, 7);

INSERT INTO reviews (id, user_id, title_id, rating, comment, status) VALUES
(1, 1, 1, 5, 'A thoughtful story with a memorable setting.', 'APPROVED'),
(2, 2, 2, 4, 'The crew dynamics make the science-fiction premise work.', 'APPROVED'),
(3, 3, 3, 5, 'A warm adventure with a clever visual idea.', 'APPROVED'),
(4, 4, 4, 4, 'Strong mystery and a compelling lead character.', 'PENDING'),
(5, 5, 5, 3, 'An easygoing comedy with an appealing cast.', 'APPROVED'),
(6, 6, 6, 4, 'The atmosphere and sound design stand out.', 'PENDING'),
(7, 7, 7, 5, 'A moving story about making art together.', 'APPROVED'),
(8, 8, 8, 4, 'A layered mystery that rewards close attention.', 'REJECTED'),
(9, 9, 9, 4, 'An energetic story with an optimistic message.', 'APPROVED'),
(10, 10, 10, 3, 'An unusual premise with a calm pace.', 'PENDING'),
(11, 11, 11, 5, 'A small-scale story that feels sincere.', 'APPROVED'),
(12, 12, 12, 4, 'The rescue stories are engaging and varied.', 'PENDING'),
(13, 13, 13, 5, 'A charming take on second chances.', 'APPROVED'),
(14, 14, 14, 4, 'The music and community setting are the highlights.', 'APPROVED'),
(15, 15, 15, 5, 'A kind, observant story about a neighborhood.', 'PENDING');

UPDATE titles t
SET avg_rating = COALESCE(
    (SELECT ROUND(AVG(r.rating), 2) FROM reviews r WHERE r.title_id = t.id AND r.status = 'APPROVED'),
    0.00
);

INSERT INTO watchlist_items (user_id, title_id) VALUES
(3, 4), (4, 5), (5, 6), (6, 7), (7, 8),
(8, 9), (9, 10), (10, 11), (11, 12), (12, 13),
(13, 14), (14, 15), (15, 1);
