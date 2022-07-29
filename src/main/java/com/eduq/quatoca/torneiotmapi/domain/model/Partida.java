package com.eduq.quatoca.torneiotmapi.domain.model;

import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.*;

//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@ToString
//@RequiredArgsConstructor
@Entity
public class Partida {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

//	@ManyToMany(mappedBy = "partidas")
	@ManyToMany(cascade = 
		{CascadeType.PERSIST,
		CascadeType.MERGE})
	@JoinTable(name = "jogadores_partidas",
		joinColumns = @JoinColumn(name="jogador_id"),
		inverseJoinColumns = @JoinColumn(name="partida_id"))
	@JsonIgnore
	@ToString.Exclude
	private Set<Jogador> jogadores = new HashSet<>();

	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JsonIgnore
	private Jogador primeiroSacador;

	@OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
	@Embedded
	@ToString.Exclude
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
	
	public void liberarJogadores() {
		this.jogadores.forEach(Jogador::liberar);
	}

	public Partida() {
		super();
		this.setStatus(StatusJogo.Preparado);
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
			boolean jogadoresDisponiveis = jogadoresDisponiveisParaIniciarPartida();
			if (jogadoresDisponiveis) {
				this.setStatus(StatusJogo.EmAndamento);
				this.setInicio(OffsetDateTime.now());
				this.jogadores.forEach(Jogador::convocar);
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

	public void finalizar() {
		this.setStatus(StatusJogo.Finalizado);
		this.jogadores.forEach(Jogador::liberar);
		this.games.forEach(game -> {
			if (game.preparado())
				game.cancelar();
		});
		this.setFim(OffsetDateTime.now());
	}
	
	public void cancelar() {
		this.setStatus(StatusJogo.Cancelado);
	}

	public boolean jogadoresDisponiveisParaIniciarPartida() {
		boolean disponibilidade = true;
		for (Jogador jog : this.jogadores)
			disponibilidade = jog.disponivel() && disponibilidade;
		return disponibilidade;
	}

	public boolean isJogadorDaPartida(Jogador jogador) {
		return this.jogadores.contains(jogador);
	}

	public boolean isGameDaPartida(Game game) {
		return this.games.contains(game);
	}

	public Jogador buscarNaoPrimeiroSacador() {
		Optional<Jogador> naoSacador = this.jogadores.stream()
				.filter(p -> !(this.getPrimeiroSacador() == p))
				.findAny();
		return naoSacador.orElse(null);
	}

	public boolean jaRegistrouPontuacao() {
		return !(((this.emAndamento() && this.getGames().get(0).getPontos().get(0).getPontos() == 0)
				&& this.getGames().get(0).getPontos().get(1).getPontos() == 0) || this.preparado());
	}

	public void setEmAndamento() {
		if (this.getStatus() == StatusJogo.Finalizado) {
			this.setStatus(StatusJogo.EmAndamento);
			this.jogadores.forEach(Jogador::convocar);
			this.games.forEach(game -> {
				if (game.cancelado())
					game.setPreparado();
			});
			this.garantirNoMaximoUmGameEmAndamento();
			this.setFim(null);
		}
	}

	public boolean preparado() {
		return this.getStatus() == StatusJogo.Preparado;
	}

	public boolean emAndamento() {
		return this.getStatus() == StatusJogo.EmAndamento;
	}

	public boolean finalizado() {
		return this.getStatus() == StatusJogo.Finalizado;
	}

	public boolean interrompido() {
		return this.getStatus() == StatusJogo.Interrompido;
	}

	public boolean cancelado() {
		return this.getStatus() == StatusJogo.Cancelado;
	}
	
	public Game getGame(int i) {
		return this.getGames().get(i);
	}
	
	private void garantirNoMaximoUmGameEmAndamento() {
		boolean temGameEmAndamento = false;
		for (Game game : games) {
			if (game.emAndamento()) {
				if (!temGameEmAndamento) temGameEmAndamento = true;
				else {
					game.setStatus(StatusJogo.Preparado);
				}
			}
		}
	}

	private int getQuantidadeGamesDaPartida() {
		return this.games.size();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Partida partida = (Partida) o;
		return id != null && Objects.equals(id, partida.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
