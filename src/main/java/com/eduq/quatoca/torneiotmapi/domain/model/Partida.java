package com.eduq.quatoca.torneiotmapi.domain.model;

import com.eduq.quatoca.torneiotmapi.common.CalculosGlobais;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.enums.StatusGame;
import com.eduq.quatoca.torneiotmapi.domain.model.enums.StatusPartida;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.IntStream;

//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
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
	//private Map<Integer, Jogador> jogadores = new HashMap<>();
	private Set<Jogador> jogadores = new HashSet<>();

	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JsonIgnore
	private Jogador jogadorA;

	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JsonIgnore
	private Jogador jogadorB;

	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JsonIgnore
	private Jogador primeiroSacador;

	@OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
	@Embedded
	@ToString.Exclude
	private List<Game> games = new ArrayList<>();

	private int gameAtualIndice;

	private OffsetDateTime inicio;
	private OffsetDateTime fim;

	@NotNull(message = "Status da partida é mandatório")
	@Enumerated(EnumType.STRING)
	private StatusPartida status;

	private int gamesVencidosA;
	private int gamesVencidosB;

	private int quantidadeGames;

	public void addGame(Game game) {
		games.add(game);
		game.setPartida(this);
	}

	public void liberarJogadores() {
		getJogadorA().liberar();
		getJogadorB().liberar();
	}

	public void convocarJogadores() {
		getJogadorA().convocar();
		getJogadorB().convocar();
	}

	public Partida() {
		super();
		this.setStatus(StatusPartida.Preparada);
	}

	public Game buscarGameEmAndamento() {
		Collections.sort(this.games);

		for (Game g : this.getGames())
			if (g.emAndamento()) return g;
		for (Game g : this.getGames())
			if (g.preparado()) {
				g.iniciar();
				return g;
			}
		else return null;
		throw (new NegocioException("Não há game em andamento na partida"));
	}

	public Game primeiroGameDaPartida() {
		Collections.sort(this.games);
		return this.getGames().get(0);
	}

	public Game gameAnterior() {
		Collections.sort(this.games);

		int indiceGameAtual = this.getGameAtualIndice();
		if (indiceGameAtual > 0) {
			return this.getGames().get(indiceGameAtual -1);
		} else {
			throw (new NegocioException("Este é o primeiro game da partida"));
		}
	}

	public void iniciar() {
		switch (this.getStatus()) {
		case Preparada:
			boolean jogadoresDisponiveis = jogadoresDisponiveisParaIniciarPartida();
			if (jogadoresDisponiveis) {
				this.setStatus(StatusPartida.EmAndamento);
				this.setInicio(OffsetDateTime.now());
				convocarJogadores();
			} else {
				throw new NegocioException("Ao menos um dos jogadores não está disponível para a partida");
			}
			break;
		case EmAndamento:
			break;
		case Cancelada:
			throw (new NegocioException("Partida Cancelada não pode ser iniciada"));
		case Finalizada:
			throw (new NegocioException("Partida já foi Finalizada então não pode ser iniciada"));
		case Interrompida:
			throw (new NegocioException(
					"Partida Interrompida precisa voltar para o status Preparado para ser reiniciada"));
		default:
			throw (new NegocioException("Ops, algo deu errado..."));
		}
	}

	public void finalizar() {
		this.setStatus(StatusPartida.Finalizada);
		liberarJogadores();
		this.games.forEach(game -> {
			if (game.preparado())
				game.cancelar();
		});
		this.setFim(OffsetDateTime.now());
		this.setGameAtualIndice(-1);
		this.getGames().stream()
				.filter(game -> game.getStatus() != StatusGame.Finalizado)
				.forEach(game -> game.setStatus(StatusGame.Cancelado));
		Collections.sort(this.games);
//		System.out.println(" finalizada "+this+" Partida.finalizar()");
	}

	public void interromper() {
		this.setStatus(StatusPartida.Interrompida);
		Collections.sort(this.games);
		System.out.println(" interrompida "+this);
	}
	
	public void cancelar() {
		this.setStatus(StatusPartida.Cancelada);
		Collections.sort(this.games);
		System.out.println(" cancelada "+this);
	}

	public boolean jogadoresDisponiveisParaIniciarPartida() {
		return (getJogadorA().disponivel() && getJogadorB().disponivel());
	}

	public boolean isJogadorDaPartida(Jogador jogador) {
		return  (getJogadorA() == jogador || getJogadorB() == jogador);
	}

	public boolean isGameDaPartida(Game game) {
		return this.games.contains(game);
	}

	public Jogador buscarNaoPrimeiroSacador() {
		return jogadorB;
	}

	public boolean jaRegistrouPontuacao() {
		return !(((this.emAndamento() && this.getGames().get(0).getPontos().get(0).getPontos() == 0)
				&& this.getGames().get(0).getPontos().get(1).getPontos() == 0) || this.preparado());
	}

	public void setEmAndamento() {
		this.setStatus(StatusPartida.EmAndamento);
		Collections.sort(this.games);
		System.out.println(" em andamento "+this);
	}

	public boolean preparado() {
		return this.getStatus() == StatusPartida.Preparada;
	}

	public boolean emAndamento() {
		return this.getStatus() == StatusPartida.EmAndamento;
	}

	public boolean finalizado() {
		return this.getStatus() == StatusPartida.Finalizada;
	}

	public boolean interrompido() {
		return this.getStatus() == StatusPartida.Interrompida;
	}

	public boolean cancelado() {
		return this.getStatus() == StatusPartida.Cancelada;
	}
	
	public Game getGame(int game) {
		return this.getGames().get(game);
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

	@Override
	public String toString() {
		String partidaToString = "";
		List<Integer> resultado = new ArrayList<>(calculaResultado());
		Game game = this.getGame(0);
		String jogA = game.getPontos().get(0).getJogador().getNome();
		String jogB = game.getPontos().get(1).getJogador().getNome();
		partidaToString = partidaToString + "ptd " + this.getId() + " " + this.getStatus()+", "
				+ jogA +" "+ resultado.get(0) + " X " + resultado.get(1) +" "+ jogB;
		if (this.getGameAtualIndice() != -1) partidaToString = partidaToString + "\n gAtual # " + this.getGameAtualIndice();
		partidaToString = partidaToString + this.getGames() + "\n";
		return partidaToString;
	}

	public List<Integer> calculaResultado() {
		List<Integer> resultado = new ArrayList<>();
		resultado.add(0);
		resultado.add(0);
		IntStream.range(0, this.getGames().size())
				.mapToObj(this::getGame)
				.filter(Game::finalizado)
				.forEach(game -> {
					int ptsJgdrA_noGame = game.getPontos().get(0).getPontos();
					int ptsJgdrB_noGame = game.getPontos().get(1).getPontos();
					int vencedorGame = CalculosGlobais.vencedorGame(ptsJgdrA_noGame, ptsJgdrB_noGame);
					if (vencedorGame == 0)
						resultado.set(0, resultado.get(0) + 1);
					else if (vencedorGame == 1)
						resultado.set(1, resultado.get(1) + 1);
					else {
						throw new NegocioException("Game não finalizado");
					}
		});
		return resultado;
	}

	public void moverParaProximoGame() { //TODO Checar controle de gameAtualIndice. Talvez esteja pulando um game
		if (this.getGameAtualIndice() < this.getGames().size() - 1) {
			this.setGameAtualIndice(this.getGameAtualIndice() + 1);
			this.getGames().get(getGameAtualIndice()).setEmAndamento();
		}
	}
}
