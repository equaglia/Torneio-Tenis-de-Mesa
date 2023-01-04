CREATE TABLE jogadores_partidas (
  partida_id bigint NOT NULL,
  jogador_id bigint NOT NULL
);

ALTER TABLE jogadores_partidas ADD CONSTRAINT FK_jogador
FOREIGN KEY (jogador_id) REFERENCES partida (id);

ALTER TABLE jogadores_partidas ADD CONSTRAINT FK_partida
FOREIGN KEY (partida_id) REFERENCES jogador (id);