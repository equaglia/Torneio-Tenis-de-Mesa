package com.eduq.quatoca.torneiotmapi.model;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class Jogador {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "jogadores_partidas",
		joinColumns = {@JoinColumn(name="partida_id")},
		inverseJoinColumns = {@JoinColumn(name="jogador_id")})
	private List<Partida> partidas;
	
	@Column(nullable = false)
	private String nome;
	@Column(nullable = false)
	private String sobrenome;
	
	@Enumerated(EnumType.STRING)
	private CategoriaJogador categoria;
	
	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
	private Set<Pontuacao> pontos;

}
