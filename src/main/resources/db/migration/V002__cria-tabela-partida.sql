CREATE TABLE partida (

/*  id bigint NOT NULL AUTO_INCREMENT,
  fim datetime(6) DEFAULT NULL,
  inicio datetime(6) DEFAULT NULL, no mysql */
  id SERIAL,
  fim timestamp DEFAULT NULL,
  inicio timestamp DEFAULT NULL,
  status varchar(20) DEFAULT NULL,
  jogador_a bigint DEFAULT NULL,
  jogador_b bigint DEFAULT NULL,
  primeiro_sacador_id bigint DEFAULT NULL,
  games_vencidosa int DEFAULT NULL,
  games_vencidosb int DEFAULT NULL,
  quantidade_games int DEFAULT NULL,

  PRIMARY KEY (id)
);

ALTER TABLE partida ADD CONSTRAINT FK_primeiroSacador_partida
FOREIGN KEY (primeiro_sacador_id) REFERENCES jogador (id);

ALTER TABLE partida ADD CONSTRAINT FK_jogador_a
FOREIGN KEY (jogador_a) REFERENCES jogador (id);

ALTER TABLE partida ADD CONSTRAINT FK_jogador_b
FOREIGN KEY (jogador_b) REFERENCES jogador (id);