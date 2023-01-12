package com.eduq.quatoca.torneiotmapi.api.model;

import java.time.OffsetDateTime;
import java.util.List;

import com.eduq.quatoca.torneiotmapi.domain.model.enums.StatusPartida;
import lombok.Data;

@Data
public class PartidaModel {

	private Long id;
	private int gamesVencidosA;
	private int gamesVencidosB;
	private StatusPartida partidaStatus;
	private OffsetDateTime inicioPartida;
	private OffsetDateTime fimPartida;
	private JogadorResumoModel jogadorPrimeiroSacador;
	private JogadorModel jogadorA;
	private JogadorModel jogadorB;
	private int quantidadeGames;
	private List<GameModel> games;
	private int gameAtualIndice;
}
