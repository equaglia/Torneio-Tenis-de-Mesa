CREATE TABLE `pontuacao` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pontos` int DEFAULT '0',
  `game_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_game_pontuacao` (`game_id`),
  CONSTRAINT `FK_game_pontuacao` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`)
);