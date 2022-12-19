package com.eduq.quatoca.torneiotmapi.api.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import com.eduq.quatoca.torneiotmapi.domain.model.StatusPartida;
import lombok.Data;

@Data
public class PartidaModel {

	private Long id;
//	private List<JogadorModel> jogadores;
	private int quantidadeGames;
	private int gamesVencidosA;
	private int gamesVencidosB;
	private StatusPartida partidaStatus;
	private JogadorResumoModel jogadorPrimeiroSacador;
	private Set<JogadorModel> jogadores;
	private OffsetDateTime inicioPartida;
	private OffsetDateTime fimPartida;
	private List<GameModel> games;
//	private Game gameAtual;

}
