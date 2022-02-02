CREATE TABLE `pontuacao` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pontos` int DEFAULT NULL,
  `game_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfh63a3jr15oc9a4a02vq7qj0x` (`game_id`),
  CONSTRAINT `FKfh63a3jr15oc9a4a02vq7qj0x` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`)
);