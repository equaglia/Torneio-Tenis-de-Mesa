package com.eduq.quatoca.torneiotmapi.domain.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Jogador;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ControleSacadorService {

	private GestaoPartidaService gestaoPartidaService;
	private CatalogoJogadorService catalogoJogadorService;

	public void definirPrimeiroSacador(Long partidaId, Long jogadorId) {
		Partida partida = gestaoPartidaService.buscar(partidaId);
		if (partida.jaRegistrouPontuacao()) {
			throw new NegocioException("Partida já registrou pontuacao e não pode alterar o 'primeiro sacador'");
		} else {
			Optional<Jogador> jogador = catalogoJogadorService.buscar(jogadorId);
			partida.getJogadores().stream().forEach(jog -> {
				if (jog == jogador.get()) {
					partida.setPrimeiroSacador(jogador.get());
				}
			});
			if (jogadorNaoFazParteDaPartida(partida, jogador)) {
				throw new NegocioException("O jogador com ID " + jogador.get().getId() + " não faz parte da partida.");
			}
			gestaoPartidaService.salvar(partida);
		}
	}

	private boolean jogadorNaoFazParteDaPartida(Partida partida, Optional<Jogador> jogador) {
		return !(partida.getPrimeiroSacador() == jogador.get());
	}
}
