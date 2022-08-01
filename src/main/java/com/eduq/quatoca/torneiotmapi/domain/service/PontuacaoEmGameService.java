package com.eduq.quatoca.torneiotmapi.domain.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.common.CalculosGlobais;
import com.eduq.quatoca.torneiotmapi.domain.exception.EntidadeNaoEncontradaException;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;
import com.eduq.quatoca.torneiotmapi.domain.model.Pontuacao;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PontuacaoEmGameService {

	private GestaoPontuacaoService gestaoPontuacaoService;
	private GestaoGameService gestaoGameService;
	private GestaoPartidaService gestaoPartidaService;

	private static final int jogadorA = 0;
	private static final int jogadorB = 1;

	@Transactional
	public Game atualizarPontuacao(Long gameId, int pontuacaoA, int pontuacaoB) {
		CalculosGlobais.garantirPontuacaoPositiva(pontuacaoA, pontuacaoB);
		Game game = gestaoGameService.buscar(gameId);
		boolean gameAtivo = gestaoGameService.isGameEmAndamento(game);
		if (gestaoPartidaService.temGameEmAndamento(game.getPartida()) && !gameAtivo) {
			throw new NegocioException("Game " + gameId + " não é o próximo game da partida.");
		}
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
		boolean gameAtivo = gestaoGameService.isGameEmAndamento(game);
		if (!gameAtivo && gestaoGameService.proximoGameProntoParaIniciar(game)) {
			gestaoGameService.garantirGameAnteriorJaFinalizado(game);
			gestaoGameService.iniciarGame(game);
			gameAtivo = true;
		}
		if (gameAtivo) {
			Pontuacao pontuacaoJogadorA = buscarPontuacaoDeJogador(game, jogadorA);
			Pontuacao pontuacaoJogadorB = buscarPontuacaoDeJogador(game, jogadorB);
			if (pontuacaoJogadorA.getId().equals(pontoId)) {
				incrementar(pontuacaoJogadorA);
			} else if (pontuacaoJogadorB.getId().equals(pontoId)) {
				incrementar(pontuacaoJogadorB);
			} else
				throw new EntidadeNaoEncontradaException("Game não encontrado");
			if (CalculosGlobais.pontuacaoParaFinalizarGame(pontuacaoJogadorA.getPontos(),
					pontuacaoJogadorB.getPontos())) {
				gestaoGameService.finalizarGame(game);
				Partida partida = game.getPartida();
				if (gestaoPartidaService.partidaJaTemVencedor(partida.calculaResultado())) {
					gestaoPartidaService.finalizarPartida(partida);
				}
			}
		}
		return game;
	}

	@Transactional
	public Game diminueUmPonto(Long gameId, Long pontoId) {
		Game game = gestaoGameService.buscar(gameId);
		boolean gameAtivo = game.emAndamento();
		Partida partida = game.getPartida();
		int indiceGame = partida.getGames().indexOf(game);
		Game gameSeguinte = partida.getGames().get(indiceGame + 1);
		if (partida.emAndamento() || partida.interrompido()) {
			if (partida.proximoGame() == gameSeguinte) {
				if (!gameAtivo && game.finalizado()) {
					gameSeguinte.setPreparado();
					game.setEmAndamento();
					gameAtivo = true;
				}
			}
			if (gameAtivo) {
				Pontuacao pontuacaoJogadorA = buscarPontuacaoDeJogador(game, jogadorA);
				Pontuacao pontuacaoJogadorB = buscarPontuacaoDeJogador(game, jogadorB);
				if (pontuacaoJogadorA.getId().equals(pontoId)) {
					if (pontuacaoJogadorA.getPontos() > 0) decrementar(pontuacaoJogadorA);
				} else if (pontuacaoJogadorB.getId().equals(pontoId)) {
					if (pontuacaoJogadorB.getPontos() > 0) decrementar(pontuacaoJogadorB);
				}
			} else
				throw new EntidadeNaoEncontradaException("Game não encontrado");
		}
		return game;
	}

	public Pontuacao buscarPontuacaoDeJogador(Game game, int indice) {
		return gestaoPontuacaoService.buscar(game.getPontos().get(indice).getId());
	}

	@Transactional
	private void efetivarAtualizacaoDePontuacao(int pontuacaoA, int pontuacaoB, Game game) {
		Pontuacao pontuacaoJogadorA = buscarPontuacaoDeJogador(game, jogadorA);
		Pontuacao pontuacaoJogadorB = buscarPontuacaoDeJogador(game, jogadorB);

		// TODO Corrigir pontuacao de game em partida já finalizada, tipo, errou ultimo
		// ponto da partida
		if (game.getPartida().finalizado()) {
			gestaoPartidaService.setPartidaEmAndamento(game.getPartida());
		}
		if (CalculosGlobais.pontuacaoParaContinuarGame(pontuacaoA, pontuacaoB)) {
			atualizarPontosAmbosJogadores(pontuacaoA, pontuacaoB, pontuacaoJogadorA, pontuacaoJogadorB);
			gestaoGameService.setEmAndamento(game);
		} else if (CalculosGlobais.pontuacaoParaFinalizarGame(pontuacaoA, pontuacaoB)) {
			atualizarPontosAmbosJogadores(pontuacaoA, pontuacaoB, pontuacaoJogadorA, pontuacaoJogadorB);
			gestaoGameService.finalizarGame(game);
			Partida partida = game.getPartida();
			Game proximoGame = partida.proximoGame();// TODO proximoGame null se partida ja deu resultado
			if (proximoGame == null
					|| gestaoPartidaService.partidaJaTemVencedor(partida.calculaResultado())) {
				gestaoPartidaService.finalizarPartida(partida);
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