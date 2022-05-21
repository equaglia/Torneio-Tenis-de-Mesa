CREATE TABLE pontuacao (

  /* id bigint NOT NULL AUTO_INCREMENT, no mysql */
  id SERIAL,
  pontos int DEFAULT 0,
  game_id bigint DEFAULT NULL,
  jogador_id bigint DEFAULT NULL,
  
  PRIMARY KEY (id)
);

ALTER TABLE pontuacao ADD CONSTRAINT FK_game_pontuacao
FOREIGN KEY (game_id) REFERENCES game (id);

ALTER TABLE pontuacao ADD CONSTRAINT FK_jogador_pontuacao
FOREIGN KEY (jogador_id) REFERENCES jogador (id);

