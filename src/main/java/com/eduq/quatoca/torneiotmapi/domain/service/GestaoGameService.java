package com.eduq.quatoca.torneiotmapi.domain.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;
import com.eduq.quatoca.torneiotmapi.domain.model.StatusJogo;
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
		if (pontuacaoA < 0 || pontuacaoB < 0) {
			throw (new NegocioException("Pontuações devem ter valores positivos"));
		}
		Game game = this.buscar(gameId);
		Boolean gameAtivo = gameEmAndamento(game);
		if (!gameAtivo && proximoGameProntoParaIniciar(game)) {
			garantirGameAnteriorJaFinalizado(game);
			gameAtivo = iniciarGame(game);
		}

		if (gameAtivo) {
			Pontuacao pontuacaoJogadorA = gestaoPontuacaoService.buscar(game.getPontos().get(0).getId());
			Pontuacao pontuacaoJogadorB = gestaoPontuacaoService.buscar(game.getPontos().get(1).getId());

			if (pontuacaoParaContinuarGame(pontuacaoA, pontuacaoB)) {
				atualizarPontosAmbosJogadores(pontuacaoA, pontuacaoB, pontuacaoJogadorA, pontuacaoJogadorB);
			} else if (pontuacaoParaFinalizarGame(pontuacaoA, pontuacaoB)) {
				atualizarPontosAmbosJogadores(pontuacaoA, pontuacaoB, pontuacaoJogadorA, pontuacaoJogadorB);
				finalizarGame(game);
			} else {
				throw (new NegocioException(
						"Pontuacao maior que ONZE, não pode ter diferença maior que DOIS entre os 2 jogadores"));
			}
		}
		return game;
	}

	@Transactional
	public Game somaUmPonto(Long gameId, Long pontoId) {
		Game game = this.buscar(gameId);
		Boolean gameAtivo = gameEmAndamento(game);
		if (!gameAtivo && proximoGameProntoParaIniciar(game)) {
			garantirGameAnteriorJaFinalizado(game);
			gameAtivo = iniciarGame(game);
		}
		if (gameAtivo) {
			Pontuacao pontuacaoJogadorA = gestaoPontuacaoService.buscar(game.getPontos().get(0).getId());
			Pontuacao pontuacaoJogadorB = gestaoPontuacaoService.buscar(game.getPontos().get(1).getId());
			if (pontuacaoJogadorA.getId() == pontoId) {
				incrementar(pontuacaoJogadorA);
			} else if (pontuacaoJogadorB.getId() == pontoId) {
				incrementar(pontuacaoJogadorB);
			} else
				throw new EntidadeNaoEncontradaException("Game não encontrado");
			if (pontuacaoParaFinalizarGame(pontuacaoJogadorA.getPontos(), pontuacaoJogadorB.getPontos())) {
				finalizarGame(game);
			}
		}
		return game;
	}

	@Transactional
	public Game diminueUmPonto(Long gameId, Long pontoId) {
		Game game = this.buscar(gameId);
		Boolean gameAtivo = game.emAndamento();
		if (!gameAtivo && game.finalizado()) {
			game.setStatus(StatusJogo.EmAndamento);
			gameAtivo = true;
		}
		if (gameAtivo) {
			Pontuacao pontuacaoJogadorA = gestaoPontuacaoService.buscar(game.getPontos().get(0).getId());
			Pontuacao pontuacaoJogadorB = gestaoPontuacaoService.buscar(game.getPontos().get(1).getId());
			if (pontuacaoJogadorA.getId() == pontoId) {
				if (pontuacaoJogadorA.getPontos() > 0) {
					decrementar(pontuacaoJogadorA);
				}
			} else if (pontuacaoJogadorB.getId() == pontoId) {
				if (pontuacaoJogadorB.getPontos() > 0) {
					decrementar(pontuacaoJogadorB);
				}
			}
		} else
			throw new EntidadeNaoEncontradaException("Game não encontrado");
		return game;
	}

	@Transactional
	private Boolean iniciarGame(Game game) {
		game.iniciar();
		this.salvar(game);
		return true;// TODO ???
	}

	@Transactional
	private void finalizarGame(Game game) {
		game.finalizar();
		this.salvar(game);
	}

	private boolean pontuacaoParaContinuarGame(int pontuacaoA, int pontuacaoB) {
		return pontuacaoA < 11 && pontuacaoB < 11;
	}

	private boolean pontuacaoParaFinalizarGame(int pontuacaoA, int pontuacaoB) {
		return pontuacaoA == 11 && pontuacaoB < 10 || pontuacaoA < 10 && pontuacaoB == 11
				|| (pontuacaoA >= 10 && pontuacaoB >= 10 && Math.abs(pontuacaoA - pontuacaoB) == 2);
	}

	private boolean proximoGameProntoParaIniciar(Game game) {
		return game.getPartida().gameAnterior().finalizado() && game.preparado() && game.getPartida().emAndamento();
	}

	private boolean gameEmAndamento(Game game) {
		return game.getPartida().buscarGameEmAndamento() == game;
	}

	@Transactional
	private void incrementar(Pontuacao pontuacao) {
		pontuacao.setPontos(pontuacao.maisUmPonto());
		gestaoPontuacaoService.salvar(pontuacao);
	}

	@Transactional
	private void decrementar(Pontuacao pontuacao) {
		pontuacao.setPontos(pontuacao.menosUmPonto());
		gestaoPontuacaoService.salvar(pontuacao);
	}

	@Transactional
	private void atualizarPontosAmbosJogadores(int pontosA, int pontosB, Pontuacao pontuacaoJogadorA,
			Pontuacao pontuacaoJogadorB) {
		pontuacaoJogadorA.setPontos(pontosA);
		pontuacaoJogadorB.setPontos(pontosB);
		gestaoPontuacaoService.salvar(pontuacaoJogadorA);
		gestaoPontuacaoService.salvar(pontuacaoJogadorB);
	}

	@Transactional
	private void garantirGameAnteriorJaFinalizado(Game game) {
		game.getPartida().gameAnterior().finalizar();
		this.salvar(game.getPartida().gameAnterior());
	}
}
