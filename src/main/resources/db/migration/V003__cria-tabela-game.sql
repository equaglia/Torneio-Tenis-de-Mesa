CREATE TABLE `game` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fim` datetime(6) DEFAULT NULL,
  `inicio` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `partida_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_partida_game` (`partida_id`),
  CONSTRAINT `FK_partida_game` FOREIGN KEY (`partida_id`) REFERENCES `partida` (`id`)
)