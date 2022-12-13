CREATE TABLE partida (

/*  id bigint NOT NULL AUTO_INCREMENT,
  fim datetime(6) DEFAULT NULL,
  inicio datetime(6) DEFAULT NULL, no mysql */
  id SERIAL,
  fim timestamp DEFAULT NULL,
  inicio timestamp DEFAULT NULL,
  status varchar(20) DEFAULT NULL,
  primeiro_sacador_id bigint DEFAULT NULL,
  games_vencidosa int DEFAULT NULL,
  games_vencidosb int DEFAULT NULL,

  PRIMARY KEY (id)
);

ALTER TABLE partida ADD CONSTRAINT FK_primeiroSacador_partida
FOREIGN KEY (primeiro_sacador_id) REFERENCES jogador (id);