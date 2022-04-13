package com.eduq.quatoca.torneiotmapi.domain.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity
public class Partida {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToMany(mappedBy = "partidas")
	@JsonIgnore
	private Set<Jogador> jogadores = new HashSet<Jogador>();
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JsonIgnore
	private Jogador primeiroSacador;

	@OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
	@Embedded
	private List<Game> games = new ArrayList<>();

	private OffsetDateTime inicio;
	private OffsetDateTime fim;

	@NotNull(message = "Status da partida é mandatório")
	@Enumerated(EnumType.STRING)
	private StatusJogo status;

	public void addGame(Game game) {
		games.add(game);
		game.setPartida(this);
	}

	public void addJogador(Jogador jogador) {
		if (this.jogadores.size() < 2) {
			jogadores.add(jogador);
			if (this.primeiroSacador == null) {
				this.setPrimeiroSacador(jogador);
			}
			jogador.getPartidas().add(this);
		} else {
			throw new NegocioException("Os dois jogadores da partida já haviam sido selecionados");
		}
	}

	public Partida() {
		super();
		this.status = StatusJogo.Preparado;
	}

	public Game buscarGameEmAndamento() {
		int quantidadeGames = getQuantidadeGamesDaPartida();

		int i = 0;
		while (i < quantidadeGames) {
			if (this.games.get(i).emAndamento())
				return this.games.get(i);
			i++;
		}
		if (primeiroGameDaPartida().preparado()) {
			primeiroGameDaPartida().iniciar();
			return primeiroGameDaPartida();
		}
		throw (new NegocioException("Não há game em andamento na partida"));
	}

	public Game primeiroGameDaPartida() {
		return this.games.get(0);
	}

	public Game proximoGame() {
		if (this.emAndamento()) {
			int proximoGame = 0;
			int quantidadeGames = getQuantidadeGamesDaPartida();
			while (proximoGame < quantidadeGames) {
				Game thisGame = this.games.get(proximoGame);
				if (thisGame.finalizado()) {
					if (proximoGame == getQuantidadeGamesDaPartida() - 1)
						this.finalizar();
					proximoGame++;
				} else if (thisGame.preparado() || thisGame.emAndamento()) {
					return thisGame;
				} else
					throw new NegocioException("Partida impedida de continuar");
			}
		}
		return null;
	}

	public Game gameAnterior() {
		int quantidadeGames = getQuantidadeGamesDaPartida();
		if (primeiroGameDaPartida() == buscarGameEmAndamento()) {
			throw (new NegocioException("Este é o primeiro game da partida"));
		}
		int i = 1;
		while (i <= quantidadeGames) {
			if (this.games.get(i).emAndamento())
				return this.games.get(i - 1);
			i++;
		}
		throw (new NegocioException("Não há game em andamento"));
	}

	public void iniciar() {
		switch (this.getStatus()) {
		case Preparado:
			Boolean jogadoresDisponiveis = checarSeJogadoresDisponiveisParaIniciarPartida();
			if (jogadoresDisponiveis) {
				this.setStatus(StatusJogo.EmAndamento);
				this.getGames().get(0).iniciar();
				this.setInicio(OffsetDateTime.now());
				this.jogadores.stream().forEach(jogador -> jogador.convocar());
			} else {
				throw new NegocioException("Ao menos um dos jogadores não está disponível para a partida");
			}
			break;
		case EmAndamento:
			break;
		case Cancelado:
			throw (new NegocioException("Partida Cancelada não pode ser iniciada"));
		case Finalizado:
			throw (new NegocioException("Partida já foi Finalizada então não pode ser iniciada"));
		case Interrompido:
			throw (new NegocioException(
					"Partida Interrompida precisa voltar para o status Preparado para ser reiniciada"));
		default:
			throw (new NegocioException("Ops, algo deu errado..."));
		}
	}

	public Boolean checarSeJogadoresDisponiveisParaIniciarPartida() {
		Boolean disponibilidade = true;
		for (Jogador jog : this.jogadores)
			disponibilidade = jog.disponivel() && disponibilidade;
		return disponibilidade;
	}

	public void finalizar() {
		this.setStatus(StatusJogo.Finalizado);
		this.jogadores.stream().forEach(jogador -> jogador.liberar());
		this.games.stream().forEach(game -> {
			if (game.preparado())
				game.cancelar();
		});
		this.setFim(OffsetDateTime.now());
	}

	public Boolean preparado() {
		return this.getStatus() == StatusJogo.Preparado;
	}

	public Boolean emAndamento() {
		return this.getStatus() == StatusJogo.EmAndamento;
	}
	
	public boolean jaRegistrouPontuacao() {
		return !(((this.emAndamento() 
					&& this.getGames().get(0).getPontos().get(0).getPontos() == 0)
					&& this.getGames().get(0).getPontos().get(1).getPontos() == 0) 
				|| this.preparado());
	}

	public Boolean finalizado() {
		return this.getStatus() == StatusJogo.Finalizado;
	}

	public Boolean interrompido() {
		return this.getStatus() == StatusJogo.Interrompido;
	}

	public Boolean cancelado() {
		return this.getStatus() == StatusJogo.Cancelado;
	}

	private int getQuantidadeGamesDaPartida() {
		return this.games.size();
	}
}
