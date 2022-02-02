CREATE TABLE `jogadores_partidas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `jogadorDireita_id` bigint DEFAULT NULL,
  `jogadorEsquerda_id` bigint DEFAULT NULL,
  `partida_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjkcdqsi8lwnbai93a8u8sdyvf` (`jogadorDireita_id`),
  KEY `FK_esquerda` (`jogadorEsquerda_id`),
  KEY `FKg6scthq9a3kqyi05wor5vx3gi` (`partida_id`),
  CONSTRAINT `FKg6scthq9a3kqyi05wor5vx3gi` FOREIGN KEY (`partida_id`) REFERENCES `partida` (`id`),
  CONSTRAINT `FKjkcdqsi8lwnbai93a8u8sdyvf` FOREIGN KEY (`jogadorDireita_id`) REFERENCES `jogador` (`id`),
  CONSTRAINT `FK_esquerda` FOREIGN KEY (`jogadorEsquerda_id`) REFERENCES `jogador` (`id`)
)