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
		jogadores.add(jogador);
		jogador.getPartidas().add(this);
	}

	public Partida() {
		super();
		this.status = StatusJogo.Preparado;
	}

	public Game buscarGameEmAndamento() {
		System.out.println("entrou Partida:gameEmCurso() ");
		int size = games.size();

		int i = 0;
		while (i < size) {
			System.out.println("entrou while Partida:gameEmCurso() i= " + i);
			if (games.get(i).emAndamento())
				return games.get(i);
			i++;
		}
		System.out.println("saindo return null Partida:gameEmCurso() ");
		primeiroGameDaPartida().iniciar();
		return primeiroGameDaPartida();
	}

	private Game primeiroGameDaPartida() {
		return games.get(0);
	}

	public Game proximoGame() {
		int size = games.size();
		int i = 0;
		while (i < size) {
			if (games.get(i).emAndamento())
				return games.get(i + 1);
			i++;
		}
		if (games.get(i + 1).preparado())
			return games.get(i + 1);
		return null;
	}

	public Game gameAnterior() {
		int size = games.size();
		if (primeiroGameDaPartida() == buscarGameEmAndamento()) {
			new NegocioException("Este é o primeiro game da partida");
			return null;
		}
		int i = 1;
		while (i <= size) {
			if (games.get(i).emAndamento())
				return games.get(i - 1);
			i++;
		}
		new NegocioException("Não há game em andamento");
		return null;
	}

	public void iniciar() {
		switch (this.getStatus()) {
		case Preparado:
			this.setStatus(StatusJogo.EmAndamento);
			this.getGames().get(0).iniciar();
			break;
		case EmAndamento:
			new NegocioException("Partida não pode ser iniciada, pois já estava Em Andamento");
			break;
		case Cancelado:
			new NegocioException("Partida Cancelada não pode ser iniciada");
			break;
		case Finalizado:
			new NegocioException("Partida já foi Finalizada então não pode ser iniciada");
			break;
		case Interrompido:
			new NegocioException("Partida Interrompida precisa voltar para o status Preparado para ser reiniciada");
			break;
		default:
			new NegocioException("Ops, algo deu errado...");
			break;
		}
	}

	public void finalizar() {
		this.setStatus(StatusJogo.Finalizado);
		this.setFim(OffsetDateTime.now());
	}

	public Boolean preparado() {
		return this.getStatus() == StatusJogo.Preparado;
	}

	public Boolean emAndamento() {
		return this.getStatus() == StatusJogo.EmAndamento;
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

}
