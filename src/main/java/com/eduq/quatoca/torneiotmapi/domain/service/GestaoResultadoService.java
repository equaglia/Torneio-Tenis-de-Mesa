package com.eduq.quatoca.torneiotmapi.domain.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.eduq.quatoca.torneiotmapi.common.CalculosGlobais;
import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;
import com.eduq.quatoca.torneiotmapi.domain.model.Game;
import com.eduq.quatoca.torneiotmapi.domain.model.Partida;
import com.eduq.quatoca.torneiotmapi.domain.model.Resultado;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class GestaoResultadoService {

	public List<Resultado> resultadoCorrente(Partida partida) {

		ArrayList<Resultado> resultados = new ArrayList<>();
		Resultado resultadoJogadorA = new Resultado();
		Resultado resultadoJogadorB = new Resultado();
		resultados.add(resultadoJogadorA);
		resultados.add(resultadoJogadorB);

		int gamesVencidosJogadorA = 0;
		int gamesVencidosJogadorB = 0;
		for (Game game : partida.getGames()) {
			if (game.isFinalizado()) {
				int pontosJogadorA = game.getPontos().get(0).getPontos();
				int pontosJogadorB = game.getPontos().get(1).getPontos();
				if (CalculosGlobais.pontuacaoParaFinalizarGame(pontosJogadorA, pontosJogadorB)) {
					if (pontosJogadorA > pontosJogadorB)
						gamesVencidosJogadorA++;
					else if (pontosJogadorB > pontosJogadorA)
						gamesVencidosJogadorB++;
					else 
						throw new NegocioException("Partida finalizada não poderia acabar empatada");

				} else
					throw new NegocioException("Pontuação deste game está incorreta");
			} else
				break;
		}
		resultadoJogadorA.setResultado(gamesVencidosJogadorA);
		resultadoJogadorB.setResultado(gamesVencidosJogadorB);
		return resultados;

	}
}
