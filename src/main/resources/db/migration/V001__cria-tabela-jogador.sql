
CREATE TABLE `jogador` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `categoria` varchar(255) DEFAULT NULL,
  `nome` varchar(255) NOT NULL,
  `sobrenome` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);