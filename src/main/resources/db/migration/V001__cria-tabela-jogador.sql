
CREATE TABLE jogador (

  /* id bigint NOT NULL AUTO_INCREMENT, no mysql*/
  id SERIAL,
  nome varchar(30) NOT NULL,
  sobrenome varchar(30) NOT NULL,
  categoria varchar(2) DEFAULT 'D' NOT NULL,
  statusJogador varchar(20) DEFAULT 'Disponivel' NOT NULL,
  status varchar(10) DEFAULT 'Ativo' NOT NULL,
  
  PRIMARY KEY (id)
);