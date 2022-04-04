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
		return pontuacaoA == 11 && pontuacaoB < 10 
				|| pontuacaoA < 10 && pontuacaoB == 11
				|| (pontuacaoA >= 10 && pontuacaoB >= 10 && Math.abs(pontuacaoA - pontuacaoB) == 2);
	}

}
