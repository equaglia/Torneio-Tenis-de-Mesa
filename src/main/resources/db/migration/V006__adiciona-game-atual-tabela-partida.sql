ALTER TABLE partida
ADD game_atual_indice int DEFAULT NULL;
/*
ALTER TABLE partida ADD game_atual_id bigint DEFAULT NULL; ALTER TABLE partida ADD CONSTRAINT FK_game_atual FOREIGN KEY (game_atual_id) REFERENCES game (id);
*/