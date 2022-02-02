package com.eduq.quatoca.torneiotmapi.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class JogadoresPartidas {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

//	@ManyToOne
//	@JoinColumn(name = "jogador_id")
//	private Jogador jogador;

	@ManyToOne
	@JoinColumn(name = "jogadorDireita_id")
	private Jogador jogadorDireita;

	@ManyToOne
	@JoinColumn(name = "jogadorEsquerda_id")
	private Jogador jogadorEsquerda;

	@ManyToOne
	@JoinColumn(name = "partida_id")
	private Partida partida;
	
//	TBD estatisticas
}
