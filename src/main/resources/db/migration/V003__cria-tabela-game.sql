CREATE TABLE game (

  /* id bigint NOT NULL AUTO_INCREMENT,
  fim datetime(6) DEFAULT NULL,
  inicio datetime(6) DEFAULT NULL, no mysql */
  id SERIAL,
  fim timestamp DEFAULT NULL,
  inicio timestamp DEFAULT NULL,
  status varchar(20) DEFAULT NULL,
  partida_id bigint DEFAULT NULL,
  
  PRIMARY KEY (id)
);

ALTER TABLE game ADD CONSTRAINT FK_partida_game
FOREIGN KEY (partida_id) REFERENCES partida (id);