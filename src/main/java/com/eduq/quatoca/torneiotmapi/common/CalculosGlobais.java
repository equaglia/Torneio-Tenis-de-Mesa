package com.eduq.quatoca.torneiotmapi.common;

import com.eduq.quatoca.torneiotmapi.domain.exception.NegocioException;

public class CalculosGlobais {

	public static void garantirPontuacaoPositiva(int pontuacaoA, int pontuacaoB) {
		if (pontuacaoA < 0 || pontuacaoB < 0) {
			throw (new NegocioException("Pontuações devem ter valores positivos"));
		}
	}

	public static boolean pontuacaoParaContinuarGame(int pontuacaoA, int pontuacaoB) {
		return pontuacaoA < 11 && pontuacaoB < 11;
	}

	public static boolean pontuacaoParaFinalizarGame(int pontuacaoA, int pontuacaoB) {
//		System.out.println("pontuacaoA = "+pontuacaoA+"   pontuacaoB = "+pontuacaoB);
		return pontuacaoA == 11 && pontuacaoB < 10 || pontuacaoA < 10 && pontuacaoB == 11
				|| (pontuacaoA >= 10 && pontuacaoB >= 10 && Math.abs(pontuacaoA - pontuacaoB) == 2);
	}

	public static boolean pontuacaoParaFinalizarPartida(int gamesVencidosA, int gamesVencidosB, int maxGames) {

		int gamesParaVencer = (maxGames / 2) + 1;

		return (gamesVencidosA == gamesParaVencer || gamesVencidosB == gamesParaVencer)
				&& !(gamesVencidosA == gamesVencidosB);
	}

	public static boolean isResultadoValido(int gamesVencidosA, int gamesVencidosB, int maxGames) {
		int gamesParaVencer = (maxGames / 2) + 1;
		if (gamesVencidosA >= 0 && gamesVencidosB >= 0) {
			if (gamesVencidosA < gamesParaVencer && gamesVencidosB < gamesParaVencer) {
				return true;
			} else {
				return gamesVencidosA != gamesVencidosB;
			}
		}
		return false;
	}


	/*	return
		 0 - vitoria no game para o jogadorA
		 1 - vitoria no game para o jogadorB
		-1 - game não tem vencedor	 */
	public static int vencedorGame(int pontosJogadorA, int pontosJogadorB) {
		if (pontosJogadorA == 11 && pontosJogadorB <= 9 ) return 0;
		if (pontosJogadorB == 11 && pontosJogadorA <= 9 ) return 1;
		if (pontosJogadorA > 11 && pontosJogadorA == pontosJogadorB +2 ) return 0;
		if (pontosJogadorB > 11 && pontosJogadorB == pontosJogadorA +2 ) return 1;
		return -1;
	}

	public static boolean isPar(int numero) {
		return numero % 2 == 0;
	}

	public static boolean isImpar(int numero) {
		return !isPar(numero);
	}
}
