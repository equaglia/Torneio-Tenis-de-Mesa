CREATE TABLE `game` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fim` datetime(6) DEFAULT NULL,
  `inicio` datetime(6) DEFAULT NULL,
  `partida_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfbe6cm6g4w4ayato6oppysntg` (`partida_id`),
  CONSTRAINT `FKfbe6cm6g4w4ayato6oppysntg` FOREIGN KEY (`partida_id`) REFERENCES `partida` (`id`)
)