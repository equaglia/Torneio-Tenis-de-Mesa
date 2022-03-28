package com.eduq.quatoca.torneiotmapi.domain.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;
import com.eduq.quatoca.torneiotmapi.domain.repository.GameRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class GestaoGameService {
	
	private GameRepository gameRepository;
	private GestaoPontuacaoService gestaoPontuacaoService;

	public Game buscar(Long gameId) {
		return gameRepository.findById(gameId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException("Game não encontrado GestaoGameService"));
	}
	
	public List<Game> listar() {
		return gameRepository.findAll();
	}

	@Transactional
	public Game salvar(Game game) {
		return gameRepository.save(game);
	}

	@Transactional
	public Game prepararGame(Optional<Jogador> jogadorA, Optional<Jogador> jogadorB, OffsetDateTime horarioInicial) {
		Game game = this.prepararGame(jogadorA, jogadorB);
		game.setInicio(horarioInicial);
		this.salvar(game);
		return game;
	}

	@Transactional
	public Game prepararGame(Optional<Jogador> jogadorA, Optional<Jogador> jogadorB) {
//		String datesmall = "2022-02-03T16:05"; // TBD provisório
		Game game = new Game();
		game.addPontuacao(gestaoPontuacaoService.preparaPontuacao(), jogadorA.get());
		game.addPontuacao(gestaoPontuacaoService.preparaPontuacao(), jogadorB.get());
		this.salvar(game);
		return game;
	}

	@Transactional
	public void iniciarGame(Long gameId) {
		Game game = this.buscar(gameId);
		game.iniciar();
		this.salvar(game);		
	}

	@Transactional
		public Game atualizarPontuacao(Long gameId, int pontuacaoA, int pontuacaoB) {
			if (pontuacaoA < 0 || pontuacaoB <0) {
				new NegocioException("Pontuaçõe devem ter valores positivos");
				return null;
			}
			Game game = this.buscar(gameId);
			Boolean gameAtivo = gameSendoJogado(game);
			if (!gameAtivo && proximoGameProntoParaIniciar(game)) {
				garantirGameAnteriorJaFinalizado(game);
				gameAtivo = iniciarGame(game);
			}
	
			if (!(game.emAndamento())) gameAtivo = false; //TODO acho que não precisa desta linha
			if (gameAtivo) {
				Pontuacao pontuacaoJogadorA = gestaoPontuacaoService.buscar(game.getPontos().get(0).getId());
				Pontuacao pontuacaoJogadorB = gestaoPontuacaoService.buscar(game.getPontos().get(1).getId());
	
				if (pontuacaoA < 11 && pontuacaoB < 11) {
					atualizarPontosAmbosJogadores(pontuacaoA, pontuacaoB, pontuacaoJogadorA, pontuacaoJogadorB);
				} else if (pontuacaoA == 11 && pontuacaoB < 10
						|| pontuacaoA < 10 && pontuacaoB == 11
						|| Math.abs(pontuacaoA - pontuacaoB) == 2) {
					atualizarPontosAmbosJogadores(pontuacaoA, pontuacaoB, pontuacaoJogadorA, pontuacaoJogadorB);
					game.finalizar();
					this.salvar(game);
				} else {
					new NegocioException("Pontuacao maior que ONZE, não pode ter diferença maior que DOIS entre os 2 jogadores");
					return null;
				}
			}
			return game;
		}

	@Transactional
	public Game somaUmPonto(Long gameId, Long pontoId) {
		Game game = this.buscar(gameId);
		Boolean gameAtivo = gameSendoJogado(game);
		if (!gameAtivo && proximoGameProntoParaIniciar(game)) {
			garantirGameAnteriorJaFinalizado(game);
			gameAtivo = iniciarGame(game);
		}
		if (!(game.emAndamento())) gameAtivo = false; // TODO talvez não precise desta linha
		if (gameAtivo) {
			Pontuacao pontuacaoJogadorA = gestaoPontuacaoService.buscar(game.getPontos().get(0).getId());
			Pontuacao pontuacaoJogadorB = gestaoPontuacaoService.buscar(game.getPontos().get(1).getId());
			if (pontuacaoJogadorA.getId() == pontoId) {
				incrementar(pontuacaoJogadorA);
				finalizarGameComOnzePontosOuDiferencaMaiorQueDois(game, pontuacaoJogadorA, pontuacaoJogadorB);
			} else if (pontuacaoJogadorB.getId() == pontoId) {
				incrementar(pontuacaoJogadorB);
				finalizarGameComOnzePontosOuDiferencaMaiorQueDois(game, pontuacaoJogadorB, pontuacaoJogadorA);
			} else
				throw new EntidadeNaoEncontradaException("Game não encontrado");
		}
		return game;
	}

	private void finalizarGameComOnzePontosOuDiferencaMaiorQueDois(Game game, Pontuacao pontuacaoJogadorA, Pontuacao pontuacaoJogadorB) {
		int pontosParaVencer = 11;
		if (pontuacaoJogadorA.getPontos() >= pontosParaVencer
				&& Math.abs(pontuacaoJogadorA.getPontos() - pontuacaoJogadorB.getPontos()) >= 2) {
			game.finalizar();
			this.salvar(game);
		}
	}

	private boolean proximoGameProntoParaIniciar(Game game) {
		return game.getPartida().gameAnterior().finalizado() && game.preparado() && game.getPartida().emAndamento();
	}

	private boolean gameSendoJogado(Game game) {
		return game.getPartida().buscarGameEmAndamento() == game;
	}

	private void incrementar(Pontuacao pontuacao) {
		pontuacao.setPontos(pontuacao.mais1ponto());
		gestaoPontuacaoService.salvar(pontuacao);
	}

	private void atualizarPontosAmbosJogadores(int pontuacaoA, int pontuacaoB, Pontuacao pontuacaoJogadorA,
			Pontuacao pontuacaoJogadorB) {
		pontuacaoJogadorA.setPontos(pontuacaoA);
		pontuacaoJogadorB.setPontos(pontuacaoB);
		gestaoPontuacaoService.salvar(pontuacaoJogadorA);
		gestaoPontuacaoService.salvar(pontuacaoJogadorB);
	}

	private Boolean iniciarGame(Game game) {
		game.iniciar();
		this.salvar(game);
		return true;
	}

	private void garantirGameAnteriorJaFinalizado(Game game) {
		game.getPartida().gameAnterior().finalizar();
		this.salvar(game.getPartida().gameAnterior());
	}
}
