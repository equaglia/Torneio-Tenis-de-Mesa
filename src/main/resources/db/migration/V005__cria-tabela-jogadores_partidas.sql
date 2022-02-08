CREATE TABLE `jogadores_partidas` (
  `partida_id` bigint NOT NULL,
  `jogador_id` bigint NOT NULL,
  KEY `FK_jogador` (`jogador_id`),
  KEY `FK_partida` (`partida_id`),
  CONSTRAINT `FK_partida` FOREIGN KEY (`partida_id`) REFERENCES `jogador` (`id`),
  CONSTRAINT `FK_jogador` FOREIGN KEY (`jogador_id`) REFERENCES `partida` (`id`)
)