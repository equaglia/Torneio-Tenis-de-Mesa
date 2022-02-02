package com.eduq.quatoca.torneiotmapi.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class Jogador {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
	private Set<JogadoresPartidas> partidas;
//	@OneToMany(mappedBy = "jogador", cascade = CascadeType.ALL)
//	private Set<JogadoresPartidas> jogadores;

	@Column(nullable = false)
	private String nome;
	@Column(nullable = false)
	private String sobrenome;
	
	@Enumerated(EnumType.STRING)
	private CategoriaJogador categoria;
	
	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
	private Set<Pontuacao> pontos;

}
