-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: bodrio_demo_export_ef25f0f0
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `script` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','create initial schema','SQL','V1__create_initial_schema.sql',-965316693,'root','2026-11-27 22:20:10',158,1),(2,'2','seed demo data','SQL','V2__seed_demo_data.sql',-1732245755,'root','2026-11-27 22:20:10',29,1),(3,'3','create revoked tokens','SQL','V3__create_revoked_tokens.sql',1305015491,'root','2026-11-27 22:20:10',44,1),(4,'4','create people and title credits','SQL','V4__create_people_and_title_credits.sql',2083264008,'root','2026-11-27 22:20:10',39,1),(5,'5','seed people and title cast','SQL','V5__seed_people_and_title_cast.sql',-1641483553,'root','2026-11-27 22:20:10',19,1),(6,'6','create password reset tokens','SQL','V6__create_password_reset_tokens.sql',-1857008240,'root','2026-11-27 22:20:10',24,1),(7,'7','link tmdb titles','SQL','V7__link_tmdb_titles.sql',1141366690,'root','2026-11-27 22:20:10',101,1),(8,'8','validate tmdb reference','SQL','V8__validate_tmdb_reference.sql',637248459,'root','2026-11-27 22:20:10',80,1),(9,'9','link tmdb people','SQL','V9__link_tmdb_people.sql',-1980338377,'root','2026-11-27 22:20:10',80,1),(10,'10','add user avatars','SQL','V10__add_user_avatars.sql',621851331,'root','2026-11-27 22:20:10',17,1),(11,'11','add google subject','SQL','V11__add_google_subject.sql',1226144708,'root','2026-11-27 22:20:11',77,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `genres`
--

DROP TABLE IF EXISTS `genres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `genres` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_genres_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genres`
--

LOCK TABLES `genres` WRITE;
/*!40000 ALTER TABLE `genres` DISABLE KEYS */;
INSERT INTO `genres` VALUES (1,'Action'),(2,'Adventure'),(3,'Animation'),(4,'Comedy'),(5,'Crime'),(6,'Documentary'),(7,'Drama'),(8,'Family'),(9,'Fantasy'),(10,'Horror'),(12,'Musical'),(11,'Mystery'),(13,'Romance'),(14,'Science Fiction'),(15,'Thriller');
/*!40000 ALTER TABLE `genres` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `token_hash` char(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `expires_at` timestamp NOT NULL,
  `used` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_password_reset_tokens_hash` (`token_hash`),
  KEY `idx_password_reset_tokens_user_used` (`user_id`,`used`),
  KEY `idx_password_reset_tokens_expires_at` (`expires_at`),
  CONSTRAINT `fk_password_reset_tokens_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_tokens`
--

LOCK TABLES `password_reset_tokens` WRITE;
/*!40000 ALTER TABLE `password_reset_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `people`
--

DROP TABLE IF EXISTS `people`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `people` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role_type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tmdb_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_people_tmdb_role` (`tmdb_id`,`role_type`),
  KEY `idx_people_name` (`name`),
  CONSTRAINT `chk_people_role_type` CHECK ((`role_type` in (_utf8mb4'ACTOR',_utf8mb4'DIRECTOR')))
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `people`
--

LOCK TABLES `people` WRITE;
/*!40000 ALTER TABLE `people` DISABLE KEYS */;
INSERT INTO `people` VALUES (1,'Elena Varela','ACTOR',NULL),(2,'Mateo Rios','ACTOR',NULL),(3,'Lucia Ferrer','ACTOR',NULL),(4,'Bruno Salcedo','ACTOR',NULL),(5,'Isla Campos','ACTOR',NULL),(6,'Tomas Gil','ACTOR',NULL),(7,'Nora Vidal','ACTOR',NULL),(8,'Ciro Mendoza','ACTOR',NULL),(9,'Mara Solano','ACTOR',NULL),(10,'Teo Paredes','ACTOR',NULL),(11,'Clara Neri','DIRECTOR',NULL),(12,'Dario Mena','DIRECTOR',NULL),(13,'Alma Reyes','DIRECTOR',NULL),(14,'Rafael Costa','DIRECTOR',NULL),(15,'Ines Duarte','DIRECTOR',NULL);
/*!40000 ALTER TABLE `people` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title_id` bigint NOT NULL,
  `rating` int NOT NULL,
  `comment` varchar(2000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reviews_user_title` (`user_id`,`title_id`),
  KEY `idx_reviews_title_status` (`title_id`,`status`),
  CONSTRAINT `fk_reviews_title` FOREIGN KEY (`title_id`) REFERENCES `titles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reviews_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_reviews_rating` CHECK ((`rating` between 1 and 5)),
  CONSTRAINT `chk_reviews_status` CHECK ((`status` in (_utf8mb4'PENDING',_utf8mb4'APPROVED',_utf8mb4'REJECTED')))
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,1,1,5,'A thoughtful story with a memorable setting.','APPROVED','2026-11-27 22:20:10'),(2,2,2,4,'The crew dynamics make the science-fiction premise work.','APPROVED','2026-11-27 22:20:10'),(3,3,3,5,'A warm adventure with a clever visual idea.','APPROVED','2026-11-27 22:20:10'),(4,4,4,4,'Strong mystery and a compelling lead character.','PENDING','2026-11-27 22:20:10'),(5,5,5,3,'An easygoing comedy with an appealing cast.','APPROVED','2026-11-27 22:20:10'),(6,6,6,4,'The atmosphere and sound design stand out.','PENDING','2026-11-27 22:20:10'),(7,7,7,5,'A moving story about making art together.','APPROVED','2026-11-27 22:20:10'),(8,8,8,4,'A layered mystery that rewards close attention.','REJECTED','2026-11-27 22:20:10'),(9,9,9,4,'An energetic story with an optimistic message.','APPROVED','2026-11-27 22:20:10'),(10,10,10,3,'An unusual premise with a calm pace.','PENDING','2026-11-27 22:20:10'),(11,11,11,5,'A small-scale story that feels sincere.','APPROVED','2026-11-27 22:20:10'),(12,12,12,4,'The rescue stories are engaging and varied.','PENDING','2026-11-27 22:20:10'),(13,13,13,5,'A charming take on second chances.','APPROVED','2026-11-27 22:20:10'),(14,14,14,4,'The music and community setting are the highlights.','APPROVED','2026-11-27 22:20:10'),(15,15,15,5,'A kind, observant story about a neighborhood.','PENDING','2026-11-27 22:20:10');
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `revoked_tokens`
--

DROP TABLE IF EXISTS `revoked_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `revoked_tokens` (
  `jti` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `expires_at` timestamp NOT NULL,
  PRIMARY KEY (`jti`),
  KEY `idx_revoked_tokens_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `revoked_tokens`
--

LOCK TABLES `revoked_tokens` WRITE;
/*!40000 ALTER TABLE `revoked_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `revoked_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_roles_name` (`name`),
  CONSTRAINT `chk_roles_name` CHECK ((`name` in (_utf8mb4'ADMIN',_utf8mb4'MODERATOR',_utf8mb4'USER')))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ADMIN'),(2,'MODERATOR'),(3,'USER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `title_cast`
--

DROP TABLE IF EXISTS `title_cast`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `title_cast` (
  `title_id` bigint NOT NULL,
  `person_id` bigint NOT NULL,
  `character_name` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`title_id`,`person_id`),
  KEY `idx_title_credits_person_id` (`person_id`),
  CONSTRAINT `fk_title_credits_person` FOREIGN KEY (`person_id`) REFERENCES `people` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_title_credits_title` FOREIGN KEY (`title_id`) REFERENCES `titles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `title_cast`
--

LOCK TABLES `title_cast` WRITE;
/*!40000 ALTER TABLE `title_cast` DISABLE KEYS */;
INSERT INTO `title_cast` VALUES (1,1,'Elena'),(2,2,'Commander Vale'),(3,3,'Mira'),(4,4,'Inspector Sol'),(5,5,'Rosa'),(6,6,'Tomas'),(7,7,'Nina'),(8,8,'Archivist Ciro'),(9,9,'Mara'),(10,10,'Teo'),(11,11,NULL),(12,12,NULL),(13,13,NULL),(14,14,NULL),(15,15,NULL);
/*!40000 ALTER TABLE `title_cast` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `title_genres`
--

DROP TABLE IF EXISTS `title_genres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `title_genres` (
  `title_id` bigint NOT NULL,
  `genre_id` bigint NOT NULL,
  PRIMARY KEY (`title_id`,`genre_id`),
  KEY `idx_title_genres_genre_id` (`genre_id`),
  CONSTRAINT `fk_title_genres_genre` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_title_genres_title` FOREIGN KEY (`title_id`) REFERENCES `titles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `title_genres`
--

LOCK TABLES `title_genres` WRITE;
/*!40000 ALTER TABLE `title_genres` DISABLE KEYS */;
INSERT INTO `title_genres` VALUES (1,1),(9,1),(12,1),(3,2),(5,4),(11,4),(8,5),(15,6),(2,7),(5,7),(7,7),(9,7),(12,7),(14,7),(15,7),(3,9),(10,9),(6,10),(4,11),(6,11),(8,11),(10,11),(7,12),(14,12),(11,13),(13,13),(2,14),(13,14),(1,15),(4,15);
/*!40000 ALTER TABLE `title_genres` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `titles`
--

DROP TABLE IF EXISTS `titles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `titles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(180) COLLATE utf8mb4_unicode_ci NOT NULL,
  `synopsis` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `release_year` int NOT NULL,
  `duration_minutes` int DEFAULT NULL,
  `seasons` int DEFAULT NULL,
  `poster_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avg_rating` decimal(3,2) NOT NULL DEFAULT '0.00',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tmdb_id` bigint DEFAULT NULL,
  `tmdb_media_type` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_titles_tmdb` (`tmdb_media_type`,`tmdb_id`),
  KEY `idx_titles_name` (`name`),
  CONSTRAINT `chk_titles_avg_rating` CHECK (((`avg_rating` >= 0.00) and (`avg_rating` <= 5.00))),
  CONSTRAINT `chk_titles_release_year` CHECK ((`release_year` between 1888 and 2100)),
  CONSTRAINT `chk_titles_tmdb` CHECK ((((`tmdb_id` is null) and (`tmdb_media_type` is null)) or ((`tmdb_id` is not null) and (`tmdb_media_type` is not null) and (`tmdb_id` > 0) and (`tmdb_media_type` in (_utf8mb4'movie',_utf8mb4'tv'))))),
  CONSTRAINT `chk_titles_type` CHECK ((`type` in (_utf8mb4'MOVIE',_utf8mb4'SERIES')))
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `titles`
--

LOCK TABLES `titles` WRITE;
/*!40000 ALTER TABLE `titles` DISABLE KEYS */;
INSERT INTO `titles` VALUES (1,'MOVIE','The Last Lantern','A lighthouse keeper follows a signal that appears only during storms.',2024,112,NULL,NULL,5.00,'2026-11-27 22:20:10',NULL,NULL),(2,'SERIES','Orbit Station','A small crew keeps a remote research station alive at the edge of known space.',2023,NULL,2,NULL,4.00,'2026-11-27 22:20:10',NULL,NULL),(3,'MOVIE','Paper Kingdom','Two siblings discover a hidden city inside their grandfather\'s unfinished map.',2022,98,NULL,NULL,5.00,'2026-11-27 22:20:10',NULL,NULL),(4,'SERIES','Northbound','A missing-persons investigator returns to the town where her family disappeared.',2021,NULL,3,NULL,0.00,'2026-11-27 22:20:10',NULL,NULL),(5,'MOVIE','Midnight Recipe','A night-shift cook enters a neighborhood competition to save a family diner.',2020,105,NULL,NULL,3.00,'2026-11-27 22:20:10',NULL,NULL),(6,'SERIES','Signal Lost','Radio operators investigate strange transmissions from an abandoned valley.',2024,NULL,1,NULL,0.00,'2026-11-27 22:20:10',NULL,NULL),(7,'MOVIE','Quiet Harbor','A retired musician reconnects with her community while restoring a seaside theater.',2019,121,NULL,NULL,5.00,'2026-11-27 22:20:10',NULL,NULL),(8,'SERIES','The Glass Archive','Archivists uncover a conspiracy hidden in records of a fictional city.',2022,NULL,2,NULL,0.00,'2026-11-27 22:20:10',NULL,NULL),(9,'MOVIE','Wild Circuit','Young mechanics build an electric race car to keep their workshop open.',2023,109,NULL,NULL,4.00,'2026-11-27 22:20:10',NULL,NULL),(10,'SERIES','Garden of Echoes','A botanist studies plants that react to memories in a remote greenhouse.',2020,NULL,2,NULL,0.00,'2026-11-27 22:20:10',NULL,NULL),(11,'MOVIE','Sunday at Nine','Three neighbors turn a weekly breakfast into an unexpected friendship.',2018,94,NULL,NULL,5.00,'2026-11-27 22:20:10',NULL,NULL),(12,'SERIES','Red Meridian','A disaster-response team coordinates rescues across a mountain region.',2021,NULL,4,NULL,0.00,'2026-11-27 22:20:10',NULL,NULL),(13,'MOVIE','Borrowed Time','A watchmaker finds a pocket watch that lets its owner revisit one ordinary day.',2025,116,NULL,NULL,5.00,'2026-11-27 22:20:10',NULL,NULL),(14,'SERIES','Blue Note Avenue','Musicians at a community venue work together to keep it independent.',2019,NULL,3,NULL,4.00,'2026-11-27 22:20:10',NULL,NULL),(15,'MOVIE','A Map of Small Things','A teacher and her students document the overlooked stories of their district.',2022,101,NULL,NULL,0.00,'2026-11-27 22:20:10',NULL,NULL);
/*!40000 ALTER TABLE `titles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_avatars`
--

DROP TABLE IF EXISTS `user_avatars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_avatars` (
  `user_id` bigint NOT NULL,
  `content_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image` mediumblob NOT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_avatars_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_avatars`
--

LOCK TABLES `user_avatars` WRITE;
/*!40000 ALTER TABLE `user_avatars` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_avatars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(254) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role_id` bigint NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `google_subject` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`),
  UNIQUE KEY `uk_users_google_subject` (`google_subject`),
  KEY `idx_users_role_id` (`role_id`),
  CONSTRAINT `fk_users_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','BodrioPopcorn Admin',NULL,NULL,1,1,'2026-11-27 22:20:10',NULL),(2,'moderator@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','BodrioPopcorn Moderator',NULL,NULL,2,1,'2026-11-27 22:20:10',NULL),(3,'user01@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','Alex Rivera',NULL,NULL,3,1,'2026-11-27 22:20:10',NULL),(4,'user02@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','Sam Torres',NULL,NULL,3,1,'2026-11-27 22:20:10',NULL),(5,'user03@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','Jamie Cruz',NULL,NULL,3,1,'2026-11-27 22:20:10',NULL),(6,'user04@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','Taylor Flores',NULL,NULL,3,1,'2026-11-27 22:20:10',NULL),(7,'user05@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','Morgan Reyes',NULL,NULL,3,1,'2026-11-27 22:20:10',NULL),(8,'user06@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','Casey Vega',NULL,NULL,3,1,'2026-11-27 22:20:10',NULL),(9,'user07@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','Jordan Luna',NULL,NULL,3,1,'2026-11-27 22:20:10',NULL),(10,'user08@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','Riley Soto',NULL,NULL,3,1,'2026-11-27 22:20:10',NULL),(11,'user09@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','Avery Castillo',NULL,NULL,3,1,'2026-11-27 22:20:10',NULL),(12,'user10@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','Quinn Moreno',NULL,NULL,3,1,'2026-11-27 22:20:10',NULL),(13,'user11@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','Drew Navarro',NULL,NULL,3,1,'2026-11-27 22:20:10',NULL),(14,'user12@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','Skyler Ortiz',NULL,NULL,3,1,'2026-11-27 22:20:10',NULL),(15,'user13@bodriopopcorn.test','$2a$10$cqhuiFnmsbQNdY0ZqgCNmeBLXN/H41XdgbzzFkFu6j/X1/CDggEwK','Robin Medina',NULL,NULL,3,1,'2026-11-27 22:20:10',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `watchlist_items`
--

DROP TABLE IF EXISTS `watchlist_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `watchlist_items` (
  `user_id` bigint NOT NULL,
  `title_id` bigint NOT NULL,
  `added_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`title_id`),
  KEY `idx_watchlist_title_id` (`title_id`),
  CONSTRAINT `fk_watchlist_title` FOREIGN KEY (`title_id`) REFERENCES `titles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_watchlist_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `watchlist_items`
--

LOCK TABLES `watchlist_items` WRITE;
/*!40000 ALTER TABLE `watchlist_items` DISABLE KEYS */;
INSERT INTO `watchlist_items` VALUES (3,4,'2026-11-27 22:20:10'),(4,5,'2026-11-27 22:20:10'),(5,6,'2026-11-27 22:20:10'),(6,7,'2026-11-27 22:20:10'),(7,8,'2026-11-27 22:20:10'),(8,9,'2026-11-27 22:20:10'),(9,10,'2026-11-27 22:20:10'),(10,11,'2026-11-27 22:20:10'),(11,12,'2026-11-27 22:20:10'),(12,13,'2026-11-27 22:20:10'),(13,14,'2026-11-27 22:20:10'),(14,15,'2026-11-27 22:20:10'),(15,1,'2026-11-27 22:20:10');
/*!40000 ALTER TABLE `watchlist_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'bodrio_demo_export_ef25f0f0'
--

--
-- Dumping routines for database 'bodrio_demo_export_ef25f0f0'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed
