
CREATE TABLE jogador (

  /* id bigint NOT NULL AUTO_INCREMENT, no mysql*/
  id SERIAL,
  nome varchar(30) NOT NULL,
  sobrenome varchar(30) NOT NULL,
  categoria varchar(2) DEFAULT 'D' NOT NULL,
  status varchar(20) DEFAULT 'Disponivel' NOT NULL,
  
  PRIMARY KEY (id)
);