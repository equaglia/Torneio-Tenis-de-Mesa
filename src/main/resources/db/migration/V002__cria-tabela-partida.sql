CREATE TABLE partida (

  id bigint NOT NULL AUTO_INCREMENT,
  fim datetime(6) DEFAULT NULL,
  inicio datetime(6) DEFAULT NULL,
  status varchar(20) DEFAULT NULL,
  
  PRIMARY KEY (id)
)