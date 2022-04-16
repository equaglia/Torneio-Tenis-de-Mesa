package com.eduq.quatoca.torneiotmapi.domain.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.common.CalculosGlobais;
import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;
import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;
import com.eduq.quatoca.torneiotmapi.domain.model.Resultado;
import com.eduq.quatoca.torneiotmapi.domain.model.StatusJogo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PontuacaoEmGameService {

	private GestaoPontuacaoService gestaoPontuacaoService;
	private GestaoGameService gestaoGameService;
	private GestaoPartidaService gestaoPartidaService;

	@Transactional
	public Game atualizarPontuacao(Long gameId, int pontuacaoA, int pontuacaoB) {
		CalculosGlobais.garantirPontuacaoPositiva(pontuacaoA, pontuacaoB);
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
		CalculosGlobais.garantirPontuacaoPositiva(pontuacaoA, pontuacaoB);
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
			if (CalculosGlobais.pontuacaoParaFinalizarGame(pontuacaoJogadorA.getPontos(), pontuacaoJogadorB.getPontos())) {
				gestaoGameService.finalizarGame(game);
			}
		}
		return game;
	}

	@Transactional
	public Game diminueUmPonto(Long gameId, Long pontoId) {
		Game game = gestaoGameService.buscar(gameId);
		Boolean gameAtivo = game.isEmAndamento();
		if (!gameAtivo && game.isFinalizado()) {
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

		//TODO Corrigir pontuacao de game em partida já finalizada, tipo, errou ultimo ponto da partida
//		if (gestaoPartidaService.buscar(game.getPartida().getId())) {
//			
//		}
//		if (game.getPartida().isFinalizado())
//			game.getPartida().setStatus(StatusJogo.EmAndamento);
		if (CalculosGlobais.pontuacaoParaContinuarGame(pontuacaoA, pontuacaoB)) {
			atualizarPontosAmbosJogadores(pontuacaoA, pontuacaoB, pontuacaoJogadorA, pontuacaoJogadorB);
			gestaoGameService.iniciarGame(game);
//			game.setStatus(StatusJogo.EmAndamento);
		} else if (CalculosGlobais.pontuacaoParaFinalizarGame(pontuacaoA, pontuacaoB)) {
			atualizarPontosAmbosJogadores(pontuacaoA, pontuacaoB, pontuacaoJogadorA, pontuacaoJogadorB);
			gestaoGameService.finalizarGame(game);
			Partida partida = game.getPartida();
			Game proximoGame = partida.proximoGame();//TODO proximoGame null se partida ja deu resultado
			if (proximoGame == null
					|| gestaoPartidaService.partidaJaTemVencedor(Resultado.resultadoCorrente(partida))) {
				gestaoPartidaService.finalizarPartida(partida);
//				partida.finalizar();
			}
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

	@Transactional
	private void atualizarPontosAmbosJogadores(int pontosA, int pontosB, Pontuacao pontuacaoJogadorA,
			Pontuacao pontuacaoJogadorB) {
		pontuacaoJogadorA.setPontos(pontosA);
		pontuacaoJogadorB.setPontos(pontosB);
		gestaoPontuacaoService.salvar(pontuacaoJogadorA);
		gestaoPontuacaoService.salvar(pontuacaoJogadorB);
	}

	
}
