package com.eduq.quatoca.torneiotmapi.domain.model;

import com.eduq.quatoca.torneiotmapi.common.CalculosGlobais;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
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
	private Set<Jogador> jogadores = new HashSet<>();

	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JsonIgnore
	private Jogador primeiroSacador;

	@OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
	@Embedded
	@ToString.Exclude
	private List<Game> games = new ArrayList<>();

//	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//	@JsonIgnore
//	private Game gameAtual;

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
		this.setStatus(StatusPartida.Preparada);
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
		return this.getGames().get(0);
	}

	public Game proximoGame() {

//		int indiceGameAtual;
//		if (this.getGameAtual() == null) {
//			throw new NegocioException("Partida sem game atual. Não há game em andamento."); //TODO melhorar texto da exception
//		} else {
//			indiceGameAtual = this.getGames().indexOf(this.getGameAtual());
//		}
//		if (indiceGameAtual < getQuantidadeGamesDaPartida()) {
//			return this.getGames().get(indiceGameAtual + 1);
//		} else {
//			throw (new NegocioException("Este é o último game da partida"));
//		}


		if (this.emAndamento()) {
//			int indiceGameAtual = this.getGames().indexOf(this.getGameAtual());
			int indiceGameAtual = this.getGameAtualIndice();
			int proximoGameAposAtual = indiceGameAtual + 1;
//			System.out.println("indice gameAtual "+indiceGameAtual + "  proximo game "+proximoGameAposAtual);
			int proximoGameIndice = 0;
			int quantidadeGames = getQuantidadeGamesDaPartida();
			while (proximoGameIndice < quantidadeGames) {
				Game thisGame = this.games.get(proximoGameIndice);
				System.out.println("proximoGame indice "+proximoGameIndice+ " id "+thisGame.getId()+" Partida.proximoGame()");
				if (thisGame.finalizado()) {
					if (proximoGameIndice == getQuantidadeGamesDaPartida() - 1) {
						this.finalizar();
						System.out.println("Partida.proximoGame FINALIZOU GAME game = "+thisGame.getId());
					}
					proximoGameIndice++;
				} else if (thisGame.preparado() || thisGame.emAndamento()) {
					System.out.println("RETURN proximoGame indice "+proximoGameIndice+" id "+thisGame.getId() + " partida "+this.getId()+" Partida.proximoGame()");
//					return this.getGameAtual();
					return thisGame;
				} else
					throw new NegocioException("Partida impedida de continuar");
			}
		}
		return null;
	}

	public Game gameAnterior() {
		int indiceGameAtual = this.getGameAtualIndice();
//		if (indiceGameAtual == -1) {
//			throw new NegocioException("Partida sem game atual. Não há game em andamento."); //TODO melhorar texto da exception
//
//			indiceGameAtual = this.getGames().indexOf(this.getGameAtual());
//		} else {
//			throw new NegocioException("Partida sem game atual. Não há game em andamento."); //TODO melhorar texto da exception
//		}
//		if (this.getGameAtual() != null) {
//		}
		if (indiceGameAtual > 0) {
			return this.getGames().get(indiceGameAtual -1);
		} else {
			throw (new NegocioException("Este é o primeiro game da partida"));
		}

//		int quantidadeGames = getQuantidadeGamesDaPartida();
//		if (primeiroGameDaPartida() == buscarGameEmAndamento()) {
//			throw (new NegocioException("Este é o primeiro game da partida"));
//		}
//		int i = 1;
//		while (i <= quantidadeGames) {
//			if (this.games.get(i).emAndamento())
//				return this.games.get(i - 1);
//			i++;
//		}
//		throw (new NegocioException("Não há game em andamento"));
	}

	public void iniciar() {
		switch (this.getStatus()) {
		case Preparada:
			boolean jogadoresDisponiveis = jogadoresDisponiveisParaIniciarPartida();
			if (jogadoresDisponiveis) {
				this.setStatus(StatusPartida.EmAndamento);
				this.setInicio(OffsetDateTime.now());
				this.jogadores.forEach(Jogador::convocar);
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
		this.jogadores.forEach(Jogador::liberar);
		this.games.forEach(game -> {
			if (game.preparado())
				game.cancelar();
		});
		this.setFim(OffsetDateTime.now());
//		this.setGameAtual(null);
		this.setGameAtualIndice(-1);
		this.getGames().stream()
				.filter(game -> game.getStatus() != StatusGame.Finalizado)
				.forEach(game -> game.setStatus(StatusGame.Cancelado));
		System.out.println(" finalizada "+this+" Partida.finalizar()");
	}
	
	public void cancelar() {
		this.setStatus(StatusPartida.Cancelada);
//		System.out.println(" cancelada "+this);
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
		if (this.getStatus() == StatusPartida.Finalizada) {
			this.setStatus(StatusPartida.EmAndamento);
			this.jogadores.forEach(Jogador::convocar);
			this.games.forEach(game -> {
				if (game.cancelado())
					game.setPreparado();
//				if (game.emAndamento()) {
//					this.setGameAtual(game);
//					this.setGameAtualIndice();
//				}
			});
			this.garantirNoMaximoUmGameEmAndamento();
			this.setFim(null);
		}
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

	private void garantirNoMaximoUmGameEmAndamento() {
//		boolean temGameEmAndamento = false;
		for (int i = 0; i < getQuantidadeGamesDaPartida(); i++) {
			Game game = this.getGame(i);
			if (game.emAndamento()) {
//				this.setGameAtual(game);
				this.setGameAtualIndice(i);
				System.out.println("atualizou gameAtualIndice para "+i);
				break;
//				if (!temGameEmAndamento) temGameEmAndamento = true;
//				else {
//					game.setStatus(StatusJogo.Preparado);
//				}
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

	@Override
	public String toString() {
		String partidaToString = "";
		List<Integer> resultado = new ArrayList<>(calculaResultado());
		Game game = this.getGame(0);
		String jogadorA = game.getPontos().get(0).getJogador().getNome();
		String jogadorB = game.getPontos().get(1).getJogador().getNome();
		partidaToString = partidaToString + "ptd " + this.getId() + ", " + jogadorA + " x " + jogadorB;

		//Collections.sort(this.getGames());
		partidaToString = partidaToString + this.getGames();
		partidaToString = partidaToString +
				"\n " + jogadorA +" "+ resultado.get(0) + " X " + resultado.get(1) +" "+ jogadorB;
		if (this.getGameAtualIndice() != -1) partidaToString = partidaToString + "\n gAtual id " + this.getGameAtualIndice() +"\n";

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
//					if (ptsJgdrA_noGame > ptsJgdrB_noGame)
						resultado.set(0, resultado.get(0) + 1);
					else if (vencedorGame == 1)
//					else if (ptsJgdrB_noGame > ptsJgdrA_noGame) {
						resultado.set(1, resultado.get(1) + 1);
					else {
						throw new NegocioException("Game não finalizado");
//						throw new NegocioException("Pontuação deste game está incorreta");
					}
		});
//		this.setGamesVencidosA(resultado.get(0));
//		this.setGamesVencidosB(resultado.get(1));
		return resultado;
	}

	public void moverParaProximoGame() {
		//System.out.println("ANTES Partida.moverParaProximoGame gameAtualIndice = "+this.getGameAtualIndice()+"   getGames.size() = "+this.getGames().size());
		if (this.getGameAtualIndice() < this.getGames().size() - 1) {
			this.setGameAtualIndice(this.getGameAtualIndice() + 1);
		}
		//System.out.println("DEPOIS Partida.moverParaProximoGame gameAtualIndice = "+this.getGameAtualIndice()+"   getGames.size() = "+this.getGames().size());
	}
}
