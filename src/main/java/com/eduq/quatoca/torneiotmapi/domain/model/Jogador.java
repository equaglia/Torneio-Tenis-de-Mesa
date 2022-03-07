package com.eduq.quatoca.torneiotmapi.domain.model;

import java.util.HashSet;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(exclude = {"partidas", "categoria"})
public class Jogador {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToMany(cascade = 
		{CascadeType.PERSIST,
		CascadeType.MERGE})
	@JoinTable(name = "jogadores_partidas",
		joinColumns = @JoinColumn(name="partida_id"),
		inverseJoinColumns = @JoinColumn(name="jogador_id"))
	private Set<Partida> partidas = new HashSet<>();
	
	@NotBlank(message = "Nome é mandatório")
	@Column(nullable = false)
	private String nome;
	
	@NotBlank(message = "Sobrenome é mandatório")
	@Column(nullable = false)
	private String sobrenome;
	
	@NotNull(message = "Categoria é mandatório")
	@Enumerated(EnumType.STRING)
	private CategoriaJogador categoria;
	
	@OneToMany(mappedBy = "jogador", cascade = CascadeType.ALL)
	private Set<Pontuacao> pontos;

}
