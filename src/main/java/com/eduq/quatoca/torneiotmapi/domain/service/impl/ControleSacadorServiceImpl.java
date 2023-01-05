package com.eduq.quatoca.torneiotmapi.domain.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import com.eduq.quatoca.torneiotmapi.domain.service.CatalogoJogadorService;
import com.eduq.quatoca.torneiotmapi.domain.service.ControleSacadorService;
import com.eduq.quatoca.torneiotmapi.domain.service.GestaoGameService;
import com.eduq.quatoca.torneiotmapi.domain.service.GestaoPartidaService;
import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.common.CalculosGlobais;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ControleSacadorServiceImpl implements ControleSacadorService {

	private GestaoPartidaService gestaoPartidaService;
	private GestaoGameService gestaoGameService;
	private CatalogoJogadorService catalogoJogadorService;

	@Override
	@Transactional
	public void setPrimeiroSacador(Long partidaId, Long jogadorId) {
		Partida partida = gestaoPartidaService.buscar(partidaId);
		if (partida.jaRegistrouPontuacao()) {
			throw new NegocioException("Partida já registrou pontuacao e não pode alterar o 'primeiro sacador'");
		} else {
			Optional<Jogador> jogador = catalogoJogadorService.buscar(jogadorId);
			if (partida.isJogadorDaPartida(jogador.orElse(null))) {
				partida.setPrimeiroSacador(jogador.orElse(null));
			} else {
				assert jogador.orElse(null) != null;
				throw new NegocioException("O jogador com ID " + jogador.orElse(null).getId() + " não faz parte da partida "+partida.getId());
			}
			gestaoPartidaService.salvar(partida);
		}
	}
	
	@Override
	public Jogador getSacador(Long partidaId, Long gameId) {
		Partida partida = gestaoPartidaService.buscar(partidaId);
		Game game = gestaoGameService.buscar(gameId);
		fazSentidoTerSacador(partida, game);
		Jogador primeiroSacadorDoGame;
		Jogador outroJogador;
		if (gestaoGameService.isGamePar(game)) {
			primeiroSacadorDoGame = partida.getPrimeiroSacador();
			outroJogador = partida.buscarNaoPrimeiroSacador();
		} else {
			primeiroSacadorDoGame = partida.buscarNaoPrimeiroSacador();
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
		if (partida.finalizado() || partida.cancelado())
			throw new NegocioException("Partida finalizada/cancelada não tem sacador");
		if (game.finalizado() || game.cancelado())
			throw new NegocioException("Game finalizado/cancelado não tem sacador");
	}
}
