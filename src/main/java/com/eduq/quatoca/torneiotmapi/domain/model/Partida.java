package com.eduq.quatoca.torneiotmapi.domain.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(exclude = {"games", "inicio", "fim"})
public class Partida {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToMany(mappedBy = "partidas")
	@JsonIgnore
	private Set<Jogador> jogadores = new HashSet<Jogador>();
	
	@OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
	@Embedded
	private List<Game> games = new ArrayList<>();

	private OffsetDateTime inicio;
	private OffsetDateTime fim;
	
	public void addGame(Game game) {
		games.add(game);
		game.setPartida(this);
	}
	
	public void addJogador(Jogador jogador) {
		System.out.println("jogador nome = "+jogador.getNome());
		jogadores.add(jogador);
		System.out.println("numero de jogadores em partida = " + jogadores.size());
		jogador.getPartidas().add(this);
	}
}
