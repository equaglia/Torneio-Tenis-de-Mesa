package com.eduq.quatoca.torneiotmapi.domain.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.common.CalculosGlobais;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ControleSacadorService {

	private GestaoPartidaService gestaoPartidaService;
	private GestaoGameService gestaoGameService;
	private CatalogoJogadorService catalogoJogadorService;

	@Transactional
	public void definirPrimeiroSacador(Long partidaId, Long jogadorId) {
		Partida partida = gestaoPartidaService.buscar(partidaId);
		if (partida.jaRegistrouPontuacao()) {
			throw new NegocioException("Partida já registrou pontuacao e não pode alterar o 'primeiro sacador'");
		} else {
			Optional<Jogador> jogador = catalogoJogadorService.buscar(jogadorId);
			if (partida.isJogadorDaPartida(jogador.get()))
				throw new NegocioException("Jogador com ID "+jogador.get().getId()+" não faz parte da partida "+partida.getId());
			if (partida.isJogadorDaPartida(jogador.get())) {
				partida.setPrimeiroSacador(jogador.get());
			} else {
				throw new NegocioException("O jogador com ID " + jogador.get().getId() + " não faz parte da partida "+partida.getId());
			}
			gestaoPartidaService.salvar(partida);
		}
	}
	
	public Jogador getSacador(Long partidaId, Long gameId) {
		Partida partida = gestaoPartidaService.buscar(partidaId);
		Game game = gestaoGameService.buscar(gameId);
		fazSentidoTerSacador(partida, game);
		Jogador primeiroSacadorDoGame;
		Jogador outroJogador;
		if (gestaoGameService.isGamePar(game)) {
			primeiroSacadorDoGame = partida.getPrimeiroSacador();
			outroJogador = partida.getNaoPrimeiroSacador();
		} else {
			primeiroSacadorDoGame = partida.getNaoPrimeiroSacador();
			outroJogador = partida.getPrimeiroSacador();
		}
		int totalPontosGame = gestaoGameService.getTotalPontos(game);
		if (totalPontosGame < 20) {
			if (CalculosGlobais.isPar(totalPontosGame/2))
				return primeiroSacadorDoGame;
		} else {
			if (CalculosGlobais.isPar(totalPontosGame))
				return primeiroSacadorDoGame;
		}
		return outroJogador;
	}

	private void fazSentidoTerSacador(Partida partida, Game game) {
		if (!partida.isGameDaPartida(game))
			throw new NegocioException("Game com ID "+game.getId()+" não faz parte da partida "+partida.getId());
		if (partida.isFinalizado() || partida.isCancelado())
			throw new NegocioException("Partida finalizada/cancelada não tem sacador");
		if (game.isFinalizado() || game.isCancelado())
			throw new NegocioException("Game finalizado/cancelado não tem sacador");
	}
}
