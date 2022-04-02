package com.eduq.quatoca.torneiotmapi.domain.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;
import com.eduq.quatoca.torneiotmapi.domain.model.StatusJogo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PontuacaoEmGameService {

	private GestaoPontuacaoService gestaoPontuacaoService;
	private GestaoGameService gestaoGameService;

	@Transactional
	public Game atualizarPontuacao(Long gameId, int pontuacaoA, int pontuacaoB) {
		garantirPontuacaoPositiva(pontuacaoA, pontuacaoB);
		Game game = gestaoGameService.buscar(gameId);
		Boolean gameAtivo = gestaoGameService.gameEmAndamento(game);
		if (!gameAtivo && gestaoGameService.proximoGameProntoParaIniciar(game)) {
			gestaoGameService.garantirGameAnteriorJaFinalizado(game);
			gestaoGameService.iniciarGame(game);
			gameAtivo = true;
		}
		if (gameAtivo) {
			efetivarAtualizacaoDePontuacao(pontuacaoA, pontuacaoB, game);
		}
		return game;
	}

	@Transactional
	public Game atualizarPontuacaoGameFinalizado(Long gameId, int pontuacaoA, int pontuacaoB) {
		garantirPontuacaoPositiva(pontuacaoA, pontuacaoB);
		Game game = gestaoGameService.buscar(gameId);
		efetivarAtualizacaoDePontuacao(pontuacaoA, pontuacaoB, game);
		return game;
	}

	@Transactional
	public Game somaUmPonto(Long gameId, Long pontoId) {
		Game game = gestaoGameService.buscar(gameId);
		Boolean gameAtivo = gestaoGameService.gameEmAndamento(game);
		if (!gameAtivo && gestaoGameService.proximoGameProntoParaIniciar(game)) {
			gestaoGameService.garantirGameAnteriorJaFinalizado(game);
			gestaoGameService.iniciarGame(game);
			gameAtivo = true;
		}
		if (gameAtivo) {
			Pontuacao pontuacaoJogadorA = buscarPontosDeJogador(game, 0);
			Pontuacao pontuacaoJogadorB = buscarPontosDeJogador(game, 1);
			if (pontuacaoJogadorA.getId() == pontoId) {
				incrementar(pontuacaoJogadorA);
			} else if (pontuacaoJogadorB.getId() == pontoId) {
				incrementar(pontuacaoJogadorB);
			} else
				throw new EntidadeNaoEncontradaException("Game não encontrado");
			if (pontuacaoParaFinalizarGame(pontuacaoJogadorA.getPontos(), pontuacaoJogadorB.getPontos())) {
				gestaoGameService.finalizarGame(game);
			}
		}
		return game;
	}

	@Transactional
	public Game diminueUmPonto(Long gameId, Long pontoId) {
		Game game = gestaoGameService.buscar(gameId);
		Boolean gameAtivo = game.emAndamento();
		if (!gameAtivo && game.finalizado()) {
			game.setStatus(StatusJogo.EmAndamento);
			gameAtivo = true;
		}
		if (gameAtivo) {
			Pontuacao pontuacaoJogadorA = buscarPontosDeJogador(game, 0);
			Pontuacao pontuacaoJogadorB = buscarPontosDeJogador(game, 1);
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

	private Pontuacao buscarPontosDeJogador(Game game, int indice) {
		Pontuacao pontuacaoJogadorA = gestaoPontuacaoService.buscar(game.getPontos().get(indice).getId());
		return pontuacaoJogadorA;
	}

	private void efetivarAtualizacaoDePontuacao(int pontuacaoA, int pontuacaoB, Game game) {
		Pontuacao pontuacaoJogadorA = buscarPontosDeJogador(game, 0);
		Pontuacao pontuacaoJogadorB = buscarPontosDeJogador(game, 1);

		if (pontuacaoParaContinuarGame(pontuacaoA, pontuacaoB)) {
			atualizarPontosAmbosJogadores(pontuacaoA, pontuacaoB, pontuacaoJogadorA, pontuacaoJogadorB);
			game.setStatus(StatusJogo.EmAndamento);
		} else if (pontuacaoParaFinalizarGame(pontuacaoA, pontuacaoB)) {
			atualizarPontosAmbosJogadores(pontuacaoA, pontuacaoB, pontuacaoJogadorA, pontuacaoJogadorB);
			gestaoGameService.finalizarGame(game);
		} else {
			throw (new NegocioException(
					"Pontuacao maior que ONZE, não pode ter diferença maior que DOIS entre os 2 jogadores"));
		}
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

	private void garantirPontuacaoPositiva(int pontuacaoA, int pontuacaoB) {
		if (pontuacaoA < 0 || pontuacaoB < 0) {
			throw (new NegocioException("Pontuações devem ter valores positivos"));
		}
	}

	private boolean pontuacaoParaContinuarGame(int pontuacaoA, int pontuacaoB) {
		return pontuacaoA < 11 && pontuacaoB < 11;
	}

	private boolean pontuacaoParaFinalizarGame(int pontuacaoA, int pontuacaoB) {
		return pontuacaoA == 11 && pontuacaoB < 10 || pontuacaoA < 10 && pontuacaoB == 11
				|| (pontuacaoA >= 10 && pontuacaoB >= 10 && Math.abs(pontuacaoA - pontuacaoB) == 2);
	}

	@Transactional
	private void atualizarPontosAmbosJogadores(int pontosA, int pontosB, Pontuacao pontuacaoJogadorA,
			Pontuacao pontuacaoJogadorB) {
		pontuacaoJogadorA.setPontos(pontosA);
		pontuacaoJogadorB.setPontos(pontosB);
		gestaoPontuacaoService.salvar(pontuacaoJogadorA);
		gestaoPontuacaoService.salvar(pontuacaoJogadorB);
	}

}
